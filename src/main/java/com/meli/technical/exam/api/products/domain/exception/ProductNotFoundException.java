package com.meli.technical.exam.api.products.domain.exception;

public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }

}