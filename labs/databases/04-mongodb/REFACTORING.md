# Refactoring: MongoDB

## Embedding vs Referencing Refactor

```java
// Before: normalized (like RDBMS)
@Document("orders")
public class Order {
    private String id;
    private String customerId; // reference
}

@Document("customers")
public class Customer {
    private String id;
    private String name;
}

// After: embedded for 1:1 read optimization
@Document("orders")
public class Order {
    private String id;
    private CustomerInfo customer; // embedded
    public static class CustomerInfo {
        private String id;
        private String name;
    }
}
```

## Schema Migration (schema-less → validated)

```java
// Step 1: Add new field with default
db.users.updateMany({},
  { $set: { "status": "active", "version": 2 } });

// Step 2: Add schema validation
db.runCommand({
  collMod: "users",
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["name", "email", "status"],
      properties: {
        name: { bsonType: "string" },
        email: { bsonType: "string" },
        status: { enum: ["active", "inactive"] }
      }
    }
  },
  validationLevel: "moderate" // warn on existing docs
});
```

## MapReduce to Aggregation Pipeline

```javascript
// Before: MapReduce (slow, complex)
db.orders.mapReduce(
  function() { emit(this.customer, this.amount); },
  function(key, values) { return Array.sum(values); },
  { out: "customer_totals" }
);

// After: Aggregation pipeline (10-100x faster)
db.orders.aggregate([
  { $group: { _id: "$customer", total: { $sum: "$amount" } } },
  { $out: "customer_totals" }
]);
```

## Reactive to Sync (or vice versa)

```java
// Before: synchronous driver
List<User> users = collection.find().into(new ArrayList<>());

// After: reactive (RxJava)
Flowable<User> users = rxCollection.find();
```

## Index Refactoring

```javascript
// Before: individual indexes
db.users.createIndex({ "status": 1 });
db.users.createIndex({ "createdAt": -1 });

// After: compound index covering both queries
db.users.createIndex({ "status": 1, "createdAt": -1 });
```

## Data Type Migration

```javascript
// Migrate string dates to ISODate
db.events.find({ "timestamp": { $type: "string" } }).forEach(doc => {
  db.events.updateOne(
    { _id: doc._id },
    { $set: { "timestamp": ISODate(doc.timestamp) } }
  );
});
```
