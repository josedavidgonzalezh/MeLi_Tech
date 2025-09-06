package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;

import java.util.List;
import java.util.Map;

public record RatingStats(
        ProductDto bestRated,
        ProductDto lowestRated,
        Double totalRating,
        Double averageRating,
        Double ratingRange,
        Map<String, Double> ratingDistribution,
        List<ProductDto> highlyRated
) {}