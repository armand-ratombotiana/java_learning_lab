package com.systemdesign.deep.lab01;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Lab 01: Design Patterns — CQRS, Saga, Outbox, Transactional Outbox, Event Sourcing
 *
 * Run this file to see all patterns demonstrated.
 */
public class DesignPatternsLab {

    // ──────────────────────────────────────────────
    // Domain Events (shared across patterns)
    // ──────────────────────────────────────────────
    public sealed interface Event permits OrderPlaced, PaymentReceived, InventoryReserved, OrderShipped, OrderFailed {}

    public record OrderPlaced(String orderId, String customerId, List<String> items, double total) implements Event {}
    public record PaymentReceived(String orderId, double amount, String transactionId) implements Event {}
    public record InventoryReserved(String orderId, boolean success) implements Event {}
    public record OrderShipped(String orderId, String trackingId) implements Event {}
    public record OrderFailed(String orderId, String reason) implements Event {}

    // ──────────────────────────────────────────────
    // 1. CQRS — Command & Query Separation
    // ──────────────────────────────────────────────
    public static class CqrsDemo {

        public sealed interface Command permits PlaceOrder, CancelOrder {}
        public record PlaceOrder(String orderId, String customerId, List<String> items, double total) implements Command {}
        public record CancelOrder(String orderId, String reason) implements Command {}

        // Write model
        public static class OrderWriteModel {
            final Map<String, String> state = new ConcurrentHashMap<>();
            final List<Event> events = new CopyOnWriteArrayList<>();

            public void handle(Command cmd) {
                if (cmd instanceof PlaceOrder p) {
                    state.put(p.orderId(), "PLACED");
                    events.add(new OrderPlaced(p.orderId(), p.customerId(), p.items(), p.total()));
                } else if (cmd instanceof CancelOrder c) {
                    state.put(c.orderId(), "CANCELLED");
                    events.add(new OrderFailed(c.orderId(), c.reason()));
                }
            }

            public String getStatus(String orderId) {
                return state.getOrDefault(orderId, "UNKNOWN");
            }
        }

        // Read model (denormalized for fast queries)
        public static class OrderReadModel {
            final Map<String, String> displayState = new ConcurrentHashMap<>();

            public void apply(Event event) {
                if (event instanceof OrderPlaced o) {
                    displayState.put(o.orderId(), "Order " + o.orderId() + " placed for $" + o.total());
                } else if (event instanceof OrderShipped o) {
                    displayState.put(o.orderId(), "Order " + o.orderId() + " shipped — tracking: " + o.trackingId());
                } else if (event instanceof OrderFailed o) {
                    displayState.put(o.orderId(), "Order " + o.orderId() + " failed: " + o.reason());
                }
            }

            public String getDisplay(String orderId) {
                return displayState.getOrDefault(orderId, "Not found");
            }
        }

