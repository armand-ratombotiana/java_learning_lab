# Common Mistakes: MongoDB

## 1. Unbounded Array Growth
```java
// WRONG: arrays grow unboundedly (max 16MB document)
Document doc = new Document("user", "Alice")
    .append("events", eventsList); // eventsList grows forever

// RIGHT: move to separate collection for 1:N
Document doc = new Document("user", "Alice");
// Events in separate collection with user reference
```

## 2. No Indexes on Query Fields
```java
// WRONG: collection scan on every query
users.find(Filters.eq("email", "alice@example.com"));

// RIGHT: create index
users.createIndex(Indexes.ascending("email"));
```

## 3. Wrong Shard Key
```javascript
// WRONG: monotonic shard key ("_id" or timestamp)
sh.shardCollection("myapp.users", { "_id": "hashed" });

// RIGHT: high-cardinality, evenly-distributed key
sh.shardCollection("myapp.users", { "user_id": "hashed" });
```

## 4. Not Using Projections
```java
// WRONG: fetches all fields, including large unused ones
Document doc = users.find(Filters.eq("_id", id)).first();

// RIGHT: only request needed fields
Document doc = users.find(Filters.eq("_id", id))
    .projection(Projections.include("name", "email"))
    .first();
```

## 5. Deeply Nested Documents
```json
// WRONG: deeply nested arrays (query complexity)
{ "a": { "b": { "c": { "d": "value" } } } }

// RIGHT: flat structure
{ "a.b.c.d": "value" }
```

## 6. Forgetting `w: majority`
```java
// WRONG: write acknowledged by primary only (rollback risk on failover)
users.insertOne(doc);

// RIGHT: wait for majority acknowledgment
users.withWriteConcern(WriteConcern.MAJORITY).insertOne(doc);
```

## 7. Overusing `$lookup`
```java
// WRONG: $lookup performs JOIN (anti-pattern in MongoDB)
// If you need joins frequently, consider embedding or using relational DB

// PREFER: embedded documents or denormalization
```

## 8. Not Handling `ObjectId` Correctly
```java
// WRONG: string comparison instead of ObjectId
users.find(Filters.eq("_id", id)); // id is String, _id is ObjectId

// RIGHT: convert to ObjectId
users.find(Filters.eq("_id", new ObjectId(id)));
```

## 9. Ignoring Connection Pooling
```java
// WRONG: creating new MongoClient per request
MongoClient client = MongoClients.create(uri);

// RIGHT: reuse single MongoClient (thread-safe)
@Bean
public MongoClient mongoClient() {
    return MongoClients.create(uri);
}
```
