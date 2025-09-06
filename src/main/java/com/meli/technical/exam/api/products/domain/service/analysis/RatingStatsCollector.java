package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class RatingStatsCollector implements ReactiveStatsCollector<RatingStats> {

    @Override
    public Mono<RatingStats> collectReactive(Flux<ProductDto> productFlux) {
        return Mono.zip(
                // Find best and lowest rated concurrently
                productFlux.reduce((p1, p2) -> p1.getRating() >= p2.getRating() ? p1 : p2),
                productFlux.reduce((p1, p2) -> p1.getRating() <= p2.getRating() ? p1 : p2),
                // Calculate total rating and count
                productFlux.map(ProductDto::getRating).reduce(0.0, Double::sum),
                productFlux.count(),
                // Collect distribution map
                productFlux.collectMap(ProductDto::getName, ProductDto::getRating),
                // Collect highly rated products
                productFlux.filter(p -> p.getRating() >= 4.5).collectList()
        ).map(tuple -> {
            ProductDto bestRated = tuple.getT1();
            ProductDto lowestRated = tuple.getT2();
            Double totalRating = tuple.getT3();
            Long count = tuple.getT4();
            Map<String, Double> distribution = tuple.getT5();
            List<ProductDto> highlyRated = tuple.getT6();
            
            double avgRating = count > 0 ? 
                    Math.round((totalRating / count) * 10.0) / 10.0 : 
                    0.0;
            double range = (bestRated != null && lowestRated != null) ?
                    bestRated.getRating() - lowestRated.getRating() :
                    0.0;
            
            return new RatingStats(bestRated, lowestRated, totalRating, avgRating, range, distribution, highlyRated);
        });
    }
}