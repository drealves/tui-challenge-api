package com.core.tuichallengeapi.model.dto;

import com.core.tuichallengeapi.model.BranchInfo;
import com.core.tuichallengeapi.model.Owner;

import java.util.List;

public class RepositoryInfoDto {

    private String name;
    private Owner owner;
    private boolean fork;
    private List<BranchInfoDto> branches; // Add this field


    public RepositoryInfoDto() {
    }

    public RepositoryInfoDto(String name, Owner owner, boolean fork, List<BranchInfoDto> branches) {
        this.name = name;
        this.owner = owner;
        this.fork = fork;
        this.branches = branches;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public List<BranchInfoDto> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchInfoDto> branches) {
        this.branches = branches;
    }
}
