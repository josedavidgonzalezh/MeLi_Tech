package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
/*
* We are using the facade pattern to breakdownn the diferent collectors
* */
public class ProductStatsCollector {
    private final PriceStatsCollector priceCollector = new PriceStatsCollector();
    private final RatingStatsCollector ratingCollector = new RatingStatsCollector();
    private final SpecStatsCollector specCollector = new SpecStatsCollector();

    public static ProductStats collectStats(List<ProductDto> products) {
        return new ProductStatsCollector().collect(products);
    }

    public static Mono<ProductStats> collectStatsReactive(Flux<ProductDto> productFlux) {
        return new ProductStatsCollector().collectReactive(productFlux);
    }

    public ProductStats collect(List<ProductDto> products) {
        if (products == null || products.isEmpty()) {
            return ProductStats.createEmpty();
        }

        PriceStats priceStats = priceCollector.collect(products);
        RatingStats ratingStats = ratingCollector.collect(products);
        SpecStats specStats = specCollector.collect(products);

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
                .totalProducts(products.size())
                .allProducts(specStats.allProducts())
                .highlyRatedProducts(ratingStats.highlyRated())
                .priceDistribution(priceStats.priceDistribution())
                .ratingDistribution(ratingStats.ratingDistribution())
                .build();
    }

    // Reactive adapter
    public Mono<ProductStats> collectReactive(Flux<ProductDto> productFlux) {
        return productFlux.collectList().map(this::collect);
    }
}