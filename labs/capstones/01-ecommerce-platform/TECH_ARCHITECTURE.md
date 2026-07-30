# Technical Architecture: E-Commerce Platform

## Architecture Overview

```
                    ┌─────────────┐
                    │   CDN       │
                    │ (CloudFront) │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  Load Balancer│
                    │  (ALB)       │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway  │
                    │ (Spring GW)  │
                    └──┬───┬───┬──┘
          ┌────────────┘   │   └────────────┐
          ▼                ▼                ▼
   ┌──────────┐     ┌──────────┐     ┌──────────┐
   │ Product  │     │  Cart    │     │  Order   │
   │ Catalog  │     │  Service │     │  Service │
   └────┬─────┘     └────┬─────┘     └────┬─────┘
        │                │                │
   ┌────▼─────┐     ┌────▼─────┐     ┌────▼─────┐
   │  Redis    │     │  Redis   │     │PostgreSQL│
   │  Cache    │     │Session   │     │Sharded   │
   └───────────┘     └──────────┘     └────┬─────┘
                                           │
                    ┌───────────────────────┼───────────────┐
                    ▼                       ▼               ▼
             ┌──────────┐           ┌──────────┐    ┌──────────┐
             │ Payment  │           │Inventory │    │Recommend│
             │ Service  │           │ Service  │    │ Service  │
             └────┬─────┘           └────┬─────┘    └────┬─────┘
                  │                      │               │
             ┌────▼─────┐           ┌────▼─────┐    ┌────▼─────┐
             │3rd Party │           │PostgreSQL │    │   ML     │
             │Payment GW│           │  + Redis  │    │  Engine  │
             └──────────┘           └──────────┘    └──────────┘

                   ┌─────────────────────────────────┐
                   │   Message Queue (Kafka)          │
                   │   Topics: order-created,         │
                   │   payment-completed,             │
                   │   inventory-updated,             │
                   │   notification-send              │
                   └─────────────────────────────────┘
```

## Component Breakdown

