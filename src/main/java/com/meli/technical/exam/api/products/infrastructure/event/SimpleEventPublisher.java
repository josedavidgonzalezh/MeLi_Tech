package com.meli.technical.exam.api.products.infrastructure.event;

import com.meli.technical.exam.api.products.domain.event.DomainEvent;
import com.meli.technical.exam.api.products.domain.event.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimpleEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleEventPublisher.class);
    
    @Override
    public void publish(DomainEvent event) {
        logger.info("Publishing domain event: {} - {}", event.getEventType(), event.getEventId());
    }
    
    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}