package com.meli.technical.exam.api.products.domain.event;

import java.util.List;

public interface DomainEventPublisher {
    
    void publish(DomainEvent event);
    
    void publishAll(List<DomainEvent> events);
}