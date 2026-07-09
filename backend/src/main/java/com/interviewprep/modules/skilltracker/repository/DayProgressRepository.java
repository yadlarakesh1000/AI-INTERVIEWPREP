package com.interviewprep.modules.skilltracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.modules.skilltracker.entity.DayProgress;

@Repository
public interface DayProgressRepository extends JpaRepository<DayProgress, Long> {

    List<DayProgress> findByUserRoadmapIdAndWeekNumber(Long userRoadmapId, Integer weekNumber);

    long countByUserRoadmapIdAndIsCompletedTrue(Long userRoadmapId);
}
