# Interview Questions: Database Security

## Basic Level

### Q1: What is database security and why is it used?
**A:** It's a strategy that distributes data across multiple independent database instances to overcome storage, throughput, and geographic limitations of single-node databases.

### Q2: What is a routing key?
**A:** A column or set of columns that determines how data is distributed. Every row is assigned to a node based on its routing key value.

### Q3: Explain the difference between horizontal and vertical partitioning.
**A:** Horizontal (sharding) splits rows across tables/databases. Vertical splits columns within a table.

### Q4: What makes a good routing key?
**A:** High cardinality, even distribution, stability, and alignment with query patterns.

### Q5: Range vs hash distribution?
**A:** Range divides by value ranges (good for ordered queries, hotspot risk). Hash distributes evenly (breaks range locality).

## Intermediate Level

### Q6: How does consistent hashing work?
**A:** Keys and nodes are placed on a hash ring. Keys go to the nearest node clockwise. Virtual nodes improve distribution. Node changes move only K/N keys.

### Q7: What is scatter-gather?
**A:** Query broadcast to all nodes in parallel, results collected and merged. Used when routing key isn't in the query.

### Q8: How do you handle transactions across nodes?
**A:** Options: 2PC (strong consistency, blocking), Saga (non-blocking with compensation), or avoid (design to minimize cross-node ops).

### Q9: What is a hotspot and how do you mitigate it?
**A:** A node receiving disproportionate traffic. Mitigations: better key selection, composite keys, virtual nodes, dynamic rebalancing, caching.

### Q10: How do you rebalance data?
**A:** Monitor skew, plan movement, execute migration in batches, update routing table, clean up.

## Advanced Level

### Q11: Design a distributed database for a global social media platform.
**A:** Geographic distribution, composite routing key (region + user_id hash), read replicas per region, eventual consistency across regions, caching layer.

### Q12: How do you ensure consistency during rebalancing?
**A:** Mark routing as \"migrating\", dual-write, backfill, verify checksums, atomic routing update, cleanup.

### Q13: What causes split-brain and how to prevent it?
**A:** Multiple nodes think they're primary. Prevention: quorum writes, leases, fencing (STONITH), network redundancy.

### Q14: Client-side vs proxy routing?
**A:** Client-side: simpler, lower latency, needs updates. Proxy: transparent, centralized, adds network hop.

### Q15: Describe a real distributed database failure.
**A:** Instagram's monotonically increasing keys, Twitter's \"fail whale\", celebrity hotspot issues. Key lessons: always hash, monitor, automate.

## System Design Questions

### Q16: Design for an e-commerce platform.
**Key considerations:** Routing key (customer_id), product catalog (separate, replicated), consistency (strong for orders, eventual for recommendations).

### Q17: How to migrate from single-node to distributed?
**Plan:** Choose key, set up routing, dual-writes, backfill, verify, gradual cutover, decommission old.

### Q18: Design a monitoring system.
**Metrics:** Per-node QPS, latency, error rate, data size, connection pool, skew factor.
**Alerting:** Skew > 0.3, P99 > 50ms, error rate > 1%, capacity > 70%.

### Q19: Compare approaches.
| System | Approach | Strengths |
|--------|----------|-----------|
| MongoDB | Range/hash | Flexible, built-in |
| Cassandra | Consistent hashing | Excellent distribution |
| CockroachDB | Auto range | No manual management |
| Vitess | Range + hash | SQL compatible |

### Q20: Emerging trends?
AI-driven key recommendation, serverless transparent distribution, edge computing, CRDTs, multi-cloud fabrics, compute-storage separation.
