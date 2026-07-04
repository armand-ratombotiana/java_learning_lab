# Event-Driven Architecture Quiz

## Question 1
What is the key difference between an event and a command?
- a) Events are synchronous, commands are async
- b) Events are facts about the past, commands are instructions for the future
- c) Events are JSON, commands are XML
- d) No difference

**Answer: b)**

## Question 2
What does Kafka use to ensure ordering within a partition?
- a) Random assignment
- b) Key-based partitioning
- c) Timestamp ordering
- d) ACK mechanism

**Answer: b)**

## Question 3
What is a dead letter queue used for?
- a) Storing successfully processed events
- b) Storing events that failed processing after retries
- c) Queue for scheduled events
- d) Backup queue for high-priority events

**Answer: b)**

## Question 4
What is the at-least-once delivery guarantee?
- a) Event may be delivered multiple times
- b) Event is delivered exactly once
- c) Event may be lost once
- d) Event is delivered at most once

**Answer: a)**

## Question 5
What is event-carried state transfer?
- a) Transferring state via REST API
- b) Including relevant data in the event for consumer convenience
- c) Transferring state between databases
- d) State synchronization protocol

**Answer: b)**

## Question 6
When should you NOT use event-driven architecture?
- a) Strong consistency requirements
- b) Loose coupling needed
- c) Audit trail requirements
- d) Async processing needs

**Answer: a)**
