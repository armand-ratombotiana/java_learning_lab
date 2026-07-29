# PROBLEM WALKTHROUGH: Implement CQRS with Event Sourcing for an Order System

## Problem Statement

Implement a complete CQRS (Command Query Responsibility Segregation) and Event Sourcing system for an e-commerce order management domain. The system should:

- Handle commands: `CreateOrder`, `AddItem`, `RemoveItem`, `SubmitOrder`, `CancelOrder`, `ShipOrder`
- Produce events: `OrderCreated`, `ItemAdded`, `ItemRemoved`, `OrderSubmitted`, `OrderCancelled`, `OrderShipped`
- Maintain an append-only event store
- Rebuild aggregate state by replaying events
- Maintain read-side projections (order summary, customer order history, inventory projection)
- Support sagas for multi-step workflows (payment → inventory → shipping)
- Provide snapshot support for large aggregates

**Constraints:**
- Pure Java 21+ (no Axon Framework dependency for this walkthrough)
- In-memory event store (can be swapped for PostgreSQL/EventStoreDB)
- Record-based immutable events
- Thread-safe aggregate operations

---

## Step-by-Step Solution

### Step 1: Domain Events (Java 21 Records)

```java
public sealed interface OrderEvent permits
    OrderCreated, ItemAdded, ItemRemoved, OrderSubmitted,
    OrderCancelled, OrderShipped, PaymentReceived {

    String orderId();
    Instant timestamp();
}

public record OrderCreated(
    String orderId,
    String customerId,
    String customerName,
    String shippingAddress,
    Instant timestamp
) implements OrderEvent {}

public record ItemAdded(
    String orderId,
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice,
    Instant timestamp
) implements OrderEvent {
    public BigDecimal totalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

public record ItemRemoved(
    String orderId,
    String productId,
    int quantity,
    Instant timestamp
) implements OrderEvent {}

public record OrderSubmitted(
    String orderId,
    Instant timestamp
) implements OrderEvent {}

public record PaymentReceived(
    String orderId,
    String paymentId,
    BigDecimal amount,
    String paymentMethod,
    Instant timestamp
) implements OrderEvent {}

public record OrderCancelled(
    String orderId,
    String reason,
    Instant timestamp
) implements OrderEvent {}

public record OrderShipped(
    String orderId,
    String trackingNumber,
    String carrier,
    Instant timestamp
) implements OrderEvent {}
```

### Step 2: Commands

```java
public sealed interface OrderCommand permits
    CreateOrderCommand, AddItemCommand, RemoveItemCommand,
    SubmitOrderCommand, CancelOrderCommand, ShipOrderCommand {}

public record CreateOrderCommand(
    String orderId,
    String customerId,
    String customerName,
    String shippingAddress
) implements OrderCommand {}

public record AddItemCommand(
    String orderId,
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice
) implements OrderCommand {}

public record RemoveItemCommand(
    String orderId,
    String productId,
    int quantity
) implements OrderCommand {}

public record SubmitOrderCommand(
    String orderId
) implements OrderCommand {}

public record CancelOrderCommand(
    String orderId,
    String reason
) implements OrderCommand {}

public record ShipOrderCommand(
    String orderId,
    String trackingNumber,
    String carrier
) implements OrderCommand {}
```

### Step 3: Aggregate (Order Aggregate Root)

