package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record SpecStats(
        Set<String> allSpecKeys,
        Set<String> commonSpecs,
        ProductDto mostFeatured,
        Map<String, Set<String>> productSpecifications,
        List<ProductDto> allProducts
) {}