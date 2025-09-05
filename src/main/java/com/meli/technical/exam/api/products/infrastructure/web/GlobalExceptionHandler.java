package com.meli.technical.exam.api.products.infrastructure.web;

import com.meli.technical.exam.api.products.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFoundException(ProductNotFoundException ex) {
        logger.warn("Product not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Product not found", ex.getMessage());
    }
    
    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidProductException(InvalidProductException ex) {
        logger.warn("Invalid product: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid product", ex.getMessage());
    }
    
    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<Map<String, Object>> handleProductValidationException(ProductValidationException ex) {
        logger.warn("Product validation failed: {}", ex.getMessage());
        Map<String, Object> errorResponse = buildErrorResponseMap(HttpStatus.BAD_REQUEST, "Validation failed", ex.getMessage());
        errorResponse.put("validationErrors", ex.getValidationErrors());
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(ProductComparisonException.class)
    public ResponseEntity<Map<String, Object>> handleProductComparisonException(ProductComparisonException ex) {
        logger.warn("Product comparison failed: {}", ex.getMessage());
        Map<String, Object> errorResponse = buildErrorResponseMap(HttpStatus.BAD_REQUEST, "Comparison failed", ex.getMessage());
        errorResponse.put("requestedProductIds", ex.getRequestedProductIds());
        errorResponse.put("foundProductIds", ex.getFoundProductIds());
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(ProductDataException.class)
    public ResponseEntity<Map<String, Object>> handleProductDataException(ProductDataException ex) {
        logger.error("Product data error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Data access error", "Unable to access product data");
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid argument", ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "An unexpected error occurred");
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(buildErrorResponseMap(status, error, message));
    }
    
    private Map<String, Object> buildErrorResponseMap(HttpStatus status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", Instant.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return errorResponse;
    }
}