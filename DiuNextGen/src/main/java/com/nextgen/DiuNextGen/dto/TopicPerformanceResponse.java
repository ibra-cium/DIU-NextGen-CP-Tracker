package com.nextgen.DiuNextGen.dto; // Check package name

import lombok.Data;

@Data
public class TopicPerformanceResponse {
    private String roundName;
    private Integer solvedCount;
    private Integer totalProblems; // Optional: If you want to show "5/10 solved"

    public TopicPerformanceResponse(String roundName, Integer solvedCount) {
        this.roundName = roundName;
        this.solvedCount = solvedCount;
    }
}