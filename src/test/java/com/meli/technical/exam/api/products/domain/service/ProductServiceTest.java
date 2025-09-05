package com.meli.technical.exam.api.products.domain.service;

import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
        testProduct = new Product(
            "1", "Test Product", "url", "desc", 
            new BigDecimal("99.99"), 4.5, List.of()
        );
    }

    @Test
    void shouldFindProductById() {
        when(productRepository.findById("1")).thenReturn(Mono.just(testProduct));

        StepVerifier.create(productService.findById("1"))
                .expectNext(testProduct)
                .verifyComplete();

        verify(productRepository).findById("1");
    }

    @Test
    void shouldReturnEmptyWhenProductNotFound() {
        when(productRepository.findById("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(productService.findById("nonexistent"))
                .verifyComplete();

        verify(productRepository).findById("nonexistent");
    }

    @Test
    void shouldFindProductsForComparison() {
        List<String> productIds = List.of("1", "2");
        Product product2 = new Product(
            "2", "Product 2", "url2", "desc2", 
            new BigDecimal("199.99"), 4.0, List.of()
        );

        when(productRepository.findByIds(productIds))
                .thenReturn(Flux.just(testProduct, product2));

        StepVerifier.create(productService.findProductsForComparison(productIds))
                .expectNext(testProduct)
                .expectNext(product2)
                .verifyComplete();

        verify(productRepository).findByIds(productIds);
    }

    @Test
    void shouldReturnEmptyWhenProductIdsListIsEmpty() {
        StepVerifier.create(productService.findProductsForComparison(List.of()))
                .verifyComplete();

        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldReturnEmptyWhenProductIdsListIsNull() {
        StepVerifier.create(productService.findProductsForComparison(null))
                .verifyComplete();

        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldFindAllProducts() {
        when(productRepository.findAll()).thenReturn(Flux.just(testProduct));

        StepVerifier.create(productService.findAll())
                .expectNext(testProduct)
                .verifyComplete();

        verify(productRepository).findAll();
    }

    @Test
    void shouldFindAllProductsPaginated() {
        when(productRepository.findAllPaginated(0, 10))
                .thenReturn(Flux.just(testProduct));

        StepVerifier.create(productService.findAllPaginated(0, 10))
                .expectNext(testProduct)
                .verifyComplete();

        verify(productRepository).findAllPaginated(0, 10);
    }

    @Test
    void shouldReturnErrorWhenInvalidPaginationParameters() {
        StepVerifier.create(productService.findAllPaginated(-1, 10))
                .expectError(IllegalArgumentException.class)
                .verify();

        StepVerifier.create(productService.findAllPaginated(0, 0))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldCountProducts() {
        when(productRepository.count()).thenReturn(Mono.just(5L));

        StepVerifier.create(productService.count())
                .expectNext(5L)
                .verifyComplete();

        verify(productRepository).count();
    }
}