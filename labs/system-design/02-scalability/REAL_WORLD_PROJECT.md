# Scalability - REAL WORLD PROJECT

## Project Overview

**Project Name**: Global E-Commerce Platform
**Time Estimate**: 40-60 hours
**Difficulty**: Advanced

Build a production-ready global e-commerce platform that scales to millions of users across multiple regions.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CDN (CloudFront)                                │
└──────────────────────────────┬──────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                    API Gateway (Kong/AWS API Gateway)                       │
│                   (Rate Limiting, Auth, Routing, Caching)                   │
└──────────┬────────────────────────────┬─────────────────────────┬───────────┘
           │                            │                         │
           ▼                            ▼                         ▼
┌─────────────────┐      ┌─────────────────────────┐    ┌─────────────────┐
│  US-East        │      │  EU-West                │    │  AP-SouthEast  │
│  Region         │      │  Region                  │    │  Region        │
├─────────────────┤      ├─────────────────────────┤    ├─────────────────┤
│ - Users Service │      │ - Users Service         │    │ - Users Service │
│ - Products      │      │ - Products              │    │ - Products      │
│ - Orders        │      │ - Orders                │    │ - Orders        │
│ - Inventory     │      │ - Inventory             │    │ - Inventory     │
└────────┬────────┘      └────────────┬────────────┘    └────────┬────────┘
         │                             │                         │
         └─────────────────────────────┴─────────────────────────┘
                                       │
                    ┌──────────────────┴──────────────────┐
                    │     Event Bus (Kafka)               │
                    └──────────────────┬──────────────────┘
                                       │
         ┌─────────────────────────────┼─────────────────────────────┐
         ▼                             ▼                             ▼
┌─────────────────┐      ┌─────────────────────────┐    ┌─────────────────┐
│ Analytics       │      │ Search Service          │    │ Notification    │
│ (ClickStream)   │      │ (Elasticsearch)         │    │ Service         │
└─────────────────┘      └─────────────────────────┘    └─────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                          Data Layer                                          │
├─────────────────┬─────────────────┬─────────────────┬─────────────────────┤
│ PostgreSQL      │ Redis Cluster   │ Elasticsearch   │ S3                  │
│ (Primary +      │ (Global         │ (Product        │ (Images,            │
│  Read Replicas)│  Cache)         │  Search)        │  Static Content)    │
└─────────────────┴─────────────────┴─────────────────┴─────────────────────┘
```

---

## Part 1: Infrastructure Setup

### 1.1 Kubernetes Cluster Configuration

```yaml
# aws-eks-cluster.yaml
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig
metadata:
  name: ecommerce-global
  region: us-east-1

nodeGroups:
  - name: application-nodes
    instanceType: m6i.xlarge
    desiredCapacity: 10
    minSize: 3
    maxSize: 20
    volumeSize: 100
    labels:
      workload: application

  - name: database-nodes
    instanceType: r6i.2xlarge
    desiredCapacity: 3
    minSize: 3
    maxSize: 6
    volumeSize: 500
    labels:
      workload: database

  - name: cache-nodes
    instanceType: r6i.xlarge
    desiredCapacity: 3
    minSize: 3
    maxSize: 6
    volumeSize: 100
    labels:
      workload: cache
```

### 1.2 Helm Charts for Services

```yaml
# charts/product-service/values.yaml
replicaCount: 10

resources:
  requests:
    cpu: "500m"
    memory: "1Gi"
  limits:
    cpu: "2000m"
    memory: "4Gi"

autoscaling:
  enabled: true
  minReplicas: 5
  maxReplicas: 50
  targetCPUUtilizationPercentage: 70

livenessProbe:
  path: /actuator/health
  initialDelaySeconds: 30
  periodSeconds: 10

readinessProbe:
  path: /actuator/health
  initialDelaySeconds: 10
  periodSeconds: 5

