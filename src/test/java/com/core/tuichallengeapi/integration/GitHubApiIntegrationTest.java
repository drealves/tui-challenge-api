package com.core.tuichallengeapi.integration;


import com.core.tuichallengeapi.controller.GitHubController;
import com.core.tuichallengeapi.model.dto.PaginatedRepositoriesResponseDto;
import com.core.tuichallengeapi.model.dto.RepositoryInfoDto;
import com.core.tuichallengeapi.service.GitHubService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.log.model.LogEntry;
import org.mockserver.matchers.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@WebFluxTest(GitHubController.class)
public class GitHubApiIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static ClientAndServer mockServer;

    @Mock
    private GitHubService gitHubService;


    @BeforeAll
    public static void setUpMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1080); // Start the mock server on port 1080

        // Define mock responses
        new MockServerClient("localhost", 1080)
                .when(request()
                                .withPath("/api/v1/github/users/testUser/repositories?page=1&size=5&includeForks=false")
                                .withMethod("GET")
                                .withHeader("Accept", "application/json"),
                        Times.once())
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"currentPage\": 1, \"pageSize\": 5, \"totalElements\": 10, \"totalPages\": 2, \"repositories\": []}"));

    }

    @AfterAll
    public static void stopMockServer() {
        mockServer.stop(); // Stop the mock server after tests are done
    }

    @Test
    public void testListUserRepositories() {
        // Perform a GET request to the endpoint with the mock server
        webTestClient.get()
                .uri("/api/v1/github/users/{username}/repositories?page={page}&size={size}&includeForks={includeForks}", "testUser", 1, 5, false)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.currentPage").isEqualTo(1)
                .jsonPath("$.pageSize").isEqualTo(5)
                .jsonPath("$.totalElements").isEqualTo(0)
                .jsonPath("$.totalPages").isEqualTo(0);
    }

}

