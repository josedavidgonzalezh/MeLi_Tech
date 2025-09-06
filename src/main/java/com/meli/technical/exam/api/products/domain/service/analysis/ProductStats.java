package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import lombok.Builder;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class ProductStats {
    
    // Price statistics
    private final ProductDto cheapestProduct;
    private final ProductDto mostExpensiveProduct;
    private final BigDecimal totalPrice;
    private final BigDecimal averagePrice;
    private final BigDecimal priceRange;
    
    // Rating statistics  
    private final ProductDto bestRatedProduct;
    private final ProductDto lowestRatedProduct;
    private final Double totalRating;
    private final Double averageRating;
    private final Double ratingRange;
    
    // Specification statistics
    private final Set<String> allSpecificationKeys;
    private final Set<String> commonSpecifications;
    private final ProductDto mostFeaturedProduct;
    private final Map<String, Set<String>> productSpecifications;
    
    // General statistics
    private final int totalProducts;
    private final List<ProductDto> allProducts;
    private final List<ProductDto> highlyRatedProducts;
    
    // Calculated collections
    private final Map<String, BigDecimal> priceDistribution;
    private final Map<String, Double> ratingDistribution;

    public static Mono<ProductStats> fromProductsReactive(Flux<ProductDto> productFlux) {
        return ProductStatsCollector.collectStatsReactive(productFlux);
    }
    
    public static ProductStats createEmpty() {
        return ProductStats.builder()
                .totalProducts(0)
                .allProducts(Collections.emptyList())
                .totalPrice(BigDecimal.ZERO)
                .averagePrice(BigDecimal.ZERO)
                .priceRange(BigDecimal.ZERO)
                .totalRating(0.0)
                .averageRating(0.0)
                .ratingRange(0.0)
                .allSpecificationKeys(Collections.emptySet())
                .commonSpecifications(Collections.emptySet())
                .productSpecifications(Collections.emptyMap())
                .highlyRatedProducts(Collections.emptyList())
                .priceDistribution(Collections.emptyMap())
                .ratingDistribution(Collections.emptyMap())
                .build();
    }
    
    public boolean isEmpty() {
        return totalProducts == 0;
    }
    
    public boolean hasSingleProduct() {
        return totalProducts == 1;
    }
}