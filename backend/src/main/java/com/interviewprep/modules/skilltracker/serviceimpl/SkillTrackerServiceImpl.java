package com.interviewprep.modules.skilltracker.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.exception.BadRequestException;
import com.interviewprep.modules.roadmap.entity.RoadmapTemplate;
import com.interviewprep.modules.roadmap.entity.UserRoadmap;
import com.interviewprep.modules.roadmap.repository.UserRoadmapRepository;
import com.interviewprep.modules.skilltracker.dto.DayToggleRequest;
import com.interviewprep.modules.skilltracker.dto.SkillTrackerOverviewDto;
import com.interviewprep.modules.skilltracker.dto.WeekProgressDto;
import com.interviewprep.modules.skilltracker.dto.WeekProgressDto.DayDto;
import com.interviewprep.modules.skilltracker.entity.DayProgress;
import com.interviewprep.modules.skilltracker.mapper.SkillTrackerMapper;
import com.interviewprep.modules.skilltracker.repository.DayProgressRepository;
import com.interviewprep.modules.skilltracker.service.SkillTrackerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillTrackerServiceImpl implements SkillTrackerService {

    private static final int DAYS_PER_WEEK = 7;

    private final UserRoadmapRepository userRoadmapRepository;
    private final DayProgressRepository dayProgressRepository;
    private final SkillTrackerMapper skillTrackerMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public WeekProgressDto getWeekProgress(Long userId, Integer weekNumber) {
        UserRoadmap roadmap = getActiveRoadmap(userId);
        RoadmapTemplate template = roadmap.getRoadmapTemplate();
        JsonNode week = weekNode(template, weekNumber);

        String weekTitle = week.path("title").asText("");
        Map<Integer, String> dayTopics = dayTopics(week);

        List<DayProgress> progress = ensureWeekInitialized(roadmap, weekNumber);

        List<DayDto> days = new ArrayList<>();
        long completed = 0;
        for (DayProgress dp : progress) {
            DayDto day = skillTrackerMapper.toDayDto(dp);
            day.setTopic(dayTopics.get(dp.getDayNumber()));
            days.add(day);
            if (Boolean.TRUE.equals(dp.getIsCompleted())) {
                completed++;
            }
        }

        double progressPercent = round2((double) completed / DAYS_PER_WEEK * 100);

        return WeekProgressDto.builder()
                .weekNumber(weekNumber)
                .weekTitle(weekTitle)
                .days(days)
                .progressPercent(progressPercent)
                .build();
    }

    @Override
    @Transactional
    public DayDto toggleDay(Long userId, DayToggleRequest request) {
        UserRoadmap roadmap = getActiveRoadmap(userId);
        JsonNode week = weekNode(roadmap.getRoadmapTemplate(), request.getWeekNumber());
        Map<Integer, String> dayTopics = dayTopics(week);

        List<DayProgress> progress = ensureWeekInitialized(roadmap, request.getWeekNumber());
        DayProgress day = progress.stream()
                .filter(dp -> dp.getDayNumber().equals(request.getDayNumber()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "Invalid day number: " + request.getDayNumber()));

        boolean nowCompleted = !Boolean.TRUE.equals(day.getIsCompleted());
        day.setIsCompleted(nowCompleted);
        day.setCompletedAt(nowCompleted ? LocalDateTime.now() : null);
        DayProgress saved = dayProgressRepository.save(day);

        log.info("Toggled day (userRoadmap={}, week={}, day={}) -> completed={}",
                roadmap.getId(), request.getWeekNumber(), request.getDayNumber(), nowCompleted);

        DayDto dto = skillTrackerMapper.toDayDto(saved);
        dto.setTopic(dayTopics.get(saved.getDayNumber()));
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public SkillTrackerOverviewDto getOverview(Long userId) {
        UserRoadmap roadmap = getActiveRoadmap(userId);
        int totalWeeks = roadmap.getRoadmapTemplate().getDurationWeeks();
        int totalDays = totalWeeks * DAYS_PER_WEEK;

        long completedDays = dayProgressRepository.countByUserRoadmapIdAndIsCompletedTrue(roadmap.getId());
        double progressPercent = totalDays == 0 ? 0.0 : round2((double) completedDays / totalDays * 100);
        int currentWeek = (int) Math.min(totalWeeks, completedDays / DAYS_PER_WEEK + 1);

        return SkillTrackerOverviewDto.builder()
                .totalWeeks(totalWeeks)
                .totalDays(totalDays)
                .completedDays(completedDays)
                .progressPercent(progressPercent)
                .currentWeek(Math.max(1, currentWeek))
                .build();
    }

    // ----------------------------------------------------------------------------------------

    private UserRoadmap getActiveRoadmap(Long userId) {
        return userRoadmapRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new BadRequestException(
                        "Select a roadmap first to use the skill tracker."));
    }

    /**
     * Lazily creates the seven day-progress records for a week the first time it is accessed
     * (Section 9). Returns the week's records sorted by day number.
     */
    private List<DayProgress> ensureWeekInitialized(UserRoadmap roadmap, int weekNumber) {
        List<DayProgress> existing = dayProgressRepository
                .findByUserRoadmapIdAndWeekNumber(roadmap.getId(), weekNumber);
        if (existing.isEmpty()) {
            List<DayProgress> created = new ArrayList<>();
            for (int day = 1; day <= DAYS_PER_WEEK; day++) {
                created.add(DayProgress.builder()
                        .user(roadmap.getUser())
                        .userRoadmap(roadmap)
                        .weekNumber(weekNumber)
                        .dayNumber(day)
                        .isCompleted(false)
                        .build());
            }
            existing = dayProgressRepository.saveAll(created);
            log.info("Initialized day progress for userRoadmap {} week {}", roadmap.getId(), weekNumber);
        }
        existing.sort(Comparator.comparingInt(DayProgress::getDayNumber));
        return existing;
    }

    private JsonNode weekNode(RoadmapTemplate template, int weekNumber) {
        JsonNode plan;
        try {
            plan = objectMapper.readTree(template.getWeeklyPlan());
        } catch (Exception ex) {
            throw new BadRequestException("Roadmap plan could not be read.");
        }
        if (weekNumber < 1 || weekNumber > plan.size()) {
            throw new BadRequestException("Invalid week number: " + weekNumber
                    + " (roadmap has " + plan.size() + " weeks).");
        }
        return plan.get(weekNumber - 1);
    }

    private Map<Integer, String> dayTopics(JsonNode week) {
        Map<Integer, String> topics = new LinkedHashMap<>();
        JsonNode days = week.path("days");
        if (days.isArray()) {
            days.forEach(d -> topics.put(d.path("day").asInt(), d.path("topic").asText("")));
        }
        return topics;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
