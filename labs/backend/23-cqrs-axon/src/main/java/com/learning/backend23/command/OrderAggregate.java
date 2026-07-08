package com.learning.backend23.command;

import com.learning.backend23.event.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.Instant;
import java.util.List;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String customerId;
    private OrderStatus status;

    protected OrderAggregate() {}

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        apply(new OrderCreatedEvent(
            command.orderId(),
            command.customerId(),
            command.items().stream()
                .map(i -> new OrderCreatedEvent.OrderItem(i.productId(), i.productName(), i.quantity(), i.price()))
                .toList(),
            Instant.now()
        ));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.orderId();
        this.customerId = event.customerId();
        this.status = OrderStatus.CREATED;
    }

    public enum OrderStatus {
        CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
}
