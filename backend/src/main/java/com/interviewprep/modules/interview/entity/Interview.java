package com.interviewprep.modules.interview.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.interviewprep.modules.auth.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A completed (or in-progress) interview record (Section 7). Only the summary is persisted —
 * no transcripts. Interview session state (the Q&A history) lives in memory only. At most the
 * latest 5 COMPLETED interviews are kept per user.
 *
 * <p>{@code interviewType}, {@code difficulty} and {@code status} are stored as their String
 * names in ENUM columns. The canonical value sets are defined by {@link InterviewType},
 * {@link Difficulty} and {@link InterviewStatus}. String fields keep the whole interview flow
 * (session model, AI services, repository queries) consistently String-based.
 */
@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "interview_type", nullable = false,
            columnDefinition = "ENUM('HR','RESUME_BASED','CS_FUNDAMENTALS')")
    private String interviewType;

    @Column(name = "difficulty", nullable = false, columnDefinition = "ENUM('EASY','MEDIUM')")
    private String difficulty;

    @Column(name = "overall_score", precision = 4, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "confidence_score", precision = 4, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    // Stores the LLM-generated improvement suggestions as a JSON string (List<String>
    // serialized via Jackson in the service layer).
    @Column(name = "improvement_suggestions", columnDefinition = "TEXT")
    private String improvementSuggestions;

    @Column(name = "status", nullable = false,
            columnDefinition = "ENUM('IN_PROGRESS','COMPLETED','ABANDONED') DEFAULT 'IN_PROGRESS'")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
