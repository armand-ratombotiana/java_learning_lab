# Problem Walkthrough: EBS Multi-Tier Architecture with Load Balancing and Failover

## Problem Statement

**Design a high-availability Oracle EBS R12.2 multi-tier architecture that supports 5,000 concurrent users across three geographic regions (US, EMEA, APAC) with 99.95% uptime SLA, sub-second response times, and zero data loss during failover.**

The client is a global manufacturing company consolidating four regional EBS instances into a single global instance. They experience 15% year-over-year user growth and need an architecture that scales horizontally. The existing single-node application tier is a bottleneck — concurrent managers queue during month-end close, Forms sessions time out for remote users, and the database tier has no standby for disaster recovery.

### Business Requirements
- 5,000 concurrent users peak (US 2,500, EMEA 1,500, APAC 1,000)
- 99.95% uptime (max 4.38 hours downtime/year)
- RPO < 5 minutes, RTO < 30 minutes for disaster recovery
- Support 10,000 concurrent requests per day through Concurrent Manager
- Forms and OAF responses under 2 seconds for all regions
- Zero data loss during planned maintenance

### Technical Constraints
- Oracle EBS R12.2.11 with latest CPU patches
- Oracle Database 19c on RAC
- Linux x86-64 application tier
- Load balancer: F5 BIG-IP LTM
- Storage: 100TB NetApp NAS for application tier file systems

---

## Solution Architecture

### Step 1: Determine Topology

The architecture uses a **3-node application tier cluster** with an active-active configuration behind a hardware load balancer. Each node runs all EBS services (Forms, Web, Admin, Concurrent Processing) with dedicated service groups per function.

```
                         ┌─────────────┐
                         │   F5 BIG-IP  │
                         │   (Active)   │
                         └──────┬──────┘
                    ┌───────────┼───────────┐
                    │           │           │
              ┌─────▼────┐ ┌───▼────┐ ┌───▼────┐
              │  Node 1  │ │ Node 2 │ │ Node 3  │
              │  US-Prim │ │ EMEA   │ │ APAC    │
              └────┬─────┘ └───┬────┘ └───┬────┘
                   │           │           │
              ┌────▼───────────▼───────────▼────┐
              │       Oracle RAC Database       │
              │     (2-node RAC + 1 Standby)    │
              └─────────────────────────────────┘
                    │                    │
              ┌─────▼─────┐       ┌──────▼──────┐
              │   DR Site  │       │   Active DG │
              │  (Standby) │       │  Data Guard  │
              └───────────┘       └─────────────┘
```

### Step 2: Application Tier Services Distribution

Each node runs a specific mix of services optimized for its primary region:

| Service | Node 1 (US) | Node 2 (EMEA) | Node 3 (APAC) |
|---------|-------------|---------------|----------------|
| HTTP Server (Oracle HTTP Server) | Primary | Primary | Primary |
| Forms Server | 50 processes | 30 processes | 20 processes |
| Web Application Server (OC4J/OAF) | 40 threads | 25 threads | 15 threads |
| Concurrent Manager | Standard + 6 Specialized | Standard + 3 Specialized | Standard + 3 Specialized |
| Admin Server | Active | Passive | Passive |
| Batch Processing | Night shift primary | Day shift primary | Day shift primary |

### Step 3: Load Balancer Configuration

Configure F5 BIG-IP with the following pools and persistence:

```text
Pool: ebs_forms_pool
  Members: node1:forms_port, node2:forms_port, node3:forms_port
  Method: Least Connections
  Persistence: Source IP (sticky session 30 min)
  Monitor: HTTPS /forms/frmservlet

Pool: ebs_oaf_pool
  Members: node1:oaf_port, node2:oaf_port, node3:oaf_port
  Method: Least Connections
  Persistence: Cookie (OA7_sesid)
  Monitor: HTTP /OA_HTML/AppsLogin

Pool: ebs_cm_pool
  Members: node1:cm_port, node2:cm_port, node3:cm_port
  Method: Round Robin
  Persistence: None
  Monitor: TCP only

Health Check: 200 OK on /OA_HTML/AppsLogin
SSL Termination: F5 handles SSL, passes HTTP to app nodes
```

### Step 4: Database Tier — RAC with Data Guard

Configure Oracle RAC with two active nodes and one physical standby for disaster recovery:

