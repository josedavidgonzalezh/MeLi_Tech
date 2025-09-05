package com.meli.technical.exam.api.products.infrastructure.web;

import com.meli.technical.exam.api.products.application.dto.ComparisonResponseDto;
import com.meli.technical.exam.api.products.application.dto.PaginatedResponseDto;
import com.meli.technical.exam.api.products.application.dto.ProductDto;
import com.meli.technical.exam.api.products.application.usecase.ProductComparisonUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductComparisonUseCase productComparisonUseCase;

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
            return Mono.just(ResponseEntity.badRequest()
                    .body(new ComparisonResponseDto(List.of(), List.of())));
        }

        List<String> productIds = Arrays.asList(ids.split(","));
        
        // Clean and validate product IDs
        List<String> cleanIds = productIds.stream()
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .distinct()
                .toList();

        if (cleanIds.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(new ComparisonResponseDto(List.of(), List.of())));
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
        final int validatedSize = (size <= 0 || size > 100) ? 10 : size;
        
        if (page < 0) {
            logger.warn("Invalid page parameter: {}. Using default page 0", page);
        }
        
        if (size <= 0 || size > 100) {
            logger.warn("Invalid size parameter: {}. Using default size 10", size);
        }

        // If requesting first page with large size, return all products
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