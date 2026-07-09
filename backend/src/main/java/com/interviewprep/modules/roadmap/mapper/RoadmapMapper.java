package com.interviewprep.modules.roadmap.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.interviewprep.modules.roadmap.dto.RoadmapResponseDto;
import com.interviewprep.modules.roadmap.entity.RoadmapTemplate;

/**
 * Maps a {@link RoadmapTemplate} to its summary DTO. The JSON detail fields are parsed and
 * set by the service (they are stored as JSON strings but exposed as parsed JSON nodes), and
 * {@code selectedDurationWeeks} comes from the user's roadmap, so both are ignored here.
 */
@Mapper(componentModel = "spring")
public interface RoadmapMapper {

    @Mapping(target = "weeklyPlan", ignore = true)
    @Mapping(target = "milestones", ignore = true)
    @Mapping(target = "recommendedProjects", ignore = true)
    @Mapping(target = "learningResources", ignore = true)
    @Mapping(target = "selectedDurationWeeks", ignore = true)
    RoadmapResponseDto toSummary(RoadmapTemplate entity);
}
