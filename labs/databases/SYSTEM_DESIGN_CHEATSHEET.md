# Oracle Database — System Design Cheatsheet

<div align="center">

![System Design](https://img.shields.io/badge/Database_System_Design-FF6C37?style=for-the-badge&logo=oracle&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-000000?style=for-the-badge)

**Database-focused system design reference for interviews — Oracle-centric with multi-cloud perspective**

</div>

---

## Table of Contents

1. [Oracle Architecture Deep Dive](#1-oracle-architecture-deep-dive)
2. [High Availability](#2-high-availability)
3. [Scalability](#3-scalability)
4. [Performance Tuning](#4-performance-tuning)
5. [Security Architecture](#5-security-architecture)
6. [Cloud Architecture](#6-cloud-architecture)
7. [Data Modeling Patterns](#7-data-modeling-patterns)
8. [Interview Response Templates](#8-interview-response-templates)

---

## 1. Oracle Architecture Deep Dive

### Memory Structures — SGA

| Component | Contents | Configuration | Tuning Target |
|-----------|----------|---------------|---------------|
| **Database Buffer Cache** | Cached data blocks | `DB_CACHE_SIZE` | 90%+ hit ratio (from `v$sysstat:consistent gets - physical reads`) |
| **Shared Pool** | SQL, PL/SQL, data dictionary | `SHARED_POOL_SIZE` | Soft parse > 90% (`v$sysstat:parse count (total) vs (hard)`) |
| **Redo Log Buffer** | Redo entries before LGWR write | `LOG_BUFFER` | Wait for `log buffer space` should be minimal |
| **Large Pool** | RMAN, parallel query, shared server | `LARGE_POOL_SIZE` | Monitor `v$sgastat` for free memory |
| **Java Pool** | JVM in database | `JAVA_POOL_SIZE` | Needed only for Java stored procedures |
| **Streams Pool** | GoldenGate, Advanced Queuing | `STREAMS_POOL_SIZE` | Needed only for replication |
| **Fixed SGA** | Internal DB communication | Internal | Not configurable |
| **In-Memory Column Store** | Columnar format for analytics | `INMEMORY_SIZE` | For In-Memory option |

### Memory Structures — PGA

```sql
-- PGA: Private SQL areas (sort, hash joins, bitmap merges)
-- Automatic PGA management (default)
ALTER SYSTEM SET PGA_AGGREGATE_TARGET = 2G;

-- Workarea size policy per session (for bulk operations)
ALTER SESSION SET WORKAREA_SIZE_POLICY = MANUAL;
ALTER SESSION SET SORT_AREA_SIZE = 1048576;  -- 1 MB

-- Monitor PGA usage
SELECT * FROM v$pgastat;
-- Key metrics: aggregate PGA target parameter, total PGA allocated, maximum PGA allocated
```

### Background Processes

| Process | Function | Key Wait Events |
|---------|----------|-----------------|
| **DBWR** (DBW0–DBW9) | Writes dirty buffers from buffer cache to datafiles | `db file sequential read`, `db file scattered read` |
| **LGWR** | Writes redo log buffer to online redo logs | `log file sync`, `log file parallel write` |
| **CKPT** | Updates datafile headers with checkpoint SCN | `control file sequential read/write` |
| **SMON** | Instance recovery, temp segment cleanup, coalesces free extents | — |
| **PMON** | Cleans up failed processes, registers with listener | — |
| **ARCn** | Archives filled redo logs | `log file switch (archiving needed)` |
| **MMON** | AWR snapshots, ADDM analysis | — |
| **MMAN** | Automatic memory management | — |
| **LNSn** | Data Guard redo transport | `log file sync` on primary |

### ASM (Automatic Storage Management)

```
┌─────────────────────────────────────────────────┐
│                 Oracle Database                 │
├─────────────────────────────────────────────────┤
│  ASM Instance (ASMB, RBAL, O0nn processes)      │
├─────────────────────────────────────────────────┤
│  Disk Group 1 (DATA) │ Disk Group 2 (FRA)       │
│  ├─ disk01           │ ├─ disk01                │
│  ├─ disk02           │ ├─ disk02                │
│  └─ disk03           │ └─ disk03                │
├─────────────────────────────────────────────────┤
│  LUNs from Storage Array (SAN/NAS)              │
└─────────────────────────────────────────────────┘
```

- **Normal redundancy:** 2-way mirroring (requires 2 failure groups)
- **High redundancy:** 3-way mirroring (requires 3 failure groups)
- **External redundancy:** Rely on storage array RAID

### ACFS (ASM Cluster File System)

```sql
-- Create ACFS filesystem
CREATE DISKGROUP acfs_dg EXTERNAL REDUNDANCY DISK '/dev/sdb1';
ALTER DISKGROUP acfs_dg ADD VOLUME acfs_vol SIZE 100G;
EXEC DBMS_ACFS.CREATE_FILESYSTEM('+ACFS_DG/ACFS_VOL/', '/u01/app/oracle/acfs');
```

---

## 2. High Availability

### RAC (Real Application Clusters)

```
               ┌──────────┐     ┌──────────┐
               │ App Tier │     │ App Tier │
               └────┬─────┘     └────┬─────┘
                    │                │
               ┌────▼────────────────▼─────┐
               │     Oracle Listener        │
               │   SCAN Listener: 1521      │
               └────┬────────────────┬──────┘
                    │                │
          ┌─────────▼──┐      ┌─────▼──────────┐
          │ Node 1     │      │ Node 2          │
          │ Instance 1 │◄────►│ Instance 2      │
          │ (LGWR,     │      │ (LGWR,          │
          │  DBWR, ...)│      │  DBWR, ...)     │
          │            │      │                 │
          └─────────┬──┘      └─────┬───────────┘
                    │                │
                    └──────┬────────┘
                           │
               ┌───────────▼────────────┐
               │   Cache Fusion         │
               │   (Global Cache Service)│
               └────────────────────────┘
                           │
               ┌───────────▼────────────┐
               │   Shared Storage       │
               │   (ASM Disk Group)     │
               └────────────────────────┘
```

**Key RAC Concepts:**
- **Cache Fusion:** Direct block transfers between instances via interconnect
- **GCS (Global Cache Service):** Manages cache coherency across instances
- **GRD (Global Resource Directory):** Tracks which instance holds which resource
- **SCAN (Single Client Access Name):** Single name for all RAC nodes
- **Service:** Workload management (primary, read-only, DBA services)

### Data Guard

| Type | Protection Mode | Data Loss | Performance Impact | Use Case |
|------|----------------|-----------|-------------------|----------|
| **Physical Standby** | MAXIMUM PROTECTION | Zero | High (sync to both) | Zero data loss required |
| **Physical Standby** | MAXIMUM AVAILABILITY | Zero (if both up) | Medium | Most common production |
| **Physical Standby** | MAXIMUM PERFORMANCE | Possible (async) | Low | Cross-region DR |
| **Active Data Guard** | Any (+ read-only) | Same as above | Same as above | Reporting on standby |
| **Snapshot Standby** | N/A | Temporary divergence | — | Testing before activation |

```sql
-- Enable Data Guard
ALTER SYSTEM SET LOG_ARCHIVE_DEST_2 = 'SERVICE=standby_db SYNC AFFIRM DB_UNIQUE_NAME=standby1';
ALTER SYSTEM SET LOG_ARCHIVE_CONFIG = 'DG_CONFIG=(primary, standby1, standby2)';
ALTER DATABASE SET STANDBY DATABASE TO MAXIMIZE AVAILABILITY;

-- Switchover (planned role reversal)
ALTER DATABASE COMMIT TO SWITCHOVER TO STANDBY WITH SESSION SHUTDOWN;

-- Failover (unplanned, may lose data)
ALTER DATABASE ACTIVATE STANDBY DATABASE;
```

### Flashback Technologies

| Technology | Scope | Undo Mechanism | Time Frame |
|------------|-------|---------------|------------|
| **Flashback Query** | Row-level | UNDO | Minutes to hours |
| **Flashback Table** | Table-level | UNDO | Days (with undo retention) |
| **Flashback Drop** | Table | Recycle Bin | Indefinite (until purged) |
| **Flashback Database** | Entire DB | Flashback logs | Hours to days |
| **Flashback Transaction** | Transaction | UNDO + archive logs | Depends on undo retention |
| **Flashback Data Archive** | Historical queries | Dedicated tablespace | Years (configurable) |

### RMAN (Recovery Manager)

```sql
-- Backup
RMAN> BACKUP DATABASE PLUS ARCHIVELOG DELETE INPUT;
RMAN> BACKUP TABLESPACE USERS;
RMAN> BACKUP ARCHIVELOG ALL DELETE INPUT;

-- Incremental backup
RMAN> BACKUP INCREMENTAL LEVEL 0 DATABASE;  -- Full baseline
RMAN> BACKUP INCREMENTAL LEVEL 1 DATABASE;  -- Changed blocks only

-- Recovery
RMAN> RESTORE DATABASE;
RMAN> RECOVER DATABASE;
RMAN> RECOVER DATABASE UNTIL TIME "TIMESTAMP '2024-05-01 12:00:00'";
RMAN> RECOVER TABLESPACE USERS;

-- Validate backups
RMAN> VALIDATE BACKUPSET ALL;
RMAN> RESTORE DATABASE VALIDATE;  -- Check without restoring
```

---

## 3. Scalability

### Sharding (Oracle Sharding — 12cR2+)

```
┌──────────────────────────────────────────────┐
│              Shard Director                   │
│   (Global Service Manager + Connection Pool) │
└────┬──────────┬──────────┬────────────────────┘
     │          │          │
┌────▼────┐ ┌───▼────┐ ┌──▼───────┐
│ Shard 1 │ │Shard 2 │ │Shard N   │
│ (DB_A)  │ │(DB_B)  │ │(DB_N)    │
│ HR: 1-5 │ │HR: 6-10│ │HR: ...   │
└─────────┘ └────────┘ └──────────┘
     │          │          │
┌────▼──────────▼──────────▼────────────────────┐
│            Duplicated Tables (across all shards)│
│  (config tables, reference data)               │
└────────────────────────────────────────────────┘
```

- **System-managed sharding:** Consistent hash across shards
- **User-defined sharding:** Range/list partitioning to specific shards
- **Composite sharding:** Range then hash; good for geo-distribution

### Partitioning Strategies

| Type | Description | Best For | Example |
|------|-------------|----------|---------|
| **RANGE** | By value range | Time-series data | `PARTITION BY RANGE (sale_date)` |
| **HASH** | By hash function | Even data distribution | `PARTITION BY HASH (customer_id) PARTITIONS 8` |
| **LIST** | By discrete values | Region/category data | `PARTITION BY LIST (country)` |
| **RANGE-HASH** | Subpartitioning | Large fact tables | `RANGE(sale_date) SUBPARTITION BY HASH(customer_id)` |
| **RANGE-LIST** | Range then list | Multi-dimensional | `RANGE(sale_date) SUBPARTITION BY LIST(region)` |
| **INTERVAL** | Auto-create partitions | Rolling time windows | `PARTITION BY RANGE(sale_date) INTERVAL(NUMTOYMINTERVAL(1, 'MONTH'))` |

### Read Replicas

```
┌──────────────┐     ┌──────────────────┐
│  Primary DB   │     │  Active Data     │
│  (Read/Write) │────►│  Guard Standby   │
└──────────────┘     │  (Read-Only)     │
                     └──────────────────┘
                            │
                     ┌──────┴──────┐
                     │             │
               ┌─────▼──┐   ┌─────▼──┐
               │ Replica│   │ Replica│
               │    1   │   │    2   │
               └────────┘   └────────┘
```

**Oracle-specific:** Use Active Data Guard for read replicas + far sync for remote.

### Connection Pooling

| Technology | Description | Oracle-Specific Configuration |
|------------|-------------|-------------------------------|
| **DRCP** (Database Resident Connection Pool) | Pool inside the DB for shared server | `ALTER SYSTEM SET CONNECTION_BROKERS=(TYPE=DEDICATED,POOL_SIZE=100)` |
| **UCP** (Universal Connection Pool) | Java connection pool by Oracle | `PoolDataSource.setMinPoolSize(10); setMaxPoolSize(50);` |
| **OCI Session Pool** | C/C++ connection pool | `OCISessionPoolCreate()` |
| **Application-side pools** | HikariCP, Tomcat, WebLogic | Configure `maximum-pool-size` based on `(CPU * 2) + effective_storage_devices` |

---

## 4. Performance Tuning

### AWR (Automatic Workload Repository)

```sql
-- Generate AWR report (single instance)
EXEC DBMS_WORKLOAD_REPOSITORY.CREATE_SNAPSHOT();
SELECT * FROM TABLE(DBMS_WORKLOAD_REPOSITORY.AWR_REPORT_TEXT(123, 456, 789));

-- Compare two periods
SELECT * FROM TABLE(DBMS_WORKLOAD_REPOSITORY.AWR_DIFF_REPORT_TEXT(
    123, 100, 200,
    123, 201, 300
));

-- Top SQL by elapsed time
SELECT sql_id, elapsed_time_total, cpu_time_total, executions_total
FROM   dba_hist_sqlstat
WHERE  snap_id BETWEEN 100 AND 200
ORDER BY elapsed_time_total DESC
FETCH FIRST 20 ROWS ONLY;
```

### ASH (Active Session History)

```sql
-- Top wait events in last hour
SELECT event, COUNT(*) AS session_seconds
FROM   v$active_session_history
WHERE  sample_time >= SYSDATE - 1/24
GROUP BY event
ORDER BY COUNT(*) DESC;

-- Top SQL by active sessions
SELECT sql_id, COUNT(DISTINCT session_id) AS active_sessions
FROM   v$active_session_history
WHERE  sample_time >= SYSDATE - 1/24
GROUP BY sql_id
ORDER BY COUNT(DISTINCT session_id) DESC
FETCH FIRST 10 ROWS ONLY;
```

### ADDM (Automatic Database Diagnostic Monitor)

```sql
-- Run ADDM for last hour
EXEC DBMS_ADDM.ANALYZE_INST();

-- Run ADDM between specific snapshots
EXEC DBMS_ADDM.ANALYZE_INST(100, 200);

-- Review ADDM findings
SELECT * FROM dba_addm_findings ORDER BY impact DESC;
SELECT * FROM dba_addm_recommendations ORDER BY benefit DESC;
```

### SQL Tuning Advisor

```sql
-- Create tuning task
VARIABLE task_name VARCHAR2(100);
EXEC :task_name := DBMS_SQLTUNE.CREATE_TUNING_TASK(sql_id => 'abc123');
EXEC DBMS_SQLTUNE.EXECUTE_TUNING_TASK(task_name => :task_name);

-- View recommendations
SELECT DBMS_SQLTUNE.REPORT_TUNING_TASK(task_name => :task_name) FROM dual;

-- Implement recommended profile
EXEC DBMS_SQLTUNE.ACCEPT_SQL_PROFILE(task_name => :task_name);
```

### Index Strategy Decision Tree

```
Is the table queried frequently?
├── YES ──> Does the query filter on specific columns?
│           ├── YES ──> B-tree index on filter columns
│           │           └── Multiple filters ──> Composite index (cardinality order)
│           ├── YES + equality + range ──> Composite with equality first, range second
│           └── Low cardinality (< 1%) ──> Consider BITMAP index
├── Is the query sorted?
│   └── YES ──> Index on ORDER BY columns (avoids SORT)
├── Is it a join column?
│   └── YES ──> Index FK columns in child tables
└── Is it a text search?
    └── YES ──> Oracle Text (CONTEXT) index
```

### Optimizer Statistics

```sql
-- Gather statistics
EXEC DBMS_STATS.GATHER_TABLE_STATS('HR', 'EMPLOYEES');
EXEC DBMS_STATS.GATHER_SCHEMA_STATS('HR');
EXEC DBMS_STATS.GATHER_DATABASE_STATS;

-- Incremental statistics (partitioned tables)
EXEC DBMS_STATS.SET_TABLE_PREFS('HR', 'SALES', 'INCREMENTAL', 'TRUE');

-- Histograms for skewed data
EXEC DBMS_STATS.GATHER_TABLE_STATS('HR', 'SALES',
    METHOD_OPT => 'FOR ALL COLUMNS SIZE SKEWONLY');

-- Stale statistics monitoring
SELECT table_name, stale_stats, last_analyzed
FROM   dba_tab_statistics
WHERE  owner = 'HR';

-- Lock statistics (prevent change)
EXEC DBMS_STATS.LOCK_TABLE_STATS('HR', 'EMPLOYEES');
```

---

## 5. Security Architecture

### TDE (Transparent Data Encryption)

```
Application ──> Oracle Database
                      │
           ┌──────────▼──────────┐
           │  TDE                │
           │  ├─ Column         │  Encrypts columns (older)
           │  └─ Tablespace     │  Encrypts entire tablespace (preferred)
           └──────────┬──────────┘
                      │
           ┌──────────▼──────────┐
           │  Wallet / HSM       │
           │  (Master Key)       │
           └─────────────────────┘
```

```sql
-- Tablespace encryption (recommended)
CREATE TABLESPACE secure_data
DATAFILE '/u01/oradata/secure01.dbf' SIZE 100M
ENCRYPTION USING AES256
DEFAULT STORAGE(ENCRYPT);

-- Column encryption
CREATE TABLE credit_cards (
    id NUMBER,
    card_number VARCHAR2(16) ENCRYPT USING 'AES256'
);
```

### VPD (Virtual Private Database)

```sql
-- Create security policy function
CREATE FUNCTION auth_emp (p_schema VARCHAR2, p_object VARCHAR2)
RETURN VARCHAR2 AS
BEGIN
    RETURN 'department_id = SYS_CONTEXT(''USERENV'', ''DEPARTMENT_ID'')';
END;

-- Apply policy to table
EXEC DBMS_RLS.ADD_POLICY('HR', 'EMPLOYEES', 'dept_policy', 'HR', 'auth_emp',
                         'SELECT, UPDATE', TRUE);
```

### Audit Vault and Database Firewall

```
┌──────────────┐   ┌─────────────────┐   ┌──────────────┐
│ Database     │──►│ Database Firewall│──►│ Audit Vault  │
│ (production) │   │ (SQL inspection) │   │ Server       │
└──────────────┘   └─────────────────┘   └──────────────┘
                         │
                         ▼
                  ┌──────────────┐
                  │ Alert /      │
                  │ Block / Pass │
                  └──────────────┘
```

### Key Vault

Centralized key management for TDE, wallet files, and credentials across the Oracle stack.

---

## 6. Cloud Architecture

### Oracle Autonomous Database

| Feature | Serverless | Dedicated |
|---------|-----------|-----------|
| Scaling | Auto (compute + storage) | Manual (ECPU/OCPU adjust) |
| HA | Built-in (RAC + Data Guard) | Configurable |
| Patching | Automatic, zero-downtime | Automatic, zero-downtime |
| Indexing | Auto-indexing | Auto-indexing |
| Workload Type | OLTP, DW, JSON, APEX | Same |
| Best For | Dev/test, variable workloads | Predictable production |

### Exadata Cloud Service

```
┌──────────────────────────────────────────────────┐
│              Exadata Cloud Infrastructure         │
├──────────────────────────────────────────────────┤
│  Database Servers (x86) │ Storage Servers (cells)│
│  ├─ VM1 (RAC node 1)    │ ├─ Cell 1 (20 SSD)     │
│  ├─ VM2 (RAC node 2)    │ ├─ Cell 2 (20 SSD)     │
│  └─ VM3 (standby)       │ └─ Cell 3 (20 SSD)     │
├──────────────────────────────────────────────────┤
│  RDMA over Converged Ethernet (RoCE) interconnect │
└──────────────────────────────────────────────────┘
```

**Key features:** Smart Scan (offload SQL to storage), Storage Indexes, Hybrid Columnar Compression, InfiniBand interconnect.

### OCI Database Services

| Service | Oracle DB Engine | Use Case |
|---------|-----------------|----------|
| Base DB Service | YES — Single or RAC | Traditional lift & shift |
| Exadata Cloud Service | YES — RAC + Data Guard | High-performance, large databases |
| Autonomous Database | YES — Serverless or Dedicated | Self-managing, auto-scaling |
| MySQL HeatWave | MySQL + analytics | OLTP and analytics in one |
| NoSQL Database | Oracle NoSQL | Low-latency, simple key-value |

### Multi-Cloud / Hybrid Patterns

```
        On-Premise                  OCI
    ┌────────────────┐      ┌────────────────┐
    │ Oracle Primary  │─────►│ Oracle Standby  │
    │ (Active)        │      │ (Data Guard)    │
    └────────────────┘      └────────────────┘
             │
             │ Oracle GoldenGate (bidirectional)
    ┌────────▼────────┐      ┌────────────────┐
    │ Oracle DB        │─────►│ AWS RDS Oracle │
    │ (Reporting)      │      │ (Read replica) │
    └────────────────┘      └────────────────┘
```

---

## 7. Data Modeling Patterns

### Star Schema

```
                    ┌─────────────┐
                    │ Date Dim    │
                    │ (date_key)  │◄──────┐
                    └─────────────┘       │
                                          │
┌─────────────┐    ┌─────────────┐    ┌───┴──────────────┐
│ Product Dim │◄───│ Fact Sales  │    │ Customer Dim     │
│ (prod_key)  │    │ (date_key,  │    │ (cust_key)       │
└─────────────┘    │  prod_key,  │    └──────────────────┘
                   │  cust_key,  │
┌─────────────┐    │  quantity,  │
│ Store Dim   │◄───│  amount)    │
│ (store_key) │    └─────────────┘
└─────────────┘
```

### Snowflake Schema

```
┌─────────────┐    ┌─────────────┐                ┌─────────────┐
│ Category    │◄───│ Product Dim │                │ Region      │
│ Dim         │    └─────────────┘                │ Dim         │
└─────────────┘         │                         │ (region_key)│
                         │                         └──────┬──────┘
                         │         ┌─────────────┐        │
                         └────────┤ Fact Sales  │        │
                   ┌──────────────┤             │◄───────┘
                   │              └─────────────┘
            ┌──────▼──────┐             │
            │ Customer    │             │
            │ (cust_key)  │    ┌────────▼────────┐
            │ (addr_key)  │    │ Store Dim       │
            └──────┬──────┘    │ (store_key)     │
                   │           │ (region_key)    │
            ┌──────▼──────┐    └────────┬────────┘
            │ Address     │             │
            │ Dim         │    ┌────────▼────────┐
            └─────────────┘    │ Date Dim         │
                               └─────────────────┘
```

### 3NF (Third Normal Form)

```
┌───────────┐    ┌───────────┐    ┌───────────────┐
│ Customers │    │ Orders    │    │ Order_Items   │
├───────────┤    ├───────────┤    ├───────────────┤
│ cust_id   │───►│ ord_id    │───►│ ord_id        │
│ name      │    │ cust_id   │    │ product_id    │
│ email     │    │ ord_date  │    │ quantity      │
│ addr_id   │    │ status    │    │ unit_price    │
└───────────┘    └───────────┘    └───────────────┘
     │                                   │
┌────▼──────┐                    ┌───────▼──────┐
│ Addresses │                    │ Products     │
├───────────┤                    ├──────────────┤
│ addr_id   │                    │ product_id   │
│ street    │                    │ name         │
│ city      │                    │ category_id  │
│ state     │                    │ price        │
│ zip       │                    └──────────────┘
└───────────┘
```

### Data Vault 2.0

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│ Hub Customer │    │ Sat Customer │    │ Link         │
├──────────────┤    ├──────────────┤    │ Customer_Ord │
│ cust_id (PK)│───►│ cust_id (FK) │───►├──────────────┤
│ load_dts     │    │ load_dts     │    │ cust_id (FK) │
│ cust_code    │    │ name         │    │ ord_id (FK)  │
└──────────────┘    │ email        │    │ load_dts     │
                    └──────────────┘    └──────┬───────┘
                                               │
┌──────────────┐    ┌──────────────┐           │
│ Hub Order    │    │ Sat Order    │           │
├──────────────┤    ├──────────────┤           │
│ ord_id (PK)  │───►│ ord_id (FK)  │◄──────────┘
│ load_dts     │    │ load_dts     │
│ ord_code     │    │ status       │
└──────────────┘    │ total_amount │
                    └──────────────┘
```

### Slowly Changing Dimensions (SCD)

| Type | Strategy | Implementation |
|------|----------|---------------|
| **SCD Type 0** | Retain original | No changes (immutable dimension) |
| **SCD Type 1** | Overwrite | `UPDATE dim SET attr = new_value` — history lost |
| **SCD Type 2** | Add new row | New surrogate key, effective dates, current flag |
| **SCD Type 3** | Add new column | `ALTER TABLE ... ADD attr_previous, attr_current` |
| **SCD Type 6** | Hybrid (1+2+3) | Current + history columns plus row versioning |

---

## 8. Interview Response Templates

### Template: Design a Highly Available Oracle Database for a Global E-Commerce Platform

**1. Requirements Clarification**
```
- Read/write ratio: ~70% read, 30% write
- Data volume: ~5 TB, growing ~500 GB/year
- RPO: < 1 minute, RTO: < 15 minutes
- Regions: US-East, EU-West, AP-Southeast
- Compliance: PCI-DSS, GDPR
```

**2. High-Level Architecture**
```
┌──────────┐     ┌──────────┐     ┌──────────┐
│ US-East   │     │ EU-West  │     │ AP-SE    │
│ Primary   │◄────│ Active   │────►│ Standby  │
│ (RAC)     │     │ Standby  │     │ (Async)  │
│ Sync DG   │     │ (Max Avail)│   │ (Perf)   │
└─────┬─────┘     └──────────┘     └──────────┘
      │
┌─────▼─────┐
│ DR Site   │
│ Standby   │
└───────────┘
```

**3. Component Decisions**
| Component | Choice | Rationale |
|-----------|--------|-----------|
| Cluster | RAC (2 nodes) + Data Guard | RAC for scalability, DG for DR |
| Storage | ASM with normal redundancy | Automatic striping, mirroring |
| Backup | RMAN + FRA (Fast Recovery Area) | Automated backup management |
| Monitoring | Enterprise Manager + AWR + OEM | Comprehensive monitoring |
| Security | TDE (tablespace) + VPD + Audit Vault | Compliance + data separation |
| Partitioning | Interval-range on order_date | Automatic time-based partitions |
| Connection | DRCP (dedicated pool) | Manage thousands of concurrent users |
| Upgrade | Edition-based redefinition | Zero-downtime patching |

**4. Failure Scenarios**
```
Scenario 1: Primary node crash
  → RAC node 2 takes over, no downtime

Scenario 2: Full site failure (US-East)
  → Failover to EU-West Active Standby (15 min RTO)

Scenario 3: Corrupt data
  → Flashback Database to time before corruption

Scenario 4: Slow query
  → SQL Tuning Advisor → Add index → Update stats
```

### Template: Design a Data Warehouse for a Retail Company

```
Architecture Choices:
- Oracle Exadata for performance (Smart Scan + HCC)
- Star schema (fact + dimension tables)
- Partition by month, subpartition by region
- Compression: HCC Query High for fact tables
- ETL: Oracle Data Integrator (ODI) with GoldenGate for CDC
- Reporting: Materialized views with query rewrite
- Refresh: Incremental during day, full rebuild weekly
```

### Template: Migrate From SQL Server to Oracle

```
1. Assessment: SQL Server Migration Assistant (SSMA) for Oracle
2. Schema conversion: SSMA handles data types (DATETIME → DATE, etc.)
3. Stored procedure conversion: T-SQL → PL/SQL (CURSOR, BULK COLLECT)
4. Data migration: Oracle SQL Developer or GoldenGate
5. Validation: Row counts, checksums, application regression tests
6. Cutover: Minimal downtime via GoldenGate replication
7. Post-migration: Optimize indexes, gather stats, tune queries
```

### Template: Design a Database Security Architecture

```
1. Authentication: Oracle Database Vault + multi-factor
2. Encryption: TDE at tablespace level (AES256)
3. Access Control: VPD policies for multi-tenant isolation
4. Audit: Unified Audit (mandatory + standard)
5. Monitoring: Database Firewall for SQL injection prevention
6. Key Management: Oracle Key Vault for centralized encryption keys
7. Backup Encryption: RMAN with encrypted backupsets
8. Network: Oracle Net encryption + SSL/TLS
```

---

> **Pro Tip:** In system design interviews, always start by clarifying requirements (RPO/RTO, read/write ratio, data volume), then draw a high-level diagram, then dive into component decisions. Oracle interviewers especially care about RAC vs Data Guard trade-offs, ASM configuration, and flashback technologies.
