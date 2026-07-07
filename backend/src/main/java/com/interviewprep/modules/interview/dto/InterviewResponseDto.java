package com.interviewprep.modules.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for POST /api/interviews/start (Section 8.4) — the created interview, its session,
 * and the first question with its TTS audio URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewResponseDto {

    private Long interviewId;
    private String sessionId;
    private String interviewType;
    private String difficulty;
    private int totalQuestions;
    private int currentQuestionNumber;
    private QuestionDto question;
    private String ttsAudioUrl;
}
