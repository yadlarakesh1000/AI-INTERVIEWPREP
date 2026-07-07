package com.interviewprep.modules.resume.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.modules.resume.entity.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Optional<Resume> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
