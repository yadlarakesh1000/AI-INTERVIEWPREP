package com.interviewprep.modules.interview.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for POST /api/interviews/{id}/end and the result view (Section 8.4) — the final
 * scores, improvement suggestions, and a per-question breakdown.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSummaryDto {

    private Long interviewId;
    private String interviewType;
    private String difficulty;
    private int totalQuestions;
    private int durationSeconds;
    private double overallScore;
    private double confidenceScore;
    private List<String> improvementSuggestions;
    private List<QuestionResultDto> questionResults;

    /**
     * Per-question scores for the result breakdown (Section 8.4).
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionResultDto {
        private int questionNumber;
        private String questionText;
        private double overallScore;
        private double confidenceScore;
    }
}
