# Backend Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-25-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Intermediate_to_Expert-purple?style=for-the-badge)

**Build production-grade backend services in Java — from REST APIs to reactive microservices**

</div>

---

## Overview

The Backend Academy covers the full backend engineering landscape in the Java ecosystem. You will learn multiple frameworks (Spring Boot, Micronaut, Quarkus, Helidon, Javalin, Vert.x), master REST API design, database access, messaging, and security. Each lab provides hands-on experience with production-ready patterns and practices.

---

## Curriculum Map

### Level 1: Spring Ecosystem
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 01 | [Spring Boot Basics](./01-spring-boot-basics/) | Auto-configuration, starters, actuators, embedded servers | 4-5 hrs | Intermediate |
| 02 | [REST APIs](./02-rest-apis/) | Controllers, request handling, validation, error handling | 4-5 hrs | Intermediate |
| 03 | [Spring MVC](./03-spring-mvc/) | Model-View-Controller, Thymeleaf, form handling, validation | 4-5 hrs | Intermediate |
| 04 | [Spring Data JPA](./04-spring-data-jpa/) | ORM, entities, relationships, JPQL, repositories | 5-6 hrs | Intermediate |
| 05 | [Transaction Management](./05-transaction-management/) | Declarative transactions, isolation levels, propagation | 4-5 hrs | Advanced |
| 06 | [Security Basics](./06-security-basics/) | Authentication, authorization, method security, login flows | 5-6 hrs | Advanced |

### Level 2: Alternative Frameworks
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 07 | [Messaging](./07-messaging/) | JMS, RabbitMQ, Kafka integration, async messaging | 4-5 hrs | Advanced |
| 08 | [Micronaut](./08-micronaut/) | Compile-time DI, GraalVM, reactive, AOT | 4-5 hrs | Advanced |
| 09 | [Helidon](./09-helidon/) | SE/MP, reactive streams, microprofile | 3-4 hrs | Advanced |
| 10 | [Quarkus](./10-quarkus/) | Live reload, native compilation, reactive | 4-5 hrs | Advanced |
| 11 | [Testing Strategies](./11-testing-strategies/) | Unit testing, integration testing, Testcontainers, Mockito | 4-5 hrs | Intermediate |

### Level 3: Integration & API Design
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 12 | [API Documentation](./12-api-documentation/) | OpenAPI, Swagger, API design, contract-first | 3-4 hrs | Intermediate |
| 13 | [Caching](./13-caching/) | Cache abstraction, Redis, Caffeine, cache strategies | 3-4 hrs | Intermediate |
| 14 | [Scheduling](./14-scheduling/) | @Scheduled, cron expressions, task execution, async | 3-4 hrs | Intermediate |

### Level 4: Advanced Backend Topics (Original)
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 15 | [Reactive Programming](./15-webflux-reactive/) | Project Reactor, WebFlux, RSocket | 5-6 hrs | Expert | [14-reactive-programming](../../14-reactive-programming/) |

