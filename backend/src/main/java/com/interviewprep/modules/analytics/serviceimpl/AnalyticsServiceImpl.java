package com.interviewprep.modules.analytics.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.modules.analytics.dto.AnalyticsResponseDto;
import com.interviewprep.modules.analytics.dto.AnalyticsResponseDto.RecentScoreDto;
import com.interviewprep.modules.analytics.service.AnalyticsService;
import com.interviewprep.modules.interview.entity.Interview;
import com.interviewprep.modules.interview.entity.InterviewStatus;
import com.interviewprep.modules.interview.repository.InterviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final double STABLE_THRESHOLD = 0.5;

    private final InterviewRepository interviewRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponseDto getAnalytics(Long userId) {
        // Newest first. Only the latest 5 completed interviews are retained per user
        // (Section 9), so this is the complete completed history.
        List<Interview> recent = interviewRepository
                .findTop5ByUserIdAndStatusOrderByCreatedAtDesc(userId, InterviewStatus.COMPLETED.name());
        long totalInterviews = interviewRepository
                .countByUserIdAndStatus(userId, InterviewStatus.COMPLETED.name());

        return AnalyticsResponseDto.builder()
                .totalInterviews(totalInterviews)
                .averageOverallScore(round2(average(recent, true)))
                .averageConfidenceScore(round2(average(recent, false)))
                .improvementStatus(improvementStatus(recent))
                .recentScores(toRecentScores(recent))
                .latestSuggestions(latestSuggestions(recent))
                .build();
    }

    private double average(List<Interview> interviews, boolean overall) {
        return interviews.stream()
                .map(i -> overall ? i.getOverallScore() : i.getConfidenceScore())
                .filter(java.util.Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average().orElse(0.0);
    }

    /**
     * Compares the average overall score of the last 3 interviews against the previous ones
     * (Section 9): IMPROVING if higher, DECLINING if lower, STABLE if within 0.5, and
     * NOT_ENOUGH_DATA when fewer than 3 interviews exist or there is nothing to compare
     * against.
     */
    private String improvementStatus(List<Interview> newestFirst) {
        if (newestFirst.size() < 3) {
            return "NOT_ENOUGH_DATA";
        }
        List<Interview> last3 = newestFirst.subList(0, 3);
        List<Interview> previous = newestFirst.subList(3, newestFirst.size());
        if (previous.isEmpty()) {
            return "NOT_ENOUGH_DATA";
        }

        double lastAvg = average(last3, true);
        double previousAvg = average(previous, true);
        double diff = lastAvg - previousAvg;

        if (Math.abs(diff) <= STABLE_THRESHOLD) {
            return "STABLE";
        }
        return diff > 0 ? "IMPROVING" : "DECLINING";
    }

    private List<RecentScoreDto> toRecentScores(List<Interview> newestFirst) {
        // Chronological (oldest -> newest) so the line chart reads left to right,
        // matching the Section 8.8 example ordering.
        List<Interview> chronological = new ArrayList<>(newestFirst);
        Collections.reverse(chronological);

        List<RecentScoreDto> scores = new ArrayList<>();
        for (Interview interview : chronological) {
            scores.add(RecentScoreDto.builder()
                    .interviewId(interview.getId())
                    .date(interview.getCreatedAt() == null ? null : interview.getCreatedAt().toLocalDate())
                    .overallScore(interview.getOverallScore() == null ? 0.0 : interview.getOverallScore().doubleValue())
                    .confidenceScore(interview.getConfidenceScore() == null ? 0.0 : interview.getConfidenceScore().doubleValue())
                    .type(interview.getInterviewType())
                    .build());
        }
        return scores;
    }

    /**
     * The improvement suggestions from the most recent completed interview, parsed from the
     * stored JSON string.
     */
    private List<String> latestSuggestions(List<Interview> newestFirst) {
        if (newestFirst.isEmpty()) {
            return List.of();
        }
        String json = newestFirst.get(0).getImprovementSuggestions();
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            String[] parsed = objectMapper.readValue(json, String[].class);
            return List.of(parsed);
        } catch (Exception ex) {
            log.warn("Could not parse stored improvement suggestions; returning empty list", ex);
            return List.of();
        }
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