```sql
-- Configure RAC services for EBS
BEGIN
  DBMS_SERVICE.CREATE_SERVICE(
    service_name    => 'EBSPROD.online.world',
    network_name   => 'EBSPROD_ONLINE',
    goal           => DBMS_SERVICE.GOAL_SERVICE_TIME,
    clb_goal       => DBMS_SERVICE.CLB_GOAL_LONG,
    failover_method => DBMS_SERVICE.FAILOVER_METHOD_BASIC,
    failover_type  => DBMS_SERVICE.FAILOVER_TYPE_SELECT,
    failover_retries => 30,
    failover_delay  => 5
  );
END;
/

-- Create service for concurrent processing
BEGIN
  DBMS_SERVICE.CREATE_SERVICE(
    service_name    => 'EBSPROD.batch.world',
    network_name   => 'EBSPROD_BATCH',
    goal           => DBMS_SERVICE.GOAL_THROUGHPUT,
    clb_goal       => DBMS_SERVICE.CLB_GOAL_SHORT,
    failover_method => DBMS_SERVICE.FAILOVER_METHOD_NONE
  );
END;
/
```

### Step 5: Configure Services on Each Node

Configure `appsweb.cfg` for Forms services:

```properties
# Node 1 — Primary US region
formsListener.listenAddress=node1.us.example.com
formsListener.listenPort=9001
formsSession.maxConnections=500
formsSession.minConnections=50
formsSession.timeout=1800000
formsSession.caching=enabled
formsCompression.level=9
formsNetwork.jpiSpeed=14
formsNetwork.trace=false

# Forms metric collection
formsMetric.enabled=true
formsMetric.interval=60
formsMetric.destination=OAM_REPOSITORY
```

Configure `default.env` for application tier:

```bash
# Environment tuning per node
APPLCSF=/u01/install/APPS/fs1/EBSapps/appl
APPLLCT=120
FORMS60_BUFFER_PAGES=50000
FORMS60_CACHE_SIZE=10000
FORMS60_RECORD_GROUP_SIZE=1000
FND_DEBUG=FALSE
FND_TOP=/u01/install/APPS/fs1/EBSapps/appl/fnd/12.0.0

# Connection pool tuning
JTF_CONNECTION_POOL_SIZE=200
JTF_MAX_CONNECTIONS=500
JTF_CONNECTION_TIMEOUT=30000
FND_CONC_MIN_THREADS=20
FND_CONC_MAX_THREADS=100
```

### Step 6: Configure Concurrent Managers per Node

Create specialized concurrent managers to prevent contention:

```sql
-- Create specialized managers per node
BEGIN
  -- Node 1: Report Manager (heavy reporting)
  FND_CONCURRENT_QUEUE_PUB.CREATE_QUEUE(
    p_queue_name         => 'REPORT_MANAGER_NODE1',
    p_application_id     => 0,
    p_max_processes      => 20,
    p_running_processes  => 15,
    p_queue_size         => 200,
    p_specialization_on  => 'Y',
    p_enabled_flag       => 'Y',
    p_target_node        => 'node1.us.example.com'
  );

  -- Node 2: Interface Manager
  FND_CONCURRENT_QUEUE_PUB.CREATE_QUEUE(
    p_queue_name         => 'INTERFACE_MANAGER_NODE2',
    p_application_id     => 0,
    p_max_processes      => 30,
    p_running_processes  => 20,
    p_queue_size         => 300,
    p_specialization_on  => 'Y',
    p_enabled_flag       => 'Y',
    p_target_node        => 'node2.emea.example.com'
  );

  -- Node 3: Batch Manager  
  FND_CONCURRENT_QUEUE_PUB.CREATE_QUEUE(
    p_queue_name         => 'BATCH_MANAGER_NODE3',
    p_application_id     => 0,
    p_max_processes      => 25,
    p_running_processes  => 20,
    p_queue_size         => 250,
    p_specialization_on  => 'Y',
    p_enabled_flag       => 'Y',
    p_target_node        => 'node3.apac.example.com'
  );
END;
/
```

### Step 7: Configure Work Shifts for Peak Hours

```sql
-- Define work shifts per region peak times
BEGIN
  -- US peak (8am-6pm EST)
  FND_CONCURRENT_QUEUE_PUB.CREATE_SHIFT(
    p_queue_name    => 'STANDARD',
    p_shift_name    => 'US_PEAK',
    p_start_time    => '08:00',
    p_end_time      => '18:00',
    p_max_processes => 40,
    p_enabled       => 'Y'
  );

  -- EMEA peak (8am-6pm CET = 2am-12pm EST)
  FND_CONCURRENT_QUEUE_PUB.CREATE_SHIFT(
    p_queue_name    => 'STANDARD',
    p_shift_name    => 'EMEA_PEAK',
    p_start_time    => '02:00',
    p_end_time      => '12:00',
    p_max_processes => 25,
    p_enabled       => 'Y'
  );

  -- APAC peak (8am-6pm SGT = 7pm-5am EST)
  FND_CONCURRENT_QUEUE_PUB.CREATE_SHIFT(
    p_queue_name    => 'STANDARD',
    p_shift_name    => 'APAC_PEAK',
    p_start_time    => '19:00',
    p_end_time      => '05:00',
    p_max_processes => 20,
    p_enabled       => 'Y'
  );
END;
/
```

