# Architecture Patterns - ARCHITECTURE

## System Context Diagram

```
┌─────────────────────────────────────────────────────┐
│              E-Commerce System                        │
│                                                       │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐         │
│  │ Web UI   │   │ Mobile   │   │  Admin   │         │
│  └────┬─────┘   └────┬─────┘   └────┬─────┘         │
│       │              │              │               │
│  ┌────▼──────────────▼──────────────▼─────┐         │
│  │          API Gateway                    │         │
│  │  (Auth, Rate Limit, Routing)           │         │
│  └────────────────┬───────────────────────┘         │
│                   │                                  │
│  ┌────────────────▼───────────────────────┐         │
│  │          Service Mesh (Istio)          │         │
│  └────┬─────────┬─────────┬──────────────┘         │
│       │         │         │                        │
│  ┌────▼───┐ ┌──▼────┐ ┌──▼──────┐                 │
│  │Product │ │Order  │ │Payment  │                 │
│  │Service │ │Service│ │Service  │                 │
│  └───┬────┘ └───┬───┘ └───┬─────┘                 │
│      │          │         │                        │
│  ┌───▼────┐ ┌───▼───┐ ┌───▼──────┐                │
│  │  DB    │ │  DB   │ │  DB      │                │
│  └────────┘ └───────┘ └──────────┘                │
│       │          │         │                        │
│       └──────────┼─────────┘                        │
│                  ▼                                  │
│          ┌───────────────┐                          │
│          │  Event Broker │                          │
│          │   (Kafka)     │                          │
│          └───────────────┘                          │
│                  │                                  │
│          ┌───────┴────────┐                        │
│          │  Notification  │                         │
│          │  Service       │                         │
│          └────────────────┘                         │
└─────────────────────────────────────────────────────┘
```

## Deployment Architecture

```
┌────────────────────── Kubernetes Cluster ─────────────────────┐
│                                                               │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐        │
│  │Product  │  │ Order   │  │Payment  │  │Kafka    │        │
│  │Pod x3   │  │ Pod x5  │  │ Pod x2  │  │Pod x3   │        │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘        │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐                       │
│  │Gateway  │  │Registry │  │Config   │                       │
│  │Pod x2   │  │ Pod x1  │  │ Pod x1  │                       │
│  └─────────┘  └─────────┘  └─────────┘                       │
└──────────────────────────────────────────────────────────────┘
```

## Data Architecture

| Service | Database | Data Pattern |
|---------|----------|-------------|
| Product | PostgreSQL | CRUD with CQRS read model |
| Order | PostgreSQL + Kafka | Event Sourcing |
| Payment | MongoDB | Event-Driven |
| Notification | Redis | Pub/Sub |
