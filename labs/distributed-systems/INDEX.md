# Distributed Systems Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-20-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Advanced_to_Expert-red?style=for-the-badge)

**Master distributed systems — from messaging to monitoring and everything in between**

</div>

---

## Overview

The Distributed Systems Academy covers the fundamental concepts and practical tools for building reliable, scalable distributed systems. You will learn messaging platforms (Kafka, Pulsar, RabbitMQ), distributed consensus, consistency models, replication strategies, and coordination services. Theory is paired with hands-on Java implementations using real-world infrastructure.

---

## Curriculum Map

### Level 1: Messaging & Streaming
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 01 | [Apache Kafka](./01-kafka/) | Topics, partitions, producers, consumers, replication, exactly-once | 5-6 hrs | Advanced | [55-kafka](../../55-kafka/), [21-kafka](../../21-kafka/) |
| 02 | [Apache Pulsar](./02-pulsar/) | Topics, subscriptions, tiered storage, geo-replication | 4-5 hrs | Advanced | [22-apache-pulsar](../../22-apache-pulsar/) |
| 03 | [RabbitMQ](./03-rabbitmq/) | Exchanges, queues, bindings, clustering, mirrored queues | 3-4 hrs | Advanced | [22-rabbitmq](../../22-rabbitmq/), [34-rabbitmq](../../34-rabbitmq/) |
| 04 | [Kafka Streams](./04-kafka-streams/) | Stateful/stateless processing, KTable, KStream, interactive queries | 5-6 hrs | Advanced | [27-kafka-streams](../../27-kafka-streams/) |

### Level 2: Coordination & State Management
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 05 | [Consul & Service Discovery](./05-consul/) | Service registration, health checks, KV store, distributed config | 3-4 hrs | Advanced | [35-consul](../../35-consul/) |
| 06 | [Hazelcast - Distributed Computing](./06-hazelcast/) | Distributed maps, executors, locks, replicated caches | 4-5 hrs | Advanced | [21-hazelcast](../../21-hazelcast/) |
| 07 | [Event Sourcing & Axon Framework](./07-axon-framework/) | Event sourcing, CQRS, aggregate, saga orchestration | 5-6 hrs | Expert | [20-axon-framework](../../20-axon-framework/), [30-event-sourcing](../../30-event-sourcing/) |

### Level 3: Distributed Systems Theory
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 08 | [Consistency Models & CAP Theorem](./08-consistency-cap/) | Strong/eventual/read-your-writes consistency, CAP theorem, PACELC | 3-4 hrs | Expert | — |
| 09 | [Distributed Consensus](./09-consensus/) | Paxos, Raft, Zab, leader election, quorum | 4-5 hrs | Expert | — |
| 10 | [Replication & Partitioning](./10-replication-partitioning/) | Leader/follower, multi-leader, consistent hashing, rebalancing | 4-5 hrs | Expert | — |
| 11 | [Distributed Transactions & Sagas](./11-distributed-transactions/) | 2PC, 3PC, Saga pattern, compensating actions | 4-5 hrs | Expert | [67-saga](../../67-saga/) |

### Level 4: Distributed Systems in Practice
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 12 | [Time Ordering](./12-time-ordering/) | Lamport clocks, vector clocks, HLC, causal ordering | 4-5 hrs | Expert | — |
| 13 | [Distributed ID Generation](./13-distributed-id-generation/) | Snowflake, UUID v7, ULID, sequential vs random IDs | 3-4 hrs | Advanced | — |
| 14 | [Distributed Locks](./14-distributed-locks/) | Redis Redlock, ZooKeeper locks, leasing, fencing tokens | 4-5 hrs | Expert | — |
| 15 | [Gossip Protocols](./15-gossip-protocols/) | SWIM, infection-style, convergence, failure detection | 3-4 hrs | Advanced | — |
| 16 | [Distributed Caching](./16-distributed-caching/) | Memcached, Redis Cluster, cache coherency, write-behind | 4-5 hrs | Advanced | — |
| 17 | [Distributed Filesystems](./17-distributed-filesystems/) | HDFS, Ceph, MinIO, erasure coding vs replication | 4-5 hrs | Expert | — |
| 18 | [Distributed Queues](./18-distributed-queues/) | SQS, Pulsar, partitioned queues, exactly-once semantics | 3-4 hrs | Advanced | — |
| 19 | [Distributed Scheduling](./19-distributed-scheduling/) | Quartz, distributed cron, leader election for jobs | 3-4 hrs | Advanced | — |
| 20 | [Distributed Monitoring](./20-distributed-monitoring/) | Prometheus federation, Thanos, Cortex, distributed tracing | 4-5 hrs | Expert | — |

