# Step-by-Step: Working with Data Catalog

1. Choose catalog platform: OpenMetadata, DataHub, or Atlas based on ecosystem
2. Deploy using Docker Compose or Helm chart on Kubernetes
3. Configure metadata database (MySQL/PostgreSQL) and search index (Elasticsearch)
4. Set up ingestion connectors for databases, data lakes, and ETL tools
5. Run initial metadata harvest to populate the catalog
6. Review harvested metadata: verify completeness and accuracy
7. Create business glossary: terms, categories, ownership, definitions
8. Build lineage for critical pipelines using SQL parser or ETL hooks
9. Set up scheduled ingestion for continuous metadata freshness
10. Enable data discovery search and train users
