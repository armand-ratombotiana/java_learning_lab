# Lab 02 — Thread Deadlock: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 5 minutes (for deadlock hang recovery) |
| RPO | 0 (no data loss — stateless lock coordinator) |
| MTD | 15 minutes |

## Disaster Scenarios

### Scenario A: Thread Deadlock (Single Node)

**Trigger:** ThreadMXBean detects deadlock
**Impact:** One node hung, 30-second watchdog reset
**Recovery:**
1. Watchdog detects unresponsive threads (30s timeout)
2. Watchdog interrupts blocked threads
3. Application resets lock state for interrupted operations
4. Node returns to serving traffic

**Verification:** Node processing requests, no BLOCKED threads.

### Scenario B: Cluster-wide Deadlock Pattern

**Trigger:** All nodes experiencing deadlocks due to consistent lock ordering bug
**Impact:** All nodes hung, cascading failures
**Recovery:**
1. Disable traffic to affected cluster
2. Restart all nodes with temporary fix (elevated watchdog timeout)
3. Deploy permanent fix (tryLock with consistent ordering)
4. Re-enable traffic gradually

### Scenario C: Distributed Deadlock

**Trigger:** Service A and Service B in mutual deadlock
**Impact:** Both services unresponsive
**Recovery:**
1. Implement circuit breaker on cross-service calls
2. Force close connections from Service A to Service B
3. Reset lock state in both services
4. Re-establish connections with retry+timeout

## Backup and Restore

| Item | Backup Method | Restore Time |
|------|--------------|-------------|
| Configuration | Git repository | 1 minute |
| Lease state | etcd/ZooKeeper snapshots | 2 minutes |
| Lock state | Reconstructed on restart (stateless) | 0 minutes |

## Failover

- Multi-region deployment with active-active lock coordinators
- DNS failover if > 50% of nodes in a region are deadlocked
- Automated rollback of recent deployment if deadlock correlates with code change

## Testing Schedule

| Test | Frequency | Criteria |
|------|-----------|----------|
| Deadlock detection test | Daily | Alert fires within 30s of deadlock |
| Watchdog recovery | Weekly | Node recovers within 35s |
| Lock ordering audit | Monthly | No inconsistent ordering found |
| Chaos test (inject deadlock) | Quarterly | System detects and recovers automatically |
| Failover drill | Monthly | Cross-region failover < 5 minutes |
