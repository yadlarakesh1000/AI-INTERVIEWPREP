package com.interviewprep.modules.analytics.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Analytics response (Section 8.8): aggregate interview performance, the improvement trend,
 * the last five completed interviews for the score line chart, and the latest improvement
 * suggestions. Everything is computed from queries — nothing is stored separately.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsResponseDto {

    private long totalInterviews;
    private double averageOverallScore;
    private double averageConfidenceScore;

    // IMPROVING / DECLINING / STABLE / NOT_ENOUGH_DATA (Section 9 rules).
    private String improvementStatus;

    private List<RecentScoreDto> recentScores;
    private List<String> latestSuggestions;

    /**
     * One completed interview's scores for the line chart (Section 8.8).
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentScoreDto {
        private Long interviewId;
        private LocalDate date;
        private double overallScore;
        private double confidenceScore;
        private String type;
    }
}
