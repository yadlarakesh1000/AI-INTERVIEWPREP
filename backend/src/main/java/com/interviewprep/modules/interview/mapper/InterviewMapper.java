package com.interviewprep.modules.interview.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.interviewprep.modules.interview.dto.InterviewSummaryDto;
import com.interviewprep.modules.interview.entity.Interview;

/**
 * Maps the persisted {@link Interview} entity to its summary DTO. The list-valued fields
 * ({@code improvementSuggestions} and {@code questionResults}) are populated by the service
 * from the in-memory session and the stored JSON, so they are ignored here.
 */
@Mapper(componentModel = "spring")
public interface InterviewMapper {

    @Mapping(target = "interviewId", source = "id")
    @Mapping(target = "improvementSuggestions", ignore = true)
    @Mapping(target = "questionResults", ignore = true)
    InterviewSummaryDto toSummaryDto(Interview entity);
}
