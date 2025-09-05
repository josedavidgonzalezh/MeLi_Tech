package com.meli.technical.exam.api.products.application.dto.response.comparison;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class SpecificationAnalysisDto {

    @JsonProperty("commonSpecifications")
    private final Set<String> commonSpecifications;

    @JsonProperty("uniqueSpecifications")
    private final Map<String, Set<String>> uniqueSpecifications;

    @JsonProperty("specificationComparison")
    private final Map<String, Map<String, String>> specificationComparison;

    @JsonProperty("mostFeaturedProduct")
    private final ProductDto mostFeaturedProduct;
}
