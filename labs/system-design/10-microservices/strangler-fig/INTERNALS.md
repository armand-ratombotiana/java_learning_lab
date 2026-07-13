# Strangler Fig Pattern Internals

## 🔀 The API Gateway (The Facade)
The API Gateway is the linchpin of the Strangler Fig pattern. It must be highly available and capable of dynamic routing without downtime.
Common technologies used for this Facade include:
- **Nginx / HAProxy**: Classic, highly performant reverse proxies.
- **Envoy / Kong**: Modern proxies built for microservices.
- **Spring Cloud Gateway**: A Java-based gateway that allows you to write routing rules and filters in Java code.

The Gateway must support **Path-Based Routing** (e.g., `/users/*` -> Microservice A, `/orders/*` -> Monolith).

## 🗄️ The Database Synchronization Problem
The hardest part of the Strangler Fig pattern is not routing HTTP traffic; it is **Data Gravity**.
The monolith usually has a single, massive relational database. Your new microservice needs its own isolated database to adhere to microservice best practices.

How do you keep them in sync during the migration?

### Approach 1: The Monolith as the Source of Truth
1. The new microservice reads from its own new database.
2. When a write occurs, the microservice writes to its database AND makes a synchronous API call to the monolith to update the legacy database.
- *Problem*: High latency, and if the monolith is down, the microservice cannot accept writes.

### Approach 2: Change Data Capture (CDC)
This is the industry standard for database migration without downtime.
1. You use a CDC tool like **Debezium**.
2. Debezium reads the transaction log (e.g., the `binlog` in MySQL or the `WAL` in PostgreSQL) of the legacy monolith database in real-time.
3. Every time an `INSERT`, `UPDATE`, or `DELETE` happens in the legacy database, Debezium publishes an event to an Apache Kafka topic.
4. Your new microservice consumes that Kafka topic and updates its own isolated database.

This keeps the new microservice's database eventually consistent (usually within milliseconds) with the legacy system, completely decoupling the two architectures.