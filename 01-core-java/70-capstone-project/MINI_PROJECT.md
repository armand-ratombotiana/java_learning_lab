# Module 70: Enterprise Capstone Project - Mini Project

**Project Name**: The Global FinTech Trading Platform  
**Difficulty Level**: Master  
**Estimated Time**: 2-4 Weeks

---

## 🎯 Objective
Design, build, and deploy the entire backend architecture for a distributed FinTech trading platform, implementing the best practices learned from Modules 01 to 69. 

## 📝 Requirements

### Core Architecture

1. **Identity Service (Spring Boot + Security)**:
   - Implement JWT-based authentication.
   - Secure endpoints using Role-Based Access Control (RBAC).

2. **Market Data Service (Spring WebFlux + WebSockets)**:
   - Connect to a public crypto/stock WebSocket API (e.g., Binance or Finnhub).
   - Use Reactive Streams to process the high-frequency data.
   - Publish the processed price ticks to an Apache Kafka topic `market-data`.

3. **Trading Engine (CQRS + Event Sourcing)**:
   - Consume prices from the `market-data` Kafka topic.
   - Expose a GraphQL API for users to submit `PlaceOrderCommand`.
   - Validate the order. If valid, save an `OrderPlacedEvent` to the database using the Transactional Outbox pattern.
   - An Outbox Poller publishes the event to the `orders` Kafka topic.

4. **Ledger & Analytics (Spark / Flink)**:
   - A separate service listens to the `orders` topic.
   - It maintains an immutable append-only ledger in PostgreSQL.
   - Use an asynchronous projection to update a Redis cache representing the user's current Portfolio balance (optimized for fast reads).

### Infrastructure & Deployment

1. **Containerization**:
   - Write Multi-Stage Dockerfiles for all microservices.
   - Compile at least one service (e.g., the Identity Service) to a GraalVM Native Image to demonstrate sub-millisecond cold starts.

2. **Kubernetes & GitOps**:
   - Write Helm charts or plain YAML manifests for the Deployments, Services, and ConfigMaps.
   - Set up ArgoCD locally to monitor your Git repository and deploy the cluster automatically.

3. **Observability**:
   - Instrument all services with OpenTelemetry.
   - Prove that a Trace ID remains consistent across the GraphQL API, through the Kafka broker, and into the Ledger service.
   - Create a Grafana dashboard showing the JVM memory usage and HTTP latency of the Trading Engine.

---

## 💡 Submission Guidelines

Because this is a comprehensive Capstone, there is no single "Solution Blueprint" snippet. 
You are expected to structure your repository as a monorepo containing multiple maven sub-modules (or separate repos managed by GitOps). 

**Your final submission must include an `ARCHITECTURE.md` file containing**:
- A sequence diagram of a trade execution.
- A justification of why you chose specific databases (e.g., why Postgres for the Ledger but Redis for the Portfolio).
- Instructions on how to spin up the local environment (e.g., a `docker-compose.yml` that boots Kafka, Postgres, Redis, Zipkin, and your services).