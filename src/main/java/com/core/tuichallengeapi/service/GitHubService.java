package com.core.tuichallengeapi.service;

import com.core.tuichallengeapi.client.GitHubClient;
import com.core.tuichallengeapi.model.BranchInfo;
import com.core.tuichallengeapi.model.CommitInfo;
import com.core.tuichallengeapi.model.RepositoryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class GitHubService {

    private final GitHubClient gitHubClient;

    @Autowired
    public GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }


    /**
     * Retrieves a list of repository information for a specific GitHub user.
     *
     * @param username The GitHub username.
     * @param page The page number for pagination.
     * @param size The number of repositories per page.
     * @return A Mono wrapping a list of RepositoryInfo objects.
     */
    public Mono<List<RepositoryInfo>> getRepositoryInfo(String username, int page, int size) {
        return fetchUserRepositories(username, page, size)
                .flatMap(this::fetchBranchesForRepository)
                .collectList();
    }

    /**
     * Fetches repository information for a given user from GitHub.
     *
     * @param username The GitHub username.
     * @param page The page number for pagination.
     * @param size The number of repositories per page.
     * @return A Flux of RepositoryInfo objects.
     */
    public Flux<RepositoryInfo> fetchUserRepositories(String username, int page, int size) {
        return gitHubClient.getRepositories(username, page, size)
                .filter(repositoryInfo -> !repositoryInfo.isFork()); // Filter out forked repositories
    }

    /**
     * Fetches branch information for each repository and updates the RepositoryInfo object.
     *
     * @param repositoryInfo The repository information.
     * @return A Mono of updated RepositoryInfo with branches.
     */
    public Mono<RepositoryInfo> fetchBranchesForRepository(RepositoryInfo repositoryInfo) {
        return gitHubClient.getBranchesForRepository(repositoryInfo.getOwner().getLogin(), repositoryInfo.getName())
                .collectList()
                .flatMap(branches -> fetchAndSetLastCommitForBranches(repositoryInfo, branches));
    }


    /**
     * Fetches the last commit SHA for each branch in a repository and updates the BranchInfo objects.
     *
     * @param repositoryInfo The repository information.
     * @param branches       The list of branches in the repository.
     * @return A Mono of RepositoryInfo with updated branches.
     */
    public Mono<RepositoryInfo> fetchAndSetLastCommitForBranches(RepositoryInfo repositoryInfo, List<BranchInfo> branches) {
        if (branches.isEmpty()) {
            repositoryInfo.setBranches(Collections.emptyList());
            return Mono.just(repositoryInfo);
        }

        return Flux.fromIterable(branches)
                .flatMap(branchInfo -> fetchLastCommitShaAndUpdateBranch(repositoryInfo, branchInfo))
                .collectList()
                .map(updatedBranches -> {
                    repositoryInfo.setBranches(updatedBranches);
                    return repositoryInfo;
                });
    }

    /**
     * Fetches the last commit SHA for a specific branch and updates the BranchInfo object.
     *
     * @param repositoryInfo The repository information.
     * @param branchInfo     The branch information.
     * @return A Mono of updated BranchInfo with the last commit SHA.
     */
    public Mono<BranchInfo> fetchLastCommitShaAndUpdateBranch(RepositoryInfo repositoryInfo, BranchInfo branchInfo) {
        return gitHubClient.getLastCommitSha(repositoryInfo.getOwner().getLogin(), repositoryInfo.getName(), branchInfo.getName())
                .defaultIfEmpty("") // This ensures that an empty string is emitted if no commit SHA is found.
                .map(commitSha -> {
                    if (!commitSha.isEmpty()) {
                        branchInfo.setCommits(Collections.singletonList(new CommitInfo(commitSha)));
                    } else {
                        // If commitSha is empty, we still include the branch with an empty commits list
                        branchInfo.setCommits(Collections.emptyList());
                    }
                    return branchInfo;
                });
    }
}



