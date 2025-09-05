package com.meli.technical.exam.api.products.domain.service.analysis.strategy;

import com.meli.technical.exam.api.products.domain.service.analysis.ProductStats;
import reactor.core.publisher.Mono;

/**
 * Strategy interface for product analysis operations.
 * Each implementation handles a specific aspect of product comparison analysis.
 */
public interface ProductAnalysisStrategy<T> {
    
    /**
     * Performs analysis based on the provided product statistics.
     * 
     * @param productStats Pre-calculated statistics from all products
     * @return Mono containing the analysis result
     */
    Mono<T> analyze(ProductStats productStats);
    
    /**
     * Returns the type of analysis this strategy performs.
     * Used for logging and debugging purposes.
     */
    String getAnalysisType();
}