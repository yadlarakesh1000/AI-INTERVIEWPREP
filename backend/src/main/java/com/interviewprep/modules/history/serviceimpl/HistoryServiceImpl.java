package com.interviewprep.modules.history.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.interviewprep.modules.history.dto.InterviewHistoryDto;
import com.interviewprep.modules.history.service.HistoryService;
import com.interviewprep.modules.interview.entity.Interview;
import com.interviewprep.modules.interview.entity.InterviewStatus;
import com.interviewprep.modules.interview.repository.InterviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final InterviewRepository interviewRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InterviewHistoryDto> getHistory(Long userId) {
        // Newest first. Only the latest 5 completed interviews are retained per user
        // (Section 9), so this is the complete history.
        List<Interview> interviews = interviewRepository
                .findTop5ByUserIdAndStatusOrderByCreatedAtDesc(userId, InterviewStatus.COMPLETED.name());

        return interviews.stream()
                .map(this::toDto)
                .toList();
    }

    private InterviewHistoryDto toDto(Interview interview) {
        return InterviewHistoryDto.builder()
                .interviewId(interview.getId())
                .interviewType(interview.getInterviewType())
                .difficulty(interview.getDifficulty())
                .date(interview.getCreatedAt() == null ? null : interview.getCreatedAt().toLocalDate())
                .durationSeconds(interview.getDurationSeconds())
                .overallScore(interview.getOverallScore() == null ? 0.0 : interview.getOverallScore().doubleValue())
                .build();
    }
}
