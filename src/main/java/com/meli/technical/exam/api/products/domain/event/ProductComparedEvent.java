package com.meli.technical.exam.api.products.domain.event;

import com.meli.technical.exam.api.products.domain.model.ProductId;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class ProductComparedEvent extends DomainEvent {

    private final List<ProductId> productIds;
    private final String comparisonResult;

    public ProductComparedEvent(List<ProductId> productIds, String comparisonResult) {
        super();
        this.productIds = Objects.requireNonNull(productIds, "Product IDs cannot be null");
        this.comparisonResult = Objects.requireNonNull(comparisonResult, "Comparison result cannot be null");
    }

    @Override
    public String getEventType() {
        return "ProductComparedEvent";
    }
}