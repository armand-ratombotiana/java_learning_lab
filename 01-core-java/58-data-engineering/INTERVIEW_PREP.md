# Module 58: Data Engineering in Java - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the purpose of "Chunk-based processing" in frameworks like Spring Batch?
**Answer**:
When dealing with massive datasets (e.g., a 50GB file containing 10 million rows), loading the entire file into Java's heap memory at once will cause an immediate `OutOfMemoryError`.
Chunk-based processing solves this by reading, processing, and writing data in manageable chunks. If the chunk size is set to 1000, the framework reads 1000 rows into memory, processes them, opens a database transaction, writes all 1000 rows, commits the transaction, and then discards those objects so the Garbage Collector can reclaim the memory. This keeps the memory footprint low and constant, regardless of whether the file has 10 thousand or 10 billion rows.

### Q2: Explain the concept of "Change Data Capture" (CDC).
**Answer**:
CDC is a software design pattern used to determine and track the data that has changed so that action can be taken using the changed data. 
Instead of running a heavy batch query like `SELECT * FROM users WHERE updated_at > yesterday` (which slows down the primary database), a CDC tool (like Debezium) directly monitors the database's internal transaction log (e.g., PostgreSQL's WAL or MySQL's Binlog). Every time a row is inserted, updated, or deleted, the CDC tool captures that raw event and streams it instantly to a message broker (like Apache Kafka). This allows data warehouses or other microservices to stay perfectly in sync with the primary database in real-time with virtually zero performance impact on the primary DB.

### Q3: What is the "Data Swamp" anti-pattern in Data Lakes?
**Answer**:
A Data Lake is designed to store massive amounts of raw data. Because Data Lakes use "Schema-on-read," there are no structural constraints preventing developers from dumping poorly formatted, undocumented, or corrupt data into the lake.
If a company continuously dumps JSON files into AWS S3 without maintaining a Data Catalog, enforcing schema versioning (like Avro), or applying metadata tags, the lake becomes impossible to navigate or query. Data Scientists cannot trust the data quality, and the Data Lake effectively devolves into a useless "Data Swamp."

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Streaming Large Files
**Problem**: An interviewer shows you this Java code used in a nightly ETL job. They tell you it crashes on large files. Why does it crash, and how do you rewrite it using modern Java APIs to fix the memory leak?

```java
public void processLogs(String filePath) throws IOException {
    List<String> allLines = Files.readAllLines(Paths.get(filePath));
    for (String line : allLines) {
        if (line.contains("ERROR")) {
            saveToDatabase(line);
        }
    }
}
```

**Solution**:
`Files.readAllLines()` reads the entire file into a `List<String>` in memory simultaneously. If the file is larger than the JVM heap, it crashes.
**The Fix**: Use `Files.lines()`, which returns a `Stream<String>`. A Stream processes elements lazily, keeping only the current line in memory. It must be wrapped in a `try-with-resources` block to ensure the underlying file handle is closed.

```java
public void processLogs(String filePath) throws IOException {
    // Lazily streams lines, keeping memory usage near zero
    try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
        lines.filter(line -> line.contains("ERROR"))
             .forEach(this::saveToDatabase);
    }
}
```