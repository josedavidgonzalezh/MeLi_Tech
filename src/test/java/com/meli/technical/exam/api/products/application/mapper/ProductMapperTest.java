package com.meli.technical.exam.api.products.application.mapper;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.application.dto.request.SpecificationDto;
import com.meli.technical.exam.api.products.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductMapperSpringTest {

    @Autowired
    private ProductMapper mapper;

    @Test
    void shouldMapDomainToDto() {
        Product product = Product.builder()
                .id(ProductId.of("p-123"))
                .name("Laptop X")
                .imageUrl("https://example.com/img.jpg")
                .description("High-end laptop")
                .price(Price.of(new BigDecimal("1200.50")))
                .rating(Rating.of(4.8))
                .specifications(List.of())
                .build();

        ProductDto dto = mapper.toDto(product);

        assertEquals("p-123", dto.getId());
        assertEquals("Laptop X", dto.getName());
        assertEquals("https://example.com/img.jpg", dto.getImageUrl());
        assertEquals("High-end laptop", dto.getDescription());
        assertEquals(new BigDecimal("1200.50"), dto.getPrice());
        assertEquals(4.8, dto.getRating());
        assertEquals(List.of(), dto.getSpecifications());
    }

    @Test
    void shouldMapDtoToDomain() {
        ProductDto dto = ProductDto.builder()
                .id("p-456")
                .name("Phone Y")
                .imageUrl("https://example.com/phone.jpg")
                .description("Smartphone mid-range")
                .price(new BigDecimal("599.99"))
                .rating(4.2)
                .specifications(List.of())
                .build();

        Product product = mapper.toDomain(dto);

        assertEquals(ProductId.of("p-456"), product.getId());
        assertEquals("Phone Y", product.getName());
        assertEquals("https://example.com/phone.jpg", product.getImageUrl());
        assertEquals("Smartphone mid-range", product.getDescription());
        assertEquals(Price.of(new BigDecimal("599.99")), product.getPrice());
        assertEquals(Rating.of(4.2), product.getRating());
        assertEquals(List.of(), product.getSpecifications());
    }
}
