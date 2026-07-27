# Networking Behavioral Interview Guide

> 200+ lines covering outage response, latency debugging, capacity planning, DDoS mitigation, and protocol design decisions.

---

## 1. STAR Method for Networking Scenarios

| Component | What to Include |
|-----------|-----------------|
| **S**ituation | What was the network environment? (data centers, cloud, hybrid, traffic volume) |
| **T**ask | What specific networking challenge needed solving? |
| **A**ction | What did you do? (tools, commands, changes, coordination) |
| **R**esult | Measurable outcome (latency reduced X%, downtime reduced, cost saved Y%) |

---

## 2. Outage Response

### Common Question
"Tell me about a time you handled a major network outage."

### Story Structure

| Phase | Duration | Key Actions |
|-------|----------|-------------|
| Detection | 0-5 min | Alert triggered (PagerDuty, Grafana, CloudWatch) |
| Triage | 5-15 min | Determine scope: all traffic, specific service, region |
| Mitigation | 15-60 min | Rollback, failover, reroute, scale out |
| Resolution | 1-4 hours | Permanent fix applied, confirmed via monitoring |
| Postmortem | 1-7 days | Root cause analysis, action items, timeline |

### Sample Answer Framework

**Situation**: We had a 15-minute outage on our customer-facing API due to a BGP route leak.

**Task**: Restore service and prevent recurrence.

**Actions**:
1. Pager alerted at 14:32 — latency > 5s, error rate 40%
2. Checked BGP status — 3 unexpected prefixes from upstream
3. Applied route-map filter on edge routers to reject leaked prefixes
4. Traffic normalized within 4 minutes
5. Root cause: upstream ISP misconfiguration
6. Implemented prefix-list filtering and RPKI validation

**Result**: MTTR 7 minutes, zero recurrence in 18 months, implemented BGP monitoring.

### Key Behavioral Points

- **Ownership**: Don't blame external factors — own the response
- **Communication**: Update stakeholders proactively (status page, Slack, email)
- **Systematic approach**: Show methodical troubleshooting (not panic)
- **Learn from incidents**: Always include postmortem improvements

---

## 3. Latency Debugging

### Common Question
"Walk me through how you debug a high-latency issue."

### Diagnostic Flow

```
High latency reported
├── 1. Is it network or application?
│   ├── Check app metrics (request duration, CPU)
│   └── Check network metrics (packet loss, RTT, jitter)
├── 2. Where is it slow?
│   ├── Client → CDN: run traceroute/mtr
│   ├── CDN → Origin: check CloudFront metrics
│   └── Origin → DB: check query times
├── 3. Drill down
│   ├── tcpdump / Wireshark: TCP retransmissions, window scaling
│   ├── iperf: bandwidth test
│   └── netstat: connection state, buffer queues
└── 4. Fix
    ├── TCP tuning (tcp_rmem, tcp_wmem)
    ├── Enable BBR congestion control
    ├── Move to CDN with edge caching
    └── Redesign app to reduce round trips
```

### Sample Answer Framework

**Situation**: Users in APAC reported 3-second load times. US users were under 500ms.

**Task**: Reduce APAC latency to under 1 second.

**Actions**:
1. Ran traceroute from APAC users — found packet took 300ms trans-Pacific
2. Analyzed CDN logs — 90% cache miss for APAC edge
3. Deployed APAC origin shield to prewarm cache
4. Implemented stale-while-revalidate for API endpoints
5. Turned on TCP BBR congestion control on origin servers

**Result**: APAC load times dropped to 800ms. Cache hit ratio improved from 10% to 85%.

---

## 4. Capacity Planning

### Common Question
"How do you approach network capacity planning?"

### Framework

```
1. Data Collection (current state)
   └── Bandwidth utilization (95th percentile, peak)
   └── Connection count (concurrent, new/s)
   └── Packet rate (pps)
   └── Flow count (NetFlow/sFlow)

2. Growth Analysis
   └── Historical trends (6-12 months)
   └── Business growth rate (users, traffic)
   └── Known events (launches, campaigns)

3. Threshold Definition
   └── Warning: 70% capacity → review
   └── Critical: 85% capacity → order
   └── Emergency: 95% capacity → expedite

4. Action Plan
   └── Upgrade link capacity
   └── Add additional peering
   └── Optimize routing (less backup traffic)
   └── Compress data, reduce payloads
```

### Sample Answer

