# Kafka Architecture Diagram

## Core Components

```
┌─────────────────────────────────────────────────────────┐
│                     ZooKeeper (or KRaft)                 │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                      Kafka Cluster                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐        │
│  │  Broker 1   │ │  Broker 2   │ │  Broker 3   │        │
│  │  (Leader)   │ │  (Follower) │ │  (Follower) │        │
│  └─────────────┘ └─────────────┘ └─────────────┘        │
└─────────────────────────────────────────────────────────┘
         │                  │                  │
         ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────┐
│                    Topics & Partitions                   │
│                                                          │
│  Topic: orders                                          │
│  ┌──────┐ ┌──────┐ ┌──────┐                            │
│  │ P0   │ │ P1   │ │ P2   │                            │
│  └──┬───┘ └──┬───┘ └──┬───┘                            │
└─────┼────────┼────────┼─────────────────────────────────┘
      │        │        │
      ▼        ▼        ▼
┌──────────┐ ┌──────────┐ ┌──────────┐
│Producer 1│ │Producer 2│ │Producer 3│
└──────────┘ └──────────┘ └──────────┘
```

## Key Concepts

### Broker
A single Kafka server that stores topics and serves clients.

### Topic
A category or feed name to which records are published.

### Partition
Ordered, immutable sequence of records within a topic.

### Producer
Publishes records to topics.

### Consumer
Subscribes to topics and processes records.

### Consumer Group
Logical grouping of consumers for scalable consumption.

## Data Flow
1. Producer → Topic (partitioned)
2. Broker stores messages
3. Consumer Group reads from partitions
4. Offsets tracked for replay capability