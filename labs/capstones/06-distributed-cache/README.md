# Capstone 06: Distributed Cache

Welcome to the **Distributed Cache Capstone Project**. This is a portfolio-grade project where you will build a functional, highly available distributed cache (similar to Redis or Memcached) from scratch in Java.

## 🎯 Project Objective
Build a distributed, in-memory key-value store capable of handling high throughput, data expiration, and node failures gracefully.

## 🧠 Core Architecture
- **Storage Engine**: In-memory `ConcurrentHashMap` with O(1) operations.
- **Eviction Policy**: Thread-safe LRU (Least Recently Used) eviction.
- **Data Distribution**: Consistent Hashing with virtual nodes.
- **Replication**: Master-less replication for high availability.
- **API**: TCP/IP socket server with a custom text-based protocol.

## 📂 Project Structure
1. [ARCHITECTURE.md](./ARCHITECTURE.md) - System design and component breakdown.
2. [IMPLEMENTATION_GUIDE.md](./IMPLEMENTATION_GUIDE.md) - Step-by-step guide to building the cache.