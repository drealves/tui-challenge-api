package com.core.tuichallengeapi.service;

import com.core.tuichallengeapi.config.GitHubApiPropertiesConfig;
import com.core.tuichallengeapi.dto.BranchInfo;
import com.core.tuichallengeapi.dto.CommitInfo;
import com.core.tuichallengeapi.dto.Owner;
import com.core.tuichallengeapi.dto.RepositoryInfo;
import com.core.tuichallengeapi.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GitHubServiceTest {

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

    private GitHubService gitHubService;

    @BeforeEach
    public void setup() {

        when(gitHubApiPropertiesConfig.getBaseUrl()).thenReturn("https://api.github.com");
        when(gitHubApiPropertiesConfig.getToken()).thenReturn("token");

        // Mocking WebClient.Builder to return the mock WebClient
        // Correcting the matchers here
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.defaultHeader(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        gitHubService = new GitHubService(webClientBuilder, gitHubApiPropertiesConfig); // Assuming the constructor accepts a WebClient

        // Setup for WebClient
        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
        Mockito.when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);


    }

    @Test
    public void getBranchesForRepositoryShouldReturnBranches() {
        // Mocking WebClient behavior
        // Assuming the setup for WebClient mocks has already been done as in previous tests
        Mockito.when(gitHubService.getLastCommitSha(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just("mockCommitSha"));

        // Setting up mocked data for BranchInfo
        BranchInfo branchInfo = new BranchInfo();
        branchInfo.setName("main"); // Example branch name

        // Mocking getLastCommitSha to return a specific commit SHA for the branch
        String mockLastCommitSha = "abc123";
        Mockito.when(gitHubService.getLastCommitSha("owner", "repositoryName", "main"))
                .thenReturn(Mono.just(mockLastCommitSha));

        // Setting up the responseSpec to return the mocked BranchInfo
        given(responseSpec.bodyToFlux(BranchInfo.class)).willReturn(Flux.just(branchInfo));

        // Executing the test
        StepVerifier.create(gitHubService.getBranchesForRepository("owner", "repositoryName"))
                .expectNextMatches(branchMap ->
                                "main".equals(branchMap.get("branchName")) &&
                                        mockLastCommitSha.equals(branchMap.get("lastCommitSha"))
                        // Additional validations can be added here
                )
                .verifyComplete();
    }

}
