package com.meli.technical.exam.api.products.domain.model;

import com.meli.technical.exam.api.products.domain.exception.InvalidProductException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

/*
 * Validations on this class can be separeted into another helper or utils class however
 * we keep the validations because it makes sense and helps the reader know exactly the
 * constraints within the class
 * */

@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class Product {

    private final ProductId id;
    private String name;
    private String imageUrl;
    private String description;
    private Price price;
    private Rating rating;
    private final List<Specification> specifications;

    @Builder
    private Product(ProductId id, String name, String imageUrl, String description,
                    Price price, Rating rating, List<Specification> specifications) {

        this.id = Objects.requireNonNull(id, "Product ID cannot be null");
        this.specifications = new ArrayList<>(Optional.ofNullable(specifications)
                .orElse(Collections.emptyList()));

        updateProductInfo(name, imageUrl, description, price, rating);
        validateInvariants();
    }

    public void updateProductInfo(String name, String imageUrl, String description,
                                  Price price, Rating rating) {
        this.name = validateName(name);
        this.imageUrl = validateImageUrl(imageUrl);
        this.description = validateDescription(description);
        this.price = Objects.requireNonNull(price, "Price cannot be null");
        this.rating = Objects.requireNonNull(rating, "Rating cannot be null");
    }

    public Set<String> getSpecificationNames() {
        return specifications.stream()
                .map(Specification::getKey)
                .collect(Collectors.toSet());
    }

    private void validateInvariants() {
        if (name == null || name.isBlank()) {
            throw new InvalidProductException("Product name cannot be null or empty");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new InvalidProductException("Product image URL cannot be null or empty");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidProductException("Product description cannot be null or empty");
        }
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidProductException("Product name cannot be null or empty");
        }
        if (name.trim().length() > 200) {
            throw new InvalidProductException("Product name cannot exceed 200 characters");
        }
        return name.trim();
    }

    private String validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new InvalidProductException("Product image URL cannot be null or empty");
        }
        if (imageUrl.trim().length() > 500) {
            throw new InvalidProductException("Product image URL cannot exceed 500 characters");
        }
        return imageUrl.trim();
    }

    private String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidProductException("Product description cannot be null or empty");
        }
        if (description.trim().length() > 1000) {
            throw new InvalidProductException("Product description cannot exceed 1000 characters");
        }
        return description.trim();
    }
}