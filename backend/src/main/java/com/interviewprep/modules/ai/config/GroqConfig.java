package com.interviewprep.modules.ai.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

/**
 * Groq API configuration. Reads the Groq settings from {@code application.yml}
 * ({@code app.groq.*}) and exposes them to the Groq service implementations
 * (LLM in STEP 016, Whisper STT in STEP 017).
 */
@Getter
@Configuration
public class GroqConfig {

    @Value("${app.groq.api-key:}")
    private String apiKey;

    @Value("${app.groq.base-url:https://api.groq.com/openai/v1}")
    private String baseUrl;

    @Value("${app.groq.model:llama-3.3-70b-versatile}")
    private String model;

    @Value("${app.groq.whisper-model:whisper-large-v3-turbo}")
    private String whisperModel;

    /**
     * Dedicated RestTemplate for Groq LLM calls with the 30-second timeout required by
     * Sections 13 and 21. Kept separate from the default {@code restTemplate} bean so AI
     * timeouts do not affect other HTTP usage. Injected by bean name {@code groqRestTemplate}.
     */
    @Bean
    public RestTemplate groqRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Dedicated RestTemplate for Groq Whisper STT calls with the 60-second timeout required
     * by Section 21 (audio files can be large). Injected by bean name {@code groqWhisperRestTemplate}.
     */
    @Bean
    public RestTemplate groqWhisperRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(60))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
    }
}
