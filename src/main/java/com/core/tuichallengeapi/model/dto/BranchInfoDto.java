package com.core.tuichallengeapi.model.dto;

public class BranchInfoDto {
    private String name;
    private String lastCommit;

    public BranchInfoDto(String name, String commits) {
        this.name = name;
        this.lastCommit = commits;
    }

    public BranchInfoDto() {
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(String lastCommit) {
        this.lastCommit = lastCommit;
    }
}
