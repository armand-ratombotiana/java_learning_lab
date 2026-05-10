# Kafka - Pedagogic Guide

---

## Learning Objectives

By the end of this module, learners will be able to:

1. **Understand Kafka Architecture**
   - Explain topic, partition, and replica concepts
   - Describe message lifecycle in Kafka
   - Identify when to use Kafka vs other messaging systems

2. **Implement Kafka Producers**
   - Create reliable message producers
   - Configure serialization formats
   - Implement idempotent producers

3. **Implement Kafka Consumers**
   - Build consumer groups for scalability
   - Handle offset management
   - Implement exactly-once processing

4. **Build Stream Processing Applications**
   - Use Kafka Streams for stateful processing
   - Implement windowed aggregations
   - Perform stream joins

---

## Teaching Sequence

### Phase 1: Fundamentals (2-3 hours)

**Topic 1: Kafka Basics**
- Lecture: Kafka architecture and components
- Demo: Create topics, produce and consume messages
- Exercise: Set up Kafka and run basic producer/consumer

**Topic 2: Producers**
- Lecture: Producer configuration and guarantees
- Demo: Implement reliable producer with retries
- Exercise: Send messages with different serialization

### Phase 2: Consumers (2-3 hours)

**Topic 3: Consumer Groups**
- Lecture: Consumer group dynamics
- Demo: Multi-consumer implementation
- Exercise: Scale consumers by adding instances

**Topic 4: Offset Management**
- Lecture: Auto vs manual commit
- Demo: Implement exactly-once with manual commits
- Exercise: Handle consumer rebalance scenarios

### Phase 3: Stream Processing (3-4 hours)

**Topic 5: Kafka Streams Basics**
- Lecture: Stream processing model
- Demo: Implement simple stream transformation
- Exercise: Count events in windows

**Topic 6: Advanced Streams**
- Lecture: State stores and joins
- Demo: Implement stream-stream join
- Exercise: Build real-time dashboard

### Phase 4: Production Patterns (2-3 hours)

**Topic 7: Schema Registry**
- Lecture: Schema evolution and compatibility
- Demo: Configure Avro with Schema Registry
- Exercise: Add new field with backward compatibility

**Topic 8: Reliability Patterns**
- Lecture: Exactly-once semantics
- Demo: Implement transactional outbox pattern
- Exercise: Build event sourcing system

---

## Hands-On Projects

### Mini-Project: Event Streaming System
**Duration**: 4-5 hours
**Focus**: Core Kafka concepts

Learning outcomes:
- Producer implementation
- Consumer groups
- Basic stream processing
- Error handling

### Real-World Project: E-Commerce Streaming Platform
**Duration**: 15+ hours
**Focus**: Production patterns

Learning outcomes:
- Multiple topic architecture
- Kafka Streams processing
- Schema Registry integration
- Exactly-once guarantees
- Real-time analytics

---

## Assessment Criteria

### Must Have (Core)
- [ ] Create Kafka topics with partitions
- [ ] Implement reliable producer with retries
- [ ] Build consumer with manual offset commit
- [ ] Configure consumer groups

### Should Have (Intermediate)
- [ ] Use Kafka Streams for transformations
- [ ] Implement windowed aggregations
- [ ] Configure Schema Registry
- [ ] Handle consumer rebalance

### Nice to Have (Advanced)
- [ ] Implement exactly-once processing
- [ ] Build transactional outbox
- [ ] Create event sourcing system
- [ ] Optimize partition strategy

---

## Common Pitfalls

1. **Partition Misuse**
   - Don't put all messages in one partition
   - Choose partition key based on access patterns

2. **Consumer Lag**
   - Monitor consumer lag constantly
   - Scale consumers when lag grows

3. **Schema Changes**
   - Always use backward compatible changes
   - Never delete fields from schemas

4. **Exactly-Once Confusion**
   - Exactly-once is expensive - use it only when needed
   - Understand the difference between semantics

---

## Discussion Questions

1. When would you choose Kafka over RabbitMQ?
2. How do you handle schema evolution in production?
3. What are the trade-offs of exactly-once processing?
4. How do you monitor Kafka health in production?

---

## Extension Activities

1. **Performance Challenge**: Achieve 100K messages/second throughput
2. **Reliability Challenge**: Build zero-message-loss system
3. **Scalability Challenge**: Handle 10x traffic increase

---

## Additional Resources

- Kafka: The Definitive Guide
- Confluent Blog on Kafka Streams
- Kafka Improvement Proposals (KIPs)
- Kafka Summit Talks

