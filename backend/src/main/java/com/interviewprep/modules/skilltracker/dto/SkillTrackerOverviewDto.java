package com.interviewprep.modules.skilltracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Overall skill-tracker progress for the active roadmap (Section 8.6): total weeks,
 * completed days, overall percentage, and the current week.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillTrackerOverviewDto {

    private int totalWeeks;
    private int totalDays;
    private long completedDays;
    private double progressPercent;
    private int currentWeek;
}
