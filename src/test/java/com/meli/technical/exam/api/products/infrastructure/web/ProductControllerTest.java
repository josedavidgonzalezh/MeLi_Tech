package com.meli.technical.exam.api.products.infrastructure.web;

import com.meli.technical.exam.api.products.application.dto.ComparisonResponseDto;
import com.meli.technical.exam.api.products.application.dto.PaginatedResponseDto;
import com.meli.technical.exam.api.products.application.dto.ProductDto;
import com.meli.technical.exam.api.products.application.usecase.ProductComparisonUseCase;
import com.meli.technical.exam.api.products.shared.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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
                .jsonPath("$.error").isEqualTo("Not Found");
    }

    @Test
    void shouldCompareProducts() {
        ProductDto product1 = new ProductDto(
            "1", "Product 1", "url1", "desc1",
            new BigDecimal("99.99"), 4.5, List.of()
        );
        ProductDto product2 = new ProductDto(
            "2", "Product 2", "url2", "desc2",
            new BigDecimal("199.99"), 4.0, List.of()
        );
        
        List<String> requestedIds = List.of("1", "2");
        ComparisonResponseDto response = new ComparisonResponseDto(
            List.of(product1, product2), requestedIds
        );

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
                .jsonPath("$.requestedIds").isArray();
    }

    @Test
    void shouldReturnBadRequestForEmptyCompareIds() {
        webTestClient.get()
                .uri("/api/v1/products/compare?ids=")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.products").isArray()
                .jsonPath("$.totalProducts").isEqualTo(0);
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
}