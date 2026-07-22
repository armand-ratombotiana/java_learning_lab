# Interview Questions: APEX SQL Workshop

## Oracle-Specific Questions
- How does the APEX SQL Workshop Object Browser help manage database objects? What DDL/DML operations does it support?
- Explain the APEX_DATA_PARSER package — how do you parse CSV, XLSX, and JSON files uploaded by users?
- What is ORDS (Oracle REST Data Services) and how does SQL Workshop integrate with it?
- How do you use Query Builder to visually construct complex SQL queries with joins and filters?
- What are the limitations of SQL Workshop compared to SQL Developer or SQL*Plus?
- How does APEX handle large data uploads via Data Workshop? What are the row limits and best practices?
- Explain how to create RESTful Services from SQL queries in the SQL Workshop interface.
- What monitoring features does SQL Workshop provide for long-running queries?

## Google Cloud / Technical
- Using APEX SQL Workshop with Cloud SQL for Oracle-compatible workloads
- Google Data Studio integration with APEX data sources
- Cloud Storage for bulk data loading into APEX applications

## Microsoft / Azure
- Azure Data Factory integration with APEX database objects
- SQL Server vs Oracle SQL Workshop for ad-hoc data analysis
- Azure Synapse analytics with data exported from APEX

## Amazon / AWS
- AWS Glue ETL jobs that read from APEX database tables
- Amazon RDS for Oracle — using SQL Workshop against RDS instances
- S3-based data loading into APEX using APEX_DATA_PARSER with presigned URLs

## Apple
- Handling PII in SQL Workshop queries — redaction and masking techniques
- Secure export of data from SQL Workshop for mobile app consumption

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 176 | Second Highest Salary | Easy | Subquery / NVL |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK + NVL |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | Self JOIN / LAG |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK + Filter |
| LC 262 | Trips and Users | Hard | JOIN + CASE |
| LC 569 | Median Employee Salary | Hard | PERCENTILE_CONT |
| LC 571 | Find Median Given Frequency | Hard | Window + SUM OVER |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN + Aggregate |

## Production Scenarios
- Scenario 1: "Production incident — Data Workshop import silently failed and corrupted existing data"
- Scenario 2: "Performance tuning — SQL Workshop query timing out on large table scan"
- Scenario 3: "Disaster recovery — Accidental DROP TABLE from Object Browser in production"
- Scenario 4: "Security breach — SQL Workshop credentials exposed via query history"

## Interview Patterns & Tips
- Oracle expects DBAs and developers to know SQL Workshop's role in APEX lifecycle
- Questions often focus on how to parse uploaded files with APEX_DATA_PARSER
- SQL Workshop proficiency is expected for OCP-level certifications
- Salary range for APEX developers with strong SQL skills: $120K-$175K
