# SRE INTERVIEW CHEATSHEET

## SLI / SLO / SLA — Definitions and Formulas

### Service Level Indicator (SLI)
**Definition:** A quantifiable measure of service quality.

**Common SLIs:**
| Category | SLI | Formula |
|----------|-----|---------|
| Availability | Fraction of successful requests | `successful / total` |
| Latency | Fraction under threshold | `requests < 200ms / total` |
| Throughput | Requests per second | `total_reqs / time_window` |
| Durability | Fraction of data not lost | `data_retained / total_data` |
| Freshness | Age of data presented | `current_time - data_timestamp` |

### Service Level Objective (SLO)
**Definition:** A target value for an SLI over a measurement window (typically 30 days).

**Common SLOs:**
| SLO | Downtime Allowed (per year) | (per month) |
|-----|---------------------------|-------------|
| 99.999% | 5.26 minutes | ~26 seconds |
| 99.99% | 52.56 minutes | ~4.38 minutes |
| 99.95% | 4.38 hours | ~21.9 minutes |
| 99.9% | 8.76 hours | ~43.8 minutes |
| 99.5% | 43.8 hours | ~3.65 hours |
| 99% | 87.6 hours | ~7.3 hours |

### Error Budget
```
Error Budget = 100% - SLO
```
- **Example:** 99.9% SLO → 0.1% error budget = 43.8 min/month
- When error budget is consumed: freeze feature releases, focus on reliability
- Unused error budget can be "spent" on riskier releases

### Burn Rate Alerting
| Burn Rate | Meaning | Action |
|-----------|---------|--------|
| < 1x | Within budget | No action |
| 1-2x | Using faster than planned | Review |
| 2-5x | Accelerated | Alert on-call |
| 5-10x | Rapid | Page on-call |
| > 10x | Very rapid | Declare incident |

**Multi-Window Multi-Burn-Rate:**
| Window | Rate | SLO 99.9% | Alert |
|--------|------|-----------|-------|
| 1 hour | 14.4x | > 1.44% errors | Page |
| 6 hours | 6x | > 0.6% errors | Page |

## Capacity Planning Formulas

```
Concurrent connections needed = peak_req_per_sec × avg_latency_seconds

Peak throughput = total_daily_requests / (86400 × peak_to_avg_ratio)

Time to exhaustion = (current_capacity - used_capacity) / daily_growth_rate

Servers needed = projected_peak_throughput / throughput_per_server

Headroom = (provisioned_capacity - current_peak) / provisioned_capacity
```

### Key Capacity Metrics
| Metric | What It Tells You |
|--------|-------------------|
| CPU utilization | Compute saturation |
| Memory utilization | Working set size |
| Disk IOPS | Storage throughput demand |
| Network bandwidth | Data transfer demand |
| Connection pool usage | Application concurrency |
| Thread pool utilization | Request queuing pressure |
| GC pause time | JVM health, allocation rate |

## Distributed Systems Fallacies

### The 8 Fallacies of Distributed Computing

| Fallacy | Reality | Impact on SRE |
|---------|---------|---------------|
| 1. The network is reliable | Networks drop, reorder, duplicate packets | Design for retry, timeout, circuit break |
| 2. Latency is zero | Every network call costs 1-100ms | Minimize RPCs, batch requests, async |
| 3. Bandwidth is infinite | Bandwidth is a constrained resource | Compress data, paginate, chunk |
| 4. The network is secure | Networks are not inherently secure | Encrypt in transit, mutual TLS |
| 5. Topology doesn't change | Network topology changes constantly | Service discovery, health checks, DNS TTL |
| 6. There is one administrator | Multiple teams manage different parts | Infrastructure as Code, RBAC, audit |
| 7. Transport cost is zero | Serialization/deserialization is expensive | Protocol buffers, connection pooling |
| 8. The network is homogeneous | Different OS, protocols, versions exist | Abstraction layers, standardization |

### CAP Theorem
```
Consistency (C) — Every read gets the most recent write
Availability (A) — Every request gets a response (not necessarily latest)
Partition Tolerance (P) — System continues despite network partition

Pick 2: CP, AP (CA is not possible in distributed systems)
```

- **CP systems:** Traditional RDBMS (sacrifice A during partition)
- **AP systems:** DynamoDB, Cassandra (sacrifice C during partition)
- **Tradeoff:** No distributed system can be both consistent and available during a network partition

### PACELC Theorem
Extension of CAP: During normal operation, tradeoff between Latency and Consistency.

```
PACELC: If Partition (P) → tradeoff A vs C
        Else (E) → tradeoff Latency (L) vs Consistency (C)
```

## Monitoring Pillars

### The Four Golden Signals (Google SRE)

