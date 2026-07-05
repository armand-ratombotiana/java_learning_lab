# Architecture: Recommendation Engine

## High-Level Architecture
```
[User Events] --> [Kafka] --> [Data Pipeline] --> [Feature Store]
                                                      |
                +----------+    +-----------+    +-----+------+
                | Offline  |--->| Model     |--->| Online    |
                | Trainer  |    | Registry  |    | Serving   |
                +----------+    +-----------+    +-----+------+
                                                      |
                                               [REST API]
                                                      |
                                               [Client Apps]
```

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Build**: Maven
- **Database**: PostgreSQL (interactions, metadata)
- **Cache**: Redis (factor matrices, features)
- **Messaging**: Kafka (event ingestion)
- **ML Framework**: Apache Spark (ALS training)
- **ANN Index**: HNSW (custom Java implementation or JNI wrapper)
- **Containerization**: Docker + docker-compose
- **Monitoring**: Prometheus + Grafana
