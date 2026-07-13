# Distributed Locks

Welcome to the atomic mastery lab for **Distributed Locks**. This lab is part of the System Design Academy's Distributed Systems module.

## 🧠 What You Will Master
- The failure of JVM-level locks in microservice architectures.
- The concept of Distributed Locking.
- Redis-based locking (SETNX) and its pitfalls.
- The Redlock Algorithm.
- Consensus-based locking (Zookeeper / etcd / Consul).
- Implementing a basic Redis-backed distributed lock in Java.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - Why standard locks fail across physical boundaries.
2. [INTERNALS.md](./INTERNALS.md) - Deep dive into Redlock and Fencing Tokens.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Pure Java implementation of a distributed lock using Redis concepts.