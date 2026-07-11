# Kafka Internals

Welcome to the atomic mastery lab for **Kafka Internals**. This lab is part of the System Design Academy's Messaging module.

## 🧠 What You Will Master
- Why Kafka is not a traditional Message Queue (like RabbitMQ).
- The Log-Structured Storage engine and O(1) disk reads/writes.
- Partitions, Offsets, and Consumer Groups.
- Zero-Copy optimization and OS page cache utilization.
- The architecture of Distributed Commit Logs.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - The architectural shift from Queues to Logs.
2. [INTERNALS.md](./INTERNALS.md) - Deep dive into Zero-Copy and disk I/O.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Simulating a Log-Structured append-only file in Java.