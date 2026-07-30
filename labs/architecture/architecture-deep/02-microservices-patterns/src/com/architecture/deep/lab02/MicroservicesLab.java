package com.architecture.deep.lab02;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

public class MicroservicesLab {
    public static void main(String[] args) {
        var inventoryService = new InventoryService();
        var paymentService = new PaymentService();
        var shippingService = new ShippingService();
        var sagaOrchestrator = new OrderSagaOrchestrator(inventoryService, paymentService, shippingService);

        var gateway = new ApiGateway(Map.of(
            "orders", new OrderService(sagaOrchestrator),
            "inventory", inventoryService,
            "payments", paymentService
        ));

        var circuitBreaker = new CircuitBreaker(3, 2000);

        var orderRequest = new CreateOrderRequest("user-1", List.of("item-1", "item-2"), 2999);

        for (int i = 0; i < 5; i++) {
            try {
                circuitBreaker.call(() -> gateway.route("orders", orderRequest));
            } catch (Exception e) {
                System.out.println("Request " + i + " failed: " + e.getMessage());
            }
        }

        System.out.println("Circuit breaker state: " + circuitBreaker.getState());
    }
}

record CreateOrderRequest(String userId, List<String> itemIds, long totalCents) {}
record OrderResult(String orderId, String status) {}

record ReserveInventoryCmd(String orderId, List<String> itemIds) {}
record ProcessPaymentCmd(String orderId, long amount) {}
record ShipOrderCmd(String orderId) {}

class OrderSagaOrchestrator {
    private final InventoryService inventory;
    private final PaymentService payment;
    private final ShippingService shipping;

    OrderSagaOrchestrator(InventoryService inventory, PaymentService payment, ShippingService shipping) {
        this.inventory = inventory;
        this.payment = payment;
        this.shipping = shipping;
    }

    OrderResult execute(CreateOrderRequest req) {
        var orderId = UUID.randomUUID().toString().substring(0, 8);
        try {
            inventory.reserve(new ReserveInventoryCmd(orderId, req.itemIds()));
            try {
                payment.process(new ProcessPaymentCmd(orderId, req.totalCents()));
            } catch (Exception payEx) {
                inventory.compensateReserve(orderId);
                throw new RuntimeException("Payment failed: " + payEx.getMessage());
            }
            shipping.ship(new ShipOrderCmd(orderId));
            return new OrderResult(orderId, "COMPLETED");
        } catch (Exception ex) {
            return new OrderResult(orderId, "FAILED: " + ex.getMessage());
        }
    }
}

class OrderService {
    private final OrderSagaOrchestrator orchestrator;

    OrderService(OrderSagaOrchestrator orchestrator) { this.orchestrator = orchestrator; }

    OrderResult createOrder(CreateOrderRequest req) {
        return orchestrator.execute(req);
    }
}

class ApiGateway {
    private final Map<String, Object> services;

    ApiGateway(Map<String, Object> services) { this.services = services; }

    Object route(String service, Object request) {
        var svc = services.get(service);
        if (svc instanceof OrderService os && request instanceof CreateOrderRequest req) {
            return os.createOrder(req);
        }
        throw new IllegalArgumentException("No route for " + service);
    }
}

class CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }

    private final int failureThreshold;
    private final long timeoutMs;
    private final AtomicInteger failures = new AtomicInteger(0);
    private volatile State state = State.CLOSED;
    private long lastFailureTime = 0;

    CircuitBreaker(int failureThreshold, long timeoutMs) {
        this.failureThreshold = failureThreshold;
        this.timeoutMs = timeoutMs;
    }

    synchronized Object call(Callable action) {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > timeoutMs) {
                state = State.HALF_OPEN;
            } else {
                throw new RuntimeException("Circuit breaker OPEN");
            }
        }
        try {
            var result = action.call();
            if (state == State.HALF_OPEN) {
                state = State.CLOSED;
                failures.set(0);
            }
            return result;
        } catch (Exception e) {
            failures.incrementAndGet();
            lastFailureTime = System.currentTimeMillis();
            if (failures.get() >= failureThreshold) {
                state = State.OPEN;
            }
            throw e;
        }
    }

    String getState() { return state.name(); }
}

@FunctionalInterface interface Callable { Object call(); }

class InventoryService {
    private final Set<String> reservations = ConcurrentHashMap.newKeySet();

    void reserve(ReserveInventoryCmd cmd) {
        reservations.add(cmd.orderId());
        System.out.println("Inventory reserved for " + cmd.orderId());
    }

    void compensateReserve(String orderId) {
        reservations.remove(orderId);
        System.out.println("Inventory compensation for " + orderId);
    }
}

class PaymentService {
    private final Set<String> processed = ConcurrentHashMap.newKeySet();
    private int callCount = 0;

    void process(ProcessPaymentCmd cmd) {
        callCount++;
        if (callCount % 3 == 0) throw new RuntimeException("Payment gateway timeout");
        processed.add(cmd.orderId());
        System.out.println("Payment processed for " + cmd.orderId());
    }
}

class ShippingService {
    void ship(ShipOrderCmd cmd) {
        System.out.println("Shipped order " + cmd.orderId());
    }
}
