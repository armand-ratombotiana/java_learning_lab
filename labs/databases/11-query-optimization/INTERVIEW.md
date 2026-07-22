# Interview Questions: Query Optimization (Oracle Focus)

## Oracle-Specific Questions
- How does Oracle's Cost-Based Optimizer (CBO) work? Explain cardinality estimation, selectivity, and cost calculation.
- Explain Oracle execution plans: what do full table scan, index range scan, index unique scan, and table access by rowid mean?
- How do you read an Oracle execution plan using `DBMS_XPLAN.DISPLAY` and `DBMS_XPLAN.DISPLAY_CURSOR`?
- Explain Oracle SQL Plan Management (SPM): how do you capture, evolve, and fix SQL plan baselines?
- How does Oracle Adaptive Query Optimization work? Explain adaptive plans and adaptive statistics.
- What are Oracle SQL Profiles and SQL Patches? How do they differ from SQL Plan Baselines?
- Explain Oracle's join methods: Nested Loops, Hash Join, Sort Merge Join — when does CBO choose each?
- How does Oracle Parallel Execution work? Explain `PARALLEL` hint and `DOP` (Degree of Parallelism).

## Google Cloud / Technical
- Cloud SQL query optimization vs Oracle — EXPLAIN ANALYZE vs DBMS_XPLAN
- BigQuery vs Oracle optimizer for analytical queries
- Cloud Spanner query optimization differences

## Microsoft / Azure
- SQL Server Query Store vs Oracle SPM
- Azure SQL Database Intelligent Insights vs Oracle ADDM
- SQL Server vs Oracle optimizer for similar workloads

## Amazon / AWS
- Amazon RDS Oracle performance insights vs AWR
- Aurora query optimization vs Oracle CBO
- Redshift vs Oracle optimizer for data warehousing

## Apple
- Query optimization for Apple-scale data volumes
- Privacy-preserving query optimization techniques

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | LAG |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | Window Function |
| LC 262 | Trips and Users | Hard | JOIN + CASE |
| LC 569 | Median Employee Salary | Hard | PERCENTILE_CONT |
| LC 571 | Find Median Given Frequency | Hard | Window + SUM OVER |
| LC 601 | Human Traffic of Stadium | Hard | Self JOIN + Aggregate |

## Production Scenarios
- Scenario 1: "SQL plan regression after statistics gather — fixed with SPM baseline"
- Scenario 2: "Full table scan on partitioned table — partition pruning not working"
- Scenario 3: "Hash join spilling to disk — PGA_AGGREGATE_TARGET too low"
- Scenario 4: "Cursor sharing not working — high version count causing latch contention"

## Interview Patterns & Tips
- Oracle query optimization interviews deeply test AWR, ASH, ADDM, and SQL Tuning Advisor
- Expect actual execution plan analysis on the whiteboard
- OCP Performance Tuning certification covers optimizer, SPM, and tuning tools
- Performance engineers: $140K-$210K; DBAs: $120K-$180K
- OCM certification requires expertise in using Oracle tuning tools
