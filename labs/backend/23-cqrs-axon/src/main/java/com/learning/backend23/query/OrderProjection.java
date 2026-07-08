package com.learning.backend23.query;

import com.learning.backend23.event.OrderCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderProjection {

    private static final Logger log = LoggerFactory.getLogger(OrderProjection.class);

    @EventHandler
    public void on(OrderCreatedEvent event) {
        double total = event.items().stream()
            .mapToDouble(i -> i.price() * i.quantity())
            .sum();
        log.info("Projecting order: {} total={}", event.orderId(), total);
        // In real app, save to read database
    }
}
