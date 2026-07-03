# Module 22: Microservices Concepts - Mini Project

**Project Name**: Microservices Architecture Blueprint  
**Difficulty Level**: Advanced  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Design a scalable microservices architecture for an E-Commerce platform, applying core concepts such as Bounded Contexts, the Database-per-Service pattern, API Gateways, and inter-service communication (Sync vs Async).

## 📝 Requirements

### Core Features (Architecture Design)
You will not write production code for this project; instead, you will write a comprehensive architectural design document (or draw an architecture diagram) detailing the interaction between the following components:

1. **Domain Decomposition (Bounded Contexts)**:
   - Break the E-Commerce domain into at least 4 distinct microservices (e.g., `UserService`, `InventoryService`, `OrderService`, `PaymentService`).
   - Define the primary responsibilities for each service.

2. **Database Architecture**:
   - Apply the "Database per Service" pattern.
   - Specify the type of database for each service (e.g., Relational Postgres for Payments, NoSQL MongoDB for Product Catalog/Inventory).

3. **The API Gateway**:
   - Design an API Gateway acting as the single entry point for a Web Application client.
   - Describe its routing rules (e.g., `/api/orders` routes to `OrderService`).
   - Detail where Authentication (JWT validation) happens.

4. **Inter-Service Communication**:
   - **Synchronous**: Where must services talk synchronously? (e.g., OrderService calls PaymentService via REST/gRPC to immediately verify funds).
   - **Asynchronous**: Where can services talk asynchronously to improve resilience? (e.g., PaymentService publishes an `OrderPaidEvent` to an Apache Kafka topic; InventoryService listens to this topic to deduct stock without blocking the user checkout flow).

5. **Distributed Transaction (Saga Pattern)**:
   - Outline the steps of a Choreography-based Saga for the Checkout flow:
     - 1: `OrderService` creates an order in `PENDING` state.
     - 2: `PaymentService` processes payment and emits `PaymentSuccessEvent`.
     - 3: `InventoryService` deducts stock. If out of stock, it emits `InventoryFailedEvent`.
     - 4: If `InventoryFailedEvent` is fired, how does the `PaymentService` compensate (refund) the user?

---

## 💡 Solution Blueprint (Example Excerpt)

**1. Microservices Breakdown**:
- `OrderService` (Java/Spring Boot) -> Manages the lifecycle of customer orders. Uses PostgreSQL.
- `InventoryService` (Java/Spring Boot) -> Manages product stock. Uses MongoDB.
- `PaymentService` (Node.js) -> Integrates with Stripe/PayPal. Uses PostgreSQL.

**2. Gateway Routing**:
- `Spring Cloud Gateway` receives all requests from the React frontend.
- Validates the Authorization Bearer JWT.
- Routes `POST /checkout` -> `OrderService`.

**3. The Saga (Failure Scenario)**:
- `OrderService` -> Emits `OrderCreatedEvent`.
- `PaymentService` -> Listens, charges $50, emits `PaymentSucceededEvent`.
- `InventoryService` -> Listens, attempts to reserve item. Item is sold out! Emits `InventoryFailedEvent`.
- `PaymentService` -> Listens to failure event, triggers Stripe refund API, emits `PaymentRefundedEvent`.
- `OrderService` -> Listens to refund event, marks Order status as `CANCELLED_OUT_OF_STOCK`.