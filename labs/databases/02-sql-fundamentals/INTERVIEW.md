# Interview Questions: SQL Fundamentals (Oracle Focus)

## Oracle-Specific Questions
- Explain the difference between `WHERE` and `HAVING` in Oracle SQL — can HAVING be used without GROUP BY?
- How does Oracle's `DISTINCT` vs `UNIQUE` differ? Are they identical in Oracle?
- What is the difference between `ROWNUM` and `ROW_NUMBER()` in Oracle? When do you use each?
- Explain Oracle's `CONNECT BY PRIOR` for hierarchical queries — how does it avoid cycles?
- How does Oracle handle NULLs in sorting? Explain `NULLS FIRST` and `NULLS LAST`.
- What are Oracle's `DECODE` and `CASE` expressions? When is DECODE more efficient?
- Explain Oracle's `MERGE` (UPSERT) statement — when does it INSERT vs UPDATE?
- What is the difference between `CHAR` and `VARCHAR2` in Oracle — storage and comparison semantics?

## Google Cloud / Technical
- Cloud SQL PostgreSQL vs Oracle SQL syntax differences
- BigQuery SQL dialect vs Oracle SQL for analytics

## Microsoft / Azure
- T-SQL vs Oracle SQL — key differences (TOP vs ROWNUM, GETDATE vs SYSDATE)
- Azure SQL vs Oracle SQL for ETL processes

## Amazon / AWS
- Redshift SQL vs Oracle SQL for data warehousing
- AWS DMS transformations from Oracle to target SQL dialects

## Apple
- SQL injection prevention for Apple-facing applications
- Data validation in SQL layer for Apple compliance

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | LEFT JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery + NVL |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG / Self JOIN |
| LC 181 | Employees Earning More Than Managers | Easy | Self JOIN |
| LC 182 | Duplicate Emails | Easy | GROUP BY |
| LC 183 | Customers Who Never Order | Easy | LEFT JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 196 | Delete Duplicate Emails | Easy | Self JOIN + DELETE |
| LC 197 | Rising Temperature | Easy | Self JOIN + DATEDIFF |
| LC 262 | Trips and Users | Hard | JOIN + CASE |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN + Aggregate |
| LC 626 | Exchange Seats | Medium | CASE + LEAD/LAG |

## Production Scenarios
- Scenario 1: "Query returns wrong results due to NULL handling in WHERE clause"
- Scenario 2: "MERGE statement causes ORA-30926: unable to get a stable set of rows"
- Scenario 3: "Hierarchical query CONNECT BY loops with cycle detection"
- Scenario 4: "ROWNUM pagination giving wrong results on ordered data"

## Interview Patterns & Tips
- Oracle SQL interviews focus on Oracle-specific syntax and functions
- Know the difference between Oracle and ANSI SQL features
- OCP SQL certification covers advanced query techniques
- SQL proficiency is tested in hands-on coding rounds
- $100K-$160K for SQL-focused database roles
