# Interview Questions: R2DBC (Oracle Focus)

## Oracle-Specific Questions
- Compare R2DBC with Oracle UCP (Universal Connection Pool) for reactive database access.
- How does Oracle handle reactive database access? Explain Oracle R2DBC driver and its capabilities.
- Compare Spring Data R2DBC with Spring Data JPA for Oracle — when would you choose each?
- How does R2DBC backpressure work with Oracle's result set streaming?
- Can R2DBC use Oracle-specific features like REF CURSORs and pipelined functions?
- How do you implement transactions with R2DBC in an Oracle database?
- Compare R2DBC connection pooling (r2dbc-pool) with HikariCP for Oracle.
- What are the limitations of R2DBC with Oracle compared to JDBC?

## Google Cloud / Technical
- Cloud SQL R2DBC reactive drivers for PostgreSQL
- Cloud Spanner reactive client vs R2DBC
- Cloud Run + R2DBC for fully reactive Java microservices

## Microsoft / Azure
- Azure SQL reactive support compared to Oracle R2DBC
- Azure Spring Apps reactive stack with R2DBC
- Cosmos DB reactive Java client vs R2DBC

## Amazon / AWS
- RDS Oracle with R2DBC — driver compatibility
- Aurora PostgreSQL reactive support
- Lambda + R2DBC for reactive data access

## Apple
- Reactive streams for Apple server-side (Swift NIO) vs R2DBC
- Non-blocking I/O patterns for Apple services

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |

## Production Scenarios
- Scenario 1: "R2DBC connection pool exhausted under reactive streaming load"
- Scenario 2: "Oracle R2DBC driver incompatible with Oracle 23c new features"
- Scenario 3: "Reactive transaction rollback not working as expected"
- Scenario 4: "Backpressure causing slow consumer to lose results"

## Interview Patterns & Tips
- Oracle interviews ask R2DBC to evaluate reactive programming knowledge
- Know when reactive is appropriate vs traditional JDBC
- R2DBC with Oracle is newer — experience is a differentiator
- Reactive Java + Oracle: $130K-$190K
- Spring WebFlux + R2DBC + Oracle is a hot skills combination
