# Common Mistakes with Replication

## 1. Asynchronous Replication Data Loss
Leader fails before replicating to followers → data loss. Solution: use synchronous replication for critical data.

## 2. Read Scaling Without Staleness Awareness
Reading from followers gives stale data. Solution: track replication lag, route consistent reads to leader.

## 3. Write Conflicts in Multi-Leader
Concurrent writes to same data in different leaders cause conflicts. Solution: CRDTs, last-write-wins, or custom merge.

## 4. Insufficient Replication Factor
RF=2 can lose data if one node fails. Minimum RF=3 for production.

## 5. Ignoring Replication Lag Monitoring
Lag can grow silently under load. Monitor replication lag with alerts.
