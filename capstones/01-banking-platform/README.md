# Banking Platform - Portfolio-Grade Capstone

## Overview
A production-ready, full-stack banking platform with microservices architecture, event-driven design, and enterprise-grade security.

## Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway                               │
│                    (Spring Cloud Gateway)                        │
└─────────────────┬───────────────────────────────────────────────┘
                  │
    ┌─────────────┼─────────────┬─────────────┬─────────────┐
    │             │             │             │             │
┌───▼───┐    ┌────▼────┐   ┌───▼───┐   ┌────▼────┐   ┌────▼────┐
│Account│    │Payment  │   │User   │   │Fraud    │   │Notify   │
│Service│    │Service  │   │Service│   │Service  │   │Service  │
└───┬───┘    └────┬────┘   └───┬───┘   └────┬────┘   └────┬────┘
    │             │             │             │             │
    └─────────────┴─────────────┴─────────────┴─────────────┘
                              │
                    ┌─────────▼─────────┐
                    │      Kafka        │
                    │  (Event Bus)      │
                    └─────────┬─────────┘
                              │
         ┌────────────────────┼────────────────────┐
         │                    │                    │
    ┌────▼────┐          ┌────▼────┐          ┌────▼────┐
    │Postgres │          │ Redis   │          │MongoDB  │
    │(ACID)   │          │(Cache)  │          │(Audit) │
    └─────────┘          └─────────┘          └─────────┘
```

## Tech Stack
- **Framework**: Spring Boot 3.2.x, Spring Cloud
- **Event Streaming**: Apache Kafka
- **Databases**: PostgreSQL, MongoDB, Redis
- **Security**: Spring Security, OAuth2/JWT
- **Containers**: Docker, Kubernetes
- **Observability**: Micrometer, Prometheus, Grafana
- **Testing**: JUnit5, Mockito, TestContainers

## Microservices

### 1. Account Service (Port 8081)
- Account CRUD operations
- Balance management
- Transaction history

### 2. Payment Service (Port 8082)
- Money transfers
- Scheduled payments
- Payment validation

### 3. User Service (Port 8083)
- User registration/management
- KYC workflow
- Authentication (OAuth2)

### 4. Fraud Detection Service (Port 8084)
- Real-time transaction analysis
- ML-based anomaly detection
- Risk scoring

### 5. Notification Service (Port 8085)
- Email/SMS notifications
- Push notifications
- Alert management

### 6. API Gateway (Port 8080)
- Request routing
- Rate limiting
- Authentication

## Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven 3.9+

### Run with Docker Compose
```bash
cd 01-banking-platform
docker-compose up -d
```

### Run Locally
```bash
./mvnw spring-boot:run
```

## Project Structure
```
01-banking-platform/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/bank/
│   │   │   ├── account/          # Account microservice
│   │   │   ├── payment/          # Payment microservice
│   │   │   ├── user/             # User microservice
│   │   │   ├── fraud/            # Fraud detection
│   │   │   ├── notification/     # Notification service
│   │   │   ├── gateway/          # API Gateway
│   │   │   └── common/           # Shared libraries
│   │   └── resources/
│   └── test/
└── k8s/                          # Kubernetes manifests
```

## API Endpoints

### Account Service
- `POST /api/v1/accounts` - Create account
- `GET /api/v1/accounts/{id}` - Get account
- `POST /api/v1/accounts/{id}/deposit` - Deposit money
- `POST /api/v1/accounts/{id}/withdraw` - Withdraw money

### Payment Service
- `POST /api/v1/transfers` - Initiate transfer
- `GET /api/v1/transfers/{id}` - Get transfer status

### User Service
- `POST /api/v1/users` - Register user
- `GET /api/v1/users/{id}` - Get user details

## Event-Driven Architecture

### Kafka Topics
- `account.created` - New account events
- `transaction.initiated` - Transaction events
- `transaction.completed` - Completed transactions
- `fraud.alerts` - Fraud detection alerts
- `notifications.send` - Notification requests

### Event Schema
```json
{
  "eventId": "uuid",
  "eventType": "TRANSACTION_INITIATED",
  "timestamp": "2024-01-15T10:30:00Z",
  "payload": { }
}
```

## Security Features
- OAuth2/JWT authentication
- RBAC (Role-Based Access Control)
- API rate limiting
- Request validation & sanitization
- Audit logging
- End-to-end encryption

## Deployment

### Kubernetes
```bash
kubectl apply -f k8s/
```

### Helm
```bash
helm install banking ./helm/banking
```

## Monitoring
- **Prometheus**: Metrics collection
- **Grafana**: Dashboards
- **Jaeger**: Distributed tracing
- **ELK Stack**: Log aggregation

## Testing
```bash
./mvnw test                    # Unit tests
./mvnw verify                  # Integration tests
```

## License
MIT