package com.nextgen.DiuNextGen.dto;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String role; // <--- ADD THIS

    public LoginResponse(String token, String role) {
        this.token = token;
        this.role = role; // <--- UPDATE CONSTRUCTOR
    }

    @Data
    public static class LeaderboardEntry {
        private String name;
        private String handle;
        private String batch;
        private Integer totalSolved;
        private String photoUrl;

        // Constructor for easy creation
        public LeaderboardEntry(String name, String handle, String batch, Integer totalSolved, String photoUrl) {
            this.name = name;
            this.handle = handle;
            this.batch = batch;
            this.totalSolved = totalSolved;
            this.photoUrl = photoUrl;
        }
    }
}