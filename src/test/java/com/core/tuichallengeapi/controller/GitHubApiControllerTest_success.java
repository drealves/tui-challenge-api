package com.core.tuichallengeapi.controller;

import com.core.tuichallengeapi.model.dto.BranchInfoDto;
import com.core.tuichallengeapi.model.dto.OwnerDto;
import com.core.tuichallengeapi.model.dto.PaginatedRepositoriesResponseDto;
import com.core.tuichallengeapi.model.dto.RepositoryInfoDto;
import com.core.tuichallengeapi.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitHubApiControllerTest_success {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GitHubService gitHubService;


    @Test
    public void estListUserRepositoriesSuccess() {

        PaginatedRepositoriesResponseDto paginatedRepositoriesResponseDto = new PaginatedRepositoriesResponseDto();
        paginatedRepositoriesResponseDto.setRepositories(Collections.singletonList(
                new RepositoryInfoDto("dre", new OwnerDto("userX"), false,
                        Collections.singletonList(new BranchInfoDto("master", "00000")))));
        paginatedRepositoriesResponseDto.setCurrentPage(1);
        paginatedRepositoriesResponseDto.setPageSize(10);
        paginatedRepositoriesResponseDto.setTotalPages(1);
        paginatedRepositoriesResponseDto.setTotalElements(10);

        given(gitHubService.getRepositoryInfo(any(), anyInt(), anyInt(), anyBoolean()))
                .willReturn(Mono.just(paginatedRepositoriesResponseDto));

        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/v1/github/users/{username}/repositories", Collections.singletonMap("username",  "userX"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.currentPage").isEqualTo(1)
                .jsonPath("$.pageSize").isEqualTo(10)
                .jsonPath("$.repositories").isNotEmpty();
    }

}
