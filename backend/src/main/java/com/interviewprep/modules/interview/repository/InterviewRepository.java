package com.interviewprep.modules.interview.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.modules.interview.entity.Interview;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findTop5ByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);

    long countByUserIdAndStatus(Long userId, String status);

    // Oldest first — used to enforce the "latest 5 completed interviews" limit.
    List<Interview> findByUserIdAndStatusOrderByCreatedAtAsc(Long userId, String status);

    // Used to detect an existing IN_PROGRESS interview for a user.
    Optional<Interview> findByUserIdAndStatus(Long userId, String status);
}
