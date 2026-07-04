# Architecture

```
[Governance Platform]
  [Catalog] [Lineage] [Policy Engine] [Compliance]
       |        |          |              |
  [Metadata Storage: Postgres, Neo4j, Elasticsearch, Kafka]
       |
  [Collection: Spark Listeners, Kafka Connect, JDBC, APIs]
```
