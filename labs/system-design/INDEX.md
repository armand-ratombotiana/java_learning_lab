# 🏗️ System Design Academy

![Status](https://img.shields.io/badge/status-active-success.svg)
![Labs](https://img.shields.io/badge/labs-15-blue)
![Difficulty](https://img.shields.io/badge/difficulty-intermediate--advanced-orange)
![Java](https://img.shields.io/badge/Java-21+-red)
![Distributed](https://img.shields.io/badge/distributed-systems-brightgreen)

## Overview

The System Design Academy teaches the principles and patterns for building scalable, reliable, and maintainable distributed systems. Topics span architecture patterns, scalability, availability, consistency models, caching, messaging, API design, observability, and advanced applied system design. Each lab combines theoretical depth with Java-based implementations — you will build load balancers, circuit breakers, distributed caches, message queues, monitoring systems, distributed databases, collaboration engines, rate limiters, URL shorteners, chat systems, payment engines, and video streaming platforms from scratch.

## Curriculum

### Level 1: Foundation (Labs 01–08)

| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 01 | [architecture-patterns](./01-architecture-patterns) | Layered, microservices, event-driven, CQRS, hexagonal, decision framework | 4h | ★★☆ |
| 02 | [scalability](./02-scalability) | Vertical/horizontal scaling, load balancing, sharding, auto-scaling | 4h | ★★☆ |
| 03 | [availability](./03-availability) | SLA/SLO, redundancy, failover, disaster recovery, HA patterns | 4h | ★★☆ |
| 04 | [consistency-models](./04-consistency-models) | CAP theorem, ACID vs BASE, strong/eventual/causal consistency, consensus | 5h | ★★★ |
| 05 | [caching](./05-caching) | Cache-aside, read/write-through, CDN, Redis, eviction policies | 3h | ★★☆ |
| 06 | [messaging](./06-messaging) | Point-to-point, pub-sub, event sourcing, Kafka, RabbitMQ patterns | 4h | ★★★ |
| 07 | [api-design](./07-api-design) | REST, GraphQL, gRPC, versioning, HATEOAS, OpenAPI, rate limiting | 3h | ★★☆ |
| 08 | [observability](./08-observability) | Logs, metrics, tracing, OpenTelemetry, health checks, alerting | 4h | ★★★ |

### Level 2: Advanced System Design (Labs 09–15)

| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 09 | [distributed-database-design](./09-distributed-database-design) | Sharding, replication, consistent hashing, read replicas, quorum | 6h | ★★★ |
| 10 | [real-time-collaboration](./10-real-time-collaboration) | CRDT, OT, WebSocket sync, conflict resolution, operational transforms | 6h | ★★★ |
| 11 | [rate-limiting-design](./11-rate-limiting-design) | Token bucket, leaky bucket, sliding window, distributed rate limiting | 5h | ★★★ |
| 12 | [design-url-shortener](./12-design-url-shortener) | Base62 encoding, key generation, redirection, analytics, caching | 5h | ★★★ |
| 13 | [design-chat-system](./13-design-chat-system) | WebSocket management, message ordering, presence, history, scaling | 6h | ★★★ |
| 14 | [design-payment-system](./14-design-payment-system) | Idempotency, ledger, double-entry, reconciliation, fraud detection | 7h | ★★★★ |
| 15 | [design-video-streaming](./15-design-video-streaming) | CDN, adaptive bitrate, DASH/HLS, caching, transcoding pipelines | 6h | ★★★ |

**Total estimated time: 72 hours**

## How to Use

Each lab contains:
- **THEORY.md** — In-depth concept explanations with diagrams
- **THEORY/** — Supplementary theory resources
- **CODE_DEEP_DIVE.md** — Annotated Java implementation walkthroughs
- **CODE_DEEP_DIVE/** — Supplementary code resources
- **EXERCISES.md** — Practice problems with solutions
- **EXERCISES/** — Exercise resource files
- **MINI_PROJECT.md** — Guided hands-on project
- **MINI_PROJECT/** — Project starter code
- **REAL_WORLD_PROJECT.md** — Full production scenario
- **REAL_WORLD_PROJECT/** — Project resource files
- **QUIZ.md** — Knowledge check questions
- **QUIZ/** — Quiz resource files
- **FLASHCARDS.md** — Spaced-repetition review cards
- **FLASHCARDS/** — Flashcard resource files

Work through labs in order. Each lab builds on concepts from previous ones. Read THEORY.md first, study CODE_DEEP_DIVE.md, complete EXERCISES.md, then build both MINI_PROJECT and REAL_WORLD_PROJECT.

## Prerequisites

- Java 21+
- Understanding of HTTP, TCP/IP, and REST APIs
- Basic knowledge of databases (SQL and NoSQL)
- Familiarity with distributed systems concepts (nodes, networks, consensus)
- Experience with multithreading and concurrent programming in Java
- Completion of Level 1 (Labs 01–08) before starting Level 2 (Labs 09–15)

## Learning Path

```
Level 1 (Foundation)
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08
Arch    Scale   Avail   Consist  Cache   Msg     API     Observ

Level 2 (Advanced)
09 ──→ 10 ──→ 11 ──→ 12 ──→ 13 ──→ 14 ──→ 15
DistDB  Collab  RateL   URL     Chat    Pay     Stream
```

**Level 1 (Foundation):** Labs 01–04 cover foundational architecture and distributed systems theory. Labs 05–07 focus on specific subsystems (caching, messaging, APIs). Lab 08 ties everything together with observability.

**Level 2 (Advanced):** Labs 09–15 apply foundation concepts to build real-world distributed systems. Each lab focuses on a specific production-grade system (database, collaboration, rate limiting, URL shortening, chat, payments, streaming) with complete Java implementations.

## Related Academies

- [Cloud Academy](../cloud) — AWS, Docker, Kubernetes, Terraform
- [Distributed Systems Academy](../distributed-systems) — Consensus, replication, distributed storage
- [Databases Academy](../databases) — SQL, NoSQL, sharding, replication
- [Networking Academy](../networking) — TCP/IP, DNS, load balancing, CDN
- [Security Academy](../security) — Authentication, authorization, encryption

## Resources

- [Designing Data-Intensive Applications](https://dataintensive.net) — Martin Kleppmann
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [OpenTelemetry](https://opentelemetry.io)
- [gRPC](https://grpc.io)
- [Apache Kafka](https://kafka.apache.org)
- [Redis](https://redis.io)
- [Awesome System Design](https://github.com/madd86/awesome-system-design)
