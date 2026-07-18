package com.interviewprep.modules.ai.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.exception.BadRequestException;
import com.interviewprep.modules.ai.config.GroqConfig;
import com.interviewprep.modules.ai.dto.EvaluationResult;
import com.interviewprep.modules.ai.dto.EvaluationResult.DimensionScore;
import com.interviewprep.modules.ai.service.AnswerEvaluationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Phase 1 implementation of {@link AnswerEvaluationService} backed by the Groq
 * OpenAI-compatible chat completions endpoint. Evaluates an answer across the 4-dimension
 * rubric (Section 9) using the exact Answer Evaluation Prompt from Section 13, and derives
 * the overall and confidence scores.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroqAnswerEvaluationServiceImpl implements AnswerEvaluationService {

    private static final String EVALUATOR_SYSTEM =
            "You are a strict but fair interview evaluator. You respond with valid JSON only, no markdown.";
    private static final double EVALUATION_TEMPERATURE = 0.2;

    private final GroqConfig groqConfig;
    private final RestTemplate groqRestTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public EvaluationResult evaluateAnswer(String question,
                                           String answer,
                                           String interviewType,
                                           String difficulty) {
        String prompt = buildEvaluationPrompt(question, answer, interviewType, difficulty);

        // First attempt.
        String content = callGroqChat(EVALUATOR_SYSTEM, prompt);
        try {
            return parseEvaluation(content);
        } catch (Exception ex) {
            log.warn("Evaluation JSON parse failed on first attempt, retrying with a stricter prompt", ex);
        }

        // Retry once with a stricter instruction if the JSON was malformed.
        String stricterPrompt = prompt
                + "\n\nIMPORTANT: Respond with ONLY minified, valid JSON matching the exact keys above. "
                + "No markdown, no backticks, no commentary.";
        try {
            String retryContent = callGroqChat(EVALUATOR_SYSTEM, stricterPrompt);
            return parseEvaluation(retryContent);
        } catch (Exception ex) {
            log.error("Evaluation JSON parse failed after retry; returning default evaluation", ex);
            return defaultEvaluation();
        }
    }

    @Override
    public List<String> generateImprovementSuggestions(List<EvaluationResult> evaluations) {
        if (evaluations == null || evaluations.isEmpty()) {
            return new ArrayList<>();
        }

        String results;
        try {
            results = objectMapper.writeValueAsString(evaluations);
        } catch (Exception ex) {
            results = evaluations.toString();
        }

        String prompt = """
                Based on these interview evaluation results, provide 3-5 specific, actionable
                improvement suggestions for the candidate. Be constructive and specific.
                Results: %s
                Respond as a JSON array of strings.""".formatted(results);

        try {
            return parseStringArray(callGroqChatPlain(prompt));
        } catch (Exception ex) {
            log.error("Failed to generate improvement suggestions from LLM; deriving from feedback", ex);
            return deriveFallbackSuggestions(evaluations);
        }
    }

    private List<String> parseStringArray(String content) throws Exception {
        String cleaned = content.replace("```json", "").replace("```", "").trim();
        int start = cleaned.indexOf('[');
        int end = cleaned.lastIndexOf(']');
        if (start < 0 || end <= start) {
            throw new IllegalStateException("No JSON array found in suggestions content");
        }
        String[] array = objectMapper.readValue(cleaned.substring(start, end + 1), String[].class);
        List<String> suggestions = new ArrayList<>();
        for (String item : array) {
            if (item != null && !item.isBlank()) {
                suggestions.add(item.trim());
            }
        }
        if (suggestions.isEmpty()) {
            throw new IllegalStateException("No suggestions parsed from LLM response");
        }
        return suggestions;
    }

    /**
     * Fallback used only if the LLM cannot produce a suggestion list: surfaces the feedback
     * from the lowest-scoring dimensions (Section 9 — per-dimension feedback feeds suggestions).
     */
    private List<String> deriveFallbackSuggestions(List<EvaluationResult> evaluations) {
        List<String> suggestions = new ArrayList<>();
        for (EvaluationResult ev : evaluations) {
            addIfLow(suggestions, ev.getRelevance());
            addIfLow(suggestions, ev.getDepthAndAccuracy());
            addIfLow(suggestions, ev.getClarityAndStructure());
            addIfLow(suggestions, ev.getConfidenceAndFluency());
        }
        List<String> distinct = suggestions.stream().distinct().limit(5).toList();
        if (distinct.isEmpty()) {
            return List.of("Keep practicing to provide more specific, well-structured answers with concrete examples.");
        }
        return new ArrayList<>(distinct);
    }

    private void addIfLow(List<String> out, EvaluationResult.DimensionScore dimension) {
        if (dimension != null && dimension.getScore() <= 6
                && dimension.getFeedback() != null && !dimension.getFeedback().isBlank()) {
            out.add(dimension.getFeedback().trim());
        }
    }

    /**
     * Calls Groq without JSON-object response mode (used for the suggestions array response),
     * with a single retry on transient failure.
     */
    private String callGroqChatPlain(String userContent) {
        String url = groqConfig.getBaseUrl() + "/chat/completions";

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", userContent));

        Map<String, Object> body = Map.of(
                "model", groqConfig.getModel(),
                "temperature", EVALUATION_TEMPERATURE,
                "messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqConfig.getApiKey());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                ResponseEntity<String> response = groqRestTemplate.postForEntity(url, request, String.class);
                return extractMessageContent(response.getBody());
            } catch (HttpServerErrorException | ResourceAccessException ex) {
                if (attempt > 2) {
                    log.error("Groq LLM call failed after retry (improvement suggestions)", ex);
                    throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
                }
                log.warn("Groq LLM call failed (attempt {}), retrying: {}", attempt, ex.getMessage());
            } catch (HttpClientErrorException ex) {
                log.error("Groq LLM client error {} (improvement suggestions): {}",
                        ex.getStatusCode(), ex.getResponseBodyAsString());
                throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
            }
        }
    }

    private String buildEvaluationPrompt(String question, String answer, String interviewType, String difficulty) {
        return """
                You are an interview evaluator. Evaluate the following answer to the interview question.

                Question: "%s"
                Candidate's Answer: "%s"
                Interview Type: %s
                Difficulty: %s

                Evaluate on exactly 4 dimensions, each scored 1-10 with a one-line justification:

                1. Relevance - Did the answer address the question?
                2. Clarity & Structure - Was the answer well-organized?
                3. Depth / Technical Accuracy - How correct and specific was the content?
                4. Confidence & Fluency - Based on the transcript, assess filler words, hesitation, pacing.

                Score strictly based on the candidate's actual answer above — do NOT reuse any
                example or template values. Use the full range: excellent answers deserve 8-10,
                mediocre answers 4-7, and empty or irrelevant answers 1-2.

                Respond in this exact JSON format (no markdown, no backticks):
                {
                    "relevance": {"score": <Integer 1-10>, "feedback": "..."},
                    "clarityAndStructure": {"score": <Integer 1-10>, "feedback": "..."},
                    "depthAndAccuracy": {"score": <Integer 1-10>, "feedback": "..."},
                    "confidenceAndFluency": {"score": <Integer 1-10>, "feedback": "..."}
                }""".formatted(
                nullSafe(question),
                nullSafe(answer),
                nullSafe(interviewType),
                nullSafe(difficulty));
    }

    private EvaluationResult parseEvaluation(String content) throws Exception {
        String json = extractJsonObject(content);
        JsonNode root = objectMapper.readTree(json);

        DimensionScore relevance = readDimension(root, "relevance");
        DimensionScore clarity = readDimension(root, "clarityAndStructure");
        DimensionScore depth = readDimension(root, "depthAndAccuracy");
        DimensionScore confidence = readDimension(root, "confidenceAndFluency");

        double overallScore = average(relevance.getScore(), depth.getScore());
        double confidenceScore = average(clarity.getScore(), confidence.getScore());

        EvaluationResult result = EvaluationResult.builder()
                .relevance(relevance)
                .clarityAndStructure(clarity)
                .depthAndAccuracy(depth)
                .confidenceAndFluency(confidence)
                .overallScore(overallScore)
                .confidenceScore(confidenceScore)
                .build();

        // INFO so score anomalies (e.g. the model copying template values) are
        // visible in the standard log without enabling debug logging.
        log.info("Parsed evaluation: relevance={}, clarity={}, depth={}, fluency={} -> overall={}, confidence={}",
                relevance.getScore(), clarity.getScore(), depth.getScore(), confidence.getScore(),
                overallScore, confidenceScore);
        return result;
    }

    private DimensionScore readDimension(JsonNode root, String key) {
        JsonNode node = root.path(key);
        if (node.isMissingNode()) {
            throw new IllegalStateException("Missing evaluation dimension: " + key);
        }
        int score = clampScore(node.path("score").asInt(5));
        String feedback = node.path("feedback").asText("");
        return DimensionScore.builder().score(score).feedback(feedback).build();
    }

    /**
     * Extracts the first JSON object from the model output, tolerating any surrounding
     * prose or markdown fences the model may add despite instructions.
     */
    private String extractJsonObject(String content) {
        if (content == null) {
            throw new IllegalStateException("Empty evaluation content");
        }
        String cleaned = content.replace("```json", "").replace("```", "").trim();
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalStateException("No JSON object found in evaluation content");
        }
        return cleaned.substring(start, end + 1);
    }

    private int clampScore(int score) {
        if (score < 1) {
            return 1;
        }
        return Math.min(score, 10);
    }

    private double average(int a, int b) {
        return (a + b) / 2.0;
    }

    /**
     * Fallback returned only when the LLM cannot produce valid JSON even after a retry.
     * Uses neutral mid-range scores and clearly notes that automatic evaluation failed.
     */
    private EvaluationResult defaultEvaluation() {
        String note = "Automatic evaluation was unavailable for this answer.";
        DimensionScore neutral = DimensionScore.builder().score(5).feedback(note).build();
        return EvaluationResult.builder()
                .relevance(neutral)
                .clarityAndStructure(neutral)
                .depthAndAccuracy(neutral)
                .confidenceAndFluency(neutral)
                .overallScore(5.0)
                .confidenceScore(5.0)
                .build();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    /**
     * Calls the Groq chat completions endpoint with JSON response mode and a single retry
     * on transient failure (5xx or timeout), per Section 21.
     */
    private String callGroqChat(String systemContent, String userContent) {
        String url = groqConfig.getBaseUrl() + "/chat/completions";

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemContent));
        messages.add(Map.of("role", "user", "content", userContent));

        Map<String, Object> body = Map.of(
                "model", groqConfig.getModel(),
                "temperature", EVALUATION_TEMPERATURE,
                "response_format", Map.of("type", "json_object"),
                "messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqConfig.getApiKey());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        log.debug("Groq evaluation request -> model={}", groqConfig.getModel());

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                ResponseEntity<String> response = groqRestTemplate.postForEntity(url, request, String.class);
                log.debug("Groq evaluation response (attempt {}): {}", attempt, response.getBody());
                return extractMessageContent(response.getBody());
            } catch (HttpServerErrorException | ResourceAccessException ex) {
                if (attempt > 2) {
                    log.error("Groq LLM call failed after retry (answer evaluation)", ex);
                    throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
                }
                log.warn("Groq LLM call failed (attempt {}), retrying: {}", attempt, ex.getMessage());
            } catch (HttpClientErrorException ex) {
                log.error("Groq LLM client error {} (answer evaluation): {}",
                        ex.getStatusCode(), ex.getResponseBodyAsString());
                throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
            }
        }
    }

    private String extractMessageContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().isBlank()) {
                throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
            }
            return content.asText();
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to parse Groq chat response envelope", ex);
            throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
        }
    }
}
