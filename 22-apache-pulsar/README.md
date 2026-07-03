# 22 - Apache Pulsar

Apache Pulsar messaging concepts. Covers topics and subscriptions (exclusive, shared, failover), producers and consumers, multi-tenancy (namespaces, tenants), message replay and retry, subscription types and delivery semantics, and geo-replication simulation.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- Topics: persistent/non-persistent, partitioned vs non-partitioned
- Subscription types: Exclusive (single consumer), Shared (round-robin), Failover (primary-standby)
- Producers: send messages with keys, multi-topic publishing
- Consumers: receive and acknowledge messages
- Multi-tenancy: tenant/namespace isolation
- Message replay: re-consume from specific position
- Geo-replication: cross-region message replication
- Retry and dead letter topics

## Module Structure

- `01-pulsar-basics/` - Pulsar messaging concepts simulation

## Learning Objectives

- Understand Pulsar topics, subscriptions, and delivery semantics
- Implement producers and consumers with different subscription types
- Configure multi-tenancy and message replay

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 22-apache-pulsar
mvn clean package
```

Run the lab:

```bash
cd 01-pulsar-basics
mvn compile exec:java -Dexec.mainClass="com.learning.pulsar.Lab"
```
