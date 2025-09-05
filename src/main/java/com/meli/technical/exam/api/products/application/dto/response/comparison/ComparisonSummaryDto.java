package com.meli.technical.exam.api.products.application.dto.response.comparison;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ComparisonSummaryDto {

    @JsonProperty("bestValue")
    private final ProductDto bestValue;

    @JsonProperty("bestQuality")
    private final ProductDto bestQuality;

    @JsonProperty("budgetOption")
    private final ProductDto budgetOption;

    @JsonProperty("insights")
    private final List<String> insights;

    @JsonProperty("conclusion")
    private final String conclusion;
}
