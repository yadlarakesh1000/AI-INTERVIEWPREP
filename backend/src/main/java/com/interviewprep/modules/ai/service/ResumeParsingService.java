package com.interviewprep.modules.ai.service;

import com.interviewprep.modules.ai.dto.ResumeData;

/**
 * Abstraction for structuring raw resume text into typed resume data.
 *
 * <p>Phase 1 implementation: {@code PdfBoxGroqResumeParsingServiceImpl} (STEP 019),
 * which sends the PDFBox-extracted text to the Groq LLM for structured JSON output.
 */
public interface ResumeParsingService {

    /**
     * Parses raw resume text into structured resume data.
     *
     * @param extractedText the raw text extracted from the resume PDF
     * @return the structured resume data (skills, projects, experience, education)
     */
    ResumeData parseResumeText(String extractedText);
}
