package com.core.tuichallengeapi.mapper;

import com.core.tuichallengeapi.model.dto.PaginatedRepositoriesResponseDto;
import com.core.tuichallengeapi.model.dto.RepositoryInfoDto;

import java.util.List;

public class PaginatedRepositoriesResponseMapper {

    public static PaginatedRepositoriesResponseDto toRepositoryInfoDto(
            List<RepositoryInfoDto> listRepositoryInfoDto, int page, int size) {

        long totalElements = listRepositoryInfoDto.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Correct the pagination logic
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, listRepositoryInfoDto.size());

        // Handling the case where startIndex is greater than the size of the list
        if (startIndex >= listRepositoryInfoDto.size()) {
            startIndex = Math.max(listRepositoryInfoDto.size() - 1, 0);
            endIndex = startIndex;
        }

        List<RepositoryInfoDto> paginatedList = listRepositoryInfoDto.subList(startIndex, endIndex);
        return new PaginatedRepositoriesResponseDto(paginatedList, page, size, totalElements, totalPages);
    }
}
