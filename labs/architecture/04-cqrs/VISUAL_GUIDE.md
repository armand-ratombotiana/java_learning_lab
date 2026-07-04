# Visual Guide to CQRS

## Architecture Diagram
```
                    +-------------------+
                    |    Client App      |
                    +--------+----------+
                             |
              +--------------+--------------+
              |                             |
       +------v----+                +-------v-------+
       |   Command   |              |    Query Bus   |
       |    Bus      |              |                |
       +------+------+              +-------+--------+
              |                             |
        +-----v-----+               +-------v--------+
        |  Command   |               |    Query       |
        |  Handler   |               |    Handler     |
        +-----+------+               +-------+--------+
              |                             |
        +-----v-----+               +-------v--------+
        |   Write    |     Event     |     Read       |
        |   Model    |-------------->|     Model      |
        |  (Domain)  |     Store     |  (Projection)  |
        +------------+               +----------------+
              |
              v
        +------------+
        | Event Store|  (Optional for ES)
        +------------+
```

## Command/Query Separation
```
Commands (Write)              Queries (Read)
-----------                   ------------
PlaceOrder                    GetOrder
ProcessPayment                GetPaymentHistory
UpdateCustomer                SearchCustomers
CancelOrder                   GetOrderStatus
ShipOrder                     GetDashboardMetrics

Command: Mutating, Validated  Query: Read-only, Optimized
Command: Returns result only  Query: Returns data
Command: Can fail             Query: Never modifies state
```

## Event to Read Model Pipeline
```
Write Side:
[Command] -> [Domain Logic] -> [Event]

Read Side Projection:
[Event] -> [Projector] -> [Read Model Database]

Example flow:
OrderCreated -> OrderProjector -> MongoDB OrderView

Read Model Types:
- OrderSummaryView (for dashboard)
- OrderDetailView (for detail page)
- CustomerOrderListView (for customer portal)
```
