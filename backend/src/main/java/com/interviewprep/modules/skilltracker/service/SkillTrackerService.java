package com.interviewprep.modules.skilltracker.service;

import com.interviewprep.modules.skilltracker.dto.DayToggleRequest;
import com.interviewprep.modules.skilltracker.dto.SkillTrackerOverviewDto;
import com.interviewprep.modules.skilltracker.dto.WeekProgressDto;

/**
 * Day-by-day progress tracking against the user's active roadmap (Section 8.6). Day progress
 * records are initialized lazily when a week is first viewed (Section 9).
 */
public interface SkillTrackerService {

    WeekProgressDto getWeekProgress(Long userId, Integer weekNumber);

    WeekProgressDto.DayDto toggleDay(Long userId, DayToggleRequest request);

    SkillTrackerOverviewDto getOverview(Long userId);
}
