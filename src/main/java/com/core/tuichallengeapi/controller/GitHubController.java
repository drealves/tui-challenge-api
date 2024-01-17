package com.core.tuichallengeapi.controller;

import com.core.tuichallengeapi.exception.IllegalArgumentException;
import com.core.tuichallengeapi.model.dto.PaginatedRepositoriesResponseDto;
import com.core.tuichallengeapi.service.GitHubService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * RestController for handling GitHub related API requests.
 */
@RestController
@RequestMapping("/api/v1/github")
public class GitHubController {

    private final GitHubService gitHubService;

    /**
     * Constructs a GitHubController with a GitHubService.
     *
     * @param gitHubService The service for GitHub operations.
     */
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    /**
     * Endpoint for retrieving a list of repositories for a specific GitHub user.
     * Supports pagination and only responds to JSON requests.
     *
     * @param username The GitHub username.
     * @param page The page number for pagination.
     * @param size The number of repositories per page.
     * @param acceptHeader The HTTP 'Accept' header to determine the response format.
     * @return A Mono wrapping a ResponseEntity containing RepositoriesResponseDto.
     */
    @GetMapping(path = "/users/{username}/repositories", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginatedRepositoriesResponseDto>> listUserRepositories(
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestHeader("Accept") String acceptHeader,
            @RequestParam(defaultValue = "false") boolean includeForks) {

        // Validate pagination parameters
        if (size > 100 || size < 1) {
            return Mono.error(new IllegalArgumentException("Page size must be between 1 and 100"));
        }
        if (page < 1) {
            return Mono.error(new IllegalArgumentException("Page number must be greater than 0"));
        }

        return gitHubService.getRepositoryInfo(username, page, size, includeForks)
                .map(response -> ResponseEntity.ok().body(response));
    }
}
