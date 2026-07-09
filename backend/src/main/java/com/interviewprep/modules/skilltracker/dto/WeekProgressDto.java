package com.interviewprep.modules.skilltracker.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A week's day-by-day progress for the active roadmap (Section 8.6).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeekProgressDto {

    private int weekNumber;
    private String weekTitle;
    private List<DayDto> days;
    private double progressPercent;

    /**
     * A single day: its topic (from the roadmap plan) and completion state.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DayDto {
        private int dayNumber;
        private String topic;
        private Boolean isCompleted;
        private LocalDateTime completedAt;
    }
}
