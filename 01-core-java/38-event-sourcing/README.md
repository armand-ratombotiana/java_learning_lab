# 38 - Event Sourcing

An architectural pattern where state changes are stored as a sequence of events, providing complete audit trail and enabling powerful features like time-travel debugging.

## Overview

- **Topic**: Event Sourcing Pattern
- **Prerequisites**: Java, design patterns, clean architecture
- **Duration**: 2-3 hours

## Key Concepts

- Event store and event log
- Aggregate replay
- Snapshotting
- Event projections
- CQRS (Command Query Responsibility Segregation)
- Optimistic concurrency control

## Getting Started

Run the training code:
```bash
cd 38-event-sourcing
mvn compile exec:java -Dexec.mainClass=com.learning.eventsourcing.Lab
```

## Module Contents

- Event store implementation
- Aggregate replay from events
- Snapshot strategy
- Building projections from events
- Concurrency handling