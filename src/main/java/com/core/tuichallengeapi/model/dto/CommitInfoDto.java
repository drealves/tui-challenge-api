package com.core.tuichallengeapi.model.dto;

public class CommitInfoDto {
    private String sha;

    public CommitInfoDto() {
    }

    public CommitInfoDto(String sha) {
        this.sha = sha;
    }
    // Getters and setters
    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }
}
