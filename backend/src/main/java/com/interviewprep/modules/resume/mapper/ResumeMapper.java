package com.interviewprep.modules.resume.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.interviewprep.modules.resume.dto.ResumeResponseDto;
import com.interviewprep.modules.resume.entity.Resume;

@Mapper(componentModel = "spring")
public interface ResumeMapper {

    @Mapping(target = "userId", source = "user.id")
    ResumeResponseDto toDto(Resume entity);
}
