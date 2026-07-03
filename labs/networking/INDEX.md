# Networking Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![gRPC](https://img.shields.io/badge/gRPC-244C5F?style=for-the-badge&logo=grpc&logoColor=white)
![GraphQL](https://img.shields.io/badge/GraphQL-E10098?style=for-the-badge&logo=graphql&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-9-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Intermediate_to_Expert-purple?style=for-the-badge)

**Master network communication patterns — from REST to gRPC, WebSocket to service mesh**

</div>

---

## Overview

The Networking Academy covers the full spectrum of communication protocols and patterns used in modern distributed systems. You will learn RESTful API design, GraphQL, gRPC, WebSocket, service mesh technologies, and the underlying network principles (HTTP, TCP/IP). All labs include Java implementations with Spring Boot and other frameworks.

---

## Curriculum Map

### Level 1: API Communication
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 01 | [HTTP & Networking Fundamentals](./01-http-networking/) | HTTP/1.1, HTTP/2, TCP/IP, DNS, status codes, headers | 3-4 hrs | Intermediate | — |
| 02 | [REST API Design](./02-rest-apis/) | Resource modeling, HATEOAS, versioning, pagination, OpenAPI | 4-5 hrs | Intermediate | [16-rest-apis](../../16-rest-apis/) |
| 03 | [GraphQL](./03-graphql/) | Schema, queries, mutations, subscriptions, DataLoader | 4-5 hrs | Advanced | [26-graphql](../../26-graphql/), [28-graphql](../../28-graphql/) |
| 04 | [gRPC](./04-grpc/) | Protocol Buffers, unary/server-streaming/client-streaming/bidirectional streaming | 4-5 hrs | Advanced | [29-grpc](../../29-grpc/) |

### Level 2: Real-Time & Event-Driven Networking
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 05 | [WebSocket](./05-websocket/) | STOMP, SockJS, pub/sub, point-to-point, Spring WebSocket | 3-4 hrs | Advanced | [25-websocket](../../25-websocket/) |
| 06 | [RSocket](./06-rsocket/) | Request/response, fire-and-forget, streaming, channel | 3-4 hrs | Advanced | [14-reactive-programming](../../14-reactive-programming/) |
| 07 | [Service Mesh](./07-service-mesh/) | Istio, sidecar proxy, traffic management, mTLS, circuit breaking | 4-5 hrs | Expert | [57-service-mesh](../../57-service-mesh/) |

### Level 3: Advanced Topics
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 08 | [API Gateway Patterns](./08-api-gateway/) | Spring Cloud Gateway, routing, rate limiting, filter chains | 3-4 hrs | Advanced | — |
| 09 | [Performance & Resilience](./09-performance-resilience/) | Load balancing, circuit breakers, retries, timeouts, backpressure | 4-5 hrs | Advanced | [10-cloud-native](../../10-cloud-native/), [14-reactive-programming](../../14-reactive-programming/) |

**Total estimated time: 32-41 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08 ──→ 09
HTTP    REST    GraphQL  gRPC    WS      RSocket  Mesh    Gateway  Perf &
                                                        Patterns  Resilience
```

Labs 01–02 cover foundational HTTP and REST. Labs 03–04 cover alternative API protocols. Labs 05–06 cover real-time communication. Labs 07–09 cover infrastructure-level networking.

---

## Prerequisites

- Java proficiency (Spring Boot experience helpful)
- Understanding of HTTP basics (methods, status codes)
- Basic knowledge of serialization (JSON, Protocol Buffers)
- Docker for service mesh labs

---

## How to Use This Academy

### For API Developers
Work through Labs 01–04 for a comprehensive understanding of API protocols.

### For Real-Time Engineers
Focus on Labs 05–06 for WebSocket and RSocket reactive communication.

### For Platform Engineers
Labs 07–09 are essential for service mesh, gateways, and resilient communication patterns.

---

## Related Academies

- [Backend Academy](../backend/) — Spring Boot, frameworks
- [Architecture Academy](../architecture/) — Microservices, event-driven
- [Distributed Systems Academy](../distributed-systems/) — Messaging, consensus
- [System Design Academy](../system-design/) — Scalability, caching, load balancing
- [Security Academy](../security/) — mTLS, OAuth2, API security

---

## Resources

### Official Documentation
- [gRPC Java Docs](https://grpc.io/docs/languages/java/)
- [GraphQL Java](https://www.graphql-java.com/)
- [Spring WebSocket](https://docs.spring.io/spring-framework/reference/web/websocket.html)
- [Istio Documentation](https://istio.io/latest/docs/)
- [RSocket.io](https://rsocket.io/)

### Books
- *RESTful Web APIs* — Leonard Richardson
- *GraphQL in Action* — Samer Buna
- *gRPC: Up and Running* — Kasun Indrasiri
- *Designing Web APIs* — Brenda Jin, Saurabh Sahni

### Tools
- [Postman](https://www.postman.com/)
- [grpcurl](https://github.com/fullstorydev/grpcurl)
- [GraphiQL](https://github.com/graphql/graphiql)
- [Wireshark](https://www.wireshark.org/)

---

<div align="center">

**Connect Everything. Build Everything.**

</div>
