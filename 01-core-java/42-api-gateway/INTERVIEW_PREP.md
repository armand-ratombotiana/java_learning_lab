# Module 42: API Gateway Pattern - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the primary role of an API Gateway, and why is it necessary in a Microservices architecture?
**Answer**:
In a microservices architecture, a single frontend action (like rendering an e-commerce homepage) might require fetching data from 5 different microservices. If the frontend client calls these 5 services directly:
1. It exposes the internal IP addresses/ports of the backend to the public internet.
2. The client is forced to make 5 separate HTTP requests over slow mobile networks (Chatty API).
3. Any changes to backend service boundaries will break the client apps.
An API Gateway acts as a single, unified "front door" (Reverse Proxy). The client makes 1 request to the Gateway. The Gateway routes the request internally, aggregates the data if necessary, and returns a single response.

### Q2: What are Cross-Cutting Concerns, and why are they placed in the API Gateway?
**Answer**:
Cross-Cutting Concerns are functionalities that are required by almost every microservice, such as:
- JWT Authentication / Authorization
- SSL Termination (HTTPS decryption)
- Rate Limiting (throttling requests)
- CORS management
If you put this logic in the individual microservices, you are duplicating code across 50 different applications (potentially written in different languages). By moving this logic to the API Gateway, requests are authenticated and throttled *before* they ever reach the internal network, keeping the microservices lightweight and focused purely on business logic.

### Q3: What is the Backend for Frontend (BFF) Pattern?
**Answer**:
A general-purpose API Gateway often becomes a massive, bloated bottleneck because a Mobile App, a Web App, and a 3rd Party API consumer all have radically different data requirements. 
The BFF pattern suggests creating *multiple* smaller API Gateways, each tailored to a specific client interface. You would have a `Mobile-Gateway` that optimizes payloads for low bandwidth, and a `Web-Gateway` that aggregates heavier payloads for desktop browsers. This aligns with the Interface Segregation Principle.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The "God API" Anti-Pattern
**Problem**: An interviewer presents an architecture where the API Gateway contains 15,000 lines of Java code. It performs complex SQL queries to aggregate data, generates PDF invoices, and handles deep business logic validations. What is wrong with this design, and what are the consequences?

**Solution**:
The team has accidentally recreated a Monolith inside the API Gateway, often called the "God API" anti-pattern.
**Consequences**: 
- **Deployment Bottlenecks**: Every microservice team has to touch the Gateway code to expose their features. This creates a massive merge conflict zone and slows down deployments.
- **Scaling Issues**: The Gateway is performing heavy CPU/Memory tasks instead of fast, non-blocking I/O routing. This will cause the Gateway to crash, taking down the entire system.
**Fix**: The API Gateway must remain "dumb." Its only job is routing, auth, and light data aggregation. Complex business logic and database queries must be pushed down into specialized backend microservices.

### Scenario 2: Handling Backend Failures
**Problem**: The API Gateway routes traffic to the `InventoryService`. The `InventoryService` goes down, but it doesn't crash; instead, it just hangs, taking 30 seconds to return an HTTP timeout. What happens to the API Gateway, and how do you prevent it?

**Solution**:
Because the Gateway is waiting 30 seconds for the backend, its thread pool (or connection pool) will quickly become exhausted as more requests pile up. Once the pool is full, the Gateway will start rejecting *all* traffic, even to healthy services like `UserService`. 
**Prevention**: 
Implement **Circuit Breakers** (e.g., using Spring Cloud CircuitBreaker or Resilience4j) directly in the API Gateway. If the `InventoryService` times out repeatedly, the circuit opens, and the Gateway immediately returns an HTTP 503 or a cached fallback response without waiting, protecting its own resources and keeping the rest of the system online.