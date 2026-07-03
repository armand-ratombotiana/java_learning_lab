# 07 - Database Integration

Integration with relational and NoSQL databases in Java. Covers PostgreSQL (JDBC, connection pooling, CRUD), MongoDB (document model, queries, aggregation), and Redis (caching, key-value operations, data structures).

## Prerequisites

- Java 11+
- Maven 3.x
- Docker (for running database containers)

## Key Concepts

- PostgreSQL: JDBC connections, prepared statements, CRUD operations, connection pooling
- MongoDB: document database, BSON, CRUD operations, aggregation pipeline
- Redis: in-memory data store, strings, lists, sets, sorted sets, hashes, pub/sub

## Module Structure

- `01-postgresql/` - PostgreSQL JDBC integration
- `02-mongodb/` - MongoDB driver integration
- `03-redis/` - Redis client (Jedis) integration

## Learning Objectives

- Connect Java applications to relational databases via JDBC
- Work with document databases using MongoDB
- Use Redis for caching and data structure operations

## Estimated Time

- 4-6 hours across all submodules

## How to Build

```bash
cd 07-databases
mvn clean package
```

Run individual submodules with corresponding database running:

```bash
cd 01-postgresql
mvn compile exec:java -Dexec.mainClass="com.learning.databases.Lab"
```
