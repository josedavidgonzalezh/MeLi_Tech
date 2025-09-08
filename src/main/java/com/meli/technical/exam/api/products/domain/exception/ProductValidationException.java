package com.meli.technical.exam.api.products.domain.exception;

import com.meli.technical.exam.api.products.application.dto.response.ValidationError;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductValidationException extends RuntimeException {

    private final List<String> validationErrors;
    private final List<ValidationError> fieldErrors;

    public ProductValidationException(String message, List<ValidationError> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
        this.validationErrors = List.of();
    }

    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }
}