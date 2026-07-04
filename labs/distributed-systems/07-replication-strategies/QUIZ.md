# Replication: Quiz

## Questions
1. What is the difference between sync and async replication?
2. What is a quorum?
3. How does multi-leader replication handle conflicts?
4. What is read repair?
5. What is hinted handoff?
6. What determines replication factor?
7. What is the purpose of a binlog?
8. How does gossip protocol work for replication?
9. What is anti-entropy?
10. When would you choose multi-leader over single-leader?

## Answers
1. Sync: waits for ack. Async: doesn't wait
2. Minimum nodes that must participate for a valid operation
3. CRDTs, LWW, custom merge, or application-level resolution
4. Fixing stale replicas during read operations
5. Temporary storage of writes for downed replicas
6. Desired durability and fault tolerance level
7. Record all changes for replication to followers
8. Nodes randomly exchange state with peers, convergence over time
9. Periodic full comparison and repair of replica data
10. When writing from multiple datacenters or offline operation
