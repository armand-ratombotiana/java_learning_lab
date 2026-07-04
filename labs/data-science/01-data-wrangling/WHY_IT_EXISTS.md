# Why Data Wrangling Exists

Raw data is never analysis-ready. Data wrangling exists because real-world data arrives dirty, incomplete, inconsistent, and unstructured. The discipline formalizes the process of transforming raw observations into clean, structured datasets that machine learning algorithms and statistical models can consume.

## The Gap It Bridges

- **Source systems** produce data for operational (not analytical) purposes
- **Human input** introduces typos, missing fields, free-text variability
- **Merged sources** create schema mismatches, duplicate keys, conflicting formats
- **Time drift** changes schemas, units, and conventions across system versions

## Java Context

In the Java ecosystem, wrangling is done via libraries like Tablesaw, Smile, and Apache Commons CSV rather than Python's pandas. The JVM adds strong typing, which requires explicit schema definitions before data can be loaded — catching type errors at parse time rather than silently corrupting columns.

```java
// Schema enforcement at parse time — a Java advantage
CsvSchema schema = CsvSchema.builder()
    .addColumn("age", CsvSchema.ColumnType.INTEGER)
    .addColumn("salary", CsvSchema.ColumnType.DOUBLE)
    .addColumn("name", CsvSchema.ColumnType.STRING)
    .build();
DataFrame df = DataFrame.read().csv("data.csv", schema);
```

Without this step, a column of age strings would be silently treated as strings. With schema enforcement, mixed types immediately raise parse exceptions — data wrangling as a proactive gatekeeper.
