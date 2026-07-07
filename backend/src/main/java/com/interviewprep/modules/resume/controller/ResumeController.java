package com.interviewprep.modules.resume.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.modules.resume.dto.ResumeResponseDto;
import com.interviewprep.modules.resume.service.ResumeService;
import com.interviewprep.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeResponseDto>> uploadResume(
            @RequestParam("resume") MultipartFile file) {
        ResumeResponseDto resume = resumeService.uploadResume(SecurityUtils.getCurrentUserId(), file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resume uploaded and parsed successfully", resume));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<ResumeResponseDto>> getLatestResume() {
        ResumeResponseDto resume = resumeService.getLatestResume(SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(ApiResponse.success("Latest resume fetched", resume));
    }
}
