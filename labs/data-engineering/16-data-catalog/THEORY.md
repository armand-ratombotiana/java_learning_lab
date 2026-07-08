# Data Catalog Theory

## Metadata Types
Technical: schema, column types, partitions, file sizes, row counts, update frequency. Business: definitions, ownership, data domain, PII classification, quality scores, usage context. Operational: pipeline runs, freshness, error rates, access logs. All three types are critical for comprehensive data management.

## Data Discovery
Search: full-text, faceted, filtered. Browse: by domain, database, owner, tag. Collaboration: ratings, comments, ownership, usage stats. Data preview: sample rows, statistics, quality badges. Discovery reduces time spent finding data from days to seconds.

## Data Lineage
Table-level: which tables feed which. Column-level: exactly how columns are transformed. Capture methods: SQL parsing (Calcite, jOOQ), Spark listener, ETL tool hooks, manual annotation. Column-level lineage is essential for impact analysis (which reports will break if this column changes?).

## Business Glossary
Terms: canonical business definitions. Categories: hierarchical groupings. Relationships: synonym, parent-child, related. Stewards: responsible owners. Tags: domain, compliance, PII. Glossary bridges gap between technical metadata (schema) and business understanding (meaning).
