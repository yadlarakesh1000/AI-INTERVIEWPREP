package com.interviewprep.modules.ai.service;

import com.interviewprep.modules.ai.dto.SkillRecommendationResponse;

/**
 * Abstraction for generating logical next-skill recommendations for a user.
 *
 * <p>Phase 1 implementation: {@code GroqSkillRecommendationServiceImpl} (STEP 020).
 */
public interface SkillRecommendationService {

    /**
     * Recommends the logical next skills for a developer to learn.
     *
     * @param currentSkills the user's current skills (comma-separated string)
     * @param careerPath    the user's career interest
     * @return the recommended skills, each with a mentor-style justification
     */
    SkillRecommendationResponse recommendSkills(String currentSkills, String careerPath);
}
