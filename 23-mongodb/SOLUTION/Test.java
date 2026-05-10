package com.learning.lab.module23.solution;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

public class Test {

    @Test void testDocumentCreation() { Solution.Document doc = new Solution.Document().append("name", "John"); assertEquals("John", doc.getString("name")); }
    @Test void testDocumentAppend() { Solution.Document doc = new Solution.Document().append("a", 1).append("b", 2).append("c", 3); assertNotNull(doc); }
    @Test void testDocumentGetString() { Solution.Document doc = new Solution.Document().append("name", "John"); assertEquals("John", doc.getString("name")); }
    @Test void testDocumentGetInteger() { Solution.Document doc = new Solution.Document().append("age", 30); assertEquals(30, doc.getInteger("age")); }
    @Test void testDocumentGetBoolean() { Solution.Document doc = new Solution.Document().append("active", true); assertTrue(doc.getBoolean("active")); }
    @Test void testDocumentToMap() { Solution.Document doc = new Solution.Document().append("key", "value"); Map<String,Object> map = doc.toMap(); assertEquals("value", map.get("key")); }
    @Test void testDocumentParse() { Solution.Document doc = Solution.Document.parse(Map.of("k", "v")); assertEquals("v", doc.getString("k")); }
    @Test void testInMemoryCollectionInsert() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertOne(new Solution.Document().append("name", "test")); assertEquals(1, col.findAll().size()); }
    @Test void testInMemoryCollectionInsertMany() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertMany(List.of(new Solution.Document().append("n","a"), new Solution.Document().append("n","b"))); assertEquals(2, col.findAll().size()); }
    @Test void testInMemoryCollectionFindAll() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertOne(new Solution.Document().append("n","test")); assertEquals(1, col.findAll().size()); }
    @Test void testInMemoryCollectionFindWithFilter() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertOne(new Solution.Document().append("n","test")); col.insertOne(new Solution.Document().append("n","other")); List<Solution.Document> results = col.find(Solution.Filters.eq("n","test")); assertEquals(1, results.size()); }
    @Test void testInMemoryCollectionFindFirst() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertOne(new Solution.Document().append("n","first")); Solution.Document found = col.findFirst(Solution.Filters.eq("n","first")); assertNotNull(found); }
    @Test void testInMemoryCollectionCount() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertMany(List.of(new Solution.Document(), new Solution.Document())); assertEquals(2, col.countDocuments(null)); }
    @Test void testInMemoryCollectionUpdateOne() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertOne(new Solution.Document().append("n","test")); col.updateOne(Solution.Filters.eq("n","test"), Solution.Updates.set("n","updated")); }
    @Test void testInMemoryCollectionUpdateMany() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertMany(List.of(new Solution.Document().append("a",1), new Solution.Document().append("a",1))); col.updateMany(Solution.Filters.eq("a",1), Solution.Updates.set("a",2)); }
    @Test void testInMemoryCollectionDeleteOne() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertOne(new Solution.Document().append("n","del")); col.deleteOne(Solution.Filters.eq("n","del")); assertEquals(0, col.findAll().size()); }
    @Test void testInMemoryCollectionDeleteMany() { Solution.MongoCollection col = new Solution.InMemoryMongoCollection("test"); col.insertMany(List.of(new Solution.Document().append("a",1), new Solution.Document().append("a",1))); col.deleteMany(Solution.Filters.eq("a",1)); assertEquals(0, col.findAll().size()); }
    @Test void testFiltersEq() { Solution.Bson filter = Solution.Filters.eq("age", 30); assertNotNull(filter.toDocument()); }
    @Test void testFiltersNe() { Solution.Bson filter = Solution.Filters.ne("status", "inactive"); assertNotNull(filter.toDocument()); }
    @Test void testFiltersGt() { Solution.Bson filter = Solution.Filters.gt("price", 100); assertNotNull(filter.toDocument()); }
    @Test void testFiltersLt() { Solution.Bson filter = Solution.Filters.lt("age", 18); assertNotNull(filter.toDocument()); }
    @Test void testFiltersGte() { Solution.Bson filter = Solution.Filters.gte("score", 60); assertNotNull(filter.toDocument()); }
    @Test void testFiltersLte() { Solution.Bson filter = Solution.Filters.lte("qty", 10); assertNotNull(filter.toDocument()); }
    @Test void testFiltersIn() { Solution.Bson filter = Solution.Filters.in("category", List.of("A","B")); assertNotNull(filter.toDocument()); }
    @Test void testFiltersNin() { Solution.Bson filter = Solution.Filters.nin("status", List.of("X","Y")); assertNotNull(filter.toDocument()); }
    @Test void testFiltersAnd() { Solution.Bson filter = Solution.Filters.and(Solution.Filters.eq("a",1), Solution.Filters.eq("b",2)); assertNotNull(filter.toDocument()); }
    @Test void testFiltersOr() { Solution.Bson filter = Solution.Filters.or(Solution.Filters.eq("a",1), Solution.Filters.eq("b",2)); assertNotNull(filter.toDocument()); }
    @Test void testFiltersRegex() { Solution.Bson filter = Solution.Filters.regex("name", ".*"); assertNotNull(filter.toDocument()); }
    @Test void testFiltersExists() { Solution.Bson filter = Solution.Filters.exists("email", true); assertNotNull(filter.toDocument()); }
    @Test void testUpdatesSet() { Solution.Bson update = Solution.Updates.set("age", 25); assertNotNull(update.toDocument()); }
    @Test void testUpdatesUnset() { Solution.Bson update = Solution.Updates.unset("field"); assertNotNull(update.toDocument()); }
    @Test void testUpdatesInc() { Solution.Bson update = Solution.Updates.inc("count", 1); assertNotNull(update.toDocument()); }
    @Test void testUpdatesMul() { Solution.Bson update = Solution.Updates.mul("price", 1.1); assertNotNull(update.toDocument()); }
    @Test void testUpdatesPush() { Solution.Bson update = Solution.Updates.push("tags", "new"); assertNotNull(update.toDocument()); }
    @Test void testUpdatesPull() { Solution.Bson update = Solution.Updates.pull("tags", "old"); assertNotNull(update.toDocument()); }
    @Test void testUpdatesAddToSet() { Solution.Bson update = Solution.Updates.addToSet("items", "item1"); assertNotNull(update.toDocument()); }
    @Test void testUpdatesRename() { Solution.Bson update = Solution.Updates.rename("old", "new"); assertNotNull(update.toDocument()); }
    @Test void testAggregationMatch() { Solution.Aggregation agg = new Solution.Aggregation().match(Solution.Filters.eq("status","active")); assertEquals(1, agg.getStages().size()); }
    @Test void testAggregationGroup() { Solution.Aggregation agg = new Solution.Aggregation().group(Solution.Aggregates.groupBy("status", Map.of("count", Solution.Aggregates.count("count")))); assertEquals(1, agg.getStages().size()); }
    @Test void testAggregationLimit() { Solution.Aggregation agg = new Solution.Aggregation().limit(10); assertEquals(1, agg.getStages().size()); }
    @Test void testAggregationSkip() { Solution.Aggregation agg = new Solution.Aggregation().skip(5); assertEquals(1, agg.getStages().size()); }
    @Test void testAggregationSort() { Solution.Aggregation agg = new Solution.Aggregation().sort(new Solution.BsonDocument().append("name",1)); assertEquals(1, agg.getStages().size()); }
    @Test void testAggregationChaining() { Solution.Aggregation agg = new Solution.Aggregation().match(Solution.Filters.eq("a",1)).group(Solution.Aggregates.groupBy("b", Map.of("c", Solution.Aggregates.count("c")))).limit(5); assertEquals(3, agg.getStages().size()); }
    @Test void testClientSessionStartTransaction() { Solution.ClientSession session = new Solution.ClientSession(); session.startTransaction(); assertTrue(session.isInTransaction()); }
    @Test void testClientSessionCommit() { Solution.ClientSession session = new Solution.ClientSession(); session.startTransaction(); session.commitTransaction(); assertFalse(session.isInTransaction()); }
    @Test void testClientSessionAbort() { Solution.ClientSession session = new Solution.ClientSession(); session.startTransaction(); session.abortTransaction(); assertFalse(session.isInTransaction()); }
    @Test void testIndexAscending() { Solution.Index idx = Solution.Index.ascending("name"); assertTrue(idx.isAscending()); assertFalse(idx.isUnique()); }
    @Test void testIndexDescending() { Solution.Index idx = Solution.Index.descending("date"); assertFalse(idx.isAscending()); }
    @Test void testIndexUnique() { Solution.Index idx = Solution.Index.unique("email"); assertTrue(idx.isUnique()); }
    @Test void testMongoClientGetDatabase() { Solution.MongoClient client = new Solution.MongoClient(); Solution.MongoDatabase db = client.getDatabase("test"); assertNotNull(db); }
    @Test void testMongoDatabaseGetCollection() { Solution.MongoClient client = new Solution.MongoClient(); Solution.MongoDatabase db = client.getDatabase("test"); Solution.MongoCollection col = db.getCollection("users"); assertNotNull(col); }
    @Test void testMongoDatabaseCreateCollection() { Solution.MongoClient client = new Solution.MongoClient(); Solution.MongoDatabase db = client.getDatabase("test"); db.createCollection("new-collection"); }
}