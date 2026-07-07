package com.interviewprep.modules.resume.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.exception.BadRequestException;
import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.ai.service.ResumeParsingService;
import com.interviewprep.modules.auth.entity.User;
import com.interviewprep.modules.auth.repository.UserRepository;
import com.interviewprep.modules.resume.dto.ResumeResponseDto;
import com.interviewprep.modules.resume.entity.Resume;
import com.interviewprep.modules.resume.exception.ResumeException;
import com.interviewprep.modules.resume.mapper.ResumeMapper;
import com.interviewprep.modules.resume.repository.ResumeRepository;
import com.interviewprep.modules.resume.service.ResumeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024; // 5MB

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final ResumeMapper resumeMapper;
    private final ResumeParsingService resumeParsingService;
    private final ObjectMapper objectMapper;

    @Value("${app.resume.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public ResumeResponseDto uploadResume(Long userId, MultipartFile file) {
        validateFile(file);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String originalName = sanitizeFileName(file.getOriginalFilename());
        String storedName = System.currentTimeMillis() + "_" + originalName;

        String storedPath = storeFile(file, userId, storedName);
        String extractedText = extractText(file);

        // Structure the extracted text into skills/projects/experience via the AI parser,
        // then persist it as a JSON string. Parsing degrades gracefully to empty data, so a
        // parsing failure never blocks the upload.
        ResumeData parsedData = resumeParsingService.parseResumeText(extractedText);
        String parsedJson = toJson(parsedData);

        Resume resume = Resume.builder()
                .user(user)
                .fileName(originalName)
                .filePath(storedPath)
                .extractedText(extractedText)
                .parsedData(parsedJson)
                .build();

        Resume saved = resumeRepository.save(resume);
        log.info("Uploaded resume id={} for userId={} ({} chars extracted, parsed={})",
                saved.getId(), userId, extractedText == null ? 0 : extractedText.length(), parsedJson != null);

        return resumeMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeResponseDto getLatestResume(Long userId) {
        Resume resume = resumeRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "userId", userId));
        return resumeMapper.toDto(resume);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Resume file is required.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("Resume file must not exceed 5MB.");
        }
        String contentType = file.getContentType();
        String name = file.getOriginalFilename();
        boolean isPdf = "application/pdf".equalsIgnoreCase(contentType)
                || (name != null && name.toLowerCase().endsWith(".pdf"));
        if (!isPdf) {
            throw new BadRequestException("Only PDF files are accepted.");
        }
    }

    private String storeFile(MultipartFile file, Long userId, String storedName) {
        try {
            Path userDir = Paths.get(uploadDir, String.valueOf(userId)).toAbsolutePath().normalize();
            Files.createDirectories(userDir);
            Path target = userDir.resolve(storedName);
            file.transferTo(target);
            return target.toString();
        } catch (IOException ex) {
            log.error("Failed to store resume file for userId={}", userId, ex);
            throw new ResumeException("Could not store the resume file. Please try again.", ex);
        }
    }

    private String extractText(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException ex) {
            log.error("Failed to extract text from resume PDF", ex);
            throw new ResumeException("Could not read the PDF file. Please upload a valid PDF.", ex);
        }
    }

    private String toJson(ResumeData data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize parsed resume data to JSON", ex);
            return null;
        }
    }

    private String sanitizeFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "resume.pdf";
        }
        // Strip any path components to prevent path traversal.
        return Paths.get(originalFilename).getFileName().toString();
    }
}
