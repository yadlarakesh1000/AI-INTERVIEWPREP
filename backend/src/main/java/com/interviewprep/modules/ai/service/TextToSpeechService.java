package com.interviewprep.modules.ai.service;

/**
 * Abstraction for synthesizing question text into spoken audio.
 *
 * <p>Phase 1 implementation: {@code GeminiTtsServiceImpl} (STEP 018). If Gemini TTS
 * proves complex or rate-limited, the frontend may fall back to the browser's
 * Web Speech API, as noted in Section 13 — the interface keeps that swap isolated.
 */
public interface TextToSpeechService {

    /**
     * Synthesizes the given text into spoken audio.
     *
     * @param text the question text to speak
     * @return the generated audio bytes (e.g. MP3/WAV)
     */
    byte[] synthesize(String text);
}
