# Problem Walkthrough: EBS Architecture

## Problem 1: Database Tier Scaling — Company: Oracle
### EBS Interview Scenario
"You're at Oracle consulting for a large retail client running EBS 12.2 on-prem. Their application tier is experiencing severe slowdowns during month-end close. They have 2,000 concurrent users and the concurrent manager queue is backing up. They ask you to diagnose and fix the architecture."

### The Problem
The client's EBS environment has a single application tier node running on a mid-range server. The database tier runs on a dedicated Exadata machine. During month-end (last 3 days of the month), concurrent request completion times increase by 400%, and users report forms timing out. The DBA says the database is at 60% utilization, but the application tier CPU is pegged at 100%. The concurrent manager log shows "JTF_QUEUE_LOCK" contention.

### Solution Walkthrough
- Step 1: Gather AWR and OATM reports for both tiers
- Step 2: Identify bottleneck — application tier CPU saturation
- Step 3: Check concurrent manager configuration — find single "Standard Manager" handling all requests
- Step 4: Implement Application Tier Cloning — add 2 additional nodes behind a hardware load balancer
- Step 5: Reconfigure Concurrent Managers — create specialized managers (Report Manager, Interface Manager, etc.)
- Step 6: Enable JTF clustering for calendar-based conflict resolution
- Step 7: Configure Work Shifts to throttle non-critical requests during peak hours
- Step 8: Validate with stress test using Concurrent Program Load Testing tool

### Code
```sql
-- Check concurrent manager contention
SELECT cm.concurrent_queue_name,
       cm.running_processes,
       cm.max_processes,
       cqh.request_id,
       cqh.phase_code,
       cqh.status_code,
       cqh.hold_flag,
       (SYSDATE - cqh.actual_start_date) * 24 * 60 AS minutes_running
FROM   fnd_concurrent_queues cm,
       fnd_concurrent_processes cp,
       fnd_concurrent_requests cqh
WHERE  cm.concurrent_queue_id = cp.concurrent_queue_id
AND    cp.concurrent_process_id = cqh.concurrent_process_id
AND    cm.concurrent_queue_name LIKE 'STANDARD%'
AND    cqh.phase_code = 'R'
ORDER  BY minutes_running DESC;

-- Create specialized manager
BEGIN
  fnd_concurrent_queue_pub.create_queue(
    p_queue_name        => 'REPORT_MANAGER',
    p_application_id    => 0,
    p_max_processes     => 10,
    p_running_processes => 10,
    p_queue_size        => 100,
    p_specialization_on => 'Y',
    p_enabled_flag      => 'Y'
  );
END;
/
```

### Company Evaluation
- Oracle: Deep understanding of EBS application tier architecture, connection pooling (OPP), forms server tuning, and concurrent manager internals. Must know the difference between Forms, Web, and Admin tier processes.
- Deloitte: Implementation methodology including Application TCO analysis, capacity planning templates, and deployment topology recommendations (single-node vs multi-node).
- Accenture: Global rollout patterns — understanding how to scale EBS for multi-org, multi-set-of-books deployments across geographies.
- PwC: Compliance focus — ensuring SOX controls around concurrent manager access, audit trails for admin changes, and segregation of duties.
- Amazon: Migration patterns to AWS — EBS on EC2 with ASG for app tier, RDS for database, and how to architect for elasticity.

---

## Problem 2: Forms Server Tuning — Company: Deloitte
### EBS Interview Scenario
"You're at Deloitte implementing EBS 12.2 for a manufacturing client with 500 concurrent forms users. After go-live, users in the plant report forms loading in 45+ seconds, while office users experience sub-second loads. The client is threatening to reject the project."

### The Problem
The forms application takes disproportionately long for users on the manufacturing floor. All users share the same forms server configuration, but the plant users are on a separate VLAN with higher latency. The forms server is configured with default settings — 50 max connections, no caching enabled, and using HTTP (not HTTPS) with no compression.

