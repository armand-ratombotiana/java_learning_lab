# Mock Interview: Relational Databases (Lab 01)

**Role:** Database Engineer (Junior/Mid)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Explain Oracle Database architecture — memory structures, background processes, and storage hierarchy.

**Candidate:** Oracle Database has three main layers:

**Memory structures:**
- **SGA (System Global Area):** Shared memory for all sessions. Includes Database Buffer Cache (data blocks), Redo Log Buffer (redo entries), Shared Pool (SQL, PL/SQL), Large Pool (backup, recovery), Java Pool, Streams Pool.
- **PGA (Program Global Area):** Private memory per session. Includes sort area, hash area, cursor state.

**Background processes:**
- **DBWR:** Writes dirty buffers from buffer cache to data files
- **LGWR:** Writes redo log buffer to online redo logs
- **SMON:** Instance recovery, coalesces free space, cleans temporary segments
- **PMON:** Process cleanup, releases resources for failed processes
- **CKPT:** Updates data file headers with checkpoint information
- **ARCn:** Archives filled redo logs (ARCHIVELOG mode)

**Storage hierarchy:** Database → Tablespace → Segment → Extent → Oracle Block (DB_BLOCK_SIZE).

**Interviewer:** What is the difference between OLTP and OLAP database design?

**Candidate:** 
- **OLTP (Online Transaction Processing):** Many small transactions, high concurrency, normalized schema. Optimized for INSERT/UPDATE. Row-based storage. Example: Bank transaction system.
- **OLAP (Online Analytical Processing):** Few complex queries, large scans, denormalized schema (star/snowflake). Optimized for SELECT/aggregation. Column-based storage possible. Example: Data warehouse.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain Oracle Multitenant architecture (CDB/PDB).

**Candidate:** Introduced in Oracle 12c:
- **CDB (Container Database):** Root container holding system metadata, SQL, PL/SQL
- **PDB (Pluggable Database):** User-created database that looks like a regular database to applications
- **Benefits:** Consolidation, rapid provisioning (clone PDB in seconds), resource management, isolation

Each PDB has its own default tablespace, data files, users, and application data but shares the CDB's background processes, SGA, and control file. PDBs can be unplugged/plugged across CDBs.

**Interviewer:** How does Oracle RAC work with Cache Fusion?

**Candidate:** RAC (Real Application Clusters) allows multiple instances to access the same database. Cache Fusion is the interconnect mechanism:
1. Each instance reads blocks into its own buffer cache
2. When Instance B needs a block modified by Instance A:
   - Instance A sends the current version directly via interconnect (no disk I/O)
   - This is much faster than writing to disk and reading back
3. Global Resource Directory (GRD) tracks block ownership across instances
4. Cache Fusion transforms physical reads into network transfers (which are faster)

Requires high-speed interconnect (10GbE+) and shared storage (ASM or clustered file system).

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a multi-tenant database architecture for a SaaS platform serving 1000+ customers with varying data volumes. Compare schema-per-tenant, database-per-tenant, and Oracle Multitenant approaches.

**Candidate:** 

**Option comparison:**

| Approach | Isolation | Management Overhead | Query Performance | Oracle Support |
|----------|-----------|---------------------|-------------------|----------------|
| Discriminator column | Low | Low | Medium (partitioning helps) | Standard table |
| Schema-per-tenant | Medium | Medium | Good | PDB alternative |
| Pluggable DB (PDB) | High | Medium | Excellent | Native Oracle feature |
| Database per tenant | Maximum | High | Excellent | Multiple databases |

**Recommended: Hybrid approach using Oracle Multitenant:**
```
CDB: PROD_CDB
├── PDB1: Tier 1 Customers (small, < 1GB each) — Shared PDB, partitioned by customer_id
├── PDB2: Tier 2 Customers (medium) — Individual schemas per customer
├── PDB3: Enterprise Customer A — Dedicated PDB
├── PDB4: Enterprise Customer B — Dedicated PDB
└── PDB5: Enterprise Customer C — Dedicated PDB
```

**Key decisions:**
- **Resource management:** Use CDB Resource Manager to limit PDB resource usage (CPU, I/O, parallel)
- **Data isolation:** Over 90% of queries use `V$PDBS`, no cross-tenant data leaks
- **Backup:** RMAN can backup individual PDBs, enabling per-tenant recovery
- **Scaling:** Clone PDB for new tenants in seconds; relocate PDB for high-usage tenants
- **Upgrades:** Patch the CDB, all PDBs benefit; or upgrade individual PDBs separately

**Interviewer:** How do you handle backup and recovery for this multi-tenant architecture?

**Candidate:** For 1000+ customers:
- **CDB-level backup:** Weekly full + daily incremental (using RMAN)
- **PDB-level backup:** Individual PDB backups enabled for enterprise customers
- **RPO:** 1 hour for enterprise, 24 hours for standard
- **RTO:** 1 hour for enterprise, 4 hours for standard
- **Recovery scenarios:**
  1. Single PDB corruption → `RMAN RECOVER PLUGGABLE DATABASE pdb_name`
  2. Accidental table drop → Flashback Table or TSPITR on PDB
  3. Entire CDB loss → Restore full CDB RMAN backup

---

## Interviewer Feedback

**Strengths:** Strong architectural knowledge, practical multi-tenant design, good understanding of Oracle features  
**Areas to Improve:** Could discuss Oracle 23c AI Vector Search capabilities  
**Verdict:** Strong Hire

---

*Databases Lab 01 MOCK_INTERVIEW.md — Part of Databases Academy Interview Preparation*
