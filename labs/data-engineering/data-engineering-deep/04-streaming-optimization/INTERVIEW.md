# Lab 04: Streaming Optimization — Interview Questions

1. **How does exactly-once semantics work in Kafka Streams**? Explain the transaction protocol.

2. **Design a partitioning strategy** for a topic where 90% of events have the same `user_id`.

3. **What causes consumer rebalancing** and how do you minimize its impact?

4. **Compare at-least-once vs exactly-once** in Kafka Streams. When would you choose each?

5. **How do you handle out-of-order events** in a windowed aggregation?

6. **What's the impact of max.in.flight.requests.per.connection on ordering**?

7. **How do you choose the number of partitions** for a topic?

8. **Design a solution to avoid "hot partitions"** in a high-throughput topic.

9. **How does Kafka's transaction log work** for exactly-once sinks?

10. **What is the difference between cooperative and eager rebalancing**?

11. **How would you tune Kafka Streams for low-latency vs high-throughput**?

12. **How does RocksDB-based state store work** in Kafka Streams?

13. **Design a dead-letter queue (DLQ) strategy** for a Kafka Streams application.

14. **How do you monitor Kafka Streams applications**? What metrics matter?

15. **How would you implement a custom partitioner** for geographic data locality?
