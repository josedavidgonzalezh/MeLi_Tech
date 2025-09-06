package com.meli.technical.exam.api.products.domain.validator;

import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.model.ProductId;
import com.meli.technical.exam.api.products.domain.model.Price;
import com.meli.technical.exam.api.products.domain.model.Rating;
import com.meli.technical.exam.api.products.domain.exception.InvalidProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductBusinessRulesValidatorTest {

    private ProductBusinessRulesValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProductBusinessRulesValidator();
    }

    @Test
    void shouldPassValidationForValidProduct() {
        Product product = Product.builder()
                .id(ProductId.of("1"))
                .name("Test Product")
                .imageUrl("https://example.com/image.jpg")
                .description("Test description")
                .price(Price.of(new BigDecimal("99.99")))
                .rating(Rating.of(4.5))
                .specifications(List.of())
                .build();

        assertDoesNotThrow(() -> validator.validate(product));
        assertTrue(validator.isValid(product));
    }

    @Test
    void shouldFailValidationWhenProductIsNull() {
        assertThrows(NullPointerException.class, () -> validator.validate(null));
        assertFalse(validator.isValid(null));
    }

    @Test
    void shouldFailValidationWhenIdIsNull() {
        assertThrows(NullPointerException.class, () -> {
            Product.builder()
                    .id(null)
                    .name("Test Product")
                    .imageUrl("url")
                    .description("desc")
                    .price(Price.of(new BigDecimal("99.99")))
                    .rating(Rating.of(4.5))
                    .specifications(List.of())
                    .build();
        });
    }

    @Test
    void shouldFailValidationWhenNameIsEmpty() {
        assertThrows(InvalidProductException.class, () -> {
            Product.builder()
                    .id(ProductId.of("1"))
                    .name("")
                    .imageUrl("url")
                    .description("desc")
                    .price(Price.of(new BigDecimal("99.99")))
                    .rating(Rating.of(4.5))
                    .specifications(List.of())
                    .build();
        });
    }

    @Test
    void shouldFailValidationWhenPriceIsInvalid() {
        assertThrows(InvalidProductException.class, () -> {
            Price.of(new BigDecimal("-10.00"));
        });
    }

    @Test
    void shouldFailValidationWhenRatingIsInvalid() {
        assertThrows(InvalidProductException.class, () -> {
            Rating.of(6.0);
        });
        
        assertThrows(InvalidProductException.class, () -> {
            Rating.of(-1.0);
        });
    }

    @Test
    void shouldValidatePriceRange() {
        assertDoesNotThrow(() -> Price.of(new BigDecimal("0.01")));
        assertDoesNotThrow(() -> Price.of(new BigDecimal("999999.99")));
        
        assertThrows(InvalidProductException.class, () -> 
            Price.of(new BigDecimal("1000000.00")));
    }

    @Test
    void shouldValidateRatingRange() {
        assertDoesNotThrow(() -> Rating.of(0.0));
        assertDoesNotThrow(() -> Rating.of(5.0));
        assertDoesNotThrow(() -> Rating.of(2.5));
    }
}