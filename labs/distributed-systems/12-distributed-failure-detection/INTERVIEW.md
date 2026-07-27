# Distributed Failure Detection - Interview Preparation

> Key interview questions about detecting failures in distributed systems.

---

## Core Interview Questions

### Q1: Compare heartbeat-based vs gossip-based failure detection
**Answer**: Heartbeat: centralized, periodic messages to monitoring service. Simple but O(N) communication, single point of failure. Gossip: nodes exchange state with random peers. O(log N) convergence, decentralized, no SPOF. Phi Accrual (Cassandra) uses adaptive thresholds.

### Q2: Explain the Phi Accrual Failure Detector
**Answer**: Measures suspicion level (-log10 of probability heartbeat arrives after elapsed time). Uses historical heartbeat intervals to compute normal distribution. Adapts to network variance. If phi > threshold (default 8), node suspected. More accurate than fixed timeout.

### Q3: What is the "wrong suspicion" problem?
**Answer**: Temporarily slow (GC pause, network congestion) nodes incorrectly marked failed. Mitigations: phi threshold tuning, suspicion level (not binary), acknowledgment round (SWIM protocol), indirect probing through peers.

### Q4: How does SWIM protocol work for failure detection?
**Answer**: SWIM = Scalable Weakly-consistent Infection-style Membership. Each round: ping random member. If no ack within timeout, ping K random members to indirectly ping target. On confirmed failure, disseminate via gossip. Eventually consistent membership.

### Q5: How does Kubernetes detect node failures?
**Answer**: Node controller monitors heartbeat timestamp in etcd. If node misses heartbeats for --node-monitor-period * --node-monitor-grace-period (~40s default), pod eviction begins. Taint-based eviction: node unreachable taint triggers pod eviction after --pod-eviction-timeout.

## Company-Specific Focus

| Company | Failure Detection Focus |
|---------|-----------------------|
| Amazon | "Gossip-based in DynamoDB using Phi Accrual" |
| Google | "How does Borg detect node failures?" |
| Netflix | "Chaos Monkey for failure testing" |
| Hashicorp | "SWIM gossip protocol in Consul/memberlist" |

## LeetCode Connections

| Problem | # | Failure Detection Concept |
|---------|---|-------------------------|
| Longest Consecutive Sequence | 128 | Heartbeat timeout detection |
| Number of Provinces | 547 | Cluster membership |
| Graph Valid Tree | 261 | Acyclic cluster health |
| Redundant Connection | 684 | Split brain detection |

## System Design Connections

- **Design a Cluster Membership Service**: Gossip-based failure detection
- **Design a Load Balancer**: Health check-based failure detection
- **Design a Monitoring System**: Heartbeat + Phi Accrual
- **Design a Service Mesh**: Adaptive failure detection

> **Key Insight**: Modern failure detection uses adaptive algorithms (Phi Accrual) not fixed timeouts. Discuss suspicion levels, not just binary alive/dead status.