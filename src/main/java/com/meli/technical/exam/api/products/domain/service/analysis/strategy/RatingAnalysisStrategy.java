package com.meli.technical.exam.api.products.domain.service.analysis.strategy;

import com.meli.technical.exam.api.products.application.dto.response.comparison.RatingAnalysisDto;
import com.meli.technical.exam.api.products.domain.service.analysis.ProductStats;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class RatingAnalysisStrategy implements ProductAnalysisStrategy<RatingAnalysisDto> {
    
    @Override
    public Mono<RatingAnalysisDto> analyze(ProductStats productStats) {
        if (productStats.isEmpty()) {
            return Mono.just(createEmptyRatingAnalysis());
        }
        
        return Mono.fromSupplier(() -> RatingAnalysisDto.builder()
                .bestRatedProduct(productStats.getBestRatedProduct())
                .lowestRatedProduct(productStats.getLowestRatedProduct())
                .averageRating(productStats.getAverageRating())
                .ratingDistribution(productStats.getRatingDistribution())
                .highlyRatedProducts(productStats.getHighlyRatedProducts())
                .build());
    }
    
    @Override
    public String getAnalysisType() {
        return "RATING_ANALYSIS";
    }
    
    private RatingAnalysisDto createEmptyRatingAnalysis() {
        return RatingAnalysisDto.builder()
                .bestRatedProduct(null)
                .lowestRatedProduct(null)
                .averageRating(0.0)
                .ratingDistribution(Collections.emptyMap())
                .highlyRatedProducts(Collections.emptyList())
                .build();
    }
}