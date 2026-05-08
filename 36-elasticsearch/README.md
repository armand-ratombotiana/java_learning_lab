# 36 - Elasticsearch Learning Module

## Overview
Elasticsearch is a distributed, RESTful search and analytics engine. This module covers Elasticsearch integration with Spring Boot.

## Module Structure
- `elasticsearch-learning/` - Spring Data Elasticsearch implementation

## Technology Stack
- Spring Boot 3.x
- Spring Data Elasticsearch
- Elasticsearch Java Client
- Maven

## Prerequisites
- Elasticsearch running on `localhost:9200`
- Kibana (optional): `http://localhost:5601`

## Key Features
- Full-text search with relevance scoring
- Real-time indexing and search
- Aggregations and analytics
- Distributed and scalable
- RESTful API
- JSON document storage

## Build & Run
```bash
cd elasticsearch-learning
mvn clean install
mvn spring-boot:run
```

## Default Configuration
- Host: `localhost`
- Port: `9200`
- Index prefix: `spring-`

## Core Concepts
- Index: Collection of documents
- Document: JSON data unit
- Shard: Index partition for scaling
- Replica: Shard copy for redundancy

## Related Modules
- 31-mongodb (NoSQL document store)
- 38-prometheus (observability)