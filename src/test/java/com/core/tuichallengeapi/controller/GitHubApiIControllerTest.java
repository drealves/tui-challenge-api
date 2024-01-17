package com.core.tuichallengeapi.controller;

import com.core.tuichallengeapi.model.dto.PaginatedRepositoriesResponseDto;
import com.core.tuichallengeapi.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(GitHubController.class)
public class GitHubApiIControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GitHubService gitHubService;

    @Test
    public void testListUserRepositories() {
        // Mock the GitHubService response
        PaginatedRepositoriesResponseDto mockResponse = new PaginatedRepositoriesResponseDto();
        Mockito.when(gitHubService.getRepositoryInfo(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean()))
                .thenReturn(Mono.just(mockResponse));

        // Perform a GET request to the endpoint and validate the response
        webTestClient.get()
                .uri("/api/v1/github/users/{username}/repositories?page={page}&size={size}&includeForks={includeForks}", "testUser", 1, 5, false)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.currentPage").isEqualTo(mockResponse.getCurrentPage())
                .jsonPath("$.pageSize").isEqualTo(mockResponse.getPageSize())
                .jsonPath("$.totalElements").isEqualTo(mockResponse.getTotalElements())
                .jsonPath("$.totalPages").isEqualTo(mockResponse.getTotalPages())
                .jsonPath("$.repositories").isEqualTo(mockResponse.getRepositories());
    }
    @Test
    public void testListUserRepositoriesWithInvalidPageSize() {
        // Perform a GET request with an invalid page size
        webTestClient.get()
                .uri("/api/v1/github/users/{username}/repositories?page={page}&size={size}&includeForks={includeForks}", "testUser", 1, 101, false)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Page size must be between 1 and 100");
    }

    @Test
    public void testListUserRepositoriesWithInvalidPageNumber() {
        // Perform a GET request with an invalid page number
        webTestClient.get()
                .uri("/api/v1/github/users/{username}/repositories?page={page}&size={size}&includeForks={includeForks}", "testUser", 0, 5, false)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Page number must be greater than 0");
    }
}