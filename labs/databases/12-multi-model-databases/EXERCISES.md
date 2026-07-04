# Exercises: Multi-Model & Polyglot

## Exercise 1 – Data Modeling
Design a polyglot data architecture for an e-commerce platform:
- Which database for orders? (ACID, transactions)
- Which for product catalog? (flexible schema, nested attributes)
- Which for recommendations? (graph traversals)
- Which for search? (full-text, faceted)
- Which for sessions? (fast, TTL)
Justify each choice.

## Exercise 2 – Multi-Database Application
Build a Spring Boot application that uses:
- PostgreSQL for `Customer` entity
- MongoDB for `Product` document
- Redis for `Session` cache
- Implement a composite service that reads from all three

## Exercise 3 – Data Synchronization
Implement an outbox pattern:
- Write to `outbox` table in PostgreSQL within the same transaction
- Use a scheduled task to publish events to Kafka
- Consume events and update Elasticsearch index
- Test end-to-end consistency

## Exercise 4 – Migration Strategy
Take a monolithic PostgreSQL schema:
```sql
users (id, name, email, session_data JSON)
products (id, name, category, attributes JSONB)
reviews (id, product_id, user_id, content, rating, metadata JSONB)
```
Design a polyglot migration strategy for each data type.

## Exercise 5 – Performance Comparison
- Create the same query in PostgreSQL (relational), MongoDB (document), and Neo4j (graph):
  "Find friends of friends who liked product X"
- Compare execution times and query complexity
- Analyze which database is best for this workload and why
