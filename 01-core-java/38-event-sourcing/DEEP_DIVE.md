# Module 38: Event Sourcing & CQRS - Deep Dive

**Difficulty Level**: Expert  
**Prerequisites**: Modules 01-37 (especially Microservices and System Design)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [What is Event Sourcing?](#whatis)
2. [The Event Store](#event-store)
3. [Command Query Responsibility Segregation (CQRS)](#cqrs)
4. [Projections and Read Models](#projections)
5. [Event Replay and Snapshots](#snapshots)

---

## 1. What is Event Sourcing? <a name="whatis"></a>
Traditionally, databases store the *current state* of an entity. Event Sourcing changes this by storing every *state-changing event* that occurs to an entity as an immutable sequence. The current state is derived by replaying all the events from the beginning.

Example: Instead of storing `Balance: $50`, we store `[Opened(0), Deposited(100), Withdrew(50)]`.

---

## 2. The Event Store <a name="event-store"></a>
The Event Store is a database specifically optimized to append events to the end of a log. Events are immutable and can only be appended, never updated or deleted (append-only log). Examples include EventStoreDB, Apache Kafka, or a customized relational database table.

---

## 3. Command Query Responsibility Segregation (CQRS) <a name="cqrs"></a>
CQRS separates the model used to update data (Command) from the model used to read data (Query). 
- **Command Side**: Validates business rules, handles state changes, and appends new events to the Event Store.
- **Query Side**: Subscribes to events from the Event Store and updates "Read Models" (materialized views) optimized for fast querying.

*Event Sourcing and CQRS are often used together because querying an Event Store directly for complex aggregations is highly inefficient.*

---

## 4. Projections and Read Models <a name="projections"></a>
A Projection is a piece of code that listens to events and updates a Read Model. Since the Read Model is separate from the Command side, it can be stored in a completely different type of database (e.g., Command in EventStoreDB, Read Model in Elasticsearch or MongoDB).

---

## 5. Event Replay and Snapshots <a name="snapshots"></a>
- **Event Replay**: Because the Event Store contains the entire history of the system, you can completely rebuild Read Models from scratch, or build *new* Read Models, simply by replaying the events.
- **Snapshots**: If an entity has thousands of events, replaying them every time to get the current state is slow. A snapshot saves the state of the entity at a specific version (e.g., every 100 events), so future reads only need to load the snapshot and replay the events that occurred *after* it.