package com.meli.technical.exam.api.products.shared.exception;

public class InvalidProductException extends RuntimeException {
    
    public InvalidProductException(String message) {
        super(message);
    }
}