# Module 58: Data Engineering in Java - Edge Cases & Pitfalls

---

## Pitfall 1: OutOfMemoryError in Batch Processing

### ❌ Wrong
Loading an entire 10GB CSV file into a standard Java `List<String>` or `ArrayList<MyObject>` to process it. The application will instantly crash with an `OutOfMemoryError: Java heap space`.

### ✅ Correct
Always use streaming or chunking when dealing with massive datasets. Use Java `Files.lines()` which returns a lazy `Stream<String>`, or use a framework like Spring Batch which reads, processes, and writes data in manageable chunks (e.g., 1000 records at a time) without exhausting the JVM heap.

---

## Pitfall 2: Trying to use Relational Databases for Big Data Analytics

### ❌ Wrong
Running complex aggregation queries (like computing the daily average user activity over the last 5 years) on your primary transactional PostgreSQL database (OLTP). This will lock tables, spike CPU usage to 100%, and take down the entire user-facing application.

### ✅ Correct
Separate OLTP (Online Transaction Processing) from OLAP (Online Analytical Processing). Export the data via CDC (Change Data Capture) or nightly batches to a dedicated Data Warehouse (like Amazon Redshift or Google BigQuery), which uses columnar storage optimized specifically for heavy read aggregations.

---

## Pitfall 3: Ignoring Schema Evolution in Data Lakes

### ❌ Wrong
Dumping raw JSON files into an AWS S3 Data Lake for years without enforcing a schema or versioning. When data scientists try to analyze the data 3 years later, they find that fields have been renamed, nested structures have changed, and the data is completely unqueryable ("Data Swamp").

### ✅ Correct
Use a schema registry and enforce strict, evolvable serialization formats like Apache Avro or Apache Parquet. These formats embed the schema within the file and support backward/forward compatibility rules.