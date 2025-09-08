package com.meli.technical.exam.api.products.infrastructure.adapter.in.web.utils;

import com.meli.technical.exam.api.products.application.dto.response.ValidationError;
import com.meli.technical.exam.api.products.domain.exception.ProductValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductComparisonValidator {
    public static void validateCompareRequest(String ids) {
        List<ValidationError> validationErrors = new ArrayList<>();

        // Check if ids parameter is null or empty
        if (ids == null || ids.trim().isEmpty()) {
            validationErrors.add(ValidationError.builder()
                    .field("ids")
                    .rejectedValue(ids == null ? "" : ids)
                    .message("Product IDs cannot be empty")
                    .build());
        } else {
            // Validate individual IDs
            List<String> productIds = Arrays.asList(ids.split(","));
            for (String id : productIds) {
                String trimmedId = id.trim();
                if (trimmedId.isEmpty()) {
                    validationErrors.add(ValidationError.builder()
                            .field("ids")
                            .rejectedValue("")
                            .message("Product IDs cannot be empty")
                            .build());
                }
            }
        }

        if (!validationErrors.isEmpty()) {
            throw new ProductValidationException("Invalid input provided", validationErrors);
        }
    }

}
