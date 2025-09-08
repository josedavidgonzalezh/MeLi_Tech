package com.meli.technical.exam.api.products.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationError {
    private String field;
    private String rejectedValue;
    private String message;
}