package com.learning.lab.module23.solution;

import java.util.*;
import java.time.LocalDateTime;

public class Solution {

    // MongoDB Document
    public static class Document {
        private final Map<String, Object> fields = new HashMap<>();

        public Document append(String key, Object value) {
            fields.put(key, value);
            return this;
        }

        public Object get(String key) {
            return fields.get(key);
        }

        public String getString(String key) {
            return (String) fields.get(key);
        }

        public Integer getInteger(String key) {
            return (Integer) fields.get(key);
        }

        public Long getLong(String key) {
            return (Long) fields.get(key);
        }

        public Boolean getBoolean(String key) {
            return (Boolean) fields.get(key);
        }

        public List<?> getList(String key) {
            return (List<?>) fields.get(key);
        }

        public Map<String, Object> toMap() {
            return new HashMap<>(fields);
        }

        public static Document parse(Map<String, Object> map) {
            Document doc = new Document();
            doc.fields.putAll(map);
            return doc;
        }
    }

    // MongoDB Collection
    public interface MongoCollection {
        void insertOne(Document document);
        void insertMany(List<Document> documents);
        List<Document> find(Bson filter);
        List<Document> findAll();
        Document findFirst(Bson filter);
        long countDocuments(Bson filter);
        void updateOne(Bson filter, Bson update);
        void updateMany(Bson filter, Bson update);
        void deleteOne(Bson filter);
        void deleteMany(Bson filter);
    }

    // Bson Filter
    public interface Bson {
        Map<String, Object> toDocument();
    }

    public static class BsonDocument implements Bson {
        private final Map<String, Object> fields = new HashMap<>();

        public BsonDocument append(String key, Object value) {
            fields.put(key, value);
            return this;
        }

        @Override
        public Map<String, Object> toDocument() {
            return new HashMap<>(fields);
        }
    }

    // Filter Builders
    public static class Filters {
        public static Bson eq(String field, Object value) {
            return new BsonDocument().append(field, value);
        }

        public static Bson ne(String field, Object value) {
            return new BsonDocument().append(field + "$ne", value);
        }

        public static Bson gt(String field, Object value) {
            return new BsonDocument().append(field + "$gt", value);
        }

        public static Bson gte(String field, Object value) {
            return new BsonDocument().append(field + "$gte", value);
        }

        public static Bson lt(String field, Object value) {
            return new BsonDocument().append(field + "$lt", value);
        }

        public static Bson lte(String field, Object value) {
            return new BsonDocument().append(field + "$lte", value);
        }

        public static Bson in(String field, List<?> values) {
            return new BsonDocument().append(field + "$in", values);
        }

        public static Bson nin(String field, List<?> values) {
            return new BsonDocument().append(field + "$nin", values);
        }

        public static Bson and(Bson... filters) {
            return new BsonDocument().append("$and", Arrays.stream(filters).map(Bson::toDocument).toList());
        }

        public static Bson or(Bson... filters) {
            return new BsonDocument().append("$or", Arrays.stream(filters).map(Bson::toDocument).toList());
        }

        public static Bson not(Bson filter) {
            return new BsonDocument().append("$not", filter.toDocument());
        }

        public static Bson exists(String field, boolean exists) {
            return new BsonDocument().append(field + "$exists", exists);
        }

        public static Bson regex(String field, String pattern) {
            return new BsonDocument().append(field, new BsonDocument().append("$regex", pattern));
        }
    }

    // Update Builders
    public static class Updates {
        public static Bson set(String field, Object value) {
            return new BsonDocument().append("$set", Map.of(field, value));
        }

        public static Bson unset(String field) {
            return new BsonDocument().append("$unset", Map.of(field, ""));
        }

        public static Bson inc(String field, Number value) {
            return new BsonDocument().append("$inc", Map.of(field, value));
        }

        public static Bson mul(String field, Number value) {
            return new BsonDocument().append("$mul", Map.of(field, value));
        }

        public static Bson rename(String oldName, String newName) {
            return new BsonDocument().append("$rename", Map.of(oldName, newName));
        }

        public static Bson push(String field, Object value) {
            return new BsonDocument().append("$push", Map.of(field, value));
        }

        public static Bson pushAll(String field, List<?> values) {
            return new BsonDocument().append("$push", Map.of(field, new BsonDocument().append("$each", values)));
        }

        public static Bson pull(String field, Object value) {
            return new BsonDocument().append("$pull", Map.of(field, value));
        }

        public static Bson addToSet(String field, Object value) {
            return new BsonDocument().append("$addToSet", Map.of(field, value));
        }

