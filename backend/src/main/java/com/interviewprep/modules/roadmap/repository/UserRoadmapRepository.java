package com.interviewprep.modules.roadmap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.modules.roadmap.entity.UserRoadmap;

@Repository
public interface UserRoadmapRepository extends JpaRepository<UserRoadmap, Long> {

    Optional<UserRoadmap> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Finds a user's earlier selections of the same roadmap so re-selecting it resumes that
     * progress instead of starting over. Returns a list rather than a single result because
     * data created before duplicate selections were prevented may contain more than one row.
     */
    List<UserRoadmap> findByUserIdAndRoadmapTemplateIdOrderByIdAsc(Long userId, Long roadmapTemplateId);
}
