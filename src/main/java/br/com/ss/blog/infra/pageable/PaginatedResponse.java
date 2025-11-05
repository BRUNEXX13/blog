package br.com.ss.blog.infra.pageable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public record PaginatedResponse<T>(List<T> content, int page, long totalElements,
                                   int totalPages) implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public PaginatedResponse(
            @JsonProperty("content") List<T> content,
            @JsonProperty("page") int page,
            @JsonProperty("totalElements") long totalElements,
            @JsonProperty("totalPages") int totalPages) {
        this.content = Objects.requireNonNull(content, "content must not be null");
        this.page = page;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
