package com.interviewprep.modules.interviewsession.model;

import com.interviewprep.modules.ai.dto.EvaluationResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single question-answer pair within an in-memory interview session, as described
 * in Section 15 of the specification. The list of these pairs forms the context that
 * is fed to the LLM when generating follow-up questions in Medium difficulty.
 *
 * <p>This is a plain POJO (NOT a JPA entity) — interview session state is held
 * in-memory only and is never persisted to the database.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QAPair {

    private int questionNumber;
    private String question;
    private String answer;
    private EvaluationResult evaluation;
}
