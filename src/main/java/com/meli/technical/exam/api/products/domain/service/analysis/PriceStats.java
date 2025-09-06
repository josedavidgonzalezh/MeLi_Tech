package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;

import java.math.BigDecimal;
import java.util.Map;

public record PriceStats(
        ProductDto cheapest,
        ProductDto mostExpensive,
        BigDecimal totalPrice,
        BigDecimal averagePrice,
        BigDecimal priceRange,
        Map<String, BigDecimal> priceDistribution
) {}