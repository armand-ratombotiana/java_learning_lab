# Interview Questions: Relational Databases (Oracle Focus)

## Oracle-Specific Questions
- Explain Oracle Database architecture: memory structures (SGA, PGA), background processes (DBWR, LGWR, CKPT, SMON, PMON), and storage hierarchy.
- What is Oracle Multitenant architecture? How do CDBs and PDBs work compared to non-CDB architecture?
- Explain Oracle RAC (Real Application Clusters): how does Cache Fusion work with interconnect?
- How does Oracle Data Guard work for disaster recovery? Explain Redo Apply vs SQL Apply (Active Data Guard).
- What are Oracle segments, extents, and blocks? Explain ASSM (Automatic Segment Space Management).
- How does Oracle undo management work? Explain UNDO tablespace, undo retention, and ORA-01555.
- What is Oracle's optimizer mode (ALL_ROWS, FIRST_ROWS)? How does the CBO work with statistics?
- Explain Oracle partitioning: range, list, hash, composite, and interval partitioning.

## Google Cloud / Technical
- Cloud SQL for Oracle vs PostgreSQL — migration considerations
- Cloud Spanner as Oracle RAC alternative for global scale
- Database Migration Service (DMS) for Oracle to Cloud SQL

## Microsoft / Azure
- Azure SQL Database vs Oracle Database architecture comparison
- SQL Server Always On vs Oracle Data Guard
- Azure Database Migration Service for Oracle to SQL Server

## Amazon / AWS
- Amazon RDS for Oracle vs self-managed Oracle on EC2
- Oracle on AWS: license mobility and BYOL considerations
- AWS DMS for Oracle migration and ongoing replication

## Apple
- GDPR compliance for relational databases storing Apple user data
- Data localization requirements for global database deployments

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 177 | Nth Highest Salary | Medium | DENSE_RANK |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 180 | Consecutive Numbers | Medium | Self JOIN |
| LC 181 | Employees Earning More Than Managers | Easy | Self JOIN |
| LC 182 | Duplicate Emails | Easy | GROUP BY |
| LC 183 | Customers Who Never Order | Easy | LEFT JOIN |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 185 | Department Top Three Salaries | Hard | DENSE_RANK |
| LC 196 | Delete Duplicate Emails | Easy | Self JOIN |
| LC 197 | Rising Temperature | Easy | JOIN + DATEDIFF |

## Production Scenarios
- Scenario 1: "AWR report shows 'buffer busy waits' on hot block — diagnose and resolve"
- Scenario 2: "ORA-01555 snapshot too old in nightly batch — fix with undo tuning"
- Scenario 3: "Deadlock chain in order management — identify via ASH and prevent"
- Scenario 4: "Data Guard failover fails — gap resolution with flashback"

## Interview Patterns & Tips
- Oracle DB interviews expect deep knowledge of architecture, RAC, Data Guard, tuning
- OCP certification covers: Oracle Database Administration, Backup/Recovery, Performance Tuning
- OCM requires advanced problem-solving with AWR, ASH, SQL Tuning Advisor
- DBA salaries: $120K-$180K; Senior DBA/Architect: $150K-$220K
- Oracle-specific tuning tools (AWR, ASH, ADDM) are crucial interview topics
