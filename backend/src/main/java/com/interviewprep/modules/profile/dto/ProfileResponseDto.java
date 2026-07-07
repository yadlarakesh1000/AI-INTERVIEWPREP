package com.interviewprep.modules.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto {

    private Long id;
    private Long userId;
    private String currentSkills;
    private String careerPath;
    private Integer studyDurationWeeks;
    private Integer dailyStudyHours;
}
