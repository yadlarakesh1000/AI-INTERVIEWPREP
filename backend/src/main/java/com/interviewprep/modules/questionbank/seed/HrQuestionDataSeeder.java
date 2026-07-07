package com.interviewprep.modules.questionbank.seed;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.interviewprep.modules.questionbank.entity.HrQuestion;
import com.interviewprep.modules.questionbank.repository.HrQuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seeds the HR question bank (Section 7) with a curated set of commonly-asked HR /
 * communication interview questions across the five categories. Runs once on startup and
 * only if the table is empty, so it is safe to leave enabled across restarts.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HrQuestionDataSeeder implements CommandLineRunner {

    private static final String EASY = "EASY";
    private static final String MEDIUM = "MEDIUM";

    private final HrQuestionRepository hrQuestionRepository;

    @Override
    public void run(String... args) {
        if (hrQuestionRepository.count() > 0) {
            log.debug("HR question bank already seeded ({} questions); skipping", hrQuestionRepository.count());
            return;
        }

        List<HrQuestion> questions = new ArrayList<>();
        seedIntroduction(questions);
        seedStrengthsWeaknesses(questions);
        seedBehavioral(questions);
        seedSituational(questions);
        seedMotivation(questions);

        hrQuestionRepository.saveAll(questions);
        log.info("Seeded HR question bank with {} questions", questions.size());
    }

    private void add(List<HrQuestion> list, String category, String difficulty, String text) {
        list.add(HrQuestion.builder()
                .category(category)
                .difficulty(difficulty)
                .questionText(text)
                .build());
    }

    private void seedIntroduction(List<HrQuestion> q) {
        String c = "introduction";
        add(q, c, EASY, "Tell me about yourself.");
        add(q, c, EASY, "Walk me through your resume.");
        add(q, c, EASY, "How would your friends or colleagues describe you?");
        add(q, c, EASY, "What are your hobbies and interests outside of work?");
        add(q, c, EASY, "What made you choose your field of study or career?");
        add(q, c, EASY, "How did you hear about this role?");
        add(q, c, MEDIUM, "Can you summarize your professional background in under two minutes?");
        add(q, c, MEDIUM, "Tell me about a project or accomplishment you are most proud of.");
        add(q, c, MEDIUM, "What do you know about our company, and what interests you about it?");
    }

    private void seedStrengthsWeaknesses(List<HrQuestion> q) {
        String c = "strengths_weaknesses";
        add(q, c, EASY, "What are your greatest strengths?");
        add(q, c, EASY, "What is your biggest weakness?");
        add(q, c, EASY, "What skill are you most confident in?");
        add(q, c, EASY, "What area are you currently trying to develop?");
        add(q, c, EASY, "How do you handle feedback about your weaknesses?");
        add(q, c, MEDIUM, "Describe a weakness you have actively worked to improve, and how.");
        add(q, c, MEDIUM, "Which of your strengths would be most valuable in this role, and why?");
        add(q, c, MEDIUM, "Tell me about a time your greatest strength helped you succeed.");
        add(q, c, MEDIUM, "If we asked your manager about your biggest area for growth, what would they say?");
    }

    private void seedBehavioral(List<HrQuestion> q) {
        String c = "behavioral";
        add(q, c, EASY, "Tell me about a time you helped a teammate.");
        add(q, c, MEDIUM, "Describe a significant challenge you faced and how you overcame it.");
        add(q, c, MEDIUM, "Tell me about a time you worked effectively as part of a team.");
        add(q, c, MEDIUM, "Give an example of a time you failed and what you learned from it.");
        add(q, c, MEDIUM, "Describe a situation where you had to meet a tight deadline.");
        add(q, c, MEDIUM, "Describe a time you had to learn a new skill or technology quickly.");
        add(q, c, MEDIUM, "Tell me about a time you disagreed with a decision and how you handled it.");
        add(q, c, MEDIUM, "Describe a time you took initiative without being asked.");
        add(q, c, MEDIUM, "Tell me about a time you received difficult feedback and how you responded.");
    }

    private void seedSituational(List<HrQuestion> q) {
        String c = "situational";
        add(q, c, EASY, "How would you handle working with a difficult team member?");
        add(q, c, EASY, "How would you respond if a customer or stakeholder was unhappy with your work?");
        add(q, c, MEDIUM, "How would you handle a disagreement with a coworker?");
        add(q, c, MEDIUM, "What would you do if you were assigned a task with unclear requirements?");
        add(q, c, MEDIUM, "How would you prioritize multiple projects with competing deadlines?");
        add(q, c, MEDIUM, "If you noticed a mistake in a project after it shipped, what would you do?");
        add(q, c, MEDIUM, "What would you do if you disagreed with your manager's approach?");
        add(q, c, MEDIUM, "How would you handle being given more work than you can realistically complete?");
        add(q, c, MEDIUM, "If a teammate was not contributing their share, how would you address it?");
    }

    private void seedMotivation(List<HrQuestion> q) {
        String c = "motivation";
        add(q, c, EASY, "Why should we hire you?");
        add(q, c, EASY, "Why do you want to work here?");
        add(q, c, EASY, "What motivates you to do your best work?");
        add(q, c, EASY, "What kind of work environment helps you thrive?");
        add(q, c, MEDIUM, "Where do you see yourself in five years?");
        add(q, c, MEDIUM, "What are you looking for in your next role that you don't have today?");
        add(q, c, MEDIUM, "What are your long-term career goals?");
    }
}
