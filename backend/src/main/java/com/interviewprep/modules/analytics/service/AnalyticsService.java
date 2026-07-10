package com.interviewprep.modules.analytics.service;

import com.interviewprep.modules.analytics.dto.AnalyticsResponseDto;

/**
 * Query-based interview analytics (Section 8.8). All values are computed on demand from the
 * interviews table — no analytics tables exist (Section 9).
 */
public interface AnalyticsService {

    AnalyticsResponseDto getAnalytics(Long userId);
}
