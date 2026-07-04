# Debugging: MongoDB

## Enable Query Logging

```java
// Java driver logging
System.setProperty("org.mongodb.driver.log.level", "ALL");
System.setProperty("org.mongodb.driver.verbosity", "5");

// application.properties
logging.level.org.mongodb.driver=DEBUG
logging.level.org.mongodb.driver.protocol.command=TRACE
```

## Profiling in MongoDB

```javascript
// Enable profiling
db.setProfilingLevel(2); // log all operations

// View slow queries
db.system.profile.find({
  millis: { $gt: 100 }
}).sort({ ts: -1 }).limit(20).pretty();
```

## Explain Query Plans

```java
Document explanation = users.find(
    Filters.eq("email", "alice@example.com"))
    .explain();

// Check execution stats
// "executionStats.executionSuccess": true
// "executionStats.totalDocsExamined" > "executionStats.nReturned"
// indicates inefficient query
```

## Common Debugging Commands

```javascript
// Check index usage
db.users.aggregate([
  { $indexStats: {} }
]);

// Current operations
db.currentOp({ "active": true, "secs_running": { $gt: 5 } });

// Kill slow operation
db.killOp(<opid>);

// Database statistics
db.users.stats();

// Server status
db.serverStatus().metrics.commands;
```

## Connection Pool Diagnostics

```java
// Check pool statistics
MongoClient client = MongoClients.create(uri);
MongoClientSettings settings = client.getSettings();
// Use JMX MBeans or metrics exporters
```

## Replication Lag

```javascript
// Check replication lag
rs.status().members.forEach(member => {
  print(member.name + ": " +
    (member.optimeDate ? member.optimeDate : "no optime"));
});

// Lag = primary_time - secondary_time
```

## Slow Query Analysis

```javascript
// Identify unindexed queries
db.adminCommand({
  setParameter: 1,
  notablescan: 1  // blocks queries without index
});

// Find missing indexes
db.users.aggregate([
  { $indexStats: {} },
  { $match: { accesses: { $eq: 0 } } }
]);
```
