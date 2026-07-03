# Databases Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-12-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Intermediate_to_Expert-purple?style=for-the-badge)

**Master relational and NoSQL databases — from JDBC to reactive data access**

</div>

---

## Overview

The Databases Academy provides a comprehensive curriculum covering SQL and NoSQL databases, data access patterns, and migration strategies in the Java ecosystem. Learn PostgreSQL, MongoDB, Redis, JPA/Hibernate, JDBC, R2DBC, and database migration tools. Each lab combines theory with practical Java implementations.

---

## Curriculum Map

### Level 1: Relational Databases
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 01 | [PostgreSQL Fundamentals](./01-postgresql/) | SQL, schema design, indexes, transactions, CTEs | 4-5 hrs | Intermediate | [33-postgresql](../../33-postgresql/), [07-databases](../../07-databases/) |
| 02 | [JDBC & Connection Pooling](./02-jdbc/) | DataSource, connections, HikariCP, prepared statements, batch | 3-4 hrs | Intermediate | [18-database-access](../../18-database-access/) |
| 03 | [JPA & Hibernate](./03-jpa-hibernate/) | Entities, relationships, JPQL, Criteria API, N+1 problem | 5-6 hrs | Intermediate | [17-jpa-hibernate](../../17-jpa-hibernate/) |
| 04 | [Flyway Migrations](./04-flyway/) | Versioned migrations, repeatable scripts, rollbacks | 2-3 hrs | Intermediate | [46-flyway](../../46-flyway/) |
| 05 | [Liquibase Migrations](./05-liquibase/) | Changelog files, changesets, rollbacks, diff | 2-3 hrs | Intermediate | [47-liquibase](../../47-liquibase/) |

### Level 2: NoSQL Databases
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 06 | [MongoDB - Document Store](./06-mongodb/) | Documents, collections, aggregation pipeline, indexes | 4-5 hrs | Intermediate | [23-mongodb](../../23-mongodb/), [31-mongodb](../../31-mongodb/) |
| 07 | [Redis - In-Memory Store](./07-redis/) | Strings, hashes, lists, sets, sorted sets, pub/sub | 3-4 hrs | Intermediate | [24-redis](../../24-redis/), [32-redis](../../32-redis/) |
| 08 | [Redis - Advanced Patterns](./08-redis-advanced/) | Caching, rate limiting, leaderboards, distributed locks, Redis Streams | 4-5 hrs | Advanced | [24-redis](../../24-redis/), [32-redis](../../32-redis/) |

### Level 3: Advanced Data Access
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 09 | [R2DBC - Reactive Data Access](./09-r2dbc/) | Reactive CRUD, transactions, Spring Data R2DBC | 4-5 hrs | Advanced | [69-r2dbc](../../69-r2dbc/) |
| 10 | [Spring Data JPA Advanced](./10-spring-data-jpa/) | Auditing, projections, specifications, query by example | 3-4 hrs | Advanced | [17-jpa-hibernate](../../17-jpa-hibernate/) |
| 11 | [Elasticsearch](./11-elasticsearch/) | Full-text search, indexing, aggregations, Spring Data ES | 4-5 hrs | Advanced | [36-elasticsearch](../../36-elasticsearch/) |
| 12 | [Vector Databases](./12-vector-databases/) | Embedding storage, similarity search, pgvector | 4-5 hrs | Advanced | [77-vector-database](../../77-vector-database/) |

**Total estimated time: 42-54 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08 ──→ 09 ──→ 10 ──→ 11 ──→ 12
PG    JDBC    JPA/H   Flyway  Liqui   Mongo   Redis   Redis   R2DBC  Data    ES    Vector
                                    DB              Adv            JPA                  DB
```

Labs 01–05 cover relational databases and migration. Labs 06–08 cover NoSQL. Labs 09–12 cover advanced access patterns.

---

## Prerequisites

- Java proficiency (JDBC basics, streams)
- Understanding of SQL fundamentals (SELECT, JOIN, GROUP BY)
- Familiarity with Spring Boot basics
- Docker Desktop for running database instances

---

## How to Use This Academy

### For Backend Developers
Work Labs 01–05 for core relational database skills, then Labs 06–08 for NoSQL.

### For Data Engineers
Pay special attention to Labs 01, 04, 05, 09, and 12 for data pipeline integration.

### For Full-Stack Developers
Labs 03 and 06–08 cover the most common Java database scenarios.

---

## Related Academies

- [Backend Academy](../backend/) — Spring Boot, REST APIs, frameworks
- [Data Engineering Academy](../data-engineering/) — ETL, Spark, streaming
- [DevOps Academy](../devops/) — Docker, monitoring, observability
- [System Design Academy](../system-design/) — Caching, sharding, replication

---

## Resources

### Official Documentation
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [MongoDB Docs](https://www.mongodb.com/docs/)
- [Redis Docs](https://redis.io/docs/)
- [Hibernate ORM](https://hibernate.org/orm/documentation/)
- [Flyway Docs](https://documentation.red-gate.com/flyway/)
- [Liquibase Docs](https://docs.liquibase.com/)

### Books
- *High Performance MySQL* — Baron Schwartz
- *MongoDB: The Definitive Guide* — Shannon Bradshaw
- *Redis in Action* — Josiah L. Carlson
- *Java Persistence with Hibernate* — Christian Bauer

---

<div align="center">

**Master Data. Build Everything.**

</div>