env:
  - name: SPRING_PROFILES_ACTIVE
    value: "production"
  - name: REDIS_CLUSTER_NODES
    value: "redis-cluster:6379"
  - name: DATABASE_READ_REPLICAS
    value: "replica1,replica2,replica3"
```

---

## Part 2: Service Implementation

### 2.1 Product Service with Caching

```java
package com.ecommerce.product;

import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "products")
public class ProductService {

    @Cacheable(key = "#productId", unless = "#result == null")
    public Product getProduct(String productId) {
        return productRepository.findById(productId);
    }

    @Cacheable(key = "'category:' + #category")
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    @CachePut(key = "#product.id")
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    @CacheEvict(key = "#productId")
    public void invalidateProduct(String productId) {
        // Cache is automatically evicted
    }

    @CacheEvict(allEntries = true)
    public void clearAllProductsCache() {
        // Clear all products cache
    }
}
```

### 2.2 Redis Cluster Configuration

```java
package com.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Arrays;

@Configuration
public class RedisConfig {

    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        return new RedisClusterConfiguration(
            Arrays.asList(
                "redis-node-1:6379",
                "redis-node-2:6379",
                "redis-node-3:6379",
                "redis-node-4:6379",
                "redis-node-5:6379",
                "redis-node-6:6379"
            )
        );
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

---

## Part 3: Database Scaling

### 3.1 PostgreSQL Primary-Replica Setup

```yaml
# postgresql-primary.yaml
apiVersion: v1
kind: Service
metadata:
  name: postgres-primary
spec:
  selector:
    role: primary
  ports:
    - port: 5432
      targetPort: 5432

---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres-primary
spec:
  serviceName: postgres-primary
  replicas: 1
  selector:
    matchLabels:
      role: primary
  template:
    metadata:
      labels:
        role: primary
    spec:
      containers:
      - name: postgres
        image: postgres:14
        env:
          - name: POSTGRES_DB
            value: ecommerce
          - name: POSTGRES_USER
            valueFrom:
              secretKeyRef:
                name: postgres-credentials
                key: username
          - name: POSTGRES_PASSWORD
            valueFrom:
              secretKeyRef:
                name: postgres-credentials
                key: password
        volumeMounts:
          - name: data
            mountPath: /var/lib/postgresql/data
  volumeClaimTemplates:
    - metadata:
        name: data
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 100Gi
```

```yaml
# postgresql-replica.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres-replica
spec:
  serviceName: postgres-replica
  replicas: 3
  selector:
    matchLabels:
      role: replica
  template:
    metadata:
      labels:
        role: replica
    spec:
      containers:
      - name: postgres
        image: postgres:14
        command:
          - bash
          - -c
          - |
            export PGPASSWORD=$POSTGRES_PASSWORD
            pg_basebackup -h postgres-primary -D /var/lib/postgresql/data -U replication -P -v
            exec postgres
        env:
          - name: POSTGRES_PASSWORD
            valueFrom:
              secretKeyRef:
                name: postgres-credentials
                key: password
          - name: POSTGRES_DB
            value: ecommerce
```

### 3.2 Database Sharding Setup

```java
package com.ecommerce.sharding;

import org.springframework.shardingsphere.core.keygen.KeyGenAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class ModKeyAlgorithm implements KeyGenAlgorithm {
    private int shardingCount = 4;

    @Override
    public String generateKey() {
        long id = SnowflakeIdGenerator.generateId();
        return String.valueOf(id % shardingCount);
    }

    @Override
    public void init(Properties properties) {
        if (properties.containsKey("sharding-count")) {
            this.shardingCount = Integer.parseInt(properties.getProperty("sharding-count"));
        }
    }

    @Override
    public String getType() {
        return "MOD";
    }
}
```

```yaml
# sharding-config.yaml
spring:
  shardingsphere:
    datasource:
      names: ds-0,ds-1,ds-2,ds-3
    rules:
      sharding:
        tables:
          orders:
            actual-data-nodes: ds-$->{0..3}.orders_$->{0..3}
            database-strategy:
              standard:
                sharding-column: user_id
                sharding-algorithm-name: database-mod
            table-strategy:
              standard:
                sharding-column: order_id
                sharding-algorithm-name: table-mod
        binding-tables:
          - orders,order_items
        default-database-strategy:
          standard:
            sharding-algorithm-name: database-mod
    sharding-algorithms:
      database-mod:
        type: INLINE
        props:
          algorithm-expression: ds-$->{Long.valueOf(user_id) % 4}
      table-mod:
        type: INLINE
        props:
          algorithm-expression: orders_$->{Long.valueOf(order_id) % 4}
```

---

## Part 4: Auto-Scaling Configuration

### 4.1 HPA Configuration

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: product-service
  minReplicas: 5
  maxReplicas: 50
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
    - type: Pods
      pods:
        metric:
          name: http_requests_per_second
        target:
          type: AverageValue
          averageValue: "1000"
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Percent
          value: 10
          periodSeconds: 60
        - type: Pods
          value: 2
          periodSeconds: 60
      selectPolicy: Min
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
        - type: Percent
          value: 100
          periodSeconds: 15
        - type: Pods
          value: 4
          periodSeconds: 15
      selectPolicy: Max
```

### 4.2 VPA Configuration

```yaml
apiVersion: autoscaling.k8s.io/v1
kind: VerticalPodAutoscaler
metadata:
  name: product-service-vpa
spec:
  targetRef:
    apiVersion: "apps/v1"
    kind: Deployment
    name: product-service
  updatePolicy:
    updateMode: "Auto"
  resourcePolicy:
    containerPolicies:
      - containerName: product-service
        minAllowed:
          cpu: "250m"
          memory: "512Mi"
        maxAllowed:
          cpu: "4000m"
          memory: "8Gi"
        controlledResources: ["cpu", "memory"]
```

---

## Part 5: Load Balancing

### 5.1 NGINX Ingress Configuration

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ecommerce-ingress
  annotations:
    nginx.ingress.kubernetes.io/load-balance: "least_conn"
    nginx.ingress.kubernetes.io/proxy-connect-timeout: "30"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "300"
    nginx.ingress.kubernetes.io/proxy-send-timeout: "300"
    nginx.ingress.kubernetes.io/proxy-body-size: "50m"
    nginx.ingress.kubernetes.io/rate-limit: "100"
    nginx.ingress.kubernetes.io/rate-limit-window: "1s"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - api.ecommerce.com
        - www.ecommerce.com
      secretName: ecommerce-tls
  rules:
    - host: api.ecommerce.com
      http:
        paths:
          - path: /api/users
            pathType: Prefix
            backend:
              service:
                name: user-service
                port:
                  number: 8080
          - path: /api/products
            pathType: Prefix
            backend:
              service:
                name: product-service
                port:
                  number: 8080
          - path: /api/orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 8080
```

---

## Part 6: Monitoring and Observability

### 6.1 Prometheus Metrics

```java
package com.ecommerce.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {
    private final Counter ordersCreated;
    private final Counter paymentsProcessed;
    private final Counter usersRegistered;
    private final Timer orderProcessingTime;
    private final Timer searchResponseTime;

    public BusinessMetrics(MeterRegistry registry) {
        this.ordersCreated = Counter.builder("orders.created.total")
            .description("Total orders created")
            .register(registry);

        this.paymentsProcessed = Counter.builder("payments.processed.total")
            .description("Total payments processed")
            .register(registry);

        this.usersRegistered = Counter.builder("users.registered.total")
            .description("Total users registered")
            .register(registry);

        this.orderProcessingTime = Timer.builder("orders.processing.time")
            .description("Time to process an order")
            .register(registry);

        this.searchResponseTime = Timer.builder("search.response.time")
            .description("Search query response time")
            .register(registry);
    }

    public void recordOrderCreated() {
        ordersCreated.increment();
    }

    public void recordPaymentProcessed() {
        paymentsProcessed.increment();
    }

    public void recordUserRegistered() {
        usersRegistered.increment();
    }

    public Timer.Sample startOrderTimer() {
        return Timer.start();
    }

    public void recordOrderTime(Timer.Sample sample) {
        sample.stop(orderProcessingTime);
    }
}
```

### 6.2 Grafana Dashboard Configuration

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: ecommerce-grafana-dashboard
  namespace: monitoring
data:
  ecommerce-dashboard.json: |
    {
      "dashboard": {
        "title": "E-Commerce Platform",
        "panels": [
          {
            "title": "Requests per Second",
            "type": "graph",
            "targets": [
              {
                "expr": "sum(rate(http_requests_total[5m]))",
                "legendFormat": "Total RPS"
              }
            ]
          },
          {
            "title": "Response Time p95",
            "type": "graph",
            "targets": [
              {
                "expr": "histogram_quantile(0.95, sum(rate(http_request_duration_seconds_bucket[5m])) by (le))",
                "legendFormat": "p95"
              }
            ]
          },
          {
            "title": "CPU Utilization",
            "type": "graph",
            "targets": [
              {
                "expr": "sum(rate(container_cpu_usage_seconds_total{pod=~\"product-service.*\"}[5m])) by (pod)",
                "legendFormat": "{{pod}}"
              }
            ]
          },
          {
            "title": "Active Connections (Redis)",
            "type": "graph",
            "targets": [
              {
                "expr": "redis_connected_clients",
                "legendFormat": "Connected Clients"
              }
            ]
          }
        ]
      }
    }
```

---

## Part 7: Testing and Validation

### 7.1 Load Test Configuration

```yaml
# k6-load-test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '2m', target: 100 },   // Ramp up
    { duration: '5m', target: 1000 },  // Stress
    { duration: '5m', target: 5000 },  // Spike
    { duration: '2m', target: 0 },     // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const productRes = http.get('https://api.ecommerce.com/api/products');
  check(productRes, {
    'products status 200': (r) => r.status === 200,
  });

