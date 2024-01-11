package com.core.tuichallengeapi.controller;

import com.core.tuichallengeapi.exception.HttpAcceptException;
import com.core.tuichallengeapi.service.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/github")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/repositories/{username}")
    public Mono<ResponseEntity<List<Map<String, Object>>>> listUserRepositories(
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestHeader("Accept") String acceptHeader) {

        if (acceptHeader.equals("application/xml")) {
            return Mono.error(new HttpAcceptException("XML format not supported"));
        }
        return gitHubService.getRepositoryInfo(username, page, size)
                .map(repos -> ResponseEntity.ok().body(repos));
    }
}
