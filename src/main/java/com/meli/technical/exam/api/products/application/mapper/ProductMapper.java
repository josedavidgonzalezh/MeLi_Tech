package com.meli.technical.exam.api.products.application.mapper;

import com.meli.technical.exam.api.products.application.dto.ProductDto;
import com.meli.technical.exam.api.products.domain.model.Product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SpecificationMapper.class})
public interface ProductMapper {

    @Mapping(source = "specifications", target = "specifications")
    ProductDto toDto(Product product);

    @Mapping(source = "specifications", target = "specifications")
    Product toDomain(ProductDto dto);
}