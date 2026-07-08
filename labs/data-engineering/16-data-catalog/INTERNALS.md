# Data Catalog Internals

## OpenMetadata Architecture
UI (React) + API Server (Java) + Ingestion Framework (Python). Metadata store: MySQL/PostgreSQL. Search: Elasticsearch. Lineage: custom graph database. UI interacts with API; Ingestion Framework runs connectors and pushes metadata via API. Scheduler runs ingestion workflows.

## DataHub Architecture
Frontend (React/TypeScript) + GMS (GraphQL + Metadata Service, Java) + MAE/MCE (Kafka). Metadata store: Neo4j (graph) + Elasticsearch (search). Stream-oriented: emit metadata change events (MCE) to Kafka; GMS processes and stores. Real-time ingestion via event hooks.

## Apache Atlas Architecture
Web UI + Atlas REST API + Kafka (notification) + HBase (storage) + Solr (index). Type System: define entity types (Table, Column, View) with attributes. Classification: tag entities (PII, Sensitive, Public). Lineage: provenance tracking via entity relationships.

## Ingestion Patterns
Push: sources emit metadata events (Kafka, webhook). Pull: crawler fetches metadata on schedule (nightly). Hook: ETL tools report lineage during execution (Airflow callbacks, Spark listeners). Incremental: only changed metadata since last sync (uses watermark or audit timestamps).
