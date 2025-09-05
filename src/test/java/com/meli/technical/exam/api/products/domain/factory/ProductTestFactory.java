package com.meli.technical.exam.api.products.domain.factory;

import com.meli.technical.exam.api.products.domain.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductTestFactory {
    
    public static Product createTestProduct(String id, String name, String imageUrl, String description,
                                           BigDecimal price, Double rating) {
        return createTestProduct(id, name, imageUrl, description, price, rating, new ArrayList<>());
    }
    
    public static Product createTestProduct(String id, String name, String imageUrl, String description,
                                           BigDecimal price, Double rating, List<Specification> specifications) {
        return Product.builder()
                .id(ProductId.of(id))
                .name(name)
                .imageUrl(imageUrl)
                .description(description)
                .price(Price.of(price))
                .rating(Rating.of(rating))
                .specifications(specifications)
                .build();
    }
    
    public static Product createValidTestProduct() {
        return createTestProduct(
                "test-id-123",
                "Test Product",
                "https://example.com/image.jpg",
                "Test product description",
                BigDecimal.valueOf(99.99),
                4.5
        );
    }
    
    public static Product createValidTestProductWithId(String id) {
        return createTestProduct(
                id,
                "Test Product",
                "https://example.com/image.jpg",
                "Test product description",
                BigDecimal.valueOf(99.99),
                4.5
        );
    }
}