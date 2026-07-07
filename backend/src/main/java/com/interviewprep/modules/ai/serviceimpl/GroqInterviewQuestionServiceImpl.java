package com.interviewprep.modules.ai.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.ai.service.InterviewQuestionService;
import com.interviewprep.modules.interviewsession.model.QAPair;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Phase 1 implementation of {@link InterviewQuestionService} backed by the Groq
 * OpenAI-compatible chat completions endpoint. Generates interview questions for all
 * three interview types using the exact prompts from Section 13 of the specification.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroqInterviewQuestionServiceImpl implements InterviewQuestionService {

    private static final String TYPE_HR = "HR";
    private static final String TYPE_RESUME = "RESUME_BASED";
    private static final String TYPE_CS = "CS_FUNDAMENTALS";
    private static final String DIFFICULTY_MEDIUM = "MEDIUM";

    private static final String USER_TRIGGER = "Generate the question now.";
    private static final double QUESTION_TEMPERATURE = 0.9;

    private final GroqConfig groqConfig;
    private final RestTemplate groqRestTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String generateQuestion(String interviewType,
                                   String difficulty,
                                   String topic,
                                   List<QAPair> history,
                                   ResumeData resumeData) {
        String systemPrompt = buildSystemPrompt(interviewType, difficulty, topic, history, resumeData);
        String content = callGroqChat(systemPrompt, USER_TRIGGER);
        String question = cleanQuestion(content);
        log.debug("Generated question (type={}, difficulty={}): {}", interviewType, difficulty, question);
        return question;
    }

    private String buildSystemPrompt(String interviewType,
                                     String difficulty,
                                     String topic,
                                     List<QAPair> history,
                                     ResumeData resumeData) {
        String type = interviewType == null ? "" : interviewType.trim().toUpperCase();
        boolean medium = DIFFICULTY_MEDIUM.equalsIgnoreCase(difficulty);

        if (TYPE_HR.equals(type)) {
            if (medium && history != null && !history.isEmpty()) {
                return buildHrFollowUpPrompt(history.get(history.size() - 1));
            }
            return buildHrEasyPrompt();
        }
        if (TYPE_RESUME.equals(type)) {
            return buildResumePrompt(difficulty, resumeData);
        }
        if (TYPE_CS.equals(type)) {
            return buildCsPrompt(difficulty, topic);
        }
        // Unknown type — fall back to a generic HR question rather than failing the interview.
        log.warn("Unknown interview type '{}', defaulting to HR prompt", interviewType);
        return buildHrEasyPrompt();
    }

    private String buildHrEasyPrompt() {
        return """
                You are an experienced HR interviewer. Ask a professional HR interview question.
                Choose from these categories: introduction, strengths/weaknesses, behavioral, situational, motivation.
                Return ONLY the question text, nothing else.""";
    }

    private String buildHrFollowUpPrompt(QAPair previous) {
        String previousQuestion = previous.getQuestion() == null ? "" : previous.getQuestion();
        String previousAnswer = previous.getAnswer() == null ? "" : previous.getAnswer();
        return """
                You are an experienced HR interviewer conducting a follow-up.
                The previous question was: "%s"
                The candidate's answer was: "%s"
                Generate a natural follow-up question that explores their answer deeper.
                Return ONLY the follow-up question text, nothing else.""".formatted(previousQuestion, previousAnswer);
    }

    private String buildResumePrompt(String difficulty, ResumeData resumeData) {
        String skills = formatSkills(resumeData);
        String projects = formatProjects(resumeData);
        String experience = formatExperience(resumeData);
        return """
                You are a technical interviewer. Based on this candidate's resume data:
                Skills: %s
                Projects: %s
                Experience: %s

                Generate a personalized interview question about their background, projects, or technical skills.
                Difficulty: %s
                Return ONLY the question text, nothing else.""".formatted(skills, projects, experience, safeDifficulty(difficulty));
    }

    private String buildCsPrompt(String difficulty, String topic) {
        String resolvedTopic = (topic == null || topic.isBlank()) ? "OOP, DBMS, Operating Systems, Computer Networks" : topic;
        return """
                You are a technical interviewer testing CS fundamentals.
                Topic: %s (one of: OOP, DBMS, Operating Systems, Computer Networks)
                Difficulty: %s
                If EASY: ask a conceptual/definitional question.
                If MEDIUM: ask a scenario-based question that requires deeper reasoning.
                Return ONLY the question text, nothing else.""".formatted(resolvedTopic, safeDifficulty(difficulty));
    }

    private String formatSkills(ResumeData resumeData) {
        if (resumeData == null || resumeData.getSkills() == null || resumeData.getSkills().isEmpty()) {
            return "Not provided";
        }
        return String.join(", ", resumeData.getSkills());
    }

    private String formatProjects(ResumeData resumeData) {
        if (resumeData == null || resumeData.getProjects() == null || resumeData.getProjects().isEmpty()) {
            return "Not provided";
        }
        return resumeData.getProjects().stream()
                .map(p -> {
                    String tech = (p.getTechnologies() == null || p.getTechnologies().isEmpty())
                            ? "" : " [" + String.join(", ", p.getTechnologies()) + "]";
                    String desc = p.getDescription() == null ? "" : " - " + p.getDescription();
                    return (p.getName() == null ? "Project" : p.getName()) + desc + tech;
                })
                .collect(Collectors.joining("; "));
    }

    private String formatExperience(ResumeData resumeData) {
        if (resumeData == null || resumeData.getExperience() == null || resumeData.getExperience().isEmpty()) {
            return "Not provided";
        }
        return resumeData.getExperience().stream()
                .map(e -> {
                    String role = e.getRole() == null ? "Role" : e.getRole();
                    String company = e.getCompany() == null ? "" : " at " + e.getCompany();
                    String duration = e.getDuration() == null ? "" : " (" + e.getDuration() + ")";
                    return role + company + duration;
                })
                .collect(Collectors.joining("; "));
    }

    private String safeDifficulty(String difficulty) {
        return difficulty == null ? "EASY" : difficulty.trim().toUpperCase();
    }

    /**
     * Calls the Groq chat completions endpoint with a single retry on transient failure
     * (5xx or timeout), per Section 21. On final failure a user-friendly message is thrown;
     * the raw API error is only logged server-side.
     */
    private String callGroqChat(String systemContent, String userContent) {
        String url = groqConfig.getBaseUrl() + "/chat/completions";

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemContent));
        messages.add(Map.of("role", "user", "content", userContent));

        Map<String, Object> body = Map.of(
                "model", groqConfig.getModel(),
                "temperature", QUESTION_TEMPERATURE,
                "messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqConfig.getApiKey());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        log.debug("Groq question request -> model={}, system prompt: {}", groqConfig.getModel(), systemContent);

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                ResponseEntity<String> response = groqRestTemplate.postForEntity(url, request, String.class);
                log.debug("Groq question response (attempt {}): {}", attempt, response.getBody());
                return extractMessageContent(response.getBody());
            } catch (HttpServerErrorException | ResourceAccessException ex) {
                if (attempt > 2) {
                    log.error("Groq LLM call failed after retry (question generation)", ex);
                    throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
                }
                log.warn("Groq LLM call failed (attempt {}), retrying: {}", attempt, ex.getMessage());
            } catch (HttpClientErrorException ex) {
                log.error("Groq LLM client error {} (question generation): {}",
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
            log.error("Failed to parse Groq chat response", ex);
            throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
        }
    }

    /**
     * Strips whitespace and any wrapping quotes the model may add so the returned text is
     * a clean question string.
     */
    private String cleanQuestion(String raw) {
        String text = raw == null ? "" : raw.trim();
        if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1).trim();
        }
        return text;
    }
}
