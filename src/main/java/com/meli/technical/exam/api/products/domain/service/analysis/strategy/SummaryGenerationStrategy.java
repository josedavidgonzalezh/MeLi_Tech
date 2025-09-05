package com.meli.technical.exam.api.products.domain.service.analysis.strategy;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.ComparisonSummaryDto;
import com.meli.technical.exam.api.products.domain.service.analysis.ProductStats;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class SummaryGenerationStrategy implements ProductAnalysisStrategy<ComparisonSummaryDto> {
    
    @Override
    public Mono<ComparisonSummaryDto> analyze(ProductStats productStats) {
        if (productStats.isEmpty()) {
            return Mono.just(createEmptySummary());
        }
        
        return Mono.fromSupplier(() -> {
            ProductDto bestValue = calculateBestValue(productStats.getAllProducts());
            ProductDto bestQuality = productStats.getBestRatedProduct();
            ProductDto budgetOption = productStats.getCheapestProduct();
            
            List<String> insights = generateInsights(productStats);
            String conclusion = generateConclusion(productStats, bestValue, bestQuality, budgetOption);
            
            return ComparisonSummaryDto.builder()
                    .bestValue(bestValue)
                    .bestQuality(bestQuality)
                    .budgetOption(budgetOption)
                    .insights(insights)
                    .conclusion(conclusion)
                    .build();
        });
    }
    
    @Override
    public String getAnalysisType() {
        return "SUMMARY_GENERATION";
    }
    
    private ProductDto calculateBestValue(List<ProductDto> products) {
        return products.stream()
                .max(Comparator.comparing(product -> {
                    double normalizedRating = product.getRating() / 5.0;
                    double maxPrice = products.stream()
                            .mapToDouble(p -> p.getPrice().doubleValue())
                            .max().orElse(1.0);
                    double normalizedPrice = product.getPrice().doubleValue() / maxPrice;
                    return normalizedRating / normalizedPrice;
                }))
                .orElse(null);
    }
    
    private List<String> generateInsights(ProductStats productStats) {
        List<String> insights = new ArrayList<>();
        
        // Price insights
        BigDecimal priceRange = productStats.getPriceRange();
        insights.add(String.format("Price range: $%.2f across all products", priceRange));
        
        // Rating insights
        double ratingRange = productStats.getRatingRange();
        insights.add(String.format("Rating variance: %.1f points between highest and lowest rated", ratingRange));
        
        // Quality insights
        long highRatedCount = productStats.getHighlyRatedProducts().size();
        if (highRatedCount > 0) {
            insights.add(String.format("%d out of %d products are highly rated (4.5+ stars)",
                    highRatedCount, productStats.getTotalProducts()));
        }
        
        // Specification insights
        int commonSpecsCount = productStats.getCommonSpecifications().size();
        int totalSpecsCount = productStats.getAllSpecificationKeys().size();
        
        if (commonSpecsCount > 0) {
            insights.add(String.format("Products share %d common specifications out of %d total features",
                    commonSpecsCount, totalSpecsCount));
        }
        
        return insights;
    }
    
    private String generateConclusion(ProductStats productStats, ProductDto bestValue,
                                    ProductDto bestQuality, ProductDto budgetOption) {
        if (productStats.hasSingleProduct()) {
            return "Only one product available for comparison.";
        }
        
        StringBuilder conclusion = new StringBuilder();
        
        if (bestValue != null && bestQuality != null && budgetOption != null) {
            if (bestValue.equals(bestQuality) && bestValue.equals(budgetOption)) {
                conclusion.append(String.format("%s stands out as the clear winner across all categories.",
                        bestValue.getName()));
            } else {
                conclusion.append("Each product has its strengths: ");
                conclusion.append(String.format("%s for value, ", bestValue.getName()));
                conclusion.append(String.format("%s for quality, ", bestQuality.getName()));
                conclusion.append(String.format("and %s for budget-conscious buyers.", budgetOption.getName()));
            }
        }
        
        return conclusion.toString();
    }
    
    private ComparisonSummaryDto createEmptySummary() {
        return ComparisonSummaryDto.builder()
                .bestValue(null)
                .bestQuality(null)
                .budgetOption(null)
                .insights(new ArrayList<>())
                .conclusion("No products available for comparison.")
                .build();
    }
}