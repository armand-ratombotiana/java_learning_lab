# Step by Step: Building a Polyglot Application

## Step 1: Identify Data Types
```
Orders     → ACID, transactions       → PostgreSQL
Products   → Flexible schema, nested  → MongoDB
Sessions   → Fast, TTL                → Redis
Relations  → Friends, recommendations  → Neo4j
Search     → Full-text, faceted        → Elasticsearch
```

## Step 2: Add Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-neo4j</artifactId>
</dependency>
```

## Step 3: Configure Each Database
```properties
spring.datasource.orders.url=jdbc:postgresql://localhost:5432/orders
spring.data.mongodb.uri=mongodb://localhost:27017/catalog
spring.data.redis.host=localhost
spring.data.neo4j.uri=bolt://localhost:7687
```

## Step 4: Create Repositories
- `OrderRepository extends JpaRepository` for RDBMS
- `ProductRepository extends MongoRepository` for documents
- `SessionRepository extends CrudRepository` for Redis
- `FriendRepository extends Neo4jRepository` for graph

## Step 5: Coordinate in Service Layer
```java
@Service
public class CheckoutService {
    // Orchestrate across databases
    @Transactional // RDBMS transaction
    public OrderCheckout checkout(CheckoutRequest req) {
        // 1. Update inventory in relational DB
        // 2. Store order in relational DB
        // 3. Invalidate cache in Redis
        // 4. Update recommendation graph in Neo4j
        // 5. Index order in Elasticsearch
    }
}
```
