package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
/*
* We are using the facade pattern to breakdown the different collectors
* Now purely reactive - no blocking operations
* */
public class ProductStatsCollector {
    private final PriceStatsCollector priceCollector = new PriceStatsCollector();
    private final RatingStatsCollector ratingCollector = new RatingStatsCollector();
    private final SpecStatsCollector specCollector = new SpecStatsCollector();

    public static Mono<ProductStats> collectStatsReactive(Flux<ProductDto> productFlux) {
        return new ProductStatsCollector().collectReactive(productFlux);
    }

    // Truly reactive implementation - processes streams without blocking
    public Mono<ProductStats> collectReactive(Flux<ProductDto> productFlux) {
        return productFlux.hasElements()
                .flatMap(hasElements -> {
                    if (Boolean.FALSE.equals(hasElements)) {
                        return Mono.just(ProductStats.createEmpty());
                    }
                    
                    return Mono.zip(
                            priceCollector.collectReactive(productFlux),
                            ratingCollector.collectReactive(productFlux),
                            specCollector.collectReactive(productFlux),
                            productFlux.count()
                    ).map(tuple -> {
            PriceStats priceStats = tuple.getT1();
            RatingStats ratingStats = tuple.getT2();
            SpecStats specStats = tuple.getT3();
            Long totalProducts = tuple.getT4();
            
            return ProductStats.builder()
                    .cheapestProduct(priceStats.cheapest())
                    .mostExpensiveProduct(priceStats.mostExpensive())
                    .totalPrice(priceStats.totalPrice())
                    .averagePrice(priceStats.averagePrice())
                    .priceRange(priceStats.priceRange())
                    .bestRatedProduct(ratingStats.bestRated())
                    .lowestRatedProduct(ratingStats.lowestRated())
                    .totalRating(ratingStats.totalRating())
                    .averageRating(ratingStats.averageRating())
                    .ratingRange(ratingStats.ratingRange())
                    .allSpecificationKeys(specStats.allSpecKeys())
                    .commonSpecifications(specStats.commonSpecs())
                    .mostFeaturedProduct(specStats.mostFeatured())
                    .productSpecifications(specStats.productSpecifications())
                    .totalProducts(totalProducts.intValue())
                    .allProducts(specStats.allProducts())
                    .highlyRatedProducts(ratingStats.highlyRated())
                    .priceDistribution(priceStats.priceDistribution())
                            .ratingDistribution(ratingStats.ratingDistribution())
                            .build();
                    });
                });
    }
}