        public static void demo() {
            System.out.println("=== CQRS Demo ===");
            var write = new OrderWriteModel();
            var read = new OrderReadModel();

            write.handle(new PlaceOrder("ORD-001", "CUST-42", List.of("item1", "item2"), 199.99));
            read.apply(new OrderPlaced("ORD-001", "CUST-42", List.of("item1", "item2"), 199.99));

            System.out.println("Write status: " + write.getStatus("ORD-001"));
            System.out.println("Read display: " + read.getDisplay("ORD-001"));
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // 2. Saga — Orchestration-based Distributed Transaction
    // ──────────────────────────────────────────────
    public static class SagaDemo {

        public interface SagaStep<T> {
            T execute(T context);
            T compensate(T context);
            String getName();
        }

        public static class OrderSagaContext {
            String orderId;
            String customerId;
            double amount;
            boolean paymentDone;
            boolean inventoryDone;
            boolean shippingDone;
            List<String> failures = new ArrayList<>();
        }

        public static class PaymentStep implements SagaStep<OrderSagaContext> {
            public OrderSagaContext execute(OrderSagaContext ctx) {
                System.out.println("  Processing payment of $" + ctx.amount + " for " + ctx.orderId);
                ctx.paymentDone = true;
                return ctx;
            }
            public OrderSagaContext compensate(OrderSagaContext ctx) {
                System.out.println("  [Compensate] Refunding payment for " + ctx.orderId);
                ctx.paymentDone = false;
                return ctx;
            }
            public String getName() { return "Payment"; }
        }

        public static class InventoryStep implements SagaStep<OrderSagaContext> {
            public OrderSagaContext execute(OrderSagaContext ctx) {
                System.out.println("  Reserving inventory for " + ctx.orderId);
                ctx.inventoryDone = true;
                return ctx;
            }
            public OrderSagaContext compensate(OrderSagaContext ctx) {
                System.out.println("  [Compensate] Releasing inventory for " + ctx.orderId);
                ctx.inventoryDone = false;
                return ctx;
            }
            public String getName() { return "Inventory"; }
        }

        public static class ShippingStep implements SagaStep<OrderSagaContext> {
            public OrderSagaContext execute(OrderSagaContext ctx) {
                System.out.println("  Creating shipment for " + ctx.orderId);
                ctx.shippingDone = true;
                return ctx;
            }
            public OrderSagaContext compensate(OrderSagaContext ctx) {
                System.out.println("  [Compensate] Cancelling shipment for " + ctx.orderId);
                ctx.shippingDone = false;
                return ctx;
            }
            public String getName() { return "Shipping"; }
        }

        public static class SagaOrchestrator {
            private final List<SagaStep<OrderSagaContext>> steps = new ArrayList<>();

            public void addStep(SagaStep<OrderSagaContext> step) {
                steps.add(step);
            }

            public boolean execute(OrderSagaContext ctx) {
                Deque<SagaStep<OrderSagaContext>> executed = new ArrayDeque<>();
                for (var step : steps) {
                    try {
                        System.out.println("  Executing step: " + step.getName());
                        step.execute(ctx);
                        executed.push(step);
                    } catch (Exception e) {
                        System.out.println("  Step " + step.getName() + " failed: " + e.getMessage());
                        System.out.println("  Starting compensation...");
                        while (!executed.isEmpty()) {
                            var failed = executed.pop();
                            failed.compensate(ctx);
                        }
                        return false;
                    }
                }
                return true;
            }
        }

        public static void demo() {
            System.out.println("=== Saga Demo (Orchestration) ===");
            var orchestrator = new SagaOrchestrator();
            orchestrator.addStep(new PaymentStep());
            orchestrator.addStep(new InventoryStep());
            orchestrator.addStep(new ShippingStep());

            var ctx = new OrderSagaContext();
            ctx.orderId = "ORD-002";
            ctx.customerId = "CUST-42";
            ctx.amount = 299.99;

            boolean success = orchestrator.execute(ctx);
            System.out.println("Saga result: " + (success ? "SUCCESS" : "ROLLED BACK"));
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // 3. Transactional Outbox Pattern
    // ──────────────────────────────────────────────
    public static class OutboxDemo {

        public static class OutboxRecord {
            final String id;
            final String aggregateType;
            final String aggregateId;
            final String eventType;
            final String payload;
            final long createdAt;
            boolean published;

            public OutboxRecord(String id, String aggregateType, String aggregateId,
                                String eventType, String payload) {
                this.id = id;
                this.aggregateType = aggregateType;
                this.aggregateId = aggregateId;
                this.eventType = eventType;
                this.payload = payload;
                this.createdAt = System.currentTimeMillis();
                this.published = false;
            }
        }

        public static class OutboxStore {
            final List<OutboxRecord> store = new CopyOnWriteArrayList<>();
            final AtomicLong idGen = new AtomicLong();

            // Simulates atomic DB transaction: insert business data + outbox record
            public void atomicInsert(String aggregateType, String aggregateId,
                                     String eventType, String payload, Runnable businessLogic) {
                businessLogic.run();
                store.add(new OutboxRecord("OB-" + idGen.incrementAndGet(),
                        aggregateType, aggregateId, eventType, payload));
            }

            public List<OutboxRecord> pollUnpublished() {
                return store.stream().filter(r -> !r.published).toList();
            }

            public void markPublished(String id) {
                store.stream().filter(r -> r.id.equals(id)).forEach(r -> r.published = true);
            }
        }

        // Message relay polls the outbox and publishes to a message broker
        public static class MessageRelay {
            private final OutboxStore outbox;
            private final Consumer<OutboxRecord> publisher;

            public MessageRelay(OutboxStore outbox, Consumer<OutboxRecord> publisher) {
                this.outbox = outbox;
                this.publisher = publisher;
            }

            public void pollAndPublish() {
                var records = outbox.pollUnpublished();
                for (var record : records) {
                    try {
                        publisher.accept(record);
                        outbox.markPublished(record.id);
                        System.out.println("  Published: " + record.id + " [" + record.eventType + "]");
                    } catch (Exception e) {
                        System.err.println("  Failed to publish: " + record.id + " — " + e.getMessage());
                    }
                }
            }
        }

        public static void demo() {
            System.out.println("=== Transactional Outbox Demo ===");
            var outbox = new OutboxStore();
            var relay = new MessageRelay(outbox, r ->
                    System.out.println("  [Broker] Sending " + r.eventType + " for " + r.aggregateId));

            // Simulate atomic write
            System.out.println("  Placing order in DB + outbox atomically...");
            outbox.atomicInsert("Order", "ORD-003", "OrderPlaced",
                    "{\"orderId\":\"ORD-003\",\"total\":149.99}",
                    () -> System.out.println("  [DB] Order ORD-003 inserted"));

            outbox.atomicInsert("Order", "ORD-004", "OrderPlaced",
                    "{\"orderId\":\"ORD-004\",\"total\":89.99}",
                    () -> System.out.println("  [DB] Order ORD-004 inserted"));

            System.out.println("  Relay polling...");
            relay.pollAndPublish();

            System.out.println("  Unpublished remaining: " + outbox.pollUnpublished().size());
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // 4. Event Sourcing
    // ──────────────────────────────────────────────
    public static class EventSourcingDemo {

        public static class EventStore {
            private final List<Event> events = new CopyOnWriteArrayList<>();

            public void append(Event event) {
                events.add(event);
                System.out.println("  [EventStore] Appended: " + event.getClass().getSimpleName()
                        + " — " + event);
            }

            public List<Event> getEvents() {
                return List.copyOf(events);
            }

            // Replay all events to rebuild state
            public <T> T replay(T initial, java.util.function.BiFunction<T, Event, T> projector) {
                T state = initial;
                for (var event : events) {
                    state = projector.apply(state, event);
                }
                return state;
            }
        }

        // Simple aggregate: Order
        public static class OrderAggregate {
            String orderId;
            String status;
            double total;
            String trackingId;

            public static OrderAggregate replay(List<Event> events) {
                var agg = new OrderAggregate();
                for (var e : events) {
                    agg.apply(e);
                }
                return agg;
            }

            public void apply(Event event) {
                switch (event) {
                    case OrderPlaced o -> {
                        this.orderId = o.orderId();
                        this.status = "PLACED";
                        this.total = o.total();
                    }
                    case PaymentReceived p -> {
                        this.status = "PAID";
                    }
                    case InventoryReserved r -> {
                        this.status = r.success() ? "INVENTORY_RESERVED" : "INVENTORY_FAILED";
                    }
                    case OrderShipped s -> {
                        this.status = "SHIPPED";
                        this.trackingId = s.trackingId();
                    }
                    case OrderFailed f -> {
                        this.status = "FAILED";
                    }
                }
            }

            public String toString() {
                return "OrderAggregate{id=" + orderId + ", status=" + status
                        + ", total=" + total + ", tracking=" + trackingId + "}";
            }
        }

        public static void demo() {
            System.out.println("=== Event Sourcing Demo ===");
            var eventStore = new EventStore();

            // Append events
            eventStore.append(new OrderPlaced("ORD-005", "CUST-77", List.of("widget"), 49.99));
            eventStore.append(new PaymentReceived("ORD-005", 49.99, "TXN-001"));
            eventStore.append(new InventoryReserved("ORD-005", true));
            eventStore.append(new OrderShipped("ORD-005", "TRACK-ABC123"));

            System.out.println("  Total events stored: " + eventStore.getEvents().size());

            // Rebuild state
            var state = eventStore.replay(new OrderAggregate(), (agg, event) -> {
                agg.apply(event);
                return agg;
            });
            System.out.println("  Rebuilt state: " + state);

            // Show event count for temporal query
            System.out.println("  Events at time of payment: " +
                    eventStore.getEvents().stream()
                            .filter(e -> e instanceof PaymentReceived)
                            .count());
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Lab 01: System Design Patterns Deep-Dive   ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        CqrsDemo.demo();
        SagaDemo.demo();
        OutboxDemo.demo();
        EventSourcingDemo.demo();

        System.out.println("All patterns demonstrated successfully.");
    }
}
