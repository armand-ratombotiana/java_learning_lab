# Distributed Locking - Interview Preparation

> Key interview questions about distributed locks and mutual exclusion.

---

## Core Interview Questions

### Q1: How does ZooKeeper implement distributed locks?
**Answer**: Ephemeral sequential zNodes under /lock/resource. Clients create zNode, watch preceding zNode. Lowest sequence number holds lock. On release: zNode auto-deletes, next client's watch triggers. Fencing tokens: monotonic lock ID prevents stale lock holders.

### Q2: What is the fencing token problem?
**Answer**: A client's lock expires (GC pause) but client still writes. Fencing tokens: monotonically increasing token assigned on each lock acquisition. Resource only accepts writes with valid token >= last seen token. Prevents split-brain writes.

### Q3: Compare Redis Redlock vs ZooKeeper locks
**Answer**: Redlock: acquire lock on majority of Redis nodes; if majority acquired, lock held. Simpler, faster, but less safe (no fencing tokens, clock skew). ZooKeeper: consensus-based, stronger guarantees, fencing tokens, but more complex and slower.

### Q4: What is a "lease-based" lock?
**Answer**: Lock with time-to-live. Holder must periodically renew. If holder crashes without releasing, lock auto-releases after lease expiry. Prevents deadlocks from crashed holders. ZooKeeper ephemeral nodes = lease-based.

### Q5: How do you handle deadlocks in distributed locks?
**Answer**: Lock ordering (always acquire locks in consistent order), timeouts with retry, hold locks for minimum time, deadlock detection via wait-for graphs, resource preemption.

## Company-Specific Focus

| Company | Locking Focus |
|---------|--------------|
| Google | "Chubby lock service - how does it work?" |
| Amazon | "DynamoDB conditional write for optimistic locking" |
| Apache | "ZooKeeper + Curator recipes for distributed coordination" |
| Redis | "Redlock - controversy and proper usage" |

## LeetCode Connections

| Problem | # | Locking Concept |
|---------|---|----------------|
| Print FooBar Alternately | 1115 | Mutual exclusion |
| H2O Generation | 1117 | Barrier synchronization |
| The Dining Philosophers | 1226 | Deadlock prevention |
| Design Bounded Blocking Queue | 1188 | Condition synchronization |

## System Design Connections

- **Design a Distributed Task Scheduler**: Lock tasks to prevent double execution
- **Design a Leader Election System**: Lock for electing single leader
- **Design a Resource Manager**: Distributed locks for resource access
- **Design a Configuration Service**: Locks for write access control

> **Key Insight**: Always discuss the fencing token pattern in locking interviews. Without it, lock-based systems are vulnerable to stale lock holders.