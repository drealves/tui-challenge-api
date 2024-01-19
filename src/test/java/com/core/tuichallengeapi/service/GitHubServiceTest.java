package com.core.tuichallengeapi.service;

import com.core.tuichallengeapi.client.GitHubClient;
import com.core.tuichallengeapi.exception.ForbiddenException;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import com.core.tuichallengeapi.model.BranchInfo;
import com.core.tuichallengeapi.model.Owner;
import com.core.tuichallengeapi.model.RepositoryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


class GitHubServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    @InjectMocks
    private GitHubService gitHubService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBranchesForRepositoryMock() {
        // Arrange
        when(gitHubClient.getBranchesForRepository(anyString(), anyString()))
                .thenReturn(Flux.just(new BranchInfo())); // or Flux.empty()

        // Act
        Flux<BranchInfo> result = gitHubClient.getBranchesForRepository("user", "repo");

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1) // or 0 if using Flux.empty()
                .verifyComplete();

        // Verify
        verify(gitHubClient).getBranchesForRepository("user", "repo");
    }


    @Test
    public void testGetRepositoryInfoWithError() {
        // Mock GitHub client to return an error
        when(gitHubClient.getRepositories(anyString(),anyInt()))
                .thenReturn(Flux.error(new UserNotFoundException("User not found")));

        // Call the method and expect an error
        StepVerifier.create(gitHubService.getRepositoryInfo("user", 1, 10, false))
                .expectErrorMatches(throwable -> throwable instanceof UserNotFoundException &&
                        throwable.getMessage().equals("User not found"))
                .verify();

        // Verify that gitHubClient.getRepositories method was called with the correct arguments
        verify(gitHubClient).getRepositories("user", 1);
    }

    @Test
    public void testFetchUserRepositoriesWithOnlyForkedRepos() {
        // Mock data with only forked repositories
        RepositoryInfo forkedRepo1 = new RepositoryInfo("forkedRepo1", new Owner("lala"), true, Collections.emptyList());
        RepositoryInfo forkedRepo2 = new RepositoryInfo("forkedRepo2", new Owner("lala1"), true, Collections.emptyList());

        when(gitHubClient.getRepositories("validUser", 1))
                .thenReturn(Flux.just(forkedRepo1, forkedRepo2));

        // Call the method and verify that no repository is returned
        StepVerifier.create(gitHubService.fetchUserRepositories("validUser", 1, false))
                .expectNextCount(0)
                .verifyComplete();

        // Verify that gitHubClient.getRepositories method was called with the correct arguments
        verify(gitHubClient).getRepositories("validUser", 1);
    }

    @Test
    public void testFetchUserRepositoriesWithError() {
        // Mock GitHub client to return an error
        when(gitHubClient.getRepositories("validUser", 1))
                .thenReturn(Flux.error(new ForbiddenException("Forbidden")));

        // Call the method and expect an error
        StepVerifier.create(gitHubService.fetchUserRepositories("validUser", 1, false))
                .expectErrorMatches(throwable -> throwable instanceof ForbiddenException &&
                        "Forbidden".equals(throwable.getMessage()))
                .verify();

        // Verify that gitHubClient.getRepositories method was called with the correct arguments
        verify(gitHubClient).getRepositories("validUser", 1);
    }


    @Test
    public void testFetchBranchesForRepositoryWithMultipleBranches() {
        // Setup mock repository info
        RepositoryInfo mockRepository = new RepositoryInfo("repoName", new Owner("owner"), false, null);

        // Mock GitHub client response for multiple branches
        BranchInfo branch1 = new BranchInfo("branch1", null);
        BranchInfo branch2 = new BranchInfo("branch2", null);
        when(gitHubClient.getBranchesForRepository("owner", "repoName"))
                .thenReturn(Flux.just(branch1, branch2));
        when(gitHubClient.getLastCommitSha(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just("000000000000000000"));

        // Call the method and verify the response
        StepVerifier.create(gitHubService.fetchBranchesForRepository(mockRepository))
                .expectNextMatches(repositoryInfo -> repositoryInfo.getBranches().size() == 2)
                .verifyComplete();

        // Verify that gitHubClient.getBranchesForRepository and gitHubClient.getLastCommitSha methods were called
        verify(gitHubClient).getBranchesForRepository("owner", "repoName");
        verify(gitHubClient, times(2)).getLastCommitSha(anyString(), anyString(), anyString());
    }

    @Test
    public void testFetchBranchesForRepositoryWithNoBranches() {
        // Setup mock repository info
        RepositoryInfo mockRepository = new RepositoryInfo("repoName", new Owner("owner"), false, null);

        // Mock GitHub client response for no branches
        when(gitHubClient.getBranchesForRepository("owner", "repoName"))
                .thenReturn(Flux.empty());

        // Call the method and verify the response
        StepVerifier.create(gitHubService.fetchBranchesForRepository(mockRepository))
                .expectNextMatches(repositoryInfo -> repositoryInfo.getBranches().isEmpty())
                .verifyComplete();

        // Verify that gitHubClient.getBranchesForRepository method was called with the correct arguments
        verify(gitHubClient).getBranchesForRepository("owner", "repoName");
    }

    @Test
    public void testFetchBranchesForRepositoryWithError() {
        // Setup mock repository info
        RepositoryInfo mockRepository = new RepositoryInfo("repoName", new Owner("owner"), false, null);

        // Mock GitHub client to return an error
        when(gitHubClient.getBranchesForRepository("owner", "repoName"))
                .thenReturn(Flux.error(new RuntimeException("Error fetching branches")));

        // Call the method and expect an error
        StepVerifier.create(gitHubService.fetchBranchesForRepository(mockRepository))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        "Error fetching branches".equals(throwable.getMessage()))
                .verify();

        // Verify that gitHubClient.getBranchesForRepository method was called with the correct arguments
        verify(gitHubClient).getBranchesForRepository("owner", "repoName");
    }

}
