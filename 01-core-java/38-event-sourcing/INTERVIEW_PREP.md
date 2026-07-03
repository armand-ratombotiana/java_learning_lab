# Module 38: Event Sourcing & CQRS - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the primary difference between a traditional CRUD architecture and Event Sourcing?
**Answer**:
- **CRUD (Create, Read, Update, Delete)**: Stores the *current state* of an entity. When an entity is updated, the old state is overwritten and lost forever (unless explicit auditing tables are maintained).
- **Event Sourcing**: Does not store the current state. Instead, it stores a sequence of immutable, state-changing events in an append-only log (e.g., `OrderPlaced`, `ItemAdded`, `ShippingAddressChanged`). The current state of the entity is derived by replaying these events from the beginning of time. This provides a 100% accurate, undeniable audit trail and allows for "time travel" (viewing the state of the system at any given point in the past).

### Q2: Why is CQRS almost always used in conjunction with Event Sourcing?
**Answer**:
Event Stores are extremely fast for appending new events, and efficient for retrieving all events for a *single specific entity* (to replay its state). However, they are completely incapable of answering complex queries (e.g., "Find all users over the age of 30 who bought a laptop last week"). 
To solve this, CQRS (Command Query Responsibility Segregation) is used. The Command side handles the business logic and appends events to the Event Store. The Query side listens to those events and builds "Read Models" (materialized views) in a separate, query-optimized database (like Elasticsearch or PostgreSQL) to serve UI requests efficiently.

### Q3: How do you handle schema evolution (changing the shape of an event) in Event Sourcing?
**Answer**:
Because the Event Store is immutable, you cannot go back and rewrite old events to match a new schema.
Instead, you handle evolution during deserialization using **Upcasting**. When the system reads an old "V1" event from the database, an Upcaster acts as a middleware that transforms the old V1 JSON payload into the new V2 format in-memory before passing it to the application logic. The underlying data in the Event Store remains untouched.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Compensating Transactions
**Problem**: An interviewer poses this scenario: In a traditional database, if a user accidentally cancels an order, an admin can just run a SQL `UPDATE orders SET status = 'ACTIVE' WHERE id = 1` to fix it. How do you fix a user mistake in an Event Sourced system where the Event Store is strictly append-only?

**Solution**:
Because the Event Store represents *what actually happened in the real world*, you cannot delete or modify the `OrderCancelledEvent`.
Instead, you must issue a **Compensating Event**. The admin would trigger a command that appends an `OrderReinstatedEvent` to the log. When the system replays the history (`OrderPlaced` -> `OrderCancelled` -> `OrderReinstated`), the final derived state correctly calculates the order as ACTIVE, while preserving the true historical fact that it was temporarily cancelled.

### Scenario 2: Rebuilding Projections
**Problem**: You have a CQRS architecture. Your marketing team asks for a new dashboard showing the "Total revenue per day" for the last 5 years. This read model does not currently exist. How do you fulfill this requirement?

**Solution**:
This highlights the superpower of Event Sourcing. 
1. You create a new Read Model (e.g., a SQL table `daily_revenue`).
2. You write a Projection script that listens for `ItemPurchasedEvent`s and updates the table.
3. Because the Event Store holds the entire history of the company, you simply attach your new Projection to the beginning of the Event Log and perform an **Event Replay**. The projection will rapidly process the last 5 years of historical events, perfectly building the new read model from scratch. Once caught up, it continues to listen for live events.