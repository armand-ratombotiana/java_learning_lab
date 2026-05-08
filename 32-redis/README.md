# 32 - Redis Learning Module

## Overview
Redis is an in-memory data structure store used as database, cache, and message broker. This module covers Redis caching patterns with Spring Boot.

## Module Structure
- `redis-cache/` - Spring Data Redis implementation

## Technology Stack
- Spring Boot 3.x
- Spring Data Redis
- Lettuce client
- Maven

## Prerequisites
- Redis server running on `localhost:6379`

## Key Features
- In-memory storage with optional persistence
- Rich data structures (strings, hashes, lists, sets, sorted sets)
- Cache-aside pattern
- Pub/Sub messaging
- Redis Sentinel for high availability

## Build & Run
```bash
cd redis-cache
mvn clean install
mvn spring-boot:run
```

## Default Configuration
- Host: `localhost`
- Port: `6379`
- Database: `0`

## Common Use Cases
- Session caching
- API response caching
- Distributed locks
- Rate limiting
- Real-time leaderboards

## Related Modules
- 31-mongodb (NoSQL comparison)
- 34-rabbitmq (messaging comparison)