# RabbitMQ - Pedagogic Guide

---

## Learning Objectives

By the end of this module, learners will be able to:

1. **Understand Messaging Patterns**
   - Explain point-to-point vs publish-subscribe
   - Describe exchange types and routing
   - Identify when to use different patterns

2. **Implement RabbitMQ Producers**
   - Create exchanges and queues
   - Configure message serialization
   - Implement publisher confirms

3. **Implement RabbitMQ Consumers**
   - Build annotation-based listeners
   - Handle message acknowledgment
   - Configure concurrency and prefetch

4. **Apply Production Patterns**
   - Implement dead letter queues
   - Handle message failures gracefully
   - Monitor queue health

---

## Teaching Sequence

### Phase 1: Fundamentals (2-3 hours)

**Topic 1: RabbitMQ Basics**
- Lecture: AMQP model and components
- Demo: Declare exchanges and queues
- Exercise: Send and receive first message

**Topic 2: Exchange Types**
- Lecture: Direct, Fanout, Topic, Headers
- Demo: Implement each exchange type
- Exercise: Route messages using topic patterns

### Phase 2: Producers (2-3 hours)

**Topic 3: Message Serialization**
- Lecture: JSON, XML, Avro serialization
- Demo: Configure Jackson message converter
- Exercise: Send typed messages

**Topic 4: Publisher Confirms**
- Lecture: Reliable publishing
- Demo: Implement confirm callback
- Exercise: Handle publisher failures

### Phase 3: Consumers (2-3 hours)

**Topic 5: Consumer Configuration**
- Lecture: Prefetch and concurrency
- Demo: Configure multiple consumers
- Exercise: Scale consumer processing

**Topic 6: Acknowledgment Modes**
- Lecture: Auto vs manual ack
- Demo: Implement manual acknowledgment
- Exercise: Handle consumer failures

### Phase 4: Production Patterns (2-3 hours)

**Topic 7: Dead Letter Queues**
- Lecture: Error handling strategy
- Demo: Configure DLQ with retry
- Exercise: Build resilient message processing

**Topic 8: Delayed Messages**
- Lecture: Scheduling patterns
- Demo: Implement delayed message
- Exercise: Build scheduled notification system

---

## Hands-On Projects

### Mini-Project: Message-Driven Order Processing
**Duration**: 4-5 hours
**Focus**: Core RabbitMQ concepts

Learning outcomes:
- Exchange and queue configuration
- Producer implementation
- Consumer with acknowledgment
- Dead letter queue handling

### Real-World Project: Enterprise Message Platform
**Duration**: 12+ hours
**Focus**: Production patterns

Learning outcomes:
- Multiple exchange types
- Publisher confirms
- Consumer acknowledgment
- Priority queues
- Delayed messages

---

## Assessment Criteria

### Must Have (Core)
- [ ] Create exchanges and queues
- [ ] Send messages with routing
- [ ] Configure JSON serialization
- [ ] Implement basic consumer

### Should Have (Intermediate)
- [ ] Use publisher confirms
- [ ] Implement manual acknowledgment
- [ ] Configure dead letter queues
- [ ] Set up consumer concurrency

### Nice to Have (Advanced)
- [ ] Implement priority queues
- [ ] Configure delayed messages
- [ ] Set up cluster federation
- [ ] Implement message transformation

---

## Common Pitfalls

1. **Message Loss**
   - Use publisher confirms
   - Configure persistent messages
   - Use transactions when needed

2. **Consumer Overload**
   - Set appropriate prefetch count
   - Implement retry with backoff
   - Use dead letter queues

3. **Queue Explosions**
   - Set message TTL appropriately
   - Monitor queue sizes
   - Implement cleanup

4. **Acknowledgment Errors**
   - Always acknowledge after processing
   - Use try-finally for cleanup
   - Handle rollback properly

---

## Discussion Questions

1. When would you choose RabbitMQ over Kafka?
2. How do you handle partial failures in message processing?
3. What are the trade-offs of persistent vs transient messages?
4. How do you monitor message queue health?

---

## Extension Activities

1. **Performance Challenge**: Process 10K messages/second
2. **Reliability Challenge**: Achieve zero message loss
3. **Scalability Challenge**: Handle 10x message burst

---

## Additional Resources

- RabbitMQ in Action
- Spring AMQP Reference Documentation
- RabbitMQ Best Practices
- RabbitMQ Management Plugin Guide

