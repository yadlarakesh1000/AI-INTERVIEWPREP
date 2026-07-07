package com.interviewprep.modules.interview.entity;

/**
 * The three supported interview types (Section 7). Defined as the canonical vocabulary for
 * interview types; persisted as its String name in the {@code interview_type} ENUM column.
 */
public enum InterviewType {
    HR,
    RESUME_BASED,
    CS_FUNDAMENTALS
}
