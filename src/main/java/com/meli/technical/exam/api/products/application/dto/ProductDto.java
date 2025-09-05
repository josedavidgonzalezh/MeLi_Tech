package com.meli.technical.exam.api.products.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ProductDto {
    
    @NotBlank(message = "Product ID cannot be blank")
    @JsonProperty("id")
    private String id;
    
    @NotBlank(message = "Product name cannot be blank")
    @JsonProperty("name")
    private String name;
    
    @NotBlank(message = "Image URL cannot be blank")
    @JsonProperty("imageUrl")
    private String imageUrl;
    
    @NotBlank(message = "Description cannot be blank")
    @JsonProperty("description")
    private String description;
    
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    @JsonProperty("price")
    private BigDecimal price;
    
    @NotNull(message = "Rating cannot be null")
    @Min(value = 0, message = "Rating must be at least 0")
    @Max(value = 5, message = "Rating must be at most 5")
    @JsonProperty("rating")
    private Double rating;
    
    @Valid
    @JsonProperty("specifications")
    private List<SpecificationDto> specifications;

    public ProductDto() {
    }

    public ProductDto(String id, String name, String imageUrl, String description, 
                     BigDecimal price, Double rating, List<SpecificationDto> specifications) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.specifications = specifications;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<SpecificationDto> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<SpecificationDto> specifications) {
        this.specifications = specifications;
    }
}