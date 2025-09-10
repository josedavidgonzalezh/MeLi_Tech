package com.meli.technical.exam.api.products.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductComparisonIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldGetAllProductsPaginated() {
        webTestClient.get()
                .uri("/api/v1/products?page=0&size=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(5)
                .jsonPath("$.totalElements").isNumber();
    }

    @Test
    void shouldGetProductById() {
        webTestClient.get()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.name").exists()
                .jsonPath("$.price").exists()
                .jsonPath("$.rating").exists();
    }

    @Test
    void shouldReturn404ForNonexistentProduct() {
        webTestClient.get()
                .uri("/api/v1/products/nonexistent")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCompareMultipleProducts() {
        webTestClient.get()
                .uri("/api/v1/products/compare?ids=1,2,3")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.products").isArray()
                .jsonPath("$.totalProducts").isNumber()
                .jsonPath("$.requestedIds").isArray()
                .jsonPath("$.requestedIds.length()").isEqualTo(3)
                .jsonPath("$.comparisonTimestamp").exists()
                .jsonPath("$.priceAnalysis").exists()
                .jsonPath("$.ratingAnalysis").exists()
                .jsonPath("$.specificationAnalysis").exists()
                .jsonPath("$.recommendations").isArray()
                .jsonPath("$.summary").exists();
    }

    @Test
    void shouldCompareSingleProduct() {
        webTestClient.get()
                .uri("/api/v1/products/compare?ids=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.products").isArray()
                .jsonPath("$.products.length()").isEqualTo(1)
                .jsonPath("$.totalProducts").isEqualTo(1)
                .jsonPath("$.requestedIds[0]").isEqualTo("1");
    }

    @Test
    void shouldHandleEmptyComparisonRequest() {
        webTestClient.get()
                .uri("/api/v1/products/compare?ids=")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.validationErrors").isArray();
    }

    @Test
    void shouldHandlePartiallyFoundProducts() {
        webTestClient.get()
                .uri("/api/v1/products/compare?ids=1,nonexistent,2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.products").isArray()
                .jsonPath("$.requestedIds.length()").isEqualTo(3)
                .jsonPath("$.totalProducts").isNumber()
                .jsonPath("$.comparisonTimestamp").exists();
    }

    @Test
    void shouldValidatePaginationBoundaries() {
        webTestClient.get()
                .uri("/api/v1/products?page=-1&size=0")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10);
    }

    @Test
    void shouldHandleMaxPageSize() {
        webTestClient.get()
                .uri("/api/v1/products?page=0&size=200")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10);
    }

    @Test
    void shouldUseGetAllForLargePageSize() {
        webTestClient.get()
                .uri("/api/v1/products?page=0&size=50")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.totalElements").isNumber();
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
    void shouldHandleComparisonWithDuplicatesAndWhitespace() {
        webTestClient.get()
                .uri("/api/v1/products/compare?ids= 1 , 1 , 2 , ")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.validationErrors").isArray();
    }

    @Test
    void shouldReturnAnalysisDataInComparison() {
        webTestClient.get()
                .uri("/api/v1/products/compare?ids=1,2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.priceAnalysis").exists()
                .jsonPath("$.ratingAnalysis").exists()
                .jsonPath("$.specificationAnalysis").exists()
                .jsonPath("$.recommendations").isArray()
                .jsonPath("$.summary").exists();
    }

    @Test
    void shouldHandleDefaultPaginationValues() {
        webTestClient.get()
                .uri("/api/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.page").isEqualTo(0)
                .jsonPath("$.size").isEqualTo(10)
                .jsonPath("$.totalElements").isNumber();
    }
}