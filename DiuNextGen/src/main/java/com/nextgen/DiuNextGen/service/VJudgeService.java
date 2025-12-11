package com.nextgen.DiuNextGen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextgen.DiuNextGen.entity.ContestHistory;
import com.nextgen.DiuNextGen.entity.Round;
import com.nextgen.DiuNextGen.entity.User;
import com.nextgen.DiuNextGen.repository.ContestHistoryRepository;
import com.nextgen.DiuNextGen.repository.UserRepository;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired; // <--- IMPORT THIS
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
@Service
public class VJudgeService {

    // --- FIX 1: ADD @AUTOWIRED HERE ---
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContestHistoryRepository contestHistoryRepository;
    // ----------------------------------

    public String getContestData(String contestId) {
        String url = "https://vjudge.net/contest/rank/single/" + contestId;
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .ignoreContentType(true)
                    .execute()
                    .body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String processContestUpdates(String contestId, Round round) {
        String json = getContestData(contestId);
        if (json == null) return "Failed to fetch data";

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            // Map VJudge ID -> Handle
            Map<String, String> idToHandleMap = new HashMap<>();
            JsonNode participants = root.get("participants");
            if (participants == null) return "No participants found";

            Iterator<String> fieldNames = participants.fieldNames();
            while (fieldNames.hasNext()) {
                String vId = fieldNames.next();
                String handle = participants.get(vId).get(0).asText();
                idToHandleMap.put(vId, handle);
            }

            // Map Handle -> Set of Solved Problem Indexes
            Map<String, Set<Integer>> userSolves = new HashMap<>();
            JsonNode submissions = root.get("submissions");

            if (submissions != null) {
                for (JsonNode sub : submissions) {
                    String vId = sub.get(0).asText();
                    int problemIdx = sub.get(1).asInt();
                    int status = sub.get(2).asInt();

                    if (status == 1) { // Accepted
                        String handle = idToHandleMap.get(vId);
                        if (handle != null) {
                            userSolves.putIfAbsent(handle, new HashSet<>());
                            userSolves.get(handle).add(problemIdx);
                        }
                    }
                }
            }

            int updatedUsers = 0;
            // Loop through our local results
            for (Map.Entry<String, Set<Integer>> entry : userSolves.entrySet()) {
                String handle = entry.getKey();
                int solveCount = entry.getValue().size();

                // Find user in OUR database
                User user = userRepository.findByVjudgeHandle(handle);

                if (user != null) {
                    // Update History Record
                    ContestHistory history = contestHistoryRepository.findByUserAndRound(user, round)
                            .orElse(new ContestHistory());

                    history.setUser(user);
                    history.setRound(round);
                    history.setSolvedCount(solveCount);
                    contestHistoryRepository.save(history);

                    // --- FIX 2: SAFER FILTER LOGIC ---
                    // We fetch all history for this user to calculate total score
                    // (Note: In production, use a custom SQL Query for speed)
                    Integer newTotal = contestHistoryRepository.findAll()
                            .stream()
                            // Ensure getUser() is not null before checking ID
                            .filter(h -> h.getUser() != null && h.getUser().getId()==(user.getId()))
                            .mapToInt(ContestHistory::getSolvedCount)
                            .sum();

                    user.setTotalSolved(newTotal);
                    userRepository.save(user); // Save the new total

                    updatedUsers++;
                }
            }
            int updatedCount = 0;
            List<User> allUsers  = userRepository.findAll();
            for (User user : allUsers) {
                String handle = user.getVjudgeHandle();
                boolean attended = false;
                int solveCount = 0;

                // Did this user submit code?
                if (userSolves.containsKey(handle)) {
                    attended = true;
                    solveCount = userSolves.get(handle).size();
                }

                // 2. Update Round History (Receipt)
                if (attended) {
                    ContestHistory history = contestHistoryRepository.findByUserAndRound(user, round)
                            .orElse(new ContestHistory());
                    history.setUser(user);
                    history.setRound(round);
                    history.setSolvedCount(solveCount);
                    contestHistoryRepository.save(history);
                }

                // 3. Update Total Score
                Integer newTotal = contestHistoryRepository.findAll().stream()
                        .filter(h -> h.getUser() != null && h.getUser().getId()==(user.getId()))
                        .mapToInt(ContestHistory::getSolvedCount).sum();
                user.setTotalSolved(newTotal);

                // 4. THE BAN HAMMER 🔨
                if (attended) {
                    user.setConsecutiveMisses(0); // Reset streak if they participated
                    if ("BANNED".equals(user.getStatus())) {
                        user.setStatus("ACTIVE"); // Unban if they return? (Optional)
                    }
                } else {
                    // They missed it. Increment the counter.
                    int misses = user.getConsecutiveMisses() == null ? 0 : user.getConsecutiveMisses();
                    user.setConsecutiveMisses(misses + 1);

                    if (user.getConsecutiveMisses() >= 6) {
                        user.setStatus("BANNED");
                    }
                }

                userRepository.save(user);
                updatedCount++;
            }

            return "Updated " + updatedCount + " users. Ban Hammer check complete.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing JSON: " + e.getMessage();
        }
    }
}