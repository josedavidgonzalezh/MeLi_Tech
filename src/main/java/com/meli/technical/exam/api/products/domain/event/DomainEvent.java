package com.meli.technical.exam.api.products.domain.event;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;
/*
* Ideally we would save this to a DB to have metrics on behavour, not implemented due to simplicity
* */
@Getter
public abstract class DomainEvent {

    private final String eventId = UUID.randomUUID().toString();
    private final Instant occurredOn = Instant.now();

    public abstract String getEventType();
}