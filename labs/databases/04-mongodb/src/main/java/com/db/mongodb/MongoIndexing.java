package com.db.mongodb;

import com.mongodb.client.*;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

/**
 * Demonstrates MongoDB indexing strategies.
 *
 * Covers: single-field, compound, text, TTL, and unique indexes.
 * Shows how indexes affect query performance via explain().
 */
public class MongoIndexing {

    static final String CONN_STRING = "mongodb://localhost:27017";
    static final String DB_NAME = "academy";

    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create(CONN_STRING)) {
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> coll = db.getCollection("logs");
            coll.drop();

            // Insert sample documents
            for (int i = 0; i < 1000; i++) {
                coll.insertOne(new Document("level", i % 3 == 0 ? "ERROR" : "INFO")
                    .append("message", "Log entry #" + i)
                    .append("timestamp", System.currentTimeMillis() - i * 1000)
                    .append("userId", "user_" + (i % 100)));
            }
            System.out.println("Inserted 1000 log documents");

            System.out.println("\n=== 1. Single-field index on 'level' ===");
            coll.createIndex(Indexes.ascending("level"));
            Document explain = coll.find(new Document("level", "ERROR"))
                .explain();
            System.out.println("  Query plan: " + explain.get("queryPlanner"));

            System.out.println("\n=== 2. Compound index on (level, timestamp) ===");
            coll.createIndex(Indexes.ascending("level", "timestamp"));
            Document explain2 = coll.find(new Document("level", "INFO")
                    .append("timestamp", new Document("$gte", System.currentTimeMillis() - 50000)))
                .sort(new Document("timestamp", -1))
                .explain();
            System.out.println("  Covered query possible: " +
                explain2.get("queryPlanner", Document.class));

            System.out.println("\n=== 3. TTL index (auto-expire after 1 hour) ===");
            coll.createIndex(Indexes.ascending("timestamp"),
                new IndexOptions().expireAfter(3600L, java.util.concurrent.TimeUnit.SECONDS));
            System.out.println("  TTL index created on 'timestamp' — documents expire after 1h");

            System.out.println("\n=== 4. Unique index on 'message' ===");
            try {
                coll.createIndex(Indexes.ascending("message"),
                    new IndexOptions().unique(true));
                System.out.println("  Unique index created");
            } catch (Exception e) {
                System.out.println("  Duplicate values exist — unique index would fail: " + e.getMessage());
            }

            System.out.println("\n=== Current indexes ===");
            for (Document idx : coll.listIndexes()) {
                System.out.println("  " + idx.toJson());
            }

        } catch (Exception e) {
            System.out.println("MongoDB required: " + e.getMessage());
        }
    }
}