```java
public class OrderAggregate {

    private String orderId;
    private String customerId;
    private OrderStatus status;
    private Map<String, OrderLineItem> items = new LinkedHashMap<>();
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private long version;

    public OrderAggregate() {}

    // Apply events to rebuild state
    public void apply(OrderEvent event) {
        switch (event) {
            case OrderCreated e -> apply(e);
            case ItemAdded e -> apply(e);
            case ItemRemoved e -> apply(e);
            case OrderSubmitted e -> apply(e);
            case PaymentReceived e -> apply(e);
            case OrderCancelled e -> apply(e);
            case OrderShipped e -> apply(e);
        }
        version++;
    }

    void apply(OrderCreated event) {
        this.orderId = event.orderId();
        this.customerId = event.customerId();
        this.status = OrderStatus.CREATED;
        this.items = new LinkedHashMap<>();
        this.totalAmount = BigDecimal.ZERO;
    }

    void apply(ItemAdded event) {
        OrderLineItem existing = items.get(event.productId());
        if (existing != null) {
            items.put(event.productId(), new OrderLineItem(
                event.productId(), event.productName(),
                existing.quantity() + event.quantity(), event.unitPrice()));
        } else {
            items.put(event.productId(), new OrderLineItem(
                event.productId(), event.productName(),
                event.quantity(), event.unitPrice()));
        }
        totalAmount = totalAmount.add(event.totalPrice());
    }

    void apply(ItemRemoved event) {
        OrderLineItem existing = items.get(event.productId());
        if (existing != null) {
            int newQty = existing.quantity() - event.quantity();
            if (newQty <= 0) {
                items.remove(event.productId());
            } else {
                items.put(event.productId(), new OrderLineItem(
                    existing.productId(), existing.productName(),
                    newQty, existing.unitPrice()));
            }
            totalAmount = totalAmount.subtract(
                existing.unitPrice().multiply(BigDecimal.valueOf(event.quantity())));
        }
    }

    void apply(OrderSubmitted event) { this.status = OrderStatus.SUBMITTED; }
    void apply(PaymentReceived event) { this.status = OrderStatus.PAID; }
    void apply(OrderCancelled event) { this.status = OrderStatus.CANCELLED; }
    void apply(OrderShipped event) { this.status = OrderStatus.SHIPPED; }

    // Command handling — produces events
    public List<OrderEvent> handle(CreateOrderCommand cmd) {
        if (orderId != null) throw new IllegalStateException("Order already exists");
        return List.of(new OrderCreated(cmd.orderId(), cmd.customerId(),
            cmd.customerName(), cmd.shippingAddress(), Instant.now()));
    }

    public List<OrderEvent> handle(AddItemCommand cmd) {
        if (status != OrderStatus.CREATED) throw new IllegalStateException(
            "Cannot add items to order in " + status + " state");
        return List.of(new ItemAdded(cmd.orderId(), cmd.productId(),
            cmd.productName(), cmd.quantity(), cmd.unitPrice(), Instant.now()));
    }

    public List<OrderEvent> handle(RemoveItemCommand cmd) {
        if (status != OrderStatus.CREATED) throw new IllegalStateException(
            "Cannot remove items from order in " + status + " state");
        OrderLineItem existing = items.get(cmd.productId());
        if (existing == null) throw new IllegalStateException("Product not in order");
        if (cmd.quantity() > existing.quantity()) throw new IllegalStateException(
            "Cannot remove more items than present");
        return List.of(new ItemRemoved(cmd.orderId(), cmd.productId(),
            cmd.quantity(), Instant.now()));
    }

    public List<OrderEvent> handle(SubmitOrderCommand cmd) {
        if (status != OrderStatus.CREATED) throw new IllegalStateException(
            "Cannot submit order in " + status + " state");
        if (items.isEmpty()) throw new IllegalStateException("Cannot submit empty order");
        return List.of(new OrderSubmitted(cmd.orderId(), Instant.now()));
    }

    public List<OrderEvent> handle(CancelOrderCommand cmd) {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED)
            throw new IllegalStateException("Cannot cancel shipped/delivered order");
        return List.of(new OrderCancelled(cmd.orderId(), cmd.reason(), Instant.now()));
    }

    public List<OrderEvent> handle(ShipOrderCommand cmd) {
        if (status != OrderStatus.PAID && status != OrderStatus.SUBMITTED)
            throw new IllegalStateException("Cannot ship order in " + status + " state");
        return List.of(new OrderShipped(cmd.orderId(), cmd.trackingNumber(),
            cmd.carrier(), Instant.now()));
    }

    // State queries
    public boolean canAddItems() { return status == OrderStatus.CREATED; }
    public boolean isSubmitted() { return status == OrderStatus.SUBMITTED || status.ordinal() > OrderStatus.SUBMITTED.ordinal(); }
    public boolean isCancelled() { return status == OrderStatus.CANCELLED; }
    public boolean isEmpty() { return items.isEmpty(); }
    public String getOrderId() { return orderId; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public long getVersion() { return version; }

    public static OrderAggregate replay(List<OrderEvent> events) {
        OrderAggregate aggregate = new OrderAggregate();
        events.forEach(aggregate::apply);
        return aggregate;
    }
}

public record OrderLineItem(
    String productId,
    String productName,
    int quantity,
    BigDecimal unitPrice
) {}

public enum OrderStatus {
    CREATED, SUBMITTED, PAID, SHIPPED, DELIVERED, CANCELLED
}
```

