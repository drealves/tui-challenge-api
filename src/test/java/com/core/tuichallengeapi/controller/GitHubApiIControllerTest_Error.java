package com.core.tuichallengeapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(GitHubController.class)
public class GitHubApiIControllerTest_Error {

    @Autowired
    private WebTestClient webTestClient;

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