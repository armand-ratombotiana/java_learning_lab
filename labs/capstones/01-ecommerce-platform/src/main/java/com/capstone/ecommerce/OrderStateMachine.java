package com.capstone.ecommerce;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class OrderStateMachine {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final Map<String, List<StateTransition>> auditLog = new ConcurrentHashMap<>();

    public enum OrderState {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
    }

    public record Order(String orderId, String userId, List<ShoppingCart.LineItem> items,
                        BigDecimal total, OrderState state, Instant createdAt, Instant updatedAt) {
        public Order {
            if (orderId == null || orderId.isBlank()) throw new IllegalArgumentException("Order ID required");
            items = items == null ? List.of() : List.copyOf(items);
        }
    }

    public record StateTransition(OrderState from, OrderState to, Instant timestamp, String reason) {}

    private static final Map<OrderState, Set<OrderState>> VALID_TRANSITIONS = new EnumMap<>(OrderState.class);

    static {
        VALID_TRANSITIONS.put(OrderState.PENDING, Set.of(OrderState.CONFIRMED, OrderState.CANCELLED));
        VALID_TRANSITIONS.put(OrderState.CONFIRMED, Set.of(OrderState.PROCESSING, OrderState.CANCELLED));
        VALID_TRANSITIONS.put(OrderState.PROCESSING, Set.of(OrderState.SHIPPED, OrderState.CANCELLED));
        VALID_TRANSITIONS.put(OrderState.SHIPPED, Set.of(OrderState.DELIVERED));
        VALID_TRANSITIONS.put(OrderState.DELIVERED, Set.of(OrderState.REFUNDED));
        VALID_TRANSITIONS.put(OrderState.CANCELLED, Set.of(OrderState.REFUNDED));
        VALID_TRANSITIONS.put(OrderState.REFUNDED, Set.of());
    }

    public Order createOrder(String orderId, String userId, List<ShoppingCart.LineItem> items, BigDecimal total) {
        if (orders.containsKey(orderId)) throw new IllegalStateException("Order already exists: " + orderId);
        Instant now = Instant.now();
        Order order = new Order(orderId, userId, items, total, OrderState.PENDING, now, now);
        orders.put(orderId, order);
        auditLog.computeIfAbsent(orderId, k -> new ArrayList<>())
            .add(new StateTransition(null, OrderState.PENDING, now, "Order created"));
        return order;
    }

    public Order transition(String orderId, OrderState target, String reason) {
        Order order = orders.get(orderId);
        if (order == null) throw new NoSuchElementException("Order not found: " + orderId);
        OrderState current = order.state();
        Set<OrderState> allowed = VALID_TRANSITIONS.get(current);
        if (allowed == null || !allowed.contains(target)) {
            throw new IllegalStateException("Invalid transition: " + current + " -> " + target);
        }
        Instant now = Instant.now();
        Order updated = new Order(orderId, order.userId(), order.items(), order.total(), target, order.createdAt(), now);
        orders.put(orderId, updated);
        auditLog.get(orderId).add(new StateTransition(current, target, now, reason));
        return updated;
    }

    public Optional<Order> getOrder(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    public List<Order> getOrdersByUser(String userId) {
        return orders.values().stream()
            .filter(o -> o.userId().equals(userId))
            .sorted(Comparator.comparing(Order::createdAt).reversed())
            .toList();
    }

    public List<Order> getOrdersByState(OrderState state) {
        return orders.values().stream()
            .filter(o -> o.state() == state)
            .sorted(Comparator.comparing(Order::createdAt))
            .toList();
    }

    public List<StateTransition> getAuditLog(String orderId) {
        return List.copyOf(auditLog.getOrDefault(orderId, List.of()));
    }

    public Set<OrderState> getAllowedTransitions(OrderState state) {
        return VALID_TRANSITIONS.getOrDefault(state, Set.of());
    }

    public int size() { return orders.size(); }

    public void reset() { orders.clear(); auditLog.clear(); }
}
