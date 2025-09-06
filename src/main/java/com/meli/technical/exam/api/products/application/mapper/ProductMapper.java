package com.meli.technical.exam.api.products.application.mapper;

import com.meli.technical.exam.api.products.application.dto.request.ProductDto;
import com.meli.technical.exam.api.products.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "price.value", target = "price")
    @Mapping(source = "rating.value", target = "rating")
    ProductDto toDto(Product product);

    @Mapping(target = "id", expression = "java(ProductId.of(dto.getId()))")
    @Mapping(target = "price", expression = "java(Price.of(dto.getPrice()))")
    @Mapping(target = "rating", expression = "java(Rating.of(dto.getRating()))")
    Product toDomain(ProductDto dto);
}