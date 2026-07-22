# Interview Questions: MongoDB (Oracle Comparison)

## Oracle-Specific Questions
- Compare MongoDB's document model with Oracle's relational model — when would you use each?
- How does MongoDB's sharding compare to Oracle's partitioning and RAC for horizontal scaling?
- Compare MongoDB indexes (single field, compound, multikey, text, geospatial) with Oracle indexes.
- How does MongoDB's aggregation pipeline compare to Oracle SQL for analytics?
- Explain MongoDB transactions (multi-document ACID) vs Oracle transactions — what are the limitations?
- How does MongoDB replication (replica sets) compare to Oracle Data Guard?
- Compare MongoDB WiredTiger storage engine with Oracle's storage architecture.
- Oracle to MongoDB migration strategies — when does it make sense?

## Google Cloud / Technical
- Cloud Firestore vs MongoDB for serverless document storage
- Cloud Spanner as a globally distributed alternative to MongoDB
- Atlas (MongoDB Cloud) vs OCI for database deployment

## Microsoft / Azure
- Azure Cosmos DB (MongoDB API) vs native MongoDB
- Azure MongoDB migration from Oracle via CDC
- Azure Data Factory for Oracle to MongoDB ETL

## Amazon / AWS
- Amazon DocumentDB (MongoDB-compatible) vs native MongoDB
- AWS DMS for Oracle to DocumentDB migration
- MongoDB Atlas on AWS vs Oracle RDS

## Apple
- MongoDB for Apple's cloud service data storage
- Data privacy: MongoDB field-level encryption for PII

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN (vs $lookup) |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | $lookup + $match |
| LC 184 | Department Highest Salary | Medium | $group + $max |
| LC 185 | Department Top Three Salaries | Hard | $group + $push |
| LC 262 | Trips and Users | Hard | $lookup + $match |
| LC 601 | Human Traffic of Stadium | Hard | $lookup + $group |

## Production Scenarios
- Scenario 1: "Shard key hotspot causing 10x latency on one shard"
- Scenario 2: "WiredTiger cache pressure causing write stalls"
- Scenario 3: "Replica set election causing write availability gap"
- Scenario 4: "Aggregation pipeline memory limit exceeded"

## Interview Patterns & Tips
- Oracle interviews ask MongoDB to evaluate Polyglot Persistence knowledge
- Be ready to compare Oracle RAC with MongoDB sharding for scalability
- Know when to use Oracle vs MongoDB in a microservices architecture
- MongoDB expertise adds $10K-$20K premium to Oracle DBA roles
- Migration architects: $130K-$190K
