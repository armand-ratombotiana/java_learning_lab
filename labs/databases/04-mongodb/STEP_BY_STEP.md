# Step-by-Step: MongoDB

## Step 1: Install MongoDB
```bash
# Docker
docker run --name mongodb -p 27017:27017 -d mongo:7

# Verify
mongosh mongodb://localhost:27017
```

## Step 2: Connect with Java Driver
```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>5.1.0</version>
</dependency>
```

```java
MongoClient client = MongoClients.create("mongodb://localhost:27017");
MongoDatabase db = client.getDatabase("myapp");
```

## Step 3: Create Collections and Insert Documents
```java
MongoCollection<Document> users = db.getCollection("users");

Document user = new Document("name", "Alice")
    .append("email", "alice@example.com")
    .append("address", new Document("city", "NYC").append("zip", "10001"))
    .append("createdAt", new Date());
users.insertOne(user);
```

## Step 4: Query Documents
```java
// Find by field
users.find(Filters.eq("name", "Alice")).first();

// Find with multiple conditions
users.find(Filters.and(
    Filters.eq("address.city", "NYC"),
    Filters.gt("createdAt", someDate)
)).into(new ArrayList<>());

// Sort and limit
users.find().sort(Sorts.descending("createdAt")).limit(10);
```

## Step 5: Create Indexes
```java
users.createIndex(Indexes.ascending("email"),
    new IndexOptions().unique(true));
users.createIndex(Indexes.compoundIndex(
    Indexes.ascending("status"),
    Indexes.descending("createdAt")
));
users.createIndex(Indexes.text("name"));
```

## Step 6: Aggregation Pipeline
```java
List<Bson> pipeline = Arrays.asList(
    Aggregates.match(Filters.eq("status", "active")),
    Aggregates.group("$address.city",
        Accumulators.sum("count", 1)),
    Aggregates.sort(Sorts.descending("count"))
);
users.aggregate(pipeline).into(new ArrayList<>());
```

## Step 7: Set Up Replica Set
```bash
mongod --replSet rs0 --dbpath /data/rs1 --port 27017
mongod --replSet rs0 --dbpath /data/rs2 --port 27018
mongod --replSet rs0 --dbpath /data/rs3 --port 27019

# Initiate
mongosh --eval 'rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "localhost:27017" },
    { _id: 1, host: "localhost:27018" },
    { _id: 2, host: "localhost:27019" }
  ]
})'
```

## Step 8: Enable Authentication
```
mongod --auth --keyFile /data/keyfile
```

## Step 9: Spring Data MongoDB
```java
@Document(collection = "users")
public class User {
    @Id private String id;
    private String name;
}

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByName(String name);
}
```

## Step 10: Monitoring
```bash
mongosh --eval 'db.serverStatus()'
mongosh --eval 'db.currentOp()'
mongostat --port 27017
```
