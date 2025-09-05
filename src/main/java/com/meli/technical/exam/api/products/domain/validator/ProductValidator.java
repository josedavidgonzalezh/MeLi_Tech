package com.meli.technical.exam.api.products.domain.validator;

import com.meli.technical.exam.api.products.domain.model.Product;

public interface ProductValidator {
    void validate(Product product);
    
    default boolean isValid(Product product) {
        try {
            validate(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}