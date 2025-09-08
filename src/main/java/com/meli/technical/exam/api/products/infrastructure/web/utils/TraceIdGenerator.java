package com.meli.technical.exam.api.products.infrastructure.web.utils;

import java.util.UUID;

public class TraceIdGenerator {
    
    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}