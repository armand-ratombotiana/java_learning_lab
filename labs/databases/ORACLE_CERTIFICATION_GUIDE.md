# Oracle Database — Certification Guide

<div align="center">

![Oracle DB](https://img.shields.io/badge/Oracle_Database-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![OCA](https://img.shields.io/badge/OCA-005C5C?style=for-the-badge)
![OCP](https://img.shields.io/badge/OCP-004d99?style=for-the-badge)
![OCM](https://img.shields.io/badge/OCM-800000?style=for-the-badge)

**Complete Oracle Database certification roadmap — OCA → OCP → OCM**

</div>

---

## Table of Contents

1. [Oracle Database Certification Paths](#oracle-database-certification-paths)
2. [Exam 1Z0-071 (Oracle SQL) — Mapped to Labs](#exam-1z0-071-oracle-sql--mapped-to-labs)
3. [Exam 1Z0-082/083 (Oracle DBA) — Mapped to Labs](#exam-1z0-082083-oracle-dba--mapped-to-labs)
4. [OCM Practical Exam Preparation](#ocm-practical-exam-preparation)
5. [Sample OCP/OCM Questions with Answers](#sample-ocpocm-questions-with-answers)
6. [Upgrade Paths Between Versions](#upgrade-paths-between-versions)
7. [Resources](#resources)

---

## Oracle Database Certification Paths

### Current Certification Roadmap

```
                      Oracle Database Administrator
                      Certified Master (OCM)
                              │
                              ▼
                    Oracle Database Administrator
                    Certified Professional (OCP)  
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
           Oracle Database        Oracle Database
           Administrator          Administrator
           Certified Associate    Certified Professional
           (OCA) — Discontinued   (OCP) — Direct exam path
           
Current Path (as of 2024+):
  1. Oracle Database SQL Certified Associate (1Z0-071)
  2. Oracle Database Administration Certified Professional (1Z0-082 + 1Z0-083)
  3. Oracle Database Certified Master (OCM) — Practical exam
```

### Certification Paths

| Exam | Title | Level | Topics Covered | Lab Mapping |
|------|-------|-------|---------------|-------------|
| 1Z0-071 | Oracle SQL | Associate | SQL queries, DML, DDL, schema objects | Labs 02, 04, 05, 08, 21 |
| 1Z0-082 | Oracle DBA I | Professional | Architecture, storage, security, backup | Labs 06, 08, 13, 14, 20 |
| 1Z0-083 | Oracle DBA II | Professional | Multitenant, RMAN, Data Guard, performance | Labs 09, 13, 14, 21, 23 |
| OCM | Oracle DBA Master | Master | Practical exam (2-day, hands-on) | All labs |

---

## Exam 1Z0-071 (Oracle SQL) — Mapped to Labs

### Exam Topics

| Domain | % | Mapped Labs | Key Topics |
|--------|---|-------------|------------|
| Retrieving Data with SELECT | 20% | 02 SQL Fundamentals | Basic SELECT, WHERE, ORDER BY, joins |
| DML Statements | 15% | 04 MongoDB, 05 Redis | INSERT, UPDATE, DELETE, MERGE |
| DDL Statements | 15% | 08 Flyway/Liquibase | CREATE TABLE, ALTER, constraints |
| Schema Objects | 15% | 21 Advanced SQL | Views, sequences, synonyms, indexes |
| Data Dictionary | 10% | 22 PL/SQL Foundations | USER_*, ALL_*, DBA_* views |
| Set Operators | 5% | 02 SQL Fundamentals | UNION, INTERSECT, MINUS |
| Subqueries | 10% | 02 SQL Fundamentals | Scalar, correlated, EXISTS |
| Functions | 10% | 21 Advanced SQL | Single-row, aggregate, analytical |

### Detailed Lab Mapping

| Lab | 1Z0-071 Topics | Practice Questions |
|-----|----------------|-------------------|
| 02 SQL Fundamentals | SELECT, WHERE, JOIN, GROUP BY | Write 10 queries on HR schema |
| 04 MongoDB (SQL comparison) | DML, INSERT, UPDATE | Translate MongoDB queries to SQL |
| 05 Redis (SQL comparison) | Aggregation functions | Compare SQL aggregation with Redis |
| 08 Flyway/Liquibase | DDL, constraints, indexes | Create migration scripts |
| 21 Advanced SQL | Window functions, CTE, pivot | Write analytical queries |
| 22 PL/SQL Foundations | Functions, procedures | Create stored procedures |

---

## Exam 1Z0-082/083 (Oracle DBA) — Mapped to Labs

### 1Z0-082: Oracle Database Administration I

| Domain | % | Mapped Labs | Key Topics |
|--------|---|-------------|------------|
| Database Architecture | 15% | 01 Relational DBs | Instance architecture, memory structures |
| Storage Structures | 15% | 13 Database Sharding | Tablespaces, datafiles, ASM |
| User Security | 10% | 20 Database Security | Users, roles, privileges, auditing |
| Database Availability | 15% | 14 Database Replication | Backup/recovery, flashback |
| Performance Management | 15% | 11, 15 Query Optimization | SQL tuning, AWR, ADDM |
| Backup & Recovery | 20% | 14 Database Replication | RMAN, recovery scenarios |
| Moving Data | 10% | 08 Flyway/Liquibase | Data Pump, SQL*Loader |

### 1Z0-083: Oracle Database Administration II

| Domain | % | Mapped Labs | Key Topics |
|--------|---|-------------|------------|
| Multitenant Architecture | 20% | 08 CockroachDB (compare) | CDB/PDB, pluggable databases |
| RMAN Deep Dive | 20% | 14 Database Replication | Advanced recovery, catalog, TSPITR |
| Data Guard | 15% | 14 Database Replication | Physical/Logical standby, switchover, failover |
| Performance Tuning | 15% | 11, 15 Query Optimization | SQL profiles, baselines, SQL Tuning Advisor |
| Database Security | 10% | 20 Database Security | TDE, Database Vault, FGA |
| Advanced Features | 10% | 13 Sharding, 21 Advanced SQL | Partitioning, advanced indexing |
| Diagnostic Sources | 10% | 23 PL/SQL Advanced | AWR, ASH, ADDM, trace files |

---

## OCM Practical Exam Preparation

### OCM Exam Structure

| Day | Duration | Topics |
|-----|----------|--------|
| Day 1 | 8 hours (uninterrupted) | Architecture, space management, performance tuning, backup/recovery |
| Day 2 | 8 hours (uninterrupted) | Networking, Oracle RAC, Data Guard, multitenant, advanced features |

### OCM Lab Topics

| Topic | Weight | Labs to Study | Preparation Task |
|-------|--------|---------------|------------------|
| Database Installation | 5% | 01 Relational DBs | Install Oracle 19c on Linux |
| Database Configuration | 5% | 08 CockroachDB (compare) | Create CDB + PDBs |
| Tablespace Management | 10% | 13 Sharding | Create tablespaces with different features |
| User Management & Security | 10% | 20 Security | Create users, roles, VPD |
| Performance Tuning | 20% | 11, 15 Query Optimization | Diagnose AWR, implement SQL Profile |
| Backup & Recovery | 20% | 14 Replication | RMAN: full/incremental/point-in-time |
| Data Guard | 10% | 14 Replication | Configure physical standby, switchover |
| Oracle RAC | 10% | 18 CockroachDB (compare) | Install RAC, add node |
| Advanced Features | 10% | 13 Sharding, 21 Advanced SQL | Partitioning, materialized views |

### Preparation Strategy

| Phase | Time | Focus |
|-------|------|-------|
| 1 | 6 weeks | Complete all database labs |
| 2 | 4 weeks | Focus on OCM-specific topics (RAC, Data Guard, RMAN) |
| 3 | 2 weeks | Hands-on practice in test environment |
| 4 | 1 week | Time management practice |

---

## Sample OCP/OCM Questions with Answers

### Question 1 (OCP — Easy)
**Which background process is responsible for writing dirty buffers to disk?**

A. SMON
B. PMON
C. DBWR ✓
D. LGWR

**Explanation:** DBWR (Database Writer) writes dirty buffers from the database buffer cache to the data files. LGWR writes redo log buffers to the online redo logs.

### Question 2 (OCP — Medium)
**A transaction updates 1000 rows and commits. At what point are the changes written to the data files?**

A. Immediately when COMMIT is issued
B. When DBWR next writes dirty buffers ✓
C. When the redo log is switched
D. When CHECKPOINT occurs

**Explanation:** COMMIT guarantees the redo is written (LGWR) but changed data blocks remain in buffer cache until DBWR writes them. The COMMIT is fast because it only needs to write redo, not all changed blocks.

### Question 3 (OCP — Hard)
**You have a 2TB database in ARCHIVELOG mode. The SYSTEM tablespace data file is accidentally deleted. Walk through the recovery procedure.**

**Answer:**
1. **If the database is still open:** Take affected tablespace offline (`ALTER TABLESPACE SYSTEM OFFLINE;`) — not possible for SYSTEM tablespace. Database must be in MOUNT state.
2. **Mount the database:** `STARTUP MOUNT`
3. **Restore the data file:** `RESTORE DATAFILE '/u01/oradata/system01.dbf';`
4. **Recover the data file:** `RECOVER DATAFILE '/u01/oradata/system01.dbf';`
5. **Open the database:** `ALTER DATABASE OPEN;`

Since SYSTEM tablespace is critical, there may be downtime. If using a backup control file, additional steps: `RESTORE CONTROLFILE`, then `CATALOG START WITH` for data files.

### Question 4 (OCM — Scenario)
**The database is running slowly. AWR report shows "log file sync" wait event as the top wait. What is the likely cause and resolution?

**Answer:**
- **Cause:** Sessions are waiting for LGWR to write redo to the online redo logs. This can be caused by:
  - Slow I/O on redo log files (redo logs on slow disk)
  - Small redo log size (frequent log switches)
  - Excessive COMMIT frequency in application
  - LGWR slave processes not configured
- **Resolution:**
  1. Move redo logs to faster storage (SSD/Fast NFS)
  2. Increase redo log size (check `v$log_history` for avg switch interval)
  3. Add redo log groups to reduce contention
  4. Use `LGWR_ASYNC_IO` parameter for asynchronous writes
  5. Configure `LOG_PARALLELISM` and `DBWR_IO_SLAVES` for parallel writes
  6. Application review: batch COMMITs instead of frequent single-row COMMITs

---

## Upgrade Paths Between Versions

### Supported Upgrade Paths

```
Oracle 11gR2 (11.2.0.4)
    │
    ├──→ Oracle 12cR1 (12.1.0.2)
    │       │
    │       ├──→ Oracle 12cR2 (12.2.0.1)
    │       │       │
    │       │       └──→ Oracle 18c
    │       │               │
    │       │               └──→ Oracle 19c (Long-term support) ◄── Current LTS
    │       │                       │
    │       │                       └──→ Oracle 23c (Latest)
    │       │
    │       └──→ Oracle 18c → 19c → 23c
    │
    └──→ Oracle 19c (Direct upgrade from 11.2.0.4)
```

### Recommended Upgrade Path
**11.2.0.4 → 19c (Latest LTS)** — Direct upgrade supported with `DBUA` or manual scripts. Oracle 19c is the current long-term support release with extended support through 2032.

---

## Resources

### Official Resources
| Resource | Link | Cost |
|----------|------|------|
| Oracle University Training | education.oracle.com | Varies |
| Oracle Live SQL | livesql.oracle.com | Free |
| Oracle Database Documentation | docs.oracle.com | Free |
| Oracle Dev Gym | devgym.oracle.com | Free |
| Oracle Learning Explorer | explore.oracle.com | Free |

### Recommended Books
- *Oracle Database 19c Administration* — Bob Bryla
- *Oracle SQL* — Steven Feuerstein
- *Oracle PL/SQL Programming* — Steven Feuerstein
- *Expert Oracle Database Architecture* — Tom Kyte
- *Oracle RMAN for Absolute Beginners* — Dariush Farsi

### Practice Platforms
| Platform | Use |
|----------|-----|
| Oracle Live SQL | Test SQL, PL/SQL online |
| Oracle Dev Gym | SQL and PL/SQL quizzes |
| Oracle VirtualBox | Build local test environments |
| Oracle Cloud Free Tier | Free Autonomous Database |

### Exam Costs
| Exam | Fee (USD) |
|------|-----------|
| 1Z0-071 (SQL Associate) | $245 |
| 1Z0-082 (DBA I) | $245 |
| 1Z0-083 (DBA II) | $245 |
| OCM Practical | $1,500 |
| Retake policy | 14-day wait |

---

<div align="center">

**"Master Oracle Database — from SQL fundamentals to expert administration."**

</div>
