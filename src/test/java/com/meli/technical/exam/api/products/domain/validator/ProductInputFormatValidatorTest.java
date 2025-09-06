package com.meli.technical.exam.api.products.domain.validator;

import com.meli.technical.exam.api.products.domain.exception.InvalidProductException;
import com.meli.technical.exam.api.products.domain.model.Price;
import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.model.ProductId;
import com.meli.technical.exam.api.products.domain.model.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

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
                .id(ProductId.of("valid-id-123"))
                .name("Valid Product Name")
                .imageUrl("https://example.com/image.jpg")
                .description("This is a valid product description with more than 10 characters")
                .price(Price.of(new BigDecimal("99.99")))
                .rating(Rating.of(4.5))
                .specifications(List.of())
                .build();

        assertDoesNotThrow(() -> validator.validate(product));
        assertTrue(validator.isValid(product));
    }

    @Test
    void shouldFailValidationWhenIdIsEmpty() {
        assertThrows(InvalidProductException.class, () -> ProductId.of("   "));
    }

    @Test
    void shouldFailValidationWhenNameIsTooShort() {
        Product product = Product.builder()
                .id(ProductId.of("valid-id"))
                .name("A")
                .imageUrl("https://example.com/image.jpg")
                .description("This is a valid description with more than 10 characters")
                .price(Price.of(new BigDecimal("99.99")))
                .rating(Rating.of(4.5))
                .specifications(List.of())
                .build();

        assertThrows(InvalidProductException.class, () -> validator.validate(product));
    }

    @Test
    void shouldFailValidationWhenImageUrlIsInvalid() {
        Product product = Product.builder()
                .id(ProductId.of("valid-id"))
                .name("Valid Name")
                .imageUrl("not-a-valid-url")
                .description("This is a valid description with more than 10 characters")
                .price(Price.of(new BigDecimal("99.99")))
                .rating(Rating.of(4.5))
                .specifications(List.of())
                .build();

        assertThrows(InvalidProductException.class, () -> validator.validate(product));
    }

    @Test
    void shouldFailValidationWhenDescriptionIsTooShort() {
        Product product = Product.builder()
                .id(ProductId.of("valid-id"))
                .name("Valid Name")
                .imageUrl("https://example.com/image.jpg")
                .description("Short")
                .price(Price.of(new BigDecimal("99.99")))
                .rating(Rating.of(4.5))
                .specifications(List.of())
                .build();

        assertThrows(InvalidProductException.class, () -> validator.validate(product));
    }
}