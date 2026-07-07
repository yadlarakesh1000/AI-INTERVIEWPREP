package com.interviewprep.modules.questionbank.service;

import java.util.List;

import com.interviewprep.modules.questionbank.entity.HrQuestion;

/**
 * Serves questions from the seeded HR question bank. Used by the HR interview flow to pull
 * random questions without repeating within a session.
 */
public interface QuestionBankService {

    /**
     * Returns a random HR question of the given difficulty, avoiding any already-asked ids.
     *
     * @param difficulty EASY or MEDIUM
     * @param excludeIds ids already asked in the current session (may be null/empty)
     * @return a random matching question, or null if the bank has none for that difficulty
     */
    HrQuestion getRandomQuestion(String difficulty, List<Long> excludeIds);

    List<HrQuestion> getQuestionsByCategory(String category, String difficulty);
}
