package com.core.tuichallengeapi.service;

import com.core.tuichallengeapi.client.GitHubClient;
import com.core.tuichallengeapi.dto.BranchInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    private final GitHubClient gitHubClient;

    @Autowired
    public GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public Flux<Map<String, Object>> getBranchesForRepository(String owner, String repositoryName) {
        return gitHubClient.getBranchesForRepository(owner, repositoryName)
                .flatMap(branchInfo -> getBranchDetails(owner, repositoryName, branchInfo));
    }

    private Mono<Map<String, Object>> getBranchDetails(String owner, String repositoryName, BranchInfo branchInfo) {
        return gitHubClient.getLastCommitSha(owner, repositoryName, branchInfo.getName())
                .map(lastCommitSha -> {
                    Map<String, Object> branchMap = new HashMap<>();
                    branchMap.put("branchName", branchInfo.getName());
                    branchMap.put("lastCommitSha", lastCommitSha);
                    return branchMap;
                });
    }

    public Mono<List<Map<String, Object>>> getRepositoryInfo(String username, int page, int size) {
        return gitHubClient.getRepositories(username, page, size)
                .flatMap(repositoryInfo ->
                        getBranchesForRepository(repositoryInfo.getOwner().getLogin(), repositoryInfo.getName())
                                .collectList()
                                .map(branches -> {
                                    Map<String, Object> repositoryDetails = new HashMap<>();
                                    repositoryDetails.put("repositoryName", repositoryInfo.getName());
                                    repositoryDetails.put("ownerLogin", repositoryInfo.getOwner().getLogin());
                                    repositoryDetails.put("branches", branches);
                                    return repositoryDetails;
                                })
                )
                .collectList();
    }
}



