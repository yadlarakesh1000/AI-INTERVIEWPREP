package com.interviewprep.modules.interviewsession.service;

import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.interviewsession.model.InterviewSession;

/**
 * Manages the lifecycle of in-memory interview sessions (Section 15). Sessions live only in
 * memory for the duration of an interview and are never persisted to the database.
 */
public interface InterviewSessionService {

    InterviewSession createSession(Long interviewId,
                                   Long userId,
                                   String type,
                                   String difficulty,
                                   int totalQuestions,
                                   ResumeData resumeData,
                                   String csTopic);

    InterviewSession getSession(String sessionId);

    /**
     * Finds the active session for a DB interview id. The answer/end endpoints are keyed by
     * interviewId (Section 8.4), so this locates the in-memory session from that id.
     */
    InterviewSession getByInterviewId(Long interviewId);

    void updateSession(String sessionId, InterviewSession session);

    void destroySession(String sessionId);

    boolean hasActiveSession(Long userId);
}
