package com.meli.technical.exam.api.products.domain.model;

import com.meli.technical.exam.api.products.domain.exception.InvalidProductException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
/*
* Validations on this class can be separeted into another helper or utils class however
* we keep the validations because it makes sense and helps the reader know exactly the
* constraints within the class
* */

@Getter
@EqualsAndHashCode
@ToString
public class Rating {

    private static final double MIN_RATING = 0.0;
    private static final double MAX_RATING = 5.0;
    private static final int SCALE = 1;

    private final BigDecimal value;

    private Rating(BigDecimal value) {
        this.value = value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Rating of(Double value) {
        if (value == null) {
            throw new InvalidProductException("Rating cannot be null");
        }
        if (value < MIN_RATING || value > MAX_RATING) {
            throw new InvalidProductException(
                    String.format("Rating must be between %.1f and %.1f", MIN_RATING, MAX_RATING)
            );
        }
        return new Rating(BigDecimal.valueOf(value));
    }

    public static Rating of(String value) {
        try {
            return of(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            throw new InvalidProductException("Invalid rating format: " + value);
        }
    }

    public Double getValue() {
        return value.doubleValue();
    }

}