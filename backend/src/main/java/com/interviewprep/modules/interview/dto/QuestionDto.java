package com.interviewprep.modules.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single interview question — its text and its 1-based number within the interview
 * (Section 8.4).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {

    private String text;
    private int questionNumber;
}
