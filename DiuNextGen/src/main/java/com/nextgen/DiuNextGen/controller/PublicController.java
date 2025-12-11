package com.nextgen.DiuNextGen.controller;

import com.nextgen.DiuNextGen.dto.LeaderboardEntry; // Ensure this is DTO
import com.nextgen.DiuNextGen.dto.TopicPerformanceResponse;
import com.nextgen.DiuNextGen.entity.CalendarEvent;
import com.nextgen.DiuNextGen.entity.ContestHistory;
import com.nextgen.DiuNextGen.entity.Round;
import com.nextgen.DiuNextGen.entity.Topic;
import com.nextgen.DiuNextGen.entity.User;
import com.nextgen.DiuNextGen.repository.CalendarRepository;
import com.nextgen.DiuNextGen.repository.ContestHistoryRepository;
import com.nextgen.DiuNextGen.repository.RoundRepository;
import com.nextgen.DiuNextGen.repository.TopicRepository;
import com.nextgen.DiuNextGen.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*") // The Shield Down command
public class PublicController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private ContestHistoryRepository contestHistoryRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CalendarRepository calendarRepository; // <--- THE MISSING LINK 1

    // 1. Leaderboard
    @GetMapping("/leaderboard/{batch}")
    public List<LeaderboardEntry> getLeaderboard(@PathVariable String batch) {
        // NOTE: Ensure LeaderboardEntry is the DTO class, not Entity if you have both
        List<User> users = userRepository.findByBatchOrderByTotalSolvedDesc(batch);

        return users.stream()
                .map(user -> new LeaderboardEntry(
                        user.getName(),
                        user.getVjudgeHandle(),
                        user.getBatch(),
                        user.getTotalSolved(),
                        user.getPhotoUrl(),
                        user.getStatus()
                ))
                .collect(Collectors.toList());
    }

    // 2. Topics List
    @GetMapping("/topics")
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    // 3. User Performance
    @GetMapping("/performance/{handle}/{topicId}")
    public List<TopicPerformanceResponse> getUserTopicPerformance(
            @PathVariable String handle,
            @PathVariable Long topicId) {

        User user = userRepository.findByVjudgeHandle(handle);
        if (user == null) throw new RuntimeException("User not found");

        List<Round> rounds = roundRepository.findByTopicId(topicId);
        List<TopicPerformanceResponse> response = new ArrayList<>();

        for (Round round : rounds) {
            Integer solved = contestHistoryRepository.findByUserAndRound(user, round)
                    .map(ContestHistory::getSolvedCount)
                    .orElse(0);
            response.add(new TopicPerformanceResponse(round.getName(), solved));
        }
        return response;
    }

    // 4. CALENDAR ENDPOINT (This was missing!)
    @GetMapping("/calendar")
    public List<CalendarEvent> getCalendar() {
        return calendarRepository.findAll();
    }

    // 5. ROUNDS FOR LIBRARY (This was also missing!)
    @GetMapping("/rounds/{topicId}")
    public List<Round> getRoundsByTopic(@PathVariable Long topicId) {
        return roundRepository.findByTopicId(topicId);
    }
}