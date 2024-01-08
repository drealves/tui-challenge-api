package com.core.tuichallengeapi.dto;

import java.util.List;

public class RepositoryInfo {
    private String name;
    private String ownerLogin;
    //private boolean fork; // Indicates if the repository is a fork
    private List<BranchInfo> branches; // Assuming you have a BranchInfo class

    // No-argument constructor
    public RepositoryInfo() {
    }

    // All-argument constructor
    public RepositoryInfo(String name, String ownerLogin, List<BranchInfo> branches) {
        this.name = name;
        this.ownerLogin = ownerLogin;
        //this.fork = fork;
        this.branches = branches;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public List<BranchInfo> getBranches() {
        return branches;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public void setBranches(List<BranchInfo> branches) {
        this.branches = branches;
    }

    // Other necessary methods
}
