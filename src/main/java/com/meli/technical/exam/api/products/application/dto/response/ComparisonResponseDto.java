package com.meli.technical.exam.api.products.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class ComparisonResponseDto {
    
    @JsonProperty("products")
    private final List<ProductDto> products;
    
    @JsonProperty("totalProducts")
    private final int totalProducts;
    
    @JsonProperty("requestedIds")
    private final List<String> requestedIds;
    
    @JsonProperty("comparisonTimestamp")
    private final Instant comparisonTimestamp;
    
    // Price Analysis
    @JsonProperty("priceAnalysis")
    private final PriceAnalysisDto priceAnalysis;
    
    // Rating Analysis
    @JsonProperty("ratingAnalysis")
    private final RatingAnalysisDto ratingAnalysis;
    
    // Specification Analysis
    @JsonProperty("specificationAnalysis")
    private final SpecificationAnalysisDto specificationAnalysis;
    
    // Recommendations
    @JsonProperty("recommendations")
    private final List<RecommendationDto> recommendations;
    
    // Summary insights
    @JsonProperty("summary")
    private final ComparisonSummaryDto summary;
    
    @Getter
    @Builder
    public static class PriceAnalysisDto {
        @JsonProperty("cheapestProduct")
        private final ProductDto cheapestProduct;
        
        @JsonProperty("mostExpensiveProduct")
        private final ProductDto mostExpensiveProduct;
        
        @JsonProperty("priceRange")
        private final BigDecimal priceRange;
        
        @JsonProperty("averagePrice")
        private final BigDecimal averagePrice;
        
        @JsonProperty("priceDistribution")
        private final Map<String, BigDecimal> priceDistribution;
    }
    
    @Getter
    @Builder
    public static class RatingAnalysisDto {
        @JsonProperty("bestRatedProduct")
        private final ProductDto bestRatedProduct;
        
        @JsonProperty("lowestRatedProduct")
        private final ProductDto lowestRatedProduct;
        
        @JsonProperty("averageRating")
        private final Double averageRating;
        
        @JsonProperty("ratingDistribution")
        private final Map<String, Double> ratingDistribution;
        
        @JsonProperty("highlyRatedProducts")
        private final List<ProductDto> highlyRatedProducts;
    }
    
    @Getter
    @Builder
    public static class SpecificationAnalysisDto {
        @JsonProperty("commonSpecifications")
        private final Set<String> commonSpecifications;
        
        @JsonProperty("uniqueSpecifications")
        private final Map<String, Set<String>> uniqueSpecifications;
        
        @JsonProperty("specificationComparison")
        private final Map<String, Map<String, String>> specificationComparison;
        
        @JsonProperty("mostFeaturedProduct")
        private final ProductDto mostFeaturedProduct;
    }
    
    @Getter
    @Builder
    public static class RecommendationDto {
        @JsonProperty("type")
        private final String type;
        
        @JsonProperty("title")
        private final String title;
        
        @JsonProperty("description")
        private final String description;
        
        @JsonProperty("recommendedProduct")
        private final ProductDto recommendedProduct;
        
        @JsonProperty("reason")
        private final String reason;
    }
    
    @Getter
    @Builder
    public static class ComparisonSummaryDto {
        @JsonProperty("bestValue")
        private final ProductDto bestValue;
        
        @JsonProperty("bestQuality")
        private final ProductDto bestQuality;
        
        @JsonProperty("budgetOption")
        private final ProductDto budgetOption;
        
        @JsonProperty("insights")
        private final List<String> insights;
        
        @JsonProperty("conclusion")
        private final String conclusion;
    }
}