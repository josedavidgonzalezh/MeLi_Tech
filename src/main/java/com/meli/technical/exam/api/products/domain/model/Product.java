package com.meli.technical.exam.api.products.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private String imageUrl;
    private String description;
    private BigDecimal price;
    private Double rating;

    @Builder.Default
    private List<Specification> specifications = Collections.emptyList();
}