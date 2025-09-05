package com.meli.technical.exam.api.products.shared.exception;

public class ProductDataException extends RuntimeException {
    
    public ProductDataException(String message, Throwable cause) {
        super(message, cause);
    }
}