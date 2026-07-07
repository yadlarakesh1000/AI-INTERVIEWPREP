package com.interviewprep.modules.interview.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interviewprep.modules.ai.dto.EvaluationResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for POST /api/interviews/{id}/answer (Section 8.4) — the transcribed answer, its
 * 4-dimension evaluation, and the next question (if any) with its TTS audio URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewAnswerResponse {

    private int questionNumber;
    private String transcribedAnswer;
    private EvaluationResult evaluation;
    private QuestionDto nextQuestion;
    private String ttsAudioUrl;

    // Serialized as "isLastQuestion" to match the Section 8.4 contract exactly.
    @JsonProperty("isLastQuestion")
    private boolean lastQuestion;
}
