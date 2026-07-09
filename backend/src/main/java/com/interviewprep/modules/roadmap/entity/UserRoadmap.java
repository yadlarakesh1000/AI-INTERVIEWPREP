package com.interviewprep.modules.roadmap.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
 * A user's selection of a roadmap template (Section 7). A user has at most ONE active
 * roadmap at a time — selecting a new one deactivates the previous.
 */
@Entity
@Table(name = "user_roadmaps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roadmap_template_id", nullable = false)
    private RoadmapTemplate roadmapTemplate;

    @Column(name = "selected_duration_weeks", nullable = false)
    private Integer selectedDurationWeeks;

    @CreationTimestamp
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
}
