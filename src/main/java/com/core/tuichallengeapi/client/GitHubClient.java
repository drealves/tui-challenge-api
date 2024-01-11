package com.core.tuichallengeapi.client;

import com.core.tuichallengeapi.config.GitHubApiPropertiesConfig;
import com.core.tuichallengeapi.dto.BranchInfo;
import com.core.tuichallengeapi.dto.CommitInfo;
import com.core.tuichallengeapi.dto.RepositoryInfo;
import com.core.tuichallengeapi.exception.ForbiddenException;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GitHubClient {

    private final WebClient webClient;

    @Autowired
    public GitHubClient(WebClient.Builder webClientBuilder, GitHubApiPropertiesConfig gitHubApiPropertiesConfig) {
        this.webClient = webClientBuilder.baseUrl(gitHubApiPropertiesConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, gitHubApiPropertiesConfig.getToken()).build();
    }

    public Flux<RepositoryInfo> getRepositories(String username, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{username}/repos")
                        .queryParam("page", page)
                        .queryParam("per_page", size)
                        .build(username))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new UserNotFoundException("User not found")))
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.FORBIDDEN),
                        response -> Mono.error(new ForbiddenException("User not found")))
                .bodyToFlux(RepositoryInfo.class);
    }

    public Flux<BranchInfo> getBranchesForRepository(String owner, String repositoryName) {
        return webClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repositoryName)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.FORBIDDEN),
                        response -> Mono.error(new ForbiddenException("User not found")))
                .bodyToFlux(BranchInfo.class);
    }

    public Mono<String> getLastCommitSha(String owner, String repositoryName, String branch) {
        String commitsUrl = String.format("/repos/%s/%s/commits/%s", owner, repositoryName, branch);

        return webClient.get()
                .uri(commitsUrl)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.FORBIDDEN),
                        response -> Mono.error(new ForbiddenException("User not found")))
                .bodyToMono(CommitInfo.class)
                .map(CommitInfo::getSha);
    }
}
