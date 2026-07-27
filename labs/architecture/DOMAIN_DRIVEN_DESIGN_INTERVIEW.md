# Domain-Driven Design Interview Guide

> DDD concepts and their application in software architecture interviews — from tactical patterns to strategic design.

---

## Table of Contents

1. [Why DDD in Interviews](#1-why-ddd-in-interviews)
2. [Strategic DDD for Architecture Design](#2-strategic-ddd-for-architecture-design)
3. [Tactical DDD for Implementation](#3-tactical-ddd-for-implementation)
4. [Bounded Context in System Design](#4-bounded-context-in-system-design)
5. [Aggregate Design](#5-aggregate-design)
6. [Entity vs Value Object](#6-entity-vs-value-object)
7. [Domain Events](#7-domain-events)
8. [Ubiquitous Language](#8-ubiquitous-language)
9. [DDD + Microservices: The Perfect Pair](#9-ddd--microservices-the-perfect-pair)
10. [Common DDD Interview Questions](#10-common-ddd-interview-questions)
11. [DDD Anti-Patterns in Interviews](#11-ddd-anti-patterns-in-interviews)
12. [Practical DDD: Code Examples](#12-practical-ddd-code-examples)
13. [DDD in System Design: Complete Walkthrough](#13-ddd-in-system-design-complete-walkthrough)

---

## 1. Why DDD in Interviews

Domain-Driven Design appears in interviews because:

- **Microservices boundaries**: DDD's bounded context is the primary method for defining service boundaries
- **Complex domains**: DDD provides the vocabulary to discuss complex business logic
- **Common vocabulary**: Ubiquitous language ensures everyone means the same thing
- **Strategic thinking**: DDD forces you to consider the business problem, not just technical solutions

### Levels of DDD Knowledge Expected

| Level | What You Should Know | Interview Context |
|-------|---------------------|------------------|
| **Junior/Senior** | Entity vs Value Object, Aggregates | Design a class, model a domain concept |
| **Senior/Staff** | Bounded Context, Domain Events, Repository pattern | Decompose a system into services, discuss event flow |
| **Staff+** | Strategic DDD, Context Mapping, Event Storming, Anti-Corruption Layer | Define service boundaries, discuss organizational impact |

---

## 2. Strategic DDD for Architecture Design

### Core Concepts

| Concept | Definition | Interview Use |
|---------|-----------|---------------|
| **Domain** | The sphere of knowledge/activity the software addresses | What business problem are we solving? |
| **Subdomain** | A part of the domain with a focused purpose | Break down the problem into manageable parts |
| **Core Domain** | The most important subdomain — competitive advantage | Where to invest the most design effort |
| **Supporting Subdomain** | Important but not core — buy or build? | Build internally for integration |
| **Generic Subdomain** | Commodity — buy or use open source | Choose off-the-shelf solutions |
| **Bounded Context** | Boundary within which a model applies | The boundary of a service/microservice |

### Domain Analysis in Interviews

When presented with a system design question, start with domain analysis:

```
1. Identify the domain: What business problem?
2. Identify subdomains: Break the problem into parts
3. Classify subdomains: Core / Supporting / Generic
4. Define bounded contexts: Map subdomains to context boundaries
5. Establish context maps: How do contexts interact?
```

**Example: Design a Payment System**

```
Domain: Payment processing

Subdomains:
├── Core: Payment authorization, settlement, reconciliation
├── Supporting: Fraud detection, dispute management
└── Generic: Notification, reporting, audit logging

Bounded Contexts:
├── Payment Processing (core)
│   ├── Authorize payment, capture, refund, void
│   └── Owns: Payment, Transaction, AuthorizationCode
├── Fraud Detection (supporting)
│   ├── Score transaction, block suspicious activity
│   └── Owns: FraudRule, RiskProfile, TransactionHistory
└── Notification (generic)
    ├── Send email, SMS, push notification
    └── Owns: NotificationTemplate, DeliveryStatus
```

### Context Mapping

How bounded contexts relate to each other:

| Relationship | Description | When to Use |
|-------------|-------------|-------------|
| **Partnership** | Mutual collaboration, coordinated releases | Two core domains with tight integration |
| **Shared Kernel** | Shared subset of model | When contexts genuinely overlap (rare) |
| **Customer-Supplier** | Upstream supplies, downstream consumes | API provider/consumer relationship |
| **Conformist** | Downstream conforms to upstream | When you cannot influence the upstream |
| **Anti-Corruption Layer** | Translation between contexts | Protecting your model from external models |
| **Open-Host Service** | Well-defined protocol for integration | When many contexts need to integrate |
| **Separate Ways** | No integration | When integration cost exceeds benefit |

---

## 3. Tactical DDD for Implementation

### Building Blocks

| Pattern | Purpose | Interview Use |
|---------|---------|---------------|
| **Entity** | Object with identity that changes over time | "A User has an ID and can change email" |
| **Value Object** | Object defined by its attributes, immutable | "Money is $10 USD — two are interchangeable" |
| **Aggregate** | Cluster of entities/VOs with consistency boundary | "An Order and its OrderItems" |
| **Aggregate Root** | The only entity external code references | "Order is the root — you don't reference OrderItem directly" |
| **Domain Event** | Something significant that happened | "OrderPlaced triggers PaymentProcessing" |
| **Repository** | Collection-like access to aggregates | "OrderRepository.findById(id)" |
| **Domain Service** | Stateless operation that doesn't fit on an entity | "PricingService.calculate(order)" |
| **Factory** | Creating complex aggregates | "OrderFactory.createFromCart(cart)" |
| **Specification** | Business rule as an object | "EligibleForFreeShippingSpec" |

### Entity vs Value Object Decision Table

| Question | Entity | Value Object |
|----------|--------|-------------|
| Does it have an identity? | Yes (must have ID) | No (defined by attributes) |
| Is it mutable? | Usually yes | No (immutable) |
| Are two equal if attributes match? | No (different IDs) | Yes |
| Can it exist independently? | Yes | Usually owned by entity |
| Example | User, Order, Product | Money, Address, Email, Color |

**Interview tip**: Always ask "Is this an entity or a value object?" when modeling domain concepts. It shows depth.

```java
// Value Object — immutable, no identity
public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new IllegalArgumentException("Currency mismatch");
        return new Money(this.amount.add(other.amount), this.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}

// Entity — has identity, mutable
public class Order {
    private final OrderId id;      // Identity
    private OrderStatus status;    // Mutates over time
    private List<OrderLine> items; // Value objects within aggregate
    private Money total;
    private CustomerId customerId; // Reference to another aggregate by ID

    public Order(OrderId id, CustomerId customerId) {
        this.id = id;
        this.customerId = customerId;
        this.status = OrderStatus.PENDING;
        this.items = new ArrayList<>();
        this.total = Money.ZERO;
    }

    public void addItem(ProductId productId, int quantity, Money price) {
        // Invariant check
        if (status != OrderStatus.PENDING)
            throw new IllegalStateException("Cannot modify non-pending order");

        items.add(new OrderLine(productId, quantity, price));
        total = total.add(price.multiply(quantity));
    }
}
```

---

## 4. Bounded Context in System Design

### Why Bounded Context Defines Service Boundaries

**One model does not fit all.** The same concept has different meaning in different contexts.

**Example: "Customer" in different contexts**

| Context | Customer Means | Attributes |
|---------|---------------|------------|
| Sales | A lead/prospect | Contact info, source, score |
| CRM | An account | Company, contacts, history |
| Billing | A payer | Payment methods, invoices, balance |
| Shipping | A recipient | Address, delivery preferences |
| Support | A ticket creator | Tickets, satisfaction, tier |

Each context has its own model of "Customer" with its own rules and lifecycles. This is why you can't have one "Customer" class across the entire enterprise.

### How to Identify Bounded Contexts in Interviews

**Technique 1: Linguistic analysis**
- Listen for different words for the same concept
- Listen for the same word meaning different things

**Technique 2: Event storming**
- Walk through business processes
- Identify where model meaning changes

**Technique 3: Team structure (Conway's Law)**
- Boundaries between teams are natural bounded context boundaries

### Interview Script

**Interviewer**: "Design a food delivery system like Uber Eats."

**You**: "Let me start by identifying the bounded contexts..."

```
1. Restaurant Management (menus, hours, availability)
2. Order Processing (cart, checkout, order lifecycle)
3. Delivery Management (drivers, routes, tracking)
4. Payment (transactions, payouts, billing)
5. Notification (order updates, push, SMS)
6. Search (restaurants, menus, recommendations)
7. Customer Management (profiles, preferences, history)
8. Operations (analytics, fraud, support)
```

---

## 5. Aggregate Design

### Aggregate Rules

1. **Consistency boundary**: All invariants are maintained within the aggregate
2. **Single transaction**: One aggregate = one transaction
3. **Reference by ID**: External aggregates are referenced by ID, not by object reference
4. **Small is better**: Large aggregates cause contention and performance issues

### Common Aggregate Mistakes

| Mistake | Example | Why It's Wrong |
|---------|---------|---------------|
| Too large | Order aggregate includes 10 years of order history | Unnecessary contention on the root |
| Too small | User and EmailAddress as separate aggregates | EmailAddress has no lifecycle without User |
| Transaction across aggregates | Creating Order and Payment in same transaction | Violates aggregate boundary |
| Database modeling | An aggregate for every database table | Aggregates are about consistency, not storage |

### Aggregate Design Decision Flow

```
1. Identify the consistency boundary
   → What invariants must always be true?
   → What operations must be atomic?

2. Choose the aggregate root
   → Which entity has the most references?
   → Which entity owns the lifecycle?

3. Define aggregate contents
   → What entities/VOs are within the boundary?
   → What entities are outside (referenced by ID)?

4. Size optimization
   → Is the aggregate too large? (contention risk)
   → Is it too small? (transaction overhead)
```

### Sample Aggregate: Order

```java
public class Order extends AggregateRoot<OrderId> {
    private OrderId id;
    private OrderStatus status;
    private List<OrderItem> items;      // Part of aggregate
    private ShippingAddress address;     // Value object, part of aggregate
    private Money total;
    private CustomerId customerId;       // Reference to another aggregate

    // Invariant: Order cannot exceed $10,000 without approval
    public void addItem(Product product, int quantity) {
        if (status != OrderStatus.PENDING)
            throw new OrderNotModifiableException(status);

        Money lineTotal = product.getPrice().multiply(quantity);
        Money newTotal = total.add(lineTotal);

        if (newTotal.compareTo(Money.of(10000)) > 0 && !isApproved())
            throw new OrderExceedsLimitException(newTotal);

        items.add(new OrderItem(product.getId(), quantity, product.getPrice()));
        total = newTotal;

        // Raise domain event
        addDomainEvent(new ItemAddedToOrder(id, product.getId(), quantity));
    }

    // Invariant: Order must have at least one item
    public void removeItem(ProductId productId) {
        if (items.size() <= 1)
            throw new OrderMustHaveAtLeastOneItemException();

        items.removeIf(item -> item.getProductId().equals(productId));
        recalculateTotal();
    }
}
```

---

## 6. Entity vs Value Object

### Deep Dive: When Modeling Domain Concepts

**Rule of thumb**: If you care about identity → Entity. If you care about attributes → Value Object.

### Interview Examples

**Question**: "Model a Book in a library system."

```java
// Entity: Book (the physical copy)
public class Book {
    private BookId id;               // ISBN or inventory ID
    private BookTitle title;         // Value Object
    private Author author;           // Entity (has identity)
    private BookCondition condition; // Value Object
    private LoanStatus status;       // Value Object
}
```

**Question**: "Model a Transaction in a financial system."

```java
// Entity: Transaction
public class Transaction {
    private TransactionId id;        // Unique transaction reference
    private Money amount;            // Value Object
    private AccountId fromAccount;   // Reference to another aggregate
    private AccountId toAccount;     // Reference to another aggregate
    private TransactionTimestamp timestamp; // Value Object
}
```

---

## 7. Domain Events

### Purpose

Domain events capture something meaningful that happened in the domain. They are named in the past tense.

### Common Domain Events

| Event | Meaning | Triggered By |
|-------|---------|-------------|
| OrderPlaced | Customer completed checkout | PlaceOrder command |
| PaymentReceived | Payment was processed | ProcessPayment command |
| OrderShipped | Items were dispatched | ShipOrder command |
| InventoryDepleted | Stock ran out | ReserveInventory command |
| CustomerVIPStatusGranted | Customer reached VIP threshold | CheckCustomerStatus event handler |

### Domain Event Structure

```java
public interface DomainEvent {
    UUID getEventId();
    String getAggregateType();
    Object getAggregateId();
    int getVersion();          // Event version for schema evolution
    Instant getOccurredOn();
}

public class OrderPlaced implements DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final String aggregateType = "Order";
    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money total;
    private final Instant occurredOn = Instant.now();
    private final int version = 1;

    @Override
    public Object getAggregateId() { return orderId; }

    @Override
    public int getVersion() { return version; }

    @Override
    public Instant getOccurredOn() { return occurredOn; }
}
```

### Using Domain Events in Interviews

**Scenario**: "How does the payment service know an order was placed?"

**Bad answer**: "The order service calls the payment service API."

**Good answer**: "The order aggregate fires an OrderPlaced domain event. The event is published to a message broker. The payment service subscribes to OrderPlaced events and processes the payment. This decouples the services and allows other services (inventory, notification, analytics) to react to the same event without changing the order service."

---

## 8. Ubiquitous Language

### What It Is

A common language shared by developers and domain experts, used in code, documentation, conversations, and models.

### Why It Matters in Interviews

Using the right language demonstrates domain understanding:

**Without ubiquitous language:**
- "The user clicks the thing and then the system saves the record"
- "We have a service that handles customer stuff"

**With ubiquitous language:**
- "The customer places an order, which triggers payment processing"
- "The Order aggregate enforces that total doesn't exceed the credit limit"

### Building Ubiquitous Language

1. Start with the domain expert's vocabulary
2. Document terms and their meanings
3. Use the terms in code (class names, method names, variable names)
4. Refine through conversation and discovery

---

## 9. DDD + Microservices: The Perfect Pair

### Why They Work Together

| DDD Concept | Maps To | Microservices Concept |
|-------------|---------|----------------------|
| Bounded Context | → | Service boundary |
| Aggregate | → | Consistency boundary (transaction scope) |
| Domain Event | → | Service-to-service communication (async) |
| Repository | → | Data ownership and persistence |
| Anti-Corruption Layer | → | Integration with external systems |
| Ubiquitous Language | → | Team communication and API design |

### The Critical Insight

> **Your microservice boundaries should be your bounded contexts.** If you split services within a bounded context, you'll have consistency problems. If you merge multiple bounded contexts into one service, you'll have coupling problems.

### Interview Application

When asked "How would you decompose this system into microservices?"

1. Start with domain analysis (event storming, domain storytelling)
2. Identify bounded contexts
3. Each bounded context = one microservice (or a few closely related ones)
4. Define context maps (how services communicate)
5. Design aggregates within each bounded context

---

## 10. Common DDD Interview Questions

### Question 1: "What is DDD and when would you use it?"

**Good answer**: "DDD is an approach to software design that focuses on modeling the business domain. I use it when the domain is complex (not technically complex, but business-logic complex). For simple CRUD applications, DDD adds unnecessary overhead. For systems with rich business rules — payment processing, insurance, healthcare, logistics — DDD provides the structure to manage complexity."

### Question 2: "How do you identify bounded contexts?"

**Good answer**: "Through event storming with domain experts. We walk through business processes and identify where terms change meaning or where different teams own different parts. Also, by analyzing organizational structure (Conway's Law) — team boundaries are natural bounded context boundaries."

### Question 3: "How do you decide aggregate boundaries?"

**Good answer**: "Aggregate boundaries are about consistency, not data modeling. Ask: What invariants must always be true? What operations need to be atomic? Start with the invariant and work outward. Keep aggregates small — if you have contention, your aggregate is too large."

### Question 4: "How do bounded contexts communicate?"

**Good answer**: "Through domain events (async) for cross-service workflows, and through APIs (sync) for queries. Each bounded context maps its events and APIs using an anti-corruption layer if needed. We use context maps to define the relationship — partnership, customer-supplier, conformist, etc."

### Question 5: "Entity vs Value Object — how do you decide?"

**Good answer**: "Two questions: Does it have an identity that persists over time? If yes, it's an entity. Is it defined solely by its attributes and immutable? If yes, it's a value object. For example, an Email is a value object — two identical emails are interchangeable. A User is an entity — changing the name doesn't change the user's identity."

### Question 6: "How does DDD relate to microservices?"

**Good answer**: "DDD bounded contexts provide the natural boundaries for microservices. Each microservice should own one bounded context. DDD aggregates define the transactional boundaries within each service. Domain events define the integration contracts between services."

### Question 7: "What is the Anti-Corruption Layer?"

**Good answer**: "An ACL is a translation layer that protects your domain model from external models. When integrating with a legacy system or third-party service that has its own model, the ACL translates between their model and yours. It prevents 'corruption' — leaking of external concepts into your pristine domain."

---

## 11. DDD Anti-Patterns in Interviews

| Anti-Pattern | What It Looks Like | Why It's Bad |
|-------------|-------------------|-------------|
| **Anemic Domain Model** | Services with all logic, entities are just data bags | Object-oriented programming becomes procedural programming |
| **Everything is an entity** | Address, Email, Money all have IDs | Unnecessary identity overhead, violates value object semantics |
| **God aggregate** | Order aggregate includes Customer, Products, Reviews | Performance issues, contention, confusing boundaries |
| **Database-driven aggregates** | Aggregate designed around database tables, not domain logic | The database schema drives the domain model, not the business rules |
| **Bounded context bleeding** | Referencing one context's entities directly in another context | Tight coupling between services |
| **No domain events** | Services call each other synchronously for everything | Tight coupling, cannot scale independently |
| **Repository for everything** | Even value objects have repositories | Repositories are for aggregates, not individual value objects |

---

## 12. Practical DDD: Code Examples

### Repository Pattern

```java
public interface OrderRepository extends Repository<Order, OrderId> {
    Optional<Order> findById(OrderId id);
    void save(Order order);
    void delete(OrderId id);
}

// Infrastructure implementation
@Component
public class JpaOrderRepository implements OrderRepository {
    private final SpringDataJpaOrderRepository jpa;

    public Optional<Order> findById(OrderId id) {
        return jpa.findById(id.getValue())
            .map(OrderMapper::toDomain);
    }

    public void save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        jpa.save(entity);
        // After save, publish domain events
        order.getDomainEvents().forEach(this::publish);
        order.clearEvents();
    }
}
```

### Domain Service

```java
// Domain service — stateless, handles logic that doesn't fit on an entity
public class PricingService implements DomainService {
    private final DiscountPolicy discountPolicy;
    private final TaxCalculator taxCalculator;

    public Money calculateTotal(Order order, Customer customer) {
        Money subtotal = order.getItemTotal();
        Money discount = discountPolicy.calculate(subtotal, customer);
        Money afterDiscount = subtotal.subtract(discount);
        Money tax = taxCalculator.calculate(afterDiscount, customer.getAddress());
        return afterDiscount.add(tax);
    }
}
```

### Factory

```java
// Factory for creating complex aggregates
@Component
public class OrderFactory implements DomainFactory {
    public Order createOrder(CustomerId customerId, Cart cart) {
        Order order = new Order(new OrderId(UUID.randomUUID()), customerId);

        for (CartItem item : cart.getItems()) {
            order.addItem(item.getProductId(), item.getQuantity(), item.getPrice());
        }

        order.setShippingAddress(cart.getShippingAddress());
        order.setPaymentMethod(cart.getPaymentMethod());
        return order;
    }
}
```

---

## 13. DDD in System Design: Complete Walkthrough

### Question: "Design a Payment Processing System"

**Step 1: Identify the domain**
- Domain: Online payment processing
- Core subdomain: Payment authorization and settlement
- Supporting: Fraud detection, dispute management
- Generic: Notification, reporting

**Step 2: Define bounded contexts**
```
Bounded Contexts:
├── Payment Context
│   ├── Handle: authorization, capture, refund, void
│   ├── Model: Payment, Transaction, AuthorizationCode, Money
│   └── Owner: Payment Team
├── Fraud Context
│   ├── Handle: risk scoring, transaction screening
│   ├── Model: RiskProfile, TransactionScore, Rule
│   └── Owner: Risk Team
├── Dispute Context
│   ├── Handle: chargebacks, arbitration
│   ├── Model: Dispute, Evidence, Case
│   └── Owner: Operations Team
└── Notification Context
    ├── Handle: email, SMS status updates
    ├── Model: Notification, Template, DeliveryStatus
    └── Owner: Platform Team
```

**Step 3: Define context maps**
```
Payment → [Customer-Supplier] → Fraud (Payment sends transactions for scoring)
Payment → [Domain Events] → Notification (PaymentProcessed triggers notification)
Payment → [Anti-Corruption Layer] → External Payment Gateway (ACL translates models)
Dispute ← [Partnership] → Payment (mutual dependency for dispute lifecycle)
```

**Step 4: Design aggregates per context**

**Payment Context Aggregates:**
```
Aggregate: Payment
  Root: Payment (entity)
  Children: Transaction (entity), Money (VO), PaymentMethod (VO)
  Invariant: Payment must have exactly one authorization before capture
  Invariant: Refund cannot exceed captured amount

Aggregate: PaymentMethod
  Root: PaymentMethod (entity)
  Children: Card (VO) or ACH (VO)
  Invariant: Payment method must be verified before use
```

**Step 5: Domain events**
```
PaymentAuthorized → triggers: notification, fraud follow-up
PaymentCaptured → triggers: settlement, accounting
PaymentRefunded → triggers: notification, reconciliation
PaymentFailed → triggers: retry, notification, investigation
```

**Step 6: Integration design**
```
              ┌──────────────────┐
              │   API Gateway    │
              └────────┬─────────┘
                       │
        ┌──────────────┼──────────────┐
        │              │              │
   ┌────▼────┐   ┌────▼────┐   ┌─────▼─────┐
   │ Payment │   │  Fraud  │   │ Dispute   │
   │ Service │   │ Service │   │ Service   │
   └────┬────┘   └────┬────┘   └─────┬─────┘
        │              │              │
   ┌────┴──────────────┴──────────────┴────┐
   │           Event Bus (Kafka)           │
   │  PaymentAuthorized, PaymentCaptured,  │
   │  FraudAlertGenerated, DisputeOpened   │
   └────────────────┬─────────────────────┘
                    │
              ┌─────▼─────┐
              │Notification│
              │  Service   │
              └───────────┘
```

---

*DDD is not just about code patterns — it's a strategic approach to managing complexity by centering the model around the business domain. In interviews, demonstrate this strategic thinking, not just tactical patterns.*
