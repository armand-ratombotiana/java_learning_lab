# Module 38: Event Sourcing & CQRS - Edge Cases & Pitfalls

---

## Pitfall 1: Ignoring Eventual Consistency

### ❌ Wrong
Building a UI that issues a Command (e.g., "Create Account") and immediately queries the Read Model (e.g., "Get Account Details") expecting the data to be there. CQRS systems are naturally eventually consistent. The read model might take a few milliseconds to catch up.

### ✅ Correct
Design the UI to handle eventual consistency. For example, use WebSockets to push a notification to the UI when the read model has been updated, or design the UI optimistically so it immediately shows the user's action as successful while the backend processes it.

---

## Pitfall 2: Modifying Historical Events

### ❌ Wrong
Discovering a bug in an event that was emitted weeks ago, and running a database script to modify the event payload in the Event Store.

### ✅ Correct
The Event Store is an immutable, append-only log. It represents *what actually happened*. To fix a mistake, you must emit a **Compensating Event** (e.g., `OrderCorrected` or `ChargeReversed`). Never alter historical events.

---

## Pitfall 3: Event Schema Evolution

### ❌ Wrong
Changing the structure of a Java event class (e.g., renaming a field or removing a field) without versioning. When the system attempts to replay old events from the Event Store, it will fail to deserialize them into the new Java class structure.

### ✅ Correct
Use event versioning or Upcasters. When an event is deserialized, an Upcaster transforms the old V1 JSON payload into the new V2 structure before it reaches the domain logic.