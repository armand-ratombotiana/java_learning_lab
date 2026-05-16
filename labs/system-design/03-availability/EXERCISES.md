# Availability - EXERCISES

## Exercise 1-20

### Exercise 1
**Difficulty**: Easy
Calculate annual downtime for 99.9% availability.
**Answer**: 8.76 hours (365.25 × 24 × 0.001)

### Exercise 2
**Difficulty**: Medium
Convert SLA requirement of "four nines" to maximum monthly downtime.
**Answer**: 4.38 minutes (30 days × 24 × 60 × 0.00001)

### Exercise 3
**Difficulty**: Easy
What is the difference between SLA, SLO, and SLI?
**Answer**: SLA = contract, SLO = internal target, SLI = actual measurement

### Exercise 4
**Difficulty**: Medium
Design a circuit breaker with 5 failure threshold and 30-second reset timeout.
**See CODE_DEEP_DIVE section**

### Exercise 5
**Difficulty**: Easy
List three redundancy patterns.
**Answer**: Active-passive, active-active, N+1

### Exercise 6
**Difficulty**: Medium
Implement health check for service with 3-second timeout.
**See CODE_DEEP_DIVE section**

### Exercise 7
**Difficulty**: Hard
Calculate RTO and RPO for warm standby strategy.
**Answer**: RTO = 30-60 min, RPO = 15 min

### Exercise 8
**Difficulty**: Easy
What are the four golden signals?
**Answer**: Latency, traffic, errors, saturation

### Exercise 9
**Difficulty**: Medium
Design failover mechanism for database connection.
**See CODE_DEEP_DIVE section**

### Exercise 10
**Difficulty**: Hard
Implement backup scheduling system.
**See CODE_DEEP_DIVE section**

### Exercise 11-20
Similar pattern - cover load balancing failover, multi-region deployment, monitoring, alerting thresholds, chaos engineering, graceful degradation, data replication, session management, and disaster recovery testing.