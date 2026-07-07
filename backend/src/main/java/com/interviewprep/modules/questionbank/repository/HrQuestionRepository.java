package com.interviewprep.modules.questionbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.interviewprep.modules.questionbank.entity.HrQuestion;

@Repository
public interface HrQuestionRepository extends JpaRepository<HrQuestion, Long> {

    List<HrQuestion> findByDifficulty(String difficulty);

    List<HrQuestion> findByCategoryAndDifficulty(String category, String difficulty);

    long countByDifficulty(String difficulty);
}
