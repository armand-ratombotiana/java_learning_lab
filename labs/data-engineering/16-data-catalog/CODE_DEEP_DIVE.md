# Code Deep Dive: Data Catalog

See Java source files in src/main/java/com/dataeng/sixteen/ for:
- MetadataIngestionClient.java: REST API client for pushing metadata
- LineageBuilder.java: Build and submit data lineage relationships

Key patterns:
```java
// Push metadata via REST API
MetadataIngestionClient client = new MetadataIngestionClient(catalogUrl);
DatasetMetadata metadata = new DatasetMetadata(
    "my_database", "public", "orders",
    List.of(new ColumnMetadata("id", "BIGINT", "Primary key"), ...),
    "Order transactions fact table",
    "ETL Pipeline");
client.ingestDataset(metadata);

// Build lineage
LineageBuilder lineage = new LineageBuilder()
    .addSource("db.public.orders_raw")
    .addTransformation("dbt_etl_job_v2")
    .addTarget("analytics.fact_orders");
client.submitLineage(lineage.build());
```
