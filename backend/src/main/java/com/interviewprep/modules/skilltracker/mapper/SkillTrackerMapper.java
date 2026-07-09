package com.interviewprep.modules.skilltracker.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.interviewprep.modules.skilltracker.dto.WeekProgressDto;
import com.interviewprep.modules.skilltracker.entity.DayProgress;

/**
 * Maps a {@link DayProgress} entity to its day DTO. The {@code topic} is not stored on the
 * entity — it comes from the roadmap plan and is set by the service.
 */
@Mapper(componentModel = "spring")
public interface SkillTrackerMapper {

    @Mapping(target = "topic", ignore = true)
    WeekProgressDto.DayDto toDayDto(DayProgress entity);
}
