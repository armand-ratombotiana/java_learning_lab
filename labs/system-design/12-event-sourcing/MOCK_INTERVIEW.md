# Mock Interview: Event Sourcing

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Platform Architect Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design an event sourcing system for a banking application.

---

## Transcript

**Interviewer**: "We're building a banking ledger. Every account operation (deposit, withdraw, transfer) needs to be recorded immutably. We need full audit trail and the ability to reconstruct account state at any point in time."

**Candidate**: "This is the perfect use case for Event Sourcing. Instead of storing current balance, we store the sequence of events that led to that balance. The current state is a projection of the event log."

**Interviewer**: "Define the event schema."

**Candidate**: "Each event has: event_id (UUID), aggregate_id (account_id), event_type (Deposited, Withdrawn, Transferred), event_data (JSON: amount, currency, reference), timestamp, version (monotonic per aggregate). Events are append-only — they're never modified or deleted."

**Interviewer**: "How do you store events?"

**Candidate**: "Event store database. For each aggregate (account), events are stored in order. I'd use PostgreSQL with a table: `events(aggregate_id, version, event_type, data, created_at)` with a primary key on `(aggregate_id, version)`. The version ensures optimistic concurrency — no two events can have the same version for the same aggregate."

**Interviewer**: "How do you read the current balance?"

**Candidate**: "Projection. We maintain a `account_balances` table that is updated by an event handler: when a Deposited event is processed, add to balance; Withdrawn, subtract. This is the CQRS pattern — write model (events) and read model (projections) are separate. The projection can be rebuilt from scratch by replaying all events."

**Interviewer**: "What about performance — replaying millions of events?"

**Candidate**: "Snapshots. Every 1000 events (configurable), we snapshot the aggregate state. State = balance at that point. To rebuild: load the latest snapshot, then replay events from the snapshot version onward. This keeps recovery fast. Snapshots are stored alongside events."

**Interviewer**: "How do you handle concurrent operations on the same account?"

**Candidate**: "Optimistic concurrency. When appending an event, the database checks: `INSERT INTO events (aggregate_id, version, ...) VALUES (?, ?, ...)`. The primary key `(aggregate_id, version)` prevents two concurrent writes with the same version. The second writer gets a unique constraint violation and must retry (re-read events, recompute state, try again with latest version)."

**Interviewer**: "What if I need to know the balance at a specific date?"

**Candidate**: "Load the snapshot before that date, then replay events from the snapshot version to the target date. For repeated queries, create a time-specific projection (e.g., end-of-day balances table populated by a scheduled job)."

**Interviewer**: "How does event sourcing help with debugging?"

**Candidate**: "You can replay the exact sequence of events in a development environment to reproduce issues. You can also time-travel: inspect the state of an account at any point in the past. This is invaluable for auditing and dispute resolution."

---

## Key Takeaways

- **Append-only event log**: Immutable record of all state changes
- **CQRS**: Separate write model (events) from read model (projections)
- **Snapshots**: Periodic state snapshots for fast recovery
- **Optimistic concurrency**: Version-based conflict detection
- **Time travel**: Reconstruct state at any historical point
- **Full audit trail**: Every change is recorded and attributable
