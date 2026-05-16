# Messaging Quiz

## Section 1: Kafka Basics

**Question 1:** What is a Kafka topic?

A) A single message
B) A category/feed for messages
C) A broker
D) A consumer group

**Answer:** B) A category/feed for messages

---

**Question 2:** What is a Kafka partition?

A) A backup of a broker
B) Ordered, immutable sequence of messages within a topic
C) A consumer group
D) A message broker

**Answer:** B) Ordered, immutable sequence of messages within a topic

---

**Question 3:** What does the consumer group do in Kafka?

A) Sends messages to topics
B) Groups consumers that share message processing
C) Manages broker leadership
D) Stores message offsets

**Answer:** B) Groups consumers that share message processing

---

**Question 4:** What is the default port for Kafka?

A) 9092
B) 5672
C) 8080
D) 61616

**Answer:** A) 9092

---

**Question 5:** What ensures message ordering in Kafka?

A) Consumer group
B) Partition (within a partition)
C) Broker
D) Zookeeper

**Answer:** B) Partition (within a partition)

---

## Section 2: RabbitMQ Basics

**Question 6:** What is a RabbitMQ exchange?

A) A message storage
B) Routes messages to queues based on rules
C) A consumer
D) A connection

**Answer:** B) Routes messages to queues based on rules

---

**Question 7:** Which exchange type routes by exact routing key?

A) Fanout
B) Topic
C) Direct
D) Headers

**Answer:** C) Direct

---

**Question 8:** What is the default exchange in RabbitMQ?

A) ""
B) amq.direct
C) amq.fanout
D) default

**Answer:** A) "" (empty string - direct exchange)

---

**Question 9:** Which RabbitMQ exchange broadcasts to all queues?

A) Direct
B) Topic
C) Fanout
D) Headers

**Answer:** C) Fanout

---

**Question 10:** What port does RabbitMQ use by default?

A) 9092
B) 5672
C) 8080
D) 2181

**Answer:** B) 5672

---

## Section 3: Messaging Patterns

**Question 11:** What is the point-to-point pattern?

A) One message to many consumers
B) One message to one consumer
C) Asynchronous request-reply
D) Broadcasting

**Answer:** B) One message to one consumer

---

**Question 12:** What is the publish-subscribe pattern?

A) One message to one consumer
B) One message delivered to all subscribers
C) Request-reply
D) Queue-based processing

**Answer:** B) One message delivered to all subscribers

---

**Question 13:** What is a dead letter queue?

A) A queue for messages that failed processing
B) A main queue
C) A priority queue
D) A delayed queue

**Answer:** A) A queue for messages that failed processing

---

**Question 14:** Which message delivery guarantee can lose messages?

A) At-least-once
B) At-most-once
C) Exactly-once
D) Once-and-only-once

**Answer:** B) At-most-once

---

**Question 15:** What is correlation ID used for?

A) Message priority
B) Matching requests with responses
C) Message partitioning
D) Queue identification

**Answer:** B) Matching requests with responses

---

## Section 4: Reliability

**Question 16:** What delivery mode ensures messages survive broker restart?

A) Non-persistent
B) Persistent
C) Transient
D) Volatile

**Answer:** B) Persistent

---

**Question 17:** What is message acknowledgment?

A) Confirming message receipt to broker
B) Sending message to exchange
C) Creating a queue
D) Defining exchange type

**Answer:** A) Confirming message receipt to broker

---

**Question 18:** What happens in RabbitMQ when a message is rejected with requeue=false?

A) Message goes back to original queue
B) Message goes to dead letter queue
C) Message is lost
D) Message is re-sent

**Answer:** B) Message goes to dead letter queue

---

**Question 19:** What is consumer offset in Kafka?

A) Number of consumers
B) Position of consumer in partition
C) Message size
D) Partition number

**Answer:** B) Position of consumer in partition

---

**Question 20:** What does auto.offset.reset=earliest do?

A) Start from newest messages
B) Start from oldest messages
C) Skip all messages
D) Throw exception

**Answer:** B) Start from oldest messages

---

## Section 5: Spring Integration

**Question 21:** What is Spring Cloud Stream?

A) A messaging broker
B) Framework for building event-driven microservices
C) A Kafka client
D) A RabbitMQ client

**Answer:** B) Framework for building event-driven microservices

---

**Question 22:** What annotation is used for consuming messages in Spring AMQP?

A) @RabbitListener
B) @JmsListener
C) @MessageListener
D) @KafkaListener

**Answer:** A) @RabbitListener

---

**Question 23:** What class is used for high-level Kafka operations in Spring?

A) KafkaTemplate
B) RabbitTemplate
C) JmsTemplate
D) StreamTemplate

**Answer:** A) KafkaTemplate

---

**Question 24:** Which property configures Kafka bootstrap servers?

A) spring.rabbitmq.host
B) spring.kafka.bootstrap-servers
C) spring.activemq.url
D) spring.jms.brokers

**Answer:** B) spring.kafka.bootstrap-servers

---

**Question 25:** What is the purpose of @EnableBinding in Spring Cloud Stream?

**Answer:** B) Binds to message channels

---

## Section 6: Advanced

**Question 26:** What is Kafka Streams?

A) A messaging protocol
B) Lightweight stream processing library
C) A monitoring tool
D) A replication mechanism

**Answer:** B) Lightweight stream processing library

---

**Question 27:** What is a partition key used for?

A) Message filtering
B) Determining partition assignment
C) Message routing only
D) Consumer identification

**Answer:** B) Determining partition assignment

---

**Question 28:** What is retention policy in Kafka?

A) Message priority
B) How long messages are kept
C) Consumer speed
D) Producer batch size

**Answer:** B) How long messages are kept

---

**Question 29:** What is leader and replica in Kafka?

A) Topic configuration
B) Partition distribution for fault tolerance
C) Consumer states
D) Producer types

**Answer:** B) Partition distribution for fault tolerance

---

**Question 30:** What is exactly-once semantics?

A) Message may be lost
B) Message may be duplicated
C) Message processed exactly once
D) Message always arrives

**Answer:** C) Message processed exactly once

---

## Answer Key

| Question | Answer |
|----------|--------|
| 1 | B |
| 2 | B |
| 3 | B |
| 4 | A |
| 5 | B |
| 6 | B |
| 7 | C |
| 8 | A |
| 9 | C |
| 10 | B |
| 11 | B |
| 12 | B |
| 13 | A |
| 14 | B |
| 15 | B |
| 16 | B |
| 17 | A |
| 18 | B |
| 19 | B |
| 20 | B |
| 21 | B |
| 22 | A |
| 23 | A |
| 24 | B |
| 25 | B |
| 26 | B |
| 27 | B |
| 28 | B |
| 29 | B |
| 30 | C |