| Signal | What | Example | Alert Trigger |
|--------|------|---------|---------------|
| **Latency** | Time to serve a request | P99 < 200ms | P99 > 500ms for 5 min |
| **Traffic** | Demand on the system | 1000 req/s | > 2000 req/s for 10 min |
| **Errors** | Rate of failed requests | 0.1% error rate | > 1% for 5 min |
| **Saturation** | How "full" the service is | 70% CPU | > 85% for 10 min |

### RED Method (Microservices)

| Signal | What | Example |
|--------|------|---------|
| **Rate** | Requests per second | 1000 req/s |
| **Errors** | Failed requests per second | 1 req/s (0.1%) |
| **Duration** | Distribution of request latency | P50: 50ms, P99: 200ms |

Best for: Request-driven services (REST APIs, gRPC, message consumers)

### USE Method (Infrastructure)

| Signal | What | Example |
|--------|------|---------|
| **Utilization** | How busy a resource is | CPU 70%, Disk 60% |
| **Saturation** | How much extra work is queued | CPU run queue > 2, disk queue depth > 0 |
| **Errors** | Count of error events | Disk I/O errors, network errors |

Best for: Physical/virtual resources (CPU, memory, disk, network)

### L-USE Method (For Load Balancers)

| Signal | What | Example |
|--------|------|---------|
| **Latency** | Time to process request | 5ms avg latency |
| **Utilization** | Resource usage | 60% CPU, 70% connections |
| **Saturation** | Queue length | Connection queue depth |
| **Errors** | Failed requests | 0.01% 503 errors |

Best for: Load balancers, proxy servers (nginx, HAProxy, Envoy)

## Chaos Engineering Principles

### Definition
Chaos Engineering is the discipline of experimenting on a system to build confidence in its capability to withstand turbulent conditions in production.

### Principles of Chaos
1. **Start by defining 'steady state'** — measurable output (e.g., P99 latency < 200ms, error rate < 0.1%)
2. **Hypothesize about steady state** — "If we kill one server, steady state continues"
3. **Introduce real-world variables** — Kill processes, inject latency, exhaust resources
4. **Try to disprove the hypothesis** — If steady state deviates, you found a weakness
5. **Minimize blast radius** — Start small, expand gradually
6. **Automate experiments** — Run continuously, not just once

### Common Chaos Experiments
| Experiment | What It Tests | Tool |
|------------|---------------|------|
| Kill a pod | Stateless service resilience | Chaos Mesh, Litmus |
| Inject network latency | Timeout handling | Toxiproxy, tc |
| Fill disk | Log rotation, alerting | Chaos Toolkit |
| Expire TLS cert | Certificate monitoring | Custom |
| Drop packets | Retry logic, circuit breakers | Chaos Monkey |
| Exhaust CPU | Auto-scaling, throttling | Stress tool |
| Regional failover | DR plan effectiveness | Gremlin |
| Load spike | Auto-scaling, rate limiting | Locust, k6 |

### Maturity Model
| Level | Description |
|-------|-------------|
| 1. Ad-hoc | Manual chaos experiments, no automation |
| 2. Repeatable | Automated experiments, documented runbooks |
| 3. Defined | Experiments run on schedule, integrated with CI/CD |
| 4. Managed | Blast radius controlled, steady state well-defined, experiments cover critical paths |
| 5. Optimizing | Continuous experimentation, automated rollback of experiments, game days |

## Quick Reference: Key Interview Formulas

```
Availability = uptime / total_time
Availability = successful_requests / total_requests

Error budget consumed = (1 - actual_availability / SLO_target) × 100
Error budget remaining = error_budget - error_budget_consumed

MTTR = total_time_to_resolve / number_of_incidents
MTTD = total_time_to_detect / number_of_incidents
MTBF = total_uptime / number_of_failures

Throughput = total_requests / measurement_window
Latency percentile = value at rank (e.g., P99 = 99th percentile)
Concurrency = throughput × average_latency

RTO = maximum acceptable downtime (recovery time objective)
RPO = maximum acceptable data loss (recovery point objective)
```

## Interview Response Patterns

### When asked "How would you debug X?"
1. **Isolate** — narrow down by region, user, version, time
2. **Correlate** — check recent deployments, config changes
3. **Layer-by-layer** — app → infra → DB → network
4. **Metrics, Logs, Traces** — use observability tools
5. **Mitigate first** — rollback, feature flag, scale up
6. **Root cause** — identify and fix permanently
7. **Prevent** — monitoring, tests, runbooks

### When asked "Design a reliable system"
1. Understand requirements (functional + non-functional)
2. High-level architecture (LB, app, cache, DB, queue)
3. Failure modes (what happens when each component fails)
4. Reliability mechanisms (retry, circuit breaker, timeout)
5. Observability (metrics, logs, traces)
6. Operational readiness (runbooks, chaos, load testing)
