# Event-Driven Architecture Interview Questions

## Junior Level

### Q: Explain the difference between a queue and a topic.
**A:** A queue uses point-to-point messaging (one consumer gets each message). A topic uses publish-subscribe (all subscribers get each message).

### Q: What is consumer group in Kafka?
**A:** A group of consumers that coordinate to consume partitions. Each partition is consumed by exactly one consumer in the group.

## Mid Level

### Q: How do you handle event duplicate processing?
**A:** Use idempotent consumers by tracking processed event IDs, or design event handling to be naturally idempotent.

### Q: Explain the trade-off between partition count and performance.
**A:** More partitions = more parallelism but also more files, more memory, and higher latency for ordering guarantees across partitions.

## Senior Level

### Q: Design an event-driven payment processing system.
**A:** 
1. Payment Service publishes PaymentInitiated
2. Fraud Detection Service consumes, produces FraudCheckResult
3. Payment Gateway Service processes, produces PaymentCompleted or PaymentFailed
4. Order Service updates order status
5. Notification Service sends confirmation/failure emails
6. Dead letter queue for failed payments
7. Event replay capability for audit and recovery

### Q: How would you migrate from synchronous REST to event-driven architecture?
**A:** 
1. Add event publishing alongside existing code (dual write)
2. Deploy consumers that process events in parallel
3. Compare results between old and new paths
4. Gradually shift traffic to event-driven path
5. Remove old synchronous code once confident
6. Use feature flags for easy rollback
