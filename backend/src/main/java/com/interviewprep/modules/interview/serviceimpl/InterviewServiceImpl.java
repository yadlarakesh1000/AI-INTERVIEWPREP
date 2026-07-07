package com.interviewprep.modules.interview.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.common.AppConstants;
import com.interviewprep.exception.BadRequestException;
import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.exception.UnauthorizedException;
import com.interviewprep.modules.ai.dto.EvaluationResult;
import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.ai.service.AnswerEvaluationService;
import com.interviewprep.modules.ai.service.InterviewQuestionService;
import com.interviewprep.modules.ai.service.SpeechToTextService;
import com.interviewprep.modules.ai.service.TextToSpeechService;
import com.interviewprep.modules.interview.dto.InterviewAnswerResponse;
import com.interviewprep.modules.interview.dto.InterviewResponseDto;
import com.interviewprep.modules.interview.dto.InterviewStartRequest;
import com.interviewprep.modules.interview.dto.InterviewSummaryDto;
import com.interviewprep.modules.interview.dto.InterviewSummaryDto.QuestionResultDto;
import com.interviewprep.modules.interview.dto.QuestionDto;
import com.interviewprep.modules.interview.entity.Difficulty;
import com.interviewprep.modules.interview.entity.Interview;
import com.interviewprep.modules.interview.entity.InterviewStatus;
import com.interviewprep.modules.interview.entity.InterviewType;
import com.interviewprep.modules.interview.exception.InterviewException;
import com.interviewprep.modules.interview.mapper.InterviewMapper;
import com.interviewprep.modules.interview.repository.InterviewRepository;
import com.interviewprep.modules.interview.service.InterviewService;
import com.interviewprep.modules.interviewsession.model.InterviewSession;
import com.interviewprep.modules.interviewsession.model.QAPair;
import com.interviewprep.modules.interviewsession.service.InterviewSessionService;
import com.interviewprep.modules.profile.entity.UserProfile;
import com.interviewprep.modules.profile.repository.UserProfileRepository;
import com.interviewprep.modules.questionbank.entity.HrQuestion;
import com.interviewprep.modules.questionbank.service.QuestionBankService;
import com.interviewprep.modules.resume.entity.Resume;
import com.interviewprep.modules.resume.repository.ResumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewSessionService interviewSessionService;
    private final InterviewQuestionService interviewQuestionService;
    private final AnswerEvaluationService answerEvaluationService;
    private final SpeechToTextService speechToTextService;
    private final TextToSpeechService textToSpeechService;
    private final QuestionBankService questionBankService;
    private final ResumeRepository resumeRepository;
    private final UserProfileRepository userProfileRepository;
    private final InterviewMapper interviewMapper;
    private final ObjectMapper objectMapper;

    @Override
    public InterviewResponseDto startInterview(Long userId, InterviewStartRequest request) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(
                        "Please complete your profile before starting an interview."));

        if (interviewRepository.findByUserIdAndStatus(userId, InterviewStatus.IN_PROGRESS.name()).isPresent()) {
            throw new InterviewException(
                    "You already have an interview in progress. Please end it before starting a new one.");
        }

        String type = normalizeType(request.getInterviewType());
        String difficulty = normalizeDifficulty(request.getDifficulty());
        int totalQuestions = request.getTotalQuestions();

        ResumeData resumeData = null;
        if (InterviewType.RESUME_BASED.name().equals(type)) {
            resumeData = loadLatestResumeData(userId);
        }

        Interview interview = Interview.builder()
                .user(profile.getUser())
                .interviewType(type)
                .difficulty(difficulty)
                .totalQuestions(totalQuestions)
                .status(InterviewStatus.IN_PROGRESS.name())
                .build();
        interview = interviewRepository.save(interview);

        InterviewSession session = interviewSessionService.createSession(
                interview.getId(), userId, type, difficulty, totalQuestions, resumeData, request.getCsTopic());

        String questionText = generateQuestionText(session, true);
        int questionNumber = 1;
        session.setCurrentQuestionNumber(questionNumber);
        session.getHistory().add(QAPair.builder()
                .questionNumber(questionNumber)
                .question(questionText)
                .build());

        String audioUrl = synthesizeInto(session, questionText, questionNumber);
        interviewSessionService.updateSession(session.getSessionId(), session);

        log.info("Started interview {} (session {}) for userId={}, type={}, difficulty={}",
                interview.getId(), session.getSessionId(), userId, type, difficulty);

        return InterviewResponseDto.builder()
                .interviewId(interview.getId())
                .sessionId(session.getSessionId())
                .interviewType(type)
                .difficulty(difficulty)
                .totalQuestions(totalQuestions)
                .currentQuestionNumber(questionNumber)
                .question(QuestionDto.builder().text(questionText).questionNumber(questionNumber).build())
                .ttsAudioUrl(audioUrl)
                .build();
    }

    @Override
    public InterviewAnswerResponse processAnswer(Long userId, Long interviewId, byte[] audioData, String fileName) {
        InterviewSession session = interviewSessionService.getByInterviewId(interviewId);
        verifyOwnership(session, userId);

        if (session.getHistory().isEmpty()) {
            throw new InterviewException("No active question to answer for this interview.");
        }
        QAPair pending = session.getHistory().get(session.getHistory().size() - 1);
        int answeredNumber = pending.getQuestionNumber();

        // Step 1: transcribe the spoken answer.
        String transcript = speechToTextService.transcribe(audioData, fileName);

        // Step 2: evaluate the answer against the current question.
        EvaluationResult evaluation = answerEvaluationService.evaluateAnswer(
                pending.getQuestion(), transcript, session.getInterviewType(), session.getDifficulty());

        // Step 3: record the answer and evaluation on the pending Q&A pair.
        pending.setAnswer(transcript);
        pending.setEvaluation(evaluation);

        boolean isLastQuestion = answeredNumber >= session.getTotalQuestions();

        QuestionDto nextQuestion = null;
        String audioUrl = null;
        if (!isLastQuestion) {
            // Step 4: generate the next question (independent for EASY, follow-up for MEDIUM).
            String nextText = generateQuestionText(session, false);
            int nextNumber = answeredNumber + 1;
            session.setCurrentQuestionNumber(nextNumber);
            session.getHistory().add(QAPair.builder()
                    .questionNumber(nextNumber)
                    .question(nextText)
                    .build());
            // Step 5: generate TTS for the next question.
            audioUrl = synthesizeInto(session, nextText, nextNumber);
            nextQuestion = QuestionDto.builder().text(nextText).questionNumber(nextNumber).build();
        } else {
            session.setCurrentQuestionAudio(null);
        }

        interviewSessionService.updateSession(session.getSessionId(), session);

        return InterviewAnswerResponse.builder()
                .questionNumber(answeredNumber)
                .transcribedAnswer(transcript)
                .evaluation(evaluation)
                .nextQuestion(nextQuestion)
                .ttsAudioUrl(audioUrl)
                .lastQuestion(isLastQuestion)
                .build();
    }

    @Override
    public InterviewSummaryDto endInterview(Long userId, Long interviewId) {
        InterviewSession session = interviewSessionService.getByInterviewId(interviewId);
        verifyOwnership(session, userId);

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", interviewId));

        List<QAPair> answered = new ArrayList<>();
        for (QAPair qa : session.getHistory()) {
            if (qa.getEvaluation() != null) {
                answered.add(qa);
            }
        }

        double overallScore = averageOverall(answered);
        double confidenceScore = averageConfidence(answered);

        List<EvaluationResult> evaluations = answered.stream().map(QAPair::getEvaluation).toList();
        List<String> suggestions = answerEvaluationService.generateImprovementSuggestions(evaluations);

        int durationSeconds = (int) Duration.between(session.getStartedAt(), LocalDateTime.now()).getSeconds();

        interview.setStatus(InterviewStatus.COMPLETED.name());
        interview.setOverallScore(BigDecimal.valueOf(overallScore).setScale(2, RoundingMode.HALF_UP));
        interview.setConfidenceScore(BigDecimal.valueOf(confidenceScore).setScale(2, RoundingMode.HALF_UP));
        interview.setDurationSeconds(durationSeconds);
        interview.setImprovementSuggestions(toJson(suggestions));
        interview = interviewRepository.save(interview);

        enforceHistoryLimit(userId);
        interviewSessionService.destroySession(session.getSessionId());

        log.info("Ended interview {} for userId={} (overall={}, confidence={}, {} answered)",
                interviewId, userId, overallScore, confidenceScore, answered.size());

        InterviewSummaryDto summary = interviewMapper.toSummaryDto(interview);
        summary.setImprovementSuggestions(suggestions);
        summary.setQuestionResults(buildQuestionResults(answered));
        return summary;
    }

    // ----------------------------------------------------------------------------------------
    // Question generation
    // ----------------------------------------------------------------------------------------

    /**
     * Produces the next question text following the Section 15 logic:
     * <ul>
     *     <li>HR: from the question bank, except MEDIUM follow-ups which are LLM-generated.</li>
     *     <li>RESUME_BASED / CS_FUNDAMENTALS: LLM-generated (with history context for MEDIUM
     *     follow-ups, independent otherwise).</li>
     * </ul>
     */
    private String generateQuestionText(InterviewSession session, boolean isFirst) {
        String type = session.getInterviewType();
        boolean medium = Difficulty.MEDIUM.name().equals(session.getDifficulty());

        if (InterviewType.HR.name().equals(type)) {
            if (medium && !isFirst) {
                return interviewQuestionService.generateQuestion(
                        type, session.getDifficulty(), null, session.getHistory(), null);
            }
            return pickHrQuestion(session);
        }

        // RESUME_BASED or CS_FUNDAMENTALS
        List<QAPair> context = (medium && !isFirst) ? session.getHistory() : List.of();
        return interviewQuestionService.generateQuestion(
                type, session.getDifficulty(), session.getCsTopic(), context, session.getResumeData());
    }

    private String pickHrQuestion(InterviewSession session) {
        HrQuestion question = questionBankService.getRandomQuestion(
                session.getDifficulty(), session.getAskedQuestionIds());
        if (question == null && Difficulty.MEDIUM.name().equals(session.getDifficulty())) {
            // Medium interviews may pull an Easy bank question for the opener (Section 15).
            question = questionBankService.getRandomQuestion(
                    Difficulty.EASY.name(), session.getAskedQuestionIds());
        }
        if (question == null) {
            throw new InterviewException("No HR questions are available. Please seed the question bank.");
        }
        session.getAskedQuestionIds().add(question.getId());
        return question.getQuestionText();
    }

    /**
     * Generates TTS for a question, stores the audio on the session, and returns the audio URL
     * (or null when TTS is unavailable, so the frontend falls back to browser speech).
     */
    private String synthesizeInto(InterviewSession session, String text, int questionNumber) {
        byte[] audio = textToSpeechService.synthesize(text);
        session.setCurrentQuestionAudio(audio);
        if (audio == null || audio.length == 0) {
            return null;
        }
        return "/api/interviews/audio/" + session.getSessionId() + "/q" + questionNumber;
    }

    // ----------------------------------------------------------------------------------------
    // Scoring & summary helpers
    // ----------------------------------------------------------------------------------------

    private double averageOverall(List<QAPair> answered) {
        return answered.stream()
                .mapToDouble(qa -> qa.getEvaluation().getOverallScore())
                .average().orElse(0.0);
    }

    private double averageConfidence(List<QAPair> answered) {
        return answered.stream()
                .mapToDouble(qa -> qa.getEvaluation().getConfidenceScore())
                .average().orElse(0.0);
    }

    private List<QuestionResultDto> buildQuestionResults(List<QAPair> answered) {
        List<QuestionResultDto> results = new ArrayList<>();
        for (QAPair qa : answered) {
            results.add(QuestionResultDto.builder()
                    .questionNumber(qa.getQuestionNumber())
                    .questionText(qa.getQuestion())
                    .overallScore(qa.getEvaluation().getOverallScore())
                    .confidenceScore(qa.getEvaluation().getConfidenceScore())
                    .build());
        }
        return results;
    }

    /**
     * Enforces the "latest 5 completed interviews" rule (Section 9): deletes the oldest
     * completed interviews beyond the most recent five.
     */
    private void enforceHistoryLimit(Long userId) {
        List<Interview> completed = interviewRepository
                .findByUserIdAndStatusOrderByCreatedAtAsc(userId, InterviewStatus.COMPLETED.name());
        int excess = completed.size() - AppConstants.MAX_INTERVIEW_HISTORY;
        if (excess > 0) {
            List<Interview> toDelete = completed.subList(0, excess);
            interviewRepository.deleteAll(toDelete);
            log.info("Enforced interview history limit: deleted {} oldest completed interview(s) for userId={}",
                    excess, userId);
        }
    }

    // ----------------------------------------------------------------------------------------
    // Validation & data helpers
    // ----------------------------------------------------------------------------------------

    private void verifyOwnership(InterviewSession session, Long userId) {
        if (!session.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to access this interview.");
        }
    }

    private String normalizeType(String type) {
        if (type == null) {
            throw new BadRequestException("interviewType is required.");
        }
        String normalized = type.trim().toUpperCase();
        for (InterviewType t : InterviewType.values()) {
            if (t.name().equals(normalized)) {
                return normalized;
            }
        }
        throw new BadRequestException("Invalid interview type: " + type);
    }

    private String normalizeDifficulty(String difficulty) {
        if (difficulty == null) {
            throw new BadRequestException("difficulty is required.");
        }
        String normalized = difficulty.trim().toUpperCase();
        for (Difficulty d : Difficulty.values()) {
            if (d.name().equals(normalized)) {
                return normalized;
            }
        }
        throw new BadRequestException("Invalid difficulty: " + difficulty);
    }

    private ResumeData loadLatestResumeData(Long userId) {
        Resume resume = resumeRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new BadRequestException("Please upload a resume first."));
        String parsed = resume.getParsedData();
        if (parsed == null || parsed.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(parsed, ResumeData.class);
        } catch (Exception ex) {
            log.warn("Could not parse stored resume data for userId={}; proceeding without it", userId, ex);
            return null;
        }
    }

    private String toJson(List<String> suggestions) {
        try {
            return objectMapper.writeValueAsString(suggestions == null ? new ArrayList<>() : suggestions);
        } catch (Exception ex) {
            log.error("Failed to serialize improvement suggestions to JSON", ex);
            return null;
        }
    }
}
