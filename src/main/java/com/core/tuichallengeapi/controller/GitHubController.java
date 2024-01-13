package com.core.tuichallengeapi.controller;

import com.core.tuichallengeapi.exception.HttpAcceptException;
import com.core.tuichallengeapi.model.RepositoriesResponseDto;
import com.core.tuichallengeapi.service.GitHubService;
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
    @GetMapping("/repositories/{username}")
    public Mono<ResponseEntity<RepositoriesResponseDto>> listUserRepositories(
            @PathVariable String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestHeader("Accept") String acceptHeader) {

        // Check if the request header specifies XML; if so, return an error as XML is not supported
        if (acceptHeader.equals("application/xml")) {
            return Mono.error(new HttpAcceptException("XML format not supported"));
        }
        // Call the service to get repository information and wrap it in a ResponseEntity
        return gitHubService.getRepositoryInfo(username, page, size)
                .map(repos -> ResponseEntity.ok().body(new RepositoriesResponseDto(repos)));
    }
}
