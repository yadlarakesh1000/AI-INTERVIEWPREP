package com.interviewprep.modules.skillrecommendation.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.modules.ai.dto.SkillRecommendationResponse;
import com.interviewprep.modules.ai.service.SkillRecommendationService;
import com.interviewprep.modules.profile.dto.ProfileResponseDto;
import com.interviewprep.modules.profile.service.ProfileService;
import com.interviewprep.modules.skillrecommendation.dto.SkillRecommendationResponseDto;
import com.interviewprep.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

/**
 * Thin controller (Section 8.7): reads the user's profile for their current skills and
 * career path, delegates to the AI-layer {@link SkillRecommendationService}, and shapes the
 * response to the Section 8.7 contract.
 */
@RestController
@RequestMapping("/api/skill-recommendations")
@RequiredArgsConstructor
public class SkillRecommendationController {

    private final ProfileService profileService;
    private final SkillRecommendationService skillRecommendationService;

    @GetMapping
    public ResponseEntity<ApiResponse<SkillRecommendationResponseDto>> getRecommendations() {
        ProfileResponseDto profile = profileService.getProfile(SecurityUtils.getCurrentUserId());

        SkillRecommendationResponse recommendations = skillRecommendationService
                .recommendSkills(profile.getCurrentSkills(), profile.getCareerPath());

        SkillRecommendationResponseDto response = SkillRecommendationResponseDto.builder()
                .currentSkills(splitSkills(profile.getCurrentSkills()))
                .careerPath(profile.getCareerPath())
                .recommendations(recommendations.getRecommendations())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Skill recommendations generated", response));
    }

    private List<String> splitSkills(String currentSkills) {
        if (currentSkills == null || currentSkills.isBlank()) {
            return List.of();
        }
        return Arrays.stream(currentSkills.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
