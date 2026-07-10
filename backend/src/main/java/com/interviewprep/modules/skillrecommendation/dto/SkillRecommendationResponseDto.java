package com.interviewprep.modules.skillrecommendation.dto;

import java.util.List;

import com.interviewprep.modules.ai.dto.SkillRecommendationResponse.SkillSuggestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for GET /api/skill-recommendations, matching Section 8.7 exactly: the user's
 * current skills (as a list), their career path, and the LLM-recommended next skills.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRecommendationResponseDto {

    private List<String> currentSkills;
    private String careerPath;
    private List<SkillSuggestion> recommendations;
}
