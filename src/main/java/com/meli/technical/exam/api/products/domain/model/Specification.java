package com.meli.technical.exam.api.products.domain.model;

import java.util.Objects;

public final class Specification {
    private final String key;
    private final String value;

    public Specification(String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Specification key cannot be null or empty");
        }
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Specification value cannot be null or empty");
        }
        this.key = key.trim();
        this.value = value.trim();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Specification that = (Specification) obj;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "Specification{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}