package com.interviewprep.modules.roadmap.entity;

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
 * A handcrafted, seeded career roadmap template (Section 7). Templates are NOT
 * user-generated — they are curated data browsed and selected by users.
 *
 * <p>The JSON-typed columns hold serialized structures:
 * <ul>
 *     <li>{@code weeklyPlan} — array of week objects, each with 7 day objects.</li>
 *     <li>{@code milestones} — array of milestone objects.</li>
 *     <li>{@code recommendedProjects} — array of project objects.</li>
 *     <li>{@code learningResources} — array of {name, url, type} objects.</li>
 * </ul>
 */
@Entity
@Table(name = "roadmap_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadmapTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "career_path", nullable = false, length = 200)
    private String careerPath;

    @Column(name = "duration_weeks", nullable = false)
    private Integer durationWeeks;

    @Column(name = "weekly_plan", nullable = false, columnDefinition = "JSON")
    private String weeklyPlan;

    @Column(name = "milestones", columnDefinition = "JSON")
    private String milestones;

    @Column(name = "recommended_projects", columnDefinition = "JSON")
    private String recommendedProjects;

    @Column(name = "learning_resources", columnDefinition = "JSON")
    private String learningResources;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
