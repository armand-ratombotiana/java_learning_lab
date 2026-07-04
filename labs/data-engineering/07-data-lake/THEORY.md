# Data Lake Theory

## Medallion Architecture
- **Bronze**: Raw immutable data, original format, schema-on-read
- **Silver**: Cleaned, deduplicated, schema enforced
- **Gold**: Aggregated, business-ready, denormalized

## Table Formats
| Feature | Delta Lake | Iceberg | Hudi |
|---------|-----------|---------|------|
| ACID | Yes | Yes | Yes |
| Time Travel | Yes | Yes | Yes |
| Schema Evolution | Yes | Yes | Yes |
| Upsert | Yes | Yes | Yes |
