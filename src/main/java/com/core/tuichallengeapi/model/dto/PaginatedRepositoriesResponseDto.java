package com.core.tuichallengeapi.model.dto;

import java.util.List;

public class PaginatedRepositoriesResponseDto extends RepositoriesResponseDto {

    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public PaginatedRepositoriesResponseDto() {
    }

    public PaginatedRepositoriesResponseDto(List<RepositoryInfoDto> repositories, int currentPage, int pageSize, long totalElements, int totalPages) {
        super(repositories);
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}