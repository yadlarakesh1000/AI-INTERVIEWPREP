package com.interviewprep.modules.profile.serviceimpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.modules.auth.entity.User;
import com.interviewprep.modules.auth.repository.UserRepository;
import com.interviewprep.modules.profile.dto.ProfileRequestDto;
import com.interviewprep.modules.profile.dto.ProfileResponseDto;
import com.interviewprep.modules.profile.entity.UserProfile;
import com.interviewprep.modules.profile.exception.ProfileException;
import com.interviewprep.modules.profile.mapper.ProfileMapper;
import com.interviewprep.modules.profile.repository.UserProfileRepository;
import com.interviewprep.modules.profile.service.ProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional
    public ProfileResponseDto createProfile(Long userId, ProfileRequestDto request) {
        validateStudyDuration(request.getStudyDurationWeeks());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (userProfileRepository.existsByUserId(userId)) {
            throw new ProfileException("Profile already exists for this user");
        }

        UserProfile profile = profileMapper.toEntity(request);
        profile.setUser(user);
        UserProfile saved = userProfileRepository.save(profile);
        log.info("Created profile id={} for userId={}", saved.getId(), userId);

        return profileMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));
        return profileMapper.toDto(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateProfile(Long userId, ProfileRequestDto request) {
        validateStudyDuration(request.getStudyDurationWeeks());

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", userId));

        profile.setCurrentSkills(request.getCurrentSkills());
        profile.setCareerPath(request.getCareerPath());
        profile.setStudyDurationWeeks(request.getStudyDurationWeeks());
        profile.setDailyStudyHours(request.getDailyStudyHours());

        UserProfile saved = userProfileRepository.save(profile);
        log.info("Updated profile id={} for userId={}", saved.getId(), userId);

        return profileMapper.toDto(saved);
    }

    private void validateStudyDuration(Integer studyDurationWeeks) {
        if (studyDurationWeeks == null || (studyDurationWeeks != 26 && studyDurationWeeks != 52)) {
            throw new ProfileException("Study duration must be either 26 or 52 weeks");
        }
    }
}
