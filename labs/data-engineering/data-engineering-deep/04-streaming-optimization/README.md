# Lab 04: Streaming Optimization with Kafka Streams

## Overview

Optimize Kafka Streams applications for throughput, exactly-once semantics, and intelligent partitioning strategies.

## Learning Objectives

- Configure exactly-once semantics in Kafka Streams
- Design optimal partitioning strategies for skewed data
- Tune Kafka Streams performance (buffering, threading, caching)
- Implement idempotent producers and transactional consumers

## Key Concepts

- **Exactly-Once Semantics (EOS)**: idempotent producer + transactions
- **Partitioning**: Key-based, custom partitioners, copartitioning
- **Rack Awareness**: Minimize cross-rack traffic
- **Rebalancing**: Cooperative vs eager rebalancing
- **Punctuator**: Wall-clock time punctuations for windowing
