# Messaging - ARCHITECTURE

## Event-Driven Microservices Architecture

```
┌────────────────────────────────────────────────────────────┐
│                    API Gateway                              │
└─────────────────┬──────────────────────────────────────────┘
                  │ (HTTP/REST)
                  ▼
┌────────────────────────────────────────────────────────────┐
│                    Services                                 │
│                                                             │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐          │
│  │ Order      │  │ Payment    │  │ Inventory  │          │
│  │ Service    │  │ Service    │  │ Service    │          │
│  └──────┬─────┘  └──────┬─────┘  └──────┬─────┘          │
│         │               │               │                │
│    Produce Events   Consume Events   Consume Events      │
└─────────┼───────────────┼───────────────┼────────────────┘
          │               │               │
          ▼               ▼               ▼
┌────────────────────────────────────────────────────────────┐
│                    Message Layer                            │
│                                                             │
│  ┌───────────────────────────────────────────────────┐    │
│  │              Kafka Cluster                         │    │
│  │                                                   │    │
│  │  Topics:                                          │    │
│  │  ┌──────────────┐  ┌──────────────┐              │    │
│  │  │ order-events │  │payment-events│              │    │
│  │  │ - created    │  │ - processed  │              │    │
│  │  │ - shipped   │  │ - failed     │              │    │
│  │  │ - delivered │  │ - refunded   │              │    │
│  │  └──────────────┘  └──────────────┘              │    │
│  │                                                   │    │
│  │  ┌──────────────┐  ┌──────────────────┐          │    │
│  │  │inventory-evt  │  │ notification-evt │          │    │
│  │  │ - reserved   │  │ - email         │          │    │
│  │  │ - released   │  │ - sms           │          │    │
│  │  └──────────────┘  └──────────────────┘          │    │
│  └───────────────────────────────────────────────────┘    │
└────────────────────────────────────────────────────────────┘
          │               │               │
          ▼               ▼               ▼
┌────────────────────────────────────────────────────────────┐
│                    Event Consumers                          │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐          │
│  │ Analytics  │  │ Audit Log  │  │ Search     │          │
│  │ Service    │  │ Service    │  │ Index      │          │
│  └────────────┘  └────────────┘  └────────────┘          │
└────────────────────────────────────────────────────────────┘
```

## Data Flow: Order Placement

```
1. User → API Gateway → POST /orders
2. Order Service: Save order, emit "order.created" event
3. Payment Service: Consume event, process payment, emit "payment.processed"
4. Inventory Service: Consume event, reserve items, emit "inventory.reserved"
5. Notification Service: Consume events, send email/SMS
6. Analytics Service: Consume events, update metrics
```

## Kafka Cluster Topology

```
┌──────────────────────────────────────────────┐
│              Kafka Cluster                    │
│                                              │
│  ┌────────────┐  ┌────────────┐  ┌─────────┐│
│  │ ZooKeeper  │  │ ZooKeeper  │  │ZK       ││
│  │ (Quorum)   │  │            │  │(Leader) ││
│  └────────────┘  └────────────┘  └─────────┘│
│                                              │
│  ┌────────────┐  ┌────────────┐  ┌─────────┐│
│  │ Broker 1   │  │ Broker 2   │  │Broker 3 ││
│  │ (Leader)   │  │ (Follower) │  │(Follower)││
│  └────────────┘  └────────────┘  └─────────┘│
│     │               │               │       │
│     └───────────────┼───────────────┘       │
│                     │                        │
│              ┌──────▼──────┐                │
│              │  Schema     │                 │
│              │  Registry   │                 │
│              └─────────────┘                │
└──────────────────────────────────────────────┘
```

## Event Schema Governance

| Event | Schema Version | Compatibility | Fields |
|-------|--------------|--------------|--------|
| OrderCreated | v1 | N/A | id, customerId, total |
| OrderCreated | v2 | BACKWARD | +status, +items[] |
| OrderCreated | v3 | FORWARD | +discountCode (optional) |
