package com.meli.technical.exam.api.products.infrastructure.web;

import com.meli.technical.exam.api.products.domain.exception.*;
import com.meli.technical.exam.api.products.infrastructure.web.utils.TraceIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFoundException(
            ProductNotFoundException ex, ServerWebExchange exchange) {
        logger.warn("Product not found: {}", ex.getMessage());
        String path = exchange.getRequest().getPath().value();
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "The requested product does not exist in our catalog", path);
    }
    
    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidProductException(InvalidProductException ex) {
        logger.warn("Invalid product: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid product", ex.getMessage());
    }
    
    @ExceptionHandler(ProductValidationException.class)
    public ResponseEntity<Map<String, Object>> handleProductValidationException(
            ProductValidationException ex, ServerWebExchange exchange) {
        logger.warn("Product validation failed: {}", ex.getMessage());
        String path = exchange.getRequest().getPath().value();
        Map<String, Object> errorResponse = buildErrorResponseMap(HttpStatus.BAD_REQUEST, "Validation Failed", "Invalid input provided", path);
        
        if (ex.hasFieldErrors()) {
            errorResponse.put("validationErrors", ex.getFieldErrors());
        } else {
            errorResponse.put("validationErrors", ex.getValidationErrors().stream()
                .map(error -> Map.of("message", error))
                .toList());
        }
        
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
    
    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessRuleViolationException(BusinessRuleViolationException ex) {
        logger.warn("Business rule violation: {}", ex.getMessage());
        Map<String, Object> errorResponse = buildErrorResponseMap(HttpStatus.UNPROCESSABLE_ENTITY, "Business Rule Violation", ex.getMessage());
        errorResponse.put("businessRule", ex.getBusinessRule());
        if (ex.getLimit() != null) {
            errorResponse.put("limit", ex.getLimit());
        }
        if (ex.getProvided() != null) {
            errorResponse.put("provided", ex.getProvided());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "An unexpected error occurred");
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(buildErrorResponseMap(status, error, message));
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String error, String message, String path) {
        return ResponseEntity.status(status).body(buildErrorResponseMap(status, error, message, path));
    }
    
    private Map<String, Object> buildErrorResponseMap(HttpStatus status, String error, String message) {
        return buildErrorResponseMap(status, error, message, null);
    }
    
    private Map<String, Object> buildErrorResponseMap(HttpStatus status, String error, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", Instant.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("traceId", TraceIdGenerator.generate());
        if (path != null) {
            errorResponse.put("path", path);
        }
        return errorResponse;
    }
}