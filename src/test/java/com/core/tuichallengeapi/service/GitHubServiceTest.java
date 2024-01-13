package com.core.tuichallengeapi.service;

import com.core.tuichallengeapi.client.GitHubClient;
import com.core.tuichallengeapi.exception.ForbiddenException;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import com.core.tuichallengeapi.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GitHubServiceTest {

    @Mock
    private GitHubClient gitHubClient;

    private GitHubService gitHubService;

    private Owner mockOwner;
    private CommitInfo mockCommitInfo;
    private BranchInfo mockBranchInfo;
    private RepositoryInfo mockRepositoryInfo;
    private RepositoriesResponseDto mockResponseDto;
    private ResponseErrorDto mockErrorDto;


    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);
        gitHubService = new GitHubService(gitHubClient);

        mockOwner = new Owner("username");

        mockCommitInfo = new CommitInfo("000000000000000000");

        mockBranchInfo = new BranchInfo("main", Arrays.asList(mockCommitInfo));

        mockRepositoryInfo = new RepositoryInfo("repoName", mockOwner, false, Collections.singletonList(mockBranchInfo));

        mockResponseDto = new RepositoriesResponseDto(Collections.singletonList(mockRepositoryInfo));

        mockErrorDto = new ResponseErrorDto(404, "Not Found");

    }

    @Test
    public void testGetRepositoryInfoWithValidUserAndRepositories() {
        // Mock GitHub client responses
        when(gitHubClient.getRepositories(anyString(), anyInt(), anyInt()))
                .thenReturn(Flux.just(mockRepositoryInfo));
        when(gitHubClient.getBranchesForRepository(anyString(), anyString()))
                .thenReturn(Flux.just(mockBranchInfo));
        when(gitHubClient.getLastCommitSha(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just("000000000000000000")); // Match with mockCommitInfo SHA

        // Call the method and verify the response
        StepVerifier.create(gitHubService.getRepositoryInfo("validUser", 1, 10))
                .expectNextMatches(list -> {
                    // Perform your checks here
                    // Example: Check if the list contains RepositoryInfo with the expected name
                    return list.stream().anyMatch(repo -> "repoName".equals(repo.getName()));
                })
                .verifyComplete();
    }


    @Test
    public void testGetRepositoryInfoWithValidUserNoRepositories() {
        // Mock GitHub client to return an empty Flux
        when(gitHubClient.getRepositories(anyString(), anyInt(), anyInt()))
                .thenReturn(Flux.empty());

        // Call the method and verify that it returns an empty list
        StepVerifier.create(gitHubService.getRepositoryInfo("validUser", 1, 10))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    public void testGetRepositoryInfoWithInvalidUser() {
        // Mock GitHub client to return an empty Flux for an invalid user
        when(gitHubClient.getRepositories(anyString(), anyInt(), anyInt()))
                .thenReturn(Flux.empty());

        // Call the method and verify that it returns an empty list
        StepVerifier.create(gitHubService.getRepositoryInfo("invalidUser", 1, 10))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    public void testGetRepositoryInfoWithError() {
        // Mock GitHub client to return an error
        when(gitHubClient.getRepositories(anyString(), anyInt(), anyInt()))
                .thenReturn(Flux.error(new UserNotFoundException("User not found")));

        // Call the method and expect an error
        StepVerifier.create(gitHubService.getRepositoryInfo("user", 1, 10))
                .expectErrorMatches(throwable -> throwable instanceof UserNotFoundException &&
                        throwable.getMessage().equals("User not found"))
                .verify();
    }

    @Test
    public void testFetchUserRepositoriesWithForksAndOriginalRepos() {
        // Mock data with both forked and original repositories
        RepositoryInfo forkedRepo = new RepositoryInfo("forkedRepo", mockOwner, true, Collections.emptyList());
        RepositoryInfo originalRepo = new RepositoryInfo("originalRepo", mockOwner, false, Collections.emptyList());

        when(gitHubClient.getRepositories("validUser", 1, 10))
                .thenReturn(Flux.just(forkedRepo, originalRepo));

        // Call the method and verify the response
        StepVerifier.create(gitHubService.fetchUserRepositories("validUser", 1, 10))
                .expectNextMatches(repo -> "originalRepo".equals(repo.getName()))
                .verifyComplete();
    }

    @Test
    public void testFetchUserRepositoriesWithOnlyForkedRepos() {
        // Mock data with only forked repositories
        RepositoryInfo forkedRepo1 = new RepositoryInfo("forkedRepo1", mockOwner, true, Collections.emptyList());
        RepositoryInfo forkedRepo2 = new RepositoryInfo("forkedRepo2", mockOwner, true, Collections.emptyList());

        when(gitHubClient.getRepositories("validUser", 1, 10))
                .thenReturn(Flux.just(forkedRepo1, forkedRepo2));

        // Call the method and verify that no repository is returned
        StepVerifier.create(gitHubService.fetchUserRepositories("validUser", 1, 10))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void testFetchUserRepositoriesWithError() {
        // Mock GitHub client to return an error
        when(gitHubClient.getRepositories("validUser", 1, 10))
                .thenReturn(Flux.error(new ForbiddenException("Forbidden")));

        // Call the method and expect an error
        StepVerifier.create(gitHubService.fetchUserRepositories("validUser", 1, 10))
                .expectErrorMatches(throwable -> throwable instanceof ForbiddenException &&
                        "Forbidden".equals(throwable.getMessage()))
                .verify();
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
    }



    @Test
    public void testFetchAndSetLastCommitForBranchesWithNoCommits() {
        // Create test data
        RepositoryInfo testRepo = new RepositoryInfo();
        testRepo.setOwner(new Owner("drealves"));
        testRepo.setName("SmartStart");
        testRepo.setFork(false);

        BranchInfo testBranch = new BranchInfo();
        testBranch.setName("master");

        // Mocking GitHubClient to return an empty Mono for getLastCommitSha
        when(gitHubClient.getLastCommitSha(anyString(), anyString(), anyString())).thenReturn(Mono.empty());

        // Call the method under test
        Mono<RepositoryInfo> result = gitHubService.fetchAndSetLastCommitForBranches(testRepo, Collections.singletonList(testBranch));

        // Verify the results
        StepVerifier.create(result)
                .assertNext(repositoryInfo -> {
                    assertEquals(1, repositoryInfo.getBranches().size());
                    BranchInfo branchInfo = repositoryInfo.getBranches().get(0);
                    assertEquals("master", branchInfo.getName());
                    assertTrue(branchInfo.getCommits().isEmpty());
                })
                .verifyComplete();
    }

}