### Level 5: Advanced Backend Topics (Expanded)
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 16 | [Spring Cloud Infrastructure](./16-spring-cloud/) | Eureka, Config Server, Load Balancer, Circuit Breakers (Resilience4J) | 5-6 hrs | Expert | |
| 17 | [API Versioning & Documentation](./17-api-versioning-documentation/) | OpenAPI 3.0, SpringDoc, versioning strategies, API contracts | 4-5 hrs | Advanced | |
| 18 | [File Processing with Spring Batch](./18-file-processing-batch/) | Spring Batch, chunk processing, readers/writers, schedulers | 5-6 hrs | Advanced | |
| 19 | [Server-Sent Events](./19-server-sent-events/) | SSE protocol, event streams, reactive SSE, WebFlux SSE | 3-4 hrs | Intermediate | |
| 20 | [Backend Security Deep Dive](./20-backend-security-deep/) | CSRF, CORS, rate limiting, input validation, SQL injection prevention | 5-6 hrs | Advanced | |
| 21 | [Multi-Tenancy Patterns](./21-multi-tenancy/) | Schema-per-tenant, database-per-tenant, discriminator column, SaaS | 4-5 hrs | Advanced | |
| 22 | [GraphQL with Netflix DGS](./22-graphql-dgs/) | DGS framework, schema-first, data loaders, federated GraphQL | 5-6 hrs | Advanced | |
| 23 | [CQRS with Axon Framework](./23-cqrs-axon/) | Axon Framework, command/query buses, event sourcing, sagas | 6-7 hrs | Expert | |
| 24 | [Backend Performance Optimization](./24-backend-performance/) | Profiling, JMH benchmarks, connection pooling, async I/O, caching | 5-6 hrs | Advanced | |
| 25 | [GraalVM Native Images](./25-graalvm-native/) | Native images, reflection config, resource bundles, AOT compilation | 4-5 hrs | Advanced | [24-graalvm-native](../../24-graalvm-native/) |

**Total estimated time: 100-130 hours**

---

## Learning Path

```
 01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06         07 ──→ 08 ──→ 09 ──→ 10 ──→ 11
Core   Boot    REST    JPA     DB      Security  Micro   Quarkus Helid  Javalin Vert.x
                                                        Naut
         12 ──→ 13 ──→ 14 ──→ 15 ──→ 16 ──→ 17 ──→ 18 ──→ 19 ──→ 20
        Kafka   RMQ     Camel   Reactv  Spring  API     Batch   SSE     Security
                                       Cloud   Docs
         21 ──→ 22 ──→ 23 ──→ 24 ──→ 25
        Multi   GraphQL CQRS   Perf    GraalVM
        Tenancy
```

Start with the Spring path (01–06), then explore alternative frameworks (07–11) and messaging (12–14). Labs 15–25 are advanced standalone topics covering cloud infrastructure, security, performance, and modern patterns.

---

## Prerequisites

- Strong Java fundamentals (OOP, collections, streams, concurrency)
- Understanding of HTTP, REST, and JSON
- Basic SQL knowledge
- Familiarity with Maven or Gradle

---

## How to Use This Academy

### For Spring Developers
Work Labs 01–06 first, then explore alternative frameworks in Labs 07–11.

### For Framework Evaluators
Compare Labs 01–03 (Spring) with Labs 07–11 (alternatives) to understand trade-offs.

### For Integration Specialists
Focus on Labs 12–14 for messaging and enterprise integration patterns.

### Recommended Pace
- **Part-time**: 1 lab per week
- **Full-time**: 2-3 labs per week
- **Intensive**: 1 lab per day

---

## Related Academies

- [Architecture Academy](../architecture/) — Patterns, DDD, hexagonal architecture
- [Databases Academy](../databases/) — PostgreSQL, MongoDB, Redis, Flyway
- [Security Academy](../security/) — OAuth2, Keycloak, encryption
- [DevOps Academy](../devops/) — Docker, K8s, CI/CD, monitoring
- [Distributed Systems Academy](../distributed-systems/) — Kafka, consensus, replication

---

## Resources

### Official Documentation
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Micronaut Docs](https://docs.micronaut.io/latest/guide/)
- [Quarkus Guide](https://quarkus.io/guides/)
- [Helidon Docs](https://helidon.io/docs/latest/)
- [Vert.x Docs](https://vertx.io/docs/)

### Books
- *Spring in Action* — Craig Walls
- *Reactive Spring* — Josh Long
- *Cloud Native Java* — Josh Long, Kenny Bastani
- *Building Microservices with Spring Boot* — Chris Richardson

### Tools
- [Spring Initializr](https://start.spring.io/)
- [Micronaut Launch](https://micronaut.io/launch/)
- [Quarkus Code](https://code.quarkus.io/)

---

<div align="center">

**Build Backends. Build Everything.**

</div>
