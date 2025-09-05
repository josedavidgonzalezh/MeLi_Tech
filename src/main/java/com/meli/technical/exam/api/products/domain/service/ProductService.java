package com.meli.technical.exam.api.products.domain.service;

import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.port.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Product> findById(String id) {
        return productRepository.findById(id)
                .doOnError(error -> logger.error("Error finding product by id: {}", id, error));
    }

    public Flux<Product> findProductsForComparison(List<String> productIds) {
        
        if (productIds == null || productIds.isEmpty()) {
            logger.warn("Empty product IDs list provided for comparison");
            return Flux.empty();
        }

        return productRepository.findByIds(productIds)
                .doOnError(error -> logger.error("Error finding products for comparison", error));
    }

    public Flux<Product> findAll() {
        return productRepository.findAll()
                .doOnError(error -> logger.error("Error finding all products", error));
    }

    public Flux<Product> findAllPaginated(int page, int size) {
        if (page < 0 || size <= 0) {
            return Flux.error(new IllegalArgumentException("Page must be non-negative and size must be positive"));
        }

        return productRepository.findAllPaginated(page, size)
                .doOnError(error -> logger.error("Error finding paginated products", error));
    }

    public Mono<Long> count() {
        return productRepository.count()
                .doOnError(error -> logger.error("Error counting products", error));
    }
}