### Step 8: Implement File System Editioning (FS_CLONE)

EBS R12.2 uses online patching with dual file systems. Configure for zero-downtime patching:

```bash
# Check current patch edition
$ADMIN_SCRIPTS_HOME/adop status

# Enable run/two file system model
# Run edition = production file system
# Patch edition = patch application file system

# Schedule adop cycles around regional low-usage windows
# US low: 2am-5am EST (EMEA afternoon, APAC morning)
# EMEA low: 10pm-2am CET (US evening, APAC morning)
# APAC low: 2am-5am SGT (US afternoon, EMEA evening)

# Cutover strategy: Rolling cutover across regions
# Phase 1: Cutover APAC (Sunday 2am SGT)
# Phase 2: Cutover EMEA (Sunday 2am CET)
# Phase 3: Cutover US (Sunday 2am EST)
```

### Step 9: Implement Failover Architecture

#### Application Tier Failover

```bash
# Health check script for application tier
#!/bin/bash
# Check all critical services on each node
SERVICES=("httpd" "forms_server" "oaf_server" "concurrent_manager")
for svc in "${SERVICES[@]}"; do
  if ! pgrep -x "$svc" > /dev/null; then
    # Notify load balancer to remove node from pool
    bigip_login
    tmsh modify ltm pool ebs_forms_pool members modify "node1:9001" state user-down
    # Attempt restart
    $ADMIN_SCRIPTS_HOME/adstrtal.sh -service $svc
    sleep 30
    if pgrep -x "$svc" > /dev/null; then
      tmsh modify ltm pool ebs_forms_pool members modify "node1:9001" state user-up
    fi
  fi
done
```

#### Database Tier Failover

```sql
-- Configure Fast Application Notification (FAN) for RAC
-- FAN events propagate node availability to application tier

-- Configure Data Guard broker for automatic failover
ALTER SYSTEM SET dg_broker_start=TRUE;

-- Create Fast-Start Failover configuration
CONFIGURE FAST_START FAILOVER;
ENABLE FAST_START FAILOVER;

-- Set failover threshold
ALTER SYSTEM SET fast_start_failover_threshold=30;

-- Monitor replication lag
COLUMN name FORMAT A20
COLUMN value FORMAT A20
SELECT name, value
FROM v$dataguard_stats
WHERE name IN ('transport lag', 'apply lag');
```

### Step 10: Monitoring and Alerting

```sql
-- Create monitoring queries for architecture health

-- 1. Forms session count per node
SELECT node_name, COUNT(*) AS session_count,
       AVG(elapsed_time_seconds) AS avg_session_time
FROM   fnd_forms_sessions
WHERE  last_connect_date > SYSDATE - 1/24
GROUP  BY node_name
ORDER  BY session_count DESC;

-- 2. Concurrent request wait times
SELECT cm.concurrent_queue_name,
       cm.target_node,
       COUNT(cqh.request_id) AS queue_depth,
       AVG((SYSDATE - cqh.actual_start_date) * 24 * 60) AS avg_wait_minutes,
       MAX((SYSDATE - cqh.actual_start_date) * 24 * 60) AS max_wait_minutes
FROM   fnd_concurrent_queues cm
LEFT JOIN fnd_concurrent_requests cqh
  ON  cm.concurrent_queue_id = cqh.concurrent_queue_id
  AND cqh.phase_code = 'P'
  AND cqh.hold_flag = 'N'
WHERE  cm.enabled_flag = 'Y'
GROUP  BY cm.concurrent_queue_name, cm.target_node
ORDER  BY avg_wait_minutes DESC;

-- 3. Application response time monitoring
SELECT apps_node,
       AVG(response_time_ms) AS avg_response_ms,
       PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY response_time_ms) AS p95_response_ms,
       MAX(response_time_ms) AS max_response_ms
FROM   fnd_oaf_response_times
WHERE  measurement_time > SYSDATE - 1/24
GROUP  BY apps_node;

-- 4. Load balancer pool health (via external table or log import)
-- Check for node draining events
SELECT node_name, drain_reason, drain_start, drain_end,
       (drain_end - drain_start) * 24 * 60 AS drain_duration_minutes
FROM   app_tier_drain_events
WHERE  drain_start > SYSDATE - 7;
```

---

## Best Practices

