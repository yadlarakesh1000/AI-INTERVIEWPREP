package com.interviewprep.modules.profile.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.interviewprep.modules.profile.dto.ProfileRequestDto;
import com.interviewprep.modules.profile.dto.ProfileResponseDto;
import com.interviewprep.modules.profile.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserProfile toEntity(ProfileRequestDto dto);

    @Mapping(target = "userId", source = "user.id")
    ProfileResponseDto toDto(UserProfile entity);
}
