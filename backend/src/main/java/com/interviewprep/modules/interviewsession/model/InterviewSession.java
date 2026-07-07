package com.interviewprep.modules.interviewsession.model;

import java.time.LocalDateTime;
import java.util.List;

import com.interviewprep.modules.ai.dto.ResumeData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * In-memory state for a single active interview session (Section 15). This is a plain POJO —
 * NOT a JPA entity. Sessions are held only in memory (in a ConcurrentHashMap in the session
 * service) for the duration of an interview and are never persisted to the database; if the
 * server restarts mid-interview, the session is lost, which is acceptable for this scope.
 *
 * <p>The {@code history} of {@link QAPair}s provides the context used to generate
 * context-aware follow-up questions in Medium difficulty.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSession {

    private String sessionId;             // UUID
    private Long interviewId;             // DB interview record ID
    private Long userId;
    private String interviewType;         // HR, RESUME_BASED, CS_FUNDAMENTALS
    private String difficulty;            // EASY, MEDIUM
    private int totalQuestions;
    private int currentQuestionNumber;
    private List<QAPair> history;         // all Q&A pairs so far (for follow-up context)
    private List<Long> askedQuestionIds;  // ids of HR bank questions already asked (repeat avoidance)
    private ResumeData resumeData;        // null unless RESUME_BASED
    private String csTopic;               // null unless CS_FUNDAMENTALS
    private LocalDateTime startedAt;
    private byte[] currentQuestionAudio;  // TTS audio for the current question (temp storage)
}
