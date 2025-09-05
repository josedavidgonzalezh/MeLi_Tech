package com.meli.technical.exam.api.products.application.dto.response.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
public class PriceAnalysisDto {

    @JsonProperty("cheapestProduct")
    private final ProductDto cheapestProduct;

    @JsonProperty("mostExpensiveProduct")
    private final ProductDto mostExpensiveProduct;

    @JsonProperty("priceRange")
    private final BigDecimal priceRange;

    @JsonProperty("averagePrice")
    private final BigDecimal averagePrice;

    @JsonProperty("priceDistribution")
    private final Map<String, BigDecimal> priceDistribution;
}
