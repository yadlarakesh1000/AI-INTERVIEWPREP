package com.interviewprep.modules.roadmap.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for POST /api/roadmaps/select (Section 8.5).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapSelectionRequest {

    @NotNull(message = "roadmapTemplateId is required")
    private Long roadmapTemplateId;

    @NotNull(message = "selectedDurationWeeks is required")
    private Integer selectedDurationWeeks;
}
