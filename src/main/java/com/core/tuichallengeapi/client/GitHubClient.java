package com.core.tuichallengeapi.client;

import com.core.tuichallengeapi.config.GitHubApiPropertiesConfig;
import com.core.tuichallengeapi.model.BranchInfo;
import com.core.tuichallengeapi.model.CommitInfo;
import com.core.tuichallengeapi.model.RepositoryInfo;
import com.core.tuichallengeapi.exception.ForbiddenException;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for interacting with the GitHub API.
 */
@Service
public class GitHubClient {

    private final WebClient webClient;

    /**
     * Constructs the GitHubClient with a configured WebClient.
     *
     * @param webClientBuilder The builder for WebClient.
     * @param gitHubApiPropertiesConfig Configuration properties for GitHub API.
     */
    @Autowired
    public GitHubClient(WebClient.Builder webClientBuilder, GitHubApiPropertiesConfig gitHubApiPropertiesConfig) {
        this.webClient = webClientBuilder.baseUrl(gitHubApiPropertiesConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, gitHubApiPropertiesConfig.getToken()).build();
    }

    /**
     * Retrieves a list of repositories for a given GitHub username.
     *
     * @param username The GitHub username.
     * @param page The page number for pagination.
     * @return A Flux of RepositoryInfo objects.
     */
    public Flux<RepositoryInfo> getRepositories(String username, int page) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{username}/repos")
                        .queryParam("page", page)
                        .queryParam("per_page", 100)
                        .build(username))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new UserNotFoundException("User not found")))
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.FORBIDDEN),
                        response -> Mono.error(new ForbiddenException("Forbidden")))
                .bodyToFlux(RepositoryInfo.class);
    }

    /**
     * Retrieves the branches for a given repository.
     *
     * @param owner GitHub username or organization name owning the repository.
     * @param repositoryName The name of the repository.
     * @return A Flux of BranchInfo objects.
     */
    public Flux<BranchInfo> getBranchesForRepository(String owner, String repositoryName) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repositoryName)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.FORBIDDEN),
                        response -> Mono.error(new ForbiddenException("Forbidden")))
                .bodyToFlux(BranchInfo.class);
    }

    /**
     * Retrieves the last commit SHA of a specific branch in a repository.
     *
     * @param owner GitHub username or organization name owning the repository.
     * @param repositoryName The name of the repository.
     * @param branch The branch name.
     * @return A Mono of the last commit SHA as a String.
     */
    public Mono<String> getLastCommitSha(String owner, String repositoryName, String branch) {
        String commitsUrl = String.format("/repos/%s/%s/commits/%s", owner, repositoryName, branch);

        return webClient.get()
                .uri(commitsUrl)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.FORBIDDEN),
                        response -> Mono.error(new ForbiddenException("Forbidden")))
                .bodyToMono(CommitInfo.class)
                .map(CommitInfo::getSha);
    }
}