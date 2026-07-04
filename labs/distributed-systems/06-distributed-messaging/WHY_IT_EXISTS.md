# Why Distributed Messaging Exists

## Problem
- Synchronous communication creates tight coupling
- Services become unavailable if downstream services fail
- Traffic spikes cause cascading failures
- Need for event-driven architectures

## Solution
Message brokers provide:
- **Decoupling**: Producers and consumers don't know about each other
- **Buffering**: Handle traffic spikes gracefully
- **Reliability**: Persistent message storage
- **Scalability**: Independent scaling of producers and consumers
- **Routing**: Flexible message delivery patterns

## Use Cases
- Event-driven microservices
- Stream processing (ETL, analytics)
- Log aggregation
- Command Query Responsibility Segregation (CQRS)
- Integration patterns
