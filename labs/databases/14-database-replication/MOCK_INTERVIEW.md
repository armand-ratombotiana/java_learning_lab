# Mock Interview: Database Replication (Lab 14)

**Role:** Database Engineer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is database replication and what are the main types?

**Candidate:** Database replication copies data from one database to another for redundancy, read scaling, and disaster recovery.

**Main types:**
1. **Synchronous replication:** Master waits for replica to confirm write. Zero data loss, higher latency.
2. **Asynchronous replication:** Master doesn't wait for replica. Lower latency, potential data loss.
3. **Semi-synchronous:** At least one replica confirms, rest async. Balance between safety and performance.

**Oracle-specific:**
- **Data Guard:** Physical (block-level) or logical (SQL-based) standby databases
- **GoldenGate:** Real-time, bidirectional, heterogeneous replication
- **Materialized views:** Snapshot-based replication

**Interviewer:** What is the difference between Oracle Data Guard and Oracle RAC?

**Candidate:**
| Feature | Data Guard | RAC |
|---------|-----------|-----|
| Purpose | Disaster recovery, HA | High availability, scalability |
| Architecture | Primary + Standby | Multiple instances, single database |
| Storage | Separate storage | Shared storage |
| Data | Replicated (near real-time) | Same data (Cache Fusion) |
| Failover | Minutes | Seconds (instance failure) |
| Use case | DR, reporting offload | High availability, scalability |

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you configure Oracle Data Guard with a physical standby?

**Candidate:** 
Primary side:
```sql
-- Enable forced logging
ALTER DATABASE FORCE LOGGING;

-- Enable archive logging
ALTER SYSTEM SET log_archive_dest_1='LOCATION=/u01/arch';
ALTER SYSTEM SET log_archive_dest_2='SERVICE=standby_host LGWR ASYNC VALID_FOR=(ONLINE_LOGFILES,PRIMARY_ROLE) DB_UNIQUE_NAME=standby';

-- Create standby redo logs (same size as online redo)
ALTER DATABASE ADD STANDBY LOGFILE SIZE 500M;
```

Standby side:
```sql
-- Start in nomount
STARTUP NOMOUNT;
-- Restore control file
RMAN> RESTORE CONTROLFILE FROM 'primary_controlfile.bkp';
-- Mount and start recovery
ALTER DATABASE MOUNT;
RECOVER MANAGED STANDBY DATABASE DISCONNECT FROM SESSION;
```

**Interviewer:** How does RMAN backup work with Oracle Data Guard?

**Candidate:** 
- **Backup offload:** Run RMAN backups on standby to reduce primary load
- **Blocks change tracking:** Enable on standby for faster incremental backups
- **Catalog:** Use RMAN catalog for cross-environment recovery
- **Script:** `BACKUP INCREMENTAL LEVEL 0 DATABASE;` on standby
- **Validate:** `RESTORE DATABASE VALIDATE;` to verify backup integrity

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a disaster recovery strategy for a financial database with RPO < 1 second and RTO < 5 minutes.

**Candidate:** 

**Oracle Maximum Availability Architecture (MAA):**

```
Primary Site (Prod DC)          Standby Site (DR DC)
┌──────────────────────┐        ┌──────────────────────┐
│  Oracle RAC (2 nodes)│        │  Oracle RAC (2 nodes)│
│  + Data Guard        │  SYNC  │  (Physical Standby)  │
│  + Flashback Database│◄──────►│  + Flashback Database│
│  + Active Data Guard │        │  + Active Data Guard │
│  + Fast-Start Failover│        │  + Fast-Start Failover│
│  + Oracle Sharding   │        │                      │
└──────────┬───────────┘        └──────────────────────┘
           │
    Data Guard Broker
    (Automatic failover)
```

**Key configuration:**
```sql
-- Synchronous redo transport (zero data loss)
ALTER SYSTEM SET log_archive_dest_2='SERVICE=DR_SITE SYNC AFFIRM DELAY=0';

-- Fast-Start Failover (automatic, < 5 min RTO)
CONFIGURE FAST_START FAILOVER;
ENABLE FAST_START FAILOVER;

-- Flashback Database for logical corruption recovery
ALTER DATABASE FLASHBACK ON;
```

**Failure scenarios:**
1. **Primary database crash:** Data Guard Broker auto-fails over to standby (RTO: ~3 min)
2. **Primary site loss:** DNS switch to DR site (RTO: ~5 min)
3. **Data corruption:** Flashback database to point before corruption (RTO: ~10 min)
4. **Logical error (drop table):** Flashback Table or TSPITR

**Testing:** Monthly DR drills: simulate site failure, verify failover, failback.

---

## Interviewer Feedback

**Strengths:** Deep replication knowledge, practical Data Guard setup, thorough DR design  
**Areas to Improve:** Could discuss Active Data Guard DML forwarding  
**Verdict:** Strong Hire

---

*Databases Lab 14 MOCK_INTERVIEW.md*
