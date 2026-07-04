# Why Event-Driven Architecture Exists

## Historical Problems
- **Synchronous coupling** - Request-response creates temporal coupling
- **Scalability bottlenecks** - Synchronous systems can't handle peak loads gracefully
- **Data consistency challenges** - Distributed transactions are slow and brittle
- **Lack of audit trail** - Traditional CRUD loses historical context

## Business Drivers
- Real-time event processing requirements
- Need for system resilience during failures
- Audit and compliance requirements
- Complex workflows spanning multiple systems
- Streaming data from IoT, logs, user activity

## When EDA Makes Sense
- Systems requiring loose coupling
- High-volume event processing (millions of events/day)
- Complex event-driven workflows
- Audit logging and event replay requirements
- Systems needing graceful degradation under load
