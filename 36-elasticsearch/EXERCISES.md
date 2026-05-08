# Exercises - Elasticsearch

## Exercise 1: Document Operations
Implement basic CRUD with Elasticsearch:

1. Index a document with custom mapping
2. Search documents using match queries
3. Update document fields with partial update
4. Delete documents and verify removal

## Exercise 2: Full-Text Search
Implement search functionality:

1. Create index with custom analyzer for text fields
2. Implement multi-field search with boosting
3. Use phrase matching with slop parameter
4. Implement fuzzy search for typo tolerance

## Exercise 3: Aggregations
Build analytics with aggregations:

1. Calculate average price using avg aggregation
2. Group by category with bucket aggregations
3. Implement histogram for price ranges
4. Use nested aggregations for complex analytics

## Exercise 4: Complex Queries
Build advanced search patterns:

1. Implement boolean query with must/should/filter
2. Use function_score to boost by recency
3. Implement highlighting for search results
4. Use query_string for advanced syntax

## Exercise 5: Performance Optimization
Optimize search performance:

1. Create index templates for common patterns
2. Optimize index settings (shards, replicas)
3. Implement pagination with search_after
4. Use filter context to avoid scoring overhead

## Bonus Challenge
Build an autocomplete/search-as-you-type feature using completion suggester. Support prefix matching and return suggestions in under 50ms.