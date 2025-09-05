package com.meli.technical.exam.api.products.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ComparisonResponseDto {
    
    @JsonProperty("products")
    private List<ProductDto> products;
    
    @JsonProperty("totalProducts")
    private int totalProducts;
    
    @JsonProperty("comparisonTimestamp")
    private Instant comparisonTimestamp;
    
    @JsonProperty("requestedIds")
    private List<String> requestedIds;

    public ComparisonResponseDto(List<ProductDto> products, List<String> requestedIds) {
        this.products = products;
        this.totalProducts = products != null ? products.size() : 0;
        this.requestedIds = requestedIds;
        this.comparisonTimestamp = Instant.now();
    }
}