**Total estimated time: 88-109 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08 ──→ 09 ──→ 10 ──→ 11 ──→ 12 ──→ 13 ──→ 14 ──→ 15 ──→ 16 ──→ 17 ──→ 18 ──→ 19 ──→ 20
Kafka  Pulsar  RMQ    KStream Consul  Hazel   Axon    CAP     Consen  Repl    Dist    Time    ID      Locks   Gossip  Cache   FS      Queues  Sched   Monitor
                                             cast    Framework        sus     &       Tx      Order           Gen
                                                                              Partition
```

Labs 01–04 cover messaging platforms. Labs 05–07 cover coordination and state. Labs 08–11 cover foundational distributed systems theory. Labs 12–20 cover practical distributed systems patterns and implementations.

---

## Prerequisites

- Strong Java proficiency (concurrency, threading, serialization)
- Understanding of networking basics (TCP, HTTP)
- Familiarity with database transaction concepts (ACID, isolation levels)
- Experience building and running microservices
- Docker for running distributed infrastructure

---

## How to Use This Academy

### For System Architects
Work through all labs. Labs 01–07 provide practical tooling; Labs 08–11 provide theoretical depth; Labs 12–20 provide production-ready patterns.

### For Backend Engineers
Focus on Labs 01–07 for messaging and coordination, and Labs 12–20 for caching, queues, scheduling, monitoring, and ID generation.

### For Infrastructure Engineers
Pay special attention to Labs 05, 06, 10, 14, 15, 17 for service discovery, caching, partitioning, distributed locks, gossip protocols, and filesystems.

---

## Related Academies

- [Architecture Academy](../architecture/) — Microservices, DDD, event-driven
- [Backend Academy](../backend/) — Messaging, reactive, frameworks
- [Databases Academy](../databases/) — Replication, consistency, sharding
- [DevOps Academy](../devops/) — Container orchestration, service mesh
- [Data Engineering Academy](../data-engineering/) — Streaming, CDC, data pipelines

---

## Resources

### Books
- *Designing Data-Intensive Applications* — Martin Kleppmann
- *Distributed Systems* — Maarten van Steen & Andrew Tanenbaum
- *Stream Processing with Kafka* — Neha Narkhede
- *Distributed Systems for Practitioners* — Dimos Raptis

### Papers
- [The Part-Time Parliament (Paxos)](https://lamport.azurewebsites.net/pubs/lamport-paxos.pdf) — Leslie Lamport
- [In Search of an Understandable Consensus Algorithm (Raft)](https://raft.github.io/raft.pdf) — Diego Ongaro
- [Dynamo: Amazon's Highly Available Key-Value Store](https://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf)
- [CAP Twelve Years Later: How the "Rules" Have Changed](https://www.infoq.com/articles/cap-twelve-years-later-how-the-rules-have-changed/)

### Tools
- [Apache Kafka](https://kafka.apache.org/)
- [Apache Pulsar](https://pulsar.apache.org/)
- [RabbitMQ](https://www.rabbitmq.com/)
- [Consul](https://www.consul.io/)
- [Hazelcast](https://hazelcast.com/)
- [Axon Framework](https://axoniq.io/)
- [Prometheus](https://prometheus.io/)
- [Thanos](https://thanos.io/)
- [MinIO](https://min.io/)
- [Redis Cluster](https://redis.io/)
- [Apache Pulsar](https://pulsar.apache.org/)
- [Quartz Scheduler](https://www.quartz-scheduler.org/)

---

<div align="center">

**Distribute with Confidence. Build Everything.**

</div>
