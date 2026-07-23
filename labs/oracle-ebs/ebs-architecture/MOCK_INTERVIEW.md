# Mock Interview: EBS Architecture (ebs-architecture)

**Role:** Oracle EBS System Administrator / Architect  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Explain the EBS R12.2 multi-tier architecture.

**Candidate:** Oracle EBS R12.2 has three tiers:
1. **Desktop Tier (Client):** End-user access via web browser (HTML-based forms, OA Framework pages), Forms applet (Java-based forms), or mobile applications. No EBS software installed locally.
2. **Application Tier (Middle):** Runs the business logic. Components:
   - Oracle HTTP Server (OHS) — static content, load balancing
   - WebLogic Server — hosts OA Framework (OAF), Oracle Application Framework
   - Forms Services — runs Oracle Forms (C++ based)
   - Concurrent Processing — manages background jobs
   - Admin Server — system administration console
3. **Database Tier:** Oracle Database (19c in R12.2.10+) with EBS-specific schemas (APPS, APPLSYS, etc.). Uses EBR (Edition-Based Redefinition) for online patching.

**Interviewer:** What is the file system structure in R12.2?

**Candidate:** R12.2 uses a two-file system structure:
1. **APPL_TOP:** Application executables and product files
2. **COMMON_TOP:** Common files shared across tiers (HTML, JARs, reports)
3. **INST_TOP:** Instance-specific configuration (context files, log directories)
4. **ORA_DB_TOP:** Database-specific files

Each file system has a "run" and "patch" edition for ADOP online patching. The active file system is the "run" edition; patches are applied to the "patch" edition.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does Edition-Based Redefinition (EBR) work in EBS R12.2?

**Candidate:** EBR is a database feature that allows multiple versions of database objects to exist simultaneously. In EBS R12.2:
1. When ADOP prepare runs, it creates a new edition (patch edition) in the database
2. DDL changes (new columns, modified packages) are applied to the patch edition
3. The patch edition has its own version of database objects, completely isolated from the run edition
4. During cutover, users are briefly disconnected, and the patch edition becomes the active edition
5. If the cutover fails, the system can revert to the run edition (rollback)

This enables online patching with minimal downtime (< 30 minutes for cutover), compared to hours of downtime with older AD Patch utility.

**Interviewer:** How does the Concurrent Manager architecture work?

**Candidate:** The Concurrent Manager processes background jobs in EBS:
1. **Internal Monitor Manager:** Top-level coordinator, monitors all concurrent managers
2. **Standard Manager:** Handles requests assigned to it via request types
3. **Specialization Managers:** Specific managers for particular request types (e.g., Inventory Manager, Order Import Manager)
4. **Transaction Managers:** For specific concurrent programs

The flow: User submits request → Internal Monitor assigns to appropriate manager → Manager processes request → Status updated in FND_RUN_REQUESTS. Managers can be configured for parallelism, priority, and time-based scheduling.

---

## Round 3: Hard (15-25 minutes)

**Interviewer:** Design a high-availability EBS architecture for a critical financial application used 24/7 across multiple time zones.

**Candidate:** 

**Architecture:**
```
Load Balancer (F5 / OCI Load Balancer)
        │
  ┌─────┴──────┐
  │    OHS     │
  │ (Active)   │──→ OHS (Standby)
  └─────┬──────┘
        │
  ┌─────┴──────────────────┐
  │  Application Tier      │
  │  ┌──────────────────┐  │
  │  │ WebLogic Cluster  │  │
  │  │ (2+ managed svrs)│  │
  │  ├──────────────────┤  │
  │  │ Forms Services   │  │
  │  │ (2+ instances)   │  │
  │  ├──────────────────┤  │
  │  │ Concurrent Proc. │  │
  │  │ (Multiple nodes) │  │
  │  └──────────────────┘  │
  └─────┬──────────────────┘
        │
  ┌─────┴──────────────┐
  │  Database Tier      │
  │  ┌──────────────┐  │
  │  │ RAC Cluster   │  │
  │  │ (2 nodes)    │  │
  │  └──────┬───────┘  │
  │         │          │
  │  ┌──────┴───────┐  │
  │  │ Data Guard    │  │
  │  │ (Standby)     │  │
  │  └──────────────┘  │
  └────────────────────┘
```

**High-availability strategies:**
- **Load balancer** for web tier (OHS) with health checks
- **WebLogic cluster** for OA Framework pages
- **Forms Services** load-balanced across multiple instances
- **RAC (Real Application Clusters)** for database failover
- **Data Guard** physical standby for disaster recovery
- **Concurrent managers** distributed across multiple app nodes
- **Shared APPL_TOP** on NAS for consistent code across nodes

**Disaster recovery:** Active-passive DR site with Data Guard sync replication. DNS failover in < 5 minutes.

**Interviewer:** How do you handle EBS patching in a high-availability environment?

**Candidate:** For 24/7 environments, patching strategy:
1. **Test environment first:** Clone production, apply patches to clone, test critical processes
2. **ADOP cycle in production:**
   - Prepare: Takes hours but no downtime (creates patch edition)
   - Apply: No downtime (applies to patch edition)
   - Finalize: Brief performance impact but no downtime
   - Cutover: ~15-30 minutes downtime window (schedule during lowest activity)
3. **Rollback plan:** If cutover fails, revert to run edition (EBR rollback)
4. **For critical security patches (CPU):** Use ADOP emergency patching mode with reduced validation
5. **Database patching (OPatch):** Apply to one RAC node at a time (rolling patch)
6. **Technology stack patching (Java, WebLogic):** Requires full application tier restart during maintenance window

---

## Interviewer Feedback

**Strengths:** Strong architecture knowledge, practical HA design, clear ADOP understanding  
**Areas to Improve:** Could discuss EBS on OCI vs on-premise architecture differences  
**Verdict:** Strong Hire

---

*EBS Architecture MOCK_INTERVIEW.md — Part of Oracle EBS Academy Interview Preparation*
