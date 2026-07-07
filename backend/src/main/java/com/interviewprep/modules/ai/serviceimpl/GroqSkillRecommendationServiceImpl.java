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
import com.interviewprep.modules.ai.dto.SkillRecommendationResponse;
import com.interviewprep.modules.ai.dto.SkillRecommendationResponse.SkillSuggestion;
import com.interviewprep.modules.ai.service.SkillRecommendationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Phase 1 implementation of {@link SkillRecommendationService} backed by the Groq
 * OpenAI-compatible chat completions endpoint. Uses the exact Skill Recommendation Prompt
 * from Section 13, framed as a mentor suggesting logical next skills (Section 9, Feature 3).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroqSkillRecommendationServiceImpl implements SkillRecommendationService {

    private static final double RECOMMENDATION_TEMPERATURE = 0.6;
    private static final String STRICTER_SUFFIX =
            "\n\nIMPORTANT: Respond with ONLY minified, valid JSON matching the exact format above. "
                    + "No markdown, no backticks, no commentary.";

    private final GroqConfig groqConfig;
    private final RestTemplate groqRestTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public SkillRecommendationResponse recommendSkills(String currentSkills, String careerPath) {
        String prompt = buildPrompt(currentSkills, careerPath);

        // First attempt.
        try {
            return parseRecommendations(callGroqChat(prompt));
        } catch (Exception ex) {
            log.warn("Skill recommendation parse failed on first attempt, retrying with a stricter prompt", ex);
        }

        // Retry once with a stricter instruction if the JSON was malformed.
        try {
            return parseRecommendations(callGroqChat(prompt + STRICTER_SUFFIX));
        } catch (Exception ex) {
            log.error("Skill recommendation parsing failed after retry; returning empty recommendations", ex);
            return SkillRecommendationResponse.builder()
                    .recommendations(new ArrayList<>())
                    .build();
        }
    }

    private String buildPrompt(String currentSkills, String careerPath) {
        return """
                You are a senior tech mentor. Based on this developer's profile:
                Current Skills: %s
                Career Interest: %s

                Recommend 4-6 logical next skills they should learn. For each skill, provide a brief reason why it's relevant to their career path.
                Act like a mentor suggesting what to learn next, not a validator checking what's missing.

                Respond in this exact JSON format (no markdown, no backticks):
                {
                    "recommendations": [
                        {"skill": "Docker", "reason": "Essential for containerizing Spring Boot apps..."},
                        ...
                    ]
                }""".formatted(nullSafe(currentSkills), nullSafe(careerPath));
    }

    private SkillRecommendationResponse parseRecommendations(String content) throws Exception {
        JsonNode root = objectMapper.readTree(extractJsonObject(content));
        JsonNode recommendationsNode = root.path("recommendations");

        List<SkillSuggestion> suggestions = new ArrayList<>();
        if (recommendationsNode.isArray()) {
            recommendationsNode.forEach(node -> {
                String skill = readText(node.path("skill"));
                String reason = readText(node.path("reason"));
                if (skill != null) {
                    suggestions.add(SkillSuggestion.builder().skill(skill).reason(reason).build());
                }
            });
        }

        if (suggestions.isEmpty()) {
            throw new IllegalStateException("No recommendations found in LLM response");
        }

        log.debug("Parsed {} skill recommendations", suggestions.size());
        return SkillRecommendationResponse.builder().recommendations(suggestions).build();
    }

    private String readText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String text = node.asText(null);
        return (text == null || text.isBlank()) ? null : text;
    }

    /**
     * Extracts the first JSON object from the model output, tolerating any surrounding prose
     * or markdown fences the model may add despite instructions.
     */
    private String extractJsonObject(String content) {
        if (content == null) {
            throw new IllegalStateException("Empty skill recommendation content");
        }
        String cleaned = content.replace("```json", "").replace("```", "").trim();
        int start = cleaned.indexOf('{');
        int end = cleaned.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalStateException("No JSON object found in skill recommendation content");
        }
        return cleaned.substring(start, end + 1);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    /**
     * Calls the Groq chat completions endpoint in JSON response mode with a single retry on
     * transient failure (5xx or timeout), per Section 21.
     */
    private String callGroqChat(String userContent) {
        String url = groqConfig.getBaseUrl() + "/chat/completions";

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", userContent));

        Map<String, Object> body = Map.of(
                "model", groqConfig.getModel(),
                "temperature", RECOMMENDATION_TEMPERATURE,
                "response_format", Map.of("type", "json_object"),
                "messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqConfig.getApiKey());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        log.debug("Groq skill-recommendation request -> model={}", groqConfig.getModel());

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                ResponseEntity<String> response = groqRestTemplate.postForEntity(url, request, String.class);
                log.debug("Groq skill-recommendation response (attempt {}): {}", attempt, response.getBody());
                return extractMessageContent(response.getBody());
            } catch (HttpServerErrorException | ResourceAccessException ex) {
                if (attempt > 2) {
                    log.error("Groq LLM call failed after retry (skill recommendation)", ex);
                    throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
                }
                log.warn("Groq LLM call failed (attempt {}), retrying: {}", attempt, ex.getMessage());
            } catch (HttpClientErrorException ex) {
                log.error("Groq LLM client error {} (skill recommendation): {}",
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
