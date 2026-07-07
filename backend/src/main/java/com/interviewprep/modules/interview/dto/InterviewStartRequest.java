package com.interviewprep.modules.interview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for POST /api/interviews/start (Section 8.4).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewStartRequest {

    @NotBlank(message = "interviewType is required")
    private String interviewType;

    @NotBlank(message = "difficulty is required")
    private String difficulty;

    @Min(value = 3, message = "totalQuestions must be at least 3")
    @Max(value = 10, message = "totalQuestions must be at most 10")
    @Builder.Default
    private int totalQuestions = 5;

    // Optional — only relevant for CS_FUNDAMENTALS interviews (e.g. OOP, DBMS, OS, Networks).
    private String csTopic;
}
