package com.meli.technical.exam.api.products.application.usecase;

import com.meli.technical.exam.api.products.application.dto.ComparisonResponseDto;
import com.meli.technical.exam.api.products.application.dto.PaginatedResponseDto;
import com.meli.technical.exam.api.products.application.dto.ProductDto;
import com.meli.technical.exam.api.products.application.mapper.ProductMapper;
import com.meli.technical.exam.api.products.domain.service.ProductService;
import com.meli.technical.exam.api.products.shared.exception.ProductNotFoundException;
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

    public ProductComparisonUseCase(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    public Mono<ProductDto> getProductById(String id) {
        logger.info("Getting product by id: {}", id);
        
        return productService.findById(id)
                .map(productMapper::toDto)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found with id: " + id)))
                .doOnSuccess(product -> logger.info("Successfully retrieved product: {}", product.getName()))
                .doOnError(error -> logger.error("Failed to get product by id: {}", id, error));
    }

    public Mono<ComparisonResponseDto> compareProducts(List<String> productIds) {
        logger.info("Comparing products with ids: {}", productIds);
        
        if (productIds == null || productIds.isEmpty()) {
            logger.warn("Empty or null product IDs provided for comparison");
            return Mono.just(new ComparisonResponseDto(List.of(), List.of()));
        }

        if (productIds.size() > 10) {
            logger.warn("Too many products requested for comparison: {}", productIds.size());
            return Mono.error(new IllegalArgumentException("Cannot compare more than 10 products at once"));
        }

        return productService.findProductsForComparison(productIds)
                .map(productMapper::toDto)
                .collectList()
                .map(products -> {
                    ComparisonResponseDto response = new ComparisonResponseDto(products, productIds);
                    logger.info("Successfully compared {} products out of {} requested", 
                               products.size(), productIds.size());
                    
                    if (products.size() < productIds.size()) {
                        logger.warn("Some products were not found. Requested: {}, Found: {}", 
                                   productIds.size(), products.size());
                    }
                    
                    return response;
                })
                .doOnError(error -> logger.error("Failed to compare products: {}", productIds, error));
    }

    public Mono<PaginatedResponseDto<ProductDto>> getAllProductsPaginated(int page, int size) {
        logger.info("Getting paginated products - page: {}, size: {}", page, size);
        
        if (page < 0 || size <= 0 || size > 100) {
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
                                logger.info("Successfully retrieved {} products for page {}", 
                                           products.size(), page);
                                return response;
                            })
                )
                .doOnError(error -> logger.error("Failed to get paginated products", error));
    }

    public Mono<PaginatedResponseDto<ProductDto>> getAllProducts() {
        logger.info("Getting all products");
        
        return productService.count()
                .flatMap(totalElements -> 
                    productService.findAll()
                            .map(productMapper::toDto)
                            .collectList()
                            .map(products -> {
                                PaginatedResponseDto<ProductDto> response = 
                                    new PaginatedResponseDto<>(products, 0, products.size(), totalElements);
                                logger.info("Successfully retrieved all {} products", products.size());
                                return response;
                            })
                )
                .doOnError(error -> logger.error("Failed to get all products", error));
    }
}