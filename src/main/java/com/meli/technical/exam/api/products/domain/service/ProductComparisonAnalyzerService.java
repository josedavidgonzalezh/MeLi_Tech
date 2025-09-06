package com.meli.technical.exam.api.products.domain.service;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.*;
import com.meli.technical.exam.api.products.domain.service.analysis.ProductStats;
import com.meli.technical.exam.api.products.domain.service.analysis.strategy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class ProductComparisonAnalyzerService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductComparisonAnalyzerService.class);
    
    private final PriceAnalysisStrategy priceAnalysisStrategy;
    private final RatingAnalysisStrategy ratingAnalysisStrategy;
    private final SpecificationAnalysisStrategy specificationAnalysisStrategy;
    private final RecommendationEngine recommendationEngine;
    private final SummaryGenerationStrategy summaryGenerationStrategy;
    
    public ProductComparisonAnalyzerService(PriceAnalysisStrategy priceAnalysisStrategy,
                                          RatingAnalysisStrategy ratingAnalysisStrategy,
                                          SpecificationAnalysisStrategy specificationAnalysisStrategy,
                                          RecommendationEngine recommendationEngine,
                                          SummaryGenerationStrategy summaryGenerationStrategy) {
        this.priceAnalysisStrategy = priceAnalysisStrategy;
        this.ratingAnalysisStrategy = ratingAnalysisStrategy;
        this.specificationAnalysisStrategy = specificationAnalysisStrategy;
        this.recommendationEngine = recommendationEngine;
        this.summaryGenerationStrategy = summaryGenerationStrategy;
    }

    public Mono<ComparisonResponseDto> analyzeProductsReactive(Flux<ProductDto> productFlux, List<String> requestedIds) {
        return productFlux
                .as(flux -> analyzeProductsFromFlux(flux, requestedIds))
                .doOnError(error -> logger.error("Error during reactive product analysis", error));
    }

    private Mono<ComparisonResponseDto> analyzeProductsFromFlux(Flux<ProductDto> productFlux, List<String> requestedIds) {
        return productFlux
                .collectList()
                .flatMap(products -> {
                    if (products.isEmpty()) {
                        return Mono.just(createEmptyResponse(requestedIds));
                    }
                    
                    return ProductStats.fromProductsReactive(Flux.fromIterable(products))
                            .flatMap(this::performAnalysis)
                            .map(analysisResults -> ComparisonResponseDto.builder()
                                    .products(products)
                                    .totalProducts(products.size())
                                    .requestedIds(requestedIds)
                                    .comparisonTimestamp(Instant.now())
                                    .priceAnalysis(analysisResults.priceAnalysis)
                                    .ratingAnalysis(analysisResults.ratingAnalysis)
                                    .specificationAnalysis(analysisResults.specificationAnalysis)
                                    .recommendations(analysisResults.recommendations)
                                    .summary(analysisResults.summary)
                                    .build());
                });
    }

    
    private Mono<AnalysisResults> performAnalysis(ProductStats productStats) {
        return Mono.zip(
                priceAnalysisStrategy.analyze(productStats),
                ratingAnalysisStrategy.analyze(productStats),
                specificationAnalysisStrategy.analyze(productStats),
                recommendationEngine.analyze(productStats),
                summaryGenerationStrategy.analyze(productStats)
        ).map(tuple -> new AnalysisResults(
                tuple.getT1(), // priceAnalysis
                tuple.getT2(), // ratingAnalysis  
                tuple.getT3(), // specificationAnalysis
                tuple.getT4(), // recommendations
                tuple.getT5()  // summary
        ));
    }
    
    private record AnalysisResults(
            PriceAnalysisDto priceAnalysis,
            RatingAnalysisDto ratingAnalysis,
            SpecificationAnalysisDto specificationAnalysis,
            List<RecommendationDto> recommendations,
            ComparisonSummaryDto summary
    ) {}
    
    private ComparisonResponseDto createEmptyResponse(List<String> requestedIds) {
        return ComparisonResponseDto.builder()
                .products(Collections.emptyList())
                .totalProducts(0)
                .requestedIds(requestedIds)
                .comparisonTimestamp(Instant.now())
                .priceAnalysis(null)
                .ratingAnalysis(null)
                .specificationAnalysis(null)
                .recommendations(Collections.emptyList())
                .summary(null)
                .build();
    }

}