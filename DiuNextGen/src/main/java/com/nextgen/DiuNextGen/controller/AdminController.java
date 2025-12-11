package com.nextgen.DiuNextGen.controller;

import com.nextgen.DiuNextGen.dto.RoundRequest;
import com.nextgen.DiuNextGen.dto.TopicRequest;
import com.nextgen.DiuNextGen.entity.*;
import com.nextgen.DiuNextGen.repository.RoundRepository;
import com.nextgen.DiuNextGen.repository.TopicRepository;
import com.nextgen.DiuNextGen.repository.UserRepository; // Import Repo
import com.nextgen.DiuNextGen.service.AdminService;
import com.nextgen.DiuNextGen.service.VJudgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

@CrossOrigin(origins = "*") // <--- THIS IS THE KEY LINE
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private VJudgeService vJudgeService;

    @Autowired
    private RoundRepository roundRepository;

    @Autowired
    private UserRepository userRepository; // Ensure this is wired

    @PostMapping("/topic")
    public Topic createTopic(@RequestBody TopicRequest request) {
        return adminService.createTopic(request);
    }

    @PostMapping("/round")
    public Round createRound(@RequestBody RoundRequest request) {
        return adminService.createRound(request);
    }

    @PostMapping("/refresh-round/{roundId}")
    public String refreshRound(@PathVariable Long roundId) {
        Round round = roundRepository.findById(roundId)
                .orElseThrow(() -> new RuntimeException("Round not found"));
        return vJudgeService.processContestUpdates(round.getVjudgeContestId(), round);
    }

    // --- THE PROMOTE ENDPOINT ---
    @PostMapping("/promote/{handle}")
    public String promoteToAdmin(@PathVariable String handle) {
        System.out.println("Attempting to promote: " + handle); // Debug print

        User user = userRepository.findByVjudgeHandle(handle);
        if (user == null) {
            throw new RuntimeException("User not found: " + handle);
        }

        user.setRole("ADMIN");
        userRepository.save(user);

        return "Success";
    }
    @Autowired
    private com.nextgen.DiuNextGen.repository.ResourceRepository resourceRepository;

    @Autowired
    private com.nextgen.DiuNextGen.repository.CalendarRepository calendarRepository;

    @PostMapping("/calendar")
    public CalendarEvent addEvent(@RequestBody CalendarEvent event) {
        return calendarRepository.save(event);
    }
    @PostMapping("/resource")

    public Resource addResource(@RequestBody java.util.Map<String, String> payload) {
        Long roundId = Long.valueOf(payload.get("roundId"));
        Round round = roundRepository.findById(roundId).orElseThrow();

        Resource res = new Resource();
        res.setTitle(payload.get("title"));
        res.setUrl(payload.get("url"));
        res.setRound(round);

        return resourceRepository.save(res);
    }
}