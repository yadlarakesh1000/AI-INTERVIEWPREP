package com.interviewprep.modules.questionbank.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A seeded HR / communication interview question (Section 7). Used as the question bank for
 * HR interviews — Easy interviews pull questions directly from here; Medium interviews use
 * the first question from here and then generate LLM follow-ups.
 */
@Entity
@Table(name = "hr_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "category", length = 100)
    private String category;

    // Stored as one of EASY / MEDIUM. Kept as a String (not a Java enum) so the repository
    // and service can query by difficulty using plain String values.
    @Column(name = "difficulty", nullable = false, columnDefinition = "ENUM('EASY','MEDIUM')")
    private String difficulty;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
