package com.meli.technical.exam.api.products.application.dto.response.comparison;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationDto {

    @JsonProperty("type")
    private final String type;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("recommendedProduct")
    private final ProductDto recommendedProduct;

    @JsonProperty("reason")
    private final String reason;
}
