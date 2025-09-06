package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.request.SpecificationDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class ProductStatsCollector {

    public static ProductStats collectStats(List<ProductDto> products) {
        if (products == null || products.isEmpty()) {
            return ProductStats.createEmpty();
        }

        int count = products.size();

        // Price stats
        ProductDto cheapest = products.stream()
                .min(Comparator.comparing(ProductDto::getPrice))
                .orElse(null);

        ProductDto mostExpensive = products.stream()
                .max(Comparator.comparing(ProductDto::getPrice))
                .orElse(null);

        BigDecimal totalPrice = products.stream()
                .map(ProductDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgPrice = totalPrice.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        BigDecimal priceRange = mostExpensive.getPrice().subtract(cheapest.getPrice());

        Map<String, BigDecimal> priceDistribution =
                products.stream().collect(Collectors.toMap(ProductDto::getName, ProductDto::getPrice));

        // Rating stats
        ProductDto bestRated = products.stream()
                .max(Comparator.comparing(ProductDto::getRating))
                .orElse(null);

        ProductDto lowestRated = products.stream()
                .min(Comparator.comparing(ProductDto::getRating))
                .orElse(null);

        double totalRating = products.stream()
                .mapToDouble(ProductDto::getRating)
                .sum();

        double avgRating = Math.round((totalRating / count) * 10.0) / 10.0;

        double ratingRange = bestRated.getRating() - lowestRated.getRating();

        Map<String, Double> ratingDistribution =
                products.stream().collect(Collectors.toMap(ProductDto::getName, ProductDto::getRating));

        List<ProductDto> highlyRated =
                products.stream().filter(p -> p.getRating() >= 4.5).toList();

        // Specs stats
        ProductDto mostFeatured = products.stream()
                .max(Comparator.comparingInt(p ->
                        Optional.ofNullable(p.getSpecifications()).orElse(List.of()).size()))
                .orElse(null);

        Map<String, Set<String>> productSpecifications = products.stream()
                .collect(Collectors.toMap(ProductDto::getName,
                        p -> Optional.ofNullable(p.getSpecifications()).orElse(List.of())
                                .stream().map(SpecificationDto::getKey).collect(Collectors.toSet())
                ));

        Set<String> allSpecKeys = productSpecifications.values()
                .stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        Set<String> commonSpecs = findCommonSpecifications(products, allSpecKeys);

        // Build result
        return ProductStats.builder()
                .cheapestProduct(cheapest)
                .mostExpensiveProduct(mostExpensive)
                .totalPrice(totalPrice)
                .averagePrice(avgPrice)
                .priceRange(priceRange)
                .bestRatedProduct(bestRated)
                .lowestRatedProduct(lowestRated)
                .totalRating(totalRating)
                .averageRating(avgRating)
                .ratingRange(ratingRange)
                .allSpecificationKeys(allSpecKeys)
                .commonSpecifications(commonSpecs)
                .mostFeaturedProduct(mostFeatured)
                .productSpecifications(productSpecifications)
                .totalProducts(count)
                .allProducts(products)
                .highlyRatedProducts(highlyRated)
                .priceDistribution(priceDistribution)
                .ratingDistribution(ratingDistribution)
                .build();
    }

    private static Set<String> findCommonSpecifications(List<ProductDto> products, Set<String> allSpecKeys) {
        return allSpecKeys.stream()
                .filter(specKey -> products.stream()
                        .allMatch(p -> Optional.ofNullable(p.getSpecifications()).orElse(List.of())
                                .stream().anyMatch(spec -> spec.getKey().equals(specKey))))
                .collect(Collectors.toSet());
    }
}