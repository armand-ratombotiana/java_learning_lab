# LEETCODE_SOLUTIONS — Database Replication

## High Availability Solutions

| LeetCode Problem | Replication Strategy | Read/Write Split |
|-----------------|---------------------|------------------|
| 175 Combine Tables | Primary for writes, replica for reads | SELECT → replica, INSERT → primary |
| 176 Second Highest | Read replica | Query replica for aggregation |
| 197 Rising Temperature | Read replica | Historical data → replica |
| 262 Trips and Users | Read replica for reporting | Reporting workload → replica |

### Replication Topology for LeetCode-Style Systems
```
[Primary] → [Replica 1] (read queries, 175, 176, 182)
         → [Replica 2] (reports, 184, 185, 262)
         → [Replica 3] (backup, analysis)

- Writes go to primary
- LeetCode SELECT queries go to replicas
- Use read-write splitting at the application level
```

### Replication Lag Considerations
- LeetCode 176 (Second Highest): If using async replication, recent salary changes may not appear on replica
- Solution: Route time-sensitive queries to primary, or use semi-sync replication
