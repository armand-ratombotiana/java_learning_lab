# Interview Questions: PostgreSQL (Oracle Comparison)

## Oracle-Specific Questions
- Compare PostgreSQL MVCC with Oracle MVCC — how do xmin/xmax compare to ORA_ROWSCN?
- How does PostgreSQL's VACUUM compare to Oracle's UNDO management?
- Compare PostgreSQL sequence (`SERIAL`/`IDENTITY`) with Oracle sequences — what are the syntax differences?
- How does PostgreSQL partitioning (declarative) compare to Oracle partitioning?
- Compare PostgreSQL `EXPLAIN ANALYZE` with Oracle's `DBMS_XPLAN` for query tuning.
- What are the replication differences: PostgreSQL streaming replication vs Oracle Data Guard?
- Compare PostgreSQL `pg_stat_statements` with Oracle AWR for performance analysis.
- How does PostgreSQL's connection model (process-per-connection) compare to Oracle's session architecture?

## Google Cloud / Technical
- Cloud SQL PostgreSQL vs AlloyDB for Oracle-compatible workloads
- Cloud Spanner as an alternative to both PostgreSQL and Oracle
- Migration from Oracle to PostgreSQL using ora2pg

## Microsoft / Azure
- Azure Database for PostgreSQL vs Oracle Database on Azure
- PostgreSQL vs Oracle for Azure-based Java applications
- Azure PostgreSQL with pg_partman vs Oracle partitioning

## Amazon / AWS
- Amazon Aurora PostgreSQL vs RDS Oracle performance comparison
- AWS DMS for Oracle to PostgreSQL migration
- Babelfish for Aurora PostgreSQL: SQL Server compatibility

## Apple
- PostgreSQL for Apple's server-side infrastructure
- Data privacy: PostgreSQL row-level security for GDPR

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG |
| LC 181 | Employees Earning More Than Managers | Easy | Self JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN |
| LC 615 | Average Salary | Hard | CASE + AVG |
| LC 618 | Students Report | Hard | PIVOT |
| LC 626 | Exchange Seats | Medium | CASE + LEAD |

## Production Scenarios
- Scenario 1: "VACUUM not keeping up — transaction ID wraparound approaching"
- Scenario 2: "PgBouncer connection pool exhausted due to long-running queries"
- Scenario 3: "Replication lag causing stale reads on read replicas"
- Scenario 4: "PostgreSQL vs Oracle: query plan regressions after ANALYZE"

## Interview Patterns & Tips
- Oracle interviews ask about PostgreSQL to understand your multi-database experience
- Expect comparison questions: PostgreSQL vs Oracle for specific workloads
- Know the strengths of each: Oracle for enterprise features, PostgreSQL for extensibility
- Multi-DB experience is valued in Oracle roles for migration projects
- PostgreSQL to Oracle migration consultant roles: $110K-$170K
