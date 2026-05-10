# Exercises - RabbitMQ

## Exercise 1: Direct Messaging
Implement basic point-to-point messaging:

1. Create a producer service to send messages
2. Create a consumer service to receive messages
3. Configure direct exchange with binding key
4. Test message routing based on routing key

## Exercise 2: Work Queue Pattern
Implement task distribution with competing consumers:

1. Set up a queue with multiple consumers
2. Send multiple tasks and observe load balancing
3. Configure prefetch count for fair dispatch
4. Handle message acknowledgment modes (auto vs manual)

## Exercise 3: Pub/Sub Pattern
Implement publish-subscribe with fanout exchange:

1. Create fanout exchange with multiple queues
2. Publish event to exchange
3. Verify all bound queues receive the message
4. Implement message filtering with direct exchanges

## Exercise 4: Dead Letter Queue
Implement error handling with DLQ:

1. Configure DLX for failed message routing
2. Set up DLQ consumer to inspect failed messages
3. Implement retry logic with exponential backoff
4. Create monitoring for DLQ message count

## Exercise 5: Message Ordering & Correlation
Handle complex message patterns:

1. Use correlation ID for request-response pattern
2. Configure multiple priority queues
3. Implement consumer-side ordering guarantees
4. Test message deduplication scenarios

## Exercise 6: Advanced Messaging Patterns
Implement sophisticated messaging patterns:

1. Build request-reply pattern with reply-to queue
2. Implement scatter-gather for multi-cast responses
3. Create message aggregator with batch timeout
4. Build saga orchestrator with compensating transactions
5. Implement claim check pattern for large messages

## Exercise 7: Message Transformation
Handle message format conversions:

1. Create content enricher for message augmentation
2. Implement message translator for format conversion
3. Build message router with content-based routing
4. Create splitter-aggregator pattern
5. Implement claim check to reduce payload size

## Bonus Challenge
Build a distributed transaction system using:
- Outbox pattern for reliable messaging
- SAGA orchestration for multi-service updates
- Compensation handlers for rollback
- Message deduplication with idempotency keys

## Exercise 8: High Availability Setup
Configure RabbitMQ for production:

1. Set up a 3-node cluster with mirrored queues
2. Configure quorum queues for durability
3. Implement federated queues across datacenters
4. Set up shovel plugin for queue replication
5. Configure load balancer for HAProxy