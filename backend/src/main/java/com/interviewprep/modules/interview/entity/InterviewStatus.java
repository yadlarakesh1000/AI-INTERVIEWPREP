package com.interviewprep.modules.interview.entity;

/**
 * Lifecycle status of an interview (Section 7). All three states are defined:
 * an interview starts IN_PROGRESS, becomes COMPLETED when finished, or ABANDONED if the
 * user drops it. Persisted as its String name in the {@code status} ENUM column.
 */
public enum InterviewStatus {
    IN_PROGRESS,
    COMPLETED,
    ABANDONED
}
