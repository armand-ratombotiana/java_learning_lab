# Architecture Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-20-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Intermediate_to_Expert-purple?style=for-the-badge)

**Master software architecture patterns — from layered to hexagonal, monoliths to microservices, service mesh to platform engineering**

</div>

---

## Overview

The Architecture Academy provides a comprehensive curriculum covering the full spectrum of software architecture patterns and paradigms. You will learn how to design systems that are maintainable, scalable, testable, and aligned with business goals. Each lab combines theory with hands-on Java implementation to build practical architectural skills.

---

## Curriculum Map

### Level 1: Foundational Architecture
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 01 | [Layered Architecture](./01-layered-architecture/) | Traditional N-tier, presentation/domain/data layers | 2-3 hrs | Intermediate | — |
| 02 | [Clean Architecture](./02-clean-architecture/) | Dependency rule, entities, use cases, adapters | 3-4 hrs | Intermediate | — |
| 03 | [Hexagonal Architecture (Ports & Adapters)](./03-hexagonal-architecture/) | Core domain, ports, adapters, dependency inversion | 4-5 hrs | Advanced | [26-architecture](../../26-architecture/) |
| 04 | [Domain-Driven Design](./04-domain-driven-design/) | Ubiquitous language, aggregates, bounded contexts, events | 5-6 hrs | Advanced | [65-ddd](../../65-ddd/) |

### Level 2: Distributed & Event-Driven Architecture
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 05 | [Event-Driven Architecture](./05-event-driven-architecture/) | Events, commands, event bus, async processing | 4-5 hrs | Advanced | [30-event-sourcing](../../30-event-sourcing/) |
| 06 | [CQRS](./06-cqrs/) | Command/query separation, read/write models, eventual consistency | 4-5 hrs | Advanced | [66-cqrs](../../66-cqrs/) |
| 07 | [Event Sourcing](./07-event-sourcing/) | Event store, replay, snapshots, projection | 4-5 hrs | Advanced | [30-event-sourcing](../../30-event-sourcing/) |
| 08 | [Microservices Architecture](./08-microservices/) | Service decomposition, communication, data management | 5-6 hrs | Advanced | [19-microservices](../../19-microservices/), [06-microservices](../../06-microservices/) |

### Level 3: Advanced Topics
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 09 | [Saga Pattern](./09-saga-pattern/) | Choreography & orchestration, compensating transactions | 4-5 hrs | Expert | [67-saga](../../67-saga/) |
| 10 | [Modulith & Modular Monoliths](./10-modulith/) | Spring Modulith, package structure, module boundaries | 3-4 hrs | Advanced | [68-springmodulith](../../68-springmodulith/) |

### Level 4: Advanced Architecture Patterns
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 11 | [Strangler Fig Pattern](./11-strangle-fig-pattern/) | Incremental migration, feature toggles, branch by abstraction | 4-5 hrs | Expert | — |
| 12 | [Backend for Frontend](./12-bff-pattern/) | BFF per client type, GraphQL BFF, aggregation | 3-4 hrs | Advanced | — |
| 13 | [Sidecar & Ambassador](./13-sidecar-ambassador/) | Sidecar pattern, ambassador, Envoy proxy | 4-5 hrs | Expert | — |
| 14 | [Circuit Breaker](./14-circuit-breaker-pattern/) | CB states, bulkhead, Resilience4J | 4-5 hrs | Expert | — |
| 15 | [Saga Orchestration](./15-saga-orchestration/) | Choreography vs orchestration, compensation, Axon, Camunda | 4-5 hrs | Expert | — |
| 16 | [API Composition](./16-api-composition/) | Gateway composition, GraphQL federation, aggregate services | 3-4 hrs | Advanced | — |
| 17 | [Advanced Strangler Fig](./17-strangler-fig-advanced/) | Decomposition, DB splitting, async migration | 4-5 hrs | Expert | — |
| 18 | [Event-Driven Deep Dive](./18-event-driven-architecture/) | Event sourcing, event store patterns, projections | 4-5 hrs | Advanced | — |
| 19 | [Service Mesh](./19-service-mesh-architecture/) | Istio, Linkerd, control plane, data plane, mTLS | 4-5 hrs | Expert | — |
| 20 | [Platform Engineering](./20-platform-engineering/) | IDPs, Backstage, golden paths, self-service | 4-5 hrs | Expert | — |

