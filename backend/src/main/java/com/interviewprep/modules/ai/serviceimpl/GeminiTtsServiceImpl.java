package com.interviewprep.modules.ai.serviceimpl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.interviewprep.config.AiConfig;
import com.interviewprep.modules.ai.service.TextToSpeechService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Phase 1 implementation of {@link TextToSpeechService} backed by the Google AI Studio
 * Gemini Flash TTS model (Section 13).
 *
 * <p><b>Fallback strategy (Section 13):</b> Gemini TTS can be complex or rate-limited on the
 * free tier. Rather than failing the interview, every failure path here returns {@code null}.
 * The frontend treats a null/absent audio URL as a signal to fall back to the browser's
 * built-in Web Speech API ({@code window.speechSynthesis}) and simply display the question
 * text. Because TTS is accessed through the {@link TextToSpeechService} interface, this
 * implementation can be swapped later without any frontend changes.
 *
 * <p><b>Audio format:</b> Gemini returns base64-encoded raw PCM (signed 16-bit little-endian,
 * single channel, 24kHz by default). Raw PCM is not playable by the HTML5 Audio element, so we
 * wrap it in a minimal WAV container before returning the bytes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiTtsServiceImpl implements TextToSpeechService {

    private static final String GEMINI_BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String TTS_MODEL = "gemini-2.5-flash-preview-tts";
    private static final String VOICE_NAME = "Kore";
    private static final int DEFAULT_SAMPLE_RATE = 24000;
    private static final Pattern RATE_PATTERN = Pattern.compile("rate=(\\d+)");

    private final AiConfig aiConfig;
    private final RestTemplate geminiRestTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public byte[] synthesize(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        if (aiConfig.getGeminiApiKey() == null || aiConfig.getGeminiApiKey().isBlank()) {
            // No key configured — fall back to browser Web Speech API on the frontend.
            log.warn("Gemini API key not configured; skipping TTS (frontend will use Web Speech API)");
            return null;
        }

        String url = GEMINI_BASE_URL + TTS_MODEL + ":generateContent?key=" + aiConfig.getGeminiApiKey();
        HttpEntity<Map<String, Object>> request = buildRequest(text);

        log.debug("Gemini TTS request -> model={}, {} chars", TTS_MODEL, text.length());

        int attempt = 0;
        while (true) {
            attempt++;
            try {
                ResponseEntity<String> response = geminiRestTemplate.postForEntity(url, request, String.class);
                return parseAudio(response.getBody());
            } catch (HttpServerErrorException | ResourceAccessException ex) {
                if (attempt > 2) {
                    log.error("Gemini TTS failed after retry; returning null for browser fallback", ex);
                    return null;
                }
                log.warn("Gemini TTS failed (attempt {}), retrying: {}", attempt, ex.getMessage());
            } catch (HttpClientErrorException ex) {
                log.error("Gemini TTS client error {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                return null;
            } catch (Exception ex) {
                log.error("Unexpected Gemini TTS error; returning null for browser fallback", ex);
                return null;
            }
        }
    }

    private HttpEntity<Map<String, Object>> buildRequest(String text) {
        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", text)))),
                "generationConfig", Map.of(
                        "responseModalities", List.of("AUDIO"),
                        "speechConfig", Map.of(
                                "voiceConfig", Map.of(
                                        "prebuiltVoiceConfig", Map.of("voiceName", VOICE_NAME)))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    /**
     * Extracts the base64 PCM audio from the Gemini response and wraps it in a WAV container.
     * Returns null (browser fallback) if the response contains no audio.
     */
    private byte[] parseAudio(String responseBody) {
        try {
            JsonNode inlineData = objectMapper.readTree(responseBody)
                    .path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("inlineData");

            String base64Audio = inlineData.path("data").asText(null);
            if (base64Audio == null || base64Audio.isBlank()) {
                log.error("Gemini TTS response contained no audio data; returning null for browser fallback");
                return null;
            }

            String mimeType = inlineData.path("mimeType").asText("");
            int sampleRate = parseSampleRate(mimeType);
            byte[] pcm = Base64.getDecoder().decode(base64Audio);

            log.debug("Gemini TTS produced {} PCM bytes at {}Hz", pcm.length, sampleRate);
            return pcmToWav(pcm, sampleRate);
        } catch (Exception ex) {
            log.error("Failed to parse Gemini TTS response; returning null for browser fallback", ex);
            return null;
        }
    }

    private int parseSampleRate(String mimeType) {
        if (mimeType != null) {
            Matcher matcher = RATE_PATTERN.matcher(mimeType);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        }
        return DEFAULT_SAMPLE_RATE;
    }

    /**
     * Wraps raw signed 16-bit little-endian mono PCM in a minimal 44-byte WAV header so the
     * bytes are playable by a standard HTML5 Audio element.
     */
    private byte[] pcmToWav(byte[] pcm, int sampleRate) {
        int channels = 1;
        int bitsPerSample = 16;
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;
        int dataLen = pcm.length;

        ByteBuffer buffer = ByteBuffer.allocate(44 + dataLen).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put("RIFF".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(36 + dataLen);
        buffer.put("WAVE".getBytes(StandardCharsets.US_ASCII));
        buffer.put("fmt ".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(16);                     // PCM fmt chunk size
        buffer.putShort((short) 1);            // audio format = PCM
        buffer.putShort((short) channels);
        buffer.putInt(sampleRate);
        buffer.putInt(byteRate);
        buffer.putShort((short) blockAlign);
        buffer.putShort((short) bitsPerSample);
        buffer.put("data".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(dataLen);
        buffer.put(pcm);
        return buffer.array();
    }
}
