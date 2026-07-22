# Interview Questions: Database Migrations (Flyway/Liquibase — Oracle Focus)

## Oracle-Specific Questions
- Compare Flyway vs Liquibase for Oracle database migration management.
- How do you handle Oracle-specific DDL (tablespaces, partitioning, materialized views) in migration scripts?
- How do you manage Oracle edition-based redefinition (EBR) with Flyway migrations?
- How do you handle Oracle's `ALTER TABLE MODIFY` vs `ALTER TABLE ADD` in versioned migrations?
- What are Oracle's DDL locking implications during migrations? How do you minimize downtime?
- How do you handle Oracle PL/SQL objects (packages, procedures, functions) with Flyway?
- How do you test Flyway/Liquibase migrations against Oracle in CI/CD pipelines?
- How do you roll back a failed migration on Oracle without data loss?

## Google Cloud / Technical
- Flyway with Cloud SQL Oracle-compatible migrations
- Cloud Build + Flyway for automated schema migration CI/CD
- Cloud SQL migration assessment for Oracle schema compatibility

## Microsoft / Azure
- Flyway with Azure SQL Database — migration from Oracle schema
- Azure DevOps pipelines for Liquibase Oracle migrations
- Azure SQL migrations from Oracle — schema conversion

## Amazon / AWS
- AWS DMS + Flyway for schema migration and data sync from Oracle
- RDS Oracle with Flyway — migration best practices
- Aurora PostgreSQL migration from Oracle schema with Liquibase

## Apple
- Schema migration audit trail for compliance
- Data migration privacy: masking PII during schema changes

## LeetCode-Style SQL Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| LC 175 | Combine Two Tables | Easy | JOIN |
| LC 176 | Second Highest Salary | Easy | Subquery |
| LC 178 | Rank Scores | Medium | DENSE_RANK |
| LC 184 | Department Highest Salary | Medium | Correlated Subquery |
| LC 196 | Delete Duplicate Emails | Easy | Self JOIN |
| LC 197 | Rising Temperature | Easy | DATEDIFF |

## Production Scenarios
- Scenario 1: "Migration fails mid-way — Oracle DDL lock timeout kills migration"
- Scenario 2: "Rollback disaster — Oracle flashback vs Flyway undo"
- Scenario 3: "Out-of-order migration causes schema inconsistency"
- Scenario 4: "Oracle tablespace full during migration — recovery strategy"

## Interview Patterns & Tips
- Oracle interviews value experience with schema migration tools for enterprise databases
- Know how to handle Oracle-specific DDL in versioned migrations
- Experience with zero-downtime Oracle migrations is highly valued
- Database migration specialist roles: $120K-$175K
- OCP: Oracle Database SQL certification recommended
