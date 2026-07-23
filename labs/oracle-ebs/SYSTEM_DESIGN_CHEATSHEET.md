# System Design Cheatsheet — Oracle EBS

<div align="center">

![Oracle EBS](https://img.shields.io/badge/Oracle_EBS_System_Design-F80000?style=for-the-badge)
![Architecture](https://img.shields.io/badge/Architecture-R12.2-005C5C?style=for-the-badge)

**System design patterns and architecture decisions for EBS roles**

</div>

---

## Table of Contents

1. [EBS Architecture](#ebs-architecture)
2. [High Availability](#high-availability)
3. [Scalability](#scalability)
4. [Integration Patterns](#integration-patterns)
5. [Security Architecture](#security-architecture)
6. [Migration Architecture](#migration-architecture)
7. [Cloud vs On-Premise](#cloud-vs-on-premise)
8. [Customization Architecture](#customization-architecture)
9. [Interview Templates](#interview-templates)

---

## EBS Architecture

### Multi-Tier Architecture

```
┌─────────────────────────────────────────────────────────┐
│  DESKTOP TIER                                           │
│  ┌──────────┐ ┌────────────┐ ┌──────────┐              │
│  │ Forms    │ │ Web        │ │ BI       │              │
│  │ Client   │ │ Browser    │ │ Publisher│              │
│  │ (JInitiator/Forms 12c)│ │ (HTML/JS) │ │ (PDF/Excel)│              │
│  └────┬─────┘ └─────┬──────┘ └────┬─────┘              │
│       │             │             │                     │
├───────┼─────────────┼─────────────┼─────────────────────┤
│  APP  │             │             │                     │
│  TIER │     ┌───────▼─────────────▼──────┐              │
│       │     │   Oracle HTTP Server (OHS)  │              │
│       │     │   Load Balancer + Web Cache │              │
│       │     └───┬──────────────┬──────────┘              │
│       │         │              │                         │
│       │  ┌──────▼─────┐  ┌────▼──────────┐              │
│       │  │ OC4J /     │  │ Forms         │              │
│       │  │ OAF/BC4J   │  │ Listener (12c)│              │
│       │  │ (Java EE)  │  │ (forms90)     │              │
│       │  └──────┬─────┘  └──────┬────────┘              │
│       │         │               │                       │
│       │  ┌──────▼──────────────▼──────────┐              │
│       │  │  Concurrent Manager (CM)       │              │
│       │  │  Internal Monitor (FNDSM)      │              │
│       │  │  Conflict Resolution Manager   │              │
│       │  └──────────────┬─────────────────┘              │
│       │                 │                                │
├───────┼─────────────────┼────────────────────────────────┤
│  DB   │                 │                                │
│  TIER │  ┌──────────────▼──────────────────┐             │
│       │  │  Oracle Database (19c)          │             │
│       │  │  - Edition-Based Redefinition   │             │
│       │  │  - Virtual Private Database     │             │
│       │  │  - FNDSM Connections            │             │
│       │  │  - EBR / ADOP editioning        │             │
│       │  └─────────────────────────────────┘             │
└───────┴──────────────────────────────────────────────────┘
```

### Key Architecture Interview Points

| Component | Technology | Purpose |
|-----------|-----------|---------|
| OHS | Apache 2.4 (mod_plsql, mod_osso) | Reverse proxy, load balancing, SSO |
| OC4J | Oracle AS 10.1.3 | OAF/BC4J Java container |
| Forms Listener | Forms 12c | Serves Forms Applets |
| Concurrent Manager | FNDLIBR, FNDSM | Batch processing, background jobs |
| Database | Oracle 19c | Multi-org VPD, EBR editioning |
| FNDFS | FND File System | File I/O across nodes |

**Interview Tip:** When asked "design EBS for 10K users", start with Desktop tier (2 Web nodes → 2 Forms nodes), Apps tier (2 CM nodes), DB tier (RAC 2-node), shared APPL_TOP on NAS.

---

## High Availability

### HA Configurations

| Pattern | Description | RPO | RTO | Complexity |
|---------|-------------|-----|-----|------------|
| Cold Failover | Standby DB + passive app node | Hours | 4-8 hrs | Low |
| Warm Standby | Active Data Guard + app node in standby | Minutes | 1-4 hrs | Medium |
| Active-Passive (RAC) | RAC DB + active app nodes, passive standby app | Seconds | 30-60 min | High |
| Active-Active (RAC + LB) | RAC DB + all app nodes active with load balancer | Seconds | < 15 min | Very High |

### Load Balancing

```sql
-- Architecture decision: DNS round-robin vs hardware LB
-- Option A: DNS Round-Robin (cheap, no session stickiness)
--   2 web nodes: ebsapp1.ebs.com, ebsapp2.ebs.com
--   Problem: No session affinity for Forms/OAF

-- Option B: Hardware LB (F5 / BigIP) — recommended
--   Virtual IP: ebs.ebs.com → 10.0.1.100
--   Pool: ebsapp1:8000 (weight 100), ebsapp2:8000 (weight 100)
--   Persistence: SOURCE_IP or COOKIE (JSESSIONID)
--   Health Monitor: GET /OAF_LOGOUT/OAF_LOGOUT.jsp
```

### Disaster Recovery

```
Primary Site (Ashburn)                   DR Site (Phoenix)
┌──────────────────────┐                 ┌──────────────────────┐
│ EBS App Tier (prod1) │─── Data Guard ─▶│ EBS App Tier (stdby) │
│ EBS DB (RAC 2-node)  │───  ASYNC   ──▶│ EBS DB (RAC 2-node)  │
│ APPL_TOP (NAS)       │─── rsync   ──▶│ APPL_TOP (NAS)       │
│ Shared filesystem    │─── rync    ──▶│ Shared filesystem    │
└──────────────────────┘                 └──────────────────────┘
    │                                        │
    │  Global Traffic Manager (GTM)          │
    └──────────────────┬─────────────────────┘
                       │
                 ┌─────▼─────┐
                 │  Users    │
                 └───────────┘
```

**Interview Tip:** R12.2 complicates DR — both RUN and PATCH editions must be replicated. Use ADOP cutover to sync edition states before failover.

---

## Scalability

### Cloning Strategies

| Clone Type | Method | Use Case | Downtime |
|-----------|--------|----------|----------|
| Cold Clone | Shutdown → copy files → startup | Patch testing | Full downtime |
| Hot Clone | RMAN backup + APPL_TOP copy | Patching, upgrades | DB backup window |
| Rapid Clone | adcfgclone.pl | New environment creation | Varies |
| Snapshot Clone | Storage snapshot + post-clone | Quick test/dev | Minutes |

### Patching Strategy for Scale

```
ADOP Cycle for Large Deployments (50M+ GL records):
1. PREPARE  — 2 hrs (run on PATCH edition)
2. APPLY    — 4-8 hrs (parallel workers = CPU count × 2)
3. FINALIZE — 1 hr (sync edition columns)
4. CUTOVER  — 15 min (switch RUN → PATCH edition)
5. CLEANUP  — 30 min

Guidelines:
- Use ADOP parallel workers: adopparallelworkers=8 for 16-core DB
- Stage patches on alternate APPL_TOP
- For 12.2.x: always validate edition before cutover
- Consider online patching (ADOP with EBR) for zero-downtime
```

### Table Partitioning for EBS Scale

```sql
-- Partition large EBS tables — interview-ready design
-- GL_BALANCES: 100M+ rows → Range by period
CREATE TABLE gl_balances_part (
  ledger_id NUMBER, period_name VARCHAR2(15),
  actual_flag VARCHAR2(1), currency_code VARCHAR2(15),
  period_net_dr NUMBER, period_net_cr NUMBER
) PARTITION BY RANGE (period_name)
  SUBPARTITION BY LIST (actual_flag) (
    PARTITION p_jan VALUES LESS THAN ('FEB') (
      SUBPARTITION p_jan_actual VALUES ('A'),
      SUBPARTITION p_jan_budget VALUES ('B'),
      SUBPARTITION p_jan_encumbrance VALUES ('E')
    ),
    PARTITION p_feb VALUES LESS THAN ('MAR') (
      SUBPARTITION p_feb_actual VALUES ('A'),
      SUBPARTITION p_feb_budget VALUES ('B'),
      SUBPARTITION p_feb_encumbrance VALUES ('E')
    )
  );

-- MTL_MATERIAL_TRANSACTIONS: 200M+ → Range by date, hash subpartition
CREATE TABLE mtl_transactions_part (
  transaction_id NUMBER, inventory_item_id NUMBER,
  organization_id NUMBER, transaction_date DATE,
  transaction_quantity NUMBER
) PARTITION BY RANGE (transaction_date) INTERVAL (NUMTODSINTERVAL(7, 'DAY'))
  SUBPARTITION BY HASH (organization_id) SUBPARTITIONS 4;
```

### Concurrent Manager Tuning

| Parameter | Range | Impact |
|-----------|-------|--------|
| INIT_SQLLDR | 1-5 | SQL\*Loader throughput |
| MAXPRCS (FNDSM) | CPU×2 | max processes per CM |
| SLEEP_TIME | 30-120s | Polling interval |
| COMBINATION_QUEUE_SIZE | 10-100 | Batch grouping |
| CP_CACHE_SIZE | 100-500 | Shared cursor reuse |

---

## Integration Patterns

### Oracle SOA Suite with EBS

```
                   ┌──────────────┐
                   │  Oracle SOA  │
                   │   Suite 12c  │
                   │ ┌──────────┐ │
  Sender ──SOAP──▶│ │BPEL PM   │ │──DB Adapter──▶ EBS API
  System           │ │Mediator  │ │                (INV_TXN_MANAGER_PUB)
                   │ │OSB       │ │──JCA Adapter──▶ EBS Open Interface
                   │ └──────────┘ │                (OE_ORDER_PUB)
                   └──────────────┘
```

### EDI / XML Gateway

```
Trading Partner ──EDI 850──▶ EDI Gateway ──▶ XML Gateway ──▶ EBS PO Module
    (PO)                    (ecGateway)      (ECX tables)
                                                        ┌──────────────────┐
                                                        │ ECX_OUTBOUND     │
                                                        │ ECX_TRANSACTIONS │
                                                        │ ECX_LINE_DETAILS │
                                                        └──────────────────┘
```

### Open Interfaces / APIs

| Interface | Direction | Table / API | Frequency |
|-----------|-----------|-------------|-----------|
| Payables Open Interface | Inbound | AP_INTERFACE_CONTROL, AP_INVOICES_INTERFACE | Daily batch |
| Receivables Open Interface | Inbound | RA_INTERFACE_LINES_ALL | Real-time |
| Purchase Order | Inbound | PO_HEADERS_INTERFACE, PO_LINES_INTERFACE | Near-real |
| Order Import | Inbound | OE_HEADERS_IFACE_ALL, OE_LINES_IFACE_ALL | Batch |
| GL Interface | Inbound | GL_INTERFACE | Periodic |
| HRMS Employee API | Bi-dir | PER_EMPLOYEE_API, PER_ASG_API | Real-time |

---

## Security Architecture

### Layered Security Model

```
┌──────────────────────────────────────────────┐
│  Layer 1: User Authentication                │
│  ┌────────┐ ┌─────────┐ ┌──────────┐        │
│  │Local   │ │Oracle AS│ │  SSO     │        │
│  │(FND)   │ │(OID/SSO)│ │(SAML/OAM)│        │
│  └───┬────┘ └────┬────┘ └────┬─────┘        │
│      └───────────┼───────────┘               │
├──────────────────┼───────────────────────────┤
│  Layer 2: Function Security                  │
│  Resp → Menu → Function → Form              │
│  fnd_user_resp_groups_direct                │
│  fnd_menu_entries_vl                        │
│  fnd_form_functions_ll                      │
├──────────────────────────────────────────────┤
│  Layer 3: Data Security (VPD)               │
│  MOAC: FND_MOBS, FND_MOBS_POLICY           │
│  VPD: FND_VPD_TABLES                       │
│  Row-level security via ORG_ID              │
├──────────────────────────────────────────────┤
│  Layer 4: Audit Trail                       │
│  fnd_audit_groups, fnd_audit_schemas       │
│  FND_LOGIN_RESPONSIBILITY, FND_LOG_EVENTS  │
│  fnd_concurrent_requests (audit trail)      │
└──────────────────────────────────────────────┘
```

### Audit Trail Design

```sql
-- Enable audit on AP_INVOICES_ALL
BEGIN
  fnd_audit_groups_pkg.insert_audit_group(
    p_audit_group_id => 100,
    p_table_name     => 'AP_INVOICES_ALL',
    p_audit_enabled  => 'Y'
  );
END;
-- Results go to: fnd_audit_groups, fnd_audit_schemas, FND_AUDIT_VALUES
-- Monitor via: fnd_audit_groups_v, FND_AUDIT_SCHEMAS_V
```

---

## Migration Architecture

### Upgrade Paths

```
12.1.3 ──────▶ 12.2.4 ──────▶ 12.2.12 ──────▶ 12.2.13
    │              │              │
    │  Downtime    │  Online      │  Online
    │  Upgrade     │  ADOP        │  ADOP
    │  (adpatch)   │  (adoppatch) │  (adoppatch)
    ▼              ▼              ▼
  ┌────────────────────────────────────────┐
  │  Upgrade Steps:                        │
  │  1. Pre-upgrade patch (DB)            │
  │  2. Technology stack upgrade           │
  │  3. adpreclone.pl (apps + DB)          │
  │  4. adcfgclone.pl for new FS           │
  │  5. adop prepare → apply → finalize   │
  │  6. Post-upgrade steps                 │
  └────────────────────────────────────────┘
```

### Data Migration Strategies

| Pattern | Method | Volume | Downtime |
|---------|--------|--------|----------|
| Bulk Data | SQL\*Loader Direct Path | 10M+ rows/day | None |
| Incremental | FNDLOAD / Custom scripts | 1K-10K rows | None |
| Real-time | SOA/ESB + APIs | Per transaction | None |
| Periodic | EBS Open Interfaces | 100K-1M rows/batch | Batch window |
| Cutover | Export/Import + adcfgmerge | Full migration | Planned |

---

## Cloud vs On-Premise

### Architecture Decision Matrix

| Factor | On-Premise | OCI | AWS | Azure |
|--------|-----------|-----|-----|-------|
| Licensing | Bundle (UE) | BYOL | BYOL | BYOL |
| HA Setup | Custom | EBS on OCI Reference Arch | Quick Start | ARM Template |
| Storage | SAN/NAS | Block Volumes | EBS (gp3) | Managed Disks |
| Network | VLAN | VCN + DRG | VPC + DX | VNet + ER |
| RAC Support | Full | RAC on Exadata | RAC on EC2 | RAC on VMSS |
| ADOP | Manual | Automation via OCI | Custom | Custom |
| Cost Model | Capex | Opex (CPU/hr) | Opex (Instance/hr) | Opex (DB/hr) |

### EBS on OCI Reference Architecture

```
┌──────────────────────────────────────────────┐
│ OCI Region                                   │
│                                               │
│  ┌──────────┐  ┌──────────┐  ┌───────────┐  │
│  │ AD 1     │  │ AD 2     │  │ AD 3      │  │
│  │ App VM   │  │ App VM   │  │ Filer     │  │
│  │ DB VM    │─▶│ DB VM    │  │ (Backup)  │  │
│  └──────────┘  └──────────┘  └───────────┘  │
│       │              │                       │
│       └──────────────┘                       │
│              │                               │
│       ┌──────▼──────┐                       │
│       │ Load        │                       │
│       │ Balancer    │                       │
│       └──────┬──────┘                       │
│              │                               │
│  ┌───────────▼───────────┐                  │
│  │ FastConnect / VPN     │                  │
│  └───────────────────────┘                  │
└──────────────────────────────────────────────┘
```

---

## Customization Architecture

### CEMLI Architecture

```
Customization → EBS EXTENSION (custom_*.sql, *.ldt)
                ├── Controllers (OAF custom controller)
                ├── Extensions (Forms personalization)
                ├── Modifications (Workflow customizations)
                ├── Localizations (NLS additions)
                └── Integrations (API wrappers)
```

### OAF Extension Impact

```
Base View Object (InvoiceVO)          Custom View Object (CustInvoiceVO)
┌───────────────────────────────┐    ┌──────────────────────────────┐
│ InvoiceId                     │    │ InvoiceId (inherited)        │
│ InvoiceNumber                 │    │ InvoiceNumber (inherited)    │
│ InvoiceAmount                 │    │ InvoiceAmount (inherited)    │
│ InvoiceDate                   │    │ InvoiceDate (inherited)      │
│                               │    │ ApprovalStatus (custom)     │
│                               │    │ CustomField (extension)     │
└───────────────────────────────┘    └──────────────────────────────┘
        OAF Extension:                        │
        Subtype InvoiceVO          ┌──────────┘
        Add attributes             │
        Override methods    ┌──────▼──────┐
                            │ Override    │
                            │ getEntity() │
                            │ & process() │
                            └─────────────┘
```

### Impact on System Design

| Customization | Design Impact | Upgrade Risk |
|--------------|---------------|-------------|
| Form Personalization | None — stored in FND | Low (reapplied) |
| OAF Controller Extension | Copy of controller in custom dir | Medium (API changes) |
| Workflow Customization | Modified WF XML | Medium (def change) |
| Custom Table | New table in custom schema | None |
| Custom Concurrent | New executable | Low |
| BC4J Extension | Java subclassing | High (BC4J API changes) |

---

## Interview Templates

### Template: "Design an EBS System for a Global Enterprise"

```
1. REQUIREMENTS GATHERING
   - Number of concurrent users: ___ (e.g., 5,000)
   - Number of transactions/day: ___ (e.g., 500K invoices)
   - Number of legal entities: ___ (e.g., 50 countries)
   - Compliance requirements: ___ (e.g., SOX, GDPR)
   - Availability target: ___ (e.g., 99.9% uptime)
   - Integration landscape: ___ (e.g., 3 legacy systems)

2. TIER DESIGN
   Desktop: ___ Forms nodes, ___ Web nodes, CDN?
   Apps: ___ CM nodes (___ special CMs), Load balancer type?
   DB: RAC? Data Guard? Backup strategy?

3. STORAGE
   - APPL_TOP: ___ GB (shared NAS vs local)
   - DB: ___ TB (expected growth % per year)
   - Archive: ___ TB (document retention policy)

4. SECURITY
   - Authentication: OID? SSO? OAM?
   - Roles: ___ responsibilities, ___ user groups
   - Segregation of duties: ___ conflicting responsibilities

5. HIGH AVAILABILITY
   - Active/Active vs Active/Passive
   - RPO: ___, RTO: ___
   - DR site location / region

6. CUSTOMIZATION STRATEGY
   - CEMLI standardization (custom_* naming)
   - Version control
   - Development → Test → UAT → Prod pipeline
```

### Template: "Design a Batch Processing Architecture"

```
1. LOAD PROFILE
   - Peak batch window: ___ (e.g., 2 AM - 6 AM)
   - Total transactions: ___ (e.g., 2M invoices)
   - Parallel processing OK? Yes/No

2. CONCURRENT MANAGER DESIGN
   ┌─────────────────────┬──────┬───────────┐
   │ Manager Name        │  Cnt │ Priority  │
   ├─────────────────────┼──────┼───────────┤
   │ Standard Manager    │   20 │ 50        │
   │ Invoice Import      │   5  │ 70        │
   │ Payment Processing  │   3  │ 90        │
   │ GL Posting          │   2  │ 90        │
   │ Reports             │   3  │ 30        │
   │ Purge               │   1  │ 10        │
   └─────────────────────┴──────┴───────────┘

3. DATA CHUNKING
   - Commit every ___ rows (e.g., 5000)
   - Savepoint strategy for rollback
   - Error log table: CUSTOM_INTERFACE_ERRORS

4. MONITORING
   - fnd_concurrent_requests (phase, status)
   - Custom dashboard (OAF/Apex)
   - Email notification on failure
```

### STAR Answers for System Design

- **Situation:** "Our client had a 500K invoice/month AP process failing during month-end close."
- **Task:** "Redesign the AP invoice import to complete within the 2-hour batch window."
- **Action:** "Analyzed concurrent manager config → increased Invoice Import manager from 2→5 processes, partitioned large GL_INTERFACE by org_id, added direct path SQL\*Loader, implemented staged commit every 10K records."
- **Result:** "Batch window reduced from 6 hours to 45 minutes, month-end close completed on time for 3 consecutive periods."
