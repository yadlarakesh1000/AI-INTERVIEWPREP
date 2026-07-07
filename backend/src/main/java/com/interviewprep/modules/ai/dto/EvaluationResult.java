package com.interviewprep.modules.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of evaluating a single interview answer against the 4-dimension rubric
 * defined in Section 9 of the specification.
 *
 * <p>Each dimension is scored 1-10 with a one-line feedback justification. The two
 * displayed scores are derived:
 * <ul>
 *     <li>{@code overallScore}    = average(relevance, depthAndAccuracy)</li>
 *     <li>{@code confidenceScore} = average(clarityAndStructure, confidenceAndFluency)</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationResult {

    private DimensionScore relevance;
    private DimensionScore clarityAndStructure;
    private DimensionScore depthAndAccuracy;
    private DimensionScore confidenceAndFluency;

    private double overallScore;
    private double confidenceScore;

    /**
     * A single rubric dimension: a 1-10 score with a one-line feedback justification.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DimensionScore {
        private int score;
        private String feedback;
    }
}
