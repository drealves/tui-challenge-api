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

        // Mocking the WebClient chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Mockito.<Function<UriBuilder, URI>>any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        // Correctly stubbing onStatus on the ResponseSpec
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);


        gitHubService = new GitHubService(webClientBuilder, gitHubApiPropertiesConfig);

    }

    @Test
    public void getRepositoryInfoShouldReturnRepositoryDetails() {
        // Creating a mock Owner object
        Owner mockOwner = new Owner();
        mockOwner.setLogin("username");

        RepositoryInfo mockRepo = new RepositoryInfo();
        mockRepo.setName("testRepo");
        mockRepo.setOwner(mockOwner);
        // Mock other necessary properties of mockRepo

        when(responseSpec.bodyToFlux(RepositoryInfo.class)).thenReturn(Flux.just(mockRepo));

        // Mocking the WebClient behavior for fetching branch information
        // This should be adapted based on how your application fetches branch information
        // For example, if it makes another WebClient call, you should mock that call as well

        // Assuming branch information is fetched in a similar manner:
        BranchInfo mockBranch = new BranchInfo();
        mockBranch.setName("main");
        // Mock other necessary properties of mockBranch

        when(responseSpec.bodyToFlux(BranchInfo.class)).thenReturn(Flux.just(mockBranch));

        // Mock the getLastCommitSha method if needed
        // ...

        // Execute the test
        Mono<List<Map<String, Object>>> result = gitHubService.getRepositoryInfo("username", 1, 5);

        StepVerifier.create(result)
                .expectNextMatches(repositories -> {
                    // Verify the size of the list and contents of the map
                    return repositories.size() == 1 &&
                            "testRepo".equals(repositories.get(0).get("repositoryName")) &&
                            // Add more checks as needed, like verifying branches and commit SHAs
                            true;
                })
                .verifyComplete();
    }


}
