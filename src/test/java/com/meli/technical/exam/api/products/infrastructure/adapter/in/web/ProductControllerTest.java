package com.meli.technical.exam.api.products.infrastructure.adapter.in.web;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.request.SpecificationDto;
import com.meli.technical.exam.api.products.application.dto.response.PaginatedResponseDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.ComparisonResponseDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.ComparisonSummaryDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.PriceAnalysisDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.RatingAnalysisDto;
import com.meli.technical.exam.api.products.application.usecase.ProductComparisonUseCase;
import com.meli.technical.exam.api.products.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductComparisonUseCase productComparisonUseCase;

    @Test
    void shouldGetProductById() {
        ProductDto productDto = new ProductDto(
            "1", "Test Product", "url", "desc", 
            new BigDecimal("99.99"), 4.5, List.of()
        );
        
        when(productComparisonUseCase.getProductById("1"))
                .thenReturn(Mono.just(productDto));

        webTestClient.get()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("Test Product")
                .jsonPath("$.price").isEqualTo(99.99);
    }

    @Test
    void shouldReturn404WhenProductNotFound() {
        when(productComparisonUseCase.getProductById("nonexistent"))
                .thenReturn(Mono.error(new ProductNotFoundException("Product not found")));

        webTestClient.get()
                .uri("/api/v1/products/nonexistent")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Product not found");
    }

    @Test
    void shouldCompareProducts() {
        ProductDto product1 = new ProductDto(
            "1", "Product 1", "url1", "desc1",
            new BigDecimal("99.99"), 4.5, 
            List.of(new SpecificationDto("brand", "Brand A"))
        );
        ProductDto product2 = new ProductDto(
            "2", "Product 2", "url2", "desc2",
            new BigDecimal("199.99"), 4.0,
            List.of(new SpecificationDto("brand", "Brand B"))
        );
        
        List<String> requestedIds = List.of("1", "2");
        ComparisonResponseDto response = ComparisonResponseDto.builder()
                .products(List.of(product1, product2))
                .totalProducts(2)
                .requestedIds(requestedIds)
                .comparisonTimestamp(Instant.now())
                .priceAnalysis(PriceAnalysisDto.builder()
                        .priceRange(new BigDecimal("100.00"))
                        .averagePrice(new BigDecimal("149.99"))
                        .build())
                .ratingAnalysis(RatingAnalysisDto.builder()
                        .averageRating(4.25)
                        .build())
                .summary(ComparisonSummaryDto.builder()
                        .conclusion("Product 1 offers better value")
                        .insights(List.of("Product 1 offers better value"))
                        .build())
                .build();

        when(productComparisonUseCase.compareProducts(requestedIds))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/products/compare?ids=1,2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.products").isArray()
                .jsonPath("$.products.length()").isEqualTo(2)
                .jsonPath("$.totalProducts").isEqualTo(2)
                .jsonPath("$.requestedIds").isArray()
                .jsonPath("$.priceAnalysis").exists()
                .jsonPath("$.ratingAnalysis").exists()
                .jsonPath("$.summary").exists()
                .jsonPath("$.comparisonTimestamp").exists();
    }

    @Test
    void shouldReturnEmptyComparisonForEmptyIds() {
        ComparisonResponseDto emptyResponse = ComparisonResponseDto.builder()
                .products(List.of())
                .totalProducts(0)
                .requestedIds(List.of())
                .comparisonTimestamp(Instant.now())
                .build();

        when(productComparisonUseCase.compareProducts(List.of()))
                .thenReturn(Mono.just(emptyResponse));

        webTestClient.get()
                .uri("/api/v1/products/compare?ids=")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.validationErrors").isArray();
    }

    @Test
    void shouldGetPaginatedProducts() {
        ProductDto productDto = new ProductDto(
            "1", "Product 1", "url", "desc",
            new BigDecimal("99.99"), 4.5, List.of()
        );
        
        PaginatedResponseDto<ProductDto> response = new PaginatedResponseDto<>(
            List.of(productDto), 0, 10, 1L
        );

        when(productComparisonUseCase.getAllProductsPaginated(0, 10))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/products?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10)
                .jsonPath("$.totalElements").isEqualTo(1);
    }

    @Test
    void shouldUseDefaultPaginationParameters() {
        PaginatedResponseDto<ProductDto> response = new PaginatedResponseDto<>(
            List.of(), 0, 10, 0L
        );

        when(productComparisonUseCase.getAllProductsPaginated(0, 10))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10);
    }

    @Test
    void shouldReturnHealthCheck() {
        webTestClient.get()
                .uri("/api/v1/products/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Products API is healthy");
    }

    @Test
    void shouldHandleInvalidPaginationParameters() {
        PaginatedResponseDto<ProductDto> response = new PaginatedResponseDto<>(
            List.of(), 0, 10, 0L
        );

        when(productComparisonUseCase.getAllProductsPaginated(0, 10))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/products?page=-1&size=0")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10);
    }

    @Test
    void shouldUseGetAllProductsWhenLargePageSize() {
        PaginatedResponseDto<ProductDto> response = new PaginatedResponseDto<>(
            List.of(), 0, 100, 0L
        );

        when(productComparisonUseCase.getAllProducts())
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/products?page=0&size=50")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray();
    }

    @Test
    void shouldHandleCompareWithWhitespaceAndDuplicates() {
        ProductDto product1 = new ProductDto(
            "1", "Product 1", "url1", "desc1",
            new BigDecimal("99.99"), 4.5, List.of()
        );
        
        List<String> cleanIds = List.of("1");
        ComparisonResponseDto response = ComparisonResponseDto.builder()
                .products(List.of(product1))
                .totalProducts(1)
                .requestedIds(cleanIds)
                .comparisonTimestamp(Instant.now())
                .build();

        when(productComparisonUseCase.compareProducts(cleanIds))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/api/v1/products/compare?ids= 1 , 1 , ")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.validationErrors").isArray();
    }

    @Test
    void shouldHandleCompareWithNullIds() {
        ComparisonResponseDto emptyResponse = ComparisonResponseDto.builder()
                .products(List.of())
                .totalProducts(0)
                .requestedIds(List.of())
                .comparisonTimestamp(Instant.now())
                .build();

        when(productComparisonUseCase.compareProducts(List.of()))
                .thenReturn(Mono.just(emptyResponse));

        webTestClient.get()
                .uri("/api/v1/products/compare")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}