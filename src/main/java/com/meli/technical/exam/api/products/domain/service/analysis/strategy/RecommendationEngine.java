package com.meli.technical.exam.api.products.domain.service.analysis.strategy;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.RecommendationDto;
import com.meli.technical.exam.api.products.domain.service.analysis.ProductStats;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class RecommendationEngine implements ProductAnalysisStrategy<List<RecommendationDto>> {
    
    @Override
    public Mono<List<RecommendationDto>> analyze(ProductStats productStats) {
        if (productStats.isEmpty()) {
            return Mono.just(new ArrayList<>());
        }
        
        return Flux.concat(
                generateBestValueRecommendation(productStats),
                generateBudgetRecommendation(productStats),
                generatePremiumRecommendation(productStats)
        )
        .collectList();
    }
    
    @Override
    public String getAnalysisType() {
        return "RECOMMENDATION_ENGINE";
    }
    
    private Mono<RecommendationDto> generateBestValueRecommendation(ProductStats productStats) {
        return Mono.fromSupplier(() -> {
            ProductDto bestValue = calculateBestValue(productStats.getAllProducts());
            if (bestValue == null) {
                return null;
            }
            
            return RecommendationDto.builder()
                    .type("BEST_VALUE")
                    .title("Best Value for Money")
                    .description("This product offers the best balance of price, quality, and features")
                    .recommendedProduct(bestValue)
                    .reason("Optimal price-to-rating ratio with comprehensive features")
                    .build();
        })
        .filter(recommendation -> recommendation != null);
    }
    
    private Mono<RecommendationDto> generateBudgetRecommendation(ProductStats productStats) {
        return Mono.fromSupplier(() -> {
            ProductDto cheapest = productStats.getCheapestProduct();
            if (cheapest == null) {
                return null;
            }
            
            return RecommendationDto.builder()
                    .type("BUDGET_FRIENDLY")
                    .title("Most Affordable Option")
                    .description("Best choice if budget is your primary concern")
                    .recommendedProduct(cheapest)
                    .reason("Lowest price among compared products")
                    .build();
        })
        .filter(recommendation -> recommendation != null);
    }
    
    private Mono<RecommendationDto> generatePremiumRecommendation(ProductStats productStats) {
        return Mono.fromSupplier(() -> {
            ProductDto bestRated = productStats.getBestRatedProduct();
            if (bestRated == null) {
                return null;
            }
            
            return RecommendationDto.builder()
                    .type("PREMIUM_CHOICE")
                    .title("Highest Quality")
                    .description("Top-rated product with the best customer satisfaction")
                    .recommendedProduct(bestRated)
                    .reason("Highest customer rating among compared products")
                    .build();
        })
        .filter(recommendation -> recommendation != null);
    }
    
    private ProductDto calculateBestValue(List<ProductDto> products) {
        return products.stream()
                .max(Comparator.comparing(product -> {
                    // Value score = (rating / max_rating) / (price / max_price)
                    double normalizedRating = product.getRating() / 5.0;
                    double maxPrice = products.stream()
                            .mapToDouble(p -> p.getPrice().doubleValue())
                            .max().orElse(1.0);
                    double normalizedPrice = product.getPrice().doubleValue() / maxPrice;
                    return normalizedRating / normalizedPrice;
                }))
                .orElse(null);
    }
}