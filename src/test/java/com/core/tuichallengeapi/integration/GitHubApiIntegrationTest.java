package com.core.tuichallengeapi.integration;

import com.core.tuichallengeapi.client.GitHubClient;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import com.core.tuichallengeapi.model.BranchInfo;
import com.core.tuichallengeapi.model.CommitInfo;
import com.core.tuichallengeapi.model.Owner;
import com.core.tuichallengeapi.model.RepositoryInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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

    @Test
    public void testAcceptApplicationUserNotFound() {

        given(gitHubClient.getRepositories("mockUser", 1, 5))
                .willThrow(new UserNotFoundException("User not found"));

        webTestClient.get().uri("/api/v1/github/repositories/mockUser")
                .header("Accept", "application/Json")
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("User not found");
    }


    @Test
    public void testGetRepositoriesForMockUser() {
        // Mock data for repositories
        List<RepositoryInfo> mockRepositories = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            RepositoryInfo repo = new RepositoryInfo();
            repo.setName("MockRepo" + i);
            repo.setOwner(new Owner("mockUser"));
            repo.setFork(false);
            mockRepositories.add(repo);
        }

        // Mock data for branches
        List<BranchInfo> mockBranches = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            BranchInfo branch = new BranchInfo();
            branch.setName("master" + i);
            List<CommitInfo> commits = new ArrayList<>();
            commits.add(new CommitInfo("commitSha" + i)); // Adjust according to your CommitInfo class structure
            branch.setCommits(commits);
            // Add more fields as needed

            mockBranches.add(branch);

            // Mock the GitHubClient calls
            given(gitHubClient.getRepositories("mockUser", 1, 5)).willReturn(Flux.fromIterable(mockRepositories));
            given(gitHubClient.getBranchesForRepository(anyString(), anyString())).willReturn(Flux.fromIterable(mockBranches));
            given(gitHubClient.getLastCommitSha(anyString(), anyString(), anyString())).willReturn(Mono.just("commitSha"));

            // Perform the test
            webTestClient.get()
                    .uri("/api/v1/github/repositories/mockUser")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody()
                    .jsonPath("$.repositories[0].name").isEqualTo("MockRepo0");
            // Add more assertions as needed
        }

    }
}