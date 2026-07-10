package com.interviewprep.modules.history.service;

import java.util.List;

import com.interviewprep.modules.history.dto.InterviewHistoryDto;

/**
 * Lightweight interview history (Section 8.9) — the user's last five completed interviews.
 */
public interface HistoryService {

    List<InterviewHistoryDto> getHistory(Long userId);
}
