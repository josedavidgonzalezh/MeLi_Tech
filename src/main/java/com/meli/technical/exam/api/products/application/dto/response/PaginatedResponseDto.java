package com.meli.technical.exam.api.products.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponseDto<T> {
    
    @JsonProperty("content")
    private List<T> content;
    
    @JsonProperty("page")
    private int page;
    
    @JsonProperty("size")
    private int size;
    
    @JsonProperty("totalElements")
    private long totalElements;
    
    @JsonProperty("totalPages")
    private int totalPages;
    
    @JsonProperty("hasNext")
    private boolean hasNext;
    
    @JsonProperty("hasPrevious")
    private boolean hasPrevious;

    public PaginatedResponseDto(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.hasNext = page < totalPages - 1;
        this.hasPrevious = page > 0;
    }
}