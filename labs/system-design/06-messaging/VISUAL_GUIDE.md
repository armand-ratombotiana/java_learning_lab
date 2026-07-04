# Messaging - VISUAL GUIDE

## Messaging Topology

```
                    ┌─────────────┐
                    │  Producer   │
                    │  (Service A)│
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  Message    │
                    │  Broker     │
                    └──┬──────┬──┘
                       │      │
                 ┌─────▼─┐  ┌─▼──────┐
                 │Queue 1│  │Topic A │
                 └───┬───┘  └───┬────┘
                     │          │
              ┌──────▼──┐  ┌────▼───┐ ┌───────┐
              │Consumer  │  │Consumer│ │Consumer│
              │(Service B)│ │(Svc C) │ │(Svc D) │
              └──────────┘  └────────┘ └───────┘
```

## Kafka Architecture

```
┌──────────────────────────────────────────────────────────┐
│                        Kafka Cluster                      │
│                                                           │
│  Topic: "orders"                                          │
│  ┌─────────────────────────────────────────────────┐    │
│  │ Partition 0                        Partition 1   │    │
│  │ [msg0][msg1][msg2][msg3]          [msg0][msg1]   │    │
│  │ Offset: 0,1,2,3                   Offset: 0,1    │    │
│  │ Leader: Broker 1                  Leader: Broker 2│    │
│  │ Replicas: [1,2,3]                 Replicas: [2,3,1]│  │
│  └─────────────────────────────────────────────────┘    │
│                                                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ Broker 1 │  │ Broker 2 │  │ Broker 3 │              │
│  │ (Ctrl)   │  │          │  │          │              │
│  └──────────┘  └──────────┘  └──────────┘              │
└──────────────────────────────────────────────────────────┘
```

## Consumer Group Rebalancing

```
Before Rebalance:
Consumer A: [P0, P1]
Consumer B: [P2, P3]
Consumer C: [P4, P5]

Consumer C fails:

During Rebalance:
Consumer A: [P0, P1] (frozen)
Consumer B: [P2, P3] (frozen)
Consumer C: OFFLINE

After Rebalance:
Consumer A: [P0, P1, P4]
Consumer B: [P2, P3, P5]
```

## Dead Letter Queue Flow

```
Normal Queue
┌──────────────────┐
│ [msg1][msg2][msg3]│
└──┬───┬───┬───────┘
   │   │   │
   ▼   ▼   ▼
Consumer (process)
   │   │   │
   │   │   └── success → commit offset
   │   │
   │   └────── failure → retry (x3)
   │
   └──────────── retries exhausted → DLQ

Dead Letter Queue
┌────────────────────┐
│ [msg2_dlq][msg4...]│
└──────┬─────────────┘
       ▼
Manual inspection / alert
```
