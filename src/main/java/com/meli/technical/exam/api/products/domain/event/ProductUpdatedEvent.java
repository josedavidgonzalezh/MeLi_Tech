package com.meli.technical.exam.api.products.domain.event;

import com.meli.technical.exam.api.products.domain.model.ProductId;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

@Getter
public class ProductUpdatedEvent extends DomainEvent {

    private final ProductId productId;
    private final Map<String, Object> changedFields;
    private final String updateReason;

    public ProductUpdatedEvent(@NonNull ProductId productId,
                               @NonNull Map<String, Object> changedFields,
                               String updateReason) {
        super();
        this.productId = productId;
        this.changedFields = changedFields;
        this.updateReason = updateReason != null ? updateReason : "MANUAL_UPDATE";
    }

    @Override
    public String getEventType() {
        return "ProductUpdatedEvent";
    }
}