# Visual Guide to Six-Port Architecture

## Port Types Diagram
```
                    +------------------------+
                    |   Six-Port Architecture |
                    +------------------------+
                               |
         +--------------------+--------------------+
         |                    |                    |
+--------v--------+  +-------v--------+  +--------v--------+
| Inbound Driving |  | Inbound Event  |  | Inbound Driven  |
| Port (REST)     |  | Port (Consumer)|  | Port (Internal) |
+--------+--------+  +-------+--------+  +--------+--------+
         |                    |                    |
         +--------------------+--------------------+
                              |
                     +--------v--------+
                     |    Domain Core   |
                     +--------+--------+
                              |
         +--------------------+--------------------+
         |                    |                    |
+--------v--------+  +-------v--------+  +--------v--------+
| Outbound Driven |  | Outbound Driving|  | Outbound Event  |
| Port (DB)       |  | Port (External) |  | Port (Publisher)|
+-----------------+  +-----------------+  +-----------------+
```

## Six Ports and Their Adapters
```
Port Type              Interface Suffix     Adapter Examples
---------------------  -----------------    ---------------------
Inbound Driving Port   Port (e.g., OrderPort)       REST Controller
Outbound Driven Port   Port (e.g., OrderRepoPort)   JPA Repository
Outbound Driving Port  Port (e.g., PaymentPort)     Stripe Client
Inbound Event Port     EventPort (e.g., OrderEvent) Kafka Consumer
Outbound Event Port    EventPort (e.g., OrderPub)   Kafka Producer
Notification Port      NotificationPort             Email Service
```
