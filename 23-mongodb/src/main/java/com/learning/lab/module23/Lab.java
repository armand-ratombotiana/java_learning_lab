package com.learning.lab.module23;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.List;

public class Lab {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "learningdb";
    private static final String COLLECTION_NAME = "users";

    public static void main(String[] args) {
        System.out.println("=== Module 23: MongoDB Operations ===");

        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            insertDemo(collection);
            queryDemo(collection);
            updateDemo(collection);
            deleteDemo(collection);
            aggregationDemo(collection);
        }
    }

    static void insertDemo(MongoCollection<Document> collection) {
        System.out.println("\n--- MongoDB Insert ---");
        Document doc = new Document("name", "John Doe")
                .append("email", "john@example.com")
                .append("age", 30)
                .append("skills", Arrays.asList("Java", "MongoDB", "Spring"));
        collection.insertOne(doc);
        System.out.println("Inserted document with ID: " + doc.getObjectId("_id"));
    }

    static void queryDemo(MongoCollection<Document> collection) {
        System.out.println("\n--- MongoDB Query ---");
        Document filter = new Document("name", "John Doe");
        Document result = collection.find(filter).first();
        System.out.println("Found: " + result.toJson());

        Document filter2 = Filters.eq("age", 30);
        collection.find(filter2).forEach(doc -> System.out.println("Age 30: " + doc.getString("name")));
    }

    static void updateDemo(MongoCollection<Document> collection) {
        System.out.println("\n--- MongoDB Update ---");
        Document filter = new Document("name", "John Doe");
        Document update = new Document("$set", new Document("email", "johnnew@example.com"));
        collection.updateOne(filter, update);
        System.out.println("Updated document");

        Document updateMany = new Document("$inc", new Document("age", 1));
        collection.updateMany(new Document("age", new Document("$gte", 25)), updateMany);
        System.out.println("Updated multiple documents");
    }

    static void deleteDemo(MongoCollection<Document> collection) {
        System.out.println("\n--- MongoDB Delete ---");
        Document filter = new Document("name", "John Doe");
        collection.deleteOne(filter);
        System.out.println("Deleted one document");

        collection.deleteMany(new Document("age", new Document("$lt", 18)));
        System.out.println("Deleted multiple documents");
    }

    static void aggregationDemo(MongoCollection<Document> collection) {
        System.out.println("\n--- MongoDB Aggregation ---");
        System.out.println("Aggregation pipeline: match -> group -> sort -> limit");
        System.out.println("Example: $match, $group, $project, $sort, $limit, $skip");
    }
}