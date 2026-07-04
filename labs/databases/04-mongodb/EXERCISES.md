# Exercises: MongoDB

## Exercise 1: Basic CRUD
```java
// Implement a complete Todo list with MongoDB
// - createTodo(todo)
// - findById(id)
// - findByStatus(status)
// - updateTodo(id, updates)
// - deleteTodo(id)
```

## Exercise 2: Aggregation Pipeline
```java
// From a collection of sales orders:
// 1. Filter by date range
// 2. Group by product category
// 3. Calculate total revenue, average quantity, count per category
// 4. Sort by revenue descending
// 5. Limit to top 10 categories
```

## Exercise 3: Index Optimization
1. Insert 1M documents into a collection
2. Run queries without indexes, measure performance
3. Create appropriate indexes
4. Run same queries, compare explain output
5. Experiment with compound indexes (ESR rule)

## Exercise 4: Schema Design
```
Design a blog platform schema:
- Users can write posts
- Posts have comments
- Users can like posts
- Posts have tags
- Compare embedded vs referenced approach
```

## Exercise 5: Change Streams
```java
// Implement a real-time notification system
// Watch the orders collection
// Route new orders to a notification service
// Handle resume tokens for crash recovery
```

## Exercise 6: Transactions
```java
// Implement a money transfer between two accounts
// - Begin transaction
// - Debit sender
// - Credit recipient
// - Handle rollback on failure
// - Handle retry on transient transaction errors
```

## Exercise 7: Replica Set
1. Set up a 3-node replica set in Docker
2. Test automatic failover (kill primary)
3. Configure read preference for secondary reads
4. Measure replication lag

## Exercise 8: Performance Benchmark
1. Compare insert performance: individual vs bulkWrite
2. Compare find performance: index vs no index
3. Compare aggregation vs MapReduce
4. Compare embedded vs referenced reads
