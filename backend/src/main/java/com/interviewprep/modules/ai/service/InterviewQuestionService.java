package com.interviewprep.modules.ai.service;

import java.util.List;

import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.interviewsession.model.QAPair;

/**
 * Abstraction for generating interview questions. This is the seam described in
 * Section 5 of the specification that lets Phase 2 swap in a RAG-based implementation
 * without touching controllers or the frontend.
 *
 * <p>Phase 1 implementation: {@code GroqInterviewQuestionServiceImpl} (STEP 016).
 */
public interface InterviewQuestionService {

    /**
     * Generates the next interview question. The signature is intentionally flexible
     * enough to cover all three interview types (HR, RESUME_BASED, CS_FUNDAMENTALS)
     * and both difficulties (EASY, MEDIUM).
     *
     * @param interviewType one of HR, RESUME_BASED, CS_FUNDAMENTALS
     * @param difficulty    one of EASY, MEDIUM
     * @param topic         optional CS-fundamentals topic (e.g. OOP, DBMS); null otherwise
     * @param history       the Q&A pairs so far in the session, used as context for
     *                      Medium-difficulty follow-up questions; empty for the first question
     * @param resumeData    parsed resume data for RESUME_BASED interviews; null otherwise
     * @return the generated question text
     */
    String generateQuestion(String interviewType,
                            String difficulty,
                            String topic,
                            List<QAPair> history,
                            ResumeData resumeData);
}
