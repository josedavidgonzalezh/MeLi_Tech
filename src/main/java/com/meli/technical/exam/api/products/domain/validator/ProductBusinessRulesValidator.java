package com.meli.technical.exam.api.products.domain.validator;

import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.shared.exception.InvalidProductException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductBusinessRulesValidator implements ProductValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductBusinessRulesValidator.class);
    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
    //Constantes podriamos ponerlas en otro archivo
    private static final double MIN_RATING = 0.0;
    private static final double MAX_RATING = 5.0;
    private static final int MAX_SPECIFICATIONS = 50;

    @Override
    public void validate(Product product) {
        validatePrice(product.getPrice());
        validateRating(product.getRating());
        validateSpecifications(product);
    }

    private void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new InvalidProductException("Product price cannot be null");
        }
        
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductException("Product price cannot be negative");
        }
        
        if (price.compareTo(MAX_PRICE) > 0) {
            throw new InvalidProductException("Product price cannot exceed " + MAX_PRICE);
        }
        
        if (price.scale() > 2) {
            throw new InvalidProductException("Product price cannot have more than 2 decimal places");
        }
    }

    private void validateRating(Double rating) {
        if (rating == null) {
            throw new InvalidProductException("Product rating cannot be null");
        }
        
        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new InvalidProductException(
                String.format("Product rating must be between %.1f and %.1f", MIN_RATING, MAX_RATING)
            );
        }
    }

    private void validateSpecifications(Product product) {
        if (product.getSpecifications() == null) {
            return;
        }
        
        if (product.getSpecifications().size() > MAX_SPECIFICATIONS) {
            throw new InvalidProductException(
                "Product cannot have more than " + MAX_SPECIFICATIONS + " specifications"
            );
        }
    }
}