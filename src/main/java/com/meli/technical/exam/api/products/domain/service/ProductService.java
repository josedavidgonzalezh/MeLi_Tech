package com.meli.technical.exam.api.products.domain.service;

import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.port.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Product> findById(String id) {
        logger.debug("Finding product by id: {}", id);
        return productRepository.findById(id)
                .doOnSuccess(product -> {
                    if (product != null) {
                        logger.debug("Found product: {}", product.getName());
                    } else {
                        logger.warn("Product not found with id: {}", id);
                    }
                })
                .doOnError(error -> logger.error("Error finding product by id: {}", id, error));
    }

    public Flux<Product> findProductsForComparison(List<String> productIds) {
        logger.debug("Finding products for comparison: {}", productIds);
        
        if (productIds == null || productIds.isEmpty()) {
            logger.warn("Empty product IDs list provided for comparison");
            return Flux.empty();
        }

        return productRepository.findByIds(productIds)
                .doOnNext(product -> logger.debug("Found product for comparison: {}", product.getName()))
                .doOnComplete(() -> logger.debug("Completed finding products for comparison"))
                .doOnError(error -> logger.error("Error finding products for comparison", error));
    }

    public Flux<Product> findAll() {
        logger.debug("Finding all products");
        return productRepository.findAll()
                .doOnNext(product -> logger.trace("Found product: {}", product.getName()))
                .doOnComplete(() -> logger.debug("Completed finding all products"))
                .doOnError(error -> logger.error("Error finding all products", error));
    }

    public Flux<Product> findAllPaginated(int page, int size) {
        logger.debug("Finding paginated products - page: {}, size: {}", page, size);
        
        if (page < 0 || size <= 0) {
            logger.warn("Invalid pagination parameters - page: {}, size: {}", page, size);
            return Flux.error(new IllegalArgumentException("Page must be non-negative and size must be positive"));
        }

        return productRepository.findAllPaginated(page, size)
                .doOnNext(product -> logger.trace("Found paginated product: {}", product.getName()))
                .doOnComplete(() -> logger.debug("Completed finding paginated products"))
                .doOnError(error -> logger.error("Error finding paginated products", error));
    }

    public Mono<Long> count() {
        logger.debug("Counting total products");
        return productRepository.count()
                .doOnSuccess(count -> logger.debug("Total products count: {}", count))
                .doOnError(error -> logger.error("Error counting products", error));
    }
}