# MongoDB

## Overview
MongoDB is a NoSQL document database that stores data in flexible, JSON-like documents. It uses a document data model where related data is stored together, enabling intuitive and fast data access.

## Key Concepts
- **Document**: Basic unit of data (BSON format)
- **Collection**: Group of documents (analogous to SQL table)
- **Database**: Container for collections
- **Replica Set**: Self-healing primary-secondary cluster
- **Sharding**: Horizontal scaling across partitioned data

## When to Use MongoDB
- Rapid prototyping with evolving schemas
- Hierarchical or nested data structures
- High-volume write workloads
- Geographical distribution with multi-document ACID transactions

## Java Integration
```java
// MongoDB Java Driver (Synchronous)
MongoClient client = MongoClients.create("mongodb://localhost:27017");
MongoDatabase db = client.getDatabase("myapp");
MongoCollection<Document> coll = db.getCollection("users");

Document user = new Document("name", "Alice")
    .append("email", "alice@example.com")
    .append("address", new Document("city", "NYC").append("zip", "10001"));
coll.insertOne(user);

// Spring Data MongoDB
// @Document(collection = "users")
// public class User { @Id private String id; private String name; ... }
// MongoRepository<User, String> userRepository;
```
