# Mock Interview: CockroachDB (Lab 18)

**Role:** Database Architect (Senior)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is CockroachDB and what makes it unique?

**Candidate:** CockroachDB is a distributed SQL database designed for global scale and survivability. Unique features:
- **PostgreSQL-compatible SQL:** Familiar SQL interface with distributed execution
- **Auto-sharding:** Automatically splits and rebalances data across nodes
- **Strong consistency:** Serializable isolation by default (unlike most NoSQL)
- **Global deployment:** Single logical database across multiple regions
- **Survivability:** Self-healing, survives node/zone/region failures
- **Multi-active availability:** All nodes can read and write (no passive replicas)

**Interviewer:** Compare CockroachDB with Oracle RAC.

**Candidate:**

| Feature | CockroachDB | Oracle RAC |
|---------|-------------|-----------|
| Architecture | Shared-nothing (auto-sharding) | Shared-disk |
| Consistency | Serializable (by default) | Snapshot isolation (default) |
| Geographic | Multi-region, multi-cloud | Single-region (Data Guard for DR) |
| Scaling | Add nodes → auto-rebalance | Add nodes + rebalance (manual) |
| Failover | Automatic (< 10 seconds) | RAC: fast, Data Guard: minutes |
| SQL compatibility | PostgreSQL-compatible | Native Oracle SQL |
| Licensing | Open source (BSL) | Commercial |

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does CockroachDB achieve strong consistency across global regions?

**Candidate:** CockroachDB uses **Raft consensus protocol** for strong consistency:
1. Data is divided into ranges (default 64MB each, similar to HBase regions)
2. Each range has 3-5 replicas (configurable)
3. Raft elects a leader for each range (leaseholder)
4. All writes go through the leaseholder, which replicates to followers
5. Reads can be served by leaseholder (strong) or any replica (follower reads with staleness)

**Multi-region:**
- Table data is partitioned by region (home region configured per table)
- Raft replicas spread across regions for survivability
- `GLOBAL` tables (lookup/reference) have fast reads using Follower Reads
- `REGIONAL` tables optimize for data locality (data stored near the user)

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a globally distributed application with CockroachDB that serves users from North America, Europe, and Asia. Requirements: sub-100ms write latency, strong consistency for financial data, high availability across regions.

**Candidate:** 

**Architecture:**
```
┌──────────────────────┐  ┌──────────────────────┐  ┌──────────────────────┐
│  US-East Region       │  │  EU-West Region      │  │  SE-Asia Region      │
│  3 nodes              │  │  3 nodes              │  │  3 nodes             │
│  us-east-1a, b, c     │  │  eu-west-1a, b, c     │  │  ap-southeast-1a,b,c │
│  ┌────────────────┐   │  │  ┌────────────────┐   │  │  ┌────────────────┐   │
│  │ SURVIVE REGION │   │  │  │ SURVIVE ZONE   │   │  │  │ SURVIVE ZONE   │   │
│  │ FAILURE        │   │  │  │ FAILURE        │   │  │  │ FAILURE        │   │
│  └────────────────┘   │  │  └────────────────┘   │  │  └────────────────┘   │
└──────┬───────────────┘  └──────┬───────────────┘  └──────┬───────────────┘
       └─────────────────────────┼─────────────────────────┘
                              ┌──┴──┐
                              │ DNS │
                              │ LB  │
                              └─────┘
```

**Schema design:**
```sql
-- Global application configuration (stored everywhere)
CREATE TABLE app_config (
    key STRING PRIMARY KEY,
    value STRING
) LOCALITY GLOBAL;

-- User data pinned to home region
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name STRING,
    email STRING UNIQUE,
    home_region STRING AS (substr(crdb_region, 1, 2)) STORED
) LOCALITY REGIONAL BY ROW;

-- Financial transactions (strong consistency via global tables)
CREATE TABLE transactions (
    txn_id UUID DEFAULT gen_random_uuid(),
    from_account UUID,
    to_account UUID,
    amount DECIMAL,
    created_at TIMESTAMP DEFAULT now(),
    PRIMARY KEY (crdb_region, txn_id)
) LOCALITY REGIONAL BY ROW;
```

**Survivability goals:**
- Zone failure: Survived (3 nodes across 3 AZs per region)
- Region failure: Survived (Raft quorum maintained across remaining regions)
- Global outage: Rare (multi-region spread)

---

## Interviewer Feedback

**Strengths:** Strong CockroachDB understanding, practical global architecture design, good survivability analysis  
**Areas to Improve:** Could discuss CockroachDB's change data capture (CDC) for streaming  
**Verdict:** Hire

---

*Databases Lab 18 MOCK_INTERVIEW.md*
