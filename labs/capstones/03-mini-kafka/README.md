# Mini Kafka

A simplified Kafka-like message broker built in Java, implementing topic/partition management, producer with sync/async batching, consumer with offset tracking and group coordination, log storage with segments and compaction, and basic leader election.

## Architecture Overview

```
┌──────────┐   produce    ┌──────────────────┐   consume    ┌──────────┐
│ Producer │──────────────│  Message Broker   │─────────────│ Consumer │
│ (sync/   │              │                    │              │ (poll/   │
│  async)  │              │  ┌───┐ ┌───┐ ┌───┐ │              │  commit) │
└──────────┘              │  │T0 │ │T0 │ │T1 │ │              └──────────┘
                          │  │P0 │ │P1 │ │P0 │ │              ┌──────────┐
┌──────────┐              │  └───┘ └───┘ └───┘ │              │ Consumer │
│ Offset   │◄─────commit──│  Log Segments       │───assign────│ Group    │
│ Manager  │              │  Compaction         │              │ Coordinator
└──────────┘              └──────────────────┘              └──────────┘
```

## Features

- **TopicPartition**: Append-only log with offset-based reads, key-based compaction, header support
- **MessageBroker**: Central broker with topic creation, produce/consume, offset commit
- **ProducerClient**: Sync mode (immediate write) and async mode (batch + linger), configurable acks
- **ConsumerClient**: Subscribe to topics, poll with timeout, commitSync, auto-offset-reset
- **ConsumerGroup**: Member management, partition assignment, leader election, rebalancing
- **LogSegment**: File-based segment storage, flush to disk, size tracking
- **OffsetManager**: Per-group/topic/partition offset tracking, reset capability

## Usage

```java
var broker = new MessageBroker();
broker.createTopic("orders", 3);
broker.produce("orders", "key1".getBytes(), "order-data".getBytes());

var producer = new ProducerClient(broker,
    new ProducerConfig("prod-1", true, 100, 50, "1"));
producer.send("orders", "k1".getBytes(), "v1".getBytes());
producer.flush();

var consumer = new ConsumerClient(broker,
    new ConsumerConfig("group-1", "cons-1", "earliest", 500));
consumer.subscribe("orders");
var messages = consumer.poll(Duration.ofMillis(1000));
consumer.commitSync();
```
