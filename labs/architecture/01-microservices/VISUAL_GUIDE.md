# Visual Guide to Microservices

## High-Level Architecture
```
                    +----------------+
                    |  API Gateway   |
                    | (Spring Cloud  |
                    |   Gateway)     |
                    +-------+--------+
                            |
          +-----------------+------------------+
          |                 |                  |
+---------v--------+ +-----v--------+ +------v---------+
|  Order Service   | | Payment      | | Inventory       |
|  Port: 8081      | | Service      | | Service         |
|  DB: orders_db   | | Port: 8082   | | Port: 8083      |
+------------------+ | DB: pays_db  | | DB: inv_db      |
                     +--------------+ +-----------------+
          |                 |                  |
          +-----------------+------------------+
                            |
                    +-------v--------+
                    |  Message Queue |
                    |   (Kafka)      |
                    +----------------+
```

## Request Flow Sequence
```
Client     Gateway    OrderSvc    PaymentSvc    InvSvc     Kafka
  |           |           |            |           |          |
  |--POST---->|           |            |           |          |
  |           |--route--->|            |           |          |
  |           |           |--check---->|           |          |
  |           |           |<--ok-------|           |          |
  |           |           |--reserve-->|           |          |
  |           |           |<--reserved-|           |          |
  |           |           |--process-->|           |          |
  |           |           |                    |   |          |
  |           |           |--publish---------->|---------->  |
  |           |<--resp----|           |           |          |
  |<--200-----|           |            |           |          |
```

## Circuit Breaker States Diagram
```
CLOSED ---> OPEN ---> HALF_OPEN ---> CLOSED
  ^                      |              |
  |                      |              |
  +----------------------+--------------+
     (timeout expires)     (successful test)
```
