package com.db.mongodb;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

/**
 * Demonstrates MongoDB CRUD operations using the Java Sync Driver.
 *
 * Concepts: insertOne, insertMany, find, updateOne, deleteOne,
 *           filtering with Filters helper, projections.
 *
 * Requires: MongoDB running on localhost:27017
 */
public class MongoCrudDemo {

    static final String CONN_STRING = "mongodb://localhost:27017";
    static final String DB_NAME = "academy";
    static final String COLLECTION = "users";

    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create(CONN_STRING)) {
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> coll = db.getCollection(COLLECTION);
            coll.drop(); // fresh start

            System.out.println("=== Insert One ===");
            Document alice = new Document("name", "Alice")
                .append("email", "alice@example.com")
                .append("age", 30)
                .append("roles", java.util.List.of("admin", "editor"));
            coll.insertOne(alice);
            System.out.println("  Inserted: " + alice.getObjectId("_id"));

            System.out.println("\n=== Insert Many ===");
            coll.insertMany(java.util.List.of(
                new Document("name", "Bob").append("email", "bob@example.com").append("age", 25),
                new Document("name", "Carol").append("email", "carol@example.com").append("age", 35)
            ));
            System.out.println("  Inserted 2 documents");

            System.out.println("\n=== Find All ===");
            try (MongoCursor<Document> cursor = coll.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    System.out.printf("  %s | %s | %d%n",
                        doc.getString("name"), doc.getString("email"), doc.getInteger("age"));
                }
            }

            System.out.println("\n=== Find with Filter (age > 28) ===");
            try (MongoCursor<Document> cursor = coll.find(Filters.gt("age", 28)).iterator()) {
                cursor.forEachRemaining(d -> System.out.println("  " + d.getString("name")));
            }

            System.out.println("\n=== Update One ===");
            coll.updateOne(Filters.eq("name", "Bob"), Updates.set("age", 26));
            Document bob = coll.find(Filters.eq("name", "Bob")).first();
            System.out.println("  Bob age updated to: " + bob.getInteger("age"));

            System.out.println("\n=== Delete One ===");
            coll.deleteOne(Filters.eq("name", "Carol"));
            long count = coll.countDocuments();
            System.out.println("  Documents remaining: " + count);

        } catch (Exception e) {
            System.out.println("MongoDB required: " + e.getMessage());
        }
    }
}
