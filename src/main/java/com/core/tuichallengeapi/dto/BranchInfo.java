package com.core.tuichallengeapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchInfo {
    private String name;

    public BranchInfo(String name) {
        this.name = name;
    }

    public BranchInfo() {
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
