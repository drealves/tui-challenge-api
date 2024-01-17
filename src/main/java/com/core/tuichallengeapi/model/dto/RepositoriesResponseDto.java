package com.core.tuichallengeapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RepositoriesResponseDto {

    @JsonProperty("repositories")
    private List<RepositoryInfoDto> repositories;

    public RepositoriesResponseDto() {
    }

    public RepositoriesResponseDto(List<RepositoryInfoDto> repositories) {
        this.repositories = repositories;
    }

    public List<RepositoryInfoDto> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<RepositoryInfoDto> repositories) {
        this.repositories = repositories;
    }
}