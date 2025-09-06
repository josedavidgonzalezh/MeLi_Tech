package com.meli.technical.exam.api.products.application.service;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.response.PaginatedResponseDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.ComparisonResponseDto;
import com.meli.technical.exam.api.products.application.usecase.ProductComparisonUseCase;
import com.meli.technical.exam.api.products.domain.event.DomainEventPublisher;
import com.meli.technical.exam.api.products.domain.event.ProductComparedEvent;
import com.meli.technical.exam.api.products.domain.event.ProductViewedEvent;
import com.meli.technical.exam.api.products.domain.model.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    @Mock
    private ProductComparisonUseCase productComparisonUseCase;

    @Mock
    private DomainEventPublisher eventPublisher;

    private ProductApplicationService applicationService;

    private ProductDto testProduct;
    private ComparisonResponseDto testComparisonResponse;
    private PaginatedResponseDto<ProductDto> testPaginatedResponse;

    @BeforeEach
    void setUp() {
        applicationService = new ProductApplicationService(
                productComparisonUseCase, eventPublisher);

        testProduct = new ProductDto(
                "1", "Test Product", "url", "Description",
                new BigDecimal("99.99"), 4.5, List.of()
        );

        testComparisonResponse = ComparisonResponseDto.builder()
                .products(List.of(testProduct))
                .totalProducts(1)
                .requestedIds(List.of("1"))
                .comparisonTimestamp(Instant.now())
                .build();

        testPaginatedResponse = new PaginatedResponseDto<>(
                List.of(testProduct), 0, 10, 1L
        );
    }

    @Test
    void shouldGetProductByIdAndPublishViewedEvent() {
        // Given
        when(productComparisonUseCase.getProductById("1"))
                .thenReturn(Mono.just(testProduct));

        // When & Then
        StepVerifier.create(applicationService.getProductById("1"))
                .expectNext(testProduct)
                .verifyComplete();

        // Verify event was published
        ArgumentCaptor<ProductViewedEvent> eventCaptor = 
                ArgumentCaptor.forClass(ProductViewedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        
        ProductViewedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(ProductId.of("1"), capturedEvent.getProductId());
        assertEquals("API_DIRECT_ACCESS", capturedEvent.getSource());
    }

    @Test
    void shouldNotPublishEventWhenProductNotFound() {
        // Given
        when(productComparisonUseCase.getProductById("nonexistent"))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(applicationService.getProductById("nonexistent"))
                .verifyComplete();

        // Verify no event was published
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldHandleErrorWhenGettingProductById() {
        // Given
        RuntimeException testError = new RuntimeException("Product not found");
        when(productComparisonUseCase.getProductById("1"))
                .thenReturn(Mono.error(testError));

        // When & Then
        StepVerifier.create(applicationService.getProductById("1"))
                .expectError(RuntimeException.class)
                .verify();

        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldCompareProductsAndPublishComparedEvent() {
        // Given
        List<String> productIds = List.of("1", "2");
        when(productComparisonUseCase.compareProducts(productIds))
                .thenReturn(Mono.just(testComparisonResponse));

        // When & Then
        StepVerifier.create(applicationService.compareProducts(productIds))
                .expectNext(testComparisonResponse)
                .verifyComplete();

        // Verify event was published
        ArgumentCaptor<ProductComparedEvent> eventCaptor = 
                ArgumentCaptor.forClass(ProductComparedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        
        ProductComparedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(2, capturedEvent.getProductIds().size());
        assertEquals(ProductId.of("1"), capturedEvent.getProductIds().get(0));
        assertEquals(ProductId.of("2"), capturedEvent.getProductIds().get(1));
        assertTrue(capturedEvent.getComparisonResult().contains("Compared 1 products successfully"));
    }

    @Test
    void shouldHandleEmptyProductListForComparison() {
        // Given
        List<String> emptyIds = List.of();
        ComparisonResponseDto emptyResponse = ComparisonResponseDto.builder()
                .products(List.of())
                .totalProducts(0)
                .requestedIds(emptyIds)
                .comparisonTimestamp(Instant.now())
                .build();

        when(productComparisonUseCase.compareProducts(emptyIds))
                .thenReturn(Mono.just(emptyResponse));

        // When & Then
        StepVerifier.create(applicationService.compareProducts(emptyIds))
                .expectNext(emptyResponse)
                .verifyComplete();

        // Verify event was published for empty list
        ArgumentCaptor<ProductComparedEvent> eventCaptor = 
                ArgumentCaptor.forClass(ProductComparedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        
        ProductComparedEvent capturedEvent = eventCaptor.getValue();
        assertTrue(capturedEvent.getProductIds().isEmpty());
        assertEquals("Empty product list provided for comparison", capturedEvent.getComparisonResult());
    }

    @Test
    void shouldHandleNullProductListForComparison() {
        // Given
        ComparisonResponseDto emptyResponse = ComparisonResponseDto.builder()
                .products(List.of())
                .totalProducts(0)
                .requestedIds(List.of())
                .comparisonTimestamp(Instant.now())
                .build();

        when(productComparisonUseCase.compareProducts(List.of()))
                .thenReturn(Mono.just(emptyResponse));

        // When & Then
        StepVerifier.create(applicationService.compareProducts(null))
                .expectNext(emptyResponse)
                .verifyComplete();

        // Verify event was published for null list
        ArgumentCaptor<ProductComparedEvent> eventCaptor = 
                ArgumentCaptor.forClass(ProductComparedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        
        ProductComparedEvent capturedEvent = eventCaptor.getValue();
        assertTrue(capturedEvent.getProductIds().isEmpty());
        assertEquals("Empty product list provided for comparison", capturedEvent.getComparisonResult());
    }

    @Test
    void shouldPublishErrorEventWhenComparisonFails() {
        // Given
        List<String> productIds = List.of("1", "2");
        RuntimeException testError = new RuntimeException("Comparison failed");
        
        when(productComparisonUseCase.compareProducts(productIds))
                .thenReturn(Mono.error(testError));

        // When & Then
        StepVerifier.create(applicationService.compareProducts(productIds))
                .expectError(RuntimeException.class)
                .verify();

        // Verify error event was published
        ArgumentCaptor<ProductComparedEvent> eventCaptor = 
                ArgumentCaptor.forClass(ProductComparedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        
        ProductComparedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(2, capturedEvent.getProductIds().size());
        assertTrue(capturedEvent.getComparisonResult().contains("Comparison failed: Comparison failed"));
    }

    @Test
    void shouldGetAllProductsPaginated() {
        // Given
        when(productComparisonUseCase.getAllProductsPaginated(0, 10))
                .thenReturn(Mono.just(testPaginatedResponse));

        // When & Then
        StepVerifier.create(applicationService.getAllProducts(0, 10))
                .expectNext(testPaginatedResponse)
                .verifyComplete();

        verify(productComparisonUseCase).getAllProductsPaginated(0, 10);
    }

    @Test
    void shouldGetAllProducts() {
        // Given
        when(productComparisonUseCase.getAllProducts())
                .thenReturn(Mono.just(testPaginatedResponse));

        // When & Then
        StepVerifier.create(applicationService.getAllProducts())
                .expectNext(testPaginatedResponse)
                .verifyComplete();

        verify(productComparisonUseCase).getAllProducts();
    }

    @Test
    void shouldHandleErrorWhenGettingAllProducts() {
        // Given
        RuntimeException testError = new RuntimeException("Database error");
        when(productComparisonUseCase.getAllProducts())
                .thenReturn(Mono.error(testError));

        // When & Then
        StepVerifier.create(applicationService.getAllProducts())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleErrorWhenGettingPaginatedProducts() {
        // Given
        RuntimeException testError = new RuntimeException("Database error");
        when(productComparisonUseCase.getAllProductsPaginated(0, 10))
                .thenReturn(Mono.error(testError));

        // When & Then
        StepVerifier.create(applicationService.getAllProducts(0, 10))
                .expectError(RuntimeException.class)
                .verify();
    }
}