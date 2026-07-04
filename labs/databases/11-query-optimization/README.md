# Query Optimization

## Overview
Query optimization focuses on improving database query performance through index strategies, execution plan analysis, query rewriting, and understanding database internals. The goal is to minimize response time and resource usage for database operations.

## Key Concepts
- **EXPLAIN / EXPLAIN ANALYZE**: Execution plan visualization
- **Index Strategies**: B-tree, Hash, GiST, GIN, BRIN indexes
- **Query Tuning**: Slow query analysis, rewriting poor queries
- **N+1 Problem**: Excessive queries from lazy loading in ORMs
- **Performance Budget**: Setting acceptable query time thresholds
- **Covering Indexes**: Indexes that include all needed columns

## Java Example
```java
// Before optimization – N+1 queries
List<Order> orders = orderRepository.findAll();
for (Order o : orders) {
    // Triggers N additional queries for items
    System.out.println(o.getItems().size());
}

// After – single query with JOIN FETCH
@Query("SELECT o FROM Order o JOIN FETCH o.items")
List<Order> findAllWithItems();
```
