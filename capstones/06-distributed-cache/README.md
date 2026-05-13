# Distributed Cache - Portfolio Capstone

## Overview
Redis-like distributed cache with cluster support, persistence, and advanced caching strategies.

## Architecture
```
┌─────────────────────────────────────────────────────────┐
│                    Cache Cluster                         │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐    │
│  │ Node 1  │  │ Node 2  │  │ Node 3  │  │ Node 4  │    │
│  │ (Slot   │  │ (Slot   │  │ (Slot   │  │ (Slot   │    │
│  │  0-16383│  │ 0-16383)│  │ 0-16383)│  │ 0-16383)│    │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘    │
│       │            │            │            │          │
│       └────────────┴────────────┴────────────┘          │
│                        │                                │
│              ┌─────────┴─────────┐                      │
│              │   Consistent      │                      │
│              │   Hash Ring       │                      │
│              └───────────────────┘                      │
└─────────────────────────────────────────────────────────┘
```

## Features
- Consistent hashing for data distribution
- Cluster mode with automatic failover
- AOF and RDB persistence
- TTL support
- Pub/Sub messaging
- Lua scripting
- Connection pooling

## Quick Start
```bash
cd 06-distributed-cache
docker-compose up -d
```