# Visual Guide to Data Catalog

```
OpenMetadata Architecture:
[UI (React)] -> [API Server (Java)] -> [MySQL/PostgreSQL]
                   |                      [Elasticsearch]
                   |
[Ingestion Framework (Python)]
    |
[Connectors: DB, Lake, ETL, BI]

Data Lineage Example:
[Orders DB] -> [ETL Job] -> [Order Fact Table] -> [Revenue Dashboard]
    |                      |                       |
[Customers DB] -> [ETL] -> [Customer Dim] -> [Customer 360 Dashboard]

Business Glossary:
Term: 'Active Customer'
Definition: 'Customer with at least 1 transaction in last 90 days'
Domain: Sales
Owner: Sales Data Team
Synonyms: Current Customer
```
