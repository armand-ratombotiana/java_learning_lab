# Architecture: Fraud Detection

## High-Level Architecture
```
[Kafka: raw.transactions] --> [Stream Processor]
                                   |
                +------------------+------------------+
                |                  |                  |
          [Feature Extractor] [Rule Engine]   [ML Scorer]
                |                  |                  |
                +--------+---------+---------+--------+
                         |                   |
                   [Decision Maker]    [Feature Store]
                         |                   |
                    [Kafka: fraud.decisions]  |
                         |                   |
                   [Feedback Pipeline]   [Redis]
```

## Technology Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.x + Spring Cloud Stream
- **Stream Processing**: Apache Kafka Streams / Flink
- **ML Runtime**: ONNX Runtime Java
- **Feature Store**: Redis
- **Database**: PostgreSQL (labeled data, audit)
- **Model Training**: Python (scikit-learn), exported to ONNX
- **Containerization**: Docker + docker-compose
- **Monitoring**: Prometheus, Grafana, Micrometer

## Data Flow
- Raw transactions -> Feature extraction -> Rule evaluation -> Async ML scoring -> Decision fusion -> Output topic
