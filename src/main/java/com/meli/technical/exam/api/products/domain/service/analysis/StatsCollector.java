package com.meli.technical.exam.api.products.domain.service.analysis;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;

import java.util.List;

public interface StatsCollector<T> {
    T collect(List<ProductDto> products);
}