package com.core.tuichallengeapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchInfo {
    private String name;
    private List<CommitInfo> commit;

    public BranchInfo(String name, List<CommitInfo> commits) {
        this.name = name;
        this.commit = commits;
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

    public List<CommitInfo> getCommits() {
        return commit;
    }

    public void setCommits(List<CommitInfo> commits) {
        this.commit = commits;
    }
}
