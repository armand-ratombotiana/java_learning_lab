# Module 38: Event Sourcing & CQRS - Quizzes

---

## Q1: Event Sourcing vs Traditional Databases
What is the core difference between Event Sourcing and traditional CRUD database operations?

A) Event Sourcing is only used for caching.
B) Traditional CRUD stores the *current state*, while Event Sourcing stores a complete sequence of immutable *state-changing events*.
C) Event Sourcing allows editing past data faster.
D) Traditional CRUD does not use SQL.

**Answer**: B
**Explanation**: Event sourcing derives the current state of an entity by replaying the sequence of immutable events that have occurred since its creation, rather than storing just the final snapshot.

---

## Q2: CQRS
In a CQRS architecture, why is the read model often separated from the command model?

A) To save hard drive space.
B) Because the command model (Event Store) is highly inefficient for complex querying, filtering, and aggregation.
C) To allow the application to run without an internet connection.
D) Because Java requires interfaces to be separated.

**Answer**: B
**Explanation**: The Event Store is great for fast appends, but terrible for queries like "Give me all users who bought product X." CQRS solves this by projecting events into a read-optimized database.

---

## Q3: Snapshots
What problem does "Snapshotting" solve in an Event Sourced system?

A) It prevents hackers from stealing data.
B) It allows you to delete all past events to save disk space.
C) It speeds up the process of loading the current state of an entity that has thousands of past events.
D) It synchronizes the database with the UI.

**Answer**: C
**Explanation**: Replaying a long history of events (e.g., a bank account with 10 years of transactions) every time the entity is loaded is slow. A snapshot saves a materialized state at a specific point in time, so the system only replays events that happened after the snapshot.