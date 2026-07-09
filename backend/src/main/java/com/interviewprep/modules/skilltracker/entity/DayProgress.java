package com.interviewprep.modules.skilltracker.entity;

import java.time.LocalDateTime;

import com.interviewprep.modules.auth.entity.User;
import com.interviewprep.modules.roadmap.entity.UserRoadmap;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Per-day completion state for a user's active roadmap (Section 7). Records are created
 * lazily when a user first views a week. The day's topic is NOT stored here — it comes from
 * the roadmap template's weekly plan.
 */
@Entity
@Table(name = "day_progress", uniqueConstraints = @UniqueConstraint(
        name = "unique_day", columnNames = {"user_roadmap_id", "week_number", "day_number"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DayProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_roadmap_id", nullable = false)
    private UserRoadmap userRoadmap;

    @Column(name = "week_number", nullable = false)
    private Integer weekNumber;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
