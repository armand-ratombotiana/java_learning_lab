# Multi-Model Databases & Polyglot Persistence

## Overview
Multi-model databases support multiple data models (document, graph, key-value, relational) within a single database engine. Polyglot persistence uses different database systems for different data storage needs within the same application. Choosing the right model for each data type optimizes performance, flexibility, and developer productivity.

## Key Concepts
- **Polyglot Persistence**: Use the best database for each workload
- **Multi-Model DB**: Single engine supporting multiple models (e.g., ArangoDB, CosmosDB)
- **Data Model**: Document, Graph, Key-Value, Columnar, Relational
- **Use Case Fit**: Relational for ACID, Document for schemaless, Graph for relationships
- **Consistency Models**: Strong vs eventual consistency per workload

## Java Example
```java
// Polyglot: Use different DBs for different data types
@Service
public class OrderService {
    // Relational for transactional orders
    private final JpaRepository<Order, Long> orderRepo;
    // Document for product catalog (flexible schema)
    private final MongoRepository<Product, String> productRepo;
    // Redis for session cache / real-time inventory
    private final RedisTemplate<String, Integer> inventoryCache;
    // Neo4j for product recommendation graph
    private final Neo4jRepository<ProductNode, Long> recommendationGraph;
}
```
