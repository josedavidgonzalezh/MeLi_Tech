package com.meli.technical.exam.api.products.domain.event;

import com.meli.technical.exam.api.products.domain.model.ProductId;
import lombok.ToString;

import java.util.Objects;

@ToString
public class ProductViewedEvent extends DomainEvent {
    
    private final ProductId productId;
    private final String source;
    
    public ProductViewedEvent(ProductId productId, String source) {
        super();
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.source = source != null ? source : "UNKNOWN";
    }
    
    public ProductId getProductId() {
        return productId;
    }
    
    public String getSource() {
        return source;
    }
    
    @Override
    public String getEventType() {
        return "ProductViewedEvent";
    }
}