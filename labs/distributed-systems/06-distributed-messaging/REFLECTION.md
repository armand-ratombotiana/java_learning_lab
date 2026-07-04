# Distributed Messaging: Reflection

## Key Insights
- Messaging decouples services enabling independent evolution
- Kafka's log-based architecture is fundamentally different from traditional queues
- Choose the right broker for your workload characteristics
- Exactly-once is expensive; at-least-once + idempotency is often sufficient

## Questions
1. Is your system using synchronous calls where async would be better?
2. Do you have proper error handling for undeliverable messages?
3. Can you reprocess events without side effects?
4. What's your message retention and replay strategy?

## Personal Notes
- Event-driven architecture requires mindset shift from request-response
- Monitoring consumer lag is essential for production operations
- Schema evolution (Avro, Protobuf) prevents many production issues
