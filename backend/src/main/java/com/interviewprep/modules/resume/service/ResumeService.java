package com.interviewprep.modules.resume.service;

import org.springframework.web.multipart.MultipartFile;

import com.interviewprep.modules.resume.dto.ResumeResponseDto;

public interface ResumeService {

    ResumeResponseDto uploadResume(Long userId, MultipartFile file);

    ResumeResponseDto getLatestResume(Long userId);
}