**Total estimated time: 79-98 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08 ──→ 09 ──→ 10 ──→ 11 ──→ 12 ──→ 13 ──→ 14 ──→ 15 ──→ 16 ──→ 17 ──→ 18 ──→ 19 ──→ 20
Layer   Clean   Hexag    DDD    Event    CQRS   Event   Micro   Saga  Modulith Strangler BFF  Sidecar  CB   Saga   API   Adv    EDA  Mesh  Plat
                                Driven          Source  svcs                    Fig                      Orch  Comp  Strang       Eng
```

Labs 01–03 build foundational architectural thinking. Labs 04–08 introduce DDD and distributed patterns. Labs 09–10 cover advanced coordination and modularity. Labs 11–20 cover advanced architecture patterns including migration, resilience, and platform engineering.

---

## Prerequisites

- Solid Java proficiency (collections, streams, concurrency)
- Familiarity with Spring Boot basics
- Understanding of OOP, SOLID principles, and design patterns
- Basic knowledge of REST APIs and HTTP
- Familiarity with databases and transaction concepts

---

## How to Use This Academy

### For Intermediate Developers
Work through Labs 01–04 sequentially to build foundational architecture skills.

### For Advanced Developers
Start at Lab 04 (DDD) or Lab 05 (Event-Driven) and use earlier labs as reference.

### For Architects
Focus on Labs 03–20 for comprehensive modern distributed architecture pattern coverage.

### Lab Structure
Each lab contains:
- `README.md` — Overview, learning objectives, prerequisites
- `THEORY.md` — Comprehensive theoretical foundation
- `HOW_IT_WORKS.md` — Step-by-step mechanical explanation
- `CODE_DEEP_DIVE.md` — Detailed code walkthroughs
- `EXERCISES.md` — Practice problems with solutions
- `MINI_PROJECT/` — Small hands-on project
- `REAL_WORLD_PROJECT/` — Production-scale project
- `QUIZ.md` — Self-assessment questions
- `FLASHCARDS.md` — Spaced-repetition learning cards
- `REFERENCES.md` — Further reading and resources

---

## Related Academies

- [Backend Academy](../backend/) — Spring Boot, REST APIs, frameworks
- [Distributed Systems Academy](../distributed-systems/) — Kafka, consensus, replication
- [System Design Academy](../system-design/) — Scalability, caching, load balancing
- [Databases Academy](../databases/) — SQL, NoSQL, data modeling
- [DevOps Academy](../devops/) — CI/CD, deployment, monitoring
- [Domain-Driven Design](../ddd/) — Cross-cutting DDD reference

---

## Resources

### Books
- *Clean Architecture* — Robert C. Martin
- *Domain-Driven Design* — Eric Evans
- *Implementing Domain-Driven Design* — Vaughn Vernon
- *Building Microservices* — Sam Newman
- *Designing Data-Intensive Applications* — Martin Kleppmann

### Online
- [Microsoft Architecture Guidance](https://docs.microsoft.com/en-us/azure/architecture/)
- [Heroku 12-Factor App](https://12factor.net/)
- [Cloud Design Patterns](https://docs.microsoft.com/en-us/azure/architecture/patterns/)
- [Microservices.io](https://microservices.io/) — Chris Richardson

### Tools
- [Spring Modulith](https://spring.io/projects/spring-modulith)
- [Axon Framework](https://axoniq.io/)
- [Eventuate Tram](https://eventuate.io/)

---

<div align="center">

**Design Great Systems. Build Everything.**

</div>
