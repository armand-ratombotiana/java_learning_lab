# Interview Questions — Event-Driven Architecture

## Q1: What is the difference between Event Sourcing and CQRS?
**A:** Event Sourcing stores state as a sequence of events, while CQRS separates read and write models. They are often used together but are independent patterns.

## Q2: How do you handle event schema evolution?
**A:** Use event versioning with upcasters — transform old-version events to the current schema during replay. Maintain backward compatibility.

## Q3: How does idempotency work in event-driven systems?
**A:** Track processed event IDs in a persistent store. Before processing, check if the event has already been handled. If so, skip it and return the previous result.

## Q4: What routing strategies exist for event buses?
**A:** Direct (point-to-point), topic-based (publish/subscribe), and content-based routing. Apache Kafka and RabbitMQ are common implementations.