### Step 4: Event Store (Append-Only)

```java
public class EventStore {

    private static final Logger log = LoggerFactory.getLogger(EventStore.class);
    private final ConcurrentHashMap<String, List<OrderEvent>> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> versions = new ConcurrentHashMap<>();

    public synchronized void append(String aggregateId, List<OrderEvent> events, long expectedVersion) {
        Long currentVersion = versions.getOrDefault(aggregateId, 0L);
        if (!currentVersion.equals(expectedVersion)) {
            throw new ConcurrencyException("Version conflict for " + aggregateId
                + ": expected " + expectedVersion + " but was " + currentVersion);
        }
        store.computeIfAbsent(aggregateId, k -> new CopyOnWriteArrayList<>())
            .addAll(events);
        versions.put(aggregateId, expectedVersion + events.size());
        log.info("Appended {} events to {} (v{})", events.size(), aggregateId, expectedVersion + events.size());
    }

    public List<OrderEvent> readEvents(String aggregateId) {
        return List.copyOf(store.getOrDefault(aggregateId, List.of()));
    }

    public List<OrderEvent> readEventsSince(String aggregateId, long sinceVersion) {
        List<OrderEvent> all = store.getOrDefault(aggregateId, List.of());
        if (sinceVersion >= all.size()) return List.of();
        return List.copyOf(all.subList((int) sinceVersion, all.size()));
    }

    public long getVersion(String aggregateId) {
        return versions.getOrDefault(aggregateId, 0L);
    }

    public boolean exists(String aggregateId) {
        return store.containsKey(aggregateId);
    }

    public Set<String> getAllAggregateIds() {
        return Set.copyOf(store.keySet());
    }

    public long getEventCount(String aggregateId) {
        return store.getOrDefault(aggregateId, List.of()).size();
    }

    // Snapshot support
    public void saveSnapshot(String aggregateId, OrderAggregate snapshot, long version) {
        // In production, serialize snapshot to DB
        // Here we just log
        log.info("Snapshot saved for {} at version {}", aggregateId, version);
    }
}

class ConcurrencyException extends RuntimeException {
    public ConcurrencyException(String message) { super(message); }
}
```

### Step 5: Command Handler

```java
public class OrderCommandHandler {

    private final EventStore eventStore;

    public OrderCommandHandler(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public List<OrderEvent> handle(OrderCommand command) {
        return switch (command) {
            case CreateOrderCommand cmd -> handleCreate(cmd);
            case AddItemCommand cmd -> handleModify(cmd.orderId(), agg -> agg.handle(cmd));
            case RemoveItemCommand cmd -> handleModify(cmd.orderId(), agg -> agg.handle(cmd));
            case SubmitOrderCommand cmd -> handleModify(cmd.orderId(), agg -> agg.handle(cmd));
            case CancelOrderCommand cmd -> handleModify(cmd.orderId(), agg -> agg.handle(cmd));
            case ShipOrderCommand cmd -> handleModify(cmd.orderId(), agg -> agg.handle(cmd));
        };
    }

    List<OrderEvent> handleCreate(CreateOrderCommand cmd) {
        if (eventStore.exists(cmd.orderId())) {
            throw new IllegalArgumentException("Order already exists: " + cmd.orderId());
        }
        OrderAggregate aggregate = new OrderAggregate();
        List<OrderEvent> events = aggregate.handle(cmd);
        eventStore.append(cmd.orderId(), events, 0);
        return events;
    }

    List<OrderEvent> handleModify(String orderId,
                                    Function<OrderAggregate, List<OrderEvent>> handler) {
        long version = eventStore.getVersion(orderId);
        List<OrderEvent> pastEvents = eventStore.readEvents(orderId);
        OrderAggregate aggregate = OrderAggregate.replay(pastEvents);
        List<OrderEvent> newEvents = handler.apply(aggregate);
        eventStore.append(orderId, newEvents, version);
        return newEvents;
    }

    public OrderAggregate loadAggregate(String orderId) {
        return OrderAggregate.replay(eventStore.readEvents(orderId));
    }
}
```

