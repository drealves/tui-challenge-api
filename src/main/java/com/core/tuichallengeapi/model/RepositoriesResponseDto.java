package com.core.tuichallengeapi.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoriesResponseDto {

    @JsonProperty("repositories")
    private List<RepositoryInfo> repositories;

    public RepositoriesResponseDto(List<RepositoryInfo> repositories) {
        this.repositories = repositories;
    }

    public List<RepositoryInfo> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RepositoryInfo> repositories) {
        this.repositories = repositories;
    }
}