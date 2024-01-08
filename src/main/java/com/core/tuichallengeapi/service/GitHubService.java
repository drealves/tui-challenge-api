package com.core.tuichallengeapi.service;

import com.core.tuichallengeapi.dto.RepositoryInfo;
import com.core.tuichallengeapi.exception.GitHubApiException;
import com.core.tuichallengeapi.exception.GitHubApiRateLimitExceededException;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GitHubService {

    private final WebClient webClient;

    private final String githubToken;


    public GitHubService(WebClient.Builder webClientBuilder, @Value("${github.token}") String githubToken) {
        this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
        this.githubToken = githubToken;
    }

    public Mono<List<RepositoryInfo>> getUserRepos(String username) {
        return webClient.get()
                .uri("/users/{username}/repos", username)
                .header("Authorization", "Bearer " + githubToken)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(new UserNotFoundException("User not found"));
                    }
                    return Mono.error(new GitHubApiException("Client error occurred"));
                })
                .onStatus(status -> status.is5xxServerError(), response -> Mono.error(new GitHubApiException("Server error occurred")))
                .onStatus(status -> status.equals(HttpStatus.TOO_MANY_REQUESTS), response -> Mono.error(new GitHubApiRateLimitExceededException("Rate limit exceeded")))
                .bodyToFlux(RepositoryInfo.class)
                .collectList();
    }
}
