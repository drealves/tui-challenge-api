package com.core.tuichallengeapi.integration;


import com.core.tuichallengeapi.controller.GitHubController;
import com.core.tuichallengeapi.service.GitHubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@WebFluxTest(GitHubController.class)
public class GitHubApiIntegrationTest {


    @Autowired
    private WebTestClient webTestClient;
    // Example of mocking a service
    @MockBean
    private GitHubService gitHubService;

    private static ClientAndServer mockServer;


    @BeforeAll
    public static void setUp() {
        mockServer = ClientAndServer.startClientAndServer(1080);
    }

    @AfterAll
    public static void stopMockServer() {
        mockServer.stop(); // Stop the mock server after tests are done
    }

    @Test
    public void testListUserRepositories() {
        WebTestClient webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:1080") // Set to MockServer URL
                .build();
        new MockServerClient("localhost", 1080)
                .when(request()
                                .withPath("/api/v1/github/users/teste/repositories")
                                .withQueryStringParameter("page", "1")
                                .withQueryStringParameter("size", "5")
                                .withQueryStringParameter("includeForks", "false")
                                .withMethod("GET")
                                .withHeader("Accept", MediaType.APPLICATION_JSON_VALUE),
                        Times.once())
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json"));
        // Perform a GET request to the endpoint with the mock server
        webTestClient.get()
                .uri("/api/v1/github/users/{username}/repositories?page={page}&size={size}&includeForks={includeForks}", "teste", 1, 5, false)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test
    public void testE2EUserNotFound() {
        WebTestClient webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:1080") // Set to MockServer URL
                .build();
        // Configure MockServer response for the specific request
        new MockServerClient("localhost", 1080)
                .when(request()
                                .withPath("/api/v1/github/users/alalalalalalalala/repositories")
                                .withQueryStringParameter("page", "1")
                                .withQueryStringParameter("size", "5")
                                .withQueryStringParameter("includeForks", "false")
                                .withMethod("GET")
                                .withHeader("Accept", MediaType.APPLICATION_JSON_VALUE),
                        Times.once())
                .respond(response()
                        .withStatusCode(404)
                        .withHeader("Content-Type", "application/json"));

        // Perform a GET request to the endpoint with the MockServer
        webTestClient.get()
                .uri("/api/v1/github/users/{username}/repositories?page={page}&size={size}&includeForks={includeForks}",
                        "alalalalalalalala", 1, 5, false)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void testE2EUserForbidden() {
        WebTestClient webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:1080") // Set to MockServer URL
                .build();
        // Configure MockServer response for the specific request
        new MockServerClient("localhost", 1080)
                .when(request()
                                .withPath("/api/v1/github/users/test/repositories")
                                .withQueryStringParameter("page", "1")
                                .withQueryStringParameter("size", "5")
                                .withQueryStringParameter("includeForks", "false")
                                .withMethod("GET")
                                .withHeader("Accept", MediaType.APPLICATION_JSON_VALUE),
                        Times.once())
                .respond(response()
                        .withStatusCode(403)
                        .withHeader("Content-Type", "application/json"));

        // Perform a GET request to the endpoint with the MockServer
        webTestClient.get()
                .uri("/api/v1/github/users/{username}/repositories?page={page}&size={size}&includeForks={includeForks}",
                        "test", 1, 5, false)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isForbidden();
    }





}

