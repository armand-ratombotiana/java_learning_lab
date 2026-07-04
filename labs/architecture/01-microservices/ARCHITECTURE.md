# Microservices Architecture Reference

## Architecture Decision Records

### ADR-001: Communication Protocol
**Decision**: Use REST for synchronous, Kafka for async communication
**Rationale**: REST is simple and universally supported; Kafka provides durability and replay

### ADR-002: Service Discovery
**Decision**: Eureka for service registry
**Rationale**: Mature, well-integrated with Spring Cloud, supports health checks

### ADR-003: Database per Service
**Decision**: Each service has its own database instance
**Rationale**: Enforces loose coupling, independent scaling, technology choice

## System Architecture Diagram
```
[Client] <--> [API Gateway] <--> [Service Registry]
                  |
    +-------------+-------------+-------------+
    |             |             |             |
[OrderSvc]  [PaymentSvc]  [InventorySvc] [NotificationSvc]
    |             |             |             |
[Postgres]   [Postgres]    [Redis]      [MongoDB]
    |             |             |             |
    +------+------+-------------+-------------+
           |
     [Message Broker (Kafka)]
```

## Technology Stack
| Service | Language | Framework | Database | Cache |
|---------|----------|-----------|----------|-------|
| Order | Java 17 | Spring Boot 3.2 | PostgreSQL | Redis |
| Payment | Java 17 | Spring Boot 3.2 | PostgreSQL | - |
| Inventory | Kotlin | Spring Boot 3.2 | MongoDB | Redis |
| Notification | Java 17 | Spring Boot 3.2 | - | - |

## Deployment Architecture
- Kubernetes cluster (EKS/AKS/GKE)
- Helm charts for service deployment
- Istio service mesh for mTLS and traffic management
- Prometheus + Grafana for monitoring
- ELK stack for log aggregation
