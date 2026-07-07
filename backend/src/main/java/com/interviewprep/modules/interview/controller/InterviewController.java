package com.interviewprep.modules.interview.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.interviewprep.common.ApiResponse;
import com.interviewprep.exception.BadRequestException;
import com.interviewprep.modules.interview.dto.InterviewAnswerResponse;
import com.interviewprep.modules.interview.dto.InterviewResponseDto;
import com.interviewprep.modules.interview.dto.InterviewStartRequest;
import com.interviewprep.modules.interview.dto.InterviewSummaryDto;
import com.interviewprep.modules.interview.service.InterviewService;
import com.interviewprep.modules.interviewsession.model.InterviewSession;
import com.interviewprep.modules.interviewsession.service.InterviewSessionService;
import com.interviewprep.security.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;
    private final InterviewSessionService interviewSessionService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<InterviewResponseDto>> startInterview(
            @Valid @RequestBody InterviewStartRequest request) {
        InterviewResponseDto response = interviewService.startInterview(
                SecurityUtils.getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Interview started", response));
    }

    @PostMapping(value = "/{interviewId}/answer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<InterviewAnswerResponse>> answer(
            @PathVariable Long interviewId,
            @RequestParam("audio") MultipartFile audio) {
        byte[] audioBytes = readAudio(audio);
        InterviewAnswerResponse response = interviewService.processAnswer(
                SecurityUtils.getCurrentUserId(), interviewId, audioBytes, audio.getOriginalFilename());
        return ResponseEntity.ok(ApiResponse.success("Answer evaluated", response));
    }

    @PostMapping("/{interviewId}/end")
    public ResponseEntity<ApiResponse<InterviewSummaryDto>> end(@PathVariable Long interviewId) {
        InterviewSummaryDto summary = interviewService.endInterview(
                SecurityUtils.getCurrentUserId(), interviewId);
        return ResponseEntity.ok(ApiResponse.success("Interview completed", summary));
    }

    @GetMapping("/audio/{sessionId}/{questionKey}")
    public ResponseEntity<byte[]> audio(@PathVariable String sessionId,
                                        @PathVariable String questionKey) {
        InterviewSession session = interviewSessionService.getSession(sessionId);
        byte[] audio = session.getCurrentQuestionAudio();
        if (audio == null || audio.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(audio);
    }

    private byte[] readAudio(MultipartFile audio) {
        if (audio == null || audio.isEmpty()) {
            throw new BadRequestException("Audio file is required.");
        }
        try {
            return audio.getBytes();
        } catch (IOException ex) {
            throw new BadRequestException("Could not read the uploaded audio. Please try again.");
        }
    }
}
