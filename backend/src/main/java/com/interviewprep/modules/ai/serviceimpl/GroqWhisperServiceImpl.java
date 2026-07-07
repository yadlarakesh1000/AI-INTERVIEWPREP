package com.interviewprep.modules.ai.serviceimpl;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.exception.BadRequestException;
import com.interviewprep.modules.ai.config.GroqConfig;
import com.interviewprep.modules.ai.service.SpeechToTextService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Phase 1 implementation of {@link SpeechToTextService} backed by the Groq Whisper
 * transcription endpoint. Forwards the recorded answer audio (captured by the frontend)
 * to Groq as multipart/form-data and returns the transcribed text.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroqWhisperServiceImpl implements SpeechToTextService {

    private static final String TRANSCRIBE_FAILED_MESSAGE = "Could not transcribe your answer. Please try again.";
    private static final String DEFAULT_FILE_NAME = "answer.webm";

    private final GroqConfig groqConfig;
    private final RestTemplate groqWhisperRestTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String transcribe(byte[] audioData, String fileName) {
        if (audioData == null || audioData.length == 0) {
            throw new BadRequestException(TRANSCRIBE_FAILED_MESSAGE);
        }

        String resolvedName = (fileName == null || fileName.isBlank()) ? DEFAULT_FILE_NAME : fileName;
        String url = groqConfig.getBaseUrl() + "/audio/transcriptions";

        // The "file" part must expose a filename so Whisper can infer the audio format.
        ByteArrayResource fileResource = new ByteArrayResource(audioData) {
            @Override
            public String getFilename() {
                return resolvedName;
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        body.add("model", groqConfig.getWhisperModel());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(groqConfig.getApiKey());
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        log.debug("Groq Whisper request -> model={}, file={}, {} bytes",
                groqConfig.getWhisperModel(), resolvedName, audioData.length);

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                ResponseEntity<String> response = groqWhisperRestTemplate.postForEntity(url, request, String.class);
                log.debug("Groq Whisper response (attempt {}): {}", attempt, response.getBody());
                return extractTranscription(response.getBody());
            } catch (HttpServerErrorException | ResourceAccessException ex) {
                if (attempt > 2) {
                    log.error("Groq Whisper call failed after retry", ex);
                    throw new BadRequestException(TRANSCRIBE_FAILED_MESSAGE);
                }
                log.warn("Groq Whisper call failed (attempt {}), retrying: {}", attempt, ex.getMessage());
            } catch (HttpClientErrorException ex) {
                log.error("Groq Whisper client error {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                throw new BadRequestException(TRANSCRIBE_FAILED_MESSAGE);
            }
        }
    }

    private String extractTranscription(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode text = root.path("text");
            if (text.isMissingNode()) {
                throw new BadRequestException(TRANSCRIBE_FAILED_MESSAGE);
            }
            return text.asText().trim();
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to parse Groq Whisper response", ex);
            throw new BadRequestException(TRANSCRIBE_FAILED_MESSAGE);
        }
    }
}
