# API Composition Pattern — Theory

## 1. Introduction
API Composition aggregates data from multiple backend services into a single response tailored to client needs. It reduces round-trips, hides service decomposition from clients, and enables independent service evolution.

## 2. Problem Context
Microservice architectures expose clients to the N+1 query problem, chatty interfaces requiring many small API calls, data aggregation complexity on the client side, service refactoring impacts on client implementations, and different data formats across services.

## 3. Composition Approaches

### 3.1 API Gateway Composition
The API Gateway acts as a composition layer routing requests to multiple downstream services and merging responses. The gateway receives a single client request, fans out to multiple services, aggregates responses into a unified response, and handles error aggregation.

### 3.2 GraphQL Federation
GraphQL enables declarative data fetching with a unified schema across multiple services. Key concepts include federated schema composed from multiple subgraphs, entities with @key directive for cross-service references, resolvers that fetch data from respective services, and gateway schema stitching.

### 3.3 BFF Composition
Each Backend for Frontend service composes data specifically for its client type's needs. Advantages include client-specific data shaping, protocol optimization per client, independent evolution, and simplified client code.

## 4. Aggregate Services
An aggregate service composes data from multiple downstream services. It serves multiple client types, contains no business logic, focuses solely on data composition, and provides coarse-grained APIs over fine-grained services.

## 5. Data Aggregation Patterns

### 5.1 Parallel Fan-Out
Request all downstream services simultaneously. Aggregate responses when all complete. Use when data dependencies are independent.

### 5.2 Sequential Chaining
Request services in sequence where the response of one is input to another. Use when data has dependency chains.

### 5.3 Hybrid Approach
Mix of parallel and sequential requests. Some data fetched independently while other data depends on previous results.

## 6. Error Handling in Composition

### 6.1 Partial Success
Return partial data with error indicators for failed services.

### 6.2 Fail-Fast
If any critical service fails, return an error immediately. Cancel pending requests gracefully.

### 6.3 Degraded Response
Return cached or default data for failed services. Maintains availability at the cost of freshness.

## 7. Performance Optimization
Request collapsing to batch identical requests, response caching with appropriate invalidation, connection pooling for downstream calls, async non-blocking I/O for fan-out requests, and compression for large aggregated responses.
