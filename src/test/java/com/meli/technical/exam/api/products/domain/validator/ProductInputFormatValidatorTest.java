package com.meli.technical.exam.api.products.domain.validator;

import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.shared.exception.InvalidProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductInputFormatValidatorTest {

    private ProductInputFormatValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProductInputFormatValidator();
    }

    @Test
    void shouldPassValidationForValidProduct() {
        Product product = Product.builder()
                .id("valid-id-123")
                .name("Valid Product Name")
                .imageUrl("https://example.com/image.jpg")
                .description("This is a valid product description with more than 10 characters")
                .price(new BigDecimal("99.99"))
                .rating(4.5)
                .build();

        assertDoesNotThrow(() -> validator.validate(product));
        assertTrue(validator.isValid(product));
    }

    @Test
    void shouldFailValidationWhenIdIsNull() {
        Product product = Product.builder()
                .id(null)
                .name("Test Product")
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldFailValidationWhenIdIsEmpty() {
        Product product = Product.builder()
                .id("  ")
                .name("Test Product")
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldFailValidationWhenIdHasInvalidCharacters() {
        Product product = Product.builder()
                .id("invalid@id")
                .name("Test Product")
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertTrue(exception.getMessage().contains("can only contain"));
    }

    @Test
    void shouldFailValidationWhenNameIsTooShort() {
        Product product = Product.builder()
                .id("valid-id")
                .name("A")
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertTrue(exception.getMessage().contains("at least 2 characters"));
    }

    @Test
    void shouldFailValidationWhenImageUrlIsInvalid() {
        Product product = Product.builder()
                .id("valid-id")
                .name("Valid Name")
                .imageUrl("not-a-valid-url")
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertTrue(exception.getMessage().contains("URL format is invalid"));
    }

    @Test
    void shouldFailValidationWhenDescriptionIsTooShort() {
        Product product = Product.builder()
                .id("valid-id")
                .name("Valid Name")
                .imageUrl("https://example.com/image.jpg")
                .description("Short")
                .build();

        InvalidProductException exception = assertThrows(InvalidProductException.class, 
                () -> validator.validate(product));
        assertTrue(exception.getMessage().contains("at least 10 characters"));
    }
}