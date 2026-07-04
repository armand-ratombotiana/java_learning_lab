# Visual Guide to Event-Driven Architecture

## Architecture Overview
```
                     +------------------+
                     |   Event Broker   |
                     |   (Kafka/Rabbit) |
                     +--------+---------+
                              |
               +--------------+--------------+
               |              |              |
        +------v----+  +-----v------+  +----v-------+
        | Producer A |  | Producer B |  | Producer C |
        | Order Svc  |  | User Svc   |  | Payment Svc|
        +-----------+  +------------+  +------------+
               |              |              |
               +--------------+--------------+
                              |
                     +--------v---------+
                     |   Event Stream   |
                     |   (order-events) |
                     +--------+---------+
                              |
               +--------------+--------------+
               |              |              |
        +------v----+  +-----v------+  +----v-------+
        | Consumer A |  | Consumer B |  | Consumer C |
        | Inventory  |  | Notificat. |  | Analytics  |
        +-----------+  +------------+  +------------+
```

## Event Flow Sequence
```
OrderSvc        Kafka        Inventory      Notification    Analytics
   |              |              |              |              |
   |--Publish---->|              |              |              |
   |  OrderEvent  |              |              |              |
   |              |--Consume---->|              |              |
   |              |  (partition0)|              |              |
   |              |--Consume------------------>|              |
   |              |  (partition1)|              |              |
   |              |--Consume------------------------------->  |
   |              |  (partition2)|              |              |
```

## Dead Letter Queue Flow
```
Normal Flow:
Consumer -> Process -> Success (ACK)

Failure Flow:
Consumer -> Process -> Failure -> Retry (3x) -> DLQ
```
