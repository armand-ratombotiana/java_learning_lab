# Interview: Banking Platform

## Common Questions

### Q: How would you design a payment system that handles millions of transactions per day?
Key points: microservices, CQRS, event sourcing, idempotency, horizontal scaling per service, Kafka for event backbone, database sharding by account ID.

### Q: How do you prevent double-spending in a distributed system?
Use idempotency keys on the write path, optimistic locking on account rows, and a distributed lock (Redis Redlock) for critical transfers.

### Q: How would you handle a fraud alert that's a false positive?
Implement a review queue with manual override. Store the fraud decision and allow admin to mark as false positive, which triggers model retraining.

### Q: What happens if the Fraud Service is down?
Fall back to rule-only mode (no ML). If rules are also unavailable, reject transactions that exceed a lower threshold to minimize risk.

### Q: How do you ensure regulatory compliance (SOX, PCI-DSS)?
Encrypt PII at rest, audit all data access, retain transaction logs for 7 years, implement access controls, and undergo regular SOC2 audits.
