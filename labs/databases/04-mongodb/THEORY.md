# Theory: MongoDB Document Model

## BSON Document Format
BSON (Binary JSON) is a binary-encoded serialization of JSON-like documents. It supports more data types than JSON:
- String, Integer, Double, Boolean, Null
- Array, Embedded Document
- ObjectId (12-byte unique identifier)
- Date, Timestamp, Binary Data
- Regular Expression, JavaScript Code

## Document Structure
```json
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "name": "Alice",
  "email": "alice@example.com",
  "address": {
    "city": "NYC",
    "zip": "10001"
  },
  "tags": ["premium", "vip"],
  "created_at": ISODate("2024-01-01T00:00:00Z")
}
```

## CRUD Operations
- **Create**: `insertOne()`, `insertMany()`, `bulkWrite()`
- **Read**: `find()`, `findOne()`, `aggregate()`
- **Update**: `updateOne()`, `updateMany()`, `replaceOne()`
- **Delete**: `deleteOne()`, `deleteMany()`

## Relationships
- **Embedding**: Store related data within a single document (1:1, 1:N with subdocuments)
- **Referencing**: Store references (IDs) between collections (N:M, large 1:N)
- Hybrid approach depending on access patterns

## ACID Transactions (4.0+)
```java
try (ClientSession session = client.startSession()) {
    session.startTransaction();
    try {
        coll1.insertOne(session, doc1);
        coll2.insertOne(session, doc2);
        session.commitTransaction();
    } catch (Exception e) {
        session.abortTransaction();
    }
}
```

## Indexing
- Single field: `{ name: 1 }`
- Compound: `{ status: 1, created_at: -1 }`
- Multi-key (array): `{ tags: 1 }`
- Text: `{ content: "text" }`
- Geospatial: `{ location: "2dsphere" }`
- TTL: `{ created_at: 1 }` with `expireAfterSeconds`
