# Visual Guide to Event Sourcing

## Event Store Flow
```
Command: Deposit $100
                |
                v
+---------------------------------------------+
|              Event Store                     |
|  +---------+  +---------+  +----------+     |
|  | Event 1 |  | Event 2 |  | Event 3  | ... |
|  | Created |  | Deposit |  | Withdraw |     |
|  | $0      |  | $100    |  | $50      |     |
|  | v1      |  | v2      |  | v3       |     |
|  +---------+  +---------+  +----------+     |
+---------------------------------------------+
                |
                v
        Current State: $50
        (Replay all events)
```

## Rebuild from Events
```
Aggregate: Account-123
Events:
  [v1] AccountCreated(owner="Alice", initialBalance=$0)
  [v2] MoneyDeposited(amount=$500)
  [v3] MoneyWithdrawn(amount=$200)
  [v4] MoneyDeposited(amount=$100)
  
Rebuild:
  Start: balance = $0
  v1 -> balance = $0
  v2 -> balance = $500
  v3 -> balance = $300
  v4 -> balance = $400 (Current)
```

## Snapshot Optimization
```
Without Snapshot:
  [Event 1]---[Event 2]---...---[Event 100]---[Event 101]
  |________ 100 events to replay ________|

With Snapshot:
  [Snapshot @v100]---[Event 101]
  |__ Load saved state __|__ 1 event __|

Performance: Snapshot + 1 event vs 101 events
```

## Event vs Current State
```
Traditional CRUD:              Event Sourcing:
+-------------------+         +-------------------+
| Customer          |         | Events:           |
| id: 123           |         | 1: Created(name)  |
| name: "Alice"     |         | 2: NameChanged(Bob)|
| balance: $400     |         | 3: Deposit($500)  |
+-------------------+         | 4: Withdraw($100) |
                               +-------------------+
Lost history:                    Full history:
When was name changed?           Every change recorded
What was previous balance?       Can query any point in time
```
