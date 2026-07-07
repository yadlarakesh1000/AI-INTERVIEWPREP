package com.interviewprep.modules.ai.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LLM-generated skill recommendations for a user, matching the response structure
 * in Section 8.7 of the specification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRecommendationResponse {

    private List<SkillSuggestion> recommendations;

    /**
     * A single recommended skill with a mentor-style justification.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SkillSuggestion {
        private String skill;
        private String reason;
    }
}
