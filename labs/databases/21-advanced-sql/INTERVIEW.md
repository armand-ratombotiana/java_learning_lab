# Interview Questions: Advanced SQL (Oracle Focus)

## Oracle-Specific Questions
- Explain Oracle's analytic functions: `ROW_NUMBER`, `RANK`, `DENSE_RANK`, `LEAD`, `LAG`, `FIRST_VALUE`, `LAST_VALUE` with window frames.
- How does Oracle's `MATCH_RECOGNIZE` work for pattern matching in SQL? Give a real-world example.
- Explain Oracle's `MODEL` clause for spreadsheet-like calculations in SQL.
- How does Oracle's recursive CTE (`WITH RECURSIVE` / `WITH ... CONNECT BY`) work for hierarchical queries?
- What is the Oracle `PIVOT` / `UNPIVOT` syntax and how does it compare to using `CASE` expressions?
- Explain Oracle's `MERGE` statement for upsert operations — when does it insert, update, or delete?
- How does Oracle handle JSON in SQL: `JSON_VALUE`, `JSON_QUERY`, `JSON_TABLE`, `JSON_EXISTS`, `JSON_OBJECT`, `JSON_ARRAYAGG`?
- Explain Oracle SQL Plan Management and how to evolve plan baselines.

## Google Cloud / Technical
- BigQuery SQL vs Oracle SQL for analytics — window functions and CTE differences
- Cloud SQL PostgreSQL vs Oracle for recursive CTEs
- BigQuery `MATCH_RECOGNIZE` vs Oracle pattern matching

## Microsoft / Azure
- SQL Server window functions vs Oracle analytic functions
- T-SQL `PIVOT` vs Oracle `PIVOT` syntax differences
- Azure Synapse analytics window functions

## Amazon / AWS
- Amazon Redshift window functions vs Oracle
- Redshift `MATCH_RECOGNIZE` compatibility
- AWS DMS SQL conversion for advanced SQL features

## Apple
- Advanced SQL for Apple analytics pipelines
- Data privacy in complex SQL queries

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG / Self JOIN |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK + Filter |
| LC 569 | Median Employee Salary | Hard | PERCENTILE_CONT |
| LC 571 | Find Median Given Frequency | Hard | Window + SUM OVER |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN + LAG |
| LC 615 | Average Salary | Hard | CASE + AVG |
| LC 618 | Students Report | Hard | PIVOT |
| LC 626 | Exchange Seats | Medium | CASE + LEAD/LAG |
| LC 1097 | Game Play Analysis V | Hard | LEAD + Window |

## Production Scenarios
- Scenario 1: "Window function causing spool space exhaustion — sort on huge dataset"
- Scenario 2: "MATCH_RECOGNIZE query timing out — pattern matching on unbounded stream"
- Scenario 3: "MERGE statement causing ORA-30926 — duplicate source rows"
- Scenario 4: "Recursive CTE cycling — infinite recursion detection"

## Interview Patterns & Tips
- Advanced SQL interviews at Oracle expect mastery of analytic functions
- `MATCH_RECOGNIZE` and `MODEL` clause are differentiating topics for senior roles
- OCP SQL certification covers advanced query techniques
- Data analysts with Oracle SQL: $100K-$160K
- SQL performance tuning experts: $130K-$200K
