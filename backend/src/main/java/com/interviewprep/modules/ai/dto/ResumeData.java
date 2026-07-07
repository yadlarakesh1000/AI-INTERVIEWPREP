package com.interviewprep.modules.ai.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Structured resume data produced by the resume-parsing service, matching the
 * parsed-resume structure in Section 8.3 of the specification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeData {

    private List<String> skills;
    private List<ProjectInfo> projects;
    private List<ExperienceInfo> experience;
    private String education;

    /**
     * A single project entry extracted from the resume.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectInfo {
        private String name;
        private String description;
        private List<String> technologies;
    }

    /**
     * A single work-experience entry extracted from the resume.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExperienceInfo {
        private String role;
        private String company;
        private String duration;
        private String description;
    }
}
