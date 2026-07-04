# Code Deep Dive: MongoDB with Java

## Synchronous CRUD

```java
public class UserService {
    private final MongoCollection<User> users;
    private final CodecRegistry pojoCodecRegistry;

    public UserService(MongoClient client) {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
            .automatic(true)
            .build();
        this.pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(pojoCodecProvider)
        );
        this.users = client.getDatabase("myapp")
            .getCollection("users", User.class)
            .withCodecRegistry(pojoCodecRegistry);
    }

    public User createUser(User user) {
        users.insertOne(user);
        return user;
    }

    public User findById(String id) {
        return users.find(Filters.eq("_id", new ObjectId(id))).first();
    }

    public List<User> findByCity(String city) {
        return users.find(Filters.eq("address.city", city))
            .sort(Sorts.descending("createdAt"))
            .limit(50)
            .into(new ArrayList<>());
    }

    public long activateUsers(List<String> ids) {
        UpdateResult result = users.updateMany(
            Filters.in("_id", ids.stream().map(ObjectId::new).toList()),
            Updates.set("status", "active")
        );
        return result.getModifiedCount();
    }
}
```

## POJO Model

```java
@Document(collection = "users")
public class User {
    @BsonProperty("_id")
    private ObjectId id;
    private String name;
    private String email;
    private Address address;
    private List<String> tags;
    private Instant createdAt;

    // embedded document
    public static class Address {
        private String city;
        private String street;
        private String zip;
        // getters/setters
    }
    // getters/setters
}
```

## Aggregation Pipeline

```java
public List<OrderSummary> monthlyRevenue(int year) {
    List<Bson> pipeline = Arrays.asList(
        Aggregates.match(Filters.and(
            Filters.gte("createdAt", LocalDate.of(year, 1, 1)),
            Filters.lt("createdAt", LocalDate.of(year + 1, 1, 1)),
            Filters.eq("status", "completed")
        )),
        Aggregates.group(
            Accumulators.sum("totalRevenue", "$total"),
            Accumulators.avg("averageOrder", "$total"),
            Accumulators.sum("orderCount", 1)
        ),
        Aggregates.sort(Sorts.ascending("_id")),
        Aggregates.project(Projections.fields(
            Projections.include("totalRevenue", "averageOrder", "orderCount"),
            Projections.computed("month",
                new Document("$dateToString",
                    new Document("format", "%Y-%m").append("date", "$_id")))
        ))
    );

    return orders.aggregate(pipeline, OrderSummary.class)
        .into(new ArrayList<>());
}
```

## Change Streams

```java
public void watchUserChanges() {
    ChangeStreamPublisher<User> publisher = users.watch()
        .fullDocument(FullDocument.UPDATE_LOOKUP)
        .batchSize(100);

    publisher.subscribe(new Subscriber<ChangeStreamDocument<User>>() {
        @Override
        public void onNext(ChangeStreamDocument<User> change) {
            System.out.printf("Operation: %s, Document: %s%n",
                change.getOperationType(),
                change.getFullDocument());
            // Invalidate cache, send notification, etc.
        }
    });
}
```

## Transactions

```java
public void transferFunds(String fromId, String toId, BigDecimal amount) {
    try (ClientSession session = client.startSession()) {
        session.startTransaction(TransactionOptions.builder()
            .writeConcern(WriteConcern.MAJORITY)
            .readConcern(ReadConcern.SNAPSHOT)
            .build());
        try {
            accounts.updateOne(session,
                Filters.eq("_id", fromId),
                Updates.inc("balance", amount.negate()));
            accounts.updateOne(session,
                Filters.eq("_id", toId),
                Updates.inc("balance", amount));

            session.commitTransaction();
        } catch (Exception e) {
            session.abortTransaction();
            throw e;
        }
    }
}
```
