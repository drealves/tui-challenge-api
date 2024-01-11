package com.core.tuichallengeapi.service;

import com.core.tuichallengeapi.client.GitHubClient;
import com.core.tuichallengeapi.dto.BranchInfo;
import com.core.tuichallengeapi.dto.Owner;
import com.core.tuichallengeapi.dto.RepositoryInfo;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GitHubServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private GitHubService gitHubService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBranchesForRepository() {
        // Given
        BranchInfo branchInfo = new BranchInfo("main"); // Populate with appropriate data

        when(gitHubClient.getBranchesForRepository(anyString(), anyString()))
                .thenReturn(Flux.just(branchInfo));

        when(gitHubClient.getLastCommitSha(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just("commitSha123"));

        // When & Then
        StepVerifier.create(gitHubService.getBranchesForRepository("owner", "repo"))
                .expectNextMatches(map -> map.get("branchName").equals("main") && map.get("lastCommitSha").equals("commitSha123"))
                .verifyComplete();
    }

    @Test
    public void testGetRepositoryInfo() {
        // Given
        RepositoryInfo repositoryInfo = new RepositoryInfo("repoName", new Owner("ownerLogin")); // Populate with appropriate data
        when(gitHubClient.getRepositories(anyString(), anyInt(), anyInt()))
                .thenReturn(Flux.just(repositoryInfo));

        BranchInfo branchInfo = new BranchInfo("main"); // Populate with appropriate data
        when(gitHubClient.getBranchesForRepository(anyString(), anyString()))
                .thenReturn(Flux.just(branchInfo));

        when(gitHubClient.getLastCommitSha(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just("commitSha123"));

        // When & Then
        StepVerifier.create(gitHubService.getRepositoryInfo("username", 1, 10))
                .expectNextMatches(list -> {
                    Map<String, Object> repoMap = list.get(0);
                    return repoMap.get("repositoryName").equals("repoName") &&
                            repoMap.get("ownerLogin").equals("ownerLogin") &&
                            ((List<?>) repoMap.get("branches")).size() == 1;
                })
                .verifyComplete();
    }

    @Test
    public void testGetBranchesForRepository_EmptyResult() {
        // When the client returns an empty Flux
        when(gitHubClient.getBranchesForRepository(anyString(), anyString()))
                .thenReturn(Flux.empty());

        StepVerifier.create(gitHubService.getBranchesForRepository("owner", "repo"))
                .verifyComplete(); // Verifies that the flux completes without any items
    }

    @Test
    public void testGetRepositoryInfo_ErrorHandling() {
        // When an error occurs while fetching repositories
        when(gitHubClient.getRepositories(anyString(), anyInt(), anyInt()))
                .thenReturn(Flux.error(new UserNotFoundException("User not found")));

        StepVerifier.create(gitHubService.getRepositoryInfo("username", 1, 10))
                .expectErrorMatches(throwable -> throwable instanceof UserNotFoundException)
                .verify();
    }


}