### Solution Walkthrough
- Step 1: Analyze network latency between users and forms server using traceroute and Wireshark
- Step 2: Identify that plant VLAN has 150ms round-trip time vs 2ms for office VLAN
- Step 3: Tune forms runtime parameters in default.env and appsweb.cfg
- Step 4: Enable forms compression and reduce network round-trips
- Step 5: Implement forms session pooling with Oracle Forms Services configuration
- Step 6: Configure forms metric collection in OAM for ongoing monitoring

### Code
```sql
-- Check forms server configuration parameters
SELECT name, value, description
FROM   fnd_profile_option_values fpov,
       fnd_profile_options fpo
WHERE  fpov.profile_option_id = fpo.profile_option_id
AND    fpo.profile_option_name IN (
         'FND_FORMS_TIMEOUT',
         'FND_FORMS_MAX_CONNECTIONS',
         'FND_FORMS_SESSION_TIMEOUT',
         'FND_FORMS_RECORD_GROUP_SIZE',
         'FND_FORMS_LAUNCH_PAGE_SIZE'
       );

-- Update forms timeout for plant users
BEGIN
  fnd_profile.save(
    'FND_FORMS_TIMEOUT',
    600,
    'SITE'
  );
  COMMIT;
END;
/
```

### Company Evaluation
- Oracle: Deep forms server tuning knowledge — buffer cache, socket timeout, record group fetch limits, and forms listener port configuration.
- Deloitte: Performance benchmarking methodology, user acceptance testing with realistic network simulation, and rollback planning.
- Accenture: Global deployment patterns — understanding regional network characteristics and how to configure forms servers per region using profiles.
- PwC: Security controls — ensuring forms encryption, secure cookie configuration, and audit logging for forms sessions.
- Amazon: Migration of forms workloads to AWS WorkSpaces or AppStream 2.0, using ELB with sticky sessions.

---

## Problem 3: Concurrent Manager Architecture — Company: Oracle
### EBS Interview Scenario
"At Oracle, a healthcare customer reports that their concurrent manager processing has degraded over time. They have been running the same configuration for 3 years without review. The system handles 10,000 concurrent requests per day, and the backlog during peak hours exceeds 4 hours."

### The Problem
The client's concurrent manager is still using the default "STANDARD" manager with 20 processes. Specialized programs like "General Ledger Post" and "Inventory Cost" are competing with routine report requests. There are no work shifts defined, no conflict resolution, and no priority queuing. The FND_CONCURRENT_REQUESTS table has 15 million records with no purging.

### Solution Walkthrough
- Step 1: Review concurrent request breakdown by program type and priority
- Step 2: Analyze conflict lock table (FND_CONFLICT_DOMAIN) for contention
- Step 3: Create specialized managers for critical programs
- Step 4: Implement Work Shifts to allocate more resources during business hours
- Step 5: Enable conflict resolution rules to prevent resource conflicts
- Step 6: Purge old concurrent request data using standard purge programs
- Step 7: Implement request priority queuing

### Code
```sql
-- Purge old concurrent requests
BEGIN
  fnd_program_purge.purge_all(
    p_age_days    => 90,
    p_appl_id     => 0,
    p_mode        => 'BATCH',
    p_batch_size  => 10000,
    p_commit_size => 1000
  );
END;
/

-- Create work shift for peak hours
BEGIN
  fnd_concurrent_queue_pub.create_shift(
    p_queue_name    => 'STANDARD',
    p_shift_name    => 'PEAK_HOURS',
    p_start_time    => '08:00',
    p_end_time      => '18:00',
    p_max_processes => 30,
    p_enabled       => 'Y'
  );
END;
/
```

### Company Evaluation
- Oracle: Complete knowledge of concurrent manager architecture — internal managers, conflict resolution domains, target node mapping, and cache management.
- Deloitte: Implementation methodology for concurrent manager tuning as part of performance testing phase (AIM 7.0).
- Accenture: Experience with large-scale deployments requiring complex concurrent manager topology across multiple application tiers.
- PwC: Compliance — ensuring concurrent manager audit trails, request security by responsibility, and SOD conflict reporting.
- Amazon: Migration patterns — replacing concurrent managers with AWS Batch or Step Functions for cloud-native processing.
