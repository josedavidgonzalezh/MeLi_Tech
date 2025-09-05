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
        
        // Single-pass collection of all statistics
        ProductDto cheapest = null;
        ProductDto mostExpensive = null;
        ProductDto bestRated = null;
        ProductDto lowestRated = null;
        ProductDto mostFeatured = null;
        
        BigDecimal totalPrice = BigDecimal.ZERO;
        double totalRating = 0.0;
        int maxSpecifications = 0;
        
        Map<String, BigDecimal> priceDistribution = new HashMap<>();
        Map<String, Double> ratingDistribution = new HashMap<>();
        Map<String, Set<String>> productSpecifications = new HashMap<>();
        Set<String> allSpecKeys = new HashSet<>();
        List<ProductDto> highlyRated = new ArrayList<>();
        
        // Single iteration over all products
        for (ProductDto product : products) {
            BigDecimal price = product.getPrice();
            Double rating = product.getRating();
            List<SpecificationDto> specs = product.getSpecifications() != null ? 
                    product.getSpecifications() : Collections.emptyList();
            
            // Price analysis
            totalPrice = totalPrice.add(price);
            priceDistribution.put(product.getName(), price);
            
            if (cheapest == null || price.compareTo(cheapest.getPrice()) < 0) {
                cheapest = product;
            }
            if (mostExpensive == null || price.compareTo(mostExpensive.getPrice()) > 0) {
                mostExpensive = product;
            }
            
            // Rating analysis
            totalRating += rating;
            ratingDistribution.put(product.getName(), rating);
            
            if (bestRated == null || rating > bestRated.getRating()) {
                bestRated = product;
            }
            if (lowestRated == null || rating < lowestRated.getRating()) {
                lowestRated = product;
            }
            
            if (rating >= 4.5) {
                highlyRated.add(product);
            }
            
            // Specification analysis
            Set<String> productSpecKeys = specs.stream()
                    .map(SpecificationDto::getKey)
                    .collect(Collectors.toSet());
            
            productSpecifications.put(product.getName(), productSpecKeys);
            allSpecKeys.addAll(productSpecKeys);
            
            if (specs.size() > maxSpecifications) {
                maxSpecifications = specs.size();
                mostFeatured = product;
            }
        }
        
        // Calculate derived statistics
        int productCount = products.size();
        BigDecimal averagePrice = totalPrice.divide(BigDecimal.valueOf(productCount), 2, RoundingMode.HALF_UP);
        Double averageRating = totalRating / productCount;
        BigDecimal priceRange = mostExpensive.getPrice().subtract(cheapest.getPrice());
        Double ratingRange = bestRated.getRating() - lowestRated.getRating();
        
        // Find common specifications
        Set<String> commonSpecs = findCommonSpecifications(products, allSpecKeys);
        
        return ProductStats.builder()
                .cheapestProduct(cheapest)
                .mostExpensiveProduct(mostExpensive)
                .totalPrice(totalPrice)
                .averagePrice(averagePrice)
                .priceRange(priceRange)
                .bestRatedProduct(bestRated)
                .lowestRatedProduct(lowestRated)
                .totalRating(totalRating)
                .averageRating(Math.round(averageRating * 10.0) / 10.0)
                .ratingRange(ratingRange)
                .allSpecificationKeys(allSpecKeys)
                .commonSpecifications(commonSpecs)
                .mostFeaturedProduct(mostFeatured)
                .productSpecifications(productSpecifications)
                .totalProducts(productCount)
                .allProducts(products)
                .highlyRatedProducts(highlyRated)
                .priceDistribution(priceDistribution)
                .ratingDistribution(ratingDistribution)
                .build();
    }
    
    private static Set<String> findCommonSpecifications(List<ProductDto> products, Set<String> allSpecKeys) {
        return allSpecKeys.stream()
                .filter(specKey -> products.stream()
                        .allMatch(product -> product.getSpecifications().stream()
                                .anyMatch(spec -> spec.getKey().equals(specKey))))
                .collect(Collectors.toSet());
    }
}