### Step 6: Projections (Read Models)

```java
// Read model DTOs
public record OrderSummary(
    String orderId,
    String customerId,
    String customerName,
    OrderStatus status,
    BigDecimal totalAmount,
    int itemCount,
    Instant createdAt,
    Instant submittedAt,
    Instant shippedAt,
    String trackingNumber
) {}

public record CustomerOrderHistory(
    String customerId,
    List<OrderSummary> orders,
    int totalOrders,
    BigDecimal totalSpent
) {}

public record ProductSalesProjection(
    String productId,
    String productName,
    int totalSold,
    BigDecimal totalRevenue,
    LocalDate lastSoldDate
) {}

// Projection — Order Summary
@Component
public class OrderSummaryProjection {

    private final ConcurrentHashMap<String, OrderSummary> summaries = new ConcurrentHashMap<>();

    public void apply(OrderEvent event) {
        switch (event) {
            case OrderCreated e -> apply(e);
            case ItemAdded e -> apply(e);
            case OrderSubmitted e -> apply(e);
            case PaymentReceived e -> {/* status update handled in submit */ }
            case OrderCancelled e -> apply(e);
            case OrderShipped e -> apply(e);
            default -> {}
        }
    }

    void apply(OrderCreated event) {
        summaries.put(event.orderId(), new OrderSummary(
            event.orderId(), event.customerId(), event.customerName(),
            OrderStatus.CREATED, BigDecimal.ZERO, 0, event.timestamp(),
            null, null, null));
    }

    void apply(ItemAdded event) {
        summaries.computeIfPresent(event.orderId(), (id, summary) -> {
            BigDecimal newTotal = summary.totalAmount()
                .add(event.unitPrice().multiply(BigDecimal.valueOf(event.quantity())));
            return new OrderSummary(id, summary.customerId(), summary.customerName(),
                summary.status(), newTotal, summary.itemCount() + event.quantity(),
                summary.createdAt(), summary.submittedAt(), summary.shippedAt(),
                summary.trackingNumber());
        });
    }

    void apply(OrderSubmitted event) {
        summaries.computeIfPresent(event.orderId(), (id, summary) ->
            new OrderSummary(id, summary.customerId(), summary.customerName(),
                OrderStatus.SUBMITTED, summary.totalAmount(), summary.itemCount(),
                summary.createdAt(), event.timestamp(), summary.shippedAt(),
                summary.trackingNumber()));
    }

    void apply(OrderCancelled event) {
        summaries.computeIfPresent(event.orderId(), (id, summary) ->
            new OrderSummary(id, summary.customerId(), summary.customerName(),
                OrderStatus.CANCELLED, summary.totalAmount(), summary.itemCount(),
                summary.createdAt(), summary.submittedAt(), summary.shippedAt(),
                summary.trackingNumber()));
    }

    void apply(OrderShipped event) {
        summaries.computeIfPresent(event.orderId(), (id, summary) ->
            new OrderSummary(id, summary.customerId(), summary.customerName(),
                OrderStatus.SHIPPED, summary.totalAmount(), summary.itemCount(),
                summary.createdAt(), summary.submittedAt(), event.timestamp(),
                event.trackingNumber()));
    }

    public Optional<OrderSummary> getSummary(String orderId) {
        return Optional.ofNullable(summaries.get(orderId));
    }

    public List<OrderSummary> getAllSummaries() {
        return List.copyOf(summaries.values());
    }

    public List<OrderSummary> getByCustomer(String customerId) {
        return summaries.values().stream()
            .filter(s -> s.customerId().equals(customerId))
            .toList();
    }

    public long count() { return summaries.size(); }

    public void reset() { summaries.clear(); }
}

// Projection — Customer History
@Component
public class CustomerHistoryProjection {

    private final ConcurrentHashMap<String, CustomerOrderHistory> histories = new ConcurrentHashMap<>();
    private final OrderSummaryProjection summaryProjection;

    public CustomerHistoryProjection(OrderSummaryProjection summaryProjection) {
        this.summaryProjection = summaryProjection;
    }

    public void apply(OrderEvent event) {
        if (event instanceof OrderCreated created) {
            List<OrderSummary> orders = summaryProjection.getByCustomer(created.customerId());
            BigDecimal total = orders.stream()
                .map(OrderSummary::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            histories.put(created.customerId(), new CustomerOrderHistory(
                created.customerId(), orders, orders.size(), total));
        }
    }

    public Optional<CustomerOrderHistory> getHistory(String customerId) {
        return Optional.ofNullable(histories.get(customerId));
    }
}
```

