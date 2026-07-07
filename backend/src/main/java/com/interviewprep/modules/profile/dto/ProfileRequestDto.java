package com.interviewprep.modules.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequestDto {

    @NotBlank(message = "Current skills are required")
    private String currentSkills;

    @NotBlank(message = "Career path is required")
    @Size(max = 200, message = "Career path must not exceed 200 characters")
    private String careerPath;

    @NotNull(message = "Study duration (weeks) is required")
    private Integer studyDurationWeeks;

    @NotNull(message = "Daily study hours are required")
    @Min(value = 1, message = "Daily study hours must be at least 1")
    @Max(value = 4, message = "Daily study hours must not exceed 4")
    private Integer dailyStudyHours;
}
