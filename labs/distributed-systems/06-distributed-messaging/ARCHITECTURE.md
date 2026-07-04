# Distributed Messaging: Architecture

## Event-Driven Architecture

```
┌────────────┐    ┌────────────┐    ┌────────────┐
│  Service A │    │  Service B │    │  Service C │
│  (Producer)│    │ (Consumer) │    │ (Consumer) │
└─────┬──────┘    └─────┬──────┘    └─────┬──────┘
      │                 │                 │
      └────────┬────────┘─────────────────┘
               │
        ┌──────▼──────┐
        │   Kafka     │
        │   Cluster   │
        │ (3 brokers) │
        └─────────────┘
```

## CQRS with Event Bus

```
Command Side                    Query Side
┌──────────────┐              ┌──────────────┐
│ Write Model  │ ──Event──▶   │ Read Model   │
│              │              │              │
│ validates    │   Event Bus  │ denormalized │
│ aggregates   │   (Kafka)   │ projections  │
└──────────────┘              └──────────────┘
```
