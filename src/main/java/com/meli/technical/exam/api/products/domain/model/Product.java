package com.meli.technical.exam.api.products.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
public class Product {
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private BigDecimal price;
    private Double rating;
    private List<Specification> specifications;

    public Product(
            String id, String name, String imageUrl, String description,
            BigDecimal price, Double rating, List<Specification> specifications
    ) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.specifications = specifications != null ?
                Collections.unmodifiableList(specifications) :
                Collections.emptyList();
    }
}