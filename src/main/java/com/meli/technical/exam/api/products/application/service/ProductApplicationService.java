package com.meli.technical.exam.api.products.application.service;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.response.PaginatedResponseDto;
import com.meli.technical.exam.api.products.application.dto.response.comparison.ComparisonResponseDto;
import com.meli.technical.exam.api.products.application.usecase.ProductComparisonUseCase;
import com.meli.technical.exam.api.products.domain.event.DomainEventPublisher;
import com.meli.technical.exam.api.products.domain.event.ProductComparedEvent;
import com.meli.technical.exam.api.products.domain.event.ProductViewedEvent;
import com.meli.technical.exam.api.products.domain.model.ProductId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductApplicationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductApplicationService.class);
    
    private final ProductComparisonUseCase productComparisonUseCase;
    private final DomainEventPublisher eventPublisher;
    
    public ProductApplicationService(ProductComparisonUseCase productComparisonUseCase,
                                   DomainEventPublisher eventPublisher) {
        this.productComparisonUseCase = productComparisonUseCase;
        this.eventPublisher = eventPublisher;
    }
    
    public Mono<ProductDto> getProductById(String id) {
        return productComparisonUseCase.getProductById(id)
                .doOnSuccess(product -> {
                    if (product != null) {
                        eventPublisher.publish(new ProductViewedEvent(
                            ProductId.of(id), 
                            "API_DIRECT_ACCESS"
                        ));
                    }
                })
                .doOnError(error -> logger.error("Failed to retrieve product: {}", id, error));
    }
    
    public Mono<ComparisonResponseDto> compareProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            logger.warn("Empty product list provided for comparison");
            eventPublisher.publish(new ProductComparedEvent(
                List.of(), "Empty product list provided for comparison"));
            return productComparisonUseCase.compareProducts(List.of());
        }
        
        return productComparisonUseCase.compareProducts(productIds)
                .doOnSuccess(comparisonResult -> {
                    List<ProductId> domainProductIds = productIds.stream()
                            .map(ProductId::of)
                            .collect(Collectors.toList());
                    
                    eventPublisher.publish(new ProductComparedEvent(
                        domainProductIds,
                        String.format("Compared %d products successfully", comparisonResult.getProducts().size())
                    ));
                })
                .doOnError(error -> {
                    logger.error("Failed to compare products: {}", productIds, error);
                    
                    List<ProductId> domainProductIds = productIds.stream()
                            .map(ProductId::of)
                            .collect(Collectors.toList());
                    
                    eventPublisher.publish(new ProductComparedEvent(
                        domainProductIds,
                        "Comparison failed: " + error.getMessage()
                    ));
                });
    }
    
    public Mono<PaginatedResponseDto<ProductDto>> getAllProducts(int page, int size) {
        return productComparisonUseCase.getAllProductsPaginated(page, size)
                .doOnError(error -> logger.error("Failed to retrieve paginated products", error));
    }
    
    public Mono<PaginatedResponseDto<ProductDto>> getAllProducts() {
        return productComparisonUseCase.getAllProducts()
                .doOnError(error -> logger.error("Failed to retrieve all products", error));
    }
}