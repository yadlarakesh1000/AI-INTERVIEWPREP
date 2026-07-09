package com.interviewprep.modules.roadmap.service;

import java.util.List;

import com.interviewprep.modules.roadmap.dto.RoadmapResponseDto;
import com.interviewprep.modules.roadmap.dto.RoadmapSelectionRequest;

/**
 * Browsing and selection of career roadmap templates (Section 8.5). Enforces the one-active-
 * roadmap-per-user rule (Section 9).
 */
public interface RoadmapService {

    List<RoadmapResponseDto> getAllRoadmaps();

    RoadmapResponseDto getRoadmapById(Long id);

    RoadmapResponseDto selectRoadmap(Long userId, RoadmapSelectionRequest request);

    RoadmapResponseDto getMyRoadmap(Long userId);
}
