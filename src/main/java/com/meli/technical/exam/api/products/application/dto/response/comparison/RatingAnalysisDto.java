package com.meli.technical.exam.api.products.application.dto.response.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class RatingAnalysisDto {

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
