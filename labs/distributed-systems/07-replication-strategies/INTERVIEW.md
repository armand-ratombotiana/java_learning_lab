# Replication: Interview Questions

## Q1: How does MySQL replication work?
**A**: Leader writes changes to binary log. Follower's IO thread reads binlog, writes to relay log. SQL thread applies relay log to local database. Can be sync or async.

## Q2: What happens during a failover in leader/follower?
**A**: Follower with most up-to-date data is promoted. Other followers point to new leader. Clients discover new leader via DNS, proxy, or service discovery.

## Q3: How does Cassandra handle replication?
**A**: Configurable replication factor. Consistent hashing places data on nodes. Hinted handoff for temporary failures. Read repair and anti-entropy for consistency.

## Q4: Explain conflict resolution in multi-leader.
**A**: Approaches: Last-Write-Wins (LWW with timestamps), CRDTs (convergent data types), custom merge (application-specific), version vectors (conflict detection).

## Q5: How do you measure and monitor replication lag?
**A**: Write timestamps to both leader and follower, measure difference. Read follower's lag metrics. Watch for growing lag indicating problems.
