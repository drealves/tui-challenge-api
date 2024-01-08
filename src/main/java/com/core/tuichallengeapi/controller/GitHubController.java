package com.core.tuichallengeapi.controller;

import com.core.tuichallengeapi.dto.RepositoryInfo;
import com.core.tuichallengeapi.service.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/repositories/{username}")
    public Mono<ResponseEntity<List<RepositoryInfo>>> getUserRepositories(@PathVariable String username) {
        return gitHubService.getUserRepos(username)
                .map(repos -> ResponseEntity.ok().body(repos))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
}