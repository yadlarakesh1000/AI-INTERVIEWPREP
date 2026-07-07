package com.interviewprep.modules.ai.service;

import com.interviewprep.modules.ai.dto.EvaluationResult;

/**
 * Abstraction for evaluating a candidate's answer against the 4-dimension rubric
 * defined in Section 9 of the specification.
 *
 * <p>Phase 1 implementation: {@code GroqAnswerEvaluationServiceImpl} (STEP 016).
 */
public interface AnswerEvaluationService {

    /**
     * Evaluates a single answer to an interview question.
     *
     * @param question      the question that was asked
     * @param answer        the candidate's transcribed answer
     * @param interviewType one of HR, RESUME_BASED, CS_FUNDAMENTALS
     * @param difficulty    one of EASY, MEDIUM
     * @return the 4-dimension evaluation with derived overall and confidence scores
     */
    EvaluationResult evaluateAnswer(String question,
                                    String answer,
                                    String interviewType,
                                    String difficulty);
}
