package com.nextgen.DiuNextGen.dto;

import lombok.Data;

@Data
public class LeaderboardEntry {
    private String name;
    private String handle;
    private String batch;
    private Integer totalSolved;
    private String photoUrl;
    private String status; // "ACTIVE" or "BANNED"

    public LeaderboardEntry(String name, String handle, String batch, Integer totalSolved, String photoUrl, String status) {
        this.name = name;
        this.handle = handle;
        this.batch = batch;
        this.totalSolved = totalSolved;
        this.photoUrl = photoUrl;
        this.status = status;
    }
}