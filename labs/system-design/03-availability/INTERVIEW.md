# Availability - INTERVIEW

## Common Interview Questions

### Q1: How do you calculate system availability?
**Answer**: Availability = MTBF / (MTBF + MTTR). For a chain of services, multiply individual availabilities. For parallel (redundant) components: 1 - (1-A1)×(1-A2).

### Q2: Explain the circuit breaker pattern.
**Answer**: Three states: CLOSED (normal), OPEN (failures > threshold → reject fast), HALF_OPEN (probing for recovery). Prevents cascading failures and provides fast failure.

### Q3: What's the difference between active-active and active-passive?
**Answer**: Active-active: all instances serve traffic, handles failure by redistribution. Active-passive: one serves, one(s) wait, on failure the standby takes over. Active-active uses resources better but is harder to implement with stateful services.

### Q4: How do you prevent cascading failures?
**Answer**: Circuit breakers, bulkheads (separate thread pools per dependency), load shedding, graceful degradation, and independent failure domains (cells).

### Q5: What is chaos engineering?
**Answer**: Intentionally injecting failures into production to build confidence in the system's ability to handle them. Netflix's Chaos Monkey was the pioneer. Principles: steady state hypothesis, run experiments in production, automate experiments.

### Q6: Explain RTO and RPO.
**Answer**: RTO (Recovery Time Objective) = max acceptable downtime. RPO (Recovery Point Objective) = max acceptable data loss. For a payment system: RTO=1min, RPO=0sec. For a log aggregator: RTO=1hr, RPO=15min.

### Q7: How does a load balancer detect unhealthy instances?
**Answer**: Health checks performed periodically (e.g., every 5s). If N consecutive checks fail (e.g., 3), the instance is marked unhealthy. Traffic stops until checks succeed again.

## System Design Problem: Design a Fault-Tolerant Payment System

### Requirements
- Process 10K payments/second
- Zero data loss (RPO = 0)
- < 1 minute failover (RTO = 1 min)

### Proposed Solution
- **Dual-region active-active with synchronous replication**
- **Event sourcing + outbox pattern for reliability**
- **Idempotency keys on all payment operations**
- **Circuit breakers on downstream (card networks)**
- **Dead letter queue for failed payments with retry**
