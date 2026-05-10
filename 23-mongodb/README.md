# MongoDB Module

<div align="center">

![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-Document-47A248?style=for-the-badge)
![Spring Data](https://img.shields.io/badge/Spring%20Data-MongoDB-6DB33F?style=for-the-badge)

**Master Document-Oriented Databases with MongoDB**

</div>

---

## Overview

This module covers MongoDB fundamentals, document modeling, aggregation pipelines, and advanced patterns. You'll learn how to build scalable document-based applications using Spring Data MongoDB.

---

## Topics Covered

### 1. MongoDB Fundamentals
- Document structure and BSON
- Collections and databases
- CRUD operations
- Query optimization

### 2. Document Modeling
- Embedded documents
- References vs embedding
- Schema design patterns
- Validation rules

### 3. Indexing Strategies
- Single field indexes
- Compound indexes
- Text indexes
- Geospatial indexes

### 4. Aggregation Pipeline
- $match, $group, $project
- $lookup (joins)
- $facet for multiple aggregations
- Map-reduce

### 5. Advanced Patterns
- Change streams
- Transactions
- Sharding
- Change data capture

---

## Module Structure

```
23-mongodb/
├── README.md                      # This file
├── PROJECTS.md                    # Hands-on projects
├── PEDAGOGIC_GUIDE.md            # Teaching guide
├── EXERCISES.md                  # Practice exercises
└── src/main/java/com/learning/   # Source code
```

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MongoDB (Docker)

### Start MongoDB
```bash
docker run -p 27017:27017 mongo:latest
```

Connect: mongodb://localhost:27017

### Run Examples
```bash
cd 23-mongodb
mvn clean compile
mvn spring-boot:run
```

---

## Document Model Example

```json
{
  "_id": ObjectId("..."),
  "name": "Laptop Pro",
  "description": "High-performance laptop",
  "price": Decimal128("1299.99"),
  "category": "Electronics",
  "tags": ["computer", "laptop", "tech"],
  "specs": {
    "cpu": "Intel i7",
    "ram": "16GB",
    "storage": "512GB SSD"
  },
  "reviews": [
    {
      "userId": "user123",
      "rating": 5,
      "comment": "Excellent laptop!",
      "date": "2024-01-15"
    }
  ],
  "createdAt": ISODate("2024-01-01")
}
```

---

## Key Concepts

### Repository Pattern

```java
public interface ProductRepository extends MongoRepository<Product, String> {
    
    List<Product> findByCategory(String category);
    
    @Query("{ 'price': { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);
    
    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Product> searchByName(String name);
}
```

### Aggregation Pipeline

```java
Aggregation aggregation = Aggregation.newAggregation(
    Aggregation.match(Criteria.where("active").is(true)),
    Aggregation.group("category")
        .count().as("count")
        .avg("price").as("avgPrice"),
    Aggregation.sort(Sort.Direction.DESC, "count"),
    Aggregation.limit(10)
);

AggregationResults<StatResult> results = mongoTemplate.aggregate(
    aggregation, "products", StatResult.class);
```

---

## Production Patterns

1. **Schema Validation**: Ensure document consistency
2. **Time Series Collections**: Efficient temporal data
3. **Change Streams**: Real-time data processing
4. **Transactions**: Multi-document ACID operations
5. **Change Data Capture**: CDC for analytics

---

## Next Steps

After completing this module, proceed to:
- [31-mongodb](../31-mongodb) - Advanced MongoDB patterns
- [62-data-pipeline](../62-data-pipeline) - Data pipeline design

---

## Resources

- [MongoDB Documentation](https://docs.mongodb.com/)
- [Spring Data MongoDB Reference](https://docs.spring.io/spring-data/mongodb/reference/html/)
- [MongoDB University](https://learn.mongodb.com/)

