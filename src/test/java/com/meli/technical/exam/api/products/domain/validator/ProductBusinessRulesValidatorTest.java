package com.meli.technical.exam.api.products.domain.validator;

import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.shared.exception.InvalidProductException;
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
                .id("1")
                .name("Test Product")
                .imageUrl("https://example.com/image.jpg")
                .description("Test description")
                .price(new BigDecimal("99.99"))
                .rating(4.5)
                .specifications(List.of())
                .build();

        assertDoesNotThrow(() -> validator.validate(product));
        assertTrue(validator.isValid(product));
    }

    @Test
    void shouldFailValidationWhenPriceIsNull() {
        Product product = Product.builder()
                .id("1")
                .name("Test Product")
                .price(null)
                .rating(4.5)
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertEquals("Product price cannot be null", exception.getMessage());
        assertFalse(validator.isValid(product));
    }

    @Test
    void shouldFailValidationWhenPriceIsNegative() {
        Product product = Product.builder()
                .id("1")
                .name("Test Product")
                .price(new BigDecimal("-10.00"))
                .rating(4.5)
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertEquals("Product price cannot be negative", exception.getMessage());
    }

    @Test
    void shouldFailValidationWhenPriceExceedsMaximum() {
        Product product = Product.builder()
                .id("1")
                .name("Test Product")
                .price(new BigDecimal("1000000.00"))
                .rating(4.5)
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertTrue(exception.getMessage().contains("cannot exceed"));
    }

    @Test
    void shouldFailValidationWhenRatingIsNull() {
        Product product = Product.builder()
                .id("1")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .rating(null)
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertEquals("Product rating cannot be null", exception.getMessage());
    }

    @Test
    void shouldFailValidationWhenRatingIsOutOfRange() {
        Product product = Product.builder()
                .id("1")
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .rating(6.0)
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertTrue(exception.getMessage().contains("must be between"));

        Product productWithNegativeRating = product.toBuilder().rating(-1.0).build();
        assertThrows(InvalidProductException.class, () -> validator.validate(productWithNegativeRating));
    }

    @Test
    void shouldFailValidationWhenPriceHasTooManyDecimals() {
        Product product = Product.builder()
                .id("1")
                .name("Test Product")
                .price(new BigDecimal("99.999"))
                .rating(4.5)
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertTrue(exception.getMessage().contains("decimal places"));
    }
}