package com.meli.technical.exam.api.products.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class SpecificationDto {
    
    @NotBlank(message = "Specification key cannot be blank")
    @JsonProperty("key")
    private String key;
    
    @NotBlank(message = "Specification value cannot be blank")
    @JsonProperty("value")
    private String value;

    public SpecificationDto() {
    }

    public SpecificationDto(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}