### Step 7: Event Bus & Projection Updater

```java
@Component
public class EventBus {

    private static final Logger log = LoggerFactory.getLogger(EventBus.class);
    private final List<Consumer<OrderEvent>> subscribers = new CopyOnWriteArrayList<>();

    public void subscribe(Consumer<OrderEvent> subscriber) {
        subscribers.add(subscriber);
    }

    public void publish(OrderEvent event) {
        log.info("Publishing event: {} for order {}",
            event.getClass().getSimpleName(), event.orderId());
        subscribers.forEach(sub -> {
            try {
                sub.accept(event);
            } catch (Exception e) {
                log.error("Subscriber failed for event {}: {}",
                    event.getClass().getSimpleName(), e.getMessage());
            }
        });
    }

    public void publishAll(List<OrderEvent> events) {
        events.forEach(this::publish);
    }
}

@Component
public class ProjectionUpdater {

    private final List<Object> projections;

    public ProjectionUpdater(List<Object> projections) {
        this.projections = projections;
    }

    public void onEvent(OrderEvent event) {
        projections.forEach(proj -> {
            try {
                proj.getClass().getMethod("apply", OrderEvent.class).invoke(proj, event);
            } catch (NoSuchMethodException e) {
                // Projection doesn't handle this event type
            } catch (Exception e) {
                System.err.println("Projection error: " + e.getMessage());
            }
        });
    }

    public void replayAll(EventStore eventStore) {
        // Clear projections
        projections.forEach(proj -> {
            try {
                proj.getClass().getMethod("reset").invoke(proj);
            } catch (NoSuchMethodException e) { /* no reset */ }
            catch (Exception e) { System.err.println("Reset error: " + e.getMessage()); }
        });
        // Replay all events
        eventStore.getAllAggregateIds().forEach(id -> {
            eventStore.readEvents(id).forEach(this::onEvent);
        });
    }
}
```

### Step 8: Saga (Order Fulfillment Saga)

