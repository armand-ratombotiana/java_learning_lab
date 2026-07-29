# CQRS/Event Sourcing — Deep Interview Guide

## Table of Contents
1. [CQRS Fundamentals](#cqrs-fundamentals)
2. [Event Sourcing Concepts](#event-sourcing-concepts)
3. [Axon Framework](#axon-framework)
4. [Aggregates & Command Handlers](#aggregates--command-handlers)
5. [Event Handlers & Projections](#event-handlers--projections)
6. [Snapshots & Event Replay](#snapshots--event-replay)
7. [Sagas & Choreography](#sagas--choreography)
8. [Java Code Examples](#java-code-examples)
9. [15+ Interview Questions](#15-interview-questions)

---

## CQRS Fundamentals

Command Query Responsibility Segregation separates read and write models into distinct paths.

### Core Principle

```
Client
 ├── Command → Command Handler → Write Model (Event Store)
 └── Query → Query Handler → Read Model (Projection DB)
```

| Path | Writes (Commands) | Reads (Queries) |
|------|-------------------|-----------------|
| **Purpose** | Change state | Return state |
| **Returns** | Void / event ID | Data |
| **Side effects** | Yes | No |
| **Model** | Domain model (aggregate) | DTO / view model |
| **Database** | Event Store (append-only) | SQL, NoSQL, cache |
| **Schema** | Normalized | Denormalized |

### Benefits

| Benefit | Description |
|---------|-------------|
| **Scalability** | Read/write scale independently |
| **Optimized reads** | Every query has its own optimized table |
| **Audit trail** | All changes are events |
| **Flexibility** | New projections without changing commands |
| **Complexity isolation** | Write side is DDD, read side is simple |

### Trade-offs

| Trade-off | Impact |
|-----------|--------|
| **Eventual consistency** | Read model lags behind write |
| **Operational complexity** | Two databases, message bus |
| **Learning curve** | Developers must understand event-driven thinking |
| **Event schema evolution** | Events are immutable, versioning needed |
| **No direct joins** | Read models must be pre-joined |

---

## Event Sourcing Concepts

### Event Store

Append-only log of facts:

```
Event Store (Append-only)
┌─────────────────────────────────────────┐
│ #1 | OrderCreated { orderId, customer } │
│ #2 | OrderItemAdded { sku, qty, price } │
│ #3 | OrderSubmitted { timestamp }       │
│ #4 | PaymentReceived { amount, method } │
│ #5 | OrderShipped { trackingNumber }    │
│ #6 | OrderDelivered { signedBy }        │
└─────────────────────────────────────────┘
```

### Aggregate

A cluster of domain objects treated as a unit, identified by `AggregateIdentifier`. State is derived by replaying events.

```
Order Aggregate
 ├── Event: OrderCreated → state: { id, customerId, status: CREATED }
 ├── Event: OrderItemAdded → state: { items: [{sku, qty}] }
 ├── Event: OrderSubmitted → state: { status: SUBMITTED }
 └── Event: OrderShipped → state: { status: SHIPPED, tracking: "1Z..." }
```

### Event Characteristics

| Property | Description |
|----------|-------------|
| **Immutable** | Cannot be changed after appending |
| **Append-only** | No delete, no update |
| **Timestamped** | When the event happened |
| **Versioned** | Schema evolves with `EventUpcaster` |
| **Idempotent** | Same events produce same state |
| **Auditable** | Full history of every change |

---

## Axon Framework

Axon is a Java framework for CQRS/Event Sourcing with first-class support for aggregates, sagas, and projections.

### Architecture

```
Axon Application
 ┌─────────────────────────────────────┐
 │ Command Bus ──→ Command Handler     │
 │    │              ↓                 │
 │    │         Aggregate ──→ Event    │
 │    │              ↓                 │
 │ Event Bus ──→ Event Handler         │
 │    │         (Projection)           │
 │    │              ↓                 │
 │ Query Bus ──→ Query Handler         │
 └─────────────────────────────────────┘
```

### Modules

| Module | Purpose |
|--------|---------|
| **axon-core** | Command/Event/Query buses, aggregate support |
| **axon-spring-boot-starter** | Spring Boot auto-configuration |
| **axon-mongo** | MongoDB event storage |
| **axon-micrometer** | Metrics integration |
| **axon-test** | Fixtures for testing aggregates |

### Configuration

```java
@Configuration
public class AxonConfig {

    @Bean
    public EventStore eventStore(MongoTemplate mongoTemplate) {
        return MongoEventStore.builder()
            .mongoTemplate(mongoTemplate)
            .storageIdentifier("domainEvents")
            .build();
    }

    @Bean
    public SnapshotTriggerDefinition snapshotTrigger(
            Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 100);
    }
}
```

---

## Aggregates & Command Handlers

### Aggregate Definition

```java
@Aggregate(snapshotTriggerDefinition = "orderSnapshotTrigger")
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String customerId;
    private OrderStatus status;
    private Money totalAmount;
    private List<OrderItem> items;

    protected OrderAggregate() {
        // Axon requires no-arg constructor
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        apply(OrderCreatedEvent.builder()
            .orderId(cmd.orderId())
            .customerId(cmd.customerId())
            .timestamp(Instant.now())
            .build());
    }

    @CommandHandler
    public void handle(AddItemCommand cmd) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add items to order in status: " + status);
        }
        apply(OrderItemAddedEvent.builder()
            .orderId(cmd.orderId())
            .sku(cmd.sku())
            .quantity(cmd.quantity())
            .unitPrice(cmd.unitPrice())
            .build());
    }

    @CommandHandler
    public void handle(SubmitOrderCommand cmd) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot submit order in status: " + status);
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Cannot submit empty order");
        }
        apply(OrderSubmittedEvent.builder()
            .orderId(cmd.orderId())
            .submittedAt(Instant.now())
            .build());
    }

    @CommandHandler
    public void handle(CancelOrderCommand cmd) {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel order in status: " + status);
        }
        apply(OrderCancelledEvent.builder()
            .orderId(cmd.orderId())
            .reason(cmd.reason())
            .cancelledAt(Instant.now())
            .build());
    }

    // Event Sourcing Handlers — update state
    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.orderId();
        this.customerId = event.customerId();
        this.status = OrderStatus.CREATED;
        this.items = new ArrayList<>();
        this.totalAmount = Money.of(0);
    }

    @EventSourcingHandler
    public void on(OrderItemAddedEvent event) {
        items.add(new OrderItem(event.sku(), event.quantity(), event.unitPrice()));
        this.totalAmount = this.totalAmount.add(
            event.unitPrice().multiply(event.quantity()));
    }

    @EventSourcingHandler
    public void on(OrderSubmittedEvent event) {
        this.status = OrderStatus.SUBMITTED;
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.status = OrderStatus.CANCELLED;
    }
}
```

### Commands & Events Records (Java 21)

```java
// Commands
public record CreateOrderCommand(String orderId, String customerId) implements Command {}
public record AddItemCommand(String orderId, String sku, int quantity, Money unitPrice) {}
public record SubmitOrderCommand(String orderId) {}
public record CancelOrderCommand(String orderId, String reason) {}

// Events
@Builder
public record OrderCreatedEvent(String orderId, String customerId, Instant timestamp) {}
@Builder
public record OrderItemAddedEvent(String orderId, String sku, int quantity, Money unitPrice) {}
@Builder
public record OrderSubmittedEvent(String orderId, Instant submittedAt) {}
@Builder
public record OrderCancelledEvent(String orderId, String reason, Instant cancelledAt) {}
```

### Command Gateway (Dispatching Commands)

```java
@Service
public class OrderCommandService {

    private final CommandGateway commandGateway;

    public OrderCommandService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    public CompletableFuture<String> createOrder(String customerId) {
        return commandGateway.send(new CreateOrderCommand(
            UUID.randomUUID().toString(), customerId));
    }

    public CompletableFuture<Void> addItem(String orderId, String sku, int qty, Money price) {
        return commandGateway.send(new AddItemCommand(orderId, sku, qty, price));
    }

    public CompletableFuture<Void> submitOrder(String orderId) {
        return commandGateway.send(new SubmitOrderCommand(orderId));
    }

    public CompletableFuture<Void> cancelOrder(String orderId, String reason) {
        return commandGateway.send(new CancelOrderCommand(orderId, reason));
    }
}
```

---

## Event Handlers & Projections

### Projection (Read Model)

```java
@Component
public class OrderProjection {

    private final OrderViewRepository orderViewRepository;

    public OrderProjection(OrderViewRepository orderViewRepository) {
        this.orderViewRepository = orderViewRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderView view = new OrderView();
        view.setId(event.orderId());
        view.setCustomerId(event.customerId());
        view.setStatus("CREATED");
        view.setTotalAmount(BigDecimal.ZERO);
        view.setCreatedAt(event.timestamp());
        view.setItems(List.of());
        orderViewRepository.save(view);
    }

    @EventHandler
    public void on(OrderItemAddedEvent event) {
        orderViewRepository.findById(event.orderId()).ifPresent(view -> {
            BigDecimal itemTotal = event.unitPrice().amount()
                .multiply(BigDecimal.valueOf(event.quantity()));
            List<OrderItemView> updatedItems = new ArrayList<>(view.getItems());
            updatedItems.add(new OrderItemView(event.sku(), event.quantity(),
                event.unitPrice().amount(), itemTotal));
            view.setItems(updatedItems);
            view.setTotalAmount(view.getTotalAmount().add(itemTotal));
            orderViewRepository.save(view);
        });
    }

    @EventHandler
    public void on(OrderSubmittedEvent event) {
        orderViewRepository.findById(event.orderId()).ifPresent(view -> {
            view.setStatus("SUBMITTED");
            view.setSubmittedAt(event.submittedAt());
            orderViewRepository.save(view);
        });
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        orderViewRepository.findById(event.orderId()).ifPresent(view -> {
            view.setStatus("CANCELLED");
            view.setCancelledAt(event.cancelledAt());
            orderViewRepository.save(view);
        });
    }
}
```

### Query Handler

```java
@RestController
@RequestMapping("/api/orders")
public class OrderQueryController {

    private final OrderViewRepository orderViewRepository;

    public OrderQueryController(OrderViewRepository orderViewRepository) {
        this.orderViewRepository = orderViewRepository;
    }

    @GetMapping("/{id}")
    public OrderView getOrder(@PathVariable String id) {
        return orderViewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    @GetMapping
    public Page<OrderView> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return orderViewRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderView> getCustomerOrders(@PathVariable String customerId) {
        return orderViewRepository.findByCustomerId(customerId);
    }
}
```

### Multiple Projections

```java
@Component
public class OrderSummaryProjection {

    private final OrderSummaryRepository summaryRepository;

    @EventHandler
    public void on(OrderItemAddedEvent event) {
        // Update a daily sales summary projection
        summaryRepository.findByDate(LocalDate.now()).ifPresentOrElse(
            summary -> {
                summary.setTotalOrders(summary.getTotalOrders() + 1);
                summary.setTotalRevenue(summary.getTotalRevenue()
                    .add(event.unitPrice().amount().multiply(
                        BigDecimal.valueOf(event.quantity()))));
                summaryRepository.save(summary);
            },
            () -> summaryRepository.save(new OrderSummary(
                LocalDate.now(), 1,
                event.unitPrice().amount().multiply(
                    BigDecimal.valueOf(event.quantity()))
            ))
        );
    }
}
```

---

## Snapshots & Event Replay

### Why Snapshots?

Loading an aggregate with 100,000 events requires replaying all events. Snapshots capture aggregate state at a point in time.

```
Without Snapshot:
Load Order-123 → replay events 1..100,000

With Snapshot (every 100 events):
Load Order-123 → load snapshot v99 → replay events 100..100,000
```

### Snapshot Configuration

```java
@Bean
public SnapshotTriggerDefinition orderSnapshotTrigger(
        Snapshotter snapshotter) {
    return new EventCountSnapshotTriggerDefinition(snapshotter, 100);
}

// On the aggregate
@Aggregate(snapshotTriggerDefinition = "orderSnapshotTrigger")
public class OrderAggregate {
    // ...
}
```

### Manual Snapshot Creation

```java
@Service
public class SnapshotManagementService {

    private final EmbeddedEventStore eventStore;

    public SnapshotManagementService(EventStore eventStore) {
        this.eventStore = (EmbeddedEventStore) eventStore;
    }

    public void createSnapshot(String aggregateType, String aggregateId) {
        eventStore.storeSnapshot(aggregateType, aggregateId, ...);
    }

    @Scheduled(fixedRate = 3600000) // hourly
    public void scheduledSnapshotCleanup() {
        // Remove old snapshots, keep last N per aggregate
    }
}
```

### Event Replay

```java
@Component
public class EventReplayService {

    private final EventStore eventStore;
    private final EventBus eventBus;

    public EventReplayService(EventStore eventStore, EventBus eventBus) {
        this.eventStore = eventStore;
        this.eventBus = eventBus;
    }

    public void replayEventsForProjection(String projectionName) {
        // Reset projection database
        orderViewRepository.deleteAll();

        // Replay all events from the beginning
        DomainEventStream stream = eventStore.readEvents("OrderAggregate");
        while (stream.hasNext()) {
            DomainEventMessage<?> event = stream.next();
            eventBus.publish(EventUtils.asPublishedEvent(event));
        }
    }

    @Scheduled(cron = "0 0 3 * * *") // Daily at 3 AM
    public void scheduledReplayForAudit() {
        log.info("Starting nightly event replay for audit projection");
        replayEventsForProjection("auditProjection");
    }
}
```

---

## Sagas & Choreography

### Saga (Axon)

A saga manages a long-running business transaction across multiple aggregates/services.

```
Order Saga
 ┌──────────────────────────────────────────────────┐
 │ CreateOrder → PaymentRequired → PaymentReceived   │
 │     ↓                                              │
 │ ReserveInventory → InventoryReserved               │
 │     ↓                                              │
 │ ShipOrder → OrderShipped → Complete                │
 └───────────────────────────────────────────────────┘
```

### Saga Implementation

```java
@Saga
public class OrderFulfillmentSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private Money totalAmount;
    private boolean paymentReceived;
    private boolean inventoryReserved;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        this.orderId = event.orderId();
        log.info("Saga started for order: {}", orderId);

        // Associate saga with payment and shipment
        SagaLifecycle.associateWith("paymentId", orderId);
        SagaLifecycle.associateWith("shipmentId", orderId);

        // Request payment
        commandGateway.send(new ProcessPaymentCommand(
            UUID.randomUUID().toString(), orderId, totalAmount));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentReceivedEvent event) {
        this.paymentReceived = true;
        log.info("Payment received for order: {}", orderId);

        // Reserve inventory after payment
        commandGateway.send(new ReserveInventoryCommand(
            UUID.randomUUID().toString(), orderId));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryReservedEvent event) {
        this.inventoryReserved = true;
        log.info("Inventory reserved for order: {}", orderId);

        // Start shipment
        commandGateway.send(new ShipOrderCommand(
            UUID.randomUUID().toString(), orderId));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderShippedEvent event) {
        log.info("Order shipped: {}", orderId);
        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCancelledEvent event) {
        log.warn("Order cancelled during fulfillment: {}", orderId);
        // Compensating actions
        if (paymentReceived) {
            commandGateway.send(new RefundPaymentCommand(orderId));
        }
        if (inventoryReserved) {
            commandGateway.send(new ReleaseInventoryCommand(orderId));
        }
        SagaLifecycle.end();
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderDeliveryConfirmedEvent event) {
        log.info("Saga completed for order: {}", orderId);
    }
}
```

### Choreography vs Orchestration

| Aspect | Choreography | Orchestration (Saga) |
|--------|-------------|---------------------|
| **Coordination** | Decentralized (events) | Centralized (saga) |
| **Complexity** | Hard to trace | Single flow to follow |
| **Coupling** | Loose (event-driven) | Tighter (saga knows all steps) |
| **Error handling** | Implicit (eventual consistency) | Explicit (compensating actions) |
| **Best for** | Simple chains | Complex workflows with compensating txns |
| **Example** | Order→Inventory→Shipment | Booking system (hotel+flight+car) |

---

## Java Code Examples

### 1. Complete Axon Aggregate with Validation

```java
@Aggregate
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private PaymentStatus status;
    private Money amount;

    protected PaymentAggregate() {}

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand cmd) {
        if (cmd.amount().isNegativeOrZero()) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        apply(PaymentInitiatedEvent.builder()
            .paymentId(cmd.paymentId())
            .orderId(cmd.orderId())
            .amount(cmd.amount())
            .initiatedAt(Instant.now())
            .build());
    }

    @CommandHandler
    public void handle(MarkPaymentCompletedCommand cmd) {
        if (status != PaymentStatus.INITIATED) {
            throw new IllegalStateException("Payment not in initiated state: " + status);
        }
        apply(PaymentCompletedEvent.builder()
            .paymentId(cmd.paymentId())
            .orderId(orderId)
            .completedAt(Instant.now())
            .build());
    }

    @EventSourcingHandler
    public void on(PaymentInitiatedEvent event) {
        this.paymentId = event.paymentId();
        this.orderId = event.orderId();
        this.amount = event.amount();
        this.status = PaymentStatus.INITIATED;
    }

    @EventSourcingHandler
    public void on(PaymentCompletedEvent event) {
        this.status = PaymentStatus.COMPLETED;
    }
}
```

### 2. Testing an Aggregate (Axon Test Fixtures)

```java
class OrderAggregateTest {

    private FixtureConfiguration<OrderAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    void shouldCreateOrder() {
        fixture.givenNoPriorActivity()
            .when(new CreateOrderCommand("order-1", "customer-1"))
            .expectEvents(new OrderCreatedEvent("order-1", "customer-1", any(Instant.class)));
    }

    @Test
    void shouldAddItemToOrder() {
        fixture.given(new OrderCreatedEvent("order-1", "customer-1", Instant.now()))
            .when(new AddItemCommand("order-1", "SKU-001", 2, Money.of(100)))
            .expectEvents(new OrderItemAddedEvent("order-1", "SKU-001", 2, Money.of(100)));
    }

    @Test
    void shouldNotAddItemToSubmittedOrder() {
        fixture.given(
                new OrderCreatedEvent("order-1", "customer-1", Instant.now()),
                new OrderSubmittedEvent("order-1", Instant.now()))
            .when(new AddItemCommand("order-1", "SKU-001", 1, Money.of(50)))
            .expectException(IllegalStateException.class);
    }

    @Test
    void shouldSubmitOrderWithItems() {
        fixture.given(
                new OrderCreatedEvent("order-1", "customer-1", Instant.now()),
                new OrderItemAddedEvent("order-1", "SKU-001", 1, Money.of(50)))
            .when(new SubmitOrderCommand("order-1"))
            .expectEvents(new OrderSubmittedEvent("order-1", any(Instant.class)));
    }

    @Test
    void shouldNotSubmitEmptyOrder() {
        fixture.given(new OrderCreatedEvent("order-1", "customer-1", Instant.now()))
            .when(new SubmitOrderCommand("order-1"))
            .expectException(IllegalStateException.class);
    }
}
```

### 3. Saga with Deadline (Timeout Handling)

```java
@Saga
public class PaymentTimeoutSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private String paymentId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        this.orderId = event.orderId();
        this.paymentId = UUID.randomUUID().toString();

        // Schedule deadline — 5 minutes for payment
        DeadlineManager.schedule(Duration.ofMinutes(5), "paymentDeadline");
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentCompletedEvent event) {
        // Cancel the deadline since payment succeeded
        DeadlineManager.cancelAll("paymentDeadline");
        commandGateway.send(new ReserveInventoryCommand(orderId));
    }

    @DeadlineHandler(deadlineName = "paymentDeadline")
    public void onPaymentDeadline() {
        log.warn("Payment deadline expired for order: {}", orderId);
        // Cancel order due to payment timeout
        commandGateway.send(new CancelOrderCommand(orderId, "Payment timeout"));
        SagaLifecycle.end();
    }
}
```

### 4. Event Upcaster (Schema Migration)

```java
public class OrderEventUpcaster extends EventUpcaster {

    @Override
    protected boolean canUpcast(DomainEventData intermediateEvent) {
        return "OrderCreatedEvent".equals(intermediateEvent.getType())
            && intermediateEvent.getPayloadRevision() == null;
    }

    @Override
    protected Stream<IntermediateEventRepresentation> doUpcast(
            Stream<IntermediateEventRepresentation> stream) {
        return stream.map(event -> {
            // Old: { orderId, customerId }
            // New: { orderId, customerId, version, source }
            Document document = event.getData();
            JsonNode root = (JsonNode) document;
            ObjectNode updated = ((ObjectNode) root).put("version", 2)
                .put("source", "LEGACY_MIGRATION");
            return event.withPayload(
                new JacksonDocument(updated, objectMapper),
                "2"
            );
        });
    }
}
```

---

## 15+ Interview Questions

### Basic

1. **What is CQRS?** — Separates read and write models. Commands change state, queries read state. Each has its own optimized data model and may use different databases.

2. **What is Event Sourcing?** — Stores state changes as an append-only event log. Current state is derived by replaying events. Provides full audit trail and temporal query capability.

3. **What is an aggregate in DDD/CQRS?** — A cluster of domain objects treated as a unit. Has an aggregate identifier. State is derived from events. Transaction boundary for commands.

### Intermediate

4. **How does Axon Framework implement CQRS?** — Command Bus dispatches commands to aggregates. Aggregates produce events. Event Bus delivers events to projections/event handlers. Query Bus serves read models.

5. **Explain the role of snapshots.** — Prevents replaying the entire event stream. Snapshot stores aggregate state at a specific version. Load → Snapshot + events after snapshot → Current state.

6. **What is a saga in Axon?** — A stateful coordinator for long-running transactions across aggregates. Listens to events, sends commands. Tracks state between events. Can have timeouts/deadlines.

7. **How do you handle event schema evolution?** — Event Upcasters transform old event formats to new ones during replay. Never modify existing events. Create new event versions with upcasters.

8. **What is the difference between a saga and a process manager?** — Saga: coordinates events/commands, typically within bounded context. Process manager: more complex, across multiple contexts, stores routing state.

### Advanced

9. **Design a multi-tenant CQRS system.** — Per-tenant event store (database per tenant or partition key). Command/query buses route based on tenant context. Projections use tenant-isolated databases.

10. **How do you handle eventual consistency between write and read models?** — Tracking offsets. Projection rebuild. Dead letter queues for failed events. Monitoring lag with metrics. Circuit breaker if lag exceeds threshold.

11. **Explain how to implement a saga with compensating transactions.** — Each step has a compensating action. Saga tracks completion state. If step N fails, rollback steps N-1...1. Refund payment, release inventory, cancel shipment.

12. **How does Axon's event store ensure consistency?** — Optimistic locking via aggregate version. Each event increments version. Concurrent writers to same aggregate fail with `ConcurrencyException`.

13. **Design a reporting system on top of an event store.** — Separate read model for reporting. Replay events to build reporting projections. Daily/real-time sync. CQRS naturally supports multiple read models.

14. **How do you test an aggregate in Axon?** — `AggregateTestFixture`. `given(givenEvents).when(command).expectEvents(resultEvents)` or `expectException()`. Fixture replays events, executes command, verifies output events.

15. **Explain the trade-offs between event sourcing and traditional CRUD.** — ES: audit trail, temporal queries, flexible projections, complex, eventual consistency. CRUD: simple, immediate consistency, no history, hard to add new queries.

16. **How do you implement idempotency in command handling?** — Idempotency key in command. Check if event already exists before processing. Store processed command IDs. Return existing result for duplicate commands.

17. **What happens when a projection fails to process an event?** — Log error, send to DLQ. Monitoring alerts on projection lag. Dead letter queue allows manual retry or event skipping with compensating action.

18. **How do you version events in practice?** — `@Revision` annotation or `SerialVersionUID`. Upcasters convert old → new. Never delete old event classes. Delete old upcasters after all events are migrated.