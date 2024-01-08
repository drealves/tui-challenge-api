package com.core.tuichallengeapi.integration;

import com.core.tuichallengeapi.dto.BranchInfo;
import com.core.tuichallengeapi.exception.GitHubApiException;
import com.core.tuichallengeapi.exception.GitHubApiRateLimitExceededException;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.MediaType;
import com.core.tuichallengeapi.service.GitHubService;
import com.core.tuichallengeapi.dto.RepositoryInfo;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.given;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitHubApiIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GitHubService gitHubService;



    @Test
    public void testGetUserRepositories() {
        // Mocking the GitHubService to return a predetermined list of repositories
        String username = "testUser";

        // Creating mock branches for the repositories
        List<BranchInfo> mockBranches = Arrays.asList(
                new BranchInfo("main", "sha123main"),
                new BranchInfo("develop", "sha123develop")
        );

        List<RepositoryInfo> mockRepos = Arrays.asList(
                new RepositoryInfo("Repo1", "testUser", mockBranches),
                new RepositoryInfo("Repo2", "testUser", mockBranches)
                // Add more repositories as needed for testing
        );

        given(gitHubService.getUserRepos(username)).willReturn(Mono.just(mockRepos));

        // Performing the test
        webTestClient.get().uri("/api/github/repositories/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(mockRepos.size())
                .jsonPath("$[0].name").isEqualTo("Repo1")
                .jsonPath("$[0].ownerLogin").isEqualTo("testUser")
                .jsonPath("$[0].fork").isEqualTo(false)
                .jsonPath("$[0].branches[0].name").isEqualTo("main")
                .jsonPath("$[0].branches[0].lastCommitSha").isEqualTo("sha123main")
                .jsonPath("$[0].branches[1].name").isEqualTo("develop")
                .jsonPath("$[1].name").isEqualTo("Repo2")
                .jsonPath("$[1].ownerLogin").isEqualTo("testUser");
        // Add more assertions as needed to validate the response
    }


    @Test
    public void whenUserDoesNotExist_thenStatus404() {
        String nonExistentUser = "nonExistentUser";
        given(gitHubService.getUserRepos(nonExistentUser)).willThrow(new UserNotFoundException("User does not exist"));

        webTestClient.get().uri("/api/github/repositories/{username}", nonExistentUser)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.Message").isEqualTo("User does not exist");
    }

    @Test
    public void whenInvalidAcceptHeader_thenStatus406() {
        String username = "validUser";
        webTestClient.get().uri("/api/github/repositories/{username}", username)
                .accept(MediaType.APPLICATION_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE) // Using HttpStatus to check for 406
                .expectBody()
                .jsonPath("$.status").isEqualTo(406)
                .jsonPath("$.Message").isEqualTo("This service produces JSON only");
    }

    @Test
    public void whenGitHubApiError_thenHandleGracefully() {
        // Simulate an error response from GitHub API
        String username = "validUser";
        given(gitHubService.getUserRepos(username)).willThrow(new GitHubApiException("GitHub API error"));

        webTestClient.get().uri("/api/github/repositories/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.Message").isEqualTo("GitHub API error");
    }


    @Test
    public void whenValidUserWithNoRepos_thenEmptyListReturned() {
        String validUser = "validUserWithNoRepos";
        given(gitHubService.getUserRepos(validUser)).willReturn((Mono<List<RepositoryInfo>>) Collections.emptyList());

        webTestClient.get().uri("/api/github/repositories/{username}", validUser)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    public void whenValidUser_thenCorrectRepositoryStructureReturned() {
        String validUser = "validUser";
        List<BranchInfo> mockBranches = Arrays.asList(
                new BranchInfo("master", "sha123"),
                new BranchInfo("dev", "sha456")
        );

        List<RepositoryInfo> mockRepos = Arrays.asList(
                new RepositoryInfo("Repo1", "validUser", mockBranches),
                new RepositoryInfo("Repo2", "validUser", mockBranches)
        );

        given(gitHubService.getUserRepos(validUser)).willReturn((Mono<List<RepositoryInfo>>) mockRepos);

        webTestClient.get().uri("/api/github/repositories/{username}", validUser)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Repo1")
                .jsonPath("$[0].ownerLogin").isEqualTo("validUser")
                .jsonPath("$[0].branches[0].name").isEqualTo("master")
                .jsonPath("$[0].branches[1].name").isEqualTo("dev")
                .jsonPath("$[1].name").isEqualTo("Repo2");
        // Add further assertions as necessary
    }

    @Test
    public void whenRepositoriesIncludeForks_thenForksAreFilteredOut() {
        String validUser = "validUserWithForks";
        // Assume the service method filters out forked repositories
        // Set up mock repositories, some of which are forks

        webTestClient.get().uri("/api/github/repositories/{username}", validUser)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[?(@.isFork==true)]").doesNotExist(); // Assuming each repo has 'isFork' attribute
    }

    @Test
    public void whenGitHubApiRateLimitExceeded_thenHandleError() {
        String validUser = "popularUser";
        // Simulate GitHub API rate limit exceeded
        given(gitHubService.getUserRepos(validUser))
                .willThrow(new GitHubApiRateLimitExceededException("Rate limit exceeded"));

        webTestClient.get().uri("/api/github/repositories/{username}", validUser)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS) // Using HttpStatus to check for 429
                .expectBody()
                .jsonPath("$.status").isEqualTo(429)
                .jsonPath("$.Message").isEqualTo("Rate limit exceeded");
    }
}