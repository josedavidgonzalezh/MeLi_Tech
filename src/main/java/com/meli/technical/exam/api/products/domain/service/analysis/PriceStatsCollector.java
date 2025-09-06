package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class PriceStatsCollector implements ReactiveStatsCollector<PriceStats> {

    @Override
    public Mono<PriceStats> collectReactive(Flux<ProductDto> productFlux) {
        return Mono.zip(
                // Find cheapest and most expensive concurrently
                productFlux.reduce((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()) <= 0 ? p1 : p2),
                productFlux.reduce((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()) >= 0 ? p1 : p2),
                // Calculate total price and count
                productFlux.map(ProductDto::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add),
                productFlux.count(),
                // Collect distribution map
                productFlux.collectMap(ProductDto::getName, ProductDto::getPrice)
        ).map(tuple -> {
            ProductDto cheapest = tuple.getT1();
            ProductDto mostExpensive = tuple.getT2();
            BigDecimal totalPrice = tuple.getT3();
            Long count = tuple.getT4();
            Map<String, BigDecimal> distribution = tuple.getT5();
            
            BigDecimal avgPrice = count > 0 ? 
                    totalPrice.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : 
                    BigDecimal.ZERO;
            BigDecimal priceRange = (cheapest != null && mostExpensive != null) ?
                    mostExpensive.getPrice().subtract(cheapest.getPrice()) :
                    BigDecimal.ZERO;
            
            return new PriceStats(cheapest, mostExpensive, totalPrice, avgPrice, priceRange, distribution);
        });
    }
}