**Situation**: Our inter-datacenter link was hitting 80% utilization with 20% monthly growth.

**Task**: Ensure sufficient capacity for next 12 months without over-provisioning.

**Actions**:
1. Analyzed NetFlow data to identify top talkers
2. Found 40% of traffic was replication between DB clusters
3. Implemented compression for replication traffic (reduced 60%)
4. Added a second DCI link for active-active load sharing
5. Set up Prometheus alerts at 70% utilization

**Result**: Bandwidth headroom extended by 18 months. Saved $50k/month by delaying link upgrade.

---

## 5. DDoS Mitigation

### Common Question
"Describe a DDoS attack you mitigated."

### Attack Types and Responses

| Attack Type | L3/L4/L7 | Mitigation |
|------------|----------|------------|
| SYN Flood | L4 | SYN cookies, rate limiting, scrubbing center |
| UDP Amplification | L4 | Block source ports, rate limit per IP |
| HTTP Flood | L7 | WAF, rate limiting, CAPTCHA |
| DNS Amplification | L4 | Disable recursion, rate limit per source |
| Slowloris | L7 | Connection timeout, request rate limits |

### Sample Answer

**Situation**: Our e-commerce site was hit with a 200 Gbps HTTP flood during Black Friday.

**Task**: Mitigate without blocking legitimate customers.

**Actions**:
1. Immediately enabled scrubbing via Cloudflare's DDoS protection
2. Implemented rate limiting: 100 requests/sec per IP on /checkout
3. Deployed WAF rules to block known bot patterns
4. Scaled up origin servers (auto-scaling group from 20 to 80 instances)
5. Added challenge page for suspicious requests

**Result**: Site remained up, 99.7% of legitimate traffic got through, estimated $500k revenue saved.

---

## 6. Protocol Design Decisions

### Common Question
"Tell me about a time you had to choose between protocols for a system."

### Decision Framework

```
Requirements:
├── Throughput needed
├── Latency sensitivity
├── Reliability requirements
├── Connection patterns (request-reply vs streaming)
├── Client support (browser, mobile, IoT)
└── Operational complexity

Trade-off Analysis:
├── REST (simple, cacheable, stateless) vs gRPC (performant, typed, streaming)
├── WebSocket (bidirectional, stateful) vs SSE (server→client only, simpler)
├── MQTT (lightweight, IoT) vs AMQP (feature-rich, enterprise)
└── TCP (reliable, ordered) vs UDP (fast, loss-tolerant)
```

### Sample Answer

**Situation**: We needed real-time market data for a trading platform.

**Task**: Choose between WebSocket and Server-Sent Events for price streaming.

**Actions**:
1. Requirements: low latency, bidirectional (subscribing to symbols), browser clients
2. Evaluated SSE: simpler, native EventSource API, but no client→server messages
3. Evaluated WebSocket: full duplex, lower header overhead after upgrade
4. Chose WebSocket with reconnection and backpressure handling
5. Implemented heartbeat for connection health

**Result**: ~5ms end-to-end latency, 99.99% uptime for streaming connections.

---

## 7. Behavioral Question Bank

| Question Type | Example | Key Points to Cover |
|---------------|---------|---------------------|
| Conflict | "Disagreement on network design approach" | Data-driven decision, compromise, respect for expertise |
| Failure | "A change caused an outage" | Ownership, learning, improved process |
| Leadership | "Leading a network migration" | Planning, communication, delegation |
| Innovation | "Improving network monitoring" | Proactive vs reactive, automation |
| Customer | "Helping a team debug their network issue" | Empathy, knowledge transfer |
| Ambiguity | "Undefined network requirements" | Asking questions, iterative approach |

---

## Quick Reference: Networking Behavioral Keywords

| Concept | Keywords to Use |
|---------|-----------------|
| Troubleshooting | tcpdump, Wireshark, traceroute, netstat, iperf, mtr |
| Reliability | SLA, redundancy, failover, health checks, graceful degradation |
| Security | WAF, DDoS, encryption, mTLS, zero-trust, network segmentation |
| Performance | Latency, throughput, p99, jitter, packet loss, congestion |
| Automation | IaC, Terraform, Ansible, CI/CD, GitOps, config management |
| Monitoring | Prometheus, Grafana, NetFlow, sFlow, ELK, Datadog |
| Scale | BGP, ECMP, anycast, CDN, horizontal scaling, connection pooling |

---

*"Every outage is an opportunity to improve. Every design is a set of tradeoffs."*
