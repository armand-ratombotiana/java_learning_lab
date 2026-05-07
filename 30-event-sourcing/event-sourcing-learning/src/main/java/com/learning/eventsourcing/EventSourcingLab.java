package com.learning.eventsourcing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
class EventSourcingApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventSourcingApplication.class, args);
    }
}

class OrderCreatedEvent extends ApplicationEvent {
    private final String orderId;
    private final String product;
    private final double amount;

    public OrderCreatedEvent(Object source, String orderId, String product, double amount) {
        super(source);
        this.orderId = orderId; this.product = product; this.amount = amount;
    }
    public String getOrderId() { return orderId; }
    public String getProduct() { return product; }
    public double getAmount() { return amount; }
}

class EventStore {
    private final List<String> events = new ArrayList<>();

    public void addEvent(String event) {
        events.add(event);
        System.out.println("Event stored: " + event);
    }

    public List<String> getEvents() { return events; }
}

@RestController
@RequestMapping("/orders")
class OrderController {

    private final org.springframework.context.ApplicationEventPublisher eventPublisher;
    private final EventStore eventStore;

    OrderController(org.springframework.context.ApplicationEventPublisher eventPublisher, EventStore eventStore) {
        this.eventPublisher = eventPublisher;
        this.eventStore = eventStore;
    }

    @PostMapping("/create")
    public String createOrder(@RequestParam String product, @RequestParam double amount) {
        String orderId = "ORD-" + System.currentTimeMillis();
        eventPublisher.publishEvent(new OrderCreatedEvent(this, orderId, product, amount));
        eventStore.addEvent("OrderCreated: " + orderId);
        return "Order created: " + orderId;
    }

    @GetMapping("/events")
    public List<String> getEvents() {
        return eventStore.getEvents();
    }

    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("Processing order: " + event.getOrderId() + " - " + event.getProduct());
    }
}