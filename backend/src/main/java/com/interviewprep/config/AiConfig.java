package com.interviewprep.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;

/**
 * Top-level AI configuration (Section 6 of the specification). Reads the Gemini
 * settings from {@code application.yml} ({@code app.gemini.*}) and exposes the API key
 * to the Gemini TTS service implementation ({@code GeminiTtsServiceImpl}, STEP 018).
 */
@Getter
@Configuration
public class AiConfig {

    @Value("${app.gemini.api-key:}")
    private String geminiApiKey;

    /**
     * Dedicated RestTemplate for Gemini TTS calls with the 30-second timeout required by
     * Section 21. Injected by bean name {@code geminiRestTemplate}.
     */
    @Bean
    public RestTemplate geminiRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }
}
