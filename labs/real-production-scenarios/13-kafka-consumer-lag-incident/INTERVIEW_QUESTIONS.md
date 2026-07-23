# Lab 13 — Kafka Consumer Lag Incident: Interview Questions

**Q1: What is Kafka consumer lag and why is it important?**

**Answer:** Consumer lag is the difference between the latest offset in a Kafka partition and the last offset processed by the consumer. It represents the backlog of unprocessed messages. High lag means messages are piling up faster than they're consumed, causing increasing processing delays. Monitoring lag is critical because: 1) It indicates processing capacity problems. 2) High lag can lead to data staleness. 3) Growing lag predicts future data loss (if retention exceeded). 4) Lag spikes correlate with consumer issues (slow processing, crashes, rebalancing).

**Q2: What are common causes of increasing consumer lag?**

**Answer:** 1) Insufficient consumer instances (too few partitions? too few consumers?). 2) Slow message processing (downstream DB slow, external API slow, transformation CPU-intensive). 3) Consumer crashes or restarts (lag accumulates while consumer is down). 4) Consumer rebalancing (all consumers stop processing during rebalance). 5) Producer publishing rate exceeds consumer processing capacity. 6) Backpressure from downstream services. 7) Poison pill messages that crash the consumer. 8) Garbage collection pauses (Stop-The-World pauses stop processing).

**Q3: How do you calculate the number of consumers needed for a topic?**

**Answer:** Formula: consumers = max(partitions, required_throughput / consumer_throughput). Where: required_throughput = peak message rate (msg/s). consumer_throughput = messages a single consumer can process per second (measured with load testing). Example: topic has 12 partitions, peak rate 120,000 msg/s, consumer processes 10,000 msg/s. Required consumers = max(12, 120,000/10,000) = max(12, 12) = 12. Rule: consumers per consumer group ≤ topic partitions. More consumers than partitions are idle.

**Q4: How do you monitor Kafka consumer health?**

**Answer:** 1) Consumer lag per partition — Prometheus + Grafana dashboard. 2) Consumer lag growth rate — increasing trend indicates trouble. 3) Processing rate (msg/s) per consumer. 4) Consumer group state (Stable, Rebalancing, Dead). 5) Rebalance frequency — frequent rebalances indicate unstable consumer group. 6) Last committed offset time — is the consumer still active? 7) Consumer thread pool utilization. 8) Downstream service latency. 9) Message processing error rate.

**Q5: Your consumers start lagging after a deployment. What could have changed?**

**Answer:** 1) Message processing logic slowed down (new DB calls, new API calls, more CPU-intensive logic). 2) Message schema changed — larger messages take longer to process. 3) Consumer thread pool configuration changed (too few threads). 4) Batching size changed (fetch.min.bytes, max.poll.records). 5) Downstream service became slower (DB index missing, API timeout). 6) Number of consumer instances decreased (deployment misconfiguration). 7) max.poll.interval.ms too low — consumers kicked out of group while processing long batches.

**Q6: How do you handle a poison pill message that crashes the consumer?**

**Answer:** 1) Detection: consumer process crashes, rebalancing, same partition always fails. 2) Identify the poison message: skip to next offset on the problematic partition. 3) Consume the poison message manually: `kafka-console-consumer --partition X --offset Y`. 4) Analyze: what's wrong with the message (malformed, null, oversized)? 5) Permanent fix: add error handling in the consumer (try-catch, DLQ). 6) Send to Dead Letter Queue (DLQ) — separate topic for failed messages. 7) Monitor DLQ for error patterns.

**Q7: Tell me about a Kafka lag incident you resolved. (STAR)**

**Answer:** Situation: A consumer processing orders started lagging 2M messages behind during Black Friday peak. Task: Reduce lag before messages exceeded retention (7 days) and were lost. Action: I identified the root cause — the consumer was making a synchronous HTTP call to a downstream service that was throttling. I implemented: 1) Bulkhead pattern: separate thread pool for downstream calls. 2) Circuit breaker: fail fast when downstream is slow. 3) Parallel processing: increase consumers from 6 to 12 (matching partitions). 4) Rate limiting on consumer side to match downstream capacity. Result: Lag reduced from 2M to 10K within 4 hours. No data loss.

**Q8: What is the difference between Kafka's at-least-once and exactly-once semantics?**

**Answer:** At-least-once: messages may be re-processed after consumer crash/rebalance. Consumer commits offset after processing. If crash between processing and commit, message is re-processed. Exactly-once: each message processed exactly once. Kafka 0.11+ supports idempotent producers and transactions — consumer commits offset and processing result atomically. Exactly-once is complex (transaction coordinator, idempotent writes) and has overhead. Most systems use at-least-once with idempotent consumers (processing is idempotent, duplicates are safe).

**Q9: How do you handle Kafka consumer rebalancing issues?**

**Answer:** 1) Reduce rebalance frequency: set session.timeout.ms (default 45s) and heartbeat.interval.ms (default 3s). 2) Increase max.poll.interval.ms (default 5 min) if processing can take longer. 3) Use cooperative rebalancing (assignor: CooperativeStickyAssignor, KIP-429) for fewer "stop-the-world" events. 4) Monitor rebalance events — track count and duration. 5) Static group membership: assign fixed instance IDs to consumers so rebalance doesn't reassign partitions on restart. 6) Tune: partition.assignment.strategy for rebalance optimization.

**Q10: Design a Kafka consumer monitoring and auto-scaling system.**

**Answer:** 1) Metrics: export consumer lag per partition, processing rate, error rate to Prometheus. 2) Auto-scaling based on lag: if consumer lag per partition > threshold (e.g., 10,000), increase consumers. 3) But: can't exceed partition count. So auto-scale based on aggregate: if total lag > N, increase partitions (Kafka admin API). 4) Downstream capacity: monitor downstream latency — if it increases, add more consumers (if partition-limited) or add throughput to downstream. 5) Alert: lag > 100,000 for > 5 min (page). 6) Dashboard: real-time lag, processing rate, consumer count, rebalance events.
