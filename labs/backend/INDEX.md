# Backend Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-16-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Intermediate_to_Expert-purple?style=for-the-badge)

**Build production-grade backend services in Java — from REST APIs to reactive microservices**

</div>

---

## Overview

The Backend Academy covers the full backend engineering landscape in the Java ecosystem. You will learn multiple frameworks (Spring Boot, Micronaut, Quarkus, Helidon, Javalin, Vert.x), master REST API design, database access, messaging, and security. Each lab provides hands-on experience with production-ready patterns and practices.

---

## Curriculum Map

### Level 1: Spring Ecosystem
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 01 | [Spring Core & DI](./01-spring-core/) | IoC container, beans, configuration, profiles | 4-5 hrs | Intermediate | [14-spring-core](../../14-spring-core/) |
| 02 | [Spring Boot Fundamentals](./02-spring-boot/) | Auto-configuration, starters, actuators, CLI | 4-5 hrs | Intermediate | [15-spring-boot](../../15-spring-boot/), [02-spring-boot](../../02-spring-boot/) |
| 03 | [REST API Development](./03-rest-apis/) | Controllers, request handling, validation, error handling | 4-5 hrs | Intermediate | [16-rest-apis](../../16-rest-apis/) |
| 04 | [JPA & Hibernate](./04-jpa-hibernate/) | ORM, entities, relationships, JPQL, caching | 5-6 hrs | Intermediate | [17-jpa-hibernate](../../17-jpa-hibernate/) |
| 05 | [Database Access (JDBC, R2DBC, Spring Data)](./05-database-access/) | DataSources, transactions, reactive DB access | 4-5 hrs | Advanced | [18-database-access](../../18-database-access/), [69-r2dbc](../../69-r2dbc/) |
| 06 | [Spring Security & OAuth2](./06-spring-security/) | Authentication, authorization, JWT, OAuth2, method security | 5-6 hrs | Advanced | [20-spring-security](../../20-spring-security/) |

### Level 2: Alternative Frameworks
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 07 | [Micronaut](./07-micronaut/) | Compile-time DI, GraalVM, reactive, AOT | 4-5 hrs | Advanced | [52-micronaut](../../52-micronaut/) |
| 08 | [Quarkus](./08-quarkus/) | Live reload, native compilation, reactive | 4-5 hrs | Advanced | [51-quarkus](../../51-quarkus/) |
| 09 | [Helidon](./09-helidon/) | SE/MP, reactive streams, microprofile | 3-4 hrs | Advanced | [53-helidon](../../53-helidon/) |
| 10 | [Javalin](./10-javalin/) | Lightweight, Kotlin-friendly, embedded server | 2-3 hrs | Intermediate | [19-javalin](../../19-javalin/) |
| 11 | [Vert.x](./11-vertx/) | Event loop, reactive, polyglot, Netty | 4-5 hrs | Advanced | [54-vertx](../../54-vertx/) |

### Level 3: Messaging & Integration
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 12 | [Messaging with Kafka](./12-messaging-kafka/) | Producers, consumers, streams, exactly-once | 5-6 hrs | Advanced | [08-messaging](../../08-messaging/), [55-kafka](../../55-kafka/) |
| 13 | [Messaging with RabbitMQ](./13-messaging-rabbitmq/) | Exchanges, queues, bindings, RPC | 3-4 hrs | Intermediate | [08-messaging](../../08-messaging/), [34-rabbitmq](../../34-rabbitmq/) |
| 14 | [Apache Camel Integration](./14-apache-camel/) | Routes, EIP, transformations, connectors | 4-5 hrs | Advanced | [16-apache-camel](../../16-apache-camel/) |

### Level 4: Advanced Backend Topics
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 15 | [Reactive Programming](./15-reactive-programming/) | Project Reactor, WebFlux, RSocket | 5-6 hrs | Expert | [14-reactive-programming](../../14-reactive-programming/) |
| 16 | [GraalVM Native Images](./16-graalvm-native/) | AOT compilation, native executables, reflection config | 3-4 hrs | Advanced | [24-graalvm-native](../../24-graalvm-native/) |

**Total estimated time: 63-78 hours**

---

## Learning Path

```
 01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06         07 ──→ 08 ──→ 09 ──→ 10 ──→ 11
Core   Boot    REST    JPA     DB      Security  Micro   Quarkus Helid  Javalin Vert.x
                                                        Naut
         12 ──→ 13 ──→ 14 ──→ 15 ──→ 16
        Kafka   RMQ     Camel   Reactv  GraalVM
```

Start with the Spring path (01–06), then explore alternative frameworks (07–11) and messaging (12–14). Labs 15–16 are advanced standalone topics.

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
