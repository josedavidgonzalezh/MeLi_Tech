package com.meli.technical.exam.api.products.domain.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Product {
    private final String id;
    private final String name;
    private final String imageUrl;
    private final String description;
    private final BigDecimal price;
    private final Double rating;
    private final List<Specification> specifications;

    public Product(String id, String name, String imageUrl, String description, 
                   BigDecimal price, Double rating, List<Specification> specifications) {
        this.id = validateId(id);
        this.name = validateName(name);
        this.imageUrl = validateImageUrl(imageUrl);
        this.description = validateDescription(description);
        this.price = validatePrice(price);
        this.rating = validateRating(rating);
        this.specifications = specifications != null ? 
            Collections.unmodifiableList(specifications) : Collections.emptyList();
    }

    private String validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        return id.trim();
    }

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        return name.trim();
    }

    private String validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Product image URL cannot be null or empty");
        }
        return imageUrl.trim();
    }

    private String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Product description cannot be null or empty");
        }
        return description.trim();
    }

    private BigDecimal validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price must be non-negative");
        }
        return price;
    }

    private Double validateRating(Double rating) {
        if (rating == null || rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("Product rating must be between 0.0 and 5.0");
        }
        return rating;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Double getRating() {
        return rating;
    }

    public List<Specification> getSpecifications() {
        return specifications;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", rating=" + rating +
                '}';
    }
}