package com.core.tuichallengeapi.client;

import com.core.tuichallengeapi.config.GitHubApiPropertiesConfig;
import com.core.tuichallengeapi.exception.ForbiddenException;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import com.core.tuichallengeapi.model.BranchInfo;
import com.core.tuichallengeapi.model.RepositoryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest()
public class GitHubClientTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private GitHubApiPropertiesConfig gitHubApiPropertiesConfig;

    @MockBean
    private GitHubClient gitHubClient;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader(anyString(), anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
    }


    @Test
    public void getRepositories_whenUserExists_returnsFluxOfRepositoryInfo() {
        // Arrange
        String username = "testUser";
        RepositoryInfo repo1 = new RepositoryInfo();
        RepositoryInfo repo2 = new RepositoryInfo();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(RepositoryInfo.class)).thenReturn(Flux.just(repo1, repo2));
        when(gitHubClient.getRepositories(anyString(), anyInt())).thenReturn(Flux.just(repo1, repo2));

        // Act
        Flux<RepositoryInfo> result = gitHubClient.getRepositories(username, 1);

        // Assert
        assertThat(result.collectList().block()).containsExactly(repo1, repo2);
    }

    @Test
    public void getRepositories_whenUserNotFound_throwsUserNotFoundException() {
        /// Arrange
        String username = "nonExistentUser";

        // Ensure that all necessary methods are mocked to return non-null values
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Mock the behavior when a 404 status is encountered
        when(responseSpec.onStatus(eq(HttpStatus.NOT_FOUND::equals), any()))
                .thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(RepositoryInfo.class))
                .thenReturn(Flux.error(new UserNotFoundException("User not found")));
        when(gitHubClient.getRepositories(anyString(), anyInt()))
                .thenReturn(Flux.error(new UserNotFoundException("User not found")));

        Flux<RepositoryInfo> result = gitHubClient.getRepositories(username, 1);

        // Act & Assert with StepVerifier
        StepVerifier.create(result)
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    public void getRepositories_whenAccessIsForbidden_throwsForbiddenException() {
        // Arrange
        String username = "forbiddenUser";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(RepositoryInfo.class))
                .thenReturn(Flux.error(new ForbiddenException("User not found")));
        when(gitHubClient.getRepositories(anyString(), anyInt()))
                .thenReturn(Flux.error(new ForbiddenException("User not found")));

        StepVerifier.create(gitHubClient.getRepositories(username, 1))
                .expectError(ForbiddenException.class)
                .verify();
    }

    @Test
    public void getBranchesForRepository_whenUserExists_returnsFluxOfRepositoryInfo() {
        // Arrange
        String ownerName = "testUser";
        String repoName = "testRepo";
        BranchInfo branchInfo = new BranchInfo();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(BranchInfo.class)).thenReturn(Flux.just(branchInfo));
        when(gitHubClient.getBranchesForRepository(ownerName, repoName)).thenReturn(Flux.just(branchInfo));

        // Act
        Flux<BranchInfo> result = gitHubClient.getBranchesForRepository(ownerName, repoName);

        // Assert
        assertThat(result.collectList().block()).containsExactly(branchInfo);
    }


    @Test
    public void getLastCommitSha_whenUserExists_returnsFluxOfRepositoryInfo() {
        // Arrange
        String ownerName = "testUser";
        String repoName = "testRepo";
        String branchName = "master";
        String commit = "0000000";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(commit));
        when(gitHubClient.getLastCommitSha(ownerName, repoName, branchName)).thenReturn(Mono.just(commit));

        // Assert
        StepVerifier.create(gitHubClient.getLastCommitSha(ownerName, repoName, branchName))
                .expectNext(commit) // Expect the specific String value
                .verifyComplete();
    }

}
