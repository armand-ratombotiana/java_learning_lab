# Why AWS Database Exists

## The Problem
Managing databases on-premise requires DBAs, hardware provisioning, backup strategies, failover configuration, patching, and scaling decisions. AWS Database services eliminate this operational burden while offering specialized engines for every workload pattern.

## Why Multiple Database Services?
- **RDS**: Traditional relational (SQL) — use when you need joins, ACID, ORM compatibility
- **Aurora**: MySQL/PostgreSQL-compatible with 5x performance — for high-throughput relational
- **DynamoDB**: NoSQL key-value/document — for low-latency, auto-scaling workloads
- **ElastiCache**: In-memory cache — for sub-millisecond reads, session storage
- **Redshift**: Columnar data warehouse — for analytics and reporting

## Java Context
- RDS + JDBC/Hibernate = familiar relational access for Spring Boot apps
- DynamoDB + DynamoDBMapper/Document API = fast NoSQL access
- ElastiCache + Jedis/Lettuce = Redis caching for Java microservices
- Aurora + Spring Data JPA = high-performance relational with minimal changes
