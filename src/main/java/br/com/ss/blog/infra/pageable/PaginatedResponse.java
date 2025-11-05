package br.com.ss.blog.infra.pageable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A stable, generic DTO for paginated API responses.
 *
 * @param content The list of items for the current page.
 * @param currentPage The current page number (0-indexed).
 * @param totalItems The total number of items across all pages.
 * @param totalPages The total number of pages available.
 */
public record PaginatedResponse<T>(
    List<T> content,
    @JsonProperty("current_page") int currentPage,
    @JsonProperty("total_items") long totalItems,
    @JsonProperty("total_pages") int totalPages
) {}