package com.core.tuichallengeapi.dto;

public class BranchInfo {
    private String name;  // Name of the branch
    private String lastCommitSha;  // SHA of the last commit in the branch

    // No-argument constructor
    public BranchInfo() {
    }

    // All-argument constructor
    public BranchInfo(String name, String lastCommitSha) {
        this.name = name;
        this.lastCommitSha = lastCommitSha;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getLastCommitSha() {
        return lastCommitSha;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setLastCommitSha(String lastCommitSha) {
        this.lastCommitSha = lastCommitSha;
    }

    // Override toString, equals, and hashCode if necessary
}
