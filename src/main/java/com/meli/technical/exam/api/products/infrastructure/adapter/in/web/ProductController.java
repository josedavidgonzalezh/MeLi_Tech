package com.meli.technical.exam.api.products.infrastructure.adapter.in.web;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.response.PaginatedResponseDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.ComparisonResponseDto;
import com.meli.technical.exam.api.products.application.usecase.ProductComparisonUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductComparisonUseCase productComparisonUseCase;
    private static final int MAX_PAGE_SIZE = 100;

    public ProductController(ProductComparisonUseCase productComparisonUseCase) {
        this.productComparisonUseCase = productComparisonUseCase;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProductDto>> getProductById(@PathVariable String id) {
        
        return productComparisonUseCase.getProductById(id)
                .map(ResponseEntity::ok)
                .doOnError(error -> logger.error("Error retrieving product by ID: {}", id, error));
    }

    @GetMapping("/compare")
    public Mono<ResponseEntity<ComparisonResponseDto>> compareProducts(
            @RequestParam("ids") String ids) {
        
        if (ids == null || ids.trim().isEmpty()) {
            return productComparisonUseCase.compareProducts(List.of())
                    .map(ResponseEntity::ok);
        }

        List<String> productIds = Arrays.asList(ids.split(","));
        
        // Clean and validate product IDs
        List<String> cleanIds = productIds.stream()
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .distinct()
                .toList();

        if (cleanIds.isEmpty()) {
            return productComparisonUseCase.compareProducts(List.of())
                    .map(ResponseEntity::ok);
        }

        return productComparisonUseCase.compareProducts(cleanIds)
                .map(ResponseEntity::ok)
                .doOnError(error -> logger.error("Error comparing products: {}", cleanIds, error));
    }

    @GetMapping
    public Mono<ResponseEntity<PaginatedResponseDto<ProductDto>>> getProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        // Validate pagination parameters
        final int validatedPage = page < 0 ? 0 : page;
        final int validatedSize = (size <= 0 || size > MAX_PAGE_SIZE) ? 10 : size;
        
        if (page < 0) {
            logger.warn("Invalid page parameter: {}. Using default page 0", page);
        }
        
        if (size <= 0 || size > MAX_PAGE_SIZE) {
            logger.warn("Invalid size parameter: {}. Using default size 10", size);
        }

        if (validatedPage == 0 && validatedSize >= 50) {
            return productComparisonUseCase.getAllProducts()
                    .map(ResponseEntity::ok)
                    .doOnError(error -> logger.error("Error retrieving all products", error));
        }

        return productComparisonUseCase.getAllProductsPaginated(validatedPage, validatedSize)
                .map(response -> {
                    return ResponseEntity.ok(response);
                })
                .doOnError(error -> logger.error("Error retrieving paginated products", error));
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<String>> healthCheck() {
        return Mono.just(ResponseEntity.ok("Products API is healthy"));
    }
}