# Interview Questions: Multi-Model Databases (Oracle Focus)

## Oracle-Specific Questions
- How does Oracle's multi-model support compare with standalone NoSQL databases?
- Explain Oracle JSON support: `JSON` data type, JSON indexes, and JSON query functions (JSON_VALUE, JSON_QUERY, JSON_TABLE).
- How does Oracle Spatial and Graph compare to Neo4j for graph workloads?
- Explain Oracle's property graph and PGQL for graph analytics.
- Compare Oracle XMLDB with native XML databases for document storage.
- How does Oracle's SODA (Simple Oracle Document Access) compare to MongoDB for document storage?
- Explain Oracle Text and Oracle Search for full-text search vs Elasticsearch.
- When would you use Oracle's multi-model features vs a dedicated multi-model database like ArangoDB?

## Google Cloud / Technical
- Cloud Spanner vs Oracle for multi-model workloads
- Firestore document model vs Oracle SODA
- BigQuery JSON capabilities vs Oracle JSON

## Microsoft / Azure
- Cosmos DB multi-model API vs Oracle's multi-model features
- Azure SQL JSON support vs Oracle JSON
- Azure Search vs Oracle Text

## Amazon / AWS
- DynamoDB document model vs Oracle SODA
- Neptune graph DB vs Oracle Spatial/Graph
- OpenSearch vs Oracle Text search

## Apple
- Multi-model data privacy compliance with Oracle
- Combining JSON + relational for Apple app data

## LeetCode-Style SQL/JSON Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| JSON Extraction | JSON_VALUE | Easy | Dot Notation |
| JSON Aggregation | JSON_ARRAYAGG | Medium | GROUP BY |
| JSON Table | JSON_TABLE | Medium | Lateral View |
| Graph Query | CONNECT BY | Medium | Hierarchical |
| Spatial Query | SDO_WITHIN_DISTANCE | Medium | Geospatial |

## Production Scenarios
- Scenario 1: "Oracle JSON query performing full scan — missing JSON index"
- Scenario 2: "SODA document store growing unbounded — archiving strategy"
- Scenario 3: "Graph query (CONNECT BY) cycling — cycle detection failure"
- Scenario 4: "Oracle Text index corruption — search returning no results"

## Interview Patterns & Tips
- Oracle's multi-model capabilities are increasingly important in interviews
- Know JSON, Spatial, and Graph features to differentiate from other DBs
- Oracle's JSON features (SODA, JSON_TABLE) are common interview topics
- Multi-model DB architects: $140K-$200K
- OCP JSON Oracle certification is available
