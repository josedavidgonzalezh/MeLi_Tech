package com.meli.technical.exam.api.products.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpecificationDto {
    
    @NotBlank(message = "Specification key cannot be blank")
    @JsonProperty("key")
    private String key;
    
    @NotBlank(message = "Specification value cannot be blank")
    @JsonProperty("value")
    private String value;

}