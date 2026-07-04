# Visual Guide to Saga Pattern

## Choreography vs Orchestration

### Choreography
```
                    Event Flow (No Central Coordinator)
                            
   [Order Service] --(OrderCreated)--> [Inventory Svc]
        ^                                      |
        |                            (InventoryReserved)
        |                                      |
        +-------(OrderConfirmed)------+        |
                                         v
                                  [Payment Svc]

Pros: Decentralized, simple
Cons: Hard to trace, circular dependencies
```

### Orchestration
```
                    +------------------+
                    |   Orchestrator    |
                    | (Saga Manager)    |
                    +--------+---------+
                             |
           +-----------------+------------------+
           |                 |                  |
    +------v----+    +------v----+    +--------v------+
    |  Order    |    | Inventory  |    |    Payment    |
    |  Service  |    |  Service   |    |   Service     |
    +-----------+    +-----------+    +--------------+

Pros: Centralized control, traceable
Cons: Single point of coordination
```

## Saga State Machine
```
                         +-----------+
                         | INITIAL   |
                         +-----+-----+
                               |
                         +-----v-----+
                  +------+ RESERVING +------+
                  |      | INVENTORY |      |
                  |      +-----------+      |
                  | Success             Failure
                  v                         v
          +-------+------+        +--------+-------+
          |  PROCESSING   |        |  RELEASING     |
          |   PAYMENT     |        |  INVENTORY     |
          +-------+------+        +--------+-------+
                  |                         |
             Success                    Complete
                  |                         |
            +-----v------+          +-------v------+
            | CONFIRMING |          | ORDER        |
            |  ORDER     |          | CANCELLED    |
            +-----+------+          +--------------+
                  |
            +-----v------+
            | COMPLETED  |
            +------------+
```

## Error Flow with Compensation
```
Step 1: Reserve Inventory ✓
Step 2: Process Payment ✓
Step 3: Update Loyalty Points ✗ FAILED

Compensation:
1. Process Payment → REFUND
2. Reserve Inventory → RELEASE
```
