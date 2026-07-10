package com.interviewprep.modules.analytics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.modules.analytics.dto.AnalyticsResponseDto;
import com.interviewprep.modules.analytics.service.AnalyticsService;
import com.interviewprep.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<ApiResponse<AnalyticsResponseDto>> getAnalytics() {
        return ResponseEntity.ok(ApiResponse.success("Analytics fetched",
                analyticsService.getAnalytics(SecurityUtils.getCurrentUserId())));
    }
}
