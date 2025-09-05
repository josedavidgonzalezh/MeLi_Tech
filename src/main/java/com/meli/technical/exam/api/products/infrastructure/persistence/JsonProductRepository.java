package com.meli.technical.exam.api.products.infrastructure.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.technical.exam.api.products.application.dto.ProductDto;
import com.meli.technical.exam.api.products.application.mapper.ProductMapper;
import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.port.ProductRepository;
import com.meli.technical.exam.api.products.shared.exception.ProductDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JsonProductRepository implements ProductRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonProductRepository.class);
    private static final String PRODUCTS_DATA_PATH = "data/products.json";
    
    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;
    private final Map<String, Product> productsCache;

    public JsonProductRepository(ObjectMapper objectMapper, ProductMapper productMapper) {
        this.objectMapper = objectMapper;
        this.productMapper = productMapper;
        this.productsCache = new HashMap<>();
        loadProductsFromFile();
    }

    private void loadProductsFromFile() {
        logger.info("Loading products from JSON file: {}", PRODUCTS_DATA_PATH);
        
        try {
            ClassPathResource resource = new ClassPathResource(PRODUCTS_DATA_PATH);
            
            if (!resource.exists()) {
                logger.warn("Products file not found: {}. Using empty dataset.", PRODUCTS_DATA_PATH);
                return;
            }

            try (InputStream inputStream = resource.getInputStream()) {
                List<ProductDto> productDtos = objectMapper.readValue(
                    inputStream, 
                    new TypeReference<List<ProductDto>>() {}
                );
                
                for (ProductDto dto : productDtos) {
                    Product product = productMapper.toDomain(dto);
                    productsCache.put(product.getId(), product);
                }
                
                logger.info("Successfully loaded {} products from JSON file", productsCache.size());
            }
            
        } catch (IOException e) {
            logger.error("Failed to load products from JSON file: {}", PRODUCTS_DATA_PATH, e);
            throw new ProductDataException("Failed to initialize product data", e);
        }
    }

    @Override
    public Mono<Product> findById(String id) {
        logger.debug("Finding product by id: {}", id);
        
        if (id == null || id.trim().isEmpty()) {
            return Mono.empty();
        }
        
        Product product = productsCache.get(id.trim());
        return product != null ? Mono.just(product) : Mono.empty();
    }

    @Override
    public Flux<Product> findByIds(List<String> ids) {
        logger.debug("Finding products by ids: {}", ids);
        
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }

        return Flux.fromIterable(ids)
                .distinct()
                .mapNotNull(id -> {
                    if (id == null || id.trim().isEmpty()) {
                        return null;
                    }
                    return productsCache.get(id.trim());
                })
                .doOnNext(product -> logger.debug("Found product: {}", product.getName()));
    }

    @Override
    public Flux<Product> findAll() {
        logger.debug("Finding all products");
        
        return Flux.fromIterable(new ArrayList<>(productsCache.values()))
                .doOnNext(product -> logger.trace("Retrieved product: {}", product.getName()));
    }

    @Override
    public Mono<Long> count() {
        logger.debug("Counting total products");
        
        long count = productsCache.size();
        return Mono.just(count);
    }

    @Override
    public Flux<Product> findAllPaginated(int page, int size) {
        logger.debug("Finding paginated products - page: {}, size: {}", page, size);
        
        if (page < 0 || size <= 0) {
            return Flux.error(new IllegalArgumentException("Page must be non-negative and size must be positive"));
        }

        int startIndex = page * size;
        List<Product> allProducts = new ArrayList<>(productsCache.values());
        
        if (startIndex >= allProducts.size()) {
            return Flux.empty();
        }
        
        int endIndex = Math.min(startIndex + size, allProducts.size());
        List<Product> paginatedProducts = allProducts.subList(startIndex, endIndex);
        
        return Flux.fromIterable(paginatedProducts)
                .doOnNext(product -> logger.trace("Retrieved paginated product: {}", product.getName()));
    }

    public void refreshCache() {
        logger.info("Refreshing products cache");
        productsCache.clear();
        loadProductsFromFile();
    }

    public int getCacheSize() {
        return productsCache.size();
    }
}