```java
public class OrderFulfillmentSaga {

    private static final Logger log = LoggerFactory.getLogger(OrderFulfillmentSaga.class);
    private final Map<String, SagaState> sagas = new ConcurrentHashMap<>();
    private final OrderCommandHandler commandHandler;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;

    public OrderFulfillmentSaga(OrderCommandHandler commandHandler,
                                 InventoryService inventoryService,
                                 PaymentService paymentService) {
        this.commandHandler = commandHandler;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    public void onEvent(OrderEvent event) {
        switch (event) {
            case OrderSubmitted e -> handleOrderSubmitted(e);
            case PaymentReceived e -> handlePaymentReceived(e);
            case OrderShipped e -> handleOrderShipped(e);
            case OrderCancelled e -> handleOrderCancelled(e);
            default -> {}
        }
    }

    void handleOrderSubmitted(OrderSubmitted event) {
        log.info("Saga: Order {} submitted — starting fulfillment", event.orderId());
        SagaState state = new SagaState(event.orderId(), Instant.now());
        sagas.put(event.orderId(), state);

        // Step 1: Process payment (async)
        OrderAggregate aggregate = commandHandler.loadAggregate(event.orderId());
        try {
            paymentService.processPayment(event.orderId(), aggregate.getTotalAmount());
            // In real system, payment service would emit PaymentReceived event
        } catch (Exception e) {
            log.error("Payment failed for order {}: {}", event.orderId(), e.getMessage());
            commandHandler.handle(new CancelOrderCommand(event.orderId(), "Payment failed"));
            sagas.remove(event.orderId());
        }
    }

    void handlePaymentReceived(PaymentReceived event) {
        SagaState state = sagas.get(event.orderId());
        if (state == null) return;
        state.paymentCompleted = true;
        log.info("Saga: Payment received for order {}", event.orderId());

        // Step 2: Reserve inventory
        OrderAggregate aggregate = commandHandler.loadAggregate(event.orderId());
        try {
            inventoryService.reserveInventory(event.orderId(), aggregate.getItems());
        } catch (Exception e) {
            log.error("Inventory reservation failed for order {}: {}",
                event.orderId(), e.getMessage());
            commandHandler.handle(new CancelOrderCommand(event.orderId(),
                "Inventory reservation failed"));
            sagas.remove(event.orderId());
        }
    }

    void handleOrderShipped(OrderShipped event) {
        SagaState state = sagas.get(event.orderId());
        if (state == null) return;
        state.shipped = true;
        log.info("Saga: Order {} shipped — fulfillment complete", event.orderId());
        sagas.remove(event.orderId());
    }

    void handleOrderCancelled(OrderCancelled event) {
        SagaState state = sagas.get(event.orderId());
        if (state == null) return;
        log.info("Saga: Order {} cancelled — initiating rollback", event.orderId());
        if (state.paymentCompleted) {
            paymentService.refund(event.orderId());
        }
        inventoryService.releaseInventory(event.orderId());
        sagas.remove(event.orderId());
    }

    static class SagaState {
        final String orderId;
        final Instant startedAt;
        boolean paymentCompleted;
        boolean shipped;

        SagaState(String orderId, Instant startedAt) {
            this.orderId = orderId;
            this.startedAt = startedAt;
        }
    }
}

// External service stubs
class PaymentService {
    void processPayment(String orderId, BigDecimal amount) {
        System.out.println("[Payment] Processing " + amount + " for order " + orderId);
    }
    void refund(String orderId) {
        System.out.println("[Payment] Refunding order " + orderId);
    }
}

class InventoryService {
    void reserveInventory(String orderId, Map<String, OrderLineItem> items) {
        System.out.println("[Inventory] Reserving " + items.size() + " items for order " + orderId);
    }
    void releaseInventory(String orderId) {
        System.out.println("[Inventory] Releasing inventory for order " + orderId);
    }
}
```

### Step 9: Snapshot Repository

```java
public class SnapshotRepository {

    private final EventStore eventStore;
    private static final int SNAPSHOT_THRESHOLD = 50;

    public SnapshotRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public OrderAggregate loadWithSnapshot(String aggregateId) {
        long eventCount = eventStore.getEventCount(aggregateId);

        if (eventCount > SNAPSHOT_THRESHOLD) {
            // In production: load last snapshot from DB
            // Here: simulate snapshot at threshold boundary
            long snapshotVersion = (eventCount / SNAPSHOT_THRESHOLD) * SNAPSHOT_THRESHOLD;
            List<OrderEvent> eventsSinceSnapshot = eventStore.readEventsSince(aggregateId, snapshotVersion);

            OrderAggregate aggregate = new OrderAggregate();
            eventsSinceSnapshot.forEach(aggregate::apply);
            return aggregate;
        }

        return OrderAggregate.replay(eventStore.readEvents(aggregateId));
    }

    public void maybeSaveSnapshot(OrderAggregate aggregate, String aggregateId) {
        long version = aggregate.getVersion();
        if (version > 0 && version % SNAPSHOT_THRESHOLD == 0) {
            eventStore.saveSnapshot(aggregateId, aggregate, version);
        }
    }
}
```

### Step 10: End-to-End Demo

