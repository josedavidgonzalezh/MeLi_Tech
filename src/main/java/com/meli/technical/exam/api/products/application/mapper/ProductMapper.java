package com.meli.technical.exam.api.products.application.mapper;

import com.meli.technical.exam.api.products.application.dto.ProductDto;
import com.meli.technical.exam.api.products.application.dto.SpecificationDto;
import com.meli.technical.exam.api.products.domain.model.Product;
import com.meli.technical.exam.api.products.domain.model.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getImageUrl(),
                product.getDescription(),
                product.getPrice(),
                product.getRating(),
                mapSpecificationsToDto(product.getSpecifications())
        );
    }

    public Product toDomain(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }

        return new Product(
                productDto.getId(),
                productDto.getName(),
                productDto.getImageUrl(),
                productDto.getDescription(),
                productDto.getPrice(),
                productDto.getRating(),
                mapSpecificationsToDomain(productDto.getSpecifications())
        );
    }

    public List<ProductDto> toDtoList(List<Product> products) {
        if (products == null) {
            return Collections.emptyList();
        }

        return products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Product> toDomainList(List<ProductDto> productDtos) {
        if (productDtos == null) {
            return Collections.emptyList();
        }

        return productDtos.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private List<SpecificationDto> mapSpecificationsToDto(List<Specification> specifications) {
        if (specifications == null) {
            return Collections.emptyList();
        }

        return specifications.stream()
                .map(spec -> new SpecificationDto(spec.getKey(), spec.getValue()))
                .collect(Collectors.toList());
    }

    private List<Specification> mapSpecificationsToDomain(List<SpecificationDto> specificationDtos) {
        if (specificationDtos == null) {
            return Collections.emptyList();
        }

        return specificationDtos.stream()
                .map(dto -> new Specification(dto.getKey(), dto.getValue()))
                .collect(Collectors.toList());
    }
}