package com.interviewprep.modules.roadmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.modules.roadmap.entity.RoadmapTemplate;

@Repository
public interface RoadmapTemplateRepository extends JpaRepository<RoadmapTemplate, Long> {

    List<RoadmapTemplate> findByCareerPath(String careerPath);
}
