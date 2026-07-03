# Module 39: Message Queues & Event Streaming - Quizzes

---

## Q1: Kafka vs RabbitMQ
What is a key difference between a traditional message queue like RabbitMQ and an event streaming platform like Apache Kafka?

A) RabbitMQ uses HTTP, while Kafka uses FTP.
B) In Kafka, messages are retained in an append-only log even after being consumed, whereas RabbitMQ typically deletes messages once they are consumed and acknowledged.
C) Kafka is synchronous, while RabbitMQ is asynchronous.
D) RabbitMQ is only used in monolithic applications.

**Answer**: B
**Explanation**: Kafka acts as a distributed commit log, allowing multiple independent consumer groups to "replay" events from the past. Traditional queues generally delete a message as soon as it is successfully processed.

---

## Q2: Idempotency
Why must consumer applications in a message-driven architecture be "idempotent"?

A) Because message brokers guarantee "At-Least-Once" delivery, meaning a consumer might receive the exact same message more than once (e.g., due to network timeouts).
B) To make the code compile faster.
C) Because brokers guarantee "Exactly-Once" delivery natively.
D) To prevent hackers from reading the messages.

**Answer**: A
**Explanation**: In distributed systems, acknowledging a message can fail due to network partitions. The broker will redeliver the unacknowledged message. The consumer must be able to process duplicates safely without causing unintended side-effects (like double-charging a customer).

---

## Q3: Dead Letter Queue (DLQ)
What is the purpose of a Dead Letter Queue?

A) To store messages that have successfully completed processing.
B) To act as a backup database.
C) To store "poison pill" messages that cannot be processed successfully, preventing them from infinitely blocking the main queue.
D) To delete messages permanently.

**Answer**: C
**Explanation**: If a consumer repeatedly fails to process a message (e.g., malformed JSON), leaving it in the queue will cause an infinite loop of failures. The consumer should route the bad message to a DLQ for manual analysis and continue processing the rest of the queue.