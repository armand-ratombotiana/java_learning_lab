# Consistency Models - WHY IT EXISTS

## Problem Statement
In a distributed system, multiple nodes hold copies of data. When one node is updated, others may serve stale data. Without defined consistency models, applications cannot predict what data they will read.

## Origin
Consistency models were formalized in database theory (ACID — 1980s) and extended to distributed systems by Leslie Lamport, Eric Brewer (CAP theorem, 2000), and the Amazon Dynamo paper (2007).

## Core Drivers
- **Data integrity**: Prevent corrupted or contradictory state
- **User experience**: Stale data leads to confusion (phantom cart items, double charges)
- **Business correctness**: Financial transactions require strong consistency
- **Operational simplicity**: Weak consistency requires complex resolution logic

## Why Not Just Use Strong Consistency Everywhere?
Strong consistency costs:
- Higher latency (synchronous replication)
- Lower availability (quorum requirement)
- Reduced throughput (coordination overhead)

## Java Ecosystem
Java distributed databases and frameworks handle consistency:
- **Cassandra**: Tunable consistency with Java driver
- **Apache ZooKeeper**: Strongly consistent coordination (Zab)
- **Hazelcast**: Distributed data structures with configurable consistency
- **JGroups**: Reliable multicast communication
