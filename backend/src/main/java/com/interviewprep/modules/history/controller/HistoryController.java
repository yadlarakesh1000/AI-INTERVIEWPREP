package com.interviewprep.modules.history.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.modules.history.dto.InterviewHistoryDto;
import com.interviewprep.modules.history.service.HistoryService;
import com.interviewprep.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InterviewHistoryDto>>> getHistory() {
        return ResponseEntity.ok(ApiResponse.success("Interview history fetched",
                historyService.getHistory(SecurityUtils.getCurrentUserId())));
    }
}