```java
public class CqrsOrderDemo {

    public static void main(String[] args) {
        EventStore eventStore = new EventStore();
        OrderCommandHandler commandHandler = new OrderCommandHandler(eventStore);

        // Projections
        OrderSummaryProjection summaryProjection = new OrderSummaryProjection();
        CustomerHistoryProjection historyProjection = new CustomerHistoryProjection(summaryProjection);
        ProductSalesProjection productProjection = new ProductSalesProjection();

        // Wire projections via event bus
        EventBus eventBus = new EventBus();
        eventBus.subscribe(summaryProjection::apply);
        eventBus.subscribe(historyProjection::apply);

        // Saga
        OrderFulfillmentSaga saga = new OrderFulfillmentSaga(commandHandler,
            new InventoryService(), new PaymentService());
        eventBus.subscribe(saga::onEvent);

        // Use case: Create order with items
        String orderId = UUID.randomUUID().toString();

        // 1. Create order
        var createdEvents = commandHandler.handle(
            new CreateOrderCommand(orderId, "cust-1", "John Doe", "123 Main St"));
        eventBus.publishAll(createdEvents);

        // 2. Add items
        eventBus.publishAll(commandHandler.handle(
            new AddItemCommand(orderId, "PROD-1", "Widget", 2, new BigDecimal("19.99"))));
        eventBus.publishAll(commandHandler.handle(
            new AddItemCommand(orderId, "PROD-2", "Gadget", 1, new BigDecimal("49.99"))));

        // 3. Submit order
        eventBus.publishAll(commandHandler.handle(
            new SubmitOrderCommand(orderId)));

        // 4. Check projection
        summaryProjection.getSummary(orderId).ifPresent(summary ->
            System.out.println("Order Summary: " + summary));

        // 5. Ship order
        eventBus.publishAll(commandHandler.handle(
            new ShipOrderCommand(orderId, "TRACK-123", "UPS")));

        // 6. Verify final state
        OrderAggregate aggregate = commandHandler.loadAggregate(orderId);
        System.out.println("Final status: " + aggregate.getStatus());
        System.out.println("Total: $" + aggregate.getTotalAmount());
        System.out.println("Items: " + aggregate.getItems().size());

        // 7. Customer history
        historyProjection.getHistory("cust-1").ifPresent(history ->
            System.out.println("Customer total orders: " + history.totalOrders()));
    }
}
```

---

## Complexity Analysis

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Append events | O(1) amortized | Append-only list |
| Read all events | O(n) | n = events for aggregate |
| Read events since snapshot | O(n - m) | n = total, m = snapshot version |
| Replay aggregate | O(n) | Linear replay |
| Command handling | O(n) replay + O(1) handle | n = past events |
| Projection update | O(1) per event | HashMap update |
| Saga processing | O(1) per event | State machine |
| Snapshot save | O(1) | Serialize current state |

---

## Follow-Up Questions

1. **How would you handle idempotency?** — Store processed command IDs in a `ProcessedCommands` table. Before processing, check if command ID already exists. Return existing result without reapplying.

2. **How do you handle concurrent commands on the same aggregate?** — Optimistic locking via version. If version mismatch, retry the command (re-read events, re-apply). Max retry count with exponential backoff.

3. **How would you migrate event schemas?** — Event upcasters transform old formats during replay. Store both `event_type` and `event_version`. Upcasters convert v1 → v2. Never delete old upcaster code until all events are migrated.

4. **How do you test CQRS/ES systems?** — Test each aggregate: `given(events).when(command).then(events)`. Test projections independently. Use in-memory event store for unit tests. Test sagas with mocked services.

5. **When would you NOT use Event Sourcing?** — Simple CRUD apps, high-volume non-critical data, when strong consistency is required across aggregates, when team doesn't understand event-driven patterns.

---

## Test Cases

