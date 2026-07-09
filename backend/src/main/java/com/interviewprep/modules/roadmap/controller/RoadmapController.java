package com.interviewprep.modules.roadmap.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.modules.roadmap.dto.RoadmapResponseDto;
import com.interviewprep.modules.roadmap.dto.RoadmapSelectionRequest;
import com.interviewprep.modules.roadmap.service.RoadmapService;
import com.interviewprep.security.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roadmaps")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadmapService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoadmapResponseDto>>> getAllRoadmaps() {
        return ResponseEntity.ok(ApiResponse.success("Roadmaps fetched", roadmapService.getAllRoadmaps()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoadmapResponseDto>> getRoadmap(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Roadmap fetched", roadmapService.getRoadmapById(id)));
    }

    @PostMapping("/select")
    public ResponseEntity<ApiResponse<RoadmapResponseDto>> selectRoadmap(
            @Valid @RequestBody RoadmapSelectionRequest request) {
        RoadmapResponseDto response = roadmapService.selectRoadmap(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Roadmap selected", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<RoadmapResponseDto>> getMyRoadmap() {
        return ResponseEntity.ok(ApiResponse.success("Active roadmap fetched",
                roadmapService.getMyRoadmap(SecurityUtils.getCurrentUserId())));
    }
}
