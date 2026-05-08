# Exercises - MongoDB

## Exercise 1: Document CRUD Operations
Create a simple document collection for a product catalog.

1. Implement CRUD operations for a `Product` document
2. Add embedded documents for `reviews` array
3. Query products by category using `find()` with criteria

## Exercise 2: Aggregation Pipeline
Build aggregation pipelines for analytics:

1. Calculate average price per category using `$match`, `$group`, `$avg`
2. Find top 5 best-selling products using `$sort` and `$limit`
3. Implement a text search index on product descriptions

## Exercise 3: Relationships in MongoDB
Explore different data modeling approaches:

1. Model a `User` with embedded `addresses` array
2. Model `Orders` referencing `User` via `userId` (normalized)
3. Compare query performance between embedded vs. referenced

## Exercise 4: Indexing Strategy
Optimize queries with proper indexes:

1. Create single-field index on `category`
2. Create compound index on `price` and `rating`
3. Verify index usage with `.explain()`
4. Measure query performance before/after indexing

## Exercise 5: Geospatial Queries
Implement location-based features:

1. Add `location` field with GeoJSON Point to stores
2. Query stores within 10km radius using `$near`
3. Use `$geoWithin` to find stores in a polygon region

## Bonus Challenge
Implement a real-time inventory system using MongoDB Change Streams to track product quantity updates and notify clients when stock is low.