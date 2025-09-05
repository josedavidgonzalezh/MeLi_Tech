package com.meli.technical.exam.api.products.domain.exception;

import java.util.List;

public class ProductComparisonException extends RuntimeException {
    
    private final List<String> requestedProductIds;
    private final List<String> foundProductIds;
    
    public ProductComparisonException(String message, List<String> requestedProductIds, List<String> foundProductIds) {
        super(message);
        this.requestedProductIds = requestedProductIds;
        this.foundProductIds = foundProductIds;
    }
    
    public ProductComparisonException(String message, List<String> requestedProductIds) {
        super(message);
        this.requestedProductIds = requestedProductIds;
        this.foundProductIds = List.of();
    }
    
    public List<String> getRequestedProductIds() {
        return requestedProductIds;
    }
    
    public List<String> getFoundProductIds() {
        return foundProductIds;
    }
}