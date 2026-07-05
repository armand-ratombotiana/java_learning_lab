# Architecture: Banking Platform

## High-Level Architecture
```
                         +-----------+
                         | Gateway   |
                         | Service   |
                         +-----+-----+
                               |
         +---------------------+-----------------------+
         |         |           |           |           |
    +----+----+ +--+----+ +---+----+ +---+----+ +----+----+
    | Account | |Payment| | Fraud  | |Notific.| |  User   |
    | Service | |Service| |Service | | Service| | Service |
    +----+----+ +-------+ +--------+ +--------+ +---------+
         |            |         |
    [PostgreSQL] [PostgreSQL] [Redis]
         |            |
    [Ledger DB] [Idempotency Cache]
```

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Build**: Maven (multi-module)
- **Database**: PostgreSQL per service
- **Cache**: Redis
- **Messaging**: Kafka
- **Containerization**: Docker, docker-compose
- **Orchestration**: Kubernetes (k8s manifests provided)
- **Monitoring**: Prometheus + Grafana
- **API**: RESTful (OpenAPI 3.0)

## Service Communication
- Synchronous: HTTP REST for request-response (balance checks, validations)
- Asynchronous: Kafka events for state propagation (payment events, fraud results)
- Event schema: Avro with Schema Registry
