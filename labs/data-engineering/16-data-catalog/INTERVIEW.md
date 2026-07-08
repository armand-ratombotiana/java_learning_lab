# Interview Questions: Data Catalog

### Catalog Concepts
**Q**: What is the difference between technical and business metadata?
**A**: Technical: schema, types, partitions, file sizes. Business: definitions, ownership, PII tags, quality scores, usage context.

### Lineage
**Q**: How is data lineage captured?
**A**: Parsing SQL queries, instrumenting ETL tools, capturing Spark plan, manual annotation. Column-level lineage shows exact transformation paths.

### Tool Selection
**Q**: When would you choose OpenMetadata vs DataHub?
**A**: OpenMetadata: comprehensive all-in-one, active community, extensive integrations. DataHub: strong lineage, stream-oriented, LinkedIn-proven at massive scale.

### Deployment
**Q**: How do you automate metadata ingestion?
**A**: Crawlers on schedule, event-driven (Kafka), ETL hooks (Airflow callbacks), incremental updates via REST API. All major tools support scheduled ingestion.
