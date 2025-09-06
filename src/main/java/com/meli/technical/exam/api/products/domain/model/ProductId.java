package com.meli.technical.exam.api.products.domain.model;

import com.meli.technical.exam.api.products.domain.exception.InvalidProductException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
public class ProductId {

    private final String value;

    private ProductId(String value) {
        this.value = value;
    }

    public static ProductId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidProductException("Product ID cannot be null or empty");
        }

        String trimmedValue = value.trim();

        return new ProductId(trimmedValue);
    }

    //For the future to POST new items and generate randoms GUID
    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }
}