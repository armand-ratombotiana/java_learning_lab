# How Data Catalog Works

1. Ingestion framework connects to data sources via connectors
2. Crawler extracts metadata: schema, row counts, partitions, descriptions
3. Metadata formatted and pushed to catalog API
4. Catalog stores in metadata DB and indexes in search engine
5. Lineage captured via SQL parsers, ETL tool hooks, or manual annotation
6. Classification engine tags assets (PII, sensitive, public)
7. Business glossary terms linked to technical assets
8. Users discover data via search and browse
9. Impact analysis shows downstream dependencies
10. Governance workflows manage changes and approvals
