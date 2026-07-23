# Mock Interview: EBS Technical Architecture (ebs-technical-architecture)

**Role:** Oracle EBS Technical Architect / DBA  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is the technology stack for Oracle EBS R12.2?

**Candidate:** Oracle EBS R12.2 technology stack:
- **Database:** Oracle Database 19c (R12.2.10+) or 12.2.0.2 (R12.2.9 and earlier)
- **Application Server:** Oracle WebLogic Server 12c (12.1.3) or 14c (R12.2.11+)
- **Oracle HTTP Server (OHS):** Apache-based HTTP server (12.1.3 or 12.2.1)
- **Forms & Reports:** Oracle Forms 12c, Reports 12c, BI Publisher 12c
- **Java:** Java SE 8 (minimum 8u251 for R12.2.10)
- **OA Framework:** Oracle Application Framework 12.2.x
- **ADOP:** Online patching framework
- **OPatch:** Database and application tier patching

**Interviewer:** How does Oracle HTTP Server (OHS) work with EBS?

**Candidate:** OHS serves as the web entry point for EBS:
1. Receives HTTP/HTTPS requests from users
2. Routes requests:
   - `/OA_HTML/` → OA Framework (J2EE via WebLogic)
   - `/forms/` → Forms Services (Forms Servlet)
   - `/reports/` → Reports Server
   - `/OA_CACHE/` → Static content (CSS, JS, images)
3. Load balancing across WebLogic managed servers
4. SSL termination (HTTPS)
5. URL rewriting and redirection
6. mod_plsql for PL/SQL-based applications

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain the WebLogic Server architecture in EBS R12.2.

**Candidate:** WebLogic Server architecture:
1. **Admin Server:** Central management console for the domain. Manages configuration of all managed servers.
2. **Managed Servers:** Run the EBS applications:
   - `oafm_server1/2`: OA Framework managed servers (cluster)
   - `forms_server1/2`: Forms Services managed servers
3. **Node Manager:** Lifecycle management for managed servers (start, stop, restart)
4. **Clustering:** Multiple managed servers for high availability
5. **Data Sources:** JDBC connection pools to the database

EBS R12.2 domain structure:
```
┌─────────────────────────────────────┐
│ WebLogic Domain: EBS_DOMAIN        │
├─────────────────────────────────────┤
│ Admin Server: AdminServer          │
│ Port: 7001                         │
├─────────────────────────────────────┤
│ Managed Server Cluster             │
│ - oafm_server1 (Port 7201)        │
│ - oafm_server2 (Port 7202)        │
├─────────────────────────────────────┤
│ Forms Server Cluster               │
│ - forms_server1 (Port 7301)       │
│ - forms_server2 (Port 7302)       │
└─────────────────────────────────────┘
```

**Interviewer:** How does the Concurrent Processing architecture work at the technical level?

**Candidate:** Concurrent Processing technical architecture:
1. **Internal Concurrent Manager (ICM):** Master controller, monitors all managers
2. **Concurrent Manager:** Processes requests (Standard, Specialization, Transaction)
3. **FNDLIBR:** Library that manages concurrent request processing
4. **Concurrent request flow:**
   - User submits request via web/forms → stored in `FND_RUN_REQUESTS`
   - ICM picks up request, assigns to appropriate manager
   - Manager picks up request for processing
   - Results written back to `FND_CONC_RELEASE` and logs
5. **Log files:** Standard output + log file in `$APPLCSF/$APPLLOG`
6. **Request status:** Pending, Running, Completed, Warning, Error, Terminated

---

## Round 3: Hard (15-25 minutes)

**Interviewer:** Design a high-performance EBS technical architecture that handles 5,000 concurrent users with sub-second page response times.

**Candidate:** 

**Performance architecture:**

```
Load Balancer (HTTPS offload)
    │
    ├── OHS 1 ─── WebLogic Clusters
    │              ├── oafm_cluster (4 managed servers)
    │              └── forms_cluster (2 managed servers)
    ├── OHS 2 ─── Forms Services
    └── OHS 3 (standby)
    
Application Tier (8 nodes):
    Node 1-2: WebLogic + OAFM (4 JVMs total)
    Node 3-4: Forms Services + Reports
    Node 5-6: Concurrent Processing (8 managers)
    Node 7: Admin Server
    Node 8: Batch/Reporting

Database Tier:
    Oracle RAC (2 nodes) with Active Data Guard
    128 GB SGA / 64 GB PGA
    24 CPU cores per node
    Exadata X10M or OCI DenseIO
```

**Key optimizations:**

1. **JVM tuning:**
```bash
OAFM_JVM_OPTIONS="-Xms4096m -Xmx4096m -XX:PermSize=512m -XX:MaxPermSize=512m"
FORMS_JVM_OPTIONS="-Xms2048m -Xmx2048m"
```

2. **WebLogic connection pool:**
```yaml
jdbc_connection_pool:
  initial_capacity: 50
  max_capacity: 200
  min_capacity: 20
  connection_reserve_timeout: 30s
  test_connections_on_reserve: true
```

3. **Database optimizer:**
```sql
ALTER SYSTEM SET optimizer_mode=ALL_ROWS;
ALTER SYSTEM SET db_cache_size=64G;
ALTER SYSTEM SET shared_pool_size=16G;
ALTER SYSTEM SET pga_aggregate_target=32G;
```

4. **OHS compression:**
```apache
LoadModule deflate_module
AddOutputFilterByType DEFLATE text/html text/css application/javascript
```

5. **Concurrent manager sizing:**
```sql
-- 8 Concurrent managers with total 40 processes
Standard Manager: 15 processes (general requests)
Inventory Manager: 5 processes
Order Import Manager: 5 processes
Payroll Manager: 5 processes
Report Manager: 5 processes
Program Manager: 5 processes (scheduled jobs)
```

**Monitoring stack:**
- Oracle Enterprise Manager (OEM) for end-to-end monitoring
- EBS built-in Site Level Monitor for performance diagnostics
- AWR (Automatic Workload Repository) reports weekly
- Real-time ADR (Automatic Diagnostic Repository) alerts

**Interviewer:** How do you troubleshoot a slow-performing OA Framework page?

**Candidate:** Tier-based troubleshooting:

1. **Client side:** Browser developer tools → Network tab → Identify slow requests
2. **OHS/WebLogic:**
   - Check `access_log` for response times
   - WebLogic Server log for thread stuck exceptions
   - JDBC connection pool monitoring (are connections waiting?)
3. **OA Framework:**
   - Enable OA Framework diagnostics: `FND: Debug Log = YES`
   - Check view object query performance in debug log
   - Profile `FND: OA Framework Tracing` for detailed timing
4. **Database:**
   - Identify SQL from the page request via `v$session` and `v$sql`
   - Run `EXPLAIN PLAN` on the slow SQL
   - Check for missing indexes or poor join order
5. **Forms:**
   - Forms trace file (trigger-based recording)
   - `formsweb.cfg` settings for connection pooling

Common causes: Missing index on multi-org column, N+1 query pattern in view objects, unoptimized LOV queries.

---

## Interviewer Feedback

**Strengths:** Deep technical architecture knowledge, practical performance design, systematic troubleshooting  
**Areas to Improve:** Could discuss Oracle Cloud Infrastructure (OCI) deployment architecture for EBS  
**Verdict:** Strong Hire

---

*EBS Technical Architecture MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
