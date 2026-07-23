# Mock Interview: Database Sharding (Lab 13)

**Role:** Database Architect (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is database sharding and when would you use it?

**Candidate:** Sharding horizontally partitions data across multiple database instances. Use when:
1. **Single database can't handle write throughput** (>10K writes/sec)
2. **Data volume exceeds capacity** (>10TB or >1B rows)
3. **Geographic distribution needed** (data closer to users)
4. **Multi-tenant isolation** required at database level

Each shard is an independent database containing a subset of data. A routing layer directs queries to the correct shard based on the shard key.

**Interviewer:** How is sharding different from partitioning and replication?

**Candidate:**
- **Sharding:** Data distributed across independent databases. Each has subset of rows. Different servers.
- **Partitioning:** Data divided within a single database. Each partition in separate tablespace/data file.
- **Replication:** Same data copied to multiple databases. All have all rows.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you choose a shard key? What are the trade-offs?

**Candidate:** Shard key selection is critical. Criteria:
1. **High cardinality:** Many distinct values (user_id, order_id)
2. **Even distribution:** No hot shards (a key that distributes 20% each across 5 shards)
3. **Query pattern:** Keys used in most queries (to avoid scatter-gather)
4. **Stable:** Values that don't change

**Good examples:** `customer_id % 32`, `hash(order_id)`, geographic region

**Bad examples:** `created_date` (all today's data on one shard), `status` (low cardinality), `country_code` (uneven distribution)

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a sharded database architecture for a global SaaS platform with 100M users. Users should see their data quickly regardless of geographic location.

**Candidate:** 

**Architecture:**
```
Regional Shards (Active-Active):
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Shard US-East │  │ Shard EU-West│  │ Shard AP-East│
│ user_id: 0-33M│  │ user_id: 33- │  │ user_id: 66- │
│              │  │ 66M          │  │ 100M         │
├──────────────┤  ├──────────────┤  ├──────────────┤
│ PostgreSQL   │  │ PostgreSQL   │  │ PostgreSQL   │
│ (RDS/ Aurora)│  │ (RDS/ Aurora)│  │ (RDS/ Aurora)│
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                 │                 │
       └─────────────────┼─────────────────┘
                         │
               Global Read Replicas
               (for cross-shard analytics)
```

**Shard key:** `user_id` hashed to 100 shards, mapped to 3 regional clusters.

**Data distribution:**
- **User data sharded** by `user_id` (hash-based)
- **Order data** co-located with user (same shard as user_id)
- **Reference data** replicated to all shards (product catalog, currencies)
- **Global analytics** aggregated to read replicas

**Routing:**
```java
public class ShardRouter {
    public int getShardId(String userId) {
        int hash = Math.abs(userId.hashCode()) % 100;
        if (hash < 33) return 0; // US-East
        if (hash < 66) return 1; // EU-West
        return 2; // AP-East
    }
}
```

**Cross-shard queries:** Use scatter-gather pattern: query all shards in parallel, merge results in application. For cross-shard joins, denormalize data or use a global data warehouse.

---

## Interviewer Feedback

**Strengths:** Excellent sharding knowledge, practical global design, strong shard key understanding  
**Areas to Improve:** Could discuss Vitess or Citus for managed sharding  
**Verdict:** Strong Hire

---

*Databases Lab 13 MOCK_INTERVIEW.md*