  const orderRes = http.post('https://api.ecommerce.com/api/orders', 
    JSON.stringify({
      items: [{ productId: 'prod-123', quantity: 1 }],
    }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(orderRes, {
    'order created': (r) => r.status === 201,
  });

  sleep(1);
}
```

---

## Project Deliverables Checklist

### Infrastructure
- [x] Kubernetes cluster across multiple regions
- [x] Service mesh for communication
- [x] CDN for static content

### Database Layer
- [x] PostgreSQL with primary-replica setup
- [x] Database sharding for large datasets
- [x] Connection pooling

### Caching Layer
- [x] Redis cluster for distributed caching
- [x] Multi-layer caching strategy
- [x] Cache invalidation system

### Compute Layer
- [x] Auto-scaling with HPA
- [x] Vertical scaling with VPA
- [x] Resource quotas and limits

### Traffic Management
- [x] Global load balancing
- [x] Regional routing
- [x] Rate limiting

### Observability
- [x] Prometheus metrics
- [x] Grafana dashboards
- [x] Distributed tracing

---

## Running the Project

```bash
# Deploy infrastructure
kubectl apply -f aws-eks-cluster.yaml

# Deploy services
helm install product-service charts/product-service
helm install order-service charts/order-service
helm install user-service charts/user-service

# Deploy infrastructure components
helm install redis-cluster stable/redis
helm install elasticsearch stable/elasticsearch

# Deploy monitoring
kubectl apply -f prometheus-config.yaml
kubectl apply -f grafana-dashboard.yaml

# Run load tests
k6 run k6-load-test.js

# Monitor
kubectl get hpa
kubectl top pods
```

---

## Extension Opportunities

1. **Multi-Region Active-Active**: Deploy to multiple regions with active-active
2. **Chaos Engineering**: Add ChaosMesh for resilience testing
3. **A/B Testing**: Implement feature flags with split traffic
4. **Edge Computing**: Deploy to edge locations closer to users
5. **Serverless**: Migrate some functions to serverless