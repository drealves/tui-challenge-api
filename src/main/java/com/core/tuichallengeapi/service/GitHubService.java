package com.core.tuichallengeapi.service;

import com.core.tuichallengeapi.client.GitHubClient;
import com.core.tuichallengeapi.mapper.GitHubMapper;
import com.core.tuichallengeapi.model.BranchInfo;
import com.core.tuichallengeapi.model.CommitInfo;
import com.core.tuichallengeapi.model.dto.PaginatedRepositoriesResponseDto;
import com.core.tuichallengeapi.model.RepositoryInfo;
import com.core.tuichallengeapi.model.dto.RepositoryInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private final GitHubClient gitHubClient;

    @Autowired
    public GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }


    /**
     * Retrieves a list of repository information for a specific GitHub user.
     *+
     * @param username The GitHub username.
     * @param page The page number for pagination.
     * @param size The number of repositories per page.
     * @return A Mono wrapping a list of RepositoryInfo objects.
     */
    public Mono<PaginatedRepositoriesResponseDto> getRepositoryInfo(String username, int page, int size, boolean includeForks) {
        return fetchUserRepositories(username, page, includeForks)
                .collectList()
                .map(repositories -> {
                    // Convert to DTOs
                    List<RepositoryInfoDto> repoListDto = repositories.stream()
                            .map(GitHubMapper::toRepositoryInfoDto)
                            .collect(Collectors.toList());

                    // Correct the totalElements and totalPages calculations
                    long totalElements = repoListDto.size();
                    int totalPages = (int) Math.ceil((double) totalElements / size);

                    // Correct the pagination logic
                    int startIndex = (page - 1) * size;
                    int endIndex = Math.min(startIndex + size, repoListDto.size());

                    // Handling the case where startIndex is greater than the size of the list
                    if (startIndex >= repoListDto.size()) {
                        startIndex = Math.max(repoListDto.size() - 1, 0);
                        endIndex = startIndex;
                    }

                    List<RepositoryInfoDto> paginatedList = repoListDto.subList(startIndex, endIndex);
                    return new PaginatedRepositoriesResponseDto(paginatedList, page, size, totalElements, totalPages);
                });
    }
    /**
     * Fetches repository information for a given user from GitHub.
     *
     * @param username The GitHub username.
     * @return A Flux of RepositoryInfo objects.
     */
    public Flux<RepositoryInfo> fetchUserRepositories(String username, int page, boolean includeForks) {
        return gitHubClient.getRepositories(username, page)
                .filter(repositoryInfo -> includeForks || !repositoryInfo.isFork())
                .flatMap(this::fetchBranchesForRepository);
    }

    /**
     * Fetches branch information for each repository and updates the RepositoryInfo object.
     *
     * @param repositoryInfo The repository information.
     * @return A Mono of updated RepositoryInfo with branches.
     */
    public Mono<RepositoryInfo> fetchBranchesForRepository(RepositoryInfo repositoryInfo) {
        return gitHubClient.getBranchesForRepository(repositoryInfo.getOwner().getLogin(), repositoryInfo.getName())
                .flatMap(branchInfo -> {
                    if (branchInfo.getCommits() == null) {
                        // Conditional call to fetch last commit SHA
                        return fetchLastCommitShaAndUpdateBranch(repositoryInfo, branchInfo);
                    }
                    return Mono.just(branchInfo);
                })
                .collectList()
                .map(branches -> {
                    repositoryInfo.setBranches(branches);
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



