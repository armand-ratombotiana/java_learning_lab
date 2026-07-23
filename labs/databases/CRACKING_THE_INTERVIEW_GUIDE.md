# Databases Academy — Cracking the Interview Guide

<div align="center">

![Database](https://img.shields.io/badge/Database_Interview-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![FAANG](https://img.shields.io/badge/FAANG-000000?style=for-the-badge)
![SQL](https://img.shields.io/badge/SQL-CC2927?style=for-the-badge)

**Your comprehensive guide to database engineering interviews at Oracle, Google, Microsoft, Amazon, and Apple**

</div>

---

## Table of Contents

1. [Oracle DB Interview Process (OCP/OCM)](#oracle-db-interview-process-ocp-ocm)
2. [Google — Cloud Spanner, Bigtable, SQL](#google--cloud-spanner-bigtable-sql)
3. [Microsoft — SQL Server, Cosmos DB](#microsoft--sql-server-cosmos-db)
4. [Amazon — DynamoDB, RDS, Aurora](#amazon--dynamodb-rds-aurora)
5. [Apple — Core Data, FoundationDB](#apple--core-data-foundationdb)
6. [SQL LeetCode Patterns](#sql-leetcode-patterns)
7. [NoSQL Interview Preparation](#nosql-interview-preparation)
8. [Database Design for System Design Interviews](#database-design-for-system-design-interviews)
9. [30/60/90 Day Study Plans](#306090-day-study-plans)

---

## Oracle DB Interview Process (OCP/OCM)

### Typical 4-5 Round Process

| Round | Type | Duration | Focus |
|-------|------|----------|-------|
| 1 | Recruiter Screen | 30 min | Background, SQL experience, certifications held |
| 2 | Technical Screen (SQL) | 45-60 min | Complex SQL, query optimization, PL/SQL |
| 3 | Technical Deep Dive (DBA) | 60 min | Architecture, performance, backup/recovery |
| 4 | System Design | 60 min | Design a database system for specific requirements |
| 5 | Behavioral / Manager | 45 min | Project experience, problem-solving, culture fit |

### Common Interview Topics by Level

**Junior DBA (0-3 years):**
- Basic SQL (SELECT, JOIN, GROUP BY, subqueries)
- Oracle architecture basics (SGA, PGA, background processes)
- Basic indexing strategy
- Backup and recovery fundamentals
- User management and security basics

**Mid-Level DBA (3-6 years):**
- Performance tuning (AWR, ASH, execution plans)
- RMAN backup and recovery strategies
- Data Guard and RAC fundamentals
- Partitioning and advanced indexing
- Multitenant architecture (CDB/PDB)
- SQL tuning and optimizer knowledge

**Senior DBA (6+ years):**
- Enterprise-scale architecture (Exadata, RAC, Data Guard)
- Migration strategies (cross-platform, cross-version)
- Disaster recovery planning
- Capacity planning and performance architecture
- Security compliance (TDE, Database Vault, Audit Vault)
- Automation and scripting

**Database Architect (10+ years):**
- Global database architecture
- Multi-database strategy (Oracle, NoSQL, cloud)
- Data lifecycle management
- Cost optimization
- Integration architecture

### Sample Technical Questions by Topic

**Architecture:**
1. Explain the Oracle instance memory structures (SGA components) and background processes.
2. How does Oracle's multitenant architecture work? Compare CDB/PDB with traditional architecture.
3. Explain Oracle RAC Cache Fusion protocol.
4. How does Oracle Data Guard differ from Oracle RAC? When would you use each?
5. Explain ASM (Automatic Storage Management) architecture.

**SQL and PL/SQL:**
1. Write a query to find employees with the Nth highest salary.
2. Explain flashback query and flashback data archive.
3. How do you debug a slow PL/SQL procedure?
4. Explain bulk collect and FORALL for performance.
5. Write a recursive CTE to find organizational hierarchy.

**Performance Tuning:**
1. What information is in an AWR report? How do you interpret top 5 timed events?
2. Explain how to read an execution plan. What do you look for?
3. What is a SQL profile? How is it different from a SQL baseline?
4. How do you identify and resolve "buffer busy waits"?
5. Explain the optimizer modes (ALL_ROWS, FIRST_ROWS, FIRST_ROWS_N).

**Backup and Recovery:**
1. Explain RMAN incremental backup strategy (Level 0, Level 1).
2. How do you recover from a lost data file without a backup?
3. What is TSPITR (Tablespace Point-in-Time Recovery) and when would you use it?
4. How does flashback database work? What are the prerequisites?
5. Design a backup strategy for a 24/7 OLTP database with RPO=15 min, RTO=1 hour.

---

## Google — Cloud Spanner, Bigtable, SQL

### Interview Process

| Round | Focus | Duration | Topics |
|-------|-------|----------|--------|
| Phone Screen | General database + distributed systems | 45 min | ACID, CAP theorem, SQL |
| Technical (SQL) | Complex query writing | 60 min | Window functions, CTEs, query optimization |
| System Design | Global database design | 60 min | Design Spanner-like system or Oracle on Cloud |
| Googleyness | Behavioral | 45 min | Distributed systems thinking, collaboration |

### Key Topics for Google Database Interviews

**Cloud Spanner:**
- Globally distributed, strongly consistent relational database
- Synchronous replication with Paxos across zones
- TrueTime API for external consistency (GPS + atomic clocks)
- Interleaved tables for parent-child join optimization
- Hotspot prevention with monotonically increasing keys (use hash prefix)
- Schema design: primary keys, secondary indexes, interleaving
- Spanner vs CockroachDB vs YugabyteDB comparison

**Bigtable:**
- Wide-column NoSQL database for large analytical workloads
- Row key design: avoid hotspotting, use salting
- Column families for related data
- Bigtable vs Cloud Spanner: when to use which
- Time-series data modeling with Bigtable

**Google Cloud SQL:**
- Managed MySQL, PostgreSQL, SQL Server
- Read replicas, failover replicas
- Cloud SQL vs Cloud Spanner decision matrix

**Sample Google Database Question:**
> "Design a multi-region Oracle database deployment on Google Cloud for a global SaaS."
> **Approach:** Use Google Compute Engine for Oracle instances in multiple regions. Configure Active Data Guard for cross-region replication. Use Cloud Load Balancing for read/write splitting. Cloud Interconnect for on-premise connectivity. Migrate using Oracle Data Pump or GoldenGate.

---

## Microsoft — SQL Server, Cosmos DB

### Interview Process

| Round | Focus | Duration | Topics |
|-------|-------|----------|--------|
| Recruiter Screen | Background + Oracle experience | 30 min | Oracle vs SQL Server comparison |
| Technical (Oracle) | Oracle architecture + performance | 60 min | Memory, I/O, wait events, tuning |
| Technical (Azure) | Azure SQL + migration | 60 min | Oracle to SQL Server migration planning |
| System Design | Database migration architecture | 60 min | Large-scale migration, coexistence |

### Key Topics for Microsoft Database Interviews

**SQL Server (for Oracle DBAs):**
| Oracle | SQL Server |
|--------|------------|
| SGA + PGA | Buffer Pool + Plan Cache |
| Shared Server | Connection Pooling |
| Redo Logs | Transaction Log (VLF) |
| RMAN | Backup/Restore (full, diff, log) |
| Data Guard | Always On Availability Groups |
| RAC | SQL Server Failover Cluster |
| ASM | Storage Spaces / SAN |

**Cosmos DB:**
- Globally distributed, multi-model NoSQL database
- Consistency levels: Strong, Bounded Staleness, Session, Consistent Prefix, Eventual
- Partition key selection for optimal performance
- Request Units (RU) — provisioning model
- Multi-region writes for low-latency global apps
- Cosmos DB vs Azure SQL Database decision matrix

**Migration Scenarios:**
1. **Oracle to Azure SQL Database:** Use Azure Database Migration Service (DMS)
2. **Oracle to SQL Server on VM:** Use SQL Server Migration Assistant (SSMA)
3. **Coexistence:** Set up cross-platform replication (Oracle GoldenGate, Kafka Connect)
4. **Hybrid:** Run Oracle on-premise + Azure SQL in cloud, linked servers, or app-level sharding

**Sample Microsoft Database Question:**
> "Plan a migration from Oracle 19c to Azure SQL Database for a 5TB OLTP workload."
> **Approach:** Assess compatibility with SSMA. Convert stored procedures and PL/SQL to T-SQL. Use Azure DMS for schema + data migration. Validate with test workloads. Implement change data capture for minimal downtime. Use Azure SQL DTU/Buy model based on workload profile.

---

## Amazon — DynamoDB, RDS, Aurora

### Interview Process

| Round | Focus | Duration | Topics |
|-------|-------|----------|--------|
| Recruiter Screen | Oracle DBA experience + AWS knowledge | 30 min | RAC, Data Guard, RDS |
| Technical | Performance + backup + migration | 60 min | RMAN, tuning, Aurora vs Oracle |
| System Design | Database on AWS | 60 min | RDS Oracle vs EC2 Oracle vs Aurora |
| Leadership Principles | Behavioral | 60 min | Ownership, dive deep, deliver results |

### Key Topics for Amazon Database Interviews

**DynamoDB:**
- Single-table design: optimize for access patterns, not normalization
- Partition key design: high cardinality, even access distribution
- Sort key design: composite keys for query flexibility
- Global Secondary Index (GSI): eventual consistency, separate throughput
- Local Secondary Index (LSI): strong consistency, limited to 5 per table
- DAX (DynamoDB Accelerator): in-memory cache for microsecond reads
- DynamoDB Streams: change data capture for event-driven architectures
- DynamoDB vs RDS decision matrix

**Amazon RDS for Oracle:**
- Managed Oracle: RDS Oracle Standard Edition Two, Enterprise Edition
- Multi-AZ for high availability (synchronous standby)
- Read replicas for read scaling (up to 5)
- Automated backups with point-in-time recovery
- Performance Insights for database monitoring
- RDS Custom for advanced customization needs

**Amazon Aurora:**
- MySQL and PostgreSQL-compatible with 5x throughput
- 6-way replication across 3 AZs
- Auto-scaling storage (up to 128TB)
- Aurora Global Database for cross-region replication
- Aurora Serverless for variable workloads
- Aurora vs RDS Oracle comparison for migration

**Migration Strategies:**
1. **Lift and Shift:** Move Oracle to EC2 with RDS Oracle
2. **Replatform:** Oracle to Aurora PostgreSQL using Babelfish
3. **Rearchitect:** Oracle to DynamoDB (NoSQL redesign)
4. **Replace:** Oracle to Amazon RDS MySQL

**Sample Amazon Database Question:**
> "Design a high-availability Oracle database deployment on AWS."
> **Approach:** Use RDS Oracle Multi-AZ for automated failover. For more control, deploy Oracle on EC2 with Data Guard. Configure Automatic Backup with 35-day retention. Use Performance Insights and Enhanced Monitoring. Cross-region backup for DR. ElastiCache for read-heavy workloads.

---

## Apple — Core Data, FoundationDB

### Interview Process

| Round | Focus | Duration | Topics |
|-------|-------|----------|--------|
| Recruiter Screen | DBA/SRE experience | 30 min | High-availability database background |
| Technical (SQL) | Complex query writing + optimization | 60 min | Query optimization, execution plans |
| Systems Design | Global database with privacy | 60 min | Design for privacy, low-latency, compliance |
| Apple Values | Behavioral | 45 min | Privacy, quality, attention to detail |

### Key Topics for Apple Database Interviews

**FoundationDB:**
- Distributed key-value store with ACID transactions
- Record layer: relational layer on top of FoundationDB
- Strict serializability for transactions
- Used by Apple for iCloud, Siri, and other services
- CockroachDB comparison: FoundationDB provides strict serializability, not geo-distribution

**Core Data:**
- Apple's object graph and persistence framework for iOS/macOS
- SQLite as default persistent store
- CloudKit integration for cloud sync
- Multi-threaded Core Data with private/child contexts
- Migration strategies: lightweight vs heavyweight

**Privacy-First Database Design:**
- Differential privacy for analytics
- On-device processing with minimal backend data
- End-to-end encryption for sensitive data
- Data residency compliance (GDPR, CCPA)
- Zone-based database isolation for privacy

**Sample Apple Database Question:**
> "Design a globally distributed database system for iCloud that ensures data privacy."
> **Approach:** Use FoundationDB for metadata (ACID transactions). Geo-partition data by user region. Implement end-to-end encryption at application layer. Differential privacy for aggregate analytics. Zone-based replication for data residency. Cascade replication topology for cross-region sync.

---

## SQL LeetCode Patterns

### Query Categories

| Category | Patterns | Difficulty |
|----------|----------|------------|
| SELECT + JOIN | INNER, LEFT, RIGHT, FULL, SELF joins | Easy |
| Aggregation | GROUP BY, HAVING, COUNT, SUM, AVG | Easy-Medium |
| Subqueries | Scalar, correlated, EXISTS, IN | Medium |
| Window Functions | ROW_NUMBER, RANK, DENSE_RANK, LEAD, LAG | Medium |
| CTE | Recursive CTE, non-recursive CTE | Medium-Hard |
| Date Functions | DATE arithmetic, TRUNC, EXTRACT | Medium |
| String Functions | CONCAT, SUBSTR, REGEXP_LIKE | Easy-Medium |
| Pivot/Unpivot | Rows to columns, columns to rows | Hard |
| Conditional | CASE, DECODE, IF, COALESCE | Easy-Medium |

### Common LeetCode SQL Problem Categories

**Easy Problems (Foundation):**
- Combine Two Tables (175) — LEFT JOIN
- Second Highest Salary (176) — OFFSET/FETCH or subquery
- Employees Earning More Than Managers (181) — Self JOIN
- Duplicate Emails (182) — GROUP BY + HAVING
- Customers Who Never Order (183) — LEFT JOIN + IS NULL
- Delete Duplicate Emails (196) — Self DELETE

**Medium Problems (Core):**
- Nth Highest Salary (177) — DENSE_RANK or subquery
- Rank Scores (178) — DENSE_RANK window function
- Consecutive Numbers (180) — LEAD/LAG window functions
- Department Highest Salary (184) — Window + JOIN
- Game Play Analysis IV (550) — Date functions + aggregation
- Managers with at Least 5 Direct Reports (570) — JOIN + GROUP BY
- Find Customer Referee (584) — NULL handling with OR

**Hard Problems (Advanced):**
- Department Top Three Salaries (185) — DENSE_RANK + filtering
- Trips and Users (262) — JOIN + date filtering + status
- Human Traffic of Stadium (601) — Window functions + gaps
- Median Employee Salary (569) — PERCENTILE_CONT or ROW_NUMBER
- Report Contiguous Dates (1225) — Window + CTE for gaps

### Interview-Relevant SQL Scenarios

| Scenario | Concepts | Interview Question |
|----------|----------|-------------------|
| Sessionization | LAG, date diff | "Find session boundaries from user activity logs" |
| Funnel Analysis | Multiple JOINs, COUNT | "Calculate conversion rate through a sales funnel" |
| User Retention | Date diff, cohorts | "Calculate user retention by signup cohort" |
| Gap Detection | LEAD, CTE | "Find gaps in order dates or sensor readings" |
| Hierarchical Aggregation | Recursive CTE, CONNECT BY | "Roll up budget by org hierarchy" |

---

## NoSQL Interview Preparation

### NoSQL Database Categories

| Category | Examples | Use Case | Consistency Model |
|----------|----------|----------|-------------------|
| Key-Value | Redis, DynamoDB, Memcached | Caching, sessions, leaderboards | Tunable |
| Document | MongoDB, Couchbase, Firestore | Content management, catalogs | Eventual / Strong |
| Wide-Column | Cassandra, HBase, Bigtable | Time-series, IoT, analytics | Eventual / Tunable |
| Graph | Neo4j, Amazon Neptune | Social networks, recommendations | ACID / Eventual |
| Search | Elasticsearch, Solr | Full-text search, log analytics | Near real-time |
| Multi-Model | Cosmos DB, ArangoDB | Mixed workloads | Tunable |

### NoSQL Interview Topics

**When to Choose NoSQL over RDBMS:**
- Schema flexibility needs (evolving data structures)
- Horizontal scalability requirements (web-scale)
- High write throughput (time-series, IoT)
- Simple key-value access patterns
- Graph traversal use cases

**CAP Theorem Trade-offs:**
- **CP systems:** HBase, MongoDB (with majority write concern)
- **AP systems:** Cassandra, DynamoDB (eventual consistency)
- **CA systems:** Traditional RDBMS (in single cluster)

**Consistency Models Comparison:**
| Model | Description | Examples |
|-------|-------------|----------|
| Strong | All reads see latest write | Spanner, FoundationDB |
| Eventual | Reads may see stale data | Cassandra, DynamoDB default |
| Causal | Causally related writes seen in order | Cosmos DB, DynamoDB |
| Read-your-writes | Read sees own writes | Session consistency |

---

## Database Design for System Design Interviews

### Design Framework

| Step | Activity | Questions to Ask |
|------|----------|-----------------|
| 1 | Understand requirements | Functional: what operations? Non-functional: scale, latency, consistency? |
| 2 | Choose database type | RDBMS vs NoSQL? Relational for structured data with joins; NoSQL for scale |
| 3 | Design schema | Tables/collections, relationships, access patterns |
| 4 | Plan for scale | Sharding, replication, caching strategy |
| 5 | Address availability | Multi-AZ, multi-region, failover strategy |
| 6 | Optimize performance | Indexing, materialized views, read replicas |

### Common Database System Design Questions

| Question | Database Choices | Key Design Decisions |
|----------|-----------------|---------------------|
| Design URL Shortener | PostgreSQL + Redis | Hash function, key generation, TTL-based cleanup |
| Design Twitter Feed | Cassandra + Redis | Fan-out strategy, timeline storage, ranking |
| Design Chat System | Cassandra + Redis + DB | Message ordering, presence, history retention |
| Design E-Commerce DB | PostgreSQL or CockroachDB | Product catalog, inventory, order states |
| Design Uber Backend | PostgreSQL + Redis + Cassandra | Geo-indexing, ride matching, pricing |
| Design YouTube/Netflix | MySQL + Cassandra + Blob Store | Metadata, video storage, recommendations |
| Design Online Bookstore | PostgreSQL + Elasticsearch | Catalog search, inventory, order management |
| Design Time-Series DB | InfluxDB / TimescaleDB | Storage format (columnar), downsampling, retention |
| Design Hotel Booking | PostgreSQL with partitioning | Availability, pricing, booking contention |

### Database Scalability Patterns

| Pattern | Description | Trade-offs |
|---------|-------------|------------|
| Read Replicas | Copies of master for read scaling | Eventual consistency, replication lag |
| Database Sharding | Horizontal partition by key | Cross-shard queries, resharding complexity |
| CQRS | Separate read/write models | Eventual consistency, complexity |
| Database Federation | Split by function (each microservice owns DB) | Cross-service queries, transactions |
| Materialized Views | Pre-computed query results | Data staleness, storage overhead |
| Caching Layer | Redis/Memcached in front of DB | Cache invalidation, cold start |

---

## 30/60/90 Day Study Plans

### 30-Day Intensive Plan (Oracle DBA Focus)

| Week | Focus | Daily Time | Activities |
|------|-------|------------|------------|
| 1 | Oracle Architecture | 3 hrs | Memory structures, processes, CDB/PDB, RMAN basics |
| 2 | Performance + SQL | 3 hrs | AWR, ASH, execution plans, SQL tuning, window functions |
| 3 | High Availability | 3 hrs | RAC, Data Guard, Flashback, partitioning |
| 4 | Interview Prep | 4 hrs | Mock interviews, LeetCode SQL, system design |

**Daily Schedule:**
- **Hour 1:** Architecture/HA concept study
- **Hour 2:** SQL practice on Live SQL or LeetCode
- **Hour 3:** Interview questions + mock whiteboarding

### 60-Day Balanced Plan

| Weeks | Focus | Deliverable |
|-------|-------|-------------|
| 1-2 | Oracle Foundation | Labs 01-07, architecture, SQL |
| 3-4 | Advanced Oracle | Labs 11-15, performance, HA, replication |
| 5-6 | Cloud + NoSQL | Labs 04, 05, 17-18 + company-specific prep |
| 7-8 | Interview Preparation | Mock interviews, LeetCode SQL, system design |

### 90-Day Thorough Plan

| Month | Focus | Deliverables |
|-------|-------|-------------|
| 1 | Database Foundation | Complete all 23 database labs |
| 2 | Company-Specific Deep Dive | Google/Microsoft/Amazon/AWS database services |
| 3 | Interview Readiness | 10+ mock interviews, 50+ SQL problems, 5 system design exercises |

### SQL Practice Plan
| Week | Topic | LeetCode Problems |
|------|-------|-------------------|
| 1 | Basic SELECT + JOIN | 175, 181, 182, 183, 197 |
| 2 | Aggregation + GROUP BY | 570, 577, 584, 586, 596 |
| 3 | Window Functions | 177, 178, 184, 185 |
| 4 | Complex Patterns | 180, 550, 569, 601 |
| 5 | CTE + Recursive | Recursive tree traversals, BOM |
| 6 | Advanced SQL | 262, 571, 579, 615 |

---

## Resources

### Books
| Title | Author | Focus |
|-------|--------|-------|
| Oracle Database 19c Administration | Bob Bryla | Oracle DBA fundamentals |
| Oracle SQL | Steven Feuerstein | SQL deep dive |
| Oracle PL/SQL Programming | Steven Feuerstein | PL/SQL comprehensive guide |
| Expert Oracle Database Architecture | Tom Kyte | Oracle architecture |
| Designing Data-Intensive Applications | Martin Kleppmann | Distributed database systems |
| Database Internals | Alex Petrov | Storage engines, replication |
| Seven Databases in Seven Weeks | Eric Redmond | Multi-DB comparison |

### Online Resources
| Platform | Use |
|----------|-----|
| Oracle Live SQL | Free SQL and PL/SQL practice |
| Oracle Dev Gym | SQL/PLSQL quizzes |
| LeetCode | SQL problems (50+ database problems) |
| HackerRank | SQL challenges (easy-hard) |
| O'Reilly Learning | Database books and videos |
| Coursera | Database management courses |

### Oracle Official Resources
| Resource | Link |
|----------|------|
| Oracle Database Documentation | docs.oracle.com/en/database/ |
| Oracle University | education.oracle.com |
| Oracle Learning Explorer | explore.oracle.com |
| Oracle Cloud Free Tier | High-tier autonomous database |

### Practice Tools
| Tool | Purpose |
|------|---------|
| Oracle Live SQL | Test SQL/PLSQL online |
| Docker Oracle | Run Oracle locally |
| Oracle VirtualBox | Build test environments |
| DbVisualizer | SQL IDE |
| SQL Developer | Oracle SQL IDE |

---

<div align="center">

**"Database fundamentals never go out of style — master the principles and tools are just details."**

---

[Back to Top](#databases-academy--cracking-the-interview-guide)

</div>
