package com.meli.technical.exam.api.products.application.usecase;


import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.response.PaginatedResponseDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.ComparisonResponseDto;
import com.meli.technical.exam.api.products.application.mapper.ProductMapper;
import com.meli.technical.exam.api.products.domain.exception.ProductNotFoundException;
import com.meli.technical.exam.api.products.domain.service.ProductComparisonAnalyzerService;
import com.meli.technical.exam.api.products.domain.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProductComparisonUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductComparisonUseCase.class);
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductComparisonAnalyzerService comparisonAnalyzer;
    private final int MAX_SIZE = 10;
    private final int MAX_PAGE_SIZE = 100;

    public ProductComparisonUseCase(ProductService productService, ProductMapper productMapper, 
                                   ProductComparisonAnalyzerService comparisonAnalyzer) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.comparisonAnalyzer = comparisonAnalyzer;
    }

    public Mono<ProductDto> getProductById(String id) {
        return productService.findById(id)
                .map(productMapper::toDto)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found with id: " + id)))
                .doOnError(error -> logger.error("Failed to get product by id: {}", id, error));
    }

    public Mono<ComparisonResponseDto> compareProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return comparisonAnalyzer.analyzeProducts(List.of(), productIds);
        }

        //Constante puede ir en otro archivo
        if (productIds.size() > MAX_SIZE) {
            return Mono.error(new IllegalArgumentException("Cannot compare more than 10 products at once"));
        }

        return productService.findProductsForComparison(productIds)
                .map(productMapper::toDto)
                .collectList()
                .flatMap(products -> {
                    if (products.size() < productIds.size()) {
                        logger.warn("Some products were not found. Requested: {}, Found: {}", 
                                   productIds.size(), products.size());
                    }
                    
                    return comparisonAnalyzer.analyzeProducts(products, productIds);
                })
                .doOnError(error -> logger.error("Failed to compare products: {}", productIds, error));
    }

    public Mono<PaginatedResponseDto<ProductDto>> getAllProductsPaginated(int page, int size) {
        
        if (page < 0 || size <= 0 || size > MAX_PAGE_SIZE) {
            logger.warn("Invalid pagination parameters - page: {}, size: {}", page, size);
            return Mono.error(new IllegalArgumentException(
                "Page must be non-negative, size must be positive and not exceed 100"));
        }

        return productService.count()
                .flatMap(totalElements -> 
                    productService.findAllPaginated(page, size)
                            .map(productMapper::toDto)
                            .collectList()
                            .map(products -> {
                                PaginatedResponseDto<ProductDto> response = 
                                    new PaginatedResponseDto<>(products, page, size, totalElements);
                                return response;
                            })
                )
                .doOnError(error -> logger.error("Failed to get paginated products", error));
    }

    public Mono<PaginatedResponseDto<ProductDto>> getAllProducts() {
        return productService.count()
                .flatMap(totalElements -> 
                    productService.findAll()
                            .map(productMapper::toDto)
                            .collectList()
                            .map(products -> {
                                PaginatedResponseDto<ProductDto> response = 
                                    new PaginatedResponseDto<>(products, 0, products.size(), totalElements);
                                return response;
                            })
                )
                .doOnError(error -> logger.error("Failed to get all products", error));
    }
}