package com.meli.technical.exam.api.products.domain.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class DomainEventTest {

    // Clase concreta mínima para probar la abstracta
    static class TestEvent extends DomainEvent {
        @Override
        public String getEventType() {
            return "TestEvent";
        }
    }

    @Test
    void shouldGenerateEventId() {
        DomainEvent event = new TestEvent();

        assertNotNull(event.getEventId(), "El eventId no debería ser nulo");
        assertFalse(event.getEventId().isBlank(), "El eventId no debería estar vacío");
    }

    @Test
    void shouldGenerateOccurredOnTimestamp() {
        DomainEvent event = new TestEvent();

        assertNotNull(event.getOccurredOn(), "El occurredOn no debería ser nulo");
        assertTrue(event.getOccurredOn().isBefore(Instant.now().plusSeconds(1)),
                "El occurredOn debería estar cerca del tiempo actual");
    }

    @Test
    void shouldReturnCorrectEventType() {
        DomainEvent event = new TestEvent();

        assertEquals("TestEvent", event.getEventType(),
                "El tipo de evento debería ser TestEvent");
    }

    @Test
    void shouldGenerateUniqueEventIdsForDifferentEvents() {
        DomainEvent event1 = new TestEvent();
        DomainEvent event2 = new TestEvent();

        assertNotEquals(event1.getEventId(), event2.getEventId(),
                "Cada evento debería tener un eventId único");
    }
}