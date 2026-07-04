# Performance: MongoDB

## Index Strategy

```javascript
// Equality + Sort + Range (ESR) rule
// Equality fields first, sort fields, range fields last
db.orders.createIndex({
  status: 1,       // equality
  createdAt: -1,   // sort
  total: 1         // range
});
```

## Query Performance Optimization

```java
// WRONG: no index, high document scan
users.find(Filters.regex("name", Pattern.compile("^A", CASE_INSENSITIVE)));

// RIGHT: use appropriate index
users.createIndex(Indexes.collation(
    Collation.builder().locale("en").strength(1).build())
    .ascending("name"));
users.find(Filters.eq("name", "alice"))
    .collation(Collation.builder().locale("en").strength(1).build());
```

## Bulk Operations

```java
// WRONG: individual inserts
for (User user : users) {
    collection.insertOne(user);
}

// RIGHT: bulk insert
List<InsertOneModel<User>> batch = users.stream()
    .map(InsertOneModel::new)
    .toList();
BulkWriteResult result = collection.bulkWrite(batch,
    new BulkWriteOptions().ordered(false));
```

## Connection Pool Tuning

```java
MongoClientSettings settings = MongoClientSettings.builder()
    .applyToConnectionPoolSettings(builder -> builder
        .maxSize(50)
        .minSize(5)
        .maxConnectionIdleTime(10, TimeUnit.MINUTES)
        .maxConnectionLifeTime(30, TimeUnit.MINUTES)
        .maxWaitTime(5, TimeUnit.SECONDS)
    )
    .applyToSocketSettings(builder -> builder
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
    )
    .build();
```

## WiredTiger Cache

```javascript
// Production sizing: 50% of RAM (minus other processes)
db.adminCommand({ setParameter: 1, wiredTigerCacheSizeGB: 8 });
```

## Aggregation Pipeline Optimization

```java
// WRONG: $project before $match
List.of(
    Aggregates.project(Projections.include("name", "status")),
    Aggregates.match(Filters.eq("status", "active"))
);

// RIGHT: $match first to reduce pipeline documents
List.of(
    Aggregates.match(Filters.eq("status", "active")),
    Aggregates.project(Projections.include("name", "status"))
);
```

## Monitoring Performance

```javascript
// Query execution times
db.adminCommand({ serverStatus: 1 }).opcounters;

// Index usage
db.users.aggregate([{ $indexStats: {} }]);

// Cache hit ratio
// wiredTiger.cache["bytes currently in the cache"] vs
// wiredTiger.cache["bytes read into cache"]
```
