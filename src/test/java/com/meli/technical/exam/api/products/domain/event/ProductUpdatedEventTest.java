package com.meli.technical.exam.api.products.domain.event;

import com.meli.technical.exam.api.products.domain.model.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductUpdatedEventTest {

    private ProductId mockProductId;
    private Map<String, Object> changedFields;

    @BeforeEach
    void setUp() {
        mockProductId = Mockito.mock(ProductId.class);
        changedFields = new HashMap<>();
        changedFields.put("price", 99.99);
        changedFields.put("name", "New Name");
    }

    @Test
    void shouldCreateEventWithValidData() {
        ProductUpdatedEvent event = new ProductUpdatedEvent(mockProductId, changedFields, "PRICE_UPDATE");

        assertNotNull(event.getEventId(), "El eventId no debería ser nulo");
        assertNotNull(event.getOccurredOn(), "El occurredOn no debería ser nulo");

        assertEquals(mockProductId, event.getProductId(), "El productId debería coincidir");
        assertEquals(changedFields, event.getChangedFields(), "Los changedFields deberían coincidir");
        assertEquals("PRICE_UPDATE", event.getUpdateReason(), "El updateReason debería ser PRICE_UPDATE");
        assertEquals("ProductUpdatedEvent", event.getEventType(), "El tipo de evento debería ser ProductUpdatedEvent");

        verifyNoInteractions(mockProductId); // no debería haber llamadas sobre el mock
    }

    @Test
    void shouldDefaultUpdateReasonToManualWhenNull() {
        ProductUpdatedEvent event = new ProductUpdatedEvent(mockProductId, changedFields, null);

        assertEquals("MANUAL_UPDATE", event.getUpdateReason(),
                "Si no se pasa updateReason, debería ser MANUAL_UPDATE por defecto");
    }

    @Test
    void shouldThrowExceptionWhenProductIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> new ProductUpdatedEvent(null, changedFields, "PRICE_UPDATE"),
                "Debería lanzar excepción si el productId es null");
    }

    @Test
    void shouldThrowExceptionWhenChangedFieldsIsNull() {
        assertThrows(NullPointerException.class,
                () -> new ProductUpdatedEvent(mockProductId, null, "PRICE_UPDATE"),
                "Debería lanzar excepción si los changedFields son null");
    }
}
