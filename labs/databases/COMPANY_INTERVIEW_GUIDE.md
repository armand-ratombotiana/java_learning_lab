# Oracle Database — Company Interview Guide

<div align="center">

![Oracle](https://img.shields.io/badge/Oracle_Database-Interviews-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Google](https://img.shields.io/badge/Google-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Microsoft](https://img.shields.io/badge/Microsoft-0078D4?style=for-the-badge&logo=microsoft&logoColor=white)
![Amazon](https://img.shields.io/badge/Amazon-FF9900?style=for-the-badge&logo=amazon&logoColor=white)
![Apple](https://img.shields.io/badge/Apple-000000?style=for-the-badge&logo=apple&logoColor=white)

**Prepare for Oracle Database interviews at top tech companies**

</div>

---

## Table of Contents

1. [Oracle-Specific Interview Process](#oracle-specific-interview-process)
2. [Google/Microsoft/Amazon Interview for Database Roles](#googlemicrosoftamazon-interview-for-database-roles)
3. [Apple Interview for Database Roles](#apple-interview-for-database-roles)
4. [LeetCode SQL Problem Mappings](#leetcode-sql-problem-mappings)
5. [Real Interview Experiences](#real-interview-experiences)

---

## Oracle-Specific Interview Process

### Typical 4-5 Round Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| 1 | Recruiter Screen | 30 min | Background, SQL experience, certifications |
| 2 | Technical Screen (SQL) | 45-60 min | Complex SQL, query optimization, PL/SQL |
| 3 | Technical Deep Dive (DBA) | 60 min | Architecture, performance, backup/recovery |
| 4 | System Design | 60 min | Design a database system for specific requirements |
| 5 | Behavioral / Manager | 45 min | Project experience, problem-solving, culture fit |

### Common Technical Questions

**Oracle Architecture:**
1. Explain Oracle instance memory structures (SGA, PGA) and background processes.
2. How does Oracle's multitenant architecture work (CDB/PDB)?
3. Explain Oracle RAC and Cache Fusion.
4. How does Oracle Data Guard differ from Oracle RAC?

**SQL and PL/SQL:**
1. Write a hierarchical query using CONNECT BY PRIOR.
2. Explain analytic functions (RANK, DENSE_RANK, LEAD, LAG).
3. How do you debug a slow PL/SQL procedure?
4. Explain bulk collect and FORALL for performance.

**Performance Tuning:**
1. What information is in an AWR report? How do you interpret it?
2. Explain how to read an execution plan.
3. What is SQL Tuning Advisor and when would you use it?
4. How do you identify and resolve "buffer busy waits"?

**Backup and Recovery:**
1. Explain RMAN incremental backup strategy.
2. How do you recover from a lost data file without a backup?
3. What is TSPITR (Tablespace Point-in-Time Recovery)?
4. How does flashback database work?

---

## Google/Microsoft/Amazon Interview for Database Roles

### Google — Database Engineer / Cloud SQL Engineer

**Interview Process:**
| Round | Focus | Topics |
|-------|-------|--------|
| Phone Screen | General database knowledge | SQL, ACID, CAP theorem |
| Technical (SQL) | Complex query writing | Window functions, CTEs, optimization |
| System Design | Global database | Design Spanner-like system or Oracle on Cloud |
| Googleyness | Behavioral | Distributed systems thinking |

**Sample Questions:**
1. "Design a multi-region Oracle database deployment for a global SaaS application."
2. "Compare Cloud Spanner with Oracle RAC for a globally distributed application."
3. "How would you migrate a 10TB Oracle database to Cloud SQL with minimal downtime?"

### Microsoft — SQL Server DBA / Azure Database Engineer

**Interview Process:**
| Round | Focus | Topics |
|-------|-------|--------|
| Recruiter | Background, Oracle experience | Deep Oracle vs SQL Server knowledge |
| Technical (Oracle) | Oracle architecture + Performance | Memory, I/O, tuning |
| Technical (Azure) | Azure SQL vs Oracle | Migration, coexistence |
| System Design | Database migration | Oracle to SQL Server migration plan |

**Sample Questions:**
1. "Plan a migration from Oracle to Azure SQL Database for a 5TB OLTP workload."
2. "Compare Oracle Data Guard with SQL Server Always On Availability Groups."
3. "How would you set up cross-platform replication between Oracle and SQL Server?"

### Amazon — DBA / Database Specialist

**Interview Process:**
| Round | Focus | Topics |
|-------|-------|--------|
| Recruiter | Oracle DBA experience | RAC, Data Guard, RDS |
| Technical | Performance + Backup | RMAN, tuning, Aurora vs Oracle |
| System Design | Database on AWS | RDS Oracle vs EC2 Oracle |
| Leadership Principles | Behavioral | Ownership, dive deep, deliver results |

**Sample Questions:**
1. "Design a high-availability Oracle database on AWS using RDS or EC2."
2. "Cost-optimize an Oracle workload migrating to Amazon Aurora."
3. "What would you choose: RDS Oracle, EC2 Oracle, or Aurora PostgreSQL?"

---

## Apple Interview for Database Roles

### Apple — Database Reliability Engineer

**Interview Process:**
| Round | Focus | Topics |
|-------|-------|--------|
| Recruiter | SRE/DBA experience | High-availability databases |
| Technical | SQL + Performance | Complex query optimization |
| Systems Design | Global database | Design for privacy, low-latency |
| Apple Values | Behavioral | Privacy, quality, user focus |

**Sample Questions:**
1. "Design a database system for iCloud that ensures data privacy across global regions."
2. "How would you ensure zero data loss during a cross-region failover?"
3. "Performance: An OLTP query that runs in 1 second during testing takes 10 seconds in production. Diagnose."

---

## LeetCode SQL Problem Mappings

### Easy — SQL Screen Level
| Problem | Concepts | Database Relevance |
|---------|----------|-------------------|
| 175. Combine Two Tables | LEFT JOIN | Oracle JOIN syntax |
| 176. Second Highest Salary | Subquery, OFFSET | Oracle ROWNUM vs FETCH FIRST |
| 181. Managers with >5 Reports | JOIN, GROUP BY | Reporting queries |
| 182. Duplicate Emails | GROUP BY, HAVING | Data quality checks |
| 183. Customers Who Never Order | LEFT JOIN | Data analysis |

### Medium — Technical Interview Level
| Problem | Concepts | Oracle-Specific |
|---------|----------|----------------|
| 177. Nth Highest Salary | Window functions | ROW_NUMBER(), DENSE_RANK() |
| 178. Rank Scores | DENSE_RANK | Tied ranking |
| 180. Consecutive Numbers | LEAD/LAG | Time-series analysis |
| 184. Department Highest Salary | JOIN + Window func | Hierarchical reporting |
| 550. Game Play Analysis IV | Date functions | Oracle date arithmetic |

### Hard — System Design / Senior Level
| Problem | Concepts | Oracle-Specific |
|---------|----------|----------------|
| 185. Department Top Three | Window, JOIN, Filter | NTH_VALUE analytic |
| 262. Trips and Users | Complex JOIN, date range | Query optimization |
| 601. Human Traffic Stadium | Window, CTE, gaps | Pattern matching |
| 1097. Game Play Analysis V | Recursive queries | Oracle CONNECT BY |

---

## Real Interview Experiences

### Oracle Database Administrator (Glassdoor Aggregated)

**Experience #1 — Oracle DBA (Oracle Corp, 2023)**
- "4 rounds: Technical (RMAN + Data Guard), Performance Tuning (AWR analysis), System Design (CDB/PDB deployment), Manager"
- "RMAN: Recover tablespace after disk failure without full restore"
- "Performance: Explain AWR top 5 timed events and how to remediate each"

**Experience #2 — Senior Oracle DBA (Financial Services, 2024)**
- "5 rounds: SQL coding (LeetCode hard), Architecture (RAC + Data Guard), Performance (wait events), Backup (RMAN strategy), Behavioral"
- "SQL: Write a query to find the top 3 products by revenue per region per year"
- "Backup: Design RMAN strategy for 5TB 24/7 database with 15-min RPO and 1-hour RTO"

**Experience #3 — Oracle DBA (Cloud Migration Focus, 2024)**
- "3 rounds: Technical deep dive (Oracle 19c features), Migration strategy (on-prem to OCI), Cloud architecture"
- "Migration: How to migrate 10TB database from on-premise Exadata to OCI with < 1 hour downtime"
- "Cloud: Compare Exadata Cloud Service and Autonomous Database"

### Levels.fyi Insights
- **Oracle DBA Salary:** $100K-$150K
- **Senior Oracle DBA:** $135K-$175K
- **Oracle Database Architect:** $160K-$210K
- **Cloud Database Specialist:** $140K-$190K
- **Top paying companies:** Oracle, Amazon (RDS team), Google (Cloud SQL), Microsoft (Azure SQL), Apple

---

<div align="center">

**"Database skills are timeless — focus on architecture, performance, and migration for today's cloud-native world."**

</div>
