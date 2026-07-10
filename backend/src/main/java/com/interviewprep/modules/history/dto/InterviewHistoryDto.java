package com.interviewprep.modules.history.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A lightweight interview history record (Section 8.9): type, difficulty, date, duration,
 * and overall score. No transcripts are stored or returned.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewHistoryDto {

    private Long interviewId;
    private String interviewType;
    private String difficulty;
    private LocalDate date;
    private Integer durationSeconds;
    private double overallScore;
}
