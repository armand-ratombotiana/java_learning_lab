# Databases Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-20-blue?style=for-the-badge)
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

### Level 4: Advanced Database Topics
| # | Lab | Topic | Duration | Difficulty | Module Reference |
|---|-----|-------|----------|------------|-----------------|
| 13 | [Database Sharding](./13-database-sharding/) | Horizontal sharding, consistent hashing, range vs hash, rebalancing | 5-6 hrs | Expert | [78-sharding](../../78-sharding/) |
| 14 | [Database Replication](./14-database-replication/) | Leader-follower, multi-leader, sync/async, conflict resolution | 4-5 hrs | Expert | [79-replication](../../79-replication/) |
| 15 | [Query Optimization](./15-query-optimization/) | Query plans, EXPLAIN ANALYZE, index tuning, materialized views | 4-5 hrs | Expert | [80-query-optimization](../../80-query-optimization/) |
| 16 | [Database Testing](./16-database-testing/) | Testcontainers, integration tests, data fixtures, migration testing | 3-4 hrs | Advanced | [81-database-testing](../../81-database-testing/) |
| 17 | [Cassandra NoSQL](./17-cassandra-nosql/) | Wide-column stores, CQL, data modeling, partitioning, tunable consistency | 4-5 hrs | Expert | [82-cassandra](../../82-cassandra/) |
| 18 | [CockroachDB](./18-cockroachdb/) | Distributed SQL, geo-partitioning, online schema changes, survivability | 4-5 hrs | Expert | [83-cockroachdb](../../83-cockroachdb/) |
| 19 | [Database Migration Strategies](./19-database-migration-strategies/) | Zero-downtime, expand-migrate-contract, blue-green DB | 4-5 hrs | Expert | [84-migration-strategies](../../84-migration-strategies/) |
| 20 | [Database Security](./20-database-security/) | Encryption at rest/transit, RBAC, row-level security, audit logging | 4-5 hrs | Expert | [85-database-security](../../85-database-security/) |

**Total estimated time: 70-89 hours**

---

## Learning Path

```
01 ──→ 02 ──→ 03 ──→ 04 ──→ 05 ──→ 06 ──→ 07 ──→ 08 ──→ 09 ──→ 10 ──→ 11 ──→ 12 ──→ 13 ──→ 14 ──→ 15 ──→ 16 ──→ 17 ──→ 18 ──→ 19 ──→ 20
PG    JDBC    JPA/H   Flyway  Liqui   Mongo   Redis   Redis   R2DBC  Data    ES    Vector  Shard   Repl    Query   Test    Cass    CRDB    Mig     Sec
                                    DB              Adv            JPA                  DB      ing     ication  Opt             andra           Strat   urity
```

Labs 01–05 cover relational databases and migration. Labs 06–08 cover NoSQL. Labs 09–12 cover advanced access patterns. Labs 13–20 cover advanced database topics at the expert level.

---

## Prerequisites

- Java proficiency (JDBC basics, streams)
- Understanding of SQL fundamentals (SELECT, JOIN, GROUP BY)
- Familiarity with Spring Boot basics
- Docker Desktop for running database instances

---

## How to Use This Academy

### For Backend Developers
Work Labs 01–05 for core relational database skills, then Labs 06–08 for NoSQL. Continue to Labs 13–20 for expert-level distributed database topics.

### For Data Engineers
Pay special attention to Labs 01, 04, 05, 09, 12 for data pipeline integration, and Labs 15–19 for optimization, testing, and migration at scale.

### For Full-Stack Developers
Labs 03 and 06–08 cover the most common Java database scenarios. Labs 16 and 20 cover testing and security.

### For Database Reliability Engineers
Labs 13–20 are essential: sharding, replication, query optimization, testing, Cassandra, CockroachDB, migration strategies, and security form the core of production database operations.

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
- [Cassandra Docs](https://cassandra.apache.org/doc/)
- [CockroachDB Docs](https://www.cockroachlabs.com/docs/)
- [Hibernate ORM](https://hibernate.org/orm/documentation/)
- [Flyway Docs](https://documentation.red-gate.com/flyway/)
- [Liquibase Docs](https://docs.liquibase.com/)
- [Testcontainers Docs](https://testcontainers.com/)

### Books
- *Designing Data-Intensive Applications* — Martin Kleppmann
- *High Performance MySQL* — Baron Schwartz
- *MongoDB: The Definitive Guide* — Shannon Bradshaw
- *Redis in Action* — Josiah L. Carlson
- *Cassandra: The Definitive Guide* — Jeff Carpenter, Eben Hewitt
- *Database Internals* — Alex Petrov
- *Java Persistence with Hibernate* — Christian Bauer

---

<div align="center">

**Master Data. Build Everything.**

</div>
