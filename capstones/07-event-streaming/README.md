# Event Streaming Platform - Portfolio Capstone

## Overview
Mini Kafka implementation with producer/consumer patterns, partition management, offset tracking, and consumer groups.

## Architecture
```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Producer    │────▶│   Topic      │────▶│  Consumer    │
│              │     │  (Partitioned│     │   Group      │
└──────────────┘     │   Log)       │     └──────────────┘
                     └──────────────┘            │
                          │                     │
       ┌──────────────────┼────────────────────┘
       │                  │
┌──────▼──────┐     ┌──────▼──────┐
│  Leader     │     │  Follower   │
│  Broker     │     │  Replicas   │
└─────────────┘     └─────────────┘
```

## Features
- Topic management with partitions
- Producer with batching and compression
- Consumer groups with partition assignment
- Offset management and replay
- Replication for fault tolerance
- Stream processing API

## Quick Start
```bash
cd 07-event-streaming
docker-compose up -d
```