package com.meli.technical.exam.api.products.domain.service;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.request.SpecificationDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.*;
import com.meli.technical.exam.api.products.domain.service.analysis.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductComparisonAnalyzerServiceTest {

    @Mock
    private PriceAnalysisStrategy priceAnalysisStrategy;

    @Mock
    private RatingAnalysisStrategy ratingAnalysisStrategy;

    @Mock
    private SpecificationAnalysisStrategy specificationAnalysisStrategy;

    @Mock
    private RecommendationEngine recommendationEngine;

    @Mock
    private SummaryGenerationStrategy summaryGenerationStrategy;

    private ProductComparisonAnalyzerService analyzerService;

    private ProductDto testProduct1;
    private ProductDto testProduct2;
    private List<String> requestedIds;

    @BeforeEach
    void setUp() {
        analyzerService = new ProductComparisonAnalyzerService(
                priceAnalysisStrategy,
                ratingAnalysisStrategy,
                specificationAnalysisStrategy,
                recommendationEngine,
                summaryGenerationStrategy
        );

        testProduct1 = new ProductDto(
                "1", "Product 1", "url1", "Description 1",
                new BigDecimal("99.99"), 4.5,
                List.of(new SpecificationDto("brand", "Brand A"),
                        new SpecificationDto("model", "Model X"))
        );

        testProduct2 = new ProductDto(
                "2", "Product 2", "url2", "Description 2",
                new BigDecimal("199.99"), 4.0,
                List.of(new SpecificationDto("brand", "Brand B"),
                        new SpecificationDto("model", "Model Y"))
        );

        requestedIds = List.of("1", "2");
    }

    @Test
    void shouldAnalyzeProductsReactively() {
        // Given
        Flux<ProductDto> productFlux = Flux.just(testProduct1, testProduct2);
        
        PriceAnalysisDto priceAnalysis = PriceAnalysisDto.builder()
                .priceRange(new BigDecimal("100.00"))
                .averagePrice(new BigDecimal("149.99"))
                .build();

        RatingAnalysisDto ratingAnalysis = RatingAnalysisDto.builder()
                .averageRating(4.25)
                .build();

        SpecificationAnalysisDto specAnalysis = SpecificationAnalysisDto.builder()
                .commonSpecifications(Set.of("brand", "model"))
                .build();

        List<RecommendationDto> recommendations = List.of(
                RecommendationDto.builder()
                        .type("BEST_VALUE")
                        .title("Best Value")
                        .reason("Best value for money")
                        .recommendedProduct(testProduct1)
                        .build()
        );

        ComparisonSummaryDto summary = ComparisonSummaryDto.builder()
                .conclusion("Product 1 offers better value")
                .insights(List.of("Product 1 offers better value"))
                .build();

        when(priceAnalysisStrategy.analyze(any())).thenReturn(Mono.just(priceAnalysis));
        when(ratingAnalysisStrategy.analyze(any())).thenReturn(Mono.just(ratingAnalysis));
        when(specificationAnalysisStrategy.analyze(any())).thenReturn(Mono.just(specAnalysis));
        when(recommendationEngine.analyze(any())).thenReturn(Mono.just(recommendations));
        when(summaryGenerationStrategy.analyze(any())).thenReturn(Mono.just(summary));

        // When & Then
        StepVerifier.create(analyzerService.analyzeProductsReactive(productFlux, requestedIds))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(2, result.getTotalProducts());
                    assertEquals(requestedIds, result.getRequestedIds());
                    assertNotNull(result.getComparisonTimestamp());
                    assertEquals(priceAnalysis, result.getPriceAnalysis());
                    assertEquals(ratingAnalysis, result.getRatingAnalysis());
                    assertEquals(specAnalysis, result.getSpecificationAnalysis());
                    assertEquals(recommendations, result.getRecommendations());
                    assertEquals(summary, result.getSummary());
                })
                .verifyComplete();

        verify(priceAnalysisStrategy).analyze(any());
        verify(ratingAnalysisStrategy).analyze(any());
        verify(specificationAnalysisStrategy).analyze(any());
        verify(recommendationEngine).analyze(any());
        verify(summaryGenerationStrategy).analyze(any());
    }

    @Test
    void shouldAnalyzeProductsFromList() {
        // Given
        List<ProductDto> products = List.of(testProduct1, testProduct2);

        when(priceAnalysisStrategy.analyze(any())).thenReturn(Mono.just(
                PriceAnalysisDto.builder().priceRange(new BigDecimal("100.00")).build()));
        when(ratingAnalysisStrategy.analyze(any())).thenReturn(Mono.just(
                RatingAnalysisDto.builder().averageRating(4.25).build()));
        when(specificationAnalysisStrategy.analyze(any())).thenReturn(Mono.just(
                SpecificationAnalysisDto.builder().commonSpecifications(Set.of()).build()));
        when(recommendationEngine.analyze(any())).thenReturn(Mono.just(List.of()));
        when(summaryGenerationStrategy.analyze(any())).thenReturn(Mono.just(
                ComparisonSummaryDto.builder().conclusion("Good comparison").build()));

        // When & Then
        StepVerifier.create(analyzerService.analyzeProducts(products, requestedIds))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(2, result.getTotalProducts());
                    assertEquals(products, result.getProducts());
                    assertEquals(requestedIds, result.getRequestedIds());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyResponseWhenProductsListIsEmpty() {
        // Given
        List<ProductDto> emptyProducts = List.of();

        // When & Then
        StepVerifier.create(analyzerService.analyzeProducts(emptyProducts, requestedIds))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(0, result.getTotalProducts());
                    assertTrue(result.getProducts().isEmpty());
                    assertEquals(requestedIds, result.getRequestedIds());
                    assertNotNull(result.getComparisonTimestamp());
                    assertNull(result.getPriceAnalysis());
                    assertNull(result.getRatingAnalysis());
                    assertNull(result.getSpecificationAnalysis());
                    assertTrue(result.getRecommendations().isEmpty());
                    assertNull(result.getSummary());
                })
                .verifyComplete();

        verifyNoInteractions(priceAnalysisStrategy, ratingAnalysisStrategy, 
                           specificationAnalysisStrategy, recommendationEngine, 
                           summaryGenerationStrategy);
    }

    @Test
    void shouldReturnEmptyResponseWhenProductsListIsNull() {
        // When & Then
        StepVerifier.create(analyzerService.analyzeProducts(null, requestedIds))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(0, result.getTotalProducts());
                    assertTrue(result.getProducts().isEmpty());
                    assertEquals(requestedIds, result.getRequestedIds());
                })
                .verifyComplete();

        verifyNoInteractions(priceAnalysisStrategy, ratingAnalysisStrategy, 
                           specificationAnalysisStrategy, recommendationEngine, 
                           summaryGenerationStrategy);
    }

    @Test
    void shouldHandleAnalysisErrors() {
        // Given
        List<ProductDto> products = List.of(testProduct1);
        RuntimeException analysisError = new RuntimeException("Analysis failed");

        when(priceAnalysisStrategy.analyze(any())).thenReturn(Mono.error(analysisError));

        // When & Then
        StepVerifier.create(analyzerService.analyzeProducts(products, requestedIds))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldAnalyzeReactiveFluxWithEmptyFlux() {
        // Given
        Flux<ProductDto> emptyFlux = Flux.empty();

        // When & Then
        StepVerifier.create(analyzerService.analyzeProductsReactive(emptyFlux, requestedIds))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(0, result.getTotalProducts());
                    assertTrue(result.getProducts().isEmpty());
                })
                .verifyComplete();
    }

    @Test
    void shouldAnalyzeSingleProduct() {
        // Given
        List<ProductDto> singleProduct = List.of(testProduct1);

        when(priceAnalysisStrategy.analyze(any())).thenReturn(Mono.just(
                PriceAnalysisDto.builder().priceRange(new BigDecimal("0.00")).build()));
        when(ratingAnalysisStrategy.analyze(any())).thenReturn(Mono.just(
                RatingAnalysisDto.builder().averageRating(4.5).build()));
        when(specificationAnalysisStrategy.analyze(any())).thenReturn(Mono.just(
                SpecificationAnalysisDto.builder().commonSpecifications(Set.of()).build()));
        when(recommendationEngine.analyze(any())).thenReturn(Mono.just(List.of()));
        when(summaryGenerationStrategy.analyze(any())).thenReturn(Mono.just(
                ComparisonSummaryDto.builder().conclusion("Single product analysis").build()));

        // When & Then
        StepVerifier.create(analyzerService.analyzeProducts(singleProduct, List.of("1")))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(1, result.getTotalProducts());
                    assertEquals(singleProduct, result.getProducts());
                    assertTrue(result.getComparisonTimestamp().isBefore(Instant.now().plusSeconds(1)));
                })
                .verifyComplete();
    }
}