# Caching - WHY IT EXISTS

## Problem Statement
Databases are slow. A typical PostgreSQL query takes 10-100ms. Under high load, even optimized queries cause bottlenecks. Network latency, disk I/O, and contention multiply these delays.

## Origin
Caching is as old as computing itself. CPU caches (L1/L2/L3) reduce memory access latency from ~100ns to ~1ns. Memcached (2003) and Redis (2009) popularized distributed in-memory caching for web applications.

## Core Drivers
- **Latency reduction**: Cache hits serve in < 1ms vs 10-100ms from database
- **Throughput increase**: Cache handles 100K+ QPS on a single node
- **Cost savings**: Reduce database instances (cache is cheaper per QPS)
- **Database protection**: Absorb traffic spikes, prevent DB overload

## Why Not Just Use a Faster Database?
- Even the fastest NVMe disk is 100x slower than RAM
- Database connection pools have hard limits
- Many queries are repetitive (same data, same user, same page)
- Caching reduces load on primary data store

## Java Ecosystem
- **Spring Cache**: Declarative caching with @Cacheable, @CacheEvict
- **Redis/Jedis/Lettuce**: Distributed cache clients
- **Caffeine**: High-performance local cache
- **Hazelcast**: Distributed in-memory data grid
- **Ehcache**: Local caching with optional clustering
