package com.meli.technical.exam.api.products.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public class ComparisonResponseDto {
    
    @JsonProperty("products")
    private List<ProductDto> products;
    
    @JsonProperty("totalProducts")
    private int totalProducts;
    
    @JsonProperty("comparisonTimestamp")
    private Instant comparisonTimestamp;
    
    @JsonProperty("requestedIds")
    private List<String> requestedIds;

    public ComparisonResponseDto() {
        this.comparisonTimestamp = Instant.now();
    }

    public ComparisonResponseDto(List<ProductDto> products, List<String> requestedIds) {
        this.products = products;
        this.totalProducts = products != null ? products.size() : 0;
        this.requestedIds = requestedIds;
        this.comparisonTimestamp = Instant.now();
    }

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
        this.totalProducts = products != null ? products.size() : 0;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Instant getComparisonTimestamp() {
        return comparisonTimestamp;
    }

    public void setComparisonTimestamp(Instant comparisonTimestamp) {
        this.comparisonTimestamp = comparisonTimestamp;
    }

    public List<String> getRequestedIds() {
        return requestedIds;
    }

    public void setRequestedIds(List<String> requestedIds) {
        this.requestedIds = requestedIds;
    }
}