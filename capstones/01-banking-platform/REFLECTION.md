# Reflection: Banking Platform

## What I Learned
- Distributed transaction patterns (Saga vs 2PC) and when to use each
- The importance of idempotency in financial systems
- How fraud detection combines rule engines with ML models
- Event-driven architecture for loosely coupled service communication

## Challenges
- Ensuring data consistency across services without distributed transactions
- Debugging race conditions in balance updates
- Tuning Kafka consumer lag during peak traffic

## What I'd Do Differently
- Start with a clear event schema contract before implementing services
- Invest in integration tests early for the critical payment flow
- Use Protobuf instead of JSON for inter-service communication
- Implement more comprehensive monitoring dashboards from day one
