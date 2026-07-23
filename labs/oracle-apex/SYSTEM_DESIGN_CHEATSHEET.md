# Oracle APEX — System Design Cheatsheet

<div align="center">

![System Design](https://img.shields.io/badge/System_Design-FF6F00?style=for-the-badge)
![Oracle APEX](https://img.shields.io/badge/Oracle_APEX-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-00758F?style=for-the-badge)

**System design patterns and interview templates for APEX/database roles**

</div>

---

## Table of Contents

1. [Low-Code Platform Design](#low-code-platform-design)
2. [Oracle Database Architecture](#oracle-database-architecture)
3. [APEX Architecture](#apex-architecture)
4. [Scalability Patterns](#scalability-patterns)
5. [High Availability](#high-availability)
6. [Security Architecture](#security-architecture)
7. [Integration Patterns](#integration-patterns)
8. [Cloud Deployment](#cloud-deployment)
9. [Interview Response Templates](#interview-response-templates)

---

## Low-Code Platform Design

### Architecture Components

```
┌─────────────────────────────────────────────────────┐
│                   Client Layer                        │
│  Browser (Desktop/Mobile)  │  REST Client  │  API    │
├─────────────────────────────────────────────────────┤
│                   APEX Layer                          │
│  ORDS (REST Data Services)  │  APEX Engine            │
│  Session Mgmt  │  Security  │  Caching  │  Templates │
├─────────────────────────────────────────────────────┤
│              Oracle Database Layer                    │
│  PL/SQL │ SQL │ Packages │ Collections │ Views       │
│  Indexes │ Partitions │ Materialized Views            │
├─────────────────────────────────────────────────────┤
│              Integration Layer                        │
│  REST APIs  │  Web Services  │  Email  │  BI Pub     │
│  File Upload/Download  │  OAuth2  │  LDAP            │
├─────────────────────────────────────────────────────┤
│              Infrastructure Layer                     │
│  OCI / AWS / Azure  │  Load Balancer  │  CDN         │
│  Data Guard  │  RAC  │  Backup & Recovery             │
└─────────────────────────────────────────────────────┘
```

### APEX vs Custom Development Decision Matrix

| Factor | Choose APEX | Choose Custom (Java/Node) |
|--------|-------------|---------------------------|
| Development speed | Days to weeks | Weeks to months |
| Data-centric apps | Strong fit | Moderate fit |
| Complex business logic | PL/SQL | Any language |
| Custom UI requirements | Limited by theme | Full control |
| Integration complexity | REST sources, ORDS | Full library support |
| Team skills | SQL/PLSQL | Full-stack dev |
| Performance reqs | Database-bound | Distributed possible |
| Cost | Low (Oracle DB) | Higher infrastructure |

**Interview answer template:** "I'd choose APEX when the application is data-centric, requires rapid delivery, and the team has strong SQL skills. I'd recommend custom development when the UI needs are complex, real-time processing demands distributed architecture, or the system must integrate with non-Oracle technologies."

---

## Oracle Database Architecture

### CDB/PDB Architecture

| Component | Purpose | APEX Relevance |
|-----------|---------|----------------|
| CDB (Container Database) | Root container managing multiple PDBs | One APEX instance per PDB |
| PDB (Pluggable Database) | Isolated database environment | Each app in its own PDB |
| Root (CDB$ROOT) | System metadata, Oracle-supplied objects | No APEX in root |
| Seed (PDB$SEED) | Template for new PDBs | Clone for dev/test APEX instances |
| Application Container | Application-defined PDB group | APEX multi-tenant apps |

```sql
-- Creating a PDB for a new APEX application
CREATE PLUGGABLE DATABASE apex_app1 ADMIN USER admin IDENTIFIED BY password;
ALTER PLUGGABLE DATABASE apex_app1 OPEN;
-- Install APEX into PDB
@apexins.sql APEX_APP1 APEX_APP1 TEMP /i/
```

### SGA/PGA Components

| Component | Description | Tuning for APEX |
|-----------|-------------|-----------------|
| Shared Pool | SQL/PLSQL parsing, execution plans | Large shared pool = faster parsing for APEX repetitive queries |
| Buffer Cache | Data block caching | Size by working data set; critical for APEX runtime |
| Redo Log Buffer | Transaction logging | Affects commit performance in APEX forms |
| Large Pool | Shared server, parallel query | ORDS uses large pool for session memory |
| Java Pool | JVM in database | Not used by APEX (minimize) |
| PGA (Private) | Session memory, sorts, hash joins | Increase for APEX report sorting/aggregation |

**Tuning formula (Interview):** "For APEX systems, allocate 40% of available memory to SGA and 60% to PGA. If APEX reports are heavy on sorting/aggregation, increase PGA_AGGREGATE_TARGET. If many concurrent APEX users parsing SQL, increase SHARED_POOL_SIZE."

### Background Processes

| Process | Role | APEX Impact |
|---------|------|-------------|
| DBWR | Writes dirty buffers to disk | Batch writes improve APEX insert perf |
| LGWR | Writes redo log | Commit-heavy APEX forms generate redo |
| SMON | System monitoring, recovery | Instance recovery after APEX restart |
| PMON | Process monitoring, cleanup | Cleans up dead APEX sessions |
| CKPT | Checkpoint signaling | DBWR writes triggered by checkpoints |
| MMON | Automatic workload repository | AWR reports for APEX performance analysis |
| ARCn | Archive log writer | Required for APEX production backups |

### ASM (Automatic Storage Management)

```sql
-- Create disk group for APEX data files
CREATE DISKGROUP apex_data NORMAL REDUNDANCY
DISK '/dev/sda1', '/dev/sdb1', '/dev/sdc1';

-- ASM best practices for APEX
-- 1. Separate disk groups for data, logs, flash recovery
-- 2. Use NORMAL or HIGH redundancy for production
-- 3. Allocate 20% free space for rebalancing
-- 4. Monitor ASM disk group usage
SELECT name, total_mb, free_mb, (total_mb - free_mb) * 100 / total_mb AS pct_used
FROM v$asm_diskgroup;
```

### Data Guard

```sql
-- Primary database
CONFIGURE DG_CONFIG;
CREATE PHYSICAL STANDBY DATABASE;

-- Switchover for APEX maintenance
ALTER DATABASE COMMIT TO SWITCHOVER TO STANDBY;
-- Zero-downtime APEX upgrades using Data Guard switchover

-- Data Guard benefits for APEX:
-- 1. Disaster recovery (RPO=0 with SYNC transport)
-- 2. Read-only standby for reporting APEX instances
-- 3. Rolling upgrades for zero downtime
-- 4. Offload backups to standby
```

### RAC (Real Application Clusters)

| Feature | Benefit for APEX | Config Tip |
|---------|-----------------|------------|
| Multiple instances | Horizontal scalability | Use service names for connection distribution |
| Cache fusion | No data partitioning needed | Minimize cross-instance contention |
| Load balancing | Distribute APEX sessions | Configure server-side load balancing |
| Failover | Automatic session failover | Use TAF (Transparent Application Failover) |

---

## APEX Architecture

### Rendering Lifecycle

```
Page Request
  │
  ├── Authentication ── Failed? → Login page
  │
  ├── Authorization (page-level)
  │
  ├── Session State Loading
  │
  ├── Page Rendering
  │   ├── Before Header  (processes)
  │   ├── After Header   (processes)
  │   ├── Region Rendering (regions → items)
  │   ├── After Footer   (processes)
  │   └── Dynamic Actions initialization
  │
  └── HTML Response → Browser
```

### Session State Management

```sql
-- How APEX manages session state:
-- 1. Session created on first page access
-- 2. State stored in APEX collections in database
-- 3. Page items cached in session, not URL
-- 4. AJAX calls update state via server callbacks

-- Key session state APIs
APEX_UTIL.GET_SESSION_STATE('P1_ITEM');      -- Get item value
APEX_UTIL.SET_SESSION_STATE('P1_ITEM', val);  -- Set item value
APEX_UTIL.CREATE_SESSION( ... );              -- Programmatic session

-- Session state protection levels
-- Unrestricted → Checksum → Checksum+Session → Restricted
```

### Caching Strategies

```sql
-- 1. Page Cache (entire page cached)
-- Set in: Page attributes > Cache > Cache Mode
-- Best: Static dashboards, public pages

-- 2. Region Cache (region output cached)
-- Set in: Region attributes > Cache
-- Best: Reports with static data, LOV regions

-- 3. Query Result Cache (database-level)
SELECT /*+ RESULT_CACHE */ department_id, AVG(salary)
FROM employees GROUP BY department_id;

-- 4. APEX Collection Cache (session-level)
APEX_COLLECTION.CREATE_COLLECTION_FROM_QUERY(
    p_collection_name => 'REPORT_CACHE',
    p_query => 'SELECT * FROM large_table',
    p_truncate_if_exists => TRUE
);

-- 5. CDN Cache (static files)
-- Configure CDN for /i/ (APEX images) directory
```

### ORDS Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  HTTP Client │────▶│   ORDS      │────▶│  Oracle DB   │
│  (Browser)   │     │  (Servlet)  │     │  (APEX Eng.) │
└─────────────┘     └─────────────┘     └─────────────┘
                         │
                    ┌────┴────┐
                    │ REST API│
                    │ Endpoints│
                    └─────────┘

ORDS Confuration Files:
- defaults.xml  (global settings)
- apex.xml      (APEX pool settings)
- <schema>.xml  (schema REST config)

Key ORDS Parameters:
- apex.pool.max-size         (connection pool size)
- apex.pool.initial-size     (initial connections)
- apex.pool.max-wait         (connection wait timeout)
- jdbc.InitialLimit          (starter connections)
- jdbc.MaxLimit              (max JDBC connections)
- jdbc.MaxStatementsLimit    (statement caching)
```

---

## Scalability Patterns

### Read Replicas

```sql
-- Configure standby database for read-only APEX workload
-- Primary: read/write APEX application
-- Standby: read-only reports, dashboards

-- Create database link to standby
CREATE DATABASE LINK standby_link
CONNECT TO apex_report IDENTIFIED BY password
USING 'standby_db';

-- In APEX report source, query standby
SELECT * FROM orders@standby_link
WHERE order_date >= SYSDATE - 30;

-- For automatic routing, use Oracle Active Data Guard
-- Far Sync for remote standby (async)
```

### Connection Pooling

```sql
-- ORDS connection pool configuration
-- In apex.xml:
-- <entry key="apex.pool.max-size">100</entry>
-- <entry key="apex.pool.initial-size">20</entry>
-- <entry key="apex.pool.max-wait">30000</entry>
-- <entry key="jdbc.MaxLimit">100</entry>

-- Connection pool sizing formula:
-- Pool Size = (Peak Concurrent Users × 1.2) / Average Page Time (sec) × 60

-- Example: 500 users, 5 sec page time
-- Pool = (500 × 1.2) / (5 × 60) = 2 (per ORDS instance)
-- With 3 ORDS instances: 6 connections total

-- UCP (Universal Connection Pool) for custom apps
Hashtable env = new Hashtable();
env.put("pool.initialPoolSize", "20");
env.put("pool.minPoolSize", "10");
env.put("pool.maxPoolSize", "100");
env.put("pool.timeToLiveConnectionTimeout", "3600");
```

### APEX Scalability Best Practices

| Practice | Impact | Implementation |
|----------|--------|----------------|
| Stateless pages | Reduces session overhead | Minimize session state items per page |
| Regional caching | Reduces DB queries | Enable for static/rarely-changing data |
| Pagination | Limits result sets | Set max rows per page (500-1000) |
| Bind variables | Reuses execution plans | Never concatenate SQL |
| Avoid N+1 queries | Reduces round-trips | Use single query with JOINs |
| Lazy loading | Faster initial render | Load heavy regions on demand |
| PL/SQL bulk operations | Reduces context switches | Use FORALL/BULK COLLECT |
| ORDS connection pooling | Maximizes throughput | Tune pool size per load test |
| Database resource manager | Controls concurrency | Limit parallel queries per session |

**Scalability architecture for 10K+ APEX users:**
```
Load Balancer (LB) → 4 ORDS Nodes → Active Data Guard → 1 Primary + 2 Standby
                    → CDN for static content → Redis for session cache
```

---

## High Availability

### Active Data Guard

| Feature | RPO | RTO | APEX Use Case |
|---------|-----|-----|---------------|
| Maximum Protection | 0 | Minutes | Financial apps |
| Maximum Availability | 0 | Seconds | Mission-critical apps |
| Maximum Performance | Seconds | Minutes | Standard enterprise apps |

```sql
-- Data Guard configuration for APEX
ALTER SYSTEM SET LOG_ARCHIVE_DEST_2='SERVICE=standby SYNC REOPEN=30';
ALTER SYSTEM SET LOG_ARCHIVE_DEST_STATE_2=ENABLE;

-- Switchover procedure
ALTER DATABASE COMMIT TO SWITCHOVER TO STANDBY WITH SESSION SHUTDOWN;
STARTUP MOUNT;
ALTER DATABASE OPEN READ ONLY;

-- APEX failover steps:
-- 1. Switch ORDS config to standby connection
-- 2. Promote standby to primary
-- 3. Restart ORDS
-- 4. Update APEX workspace connections
```

### RAC for APEX

```sql
-- Create service for APEX workload
DBMS_SERVICE.CREATE_SERVICE(
    service_name => 'apex_app',
    network_name => 'apex_app.service',
    goal => DBMS_SERVICE.GOAL_SERVICE_TIME,
    clb_goal => DBMS_SERVICE.CLB_GOAL_SHORT
);

-- Service attributes for APEX:
-- 1. Transaction affinity: session sticks to instance
-- 2. Load balancing: distribute new sessions
-- 3. Failover: TAF for session recovery
-- 4. Connection load balancing: runtime vs. server-side
```

### APEX Failover Strategies

| Strategy | Complexity | Downtime | Cost | When to Use |
|----------|------------|----------|------|-------------|
| Single instance + backups | Low | Hours | Low | Dev/test |
| Active Data Guard | Medium | Minutes | Medium | Production HA |
| RAC (2 nodes) | High | Seconds | High | 24/7 critical |
| Multi-region ADG | Very High | Minutes | Very High | Global DR |

---

## Security Architecture

### Authentication Methods

| Method | Implementation | Best For |
|--------|---------------|----------|
| APEX Accounts | Built-in user repository | Internal apps, small teams |
| LDAP | Directory Services | Enterprise with Active Directory |
| OAuth2 (Authorization Code) | APEX auth scheme + provider | Consumer-facing, SSO |
| OAuth2 (Client Credentials) | Server-to-server | REST API authentication |
| OpenID Connect | ID token validation | Modern SSO, Azure AD, Google |
| SAML 2.0 | SAML assertion | Enterprise SSO, Okta, OneLogin |
| Custom | PL/SQL function | Any custom logic |
| HTTP Header | Reverse proxy header | Kerberos/IWA, nginx auth |

### Authorization Schemes

```sql
-- Row-Level Security with VPD
BEGIN
    DBMS_RLS.ADD_POLICY(
        object_schema => 'APEX_APP',
        object_name => 'employee_salaries',
        policy_name => 'emp_salary_access',
        function_schema => 'APEX_APP',
        policy_function => 'auth_pkg.authorize_employee',
        statement_types => 'SELECT, UPDATE, DELETE',
        update_check => TRUE,
        enable => TRUE
    );
END;
/

-- VPD Policy Function
CREATE FUNCTION auth_pkg.authorize_employee(
    p_schema VARCHAR2, p_object VARCHAR2
) RETURN VARCHAR2 AS
BEGIN
    RETURN 'dept_id IN (
        SELECT dept_id FROM user_depts
        WHERE username = SYS_CONTEXT(''APEX$SESSION'', ''APP_USER'')
    )';
END;
```

### Data Encryption

```sql
-- Transparent Data Encryption (TDE)
ADMINISTER KEY MANAGEMENT CREATE KEYSTORE 'path' IDENTIFIED BY password;
ADMINISTER KEY MANAGEMENT SET KEYSTORE OPEN IDENTIFIED BY password;
ADMINISTER KEY MANAGEMENT SET KEY IDENTIFIED BY password WITH BACKUP;

-- Encrypt tablespace
CREATE TABLESPACE secure_data DATAFILE 'secure01.dbf'
ENCRYPTION USING 'AES256' DEFAULT STORAGE(ENCRYPT);

-- Column-level encryption
CREATE TABLE sensitive_data (
    id NUMBER PRIMARY KEY,
    ssn VARCHAR2(200) ENCRYPT USING 'AES256'
);

-- APEX-level encryption
-- Encrypt sensitive page items
APEX_CRYPTO.ENCRYPT(
    p_input => UTL_RAW.CAST_TO_RAW(:P1_SSN),
    p_key => UTL_RAW.CAST_TO_RAW(:GLOBAL_ENCRYPT_KEY),
    p_algorithm => APEX_CRYPTO.ENCRYPT_AES256
);
```

---

## Integration Patterns

### REST APIs

| Pattern | APEX Method | When to Use |
|---------|-------------|-------------|
| Consume external API | Web Source Module | Read external data |
| Expose data as API | ORDS RESTful Services | Mobile apps, 3rd party |
| Synchronize data | REST Data Sync | Offline-capable apps |
| OAuth2 authentication | Client Credentials flow | Server-to-server |
| Webhook callback | APEX_WEB_SERVICE | Event-driven integration |

**REST API Design for APEX (CRUD):**
```sql
-- ORDS Module
BEGIN
    ORDS.DEFINE_MODULE('orders_api', '/orders/', '1.0');
    
    -- GET /orders/ (list)
    ORDS.DEFINE_TEMPLATE('orders_api', 'orders', '');
    ORDS.DEFINE_HANDLER('orders_api', 'orders', 'GET',
        'SELECT * FROM orders ORDER BY order_date');
    
    -- GET /orders/:id (detail)
    ORDS.DEFINE_TEMPLATE('orders_api', ':id', '');
    ORDS.DEFINE_HANDLER('orders_api', ':id', 'GET',
        'SELECT * FROM orders WHERE order_id = :id');
    
    -- POST /orders/ (create)
    ORDS.DEFINE_HANDLER('orders_api', 'orders', 'POST',
        'BEGIN INSERT INTO orders(...) VALUES(...); :id := SQL%ROWCOUNT; END;');
    
    -- PUT /orders/:id (update)
    ORDS.DEFINE_HANDLER('orders_api', ':id', 'PUT',
        'BEGIN UPDATE orders SET ... WHERE order_id = :id; END;');
    
    -- DELETE /orders/:id (delete)
    ORDS.DEFINE_HANDLER('orders_api', ':id', 'DELETE',
        'BEGIN DELETE FROM orders WHERE order_id = :id; END;');
END;
```

### File Upload / Download

```sql
-- File upload to APEX (BLOB storage)
BEGIN
    INSERT INTO files (filename, mime_type, blob_content, created_by)
    VALUES (
        :P101_FILENAME,
        :P101_MIME_TYPE,
        :P101_FILE_BLOB,
        :APP_USER
    );
END;

-- File download (PL/SQL region)
BEGIN
    SELECT blob_content, filename, mime_type
    INTO l_blob, l_filename, l_mime
    FROM files WHERE file_id = :P101_FILE_ID;
    
    APEX_UTIL.DOWNLOAD_FILE(
        p_content_type => l_mime,
        p_file_name => l_filename,
        p_content => l_blob
    );
END;
```

### Email Integration

```sql
-- Send email from APEX
APEX_MAIL.SEND(
    p_to => 'user@example.com',
    p_from => 'noreply@company.com',
    p_subj => 'Order Confirmation #' || :P101_ORDER_ID,
    p_body_html => '<h1>Thank you for your order</h1><p>Total: $' || :P101_TOTAL || '</p>',
    p_cc => 'sales@company.com',
    p_bcc => 'archive@company.com'
);

-- Commit AND send
COMMIT;
APEX_MAIL.PUSH_QUEUE;
```

### BI Publisher Integration

```sql
-- Generate report via BI Publisher from APEX
DECLARE
    v_report CLOB;
BEGIN
    v_report := APEX_BP.GENERATE_DOCUMENT(
        p_template_name => 'Order_Invoice',
        p_template_type => 'RTF',
        p_output_type => 'PDF',
        p_data_source => 'SELECT * FROM orders WHERE order_id = :P101_ORDER_ID'
    );
    APEX_UTIL.DOWNLOAD_FILE(
        p_content_type => 'application/pdf',
        p_file_name => 'Invoice_' || :P101_ORDER_ID || '.pdf',
        p_content => v_report
    );
END;
```

---

## Cloud Deployment

### APEX on OCI

```
┌─────────────────────────────────────────────────┐
│                  OCI Region                       │
│                                                   │
│  ┌─────────┐    ┌─────────┐    ┌─────────┐      │
│  │  LBaaS  │───▶│  ORDS   │───▶│  ADB    │      │
│  │         │    │  Compute│    │ (APEX)  │      │
│  └─────────┘    └─────────┘    └─────────┘      │
│       │                                          │
│       ▼                                          │
│  ┌─────────┐                                     │
│  │  Object  │                                     │
│  │  Storage │                                     │
│  │ (Static) │                                     │
│  └─────────┘                                     │
│                                                   │
│  ┌─────────────────────────────────────────┐      │
│  │  Autonomous Database (APEX pre-installed)│      │
│  │  • Auto-scaling                          │      │
│  │  • Auto-backup                           │      │
│  │  • Auto-patching                         │      │
│  │  • Data Guard for DR                     │      │
│  └─────────────────────────────────────────┘      │
└─────────────────────────────────────────────────┘
```

### APEX on AWS / Azure

| Aspect | OCI | AWS | Azure |
|--------|-----|-----|-------|
| Native APEX | ADB with APEX | Manual install | Manual install |
| Database | Autonomous DB | RDS Oracle / EC2 | Azure VMs / OCI Interconnect |
| Load balancer | OCI LB | ALB/NLB | Azure LB / App Gateway |
| CDN | OCI CDN | CloudFront | Azure CDN |
| Static files | Object Storage | S3 | Blob Storage |
| Auth integration | OCI IAM | Cognito | Azure AD |
| DR | ADG | Cross-region RDS | Site Recovery |

**Architecture considerations for non-OCI:**
- Install ORDS on EC2/VMs behind load balancer
- Use RDS Oracle or self-managed Oracle on EC2
- Separate compute (ORDS) from storage (DB) for scalability
- Use CloudFront/CDN for APEX static files (/i/ directory)
- Monitor with CloudWatch/Azure Monitor + Enterprise Manager

---

## Interview Response Templates

### Template 1: "Design an APEX application architecture"

**Structure your answer:**
1. **Requirements clarification** — users, data volume, SLA, security
2. **Layer breakdown** — presentation (APEX), application (ORDS), data (Oracle)
3. **Component selection** — Interactive Grid vs Report, auth scheme, caching
4. **Scalability** — ORDS nodes, connection pooling, read replicas
5. **Security** — auth, authorization, data encryption, VPD
6. **High availability** — Data Guard, RAC, backup strategy

**Sample answer:** "I'd design a multi-tenant APEX application using Oracle Autonomous Database on OCI. Each tenant gets an application-level schema with VPD policies for row-level isolation. The architecture uses ORDS behind OCI Load Balancer with auto-scaling compute nodes. For high availability, Active Data Guard provides a read-only standby for reporting and failover."

### Template 2: "Migrate an existing app to APEX"

**Structure:**
1. **Assessment** — inventory forms/reports, data model, integration points
2. **Strategy** — Big Bang vs Phased vs Parallel
3. **Data migration** — SQL*Loader, Data Pump, GoldenGate
4. **APEX implementation** — recreate forms as pages, reports as IG
5. **Testing** — regression, performance, user acceptance
6. **Cutover** — rollback plan, data validation

### Template 3: "Performance tuning a slow APEX application"

**Structure:**
1. **Diagnose** — enable APEX debug, find slow queries
2. **Database** — check execution plans, missing indexes, full table scans
3. **APEX** — region caching, reduce items, pagination
4. **Network** — static files via CDN, image optimization
5. **Monitor** — set up AWR, APEX performance views

```sql
-- Performance diagnostic query
SELECT sql_id, elapsed_time, executions, elapsed_time/executions avg_ms
FROM v$sql
WHERE module LIKE '%APEX%'
  AND elapsed_time > 1000000  -- > 1 second
ORDER BY elapsed_time DESC;
```

### Template 4: "Security design for multi-tenant APEX app"

**Structure:**
1. **Authentication** — OAuth2/SSO with Azure AD or Okta
2. **Authorization** — role-based + VPD for row-level security
3. **Session management** — short timeouts, protection levels
4. **Data encryption** — TDE at rest, SSL in transit
5. **Audit** — `APEX_APPLICATION_AUDIT`, `FGA` policies
6. **Compliance** — GDPR (data masking), SOX (audit trails)

### Template 5: "When to use APEX vs alternative low-code platforms"

**Comparison criteria:**
| Criterion | APEX | Power Platform | ServiceNow | Mendix |
|-----------|------|---------------|------------|--------|
| Database | Oracle only | SQL Server | Its own | Any |
| Deployment | Cloud/On-prem | Cloud only | Cloud/On-prem | Cloud/On-prem |
| Custom code | PL/SQL, JS | Power Fx, C# | JavaScript | Java, JS |
| Enterprise integration | ORDS, REST | Connectors | REST, APIs | REST APIs |
| Pricing | All-inclusive | Per-user | Per-user | Per-app |

---

<div align="center">

**"System design for APEX is database architecture with a low-code wrapper — know the database, and the APEX part is straightforward."**

---

[Back to Top](#oracle-apex--system-design-cheatsheet)

</div>
