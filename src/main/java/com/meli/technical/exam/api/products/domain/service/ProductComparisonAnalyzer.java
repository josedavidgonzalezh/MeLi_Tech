package com.meli.technical.exam.api.products.domain.service;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.response.ComparisonResponseDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductComparisonAnalyzer {
    
    public ComparisonResponseDto analyzeProducts(List<ProductDto> products, List<String> requestedIds) {
        if (products == null || products.isEmpty()) {
            return createEmptyResponse(requestedIds);
        }
        
        return ComparisonResponseDto.builder()
                .products(products)
                .totalProducts(products.size())
                .requestedIds(requestedIds)
                .comparisonTimestamp(Instant.now())
                .priceAnalysis(analyzePrices(products))
                .ratingAnalysis(analyzeRatings(products))
                .specificationAnalysis(analyzeSpecifications(products))
                .recommendations(generateRecommendations(products))
                .summary(generateSummary(products))
                .build();
    }
    
    private ComparisonResponseDto.PriceAnalysisDto analyzePrices(List<ProductDto> products) {
        ProductDto cheapest = products.stream()
                .min(Comparator.comparing(ProductDto::getPrice))
                .orElse(null);
        
        ProductDto mostExpensive = products.stream()
                .max(Comparator.comparing(ProductDto::getPrice))
                .orElse(null);
        
        BigDecimal priceRange = mostExpensive != null && cheapest != null
                ? mostExpensive.getPrice().subtract(cheapest.getPrice())
                : BigDecimal.ZERO;
        
        BigDecimal averagePrice = products.stream()
                .map(ProductDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(products.size()), 2, RoundingMode.HALF_UP);
        
        Map<String, BigDecimal> priceDistribution = products.stream()
                .collect(Collectors.toMap(
                        ProductDto::getName,
                        ProductDto::getPrice
                ));
        
        return ComparisonResponseDto.PriceAnalysisDto.builder()
                .cheapestProduct(cheapest)
                .mostExpensiveProduct(mostExpensive)
                .priceRange(priceRange)
                .averagePrice(averagePrice)
                .priceDistribution(priceDistribution)
                .build();
    }
    
    private ComparisonResponseDto.RatingAnalysisDto analyzeRatings(List<ProductDto> products) {
        ProductDto bestRated = products.stream()
                .max(Comparator.comparing(ProductDto::getRating))
                .orElse(null);
        
        ProductDto lowestRated = products.stream()
                .min(Comparator.comparing(ProductDto::getRating))
                .orElse(null);
        
        Double averageRating = products.stream()
                .mapToDouble(ProductDto::getRating)
                .average()
                .orElse(0.0);
        
        Map<String, Double> ratingDistribution = products.stream()
                .collect(Collectors.toMap(
                        ProductDto::getName,
                        ProductDto::getRating
                ));
        
        List<ProductDto> highlyRated = products.stream()
                .filter(p -> p.getRating() >= 4.5)
                .collect(Collectors.toList());
        
        return ComparisonResponseDto.RatingAnalysisDto.builder()
                .bestRatedProduct(bestRated)
                .lowestRatedProduct(lowestRated)
                .averageRating(Math.round(averageRating * 10.0) / 10.0)
                .ratingDistribution(ratingDistribution)
                .highlyRatedProducts(highlyRated)
                .build();
    }
    
    private ComparisonResponseDto.SpecificationAnalysisDto analyzeSpecifications(List<ProductDto> products) {
        Set<String> allSpecKeys = products.stream()
                .flatMap(p -> p.getSpecifications().stream())
                .map(spec -> spec.getKey())
                .collect(Collectors.toSet());
        
        Set<String> commonSpecs = allSpecKeys.stream()
                .filter(specKey -> products.stream()
                        .allMatch(product -> product.getSpecifications().stream()
                                .anyMatch(spec -> spec.getKey().equals(specKey))))
                .collect(Collectors.toSet());
        
        Map<String, Set<String>> uniqueSpecs = products.stream()
                .collect(Collectors.toMap(
                        ProductDto::getName,
                        product -> product.getSpecifications().stream()
                                .map(spec -> spec.getKey())
                                .filter(specKey -> !commonSpecs.contains(specKey))
                                .collect(Collectors.toSet())
                ));
        
        Map<String, Map<String, String>> specComparison = new HashMap<>();
        for (String specKey : commonSpecs) {
            Map<String, String> productValues = products.stream()
                    .collect(Collectors.toMap(
                            ProductDto::getName,
                            product -> product.getSpecifications().stream()
                                    .filter(spec -> spec.getKey().equals(specKey))
                                    .map(spec -> spec.getValue())
                                    .findFirst()
                                    .orElse("N/A")
                    ));
            specComparison.put(specKey, productValues);
        }
        
        ProductDto mostFeatured = products.stream()
                .max(Comparator.comparing(p -> p.getSpecifications().size()))
                .orElse(null);
        
        return ComparisonResponseDto.SpecificationAnalysisDto.builder()
                .commonSpecifications(commonSpecs)
                .uniqueSpecifications(uniqueSpecs)
                .specificationComparison(specComparison)
                .mostFeaturedProduct(mostFeatured)
                .build();
    }
    
    private List<ComparisonResponseDto.RecommendationDto> generateRecommendations(List<ProductDto> products) {
        List<ComparisonResponseDto.RecommendationDto> recommendations = new ArrayList<>();
        
        // Best value recommendation
        ProductDto bestValue = calculateBestValue(products);
        if (bestValue != null) {
            recommendations.add(ComparisonResponseDto.RecommendationDto.builder()
                    .type("BEST_VALUE")
                    .title("Best Value for Money")
                    .description("This product offers the best balance of price, quality, and features")
                    .recommendedProduct(bestValue)
                    .reason("Optimal price-to-rating ratio with comprehensive features")
                    .build());
        }
        
        // Budget recommendation
        ProductDto cheapest = products.stream()
                .min(Comparator.comparing(ProductDto::getPrice))
                .orElse(null);
        if (cheapest != null) {
            recommendations.add(ComparisonResponseDto.RecommendationDto.builder()
                    .type("BUDGET_FRIENDLY")
                    .title("Most Affordable Option")
                    .description("Best choice if budget is your primary concern")
                    .recommendedProduct(cheapest)
                    .reason("Lowest price among compared products")
                    .build());
        }
        
        // Premium recommendation
        ProductDto bestRated = products.stream()
                .max(Comparator.comparing(ProductDto::getRating))
                .orElse(null);
        if (bestRated != null) {
            recommendations.add(ComparisonResponseDto.RecommendationDto.builder()
                    .type("PREMIUM_CHOICE")
                    .title("Highest Quality")
                    .description("Top-rated product with the best customer satisfaction")
                    .recommendedProduct(bestRated)
                    .reason("Highest customer rating among compared products")
                    .build());
        }
        
        return recommendations;
    }
    
    private ComparisonResponseDto.ComparisonSummaryDto generateSummary(List<ProductDto> products) {
        ProductDto bestValue = calculateBestValue(products);
        ProductDto bestQuality = products.stream()
                .max(Comparator.comparing(ProductDto::getRating))
                .orElse(null);
        ProductDto budgetOption = products.stream()
                .min(Comparator.comparing(ProductDto::getPrice))
                .orElse(null);
        
        List<String> insights = generateInsights(products);
        String conclusion = generateConclusion(products, bestValue, bestQuality, budgetOption);
        
        return ComparisonResponseDto.ComparisonSummaryDto.builder()
                .bestValue(bestValue)
                .bestQuality(bestQuality)
                .budgetOption(budgetOption)
                .insights(insights)
                .conclusion(conclusion)
                .build();
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
    
    private List<String> generateInsights(List<ProductDto> products) {
        List<String> insights = new ArrayList<>();
        
        BigDecimal priceRange = products.stream()
                .map(ProductDto::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO)
                .subtract(products.stream()
                        .map(ProductDto::getPrice)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO));
        
        double ratingRange = products.stream()
                .mapToDouble(ProductDto::getRating)
                .max().orElse(0.0) - products.stream()
                .mapToDouble(ProductDto::getRating)
                .min().orElse(0.0);
        
        insights.add(String.format("Price range: $%.2f across all products", priceRange));
        insights.add(String.format("Rating variance: %.1f points between highest and lowest rated", ratingRange));
        
        long highRatedCount = products.stream()
                .filter(p -> p.getRating() >= 4.5)
                .count();
        
        if (highRatedCount > 0) {
            insights.add(String.format("%d out of %d products are highly rated (4.5+ stars)", 
                    highRatedCount, products.size()));
        }
        
        return insights;
    }
    
    private String generateConclusion(List<ProductDto> products, ProductDto bestValue, 
                                    ProductDto bestQuality, ProductDto budgetOption) {
        if (products.size() == 1) {
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
    
    private ComparisonResponseDto createEmptyResponse(List<String> requestedIds) {
        return ComparisonResponseDto.builder()
                .products(Collections.emptyList())
                .totalProducts(0)
                .requestedIds(requestedIds)
                .comparisonTimestamp(Instant.now())
                .priceAnalysis(null)
                .ratingAnalysis(null)
                .specificationAnalysis(null)
                .recommendations(Collections.emptyList())
                .summary(null)
                .build();
    }
}