package com.core.tuichallengeapi.mapper;

import com.core.tuichallengeapi.model.BranchInfo;
import com.core.tuichallengeapi.model.CommitInfo;
import com.core.tuichallengeapi.model.RepositoryInfo;
import com.core.tuichallengeapi.model.dto.BranchInfoDto;
import com.core.tuichallengeapi.model.dto.RepositoryInfoDto;

import java.util.stream.Collectors;

public class GitHubMapper {

    public static RepositoryInfoDto toRepositoryInfoDto(RepositoryInfo repositoryInfo) {
        RepositoryInfoDto dto = new RepositoryInfoDto();
        dto.setName(repositoryInfo.getName());
        dto.setFork(repositoryInfo.isFork());
        dto.setOwner(repositoryInfo.getOwner()); // Assuming Owner info is part of RepositoryInfo
        dto.setBranches(repositoryInfo.getBranches().stream()
                .map(GitHubMapper::toBranchDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public static BranchInfoDto toBranchDto(BranchInfo branchInfo) {
        BranchInfoDto dto = new BranchInfoDto();
        dto.setName(branchInfo.getName());
        // Assuming that BranchInfo contains a list of CommitInfo, we take the first one as the last commit
        if (branchInfo.getCommits() != null && !branchInfo.getCommits().isEmpty()) {
            dto.setLastCommit(toCommitDto(branchInfo.getCommits().get(0)));
        }
        return dto;
    }

    public static String toCommitDto(CommitInfo commitInfo) {
        return commitInfo.getSha();
    }

}