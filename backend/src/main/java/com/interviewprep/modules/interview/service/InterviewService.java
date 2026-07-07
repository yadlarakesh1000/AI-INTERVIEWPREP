package com.interviewprep.modules.interview.service;

import com.interviewprep.modules.interview.dto.InterviewAnswerResponse;
import com.interviewprep.modules.interview.dto.InterviewResponseDto;
import com.interviewprep.modules.interview.dto.InterviewStartRequest;
import com.interviewprep.modules.interview.dto.InterviewSummaryDto;

/**
 * Orchestrates the voice interview flow (Section 15): starting an interview, processing a
 * spoken answer (STT → evaluation → next question → TTS), and ending with a scored summary.
 */
public interface InterviewService {

    InterviewResponseDto startInterview(Long userId, InterviewStartRequest request);

    InterviewAnswerResponse processAnswer(Long userId, Long interviewId, byte[] audioData, String fileName);

    InterviewSummaryDto endInterview(Long userId, Long interviewId);
}
