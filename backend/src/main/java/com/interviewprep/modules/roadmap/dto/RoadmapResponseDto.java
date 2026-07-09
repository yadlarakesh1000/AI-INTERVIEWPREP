package com.interviewprep.modules.roadmap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Roadmap response (Section 8.5). Used both as a lightweight summary (list endpoint — only
 * the top fields populated) and as full detail (single/my endpoints — including the parsed
 * JSON plan). Null fields are omitted so summaries stay lean.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoadmapResponseDto {

    private Long id;
    private String title;
    private String description;
    private String careerPath;
    private Integer durationWeeks;

    // Full-detail fields — parsed from the stored JSON so the response nests real JSON, not
    // escaped strings. Null (and omitted) for the summary/list view.
    private JsonNode weeklyPlan;
    private JsonNode milestones;
    private JsonNode recommendedProjects;
    private JsonNode learningResources;

    // Populated only for the user's active roadmap (GET /my and select response).
    private Integer selectedDurationWeeks;
}
