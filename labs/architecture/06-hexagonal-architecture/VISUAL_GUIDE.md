# Visual Guide to Hexagonal Architecture

## Structure Diagram
```
                    Driving Side                  Driven Side
                    (Inbound)                     (Outbound)

                    +----------+                  +----------+
                    | REST API |                  | Database |
                    +----+-----+                  +----+-----+
                         |                             |
                    +----v-----+                  +----v-----+
                    | Controller|                 | Repository|
                    | (Adapter) |                 | (Adapter) |
                    +----+-----+                  +----+-----+
                         |                             |
                    +----v-----+                  +----v-----+
          +---------+  Port     |                  |  Port     +---------+
          |         | (Inbound) |                  | (Outbound)|         |
          |         +----+------+                  +----+------+         |
          |              |                             |                 |
          |    +---------v--------+         +----------v--------+       |
          |    |   Domain Core    |         |   Domain Core     |       |
          |    |   (Use Cases)    |         |   (Ports)         |       |
          |    +------------------+         +-------------------+       |
          |                                                             |
          +-------------------+-------------------+--------------------+
                              |                   |
                    +---------v--+        +-------v-------+
                    | Messaging  |        | External API  |
                    | (Adapter)  |        | (Client Adap) |
                    +------------+        +---------------+
```

## Dependency Direction
```
[Adapters] -> [Ports] <- [Domain Core] -> [Ports] <- [Adapters]
    |            |                          |           |
    |            +--- Inbound Port ---------+           |
    +--- Driving Adapter                               |
                                                        |
    +--- Driven Adapter                                 |
    |            +--- Outbound Port ---------+          |
    |            |                          |           |
    v            v                          v           v
External                            External
Input                               Output
```

## Layer Access Rules
```
Domain Layer:
- Defines ports (interfaces)
- Contains business logic
- Zero external framework dependencies
- Depends only on Java standard library

Adapter Layer:
- Implements ports
- Translates between domain and external formats
- Depends on frameworks and domain layer
```
