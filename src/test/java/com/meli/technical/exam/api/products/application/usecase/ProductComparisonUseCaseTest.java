package com.meli.technical.exam.api.products.application.usecase;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.ComparisonResponseDto;
import com.meli.technical.exam.api.products.application.mapper.ProductMapper;
import com.meli.technical.exam.api.products.domain.exception.ProductNotFoundException;
import com.meli.technical.exam.api.products.domain.model.Price;
import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.model.ProductId;
import com.meli.technical.exam.api.products.domain.model.Rating;
import com.meli.technical.exam.api.products.domain.service.ProductComparisonAnalyzerService;
import com.meli.technical.exam.api.products.domain.service.ProductService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductComparisonUseCaseTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductComparisonAnalyzerService comparisonAnalyzer;

    private ProductComparisonUseCase useCase;

    private Product testProduct1;
    private Product testProduct2;
    private ProductDto testProductDto1;
    private ProductDto testProductDto2;

    @BeforeEach
    void setUp() {
        useCase = new ProductComparisonUseCase(productService, productMapper, comparisonAnalyzer);

        testProduct1 = Product.builder()
                .id(ProductId.of("1"))
                .name("Product 1")
                .imageUrl("url1")
                .description("Description 1")
                .price(Price.of(new BigDecimal("99.99")))
                .rating(Rating.of(4.5))
                .specifications(List.of())
                .build();

        testProduct2 = Product.builder()
                .id(ProductId.of("2"))
                .name("Product 2")
                .imageUrl("url2")
                .description("Description 2")
                .price(Price.of(new BigDecimal("199.99")))
                .rating(Rating.of(4.0))
                .specifications(List.of())
                .build();

        testProductDto1 = new ProductDto(
                "1", "Product 1", "url1", "Description 1",
                new BigDecimal("99.99"), 4.5, List.of()
        );

        testProductDto2 = new ProductDto(
                "2", "Product 2", "url2", "Description 2",
                new BigDecimal("199.99"), 4.0, List.of()
        );
    }

    @Test
    void shouldGetProductById() {
        // Given
        when(productService.findById("1")).thenReturn(Mono.just(testProduct1));
        when(productMapper.toDto(testProduct1)).thenReturn(testProductDto1);

        // When & Then
        StepVerifier.create(useCase.getProductById("1"))
                .expectNext(testProductDto1)
                .verifyComplete();

        verify(productService).findById("1");
        verify(productMapper).toDto(testProduct1);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {
        // Given
        when(productService.findById("nonexistent")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(useCase.getProductById("nonexistent"))
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(productService).findById("nonexistent");
        verifyNoInteractions(productMapper);
    }

    @Test
    void shouldCompareProducts() {
        // Given
        List<String> productIds = List.of("1", "2");
        ComparisonResponseDto expectedResponse = ComparisonResponseDto.builder()
                .products(List.of(testProductDto1, testProductDto2))
                .totalProducts(2)
                .requestedIds(productIds)
                .comparisonTimestamp(Instant.now())
                .build();

        when(productService.findProductsForComparison(productIds))
                .thenReturn(Flux.just(testProduct1, testProduct2));
        when(productMapper.toDto(testProduct1)).thenReturn(testProductDto1);
        when(productMapper.toDto(testProduct2)).thenReturn(testProductDto2);
        when(comparisonAnalyzer.analyzeProducts(any(), eq(productIds)))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        StepVerifier.create(useCase.compareProducts(productIds))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(productService).findProductsForComparison(productIds);
        verify(productMapper).toDto(testProduct1);
        verify(productMapper).toDto(testProduct2);
        verify(comparisonAnalyzer).analyzeProducts(any(), eq(productIds));
    }

    @Test
    void shouldCompareProductsWhenSomeProductsNotFound() {
        // Given
        List<String> requestedIds = List.of("1", "2", "3");
        // Only products 1 and 2 are found, product 3 is missing
        List<ProductDto> foundProducts = List.of(testProductDto1, testProductDto2);

        ComparisonResponseDto expectedResponse = ComparisonResponseDto.builder()
                .products(foundProducts)
                .totalProducts(2)
                .requestedIds(requestedIds)
                .comparisonTimestamp(Instant.now())
                .build();

        when(productService.findProductsForComparison(requestedIds))
                .thenReturn(Flux.just(testProduct1, testProduct2)); // Only 2 products found
        when(productMapper.toDto(testProduct1)).thenReturn(testProductDto1);
        when(productMapper.toDto(testProduct2)).thenReturn(testProductDto2);
        when(comparisonAnalyzer.analyzeProducts(foundProducts, requestedIds))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        StepVerifier.create(useCase.compareProducts(requestedIds))
                .expectNext(expectedResponse)
                .verifyComplete();

        verify(productService).findProductsForComparison(requestedIds);
        verify(comparisonAnalyzer).analyzeProducts(foundProducts, requestedIds);
    }

    @Test
    void shouldReturnEmptyComparisonWhenProductIdsIsEmpty() {
        // Given
        List<String> emptyIds = List.of();
        ComparisonResponseDto emptyResponse = ComparisonResponseDto.builder()
                .products(List.of())
                .totalProducts(0)
                .requestedIds(emptyIds)
                .comparisonTimestamp(Instant.now())
                .build();

        when(comparisonAnalyzer.analyzeProducts(List.of(), emptyIds))
                .thenReturn(Mono.just(emptyResponse));

        // When & Then
        StepVerifier.create(useCase.compareProducts(emptyIds))
                .expectNext(emptyResponse)
                .verifyComplete();

        verify(comparisonAnalyzer).analyzeProducts(List.of(), emptyIds);
        verifyNoInteractions(productService, productMapper);
    }

    @Test
    void shouldReturnEmptyComparisonWhenProductIdsIsNull() {
        // Given
        ComparisonResponseDto emptyResponse = ComparisonResponseDto.builder()
                .products(List.of())
                .totalProducts(0)
                .requestedIds(null)
                .comparisonTimestamp(Instant.now())
                .build();

        when(comparisonAnalyzer.analyzeProducts(List.of(), null))
                .thenReturn(Mono.just(emptyResponse));

        // When & Then
        StepVerifier.create(useCase.compareProducts(null))
                .expectNext(emptyResponse)
                .verifyComplete();

        verify(comparisonAnalyzer).analyzeProducts(List.of(), null);
        verifyNoInteractions(productService, productMapper);
    }

    @Test
    void shouldThrowExceptionWhenTooManyProductsForComparison() {
        // Given
        List<String> tooManyIds = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11");

        // When & Then
        StepVerifier.create(useCase.compareProducts(tooManyIds))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(productService, productMapper, comparisonAnalyzer);
    }

    @Test
    void shouldGetAllProductsPaginated() {
        // Given
        int page = 0;
        int size = 10;
        long totalElements = 20L;

        when(productService.count()).thenReturn(Mono.just(totalElements));
        when(productService.findAllPaginated(page, size))
                .thenReturn(Flux.just(testProduct1, testProduct2));
        when(productMapper.toDto(testProduct1)).thenReturn(testProductDto1);
        when(productMapper.toDto(testProduct2)).thenReturn(testProductDto2);

        // When & Then
        StepVerifier.create(useCase.getAllProductsPaginated(page, size))
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(List.of(testProductDto1, testProductDto2), result.getContent());
                    assertEquals(page, result.getPage());
                    assertEquals(size, result.getSize());
                    assertEquals(totalElements, result.getTotalElements());
                })
                .verifyComplete();

        verify(productService).count();
        verify(productService).findAllPaginated(page, size);
        verify(productMapper).toDto(testProduct1);
        verify(productMapper).toDto(testProduct2);
    }

    @Test
    void shouldThrowExceptionForInvalidPaginationParameters() {
        // Test negative page
        StepVerifier.create(useCase.getAllProductsPaginated(-1, 10))
                .expectError(IllegalArgumentException.class)
                .verify();

        // Test zero size
        StepVerifier.create(useCase.getAllProductsPaginated(0, 0))
                .expectError(IllegalArgumentException.class)
                .verify();

        // Test size exceeding maximum
        StepVerifier.create(useCase.getAllProductsPaginated(0, 101))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(productService, productMapper);
    }

    @Test
    void shouldGetAllProducts() {
        // Given
        long totalElements = 2L;

        when(productService.count()).thenReturn(Mono.just(totalElements));
        when(productService.findAll()).thenReturn(Flux.just(testProduct1, testProduct2));
        when(productMapper.toDto(testProduct1)).thenReturn(testProductDto1);
        when(productMapper.toDto(testProduct2)).thenReturn(testProductDto2);

        // When & Then
        StepVerifier.create(useCase.getAllProducts())
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(List.of(testProductDto1, testProductDto2), result.getContent());
                    assertEquals(0, result.getPage());
                    assertEquals(2, result.getSize());
                    assertEquals(totalElements, result.getTotalElements());
                })
                .verifyComplete();

        verify(productService).count();
        verify(productService).findAll();
        verify(productMapper).toDto(testProduct1);
        verify(productMapper).toDto(testProduct2);
    }

    @Test
    void shouldHandleErrorWhenGettingProductById() {
        // Given
        RuntimeException testError = new RuntimeException("Database error");
        when(productService.findById("1")).thenReturn(Mono.error(testError));

        // When & Then
        StepVerifier.create(useCase.getProductById("1"))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleErrorWhenComparingProducts() {
        // Given
        List<String> productIds = List.of("1", "2");
        RuntimeException testError = new RuntimeException("Comparison error");
        
        when(productService.findProductsForComparison(productIds))
                .thenReturn(Flux.error(testError));

        // When & Then
        StepVerifier.create(useCase.compareProducts(productIds))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldHandleErrorWhenGettingPaginatedProducts() {
        // Given
        RuntimeException testError = new RuntimeException("Database error");
        when(productService.count()).thenReturn(Mono.error(testError));

        // When & Then
        StepVerifier.create(useCase.getAllProductsPaginated(0, 10))
                .expectError(RuntimeException.class)
                .verify();
    }
}