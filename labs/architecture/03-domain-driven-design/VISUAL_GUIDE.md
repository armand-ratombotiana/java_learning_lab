# Visual Guide to DDD

## Layered Architecture in DDD
```
+--------------------------------------------------+
|              User Interface / API                  |
+--------------------------------------------------+
|           Application Layer (Services)             |
|   - Orchestrates domain objects                   |
|   - Transactions, security, DTOs                  |
+--------------------------------------------------+
|              Domain Layer (Model)                  |
|   +----------+  +----------+  +----------------+ |
|   | Entity   |  | Value    |  | Aggregate Root | |
|   | Identity |  | Object   |  | Consistency    | |
|   +----------+  +----------+  +----------------+ |
|   +----------+  +----------+  +----------------+ |
|   | Domain   |  | Domain   |  | Repository     | |
|   | Event    |  | Service  |  | (Interface)    | |
|   +----------+  +----------+  +----------------+ |
+--------------------------------------------------+
|           Infrastructure Layer                     |
|   - Persistence, messaging, external APIs         |
+--------------------------------------------------+
```

## Bounded Context Map Example
```
+------------------+     +------------------+
|  Order Context   |---->|   Invoice Context |
|  - Order         |     |  - Invoice        |
|  - LineItem      |     |  - Payment        |
|  - Customer      |<----|  - Money          |
+------------------+     +------------------+
        |                        |
        | Shared Kernel          | Customer-Supplier
        v                        v
+------------------+     +------------------+
|  Catalog Context |     |  Shipping Context |
|  - Product       |     |  - Parcel         |
|  - Category      |     |  - Tracking       |
|  - Price         |     |  - Address        |
+------------------+     +------------------+
```

## Aggregate Consistency Boundary
```
         +----------------------------+
         |      Aggregate: Order       |
         |  +----------------------+   |
         |  | Order (Root Entity)  |   |
         |  | - orderId (identity) |   |
         |  | - status, total      |   |
         |  | - addItem()          |   |
         |  | - submit()           |   |
         |  | - cancel()           |   |
         |  +----------------------+   |
         |        | contains           |
         |  +----------------------+   |
         |  | LineItem (Entity)    |   |
         |  | - productId, qty    |   |
         |  | - price             |   |
         |  +----------------------+   |
         |        | references          |
         |  +----------------------+   |
         |  | Address (Value Obj)  |   |
         |  | - street, city, zip |   |
         |  +----------------------+   |
         +----------------------------+
         Only accessed via Order (Root)
```
