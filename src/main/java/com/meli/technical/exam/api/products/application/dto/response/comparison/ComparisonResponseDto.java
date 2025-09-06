package com.meli.technical.exam.api.products.application.dto.response.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

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

    @JsonProperty("priceAnalysis")
    private final PriceAnalysisDto priceAnalysis;

    @JsonProperty("ratingAnalysis")
    private final RatingAnalysisDto ratingAnalysis;

    @JsonProperty("specificationAnalysis")
    private final SpecificationAnalysisDto specificationAnalysis;

    @JsonProperty("recommendations")
    private final List<RecommendationDto> recommendations;

    @JsonProperty("summary")
    private final ComparisonSummaryDto summary;
}