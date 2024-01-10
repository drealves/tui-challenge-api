package com.core.tuichallengeapi.service;

import com.core.tuichallengeapi.config.GitHubApiPropertiesConfig;
import com.core.tuichallengeapi.dto.BranchInfo;
import com.core.tuichallengeapi.dto.CommitInfo;
import com.core.tuichallengeapi.dto.RepositoryInfo;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    private final WebClient webClient;

    @Autowired
    public GitHubService(WebClient.Builder webClientBuilder, GitHubApiPropertiesConfig gitHubApiPropertiesConfig) {
        this.webClient = webClientBuilder.baseUrl(gitHubApiPropertiesConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, gitHubApiPropertiesConfig.getToken()).build();
    }

    public Mono<List<Map<String, Object>>> getRepositoryInfo(String username, int page, int size) {
        Flux<RepositoryInfo> repositoriesFlux = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{username}/repos")
                        .queryParam("page", page)
                        .queryParam("per_page", size)
                        .build(username))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new UserNotFoundException("User not found")))
                .bodyToFlux(RepositoryInfo.class);

        return repositoriesFlux.flatMap(repositoryInfo -> {
                    String owner = repositoryInfo.getOwner().getLogin();
                    String repositoryName = repositoryInfo.getName();

                    Flux<BranchInfo> branchInfoFlux = webClient.get()
                            .uri("/repos/{owner}/{repo}/branches", owner, repositoryName)
                            .retrieve()
                            .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                                    response -> Mono.error(new UserNotFoundException("Repository not found")))
                            .bodyToFlux(BranchInfo.class);

                    return branchInfoFlux.flatMap(branchInfo -> getLastCommitSha(owner, repositoryName, branchInfo.getName())
                                    .map(lastCommitSha -> {
                                        Map<String, String> branchMap = new HashMap<>();
                                        branchMap.put("branchName", branchInfo.getName());
                                        branchMap.put("lastCommitSha", lastCommitSha);
                                        return branchMap;
                                    }))
                            .collectList()
                            .map(branches -> {
                                Map<String, Object> repositoryDetails = new HashMap<>();
                                repositoryDetails.put("repositoryName", repositoryInfo.getName());
                                repositoryDetails.put("ownerLogin", repositoryInfo.getOwner().getLogin());
                                repositoryDetails.put("branches", branches);
                                return repositoryDetails;
                            });
                })
                .collectList(); // Collecting all repository details into a final list
    }

    private Mono<String> getLastCommitSha(String owner, String repositoryName, String branch) {
        String commitsUrl = String.format("/repos/%s/%s/commits/%s", owner, repositoryName, branch);

        return webClient.get()
                .uri(commitsUrl)
                .retrieve()
                .onStatus(httpStatus -> httpStatus.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.error(new UserNotFoundException("Commit not found")))
                .bodyToMono(CommitInfo.class)
                .map(CommitInfo::getSha);
    }


}


