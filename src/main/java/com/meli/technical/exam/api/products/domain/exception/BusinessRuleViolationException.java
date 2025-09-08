package com.meli.technical.exam.api.products.domain.exception;

import lombok.Getter;

@Getter
public final class BusinessRuleViolationException extends RuntimeException {

    private final String businessRule;
    private final Integer limit;
    private final Integer provided;

    public BusinessRuleViolationException(String message, String businessRule, Integer limit, Integer provided) {
        super(message);
        this.businessRule = businessRule;
        this.limit = limit;
        this.provided = provided;
    }
}