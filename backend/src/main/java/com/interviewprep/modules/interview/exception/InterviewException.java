package com.interviewprep.modules.interview.exception;

/**
 * Thrown for interview-specific business rule violations (e.g. an interview already in
 * progress, no resume for a resume-based interview). Handled by the global exception handler.
 */
public class InterviewException extends RuntimeException {

    public InterviewException(String message) {
        super(message);
    }

    public InterviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
