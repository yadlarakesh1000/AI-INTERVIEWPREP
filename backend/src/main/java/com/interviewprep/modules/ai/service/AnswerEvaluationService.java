package com.interviewprep.modules.ai.service;

import java.util.List;

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

    /**
     * Generates 3-5 specific, actionable improvement suggestions for the candidate by sending
     * all of an interview's evaluation results to the LLM (Section 15). Returns an empty list
     * if there are no evaluations, and degrades gracefully to feedback-derived suggestions if
     * the LLM call fails.
     *
     * @param evaluations the per-question evaluation results from the interview
     * @return a list of improvement suggestion strings
     */
    List<String> generateImprovementSuggestions(List<EvaluationResult> evaluations);
}
