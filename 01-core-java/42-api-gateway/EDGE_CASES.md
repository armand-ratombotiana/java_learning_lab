# Module 42: API Gateway Pattern - Edge Cases & Pitfalls

---

## Pitfall 1: Building a Monolithic Gateway (The God API)

### ❌ Wrong
Adding excessive business logic, complex data transformations, and heavy database queries directly into the API Gateway. The Gateway becomes a monolithic bottleneck that requires constant deployments for every minor feature change.

### ✅ Correct
Keep the API Gateway "dumb." Its primary jobs are routing, auth, and rate-limiting. Heavy data aggregation and complex business logic should be pushed down into specialized backend microservices or Aggregator services.

---

## Pitfall 2: Gateway as a Single Point of Failure (SPOF)

### ❌ Wrong
Deploying a single instance of an API Gateway on a single machine. If the Gateway crashes, the entire microservices architecture becomes unreachable, rendering the system totally offline.

### ✅ Correct
Always run API Gateways in a highly available, clustered configuration (e.g., multiple replicas in a Kubernetes Deployment) behind an external Load Balancer or Ingress Controller.

---

## Pitfall 3: Blocking I/O in the Gateway

### ❌ Wrong
Using a synchronous, blocking framework (like traditional Spring MVC/Tomcat) for an API Gateway under heavy load. If a backend service becomes slow, the Gateway threads waiting for responses will quickly exhaust the server's thread pool.

### ✅ Correct
API Gateways should be built using asynchronous, non-blocking frameworks (like Spring Cloud Gateway, which uses Netty and Project Reactor) to handle tens of thousands of concurrent connections efficiently.