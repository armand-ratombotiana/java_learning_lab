# Mock Interview: Cassandra & NoSQL (Lab 17)

**Role:** Database Engineer (Senior)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is Apache Cassandra and what are its key features?

**Candidate:** Cassandra is a distributed, wide-column NoSQL database designed for high availability and linear scalability. Key features:
- **Peer-to-peer architecture:** No single point of failure, all nodes are equal
- **Tunable consistency:** Choose between eventual and strong consistency per query
- **Linear scalability:** Add nodes, double throughput
- **Multi-data center:** Native support for multiple DCs
- **High write throughput:** Optimized for write-heavy workloads
- **CQL:** Cassandra Query Language (similar to SQL)

**Interviewer:** Compare Cassandra with Oracle Database.

**Candidate:**

| Feature | Cassandra | Oracle |
|---------|-----------|--------|
| Consistency | Tunable (eventual → strong) | Strong by default |
| Scalability | Linear (add nodes) | Vertical (scale up) or RAC |
| Query language | CQL (limited JOINs) | Full SQL |
| Indexing | Secondary indexes (limited) | Full indexing support |
| Transactions | Lightweight transactions | Full ACID |
| Best for | Time-series, IoT, messaging | OLTP, complex queries |

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain Cassandra's data model: Keyspace, Table, Partition Key, Clustering Columns.

**Candidate:** 
- **Keyspace:** Analogous to a database/schema. Contains tables.
- **Table:** Data organized by primary key. No joins — data is denormalized.
- **Partition Key:** First part of PRIMARY KEY. Determines which node stores the row. All rows with same partition key are stored together.
- **Clustering Columns:** Defines sort order within a partition.

```cql
CREATE TABLE user_activity (
    user_id UUID,           -- Partition key
    activity_date DATE,      -- Clustering column 1
    activity_time TIMESTAMP,  -- Clustering column 2
    activity_type TEXT,
    details TEXT,
    PRIMARY KEY ((user_id), activity_date, activity_time)
) WITH CLUSTERING ORDER BY (activity_date DESC, activity_time DESC);
```

Model data by **query patterns**, not by relationships. Each table serves one query pattern. Denormalization is expected.

**Interviewer:** How does Cassandra handle consistency?

**Candidate:** Tunable consistency per query:
- `ONE`: Fastest, eventual consistency (single replica responds)
- `QUORUM`: Majority of replicas (guarantees latest write)
- `ALL`: Strongest, all replicas must respond
- `LOCAL_QUORUM`: Quorum in local DC (for multi-DC)
- `EACH_QUORUM`: Quorum in each DC

**Hinted handoff:** If a replica is down, the coordinator stores the write and delivers when the replica is back. Guarantees eventual consistency.

**Read repair:** During reads, if replicas have different values, Cassandra returns the latest and updates stale replicas.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a time-series data store using Cassandra for IoT sensor data (10K sensors, 10M readings/hour).

**Candidate:** 

**Data model:**
```cql
CREATE TABLE sensor_readings (
    sensor_id UUID,
    reading_hour TIMESTAMP,  -- Truncated to hour for partition size control
    reading_time TIMESTAMP,  -- Actual reading time
    sensor_type TEXT,
    value DOUBLE,
    unit TEXT,
    metadata MAP<TEXT, TEXT>,
    PRIMARY KEY ((sensor_id, reading_hour), reading_time)
) WITH CLUSTERING ORDER BY (reading_time DESC)
   AND compaction = { 'class': 'TimeWindowCompactionStrategy', 
                      'compaction_window_size': 1, 
                      'compaction_window_unit': 'DAYS' };
```

**Design decisions:**
- Partition key: `(sensor_id, reading_hour)` — each partition has max 3600 readings
- Clustering: `reading_time DESC` — most recent first for common queries
- Compaction: TimeWindowCompactionStrategy for time-series data

**Query examples:**
```sql
-- Latest reading for a sensor
SELECT * FROM sensor_readings 
WHERE sensor_id = ? AND reading_hour = ? 
ORDER BY reading_time DESC LIMIT 1;

-- Range query
SELECT * FROM sensor_readings
WHERE sensor_id = ? AND reading_hour IN (?, ?, ?)
AND reading_time >= ? AND reading_time <= ?;
```

**Scaling:** Add nodes as sensor count grows. Each node handles a portion of partitions based on partition key hash.

---

## Interviewer Feedback

**Strengths:** Solid Cassandra knowledge, good consistency explanation, excellent time-series design  
**Areas to Improve:** Could discuss Cassandra Repair and Anti-Entropy  
**Verdict:** Hire

---

*Databases Lab 17 MOCK_INTERVIEW.md*