        public static Bson currentDate(String field) {
            return new BsonDocument().append("$currentDate", Map.of(field, true));
        }
    }

    // In-memory collection implementation
    public static class InMemoryMongoCollection implements MongoCollection {
        private final String name;
        private final List<Document> documents = new ArrayList<>();

        public InMemoryMongoCollection(String name) {
            this.name = name;
        }

        @Override
        public void insertOne(Document document) {
            documents.add(document);
            System.out.println("Inserted document into " + name);
        }

        @Override
        public void insertMany(List<Document> documents) {
            this.documents.addAll(documents);
            System.out.println("Inserted " + documents.size() + " documents into " + name);
        }

        @Override
        public List<Document> find(Bson filter) {
            if (filter == null) return findAll();
            Map<String, Object> filterMap = filter.toDocument();
            List<Document> results = new ArrayList<>();
            for (Document doc : documents) {
                if (matchesFilter(doc, filterMap)) {
                    results.add(doc);
                }
            }
            return results;
        }

        private boolean matchesFilter(Document doc, Map<String, Object> filter) {
            for (Map.Entry<String, Object> entry : filter.entrySet()) {
                if (entry.getKey().startsWith("$")) continue;
                if (!Objects.equals(doc.get(entry.getKey()), entry.getValue())) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public List<Document> findAll() {
            return new ArrayList<>(documents);
        }

        @Override
        public Document findFirst(Bson filter) {
            List<Document> results = find(filter);
            return results.isEmpty() ? null : results.get(0);
        }

        @Override
        public long countDocuments(Bson filter) {
            return find(filter).size();
        }

        @Override
        public void updateOne(Bson filter, Bson update) {
            List<Document> results = find(filter);
            if (!results.isEmpty()) {
                System.out.println("Updated 1 document in " + name);
            }
        }

        @Override
        public void updateMany(Bson filter, Bson update) {
            List<Document> results = find(filter);
            System.out.println("Updated " + results.size() + " documents in " + name);
        }

        @Override
        public void deleteOne(Bson filter) {
            List<Document> results = find(filter);
            if (!results.isEmpty()) {
                documents.remove(results.get(0));
                System.out.println("Deleted 1 document from " + name);
            }
        }

        @Override
        public void deleteMany(Bson filter) {
            List<Document> results = find(filter);
            documents.removeAll(results);
            System.out.println("Deleted " + results.size() + " documents from " + name);
        }
    }

    // Aggregation
    public static class Aggregation {
        private final List<Bson> stages = new ArrayList<>();

        public Aggregation match(Bson filter) {
            stages.add(new BsonDocument().append("$match", filter.toDocument()));
            return this;
        }

        public Aggregation group(Bson group) {
            stages.add(new BsonDocument().append("$group", group.toDocument()));
            return this;
        }

        public Aggregation project(Bson projection) {
            stages.add(new BsonDocument().append("$project", projection.toDocument()));
            return this;
        }

        public Aggregation sort(Bson sort) {
            stages.add(new BsonDocument().append("$sort", sort.toDocument()));
            return this;
        }

        public Aggregation limit(int count) {
            stages.add(new BsonDocument().append("$limit", count));
            return this;
        }

        public Aggregation skip(int count) {
            stages.add(new BsonDocument().append("$skip", count));
            return this;
        }

        public List<Bson> getStages() {
            return new ArrayList<>(stages);
        }
    }

    // Aggregation Builders
    public static class Aggregates {
        public static Bson groupBy(String idField, Map<String, Object> accumulators) {
            Map<String, Object> group = new HashMap<>();
            group.put("_id", "$" + idField);
            group.putAll(accumulators);
            return new BsonDocument().append("$group", group);
        }

        public static Bson count(String outputField) {
            return new BsonDocument().append(outputField, new BsonDocument().append("$sum", 1));
        }

        public static Bson sum(String field) {
            return new BsonDocument().append("$sum", "$" + field);
        }

        public static Bson avg(String field) {
            return new BsonDocument().append("$avg", "$" + field);
        }

        public static Bson min(String field) {
            return new BsonDocument().append("$min", "$" + field);
        }

        public static Bson max(String field) {
            return new BsonDocument().append("$max", "$" + field);
        }

        public static Bson push(String field) {
            return new BsonDocument().append("$push", "$" + field);
        }
    }

    // MongoDB Client
    public static class MongoClient {
        private final Map<String, MongoCollection> collections = new HashMap<>();

        public MongoDatabase getDatabase(String name) {
            return new MongoDatabase(name, collections);
        }

        public void close() {
            System.out.println("MongoDB client closed");
        }
    }

    public static class MongoDatabase {
        private final String name;
        private final Map<String, MongoCollection> collections;

        public MongoDatabase(String name, Map<String, MongoCollection> collections) {
            this.name = name;
            this.collections = collections;
        }

        public MongoCollection getCollection(String name) {
            return collections.computeIfAbsent(name, n -> new InMemoryMongoCollection(name));
        }

        public void createCollection(String name) {
            collections.put(name, new InMemoryMongoCollection(name));
            System.out.println("Created collection: " + name);
        }

        public void drop() {
            collections.clear();
            System.out.println("Dropped database: " + name);
        }
    }

    // Transaction
    public static class ClientSession {
        private boolean inTransaction = false;

        public void startTransaction() {
            inTransaction = true;
            System.out.println("Transaction started");
        }

        public void commitTransaction() {
            inTransaction = false;
            System.out.println("Transaction committed");
        }

        public void abortTransaction() {
            inTransaction = false;
            System.out.println("Transaction aborted");
        }

        public boolean isInTransaction() {
            return inTransaction;
        }
    }

    // Index
    public static class Index {
        private final String field;
        private final boolean unique;
        private final boolean ascending;

        public Index(String field, boolean unique, boolean ascending) {
            this.field = field;
            this.unique = unique;
            this.ascending = ascending;
        }

        public String getField() { return field; }
        public boolean isUnique() { return unique; }
        public boolean isAscending() { return ascending; }

        public static Index ascending(String field) {
            return new Index(field, false, true);
        }

        public static Index descending(String field) {
            return new Index(field, false, false);
        }

        public static Index unique(String field) {
            return new Index(field, true, true);
        }
    }

    public static void demonstrateMongoDB() {
        System.out.println("=== MongoDB Client ===");
        MongoClient client = new MongoClient();
        MongoDatabase db = client.getDatabase("test");

        System.out.println("\n=== Collection Operations ===");
        MongoCollection users = db.getCollection("users");

        System.out.println("\n=== Insert Documents ===");
        Document user1 = new Document()
            .append("_id", "user1")
            .append("name", "John Doe")
            .append("email", "john@test.com")
            .append("age", 30)
            .append("active", true)
            .append("createdAt", LocalDateTime.now().toString());
        users.insertOne(user1);

        Document user2 = new Document()
            .append("_id", "user2")
            .append("name", "Jane Smith")
            .append("email", "jane@test.com")
            .append("age", 25)
            .append("active", false);
        users.insertOne(user2);

        users.insertMany(List.of(
            new Document().append("name", "Bob").append("age", 35),
            new Document().append("name", "Alice").append("age", 28)
        ));

        System.out.println("\n=== Query Documents ===");
        List<Document> allUsers = users.findAll();
        System.out.println("Total users: " + allUsers.size());

        List<Document> activeUsers = users.find(Filters.eq("active", true));
        System.out.println("Active users: " + activeUsers.size());

        Document youngUser = users.findFirst(Filters.lt("age", 30));
        System.out.println("First user under 30: " + (youngUser != null ? youngUser.getString("name") : "none"));

        System.out.println("\n=== Update Documents ===");
        users.updateOne(Filters.eq("_id", "user1"), Updates.set("age", 31));
        users.updateMany(Filters.eq("active", false), Updates.set("active", true));

        System.out.println("\n=== Delete Documents ===");
        users.deleteOne(Filters.eq("_id", "user2"));
        System.out.println("Users after deletion: " + users.findAll().size());

        System.out.println("\n=== Aggregation ===");
        Aggregation pipeline = new Aggregation()
            .match(Filters.gte("age", 25))
            .group(Aggregates.groupBy("age", Map.of("count", Aggregates.count("count"))))
            .sort(new BsonDocument().append("age", 1))
            .limit(10);
        System.out.println("Aggregation stages: " + pipeline.getStages().size());

        System.out.println("\n=== Indexes ===");
        Index idx1 = Index.ascending("email");
        Index idx2 = Index.unique("username");
        Index idx3 = Index.descending("createdAt");
        System.out.println("Created indexes: " + idx1.getField() + ", " + idx2.getField() + ", " + idx3.getField());

        System.out.println("\n=== Transactions ===");
        ClientSession session = new ClientSession();
        session.startTransaction();
        session.commitTransaction();

        client.close();
    }

    public static void main(String[] args) {
        demonstrateMongoDB();
    }
}