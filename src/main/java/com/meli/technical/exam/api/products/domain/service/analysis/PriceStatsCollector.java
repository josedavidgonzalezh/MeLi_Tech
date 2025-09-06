package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PriceStatsCollector implements StatsCollector<PriceStats> {
    
    @Override
    public PriceStats collect(List<ProductDto> products) {
        ProductDto cheapest = products.stream()
                .min(Comparator.comparing(ProductDto::getPrice))
                .orElse(null);

        ProductDto mostExpensive = products.stream()
                .max(Comparator.comparing(ProductDto::getPrice))
                .orElse(null);

        BigDecimal totalPrice = products.stream()
                .map(ProductDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgPrice = totalPrice.divide(BigDecimal.valueOf(products.size()), 2, RoundingMode.HALF_UP);
        BigDecimal priceRange = (cheapest != null && mostExpensive != null)
                ? mostExpensive.getPrice().subtract(cheapest.getPrice())
                : BigDecimal.ZERO;

        Map<String, BigDecimal> distribution = products.stream()
                .collect(Collectors.toMap(ProductDto::getName, ProductDto::getPrice));

        return new PriceStats(cheapest, mostExpensive, totalPrice, avgPrice, priceRange, distribution);
    }
}