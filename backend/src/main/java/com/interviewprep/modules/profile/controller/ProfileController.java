package com.interviewprep.modules.profile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.modules.profile.dto.ProfileRequestDto;
import com.interviewprep.modules.profile.dto.ProfileResponseDto;
import com.interviewprep.modules.profile.service.ProfileService;
import com.interviewprep.security.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponseDto>> createProfile(
            @Valid @RequestBody ProfileRequestDto request) {
        ProfileResponseDto profile = profileService.createProfile(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Profile created", profile));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile() {
        ProfileResponseDto profile = profileService.getProfile(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", profile));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProfileResponseDto>> updateProfile(
            @Valid @RequestBody ProfileRequestDto request) {
        ProfileResponseDto profile = profileService.updateProfile(SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", profile));
    }
}
