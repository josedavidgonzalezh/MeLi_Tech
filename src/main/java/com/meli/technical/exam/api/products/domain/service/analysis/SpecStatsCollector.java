package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.request.SpecificationDto;

import java.util.*;
import java.util.stream.Collectors;

public class SpecStatsCollector implements StatsCollector<SpecStats> {
    
    @Override
    public SpecStats collect(List<ProductDto> products) {
        ProductDto mostFeatured = products.stream()
                .max(Comparator.comparingInt(p -> Optional.ofNullable(p.getSpecifications())
                        .orElse(List.of()).size()))
                .orElse(null);

        Map<String, Set<String>> productSpecs = products.stream()
                .collect(Collectors.toMap(ProductDto::getName,
                        p -> Optional.ofNullable(p.getSpecifications()).orElse(List.of())
                                .stream().map(SpecificationDto::getKey).collect(Collectors.toSet())
                ));

        Set<String> allKeys = productSpecs.values().stream().flatMap(Set::stream).collect(Collectors.toSet());
        Set<String> commonKeys = allKeys.stream()
                .filter(k -> products.stream().allMatch(p -> Optional.ofNullable(p.getSpecifications())
                        .orElse(List.of()).stream().anyMatch(spec -> spec.getKey().equals(k))))
                .collect(Collectors.toSet());

        return new SpecStats(allKeys, commonKeys, mostFeatured, productSpecs, products);
    }
}