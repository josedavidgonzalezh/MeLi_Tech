package com.meli.technical.exam.api.products.domain.model;

import com.meli.technical.exam.api.products.domain.exception.InvalidProductException;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Value
@EqualsAndHashCode(of = "value")
public class Price {

    BigDecimal value;

    private static final BigDecimal MAX_PRICE = new BigDecimal("999999.99");
    private static final int SCALE = 2;

    private Price(BigDecimal value) {
        if (value == null) throw new InvalidProductException("Price cannot be null");
        if (value.compareTo(BigDecimal.ZERO) < 0) throw new InvalidProductException("Price cannot be negative");
        if (value.compareTo(MAX_PRICE) > 0) throw new InvalidProductException("Price cannot exceed " + MAX_PRICE);
        this.value = value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Price of(BigDecimal value) { return new Price(value); }
    public static Price of(String value) { return new Price(new BigDecimal(value)); }
    public static Price of(double value) { return new Price(BigDecimal.valueOf(value)); }
}