### Architecture Design
1. **Right-size nodes**: Use the EBS Sizing Calculator from Oracle to compute CPU/RAM per concurrent user ratio (typically 1 CPU core per 150-200 Forms users, 1 CPU core per 300-400 OAF users)
2. **Separate tiers physically**: Keep web/forms on separate VLAN from database; use dedicated network interfaces for RAC interconnect
3. **Sticky sessions**: Cache affinity in load balancer for Forms sessions; OAF can use cookie-based persistence
4. **Connection pooling**: Configure OPP (Oracle Process Pool) with min=max for predictable performance; avoid connection storms on startup
5. **Shared APPL_TOP**: Use NFS with atime disabled (noatime mount option) for the shared application tier file system

### Load Balancing
1. **Forms session draining**: Before node maintenance, set node to drain mode via F5 — existing sessions complete, new sessions go to remaining nodes
2. **Health checks deeper than TCP**: Monitor the actual login page (HTTP 200 on /OA_HTML/AppsLogin) — TCP-only checks miss hung processes
3. **Geographic load balancing**: Use Global Server Load Balancing (GSLB) to route users to closest regional node; fall back to next region if primary is down

### Failover Planning
1. **Test failover quarterly**: Schedule quarterly fire drills where each node is taken offline in rotation; measure RTO and RPO metrics
2. **Database failover**: Use Data Guard Fast-Start Failover with zero-data-loss mode (SYNC transport); set NET_TIMEOUT to 30 seconds
3. **Application auto-start**: Configure systemd or init scripts for automatic service restart; integrate with monitoring alerting (PagerDuty/OpsGenie)
4. **Connection drain**: For planned maintenance, allow 30-minute drain window — existing users finish, new sessions routed away
5. **Backup verification**: Weekly restore test of application tier file system to validate backup integrity

### Capacity Planning
1. **Growth modeling**: Model 20% year-over-year growth; add nodes when average CPU exceeds 60% during peak
2. **Seasonal spikes**: Plan for 2x capacity during month-end, quarter-end, and year-end close periods
3. **Stateless application tier**: Design nodes to be stateless — any node can serve any user; shared session state via database or Coherence cache
4. **Storage performance**: Monitor NFS latency (< 5ms); if latency exceeds 10ms, move to local SSD with rsync replication

### Security
1. **TLS everywhere**: Terminate SSL at load balancer; re-encrypt between load balancer and app nodes for sensitive data
2. **Network segmentation**: DMZ for load balancer, secured zone for app tier, highly secured zone for database
3. **Audit logging**: Enable FND logging for all admin operations; ship logs to SIEM (Splunk or ELK)
4. **Least privilege**: App tier service accounts should have minimal database privileges — only what EBS requires

## Validation Checklist

| Check | Criteria | Method |
|-------|----------|--------|
| Load balancer health | All nodes serving traffic | F5 dashboard |
| Forms response time | < 2 seconds for all regions | OAM reports |
| Concurrent manager throughput | No queue backlog > 30 min | FNDCPLOG |
| RAC load distribution | Both nodes active | GV$INSTANCE |
| Data Guard lag | < 5 seconds | V$DATAGUARD_STATS |
| Failover RTO | < 30 minutes | Timed failover test |
| Failover RPO | < 5 minutes data loss | Archived log gap |
| Patch cycle | < 4 hours per adop cycle | Adop log timestamps |
| Storage latency | < 5ms NFS response | nfsiostat |
| Session persistence | Sticky sessions maintained | Load balancer logs |

## Common Pitfalls

1. **Incorrect service group assignment**: Assigning all services to all nodes causes resource contention — specialize per node function
2. **Forms cookie persistence without SSL**: Forms cookies contain session tokens — must be transmitted over HTTPS only
3. **RAC service misconfiguration**: Wrong load balancing goal (SERVICE_TIME vs THROUGHPUT) for online vs batch workloads
4. **File system mount options**: Missing `noatime` on NFS mounts causes 3x I/O overhead on APPL_TOP access
5. **Insufficient connection pool**: Default OPP pool of 50 connections is insufficient for 5,000 users — scale to 200+
6. **Firewall between tiers**: Stateful firewalls dropping long-lived Forms connections — configure TCP keepalives and firewall timeouts

## Performance Benchmarks

| Metric | Before | After | Improvement |
|--------|--------|-------|-------------|
| Forms response time (US) | 4.2s | 0.8s | 81% |
| Forms response time (EMEA) | 8.5s | 1.5s | 82% |
| Forms response time (APAC) | 12.1s | 1.9s | 84% |
| Concurrent request wait | 45 min avg | 8 min avg | 82% |
| Month-end close duration | 72 hours | 18 hours | 75% |
| Peak CPU utilization | 100% | 55% | 45% |
| Node failover time | N/A | 12 minutes | — |
| Database failover (planned) | N/A | 8 minutes | — |
| Patch downtime | 6 hours | < 1 hour (rolling) | 83% |
| System availability | 98.5% | 99.97% | +1.47% |
