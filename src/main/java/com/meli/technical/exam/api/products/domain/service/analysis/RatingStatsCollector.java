package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RatingStatsCollector implements StatsCollector<RatingStats> {
    
    @Override
    public RatingStats collect(List<ProductDto> products) {
        ProductDto bestRated = products.stream()
                .max(Comparator.comparing(ProductDto::getRating))
                .orElse(null);

        ProductDto lowestRated = products.stream()
                .min(Comparator.comparing(ProductDto::getRating))
                .orElse(null);

        double totalRating = products.stream().mapToDouble(ProductDto::getRating).sum();
        double avgRating = Math.round((totalRating / products.size()) * 10.0) / 10.0;
        double range = (bestRated != null && lowestRated != null)
                ? bestRated.getRating() - lowestRated.getRating()
                : 0.0;

        Map<String, Double> distribution = products.stream()
                .collect(Collectors.toMap(ProductDto::getName, ProductDto::getRating));

        List<ProductDto> highlyRated = products.stream()
                .filter(p -> p.getRating() >= 4.5)
                .toList();

        return new RatingStats(bestRated, lowestRated, totalRating, avgRating, range, distribution, highlyRated);
    }
}