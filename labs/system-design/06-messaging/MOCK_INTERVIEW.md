# Mock Interview: Messaging

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Platform Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a reliable messaging system for a ride-sharing platform.

---

## Transcript

**Interviewer**: "Our ride-sharing platform needs a messaging system to handle: 1) Real-time location updates (5M GPS pings/sec), 2) Trip events (dispatch, pickup, dropoff), 3) Payment processing (transactions). What messaging infrastructure do you recommend?"

**Candidate**: "The requirements suggest different messaging patterns for each workload. I'd use a unified event bus with different topics and retention policies for each use case."

**Interviewer**: "What technology? Kafka? RabbitMQ? SQS?"

**Candidate**: "Kafka for the event bus backbone. Why: high throughput (millions/sec), persistent storage with configurable retention (we need to replay location data for ETAs), partitioning (key by trip_id for ordered processing). SQS or RabbitMQ for specific command-style messaging (dispatch commands)."

**Interviewer**: "Walk me through the location update pipeline."

**Candidate**: "Driver app → GPS data (every 5s) → Kafka topic 'driver-locations' with 100 partitions (sharded by driver_id). Consumer: Location Service updates geospatial index (H3 grid in Redis). Consumers: ETA Service, Surge Service, Trip Matching. Retention: 24 hours (for analytics/replay)."

**Interviewer**: "How do you guarantee ordering for trip events?"

**Candidate**: "Trip events for a specific trip must be processed in order (requested → accepted → started → completed). We partition the trip-events topic by trip_id. All events for a specific trip go to the same partition, ensuring Kafka guarantees ordering within a partition. Consumers process events sequentially per partition."

**Interviewer**: "What about exactly-once processing for payment events?"

**Candidate**: "Kafka's exactly-once semantics with idempotent producers and transactional consumers. But the real pattern is: consumer processes payment event, writes result to payment DB with idempotency key, commits offset atomically. If the consumer crashes and restarts, it re-processes events but the idempotency check prevents duplicate charges."

**Interviewer**: "How do you handle consumer lag?"

**Candidate**: "Monitor consumer lag via Kafka's consumer group metrics. Set up alerts when lag exceeds thresholds (e.g., >1 minute for dispatch events, >5 minutes for analytics). Auto-scaling consumers: if lag is high, add more consumer instances (Kafka rebalances partitions across consumers). For spiky traffic (New Year's Eve), over-provision consumers."

**Interviewer**: "What's your disaster recovery for Kafka?"

**Candidate**: "Multi-region Kafka: topic with 2 replicas — one in primary region, one in secondary. MirrorMaker 2 for cross-region replication. If primary region fails: promote secondary region to active, re-point producers/consumers. CROR (Cluster Recovery for Out-of-Region): controlled failover with data integrity checks."

---

## Key Takeaways

- **One event bus, multiple purposes**: Different patterns for different workloads
- **Kafka for backbone**: High throughput, persistent, replayable
- **Partition by entity**: All events for an entity stay in order
- **Idempotent processing**: Exactly-once guarantees at the consumer level
- **Consumer lag monitoring**: Auto-scaling and over-provisioning for spikes
- **Multi-region Kafka**: MirrorMaker 2 for DR
