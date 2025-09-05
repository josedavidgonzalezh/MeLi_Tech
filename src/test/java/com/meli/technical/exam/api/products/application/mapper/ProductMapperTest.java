package com.meli.technical.exam.api.products.application.mapper;

import com.meli.technical.exam.api.products.application.dto.ProductDto;
import com.meli.technical.exam.api.products.application.dto.SpecificationDto;
import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.model.Specification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        productMapper = new ProductMapper();
    }

    @Test
    void shouldMapProductToDto() {
        List<Specification> specs = List.of(
            new Specification("Color", "Red"),
            new Specification("Size", "Large")
        );
        
        Product product = new Product(
            "1", "Test Product", "https://example.com/image.jpg",
            "Test description", new BigDecimal("99.99"), 4.5, specs
        );

        ProductDto dto = productMapper.toDto(product);

        assertNotNull(dto);
        assertEquals("1", dto.getId());
        assertEquals("Test Product", dto.getName());
        assertEquals("https://example.com/image.jpg", dto.getImageUrl());
        assertEquals("Test description", dto.getDescription());
        assertEquals(new BigDecimal("99.99"), dto.getPrice());
        assertEquals(4.5, dto.getRating());
        assertEquals(2, dto.getSpecifications().size());
        assertEquals("Color", dto.getSpecifications().get(0).getKey());
        assertEquals("Red", dto.getSpecifications().get(0).getValue());
    }

    @Test
    void shouldMapDtoToProduct() {
        List<SpecificationDto> specDtos = List.of(
            new SpecificationDto("Color", "Blue"),
            new SpecificationDto("Material", "Cotton")
        );
        
        ProductDto dto = new ProductDto(
            "2", "DTO Product", "https://example.com/dto.jpg",
            "DTO description", new BigDecimal("149.99"), 3.8, specDtos
        );

        Product product = productMapper.toDomain(dto);

        assertNotNull(product);
        assertEquals("2", product.getId());
        assertEquals("DTO Product", product.getName());
        assertEquals("https://example.com/dto.jpg", product.getImageUrl());
        assertEquals("DTO description", product.getDescription());
        assertEquals(new BigDecimal("149.99"), product.getPrice());
        assertEquals(3.8, product.getRating());
        assertEquals(2, product.getSpecifications().size());
        assertEquals("Color", product.getSpecifications().get(0).getKey());
        assertEquals("Blue", product.getSpecifications().get(0).getValue());
    }

    @Test
    void shouldReturnNullWhenProductIsNull() {
        ProductDto dto = productMapper.toDto(null);
        assertNull(dto);

        Product product = productMapper.toDomain(null);
        assertNull(product);
    }

    @Test
    void shouldMapProductListToDtoList() {
        List<Product> products = List.of(
            new Product("1", "Product 1", "url1", "desc1", BigDecimal.TEN, 4.0, List.of()),
            new Product("2", "Product 2", "url2", "desc2", BigDecimal.ONE, 3.0, List.of())
        );

        List<ProductDto> dtos = productMapper.toDtoList(products);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("1", dtos.get(0).getId());
        assertEquals("2", dtos.get(1).getId());
    }

    @Test
    void shouldReturnEmptyListWhenProductListIsNull() {
        List<ProductDto> dtos = productMapper.toDtoList(null);
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    @Test
    void shouldHandleProductWithNullSpecifications() {
        Product product = new Product(
            "1", "Test Product", "url", "desc",
            new BigDecimal("99.99"), 4.5, null
        );

        ProductDto dto = productMapper.toDto(product);

        assertNotNull(dto);
        assertNotNull(dto.getSpecifications());
        assertTrue(dto.getSpecifications().isEmpty());
    }
}