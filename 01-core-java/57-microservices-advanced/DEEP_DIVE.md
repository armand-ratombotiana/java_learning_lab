# Module 57: Advanced Microservices Patterns - Deep Dive

**Difficulty Level**: Expert  
**Prerequisites**: Modules 01-56 (especially Microservices Concepts, Event Sourcing, and API Gateway)  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Anti-Corruption Layer (ACL)](#acl)
2. [Saga Pattern: Choreography vs Orchestration](#sagas)
3. [Bulkhead Pattern](#bulkhead)
4. [Sidecar Pattern](#sidecar)
5. [Transactional Outbox Pattern](#outbox)

---

## 1. Anti-Corruption Layer (ACL) <a name="acl"></a>
When migrating a monolithic application to microservices, the new modern services often need to communicate with the legacy system. The ACL acts as a translation layer between the two systems.
Instead of allowing the legacy system's outdated data structures (e.g., messy XML or obscure database column names) to "corrupt" the clean Domain-Driven Design of the new microservice, the ACL intercepts the legacy data and translates it into the modern domain language before it enters the new service.

---

## 2. Saga Pattern: Choreography vs Orchestration <a name="sagas"></a>
Handling distributed transactions across microservices requires the Saga pattern (a sequence of local transactions with compensating rollbacks).
- **Choreography**: Decentralized. Each service publishes an event (e.g., `OrderCreated`) to a message broker. Other services listen, do their part, and publish their own events. Great for simple flows (2-3 services), but creates circular dependencies and is hard to debug.
- **Orchestration**: Centralized. A dedicated "Saga Orchestrator" service acts as a manager. It explicitly sends commands to participants (e.g., `ChargeCreditCardCommand` -> Payment Service) and awaits replies. Best for complex workflows, as the entire business process is visible in one place.

---

## 3. Bulkhead Pattern <a name="bulkhead"></a>
Inspired by the watertight compartments (bulkheads) of a ship, this pattern isolates elements of an application into pools so that if one fails, the others continue to function.
For example, if Service A connects to Database X and Database Y, and Database X slows down dramatically, all threads in Service A's connection pool might get stuck waiting for Database X. By applying the Bulkhead pattern (using separate thread pools for each database connection), the requests to Database Y will continue completely unaffected.

---

## 4. Sidecar Pattern <a name="sidecar"></a>
Deploying helper components as a separate process or container (a "sidecar") alongside the primary application container within the same Pod (in Kubernetes).
This abstracts cross-cutting concerns away from the application code. Typical uses include:
- Proxying network traffic (Service Mesh / Envoy)
- Log shipping (Fluentd/Filebeat)
- Config fetching and syncing

---

## 5. Transactional Outbox Pattern <a name="outbox"></a>
Updating a local database and publishing a message to a Kafka broker cannot easily be wrapped in a single distributed transaction. If the DB commits but the broker crashes before the message is sent, you have inconsistent state (Dual Write Problem).
**Solution**: 
1. In the same database transaction, write the business entity to the main table, and write a JSON representation of the event to an `outbox` table.
2. A separate background process (or CDC tool like Debezium) continuously polls the `outbox` table and publishes the messages to Kafka, ensuring "At-Least-Once" delivery.