package com.db.mongodb;

import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Sorts;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;

import java.util.Arrays;

/**
 * Demonstrates MongoDB Aggregation Pipeline.
 *
 * Pipeline stages: $match, $group, $sort, $project, $unwind, $lookup.
 *
 * Models an e-commerce dataset: orders with embedded line items.
 */
public class AggregationPipeline {

    static final String CONN_STRING = "mongodb://localhost:27017";
    static final String DB_NAME = "academy";

    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create(CONN_STRING)) {
            MongoDatabase db = client.getDatabase(DB_NAME);
            MongoCollection<Document> orders = db.getCollection("orders");
            orders.drop();

            // Seed data
            orders.insertMany(Arrays.asList(
                new Document("customer", "Alice").append("total", 150.00)
                    .append("items", Arrays.asList(
                        new Document("product", "Mouse").append("qty", 2).append("price", 25.00),
                        new Document("product", "Keyboard").append("qty", 1).append("price", 100.00)
                    )).append("date", "2024-01-15"),
                new Document("customer", "Bob").append("total", 75.00)
                    .append("items", Arrays.asList(
                        new Document("product", "Mouse").append("qty", 1).append("price", 25.00),
                        new Document("product", "USB Cable").append("qty", 5).append("price", 10.00)
                    )).append("date", "2024-02-01"),
                new Document("customer", "Alice").append("total", 200.00)
                    .append("items", Arrays.asList(
                        new Document("product", "Monitor").append("qty", 1).append("price", 200.00)
                    )).append("date", "2024-02-15")
            ));

            System.out.println("=== $match + $group: Total spent per customer ===");
            try (MongoCursor<Document> cursor = orders.aggregate(Arrays.asList(
                    Aggregates.group("$customer", Accumulators.sum("totalSpent", "$total")),
                    Aggregates.sort(Sorts.descending("totalSpent"))
            )).iterator()) {
                while (cursor.hasNext()) {
                    Document d = cursor.next();
                    System.out.printf("  %s: $%.2f%n", d.getString("_id"), d.getDouble("totalSpent"));
                }
            }

            System.out.println("\n=== $unwind + $group: Product quantity sold ===");
            try (MongoCursor<Document> cursor = orders.aggregate(Arrays.asList(
                    Aggregates.unwind("$items"),
                    Aggregates.group("$items.product", Accumulators.sum("qtySold", "$items.qty")),
                    Aggregates.sort(Sorts.descending("qtySold"))
            )).iterator()) {
                while (cursor.hasNext()) {
                    Document d = cursor.next();
                    System.out.printf("  %s: %d sold%n", d.getString("_id"), d.getInteger("qtySold"));
                }
            }

            System.out.println("\n=== $project: Shape output ===");
            try (MongoCursor<Document> cursor = orders.aggregate(Arrays.asList(
                    Aggregates.match(gt("total", 100)),
                    Aggregates.project(new Document("customer", 1)
                        .append("total", 1)
                        .append("itemCount", new Document("$size", "$items"))
                        .append("_id", 0))
            )).iterator()) {
                while (cursor.hasNext()) {
                    System.out.println("  " + cursor.next().toJson());
                }
            }

        } catch (Exception e) {
            System.out.println("MongoDB required: " + e.getMessage());
        }
    }
}
