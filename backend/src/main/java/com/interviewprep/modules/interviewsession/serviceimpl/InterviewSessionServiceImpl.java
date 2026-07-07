package com.interviewprep.modules.interviewsession.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.interviewsession.model.InterviewSession;
import com.interviewprep.modules.interviewsession.service.InterviewSessionService;

import lombok.extern.slf4j.Slf4j;

/**
 * In-memory implementation of {@link InterviewSessionService} backed by a
 * {@link ConcurrentHashMap} (Section 15). Session state is NOT persisted; if the server
 * restarts mid-interview the session is lost, which is acceptable for this project's scope.
 *
 * <p>A scheduled task purges sessions older than two hours every 30 minutes to prevent
 * memory leaks from abandoned interviews.
 */
@Slf4j
@Service
public class InterviewSessionServiceImpl implements InterviewSessionService {

    private static final long SESSION_MAX_AGE_HOURS = 2;

    private final ConcurrentHashMap<String, InterviewSession> activeSessions = new ConcurrentHashMap<>();

    @Override
    public InterviewSession createSession(Long interviewId,
                                          Long userId,
                                          String type,
                                          String difficulty,
                                          int totalQuestions,
                                          ResumeData resumeData,
                                          String csTopic) {
        String sessionId = UUID.randomUUID().toString();

        InterviewSession session = InterviewSession.builder()
                .sessionId(sessionId)
                .interviewId(interviewId)
                .userId(userId)
                .interviewType(type)
                .difficulty(difficulty)
                .totalQuestions(totalQuestions)
                .currentQuestionNumber(0)
                .history(new ArrayList<>())
                .askedQuestionIds(new ArrayList<>())
                .resumeData(resumeData)
                .csTopic(csTopic)
                .startedAt(LocalDateTime.now())
                .build();

        activeSessions.put(sessionId, session);
        log.info("Created interview session {} for userId={} (interviewId={}, type={}, difficulty={})",
                sessionId, userId, interviewId, type, difficulty);
        return session;
    }

    @Override
    public InterviewSession getSession(String sessionId) {
        InterviewSession session = activeSessions.get(sessionId);
        if (session == null) {
            throw new ResourceNotFoundException("InterviewSession", "sessionId", sessionId);
        }
        return session;
    }

    @Override
    public InterviewSession getByInterviewId(Long interviewId) {
        return activeSessions.values().stream()
                .filter(session -> interviewId != null && interviewId.equals(session.getInterviewId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("InterviewSession", "interviewId", interviewId));
    }

    @Override
    public void updateSession(String sessionId, InterviewSession session) {
        activeSessions.put(sessionId, session);
    }

    @Override
    public void destroySession(String sessionId) {
        InterviewSession removed = activeSessions.remove(sessionId);
        if (removed != null) {
            // Release the temporary TTS audio held on the session to free memory promptly.
            removed.setCurrentQuestionAudio(null);
            log.info("Destroyed interview session {}", sessionId);
        }
    }

    @Override
    public boolean hasActiveSession(Long userId) {
        if (userId == null) {
            return false;
        }
        return activeSessions.values().stream()
                .anyMatch(session -> userId.equals(session.getUserId()));
    }

    /**
     * Purges sessions older than two hours. Runs every 30 minutes (Section 15) to prevent
     * memory leaks from interviews that were started but never ended.
     */
    @Scheduled(fixedRate = 1_800_000)
    public void cleanupStaleSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(SESSION_MAX_AGE_HOURS);
        int before = activeSessions.size();

        activeSessions.entrySet().removeIf(entry -> {
            LocalDateTime startedAt = entry.getValue().getStartedAt();
            return startedAt != null && startedAt.isBefore(cutoff);
        });

        int removed = before - activeSessions.size();
        if (removed > 0) {
            log.info("Stale session cleanup removed {} session(s); {} still active", removed, activeSessions.size());
        }
    }
}
