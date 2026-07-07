package com.interviewprep.modules.ai.service;

/**
 * Abstraction for transcribing recorded answer audio into text.
 *
 * <p>Phase 1 implementation: {@code GroqWhisperServiceImpl} (STEP 017), which forwards
 * the audio to the Groq Whisper endpoint.
 */
public interface SpeechToTextService {

    /**
     * Transcribes recorded audio into text.
     *
     * @param audioData the raw audio bytes captured by the frontend (WebM/Opus or WAV)
     * @param fileName  the original file name (used to convey the audio format to the STT API)
     * @return the transcribed text
     */
    String transcribe(byte[] audioData, String fileName);
}
