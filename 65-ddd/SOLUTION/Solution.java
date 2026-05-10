package com.learning.ddd;

import java.util.*;
import java.util.function.*;

public class DDDSolution {

    public static class Entity<T> {
        private T id;

        public Entity(T id) {
            this.id = id;
        }

        public T getId() { return id; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entity)) return false;
            Entity<?> entity = (Entity<?>) o;
            return Objects.equals(id, entity.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public static class AggregateRoot<T> extends Entity<T> {
        private List<DomainEvent> events;

        public AggregateRoot(T id) {
            super(id);
            this.events = new ArrayList<>();
        }

        protected void addEvent(DomainEvent event) {
            events.add(event);
        }

        public List<DomainEvent> getEvents() { return events; }

        public void clearEvents() { events.clear(); }
    }

    public interface DomainEvent {
        String getEventType();
    }

    public static class ValueObject<T> {
        private final T value;

        public ValueObject(T value) {
            this.value = value;
        }

        public T getValue() { return value; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ValueObject)) return false;
            ValueObject<?> that = (ValueObject<?>) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class Address extends ValueObject<String> {
        public Address(String value) {
            super(value);
        }
    }

    public static class Money extends ValueObject<Double> {
        public Money(Double value) {
            super(value);
        }

        public Money add(Money other) {
            return new Money(this.getValue() + other.getValue());
        }

        public Money subtract(Money other) {
            return new Money(this.getValue() - other.getValue());
        }
    }

    public static class Order extends AggregateRoot<String> {
        private String customerId;
        private List<OrderItem> items;
        private OrderStatus status;

        public Order(String id) {
            super(id);
            this.items = new ArrayList<>();
            this.status = OrderStatus.DRAFT;
        }

        public void addItem(String productId, int quantity, Money price) {
            items.add(new OrderItem(productId, quantity, price));
            addEvent(new OrderItemAddedEvent(getId(), productId, quantity));
        }

        public void confirm() {
            if (status == OrderStatus.DRAFT) {
                status = OrderStatus.CONFIRMED;
                addEvent(new OrderConfirmedEvent(getId()));
            }
        }

        public void cancel() {
            if (status == OrderStatus.DRAFT || status == OrderStatus.CONFIRMED) {
                status = OrderStatus.CANCELLED;
                addEvent(new OrderCancelledEvent(getId()));
            }
        }

        public Money getTotal() {
            return items.stream()
                .map(item -> item.price.multiply(item.quantity))
                .reduce(new Money(0.0), Money::add);
        }

        public String getCustomerId() { return customerId; }
        public List<OrderItem> getItems() { return items; }
        public OrderStatus getStatus() { return status; }
    }

    public static class OrderItem {
        private String productId;
        private int quantity;
        private Money price;

        public OrderItem(String productId, int quantity, Money price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public Money getPrice() { return price; }
    }

    public enum OrderStatus {
        DRAFT, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }

    public static class OrderItemAddedEvent implements DomainEvent {
        private String orderId;
        private String productId;
        private int quantity;

        public OrderItemAddedEvent(String orderId, String productId, int quantity) {
            this.orderId = orderId;
            this.productId = productId;
            this.quantity = quantity;
        }

        public String getEventType() { return "OrderItemAdded"; }
    }

    public static class OrderConfirmedEvent implements DomainEvent {
        private String orderId;

        public OrderConfirmedEvent(String orderId) {
            this.orderId = orderId;
        }

        public String getEventType() { return "OrderConfirmed"; }
    }

    public static class OrderCancelledEvent implements DomainEvent {
        private String orderId;

        public OrderCancelledEvent(String orderId) {
            this.orderId = orderId;
        }

        public String getEventType() { return "OrderCancelled"; }
    }

    public static class Repository<T extends Entity<?>> {
        private Map<Object, T> storage;

        public Repository() {
            this.storage = new HashMap<>();
        }

        public void save(T entity) {
            storage.put(entity.getId(), entity);
        }

        public Optional<T> findById(Object id) {
            return Optional.ofNullable(storage.get(id));
        }

        public void delete(T entity) {
            storage.remove(entity.getId());
        }
    }

    public static class DomainService {
        public Money calculateDiscount(Money amount, double percentage) {
            return new Money(amount.getValue() * (1 - percentage / 100));
        }
    }

    public Order createOrder(String id) {
        return new Order(id);
    }

    public Repository<Order> createOrderRepository() {
        return new Repository<>();
    }
}