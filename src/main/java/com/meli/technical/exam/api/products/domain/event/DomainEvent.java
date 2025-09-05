package com.meli.technical.exam.api.products.domain.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {

    private final String eventId = UUID.randomUUID().toString();
    private final Instant occurredOn = Instant.now();

    public abstract String getEventType();
}