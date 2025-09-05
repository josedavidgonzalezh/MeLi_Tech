package com.meli.technical.exam.api.products.domain.validator;

import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.exception.InvalidProductException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class ProductInputFormatValidator implements ProductValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductInputFormatValidator.class);

    @Override
    public void validate(Product product) {
        validateId(String.valueOf(product.getId()));
        validateName(product.getName());
        validateImageUrl(product.getImageUrl());
        validateDescription(product.getDescription());
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidProductException("Product ID cannot be null or empty");
        }
        
        String trimmedId = id.trim();
        if (trimmedId.length() > 50) {
            throw new InvalidProductException("Product ID cannot exceed 50 characters");
        }
        
        if (!trimmedId.matches("^[a-zA-Z0-9\\-_]+$")) {
            throw new InvalidProductException(
                "Product ID can only contain letters, numbers, hyphens, and underscores"
            );
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidProductException("Product name cannot be null or empty");
        }
        
        String trimmedName = name.trim();
        if (trimmedName.length() < 2) {
            throw new InvalidProductException("Product name must be at least 2 characters long");
        }
        
        if (trimmedName.length() > 200) {
            throw new InvalidProductException("Product name cannot exceed 200 characters");
        }
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new InvalidProductException("Product image URL cannot be null or empty");
        }
        
        try {
            new URL(imageUrl.trim());
        } catch (MalformedURLException e) {
            throw new InvalidProductException("Product image URL format is invalid: " + imageUrl);
        }
        
        String trimmedUrl = imageUrl.trim();
        if (trimmedUrl.length() > 2000) {
            throw new InvalidProductException("Product image URL cannot exceed 2000 characters");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new InvalidProductException("Product description cannot be null or empty");
        }
        
        String trimmedDescription = description.trim();
        if (trimmedDescription.length() < 10) {
            throw new InvalidProductException("Product description must be at least 10 characters long");
        }
        
        if (trimmedDescription.length() > 5000) {
            throw new InvalidProductException("Product description cannot exceed 5000 characters");
        }
    }
}