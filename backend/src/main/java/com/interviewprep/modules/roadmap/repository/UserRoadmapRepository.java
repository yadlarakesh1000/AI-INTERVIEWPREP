package com.interviewprep.modules.roadmap.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.modules.roadmap.entity.UserRoadmap;

@Repository
public interface UserRoadmapRepository extends JpaRepository<UserRoadmap, Long> {

    Optional<UserRoadmap> findByUserIdAndIsActiveTrue(Long userId);
}
