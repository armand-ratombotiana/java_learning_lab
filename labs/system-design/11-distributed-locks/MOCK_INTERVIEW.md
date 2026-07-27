# Mock Interview: Distributed Locks

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Platform Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a distributed lock service for a distributed system.

---

## Transcript

**Interviewer**: "Our microservices need a distributed lock service for coordinating access to shared resources. Design a lock service with: mutual exclusion, fault tolerance, deadlock prevention, and timeouts."

**Candidate**: "I'll design a lock service based on ZooKeeper/ETCD concepts. The service stores ephemeral nodes representing locks. Clients compete for locks by creating nodes. The first to create succeeds; others watch and wait."

**Interviewer**: "Describe the lock acquisition protocol."

**Candidate**: "For a lock named 'resource-1', the client attempts to create an ephemeral node at `/locks/resource-1/lock-<uuid>`. ZooKeeper-like, there are two patterns: 1) Simple lock: try to create a well-known node `/locks/resource-1/mutex`. If it exists, lock is held. If we create it, we have the lock. 2) Fair lock: create a sequential node `/locks/resource-1/lock-0000000001`. The client with the lowest sequence number holds the lock. Watchers notify when the lock is released."

**Interviewer**: "How do you handle lock holder failure?"

**Candidate**: "Ephemeral nodes (ZooKeeper) or leases (ETCD). The lock node is ephemeral — tied to the client's session. If the client crashes or loses connectivity beyond the session timeout, the node is automatically deleted. This prevents orphaned locks. In ETCD, we use leases: client must periodically refresh the lease. If not refreshed within TTL, the lock expires."

**Interviewer**: "What about the thundering herd problem?"

**Candidate**: "When a lock is released, many waiting clients could wake up and compete. Solution: sequential fair lock pattern. Each client creates a sequential node and watches ONLY the node with the sequence number one less than its own. When the lock holder releases (node deleted), only the next client in sequence is notified. This is O(1) wake-ups instead of O(n)."

**Interviewer**: "How do you handle clock skew in lock timeouts?"

**Candidate**: "Clock skew is a real problem for lock expiry. The lock client should add a safety margin: if the lock lease is 10 seconds, assume it expires at 8 seconds. The lock service itself uses monotonic clocks and synchronized time across nodes. For high-criticality locks, use the Redlock algorithm from Redis — acquire from multiple independent nodes, only consider the lock acquired if a majority succeed."

**Interviewer**: "How do you make the lock service itself highly available?"

**Candidate**: "The lock service runs as a cluster of 3 or 5 nodes using RAFT consensus. If the leader fails, a new leader is elected. Clients connect to any node in the cluster. Read operations (list locks) can go to any node. Write operations (create/delete lock) must go to the leader. The ephemeral node timeout is configurable — shorter for latency-sensitive systems, longer for reliability-sensitive."

---

## Key Takeaways

- **Ephemeral nodes / leases**: Automatic lock release on failure
- **Sequential fair lock**: Avoid thundering herd through ordered waiting
- **Safety margin**: Add buffer to lease time for clock skew
- **Redlock**: Multi-node lock acquisition for critical resources
- **RAFT-backed**: Lock service cluster for high availability
