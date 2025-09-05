package com.meli.technical.exam.api.products.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateValidProduct() {
        List<Specification> specs = List.of(
            new Specification("Color", "Red"),
            new Specification("Size", "Large")
        );
        
        Product product = new Product(
            "1",
            "Test Product",
            "https://example.com/image.jpg",
            "Test description",
            new BigDecimal("99.99"),
            4.5,
            specs
        );
        
        assertEquals("1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("https://example.com/image.jpg", product.getImageUrl());
        assertEquals("Test description", product.getDescription());
        assertEquals(new BigDecimal("99.99"), product.getPrice());
        assertEquals(4.5, product.getRating());
        assertEquals(2, product.getSpecifications().size());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Product(null, "Name", "url", "desc", BigDecimal.TEN, 4.0, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenIdIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
            new Product("", "Name", "url", "desc", BigDecimal.TEN, 4.0, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new Product("1", null, "url", "desc", BigDecimal.TEN, 4.0, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
            new Product("1", "Name", "url", "desc", new BigDecimal("-10"), 4.0, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenRatingIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
            new Product("1", "Name", "url", "desc", BigDecimal.TEN, 6.0, null)
        );
        
        assertThrows(IllegalArgumentException.class, () ->
            new Product("1", "Name", "url", "desc", BigDecimal.TEN, -1.0, null)
        );
    }

    @Test
    void shouldBeEqualWhenSameId() {
        Product product1 = new Product("1", "Name1", "url1", "desc1", BigDecimal.TEN, 4.0, null);
        Product product2 = new Product("1", "Name2", "url2", "desc2", BigDecimal.ONE, 3.0, null);
        
        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void shouldReturnImmutableSpecifications() {
        List<Specification> specs = List.of(new Specification("Color", "Red"));
        Product product = new Product("1", "Name", "url", "desc", BigDecimal.TEN, 4.0, specs);
        
        List<Specification> productSpecs = product.getSpecifications();
        assertThrows(UnsupportedOperationException.class, () ->
            productSpecs.add(new Specification("Size", "Large"))
        );
    }
}