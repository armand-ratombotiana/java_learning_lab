# Availability - THEORY

## Table of Contents
1. [Introduction to Availability](#intro)
2. [SLA and SLO](#sla)
3. [Redundancy Patterns](#redundancy)
4. [Failover Mechanisms](#failover)
5. [Disaster Recovery](#disaster)
6. [High Availability Architectures](#ha)
7. [Monitoring and Alerting](#monitoring)

---

## 1. Introduction to Availability <a name="intro"></a>

### What is Availability?

Availability is the percentage of time a system is operational and accessible. It's typically expressed as a percentage of "uptime" over a given period.

### Why Availability Matters

- **User Experience**: Downtime frustrates users and loses customers
- **Revenue**: Every minute of downtime costs money
- **Reputation**: Frequent outages damage brand trust
- **SLAs**: Contractual obligations to customers

### Key Metrics

| Availability | Downtime/Year | Downtime/Month | Downtime/Week |
|--------------|---------------|----------------|---------------|
| 99% | 87.6 hours | 7.3 hours | 1.68 hours |
| 99.9% | 8.76 hours | 43.8 minutes | 10.1 minutes |
| 99.99% | 52.6 minutes | 4.38 minutes | 1.01 minutes |
| 99.999% | 5.26 minutes | 26.3 seconds | 6.05 seconds |

---

## 2. SLA and SLO <a name="sla"></a>

### SLA (Service Level Agreement)

A contract between service provider and customer defining expected service level.

**Components**:
- Uptime guarantee (e.g., 99.9%)
- Response time commitments
- Support availability
- Compensation for breaches

### SLO (Service Level Objective)

Internal targets that teams aim for to meet the SLA.

**Example**:
- SLA: 99.9% uptime (max 8.76 hours/year)
- SLO: 99.95% uptime (max 4.38 hours/year)
- This 50% buffer provides safety margin

### SLI (Service Level Indicator)

Quantitative measure of a service level aspect.

**Common SLIs**:
- Request success rate
- Latency (p50, p95, p99)
- Error rate
- Throughput

---

## 3. Redundancy Patterns <a name="redundancy"></a>

### Types of Redundancy

#### Hardware Redundancy
- RAID arrays for storage
- Dual power supplies
- Multiple network paths

#### Software Redundancy
- Multiple application instances
- Service replication
- Database replication

#### Geographic Redundancy
- Multi-region deployment
- Data center redundancy

### Redundancy Levels

**Level 1 - Single Instance**
- No redundancy
- Lowest cost, highest risk

**Level 2 - Active-Passive**
- One active, one standby
- Automatic failover
- 2x cost

**Level 3 - Active-Active**
- Multiple active instances
- Load balanced
- Highest cost, highest reliability

---

## 4. Failover Mechanisms <a name="failover"></a>

### Automatic Failover

Systems that detect failures and redirect traffic automatically.

**Components**:
- Health checks
- Failure detection
- Traffic rerouting
- Notification system

### Failover Patterns

#### Leader-Follower
- One leader handles writes
- Followers handle reads
- Automatic leader election

#### Dual-Primary
- Both primaries handle traffic
- Data synchronization required
- Complex conflict resolution

#### Circuit Breaker
- Prevents cascading failures
- Stops requests to failing service
- Provides fallback

### Failover Considerations

- **Data Consistency**: Ensure no data loss
- **State Transfer**: Session/connection handling
- **Failback**: Process of returning to normal
- **Testing**: Regular failover drills

---

## 5. Disaster Recovery <a name="disaster"></a>

### Disaster Recovery Strategies

#### RTO (Recovery Time Objective)
- Maximum acceptable time to restore service

#### RPO (Recovery Point Objective)
- Maximum acceptable data loss measured in time

| Strategy | RTO | RPO | Cost |
|----------|-----|-----|------|
| Backup & Restore | 24+ hours | 24 hours | Low |
| Pilot Light | 1-4 hours | 1 hour | Medium |
| Warm Standby | 30-60 min | 15 min | High |
| Multi-Region Active-Active | < 5 min | Near zero | Very High |

### Disaster Recovery Components

- **Backup Systems**: Regular data backups
- **Recovery Procedures**: Documented processes
- **Runbooks**: Step-by-step guides
- **Testing**: Regular DR drills

---

## 6. High Availability Architectures <a name="ha"></a>

### HA Architecture Patterns

#### N+1 Architecture
- N instances + 1 standby
- Handles single failure

#### N+2 Architecture  
- N instances + 2 standby
- Handles two simultaneous failures

#### Distributed Architecture
- Multiple independent clusters
- Geographic distribution

### HA Best Practices

1. **Stateless Services**: No local state, enables easy scaling/failover
2. **Health Checks**: Detect problems early
3. **Graceful Degradation**: Reduce functionality, not everything
4. **Idempotency**: Safe to retry operations
5. **Circuit Breakers**: Prevent cascade failures

---

## 7. Monitoring and Alerting <a name="monitoring"></a>

### Monitoring Strategy

**The Four Golden Signals**:
1. **Latency**: How long requests take
2. **Traffic**: How much demand
3. **Errors**: Failure rate
4. **Saturation**: Resource utilization

### Alert Best Practices

- **Meaningful Alerts**: Only alert on actionable issues
- **Proper Severity**: Use P1/P2/P3/P4 levels
- **Alert Fatigue**: Too many alerts cause ignoring
- **On-Call Rotation**: Proper coverage

### Observability Stack

- **Metrics**: Prometheus, CloudWatch
- **Logs**: ELK, Loki
- **Traces**: Jaeger, Zipkin
- **Alerts**: PagerDuty, OpsGenie