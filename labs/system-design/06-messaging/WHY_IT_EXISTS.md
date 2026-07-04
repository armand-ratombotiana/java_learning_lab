# Messaging - WHY IT EXISTS

## Problem Statement
In synchronous systems, services are tightly coupled. If Service A calls Service B directly and B is slow or down, A fails too. Scaling, deploying, and maintaining coupled services is painful.

## Origin
Messaging systems originated from enterprise application integration (EAI) in the 1990s. IBM MQ Series (1992) and TIBCO Rendezvous pioneered reliable messaging. Apache Kafka (2011) revolutionized stream processing at scale.

## Core Drivers
- **Decoupling**: Producers and consumers don't need to know about each other
- **Resilience**: Messages survive service failures (persistent queues)
- **Scalability**: Add consumers to increase processing throughput
- **Async processing**: Non-blocking operations improve user experience
- **Buffering**: Handle traffic spikes by queuing messages

## Why Not Direct HTTP Calls?
- Tight coupling (both services must be online)
- No retry/buffering for failures
- No message persistence
- Difficult to replay or audit
- Hard to add new consumers without changing producers

## Java Ecosystem
- **Apache Kafka**: Distributed streaming platform (de facto standard)
- **RabbitMQ**: AMQP-based messaging broker
- **ActiveMQ**: JMS-compatible broker
- **Spring Cloud Stream**: Event-driven microservices framework
- **Pulsar**: Multi-tenant, geo-replicated messaging
