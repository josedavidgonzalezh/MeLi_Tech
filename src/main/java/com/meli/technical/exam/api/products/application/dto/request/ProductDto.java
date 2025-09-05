package com.meli.technical.exam.api.products.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
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
}