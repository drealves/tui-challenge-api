package com.core.tuichallengeapi.integration;

import com.core.tuichallengeapi.client.GitHubClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GitHubIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GitHubClient gitHubClient;

    @Test
    public void testAcceptApplicationXml() {
        webTestClient.get().uri("/api/v1/github/repositories/mockUser")
                .header("Accept", "application/xml")
                .exchange()
                .expectStatus().isEqualTo(406)
                .expectBody()
                .jsonPath("$.status").isEqualTo(406)
                .jsonPath("$.message").isEqualTo("XML format not supported");
    }


    // Additional tests for other scenarios like error handling, etc.
}
