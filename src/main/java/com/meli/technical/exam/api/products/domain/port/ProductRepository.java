package com.meli.technical.exam.api.products.domain.port;

import com.meli.technical.exam.api.products.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductRepository {
    
    Mono<Product> findById(String id);
    
    Flux<Product> findByIds(List<String> ids);
    
    Flux<Product> findAll();
    
    Mono<Long> count();
    
    Flux<Product> findAllPaginated(int page, int size);
}