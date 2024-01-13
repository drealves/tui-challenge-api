package com.core.tuichallengeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitInfo {
    private String sha;

    public CommitInfo() {
    }

    public CommitInfo(String sha) {
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