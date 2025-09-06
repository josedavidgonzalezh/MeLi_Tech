package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.request.SpecificationDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

public class SpecStatsCollector implements ReactiveStatsCollector<SpecStats> {

    @Override
    public Mono<SpecStats> collectReactive(Flux<ProductDto> productFlux) {
        return Mono.zip(
                // Find most featured product
                productFlux.reduce((p1, p2) -> {
                    int specs1 = Optional.ofNullable(p1.getSpecifications()).orElse(List.of()).size();
                    int specs2 = Optional.ofNullable(p2.getSpecifications()).orElse(List.of()).size();
                    return specs1 >= specs2 ? p1 : p2;
                }),
                // Collect product specifications map
                productFlux.collectMap(
                        ProductDto::getName,
                        p -> Optional.ofNullable(p.getSpecifications()).orElse(List.of())
                                .stream().map(SpecificationDto::getKey).collect(Collectors.toSet())
                ),
                // Collect all products
                productFlux.collectList()
        ).map(tuple -> {
            ProductDto mostFeatured = tuple.getT1();
            Map<String, Set<String>> productSpecs = tuple.getT2();
            List<ProductDto> allProducts = tuple.getT3();
            
            // Calculate all specification keys
            Set<String> allKeys = productSpecs.values().stream()
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());
            
            // Calculate common specification keys
            Set<String> commonKeys = allKeys.stream()
                    .filter(k -> allProducts.stream().allMatch(p -> 
                            Optional.ofNullable(p.getSpecifications()).orElse(List.of())
                                    .stream().anyMatch(spec -> spec.getKey().equals(k))))
                    .collect(Collectors.toSet());
            
            return new SpecStats(allKeys, commonKeys, mostFeatured, productSpecs, allProducts);
        });
    }
}