```java
class OrderAggregateTest {

    @Test
    void shouldCreateOrder() {
        var aggregate = new OrderAggregate();
        var events = aggregate.handle(new CreateOrderCommand(
            "order-1", "cust-1", "John", "Addr"));
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(OrderCreated.class);
    }

    @Test
    void shouldAddItem() {
        var aggregate = new OrderAggregate();
        aggregate.handle(new CreateOrderCommand("o1", "c1", "John", "Addr"));
        var events = aggregate.handle(
            new AddItemCommand("o1", "P1", "Product", 2, BigDecimal.TEN));
        assertThat(events.get(0)).isInstanceOf(ItemAdded.class);
        assertThat(aggregate.getTotalAmount()).isEqualTo(new BigDecimal("20"));
    }

    @Test
    void shouldReplayEvents() {
        var events = List.of(
            new OrderCreated("o1", "c1", "John", "Addr", Instant.now()),
            new ItemAdded("o1", "P1", "Product", 2, BigDecimal.TEN, Instant.now()));
        var aggregate = OrderAggregate.replay(events);
        assertThat(aggregate.getTotalAmount()).isEqualTo(new BigDecimal("20"));
        assertThat(aggregate.getStatus()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void shouldNotSubmitEmptyOrder() {
        var aggregate = new OrderAggregate();
        aggregate.handle(new CreateOrderCommand("o1", "c1", "John", "Addr"));
        assertThatThrownBy(() -> aggregate.handle(new SubmitOrderCommand("o1")))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldNotAddItemAfterSubmission() {
        var events = List.of(
            new OrderCreated("o1", "c1", "John", "Addr", Instant.now()),
            new ItemAdded("o1", "P1", "P", 1, BigDecimal.ONE, Instant.now()),
            new OrderSubmitted("o1", Instant.now()));
        var aggregate = OrderAggregate.replay(events);
        assertThatThrownBy(() -> aggregate.handle(
            new AddItemCommand("o1", "P2", "P2", 1, BigDecimal.ONE)))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldHandleVersionConflict() {
        EventStore store = new EventStore();
        store.append("o1", List.of(
            new OrderCreated("o1", "c1", "John", "Addr", Instant.now())), 0);
        assertThatThrownBy(() -> store.append("o1", List.of(
            new ItemAdded("o1", "P1", "P", 1, BigDecimal.ONE, Instant.now())), 0))
            .isInstanceOf(ConcurrencyException.class);
    }

    @Test
    void projectionShouldTrackOrderSummary() {
        var projection = new OrderSummaryProjection();
        projection.apply(new OrderCreated("o1", "c1", "John", "Addr", Instant.now()));
        projection.apply(new ItemAdded("o1", "P1", "P", 2, BigDecimal.TEN, Instant.now()));
        projection.apply(new OrderSubmitted("o1", Instant.now()));

        var summary = projection.getSummary("o1").orElseThrow();
        assertThat(summary.status()).isEqualTo(OrderStatus.SUBMITTED);
        assertThat(summary.totalAmount()).isEqualTo(new BigDecimal("20"));
        assertThat(summary.itemCount()).isEqualTo(2);
    }

    @Test
    void endToEndOrderFlow() {
        EventStore eventStore = new EventStore();
        OrderCommandHandler handler = new OrderCommandHandler(eventStore);

        String orderId = UUID.randomUUID().toString();
        handler.handle(new CreateOrderCommand(orderId, "c1", "John", "Addr"));
        handler.handle(new AddItemCommand(orderId, "P1", "Product", 1, BigDecimal.TEN));
        handler.handle(new SubmitOrderCommand(orderId));

        var aggregate = handler.loadAggregate(orderId);
        assertThat(aggregate.getStatus()).isEqualTo(OrderStatus.SUBMITTED);
        assertThat(aggregate.getTotalAmount()).isEqualTo(BigDecimal.TEN);
    }
}
```

---

## Summary

This CQRS/Event Sourcing implementation demonstrates:
- **Command/Query separation**: commands go to `OrderCommandHandler`, reads go to projections
- **Event sourcing**: `EventStore` is append-only, aggregate state derived from replay
- **Projections**: `OrderSummaryProjection` and `CustomerHistoryProjection` maintain denormalized read models
- **Event bus**: pub/sub delivery to multiple projections
- **Saga**: multi-step `OrderFulfillmentSaga` with compensating transactions
- **Snapshots**: `SnapshotRepository` reduces replay overhead for large aggregates
- **Optimistic concurrency**: version-based conflict detection
- **Immutable events**: Java 21 records prevent mutation of history