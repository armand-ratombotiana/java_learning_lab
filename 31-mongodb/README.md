# 31 - MongoDB Learning Module

## Overview
MongoDB is a NoSQL document database that stores data in flexible JSON-like documents. This module covers MongoDB integration with Spring Boot.

## Module Structure
- `mongodb-repository/` - Spring Data MongoDB implementation

## Technology Stack
- Spring Boot 3.x
- Spring Data MongoDB
- MongoDB Java Driver
- Maven

## Prerequisites
- MongoDB server running on `localhost:27017`
- MongoDB Compass (optional for visualization)

## Key Features
- Document-based data model
- Flexible schema
- Rich query language
- Horizontal scalability
- Aggregation pipelines

## Build & Run
```bash
cd mongodb-repository
mvn clean install
mvn spring-boot:run
```

## Default Configuration
- Host: `localhost`
- Port: `27017`
- Database: `test`

## Related Modules
- 33-postgresql (relational comparison)
- 36-elasticsearch (search engine)