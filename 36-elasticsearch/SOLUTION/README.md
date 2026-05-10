# Elasticsearch Solution

## Overview
This module covers Elasticsearch client operations, search queries, and aggregations.

## Key Features

### Elasticsearch Client
- Connecting to Elasticsearch cluster
- Managing indices
- Document operations

### Search Queries
- Match queries
- Boolean queries
- Full-text search

### Aggregations
- Terms aggregation
- Metrics aggregation
- Bucket aggregation

## Usage

```java
ElasticsearchClient client = ...;
ElasticsearchSolution solution = new ElasticsearchSolution(client);

// Create index
solution.createIndex("my-index");

// Index documents
solution.indexDocument("my-index", "1", Map.of("content", "Hello World"));

// Search
List<Hit<Map>> results = solution.searchDocuments("my-index", "Hello");

// Aggregate
Map<String, JsonData> aggResults = solution.aggregateByField("my-index", "category");
```

## Dependencies
- elasticsearch-java client library
- JUnit 5 for testing
- Mockito for mocking