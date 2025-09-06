package com.meli.technical.exam.api.products.infrastructure.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.mapper.ProductMapper;
import com.meli.technical.exam.api.products.domain.exception.ProductDataException;
import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.repository.ProductRepository;
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
                    productsCache.put(product.getId().getValue(), product);
                }
            }
            
        } catch (IOException e) {
            throw new ProductDataException("Failed to initialize product data", e);
        }
    }

    @Override
    public Mono<Product> findById(String id) {
        
        if (id == null || id.trim().isEmpty()) {
            return Mono.empty();
        }
        
        Product product = productsCache.get(id.trim());
        return product != null ? Mono.just(product) : Mono.empty();
    }

    @Override
    public Flux<Product> findByIds(List<String> ids) {
        
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
                });
    }

    @Override
    public Flux<Product> findAll() {
        return Flux.fromIterable(new ArrayList<>(productsCache.values()));
    }

    @Override
    public Mono<Long> count() {
        long count = productsCache.size();
        return Mono.just(count);
    }

    @Override
    public Flux<Product> findAllPaginated(int page, int size) {
        
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
        
        return Flux.fromIterable(paginatedProducts);
    }

}