# Module 22: Microservices Concepts - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What are the main drawbacks or challenges of moving from a Monolith to Microservices?
**Answer**:
While microservices offer independent deployment and scaling, they introduce massive operational complexity:
1. **Network Latency & Unreliability**: In a monolith, objects communicate via in-memory method calls. In microservices, every call is a network request that can fail or timeout (Fallacies of Distributed Computing).
2. **Data Consistency**: Because of the "Database per Service" rule, you can no longer use simple ACID database transactions across domains. You must implement complex distributed transactions (like Sagas) ensuring eventual consistency.
3. **Operational Overhead**: Deploying, monitoring, and tracing errors across 50 separate services requires advanced infrastructure (Kubernetes, ELK stack, Jaeger for distributed tracing).
4. **Testing**: Integration and End-to-End testing become significantly harder.

### Q2: Explain the "Database per Service" pattern. Why is it important?
**Answer**:
The "Database per Service" pattern states that each microservice must own its domain data and database. Other services cannot query that database directly; they must use the owning service's API.
**Importance**: It ensures loose coupling. If Service A and Service B share a database schema, and Service A changes a column name, Service B breaks. A shared database creates a single point of failure and bottleneck, transforming the architecture into a "Distributed Monolith."

### Q3: What is the Saga Pattern? What is the difference between Choreography and Orchestration?
**Answer**:
The Saga pattern is used to manage distributed transactions without locking (no 2-Phase Commits). It breaks a global transaction into a series of local transactions. If a step fails, the Saga executes compensating transactions to undo the previous steps.
- **Choreography**: Decentralized. Each service listens to events from a message broker (e.g., Kafka) and independently decides what to do next. Good for simple flows, but hard to track the overall state.
- **Orchestration**: Centralized. A central "Orchestrator" service acts as a manager, explicitly telling other services what local transactions to execute. Easier to monitor and manage complex workflows.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Designing an E-Commerce Checkout
**Problem**: An interviewer asks you to draw the architecture for an E-Commerce checkout flow. The user clicks "Buy". The system must charge their credit card, deduct inventory, and send a confirmation email. How do you design this to be highly resilient?

**Solution Strategy**:
1. **API Gateway**: Receives the `POST /checkout` request from the UI. Routes it to the `OrderService`.
2. **OrderService**: Saves the order as `PENDING`. Responds `202 Accepted` to the UI immediately (Async processing). It publishes an `OrderPlacedEvent` to a Kafka topic.
3. **PaymentService**: Listens to the topic. Charges the card. If successful, publishes `PaymentSucceededEvent`.
4. **InventoryService**: Listens to the `PaymentSucceededEvent`. Deducts stock. Publishes `InventoryReservedEvent`.
5. **NotificationService**: Listens to the `InventoryReservedEvent` and sends the email.
6. **Failure Handling (Saga)**: If the `InventoryService` fails (out of stock), it publishes `InventoryFailedEvent`. The `PaymentService` listens to this and issues a refund (compensating transaction). The `OrderService` listens and marks the order `CANCELLED`.