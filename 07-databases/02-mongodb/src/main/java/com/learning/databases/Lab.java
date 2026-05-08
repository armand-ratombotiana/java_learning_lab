package com.learning.databases;

public class Lab {

    public static void main(String[] args) {
        System.out.println("=== MongoDB Concepts ===\n");

        demonstrateDocumentModel();
        demonstrateCRUD();
        demonstrateAggregation();
        demonstrateIndexes();
        demonstrateReplication();
        demonstrateSharding();
    }

    private static void demonstrateDocumentModel() {
        System.out.println("--- Document Model ---");
        System.out.println("Database -> Collections -> Documents (BSON)");
        System.out.println("Document = JSON-like structure with dynamic schema");
        System.out.println();
        System.out.println("Example document:");
        System.out.println("{");
        System.out.println("  _id: ObjectId(\"...\"),");
        System.out.println("  email: \"alice@example.com\",");
        System.out.println("  profile: { name: \"Alice\", age: 30 },");
        System.out.println("  tags: [\"dev\", \"java\"],");
        System.out.println("  address: { city: \"NYC\", zip: \"10001\" }");
        System.out.println("}");
        System.out.println();
        System.out.println("Embedding vs Referencing:");
        System.out.println("  Embed: one-to-few (addresses within user document)");
        System.out.println("  Reference: one-to-many (orders referencing user _id)");
    }

    private static void demonstrateCRUD() {
        System.out.println("\n--- CRUD Operations ---");
        System.out.println("CREATE: db.users.insertOne({ name: \"Alice\", role: \"admin\" })");
        System.out.println("READ:   db.users.find({ role: \"admin\" }).limit(10).sort({ name: 1 })");
        System.out.println("UPDATE: db.users.updateOne(");
        System.out.println("    { _id: ObjectId(...) },");
        System.out.println("    { $set: { role: \"superadmin\" }, $push: { tags: \"lead\" } })");
        System.out.println("DELETE: db.users.deleteMany({ status: \"inactive\" })");
        System.out.println();
        System.out.println("Operators: $inc, $unset, $rename, $addToSet, $pull, $elemMatch");
    }

    private static void demonstrateAggregation() {
        System.out.println("\n--- Aggregation Pipeline ---");
        System.out.println("db.orders.aggregate([");
        System.out.println("  { $match: { status: \"shipped\" } },");
        System.out.println("  { $group: { _id: \"$customer\", total: { $sum: \"$amount\" } } },");
        System.out.println("  { $sort: { total: -1 } },");
        System.out.println("  { $limit: 10 },");
        System.out.println("  { $lookup: { from: \"customers\", localField: \"_id\",");
        System.out.println("      foreignField: \"_id\", as: \"customer_info\" } }");
        System.out.println("])");
        System.out.println();
        System.out.println("Stages: $match, $group, $sort, $project, $unwind, $lookup");
        System.out.println("  $bucket, $facet, $addFields, $replaceRoot");
    }

    private static void demonstrateIndexes() {
        System.out.println("\n--- Index Types ---");
        System.out.println("Single Field      -> db.users.createIndex({ email: 1 })");
        System.out.println("Compound          -> db.users.createIndex({ status: 1, created: -1 })");
        System.out.println("Multikey          -> db.posts.createIndex({ tags: 1 }) (arrays)");
        System.out.println("Text              -> db.articles.createIndex({ body: \"text\" })");
        System.out.println("Geospatial        -> db.places.createIndex({ location: \"2dsphere\" })");
        System.out.println("TTL               -> auto-delete after TTL (sessions, logs)");
        System.out.println();
        System.out.println("Compound indexes support prefix matching");
        System.out.println("ESR rule: Equality, Sort, Range for optimal compound index order");
    }

    private static void demonstrateReplication() {
        System.out.println("\n--- Replication (Replica Sets) ---");
        System.out.println("Primary   -> Accepts all writes");
        System.out.println("Secondary -> Replicates from primary (can serve reads)");
        System.out.println("Arbiter   -> Voting only, no data (for odd member count)");
        System.out.println();
        System.out.println("Write concern: w:1 (default), w:'majority', w:3");
        System.out.println("Read preference: primary (default), primaryPreferred,");
        System.out.println("  secondary, secondaryPreferred, nearest");
        System.out.println("Election: if primary fails, secondary becomes primary (Raft-based)");
    }

    private static void demonstrateSharding() {
        System.out.println("\n--- Sharding (Horizontal Scaling) ---");
        System.out.println("Shard       -> Each holds a subset of data");
        System.out.println("Config Server-> Stores cluster metadata");
        System.out.println("Mongos      -> Query router (application connects to mongos)");
        System.out.println();
        System.out.println("Shard key: { customer_id: 1 }");
        System.out.println("  Hashed sharding  -> even distribution");
        System.out.println("  Ranged sharding  -> data locality by key range");
        System.out.println("  Zone sharding    -> geo-aware distribution");
        System.out.println("Chunks default size: 64MB (auto-splits/splits)");
    }
}
