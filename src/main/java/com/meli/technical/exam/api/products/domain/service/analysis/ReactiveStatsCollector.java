package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveStatsCollector<T> {
    Mono<T> collectReactive(Flux<ProductDto> productFlux);
}