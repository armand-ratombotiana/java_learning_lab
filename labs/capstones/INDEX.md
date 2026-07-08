# 🎓 Capstone Projects Academy

![Status](https://img.shields.io/badge/status-active-success.svg)
![Labs](https://img.shields.io/badge/capstones-5-blue)
![Difficulty](https://img.shields.io/badge/difficulty-advanced-red)
![Java](https://img.shields.io/badge/Java-21+-red)
![Scope](https://img.shields.io/badge/scope-portfolio--grade-brightgreen)

## Overview

The Capstone Projects Academy provides portfolio-grade projects that integrate concepts from the entire learning curriculum. Each capstone is a comprehensive, multi-week project requiring you to design, implement, test, and deploy a complete distributed system. These projects demonstrate mastery of system design, distributed systems, databases, cloud infrastructure, and software engineering best practices.

## Capstone Projects

| # | Capstone | Topic | Duration | Difficulty |
|---|----------|-------|----------|------------|
| 01 | [ecommerce-platform](./01-ecommerce-platform) | Full e-commerce with orders, payments, inventory, recommendations, search, user management | 40h | ★★★★ |
| 02 | [real-time-analytics](./02-real-time-analytics) | Streaming analytics with Kafka, Flink, dashboards, alerting, data pipelines | 40h | ★★★★ |
| 03 | [microservices-migration](./03-microservices-migration) | Monolith to microservices with strangler fig, CQRS, event sourcing, API gateways | 45h | ★★★★★ |
| 04 | [vector-database](./04-vector-database) | Vector storage, HNSW indexing, ANN search, distance metrics, REST/gRPC API | 35h | ★★★★ |
| 05 | [ml-platform](./05-ml-platform) | ML training/inference platform, feature store, model registry, A/B testing, pipelines | 45h | ★★★★★ |

**Total estimated time: 205 hours**

## How to Use

Each capstone contains:
- **THEORY.md** — In-depth concept explanations with diagrams
- **MATH_FOUNDATION.md** — Mathematical concepts and formulas
- **CODE_DEEP_DIVE.md** — Annotated Java implementation walkthroughs
- **ARCHITECTURE.md** — System design and component architecture
- **SECURITY.md** — Security considerations and implementations
- **PERFORMANCE.md** — Performance analysis and optimization
- **REFACTORING.md** — Code refactoring patterns
- **DEBUGGING.md** — Debugging strategies and tools
- **COMMON_MISTAKES.md** — Frequent pitfalls and solutions
- **STEP_BY_STEP.md** — Step-by-step implementation guide
- **VISUAL_GUIDE.md** — Visual diagrams and explanations
- **INTERNALS.md** — Internal workings and mechanisms
- **HOW_IT_WORKS.md** — High-level operational explanation
- **MENTAL_MODELS.md** — Conceptual models for understanding
- **HISTORY.md** — Historical development and context
- **WHY_IT_MATTERS.md** — Importance and real-world impact
- **WHY_IT_EXISTS.md** — Problem domain and motivation
- **REFERENCES.md** — External references and resources
- **REFLECTION.md** — Self-assessment and reflection prompts
- **INTERVIEW.md** — Interview questions and answers
- **FLASHCARDS.md** — Key concept review cards
- **EXERCISES.md** — Practice exercises with solutions
- **QUIZ.md** — Knowledge assessment questions
- **README.md** — Project overview and getting started guide

Additional subdirectories: `BENCHMARK/`, `CHALLENGE/`, `DIAGRAMS/`, `MINI_PROJECT/`, `REAL_WORLD_PROJECT/`, `SOLUTION/`, `TESTS/`

## Prerequisites

- Completion of all System Design Academy labs (01–15)
- Java 21+ proficiency
- Experience with Spring Boot, REST APIs, and database design
- Familiarity with Docker and containerization
- Understanding of cloud computing concepts (AWS, GCP, or Azure)
- Knowledge of CI/CD pipelines and DevOps practices

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05
Ecom   Analytics  Migrate   Vector   ML
```

Capstones increase in complexity. Start with 01 (e-commerce) or 04 (vector database) for a well-scoped project. Tackle 02 (analytics) and 03 (migration) for distributed systems depth. Finish with 05 (ML platform) for the most comprehensive challenge.

## Related Academies

- [System Design Academy](../system-design) — Foundation for all capstones
- [Cloud Academy](../cloud) — AWS, Docker, Kubernetes, Terraform
- [Distributed Systems Academy](../distributed-systems) — Consensus, replication, distributed storage
- [Databases Academy](../databases) — SQL, NoSQL, sharding, replication
- [Data Engineering Academy](../data-engineering) — ETL, streaming, data pipelines
- [DevOps Academy](../devops) — CI/CD, monitoring, infrastructure as code

## Resources

- [Designing Data-Intensive Applications](https://dataintensive.net) — Martin Kleppmann
- [Building Microservices](https://samnewman.io/books/building-microservices/) — Sam Newman
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) — Robert C. Martin
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [Awesome Distributed Systems](https://github.com/theanalyst/awesome-distributed-systems)
