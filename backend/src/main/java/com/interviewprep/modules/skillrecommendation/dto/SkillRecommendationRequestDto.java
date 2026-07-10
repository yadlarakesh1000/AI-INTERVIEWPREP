package com.interviewprep.modules.skillrecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Carrier for the inputs to a skill recommendation (Section 6). The GET endpoint sources
 * these values from the user's profile rather than the request body, so this DTO is the
 * internal/request-shape representation of those inputs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRecommendationRequestDto {

    private String currentSkills;
    private String careerPath;
}