### 1. API Gateway (Spring Cloud Gateway)
- **Purpose**: Single entry point for all client requests
- **Functions**: Authentication, rate limiting, request routing, aggregation
- **Routing rules**: /api/products/* → product-catalog, /api/orders/* → order-service, /api/cart/* → cart-service
- **Rate limiting**: 100 req/s per user, 1000 req/s per IP, burstable to 2x

### 2. Product Catalog Service
- **Tech**: Java 21 + Spring Boot 3 + JPA + Elasticsearch
- **Endpoints**: GET /products (search), GET /products/{id} (detail), GET /categories/{id}/products (category browse)
- **Caching**: Caffeine L1 (10K entries, 30s TTL) + Redis L2 (100K entries, 5min TTL)
- **Database**: PostgreSQL (product master) + Elasticsearch (full-text search)
- **Scaling**: Horizontal, stateless — 20 pods during peak, HPA at 60% CPU

### 3. Shopping Cart Service
- **Tech**: Java 21 + Spring Boot 3 + Redis
- **Endpoints**: POST /cart/items (add), DELETE /cart/items/{id} (remove), GET /cart (view)
- **Storage**: Redis Hash (key: cart:{userId}, field: itemId, value: quantity)
- **Persistence**: Async persistence to PostgreSQL every 5 minutes and on cart merge
- **TTL**: Anonymous cart expires after 7 days; merge into user cart on login

### 4. Order Service
- **Tech**: Java 21 + Spring Boot 3 + JPA + State Machine
- **Endpoints**: POST /orders (create), GET /orders/{id} (status), GET /orders/history (list)
- **State Machine**: PENDING → INVENTORY_CHECK → PAYMENT_PENDING → CONFIRMED → SHIPPED → DELIVERED
  - Transitions: CANCEL from PENDING/INVENTORY_CHECK, REFUND from CONFIRMED/DELIVERED
- **Database**: PostgreSQL with 16 shards (shard key: customerId modulo 16)
- **Idempotency**: idempotencyKey (UUID) must be unique per order; duplicate keys return existing order

### 5. Payment Service
- **Tech**: Java 21 + Spring Boot 3 + Stripe/PayPal SDK
- **Endpoints**: POST /payments/charge, POST /payments/refund, GET /payments/{id}
- **Idempotency**: Redis-backed idempotency store with 24-hour TTL
- **Retry policy**: 3 retries with exponential backoff (1s, 2s, 4s); DLQ after 3 failures
- **Fraud detection**: Rule-based (velocity checks, geolocation mismatch, amount thresholds) + ML model scoring

### 6. Inventory Service
- **Tech**: Java 21 + Spring Boot 3 + JPA + Optimistic Locking
- **Endpoints**: POST /inventory/reserve, POST /inventory/release, GET /inventory/{sku}
- **Consistency level**: Linearizable for writes (SERIALIZABLE isolation), eventual for reads
- **Stock reservation**: 15-minute TTL on reservations; auto-release on timeout
- **Overselling prevention**: Optimistic locking on stock_version column; retry on conflict with backoff

### 7. Recommendation Service
- **Tech**: Java 21 + Spring Boot 3 + Spark ML
- **Endpoints**: GET /recommendations/{userId} (personalized), GET /recommendations/trending (popular)
- **Algorithm**: Collaborative filtering (ALS) + content-based + trending (popularity decay)
- **Refresh cadence**: Model retrained daily; recommendations cached for 4 hours
- **Cold start**: New users get trending/popular; new items get category-based similarity

### 8. Admin Analytics Service
- **Tech**: Java 21 + Spring Boot 3 + ClickHouse (OLAP)
- **Endpoints**: GET /admin/sales (revenue), GET /admin/inventory (stock levels), GET /admin/users (metrics)
- **Data source**: Kafka stream from order-service, processed and stored in ClickHouse
- **Dashboards**: BI tools (Apache Superset) querying ClickHouse directly
- **Latency tolerance**: Eventual consistency within 5 minutes acceptable

## Data Model

```sql
-- Product catalog
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(500) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES categories(id),
    price NUMERIC(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    attributes JSONB,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Orders (sharded by customer_id % 16)
CREATE TABLE orders (
    id BIGSERIAL,
    customer_id BIGINT NOT NULL,
    order_status VARCHAR(30) NOT NULL,
    total_amount NUMERIC(14,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    shipping_address JSONB,
    payment_info JSONB,
    idempotency_key VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (id, customer_id)
) PARTITION BY HASH (customer_id);

-- Order items (co-located with order)
CREATE TABLE order_items (
    id BIGSERIAL,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    PRIMARY KEY (id, order_id)
) PARTITION BY HASH (order_id);

-- Inventory
CREATE TABLE inventory (
    product_id BIGINT PRIMARY KEY,
    available_quantity INT NOT NULL,
    reserved_quantity INT DEFAULT 0,
    stock_version INT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT NOW()
);
```

## Tech Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| Language | Java 21 | Runtime platform |
| Framework | Spring Boot 3.2 | Microservices framework |
| API Gateway | Spring Cloud Gateway | Routing, auth, rate limiting |
| Service Mesh | Istio | Traffic management, observability |
| Database (OLTP) | PostgreSQL 16 | Transactional workloads |
| Cache | Redis 7 + Caffeine | Distributed + local caching |
| Search | Elasticsearch 8 | Product search |
| OLAP | ClickHouse | Analytics queries |
| Message Queue | Apache Kafka 3.6 | Event streaming |
| Container | Docker + Kubernetes | Deployment |
| CI/CD | GitHub Actions + ArgoCD | Deployment automation |
| Monitoring | Prometheus + Grafana | Metrics |
| Tracing | OpenTelemetry + Jaeger | Distributed tracing |
| Logging | ELK Stack (Elasticsearch, Logstash, Kibana) | Centralized logging |

## Deployment Topology

```
Region: us-east-1 (Primary)
  ├── AZ us-east-1a: 50% of pods, primary DB, Kafka brokers
  ├── AZ us-east-1b: 50% of pods, standby DB, Kafka brokers
  └── AZ us-east-1c: 50% of pods, read replica DB, Kafka brokers

Region: eu-west-1 (DR)
  ├── AZ eu-west-1a: 25% of pods (standby), Kafka mirror
  ├── AZ eu-west-1b: 25% of pods (standby), Kafka mirror
  └── AZ eu-west-1c: 25% of pods (standby), Kafka mirror

CDN: CloudFront with edge locations in NA, EU, APAC
DNS: Route53 with latency-based routing + health checks
```

## Security Architecture

- **Authentication**: JWT-based with OAuth2 (Keycloak); access tokens (15min TTL), refresh tokens (7 day TTL)
- **Authorization**: Role-based (USER, ADMIN, SUPPORT) with Spring Security method-level annotations
- **Data encryption**: TLS 1.3 in transit; AES-256 at rest (RDS encryption, EBS encryption)
- **Secrets management**: Vault/HashiCorp for API keys, database passwords; rotated every 90 days
- **Input validation**: All inputs sanitized; SQL parameterization; XSS/CSRF protection
- **Audit logging**: All order mutations logged with before/after images; stored in immutable audit table

## Disaster Recovery

- **RPO**: < 1 minute (synchronous replication within region, async cross-region)
- **RTO**: < 5 minutes (auto-failover via Route53 health checks)
- **Data backup**: Daily snapshots (30-day retention) + continuous WAL archiving (7-day retention)
- **DR test**: Quarterly full-region failover exercise; documented runbook
