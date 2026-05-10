# Service Mesh Projects - Module 57

This module covers Istio, Linkerd, and service mesh patterns for microservice communication.

## Mini-Project: Istio Traffic Management (2-4 hours)

### Overview
Implement traffic management, observability, and security for microservices using Istio service mesh.

### Project Structure
```
service-mesh-demo/
├── services/
│   ├── product-service/
│   │   ├── src/main/java/.../
│   │   ├── pom.xml
│   │   └── Dockerfile
│   ├── order-service/
│   │   ├── src/main/java/.../
│   │   ├── pom.xml
│   │   └── Dockerfile
│   └── gateway-service/
│       ├── src/main/java/.../
│       ├── pom.xml
│       └── Dockerfile
├── k8s/
│   ├── namespace.yaml
│   ├── product-deployment.yaml
│   ├── order-deployment.yaml
│   ├── gateway-deployment.yaml
│   ├── destination-rule.yaml
│   ├── virtual-service.yaml
│   └── gateway.yaml
└── istio/
    ├── mesh-config.yaml
    └── telemetry.yaml
```

### Implementation

#### Product Service
```java
// product-service/pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>product-service</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>
</project>

// ProductServiceApplication.java
package com.learning.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}

// ProductController.java
package com.learning.product.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    
    public ProductController() {
        products.put(1L, new Product(1L, "Laptop", 999.99, "High-performance laptop"));
        products.put(2L, new Product(2L, "Mouse", 29.99, "Wireless mouse"));
        products.put(3L, new Product(3L, "Keyboard", 79.99, "Mechanical keyboard"));
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return products.values().stream().toList();
    }
    
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return products.get(id);
    }
    
    @GetMapping("/version")
    public Map<String, String> getVersion() {
        return Map.of(
            "service", "product-service",
            "version", System.getenv().getOrDefault("VERSION", "v1"),
            "pod", System.getenv().getOrDefault("HOSTNAME", "local")
        );
    }
}

record Product(Long id, String name, double price, String description) {}

// OrderService (Client)
package com.learning.product.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OrderClient {
    
    private final RestClient restClient;
    
    public OrderClient() {
        this.restClient = RestClient.builder()
            .baseUrl("http://order-service:8081")
            .build();
    }
    
    public String getOrderStatus(String orderId) {
        try {
            return restClient.get()
                .uri("/api/orders/{orderId}/status", orderId)
                .retrieve()
                .body(String.class);
        } catch (Exception e) {
            return "Order service unavailable";
        }
    }
}
```

#### Order Service
```java
// order-service/pom.xml (similar structure)
// OrderServiceApplication.java
package com.learning.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

// OrderController.java
package com.learning.order.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    
    @PostMapping
    public Map<String, String> createOrder(@RequestBody OrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        orders.put(orderId, new Order(orderId, request.customerId(), 
            request.productIds(), "CREATED", System.currentTimeMillis()));
        return Map.of("orderId", orderId, "status", "CREATED");
    }
    
    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        return orders.get(orderId);
    }
    
    @GetMapping("/{orderId}/status")
    public Map<String, String> getOrderStatus(@PathVariable String orderId) {
        Order order = orders.get(orderId);
        return order != null 
            ? Map.of("orderId", orderId, "status", order.status())
            : Map.of("orderId", orderId, "status", "NOT_FOUND");
    }
    
    @GetMapping("/version")
    public Map<String, String> getVersion() {
        return Map.of(
            "service", "order-service",
            "version", System.getenv().getOrDefault("VERSION", "v1"),
            "pod", System.getenv().getOrDefault("HOSTNAME", "local")
        );
    }
}

record OrderRequest(String customerId, java.util.List<Long> productIds) {}
record Order(String orderId, String customerId, java.util.List<Long> productIds, 
            String status, long createdAt) {}
```

#### Istio Configuration
```yaml
# k8s/namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: mesh-demo
  labels:
    istio-injection: enabled
---
# k8s/product-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
  namespace: mesh-demo
spec:
  replicas: 2
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
        version: v1
    spec:
      containers:
      - name: product-service
        image: product-service:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: VERSION
          value: "v1"
---
# k8s/order-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  namespace: mesh-demo
spec:
  replicas: 2
  selector:
    matchLabels:
      app: order-service
  template:
    metadata:
      labels:
        app: order-service
        version: v1
    spec:
      containers:
      - name: order-service
        image: order-service:1.0.0
        ports:
        - containerPort: 8081
        env:
        - name: VERSION
          value: "v1"
---
# k8s/gateway.yaml
apiVersion: networking.istio.io/v1
kind: Gateway
metadata:
  name: demo-gateway
  namespace: mesh-demo
spec:
  selector:
    istio: ingressgateway
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - "*"
---
# k8s/virtual-service.yaml
apiVersion: networking.istio.io/v1
kind: VirtualService
metadata:
  name: product-service
  namespace: mesh-demo
spec:
  hosts:
  - product-service
  http:
  - match:
    - headers:
        x-canary:
          exact: "true"
    route:
    - destination:
        host: product-service
        subset: v2
      weight: 100
  - route:
    - destination:
        host: product-service
        subset: v1
      weight: 100
---
# k8s/destination-rule.yaml
apiVersion: networking.istio.io/v1
kind: DestinationRule
metadata:
  name: product-service
  namespace: mesh-demo
spec:
  host: product-service
  trafficPolicy:
    connectionPool:
      http:
        h2UpgradePolicy: UPGRADE
        http1MaxPendingRequests: 100
        http2MaxRequests: 1000
    loadBalancer:
      simple: LEAST_REQUEST
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2
---
# istio/telemetry.yaml
apiVersion: telemetry.istio.io/v1
kind: Telemetry
metadata:
  name: mesh-default
  namespace: mesh-demo
spec:
  tracing:
  - providers:
    - name: jaeger
    randomSamplingPercentage: 10.0
```

### Build and Deploy Instructions
```bash
# Install Istio (if not installed)
istioctl install --set profile=demo -y

# Enable namespace for automatic sidecar injection
kubectl label namespace mesh-demo istio-injection=enabled

# Build services
cd services/product-service && mvn clean package
cd services/order-service && mvn clean package

# Build Docker images
docker build -t product-service:1.0.0 ./services/product-service
docker build -t order-service:1.0.0 ./services/order-service

# Deploy to Kubernetes
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/product-deployment.yaml
kubectl apply -f k8s/order-deployment.yaml
kubectl apply -f k8s/gateway.yaml
kubectl apply -f k8s/virtual-service.yaml
kubectl apply -f k8s/destination-rule.yaml
kubectl apply -f istio/telemetry.yaml

# Check Istio resources
kubectl get gateway,vs,dr -n mesh-demo

# Test traffic routing
curl -H "x-canary: true" http://<INGRESS_IP>/api/products/version

# View Kiali dashboard
istioctl dashboard kiali
```

---

## Real-World Project: Multi-Cluster Service Mesh (8+ hours)

### Overview
Implement a production-grade service mesh spanning multiple Kubernetes clusters with global routing, mutual TLS, and distributed tracing.

### Architecture
- **Primary Cluster**: US-East (production traffic)
- **Secondary Cluster**: EU-West (disaster recovery)
- **Service Mesh**: Istio with multi-cluster configuration
- **Traffic Management**: Global load balancing with failover
- **Security**: mTLS between all services
- **Observability**: Distributed tracing, metrics, logging

### Project Structure
```
multi-cluster-mesh/
├── clusters/
│   ├── us-east/
│   │   ├── cluster-config.yaml
│   │   ├── services/
│   │   └── istio-config/
│   └── eu-west/
│       ├── cluster-config.yaml
│       ├── services/
│       └── istio-config/
├── common/
│   ├── gateway.yaml
│   ├── virtual-service-global.yaml
│   ├── destination-rule.yaml
│   └── peerauthentication.yaml
├── security/
│   ├── ca-cert.yaml
│   ├── workload-cert.yaml
│   └── authorization-policies.yaml
└── observability/
    ├── telemetry.yaml
    ├── accesslogging.yaml
    └── dashboards/
```

### Implementation

#### Multi-Cluster Configuration
```yaml
# clusters/us-east/cluster-config.yaml
apiVersion: istio.io/v1alpha1
kind: MeshConfig
metadata:
  name: istio
spec:
  localityLbSetting:
    enabled: true
    failover:
    - from: region/us-east
      to: region/eu-west
  defaultConfig:
    proxyMetadata:
      ISTIO_META_DNS_CAPTURE: "true"
      ISTIO_META_DNS_AUTO_ALLOCATE: "true"
---
# common/gateway.yaml
apiVersion: networking.istio.io/v1
kind: Gateway
metadata:
  name: global-gateway
  namespace: istio-system
spec:
  selector:
    istio: ingressgateway
  servers:
  - port:
      number: 443
      name: https
      protocol: HTTPS
    tls:
      mode: SIMPLE
      credentialName: gateway-cert
    hosts:
    - "*.global.example.com"
  - port:
      number: 8443
      name: https-internal
      protocol: HTTPS
    tls:
      mode: ISTIO_MUTUAL
    hosts:
    - "*.internal.global.example.com"
---
# common/virtual-service-global.yaml
apiVersion: networking.istio.io/v1
kind: VirtualService
metadata:
  name: global-routing
  namespace: istio-system
spec:
  hosts:
  - "api.global.example.com"
  gateways:
  - global-gateway
  http:
  - match:
    - headers:
        x-region:
          exact: us-east
    route:
    - destination:
        host: api-service.us-east.svc.cluster.local
        port:
          number: 8080
    retries:
      attempts: 3
      perTryTimeout: 2s
  - match:
    - headers:
        x-region:
          exact: eu-west
    route:
    - destination:
        host: api-service.eu-west.svc.cluster.local
        port:
          number: 8080
  - route:
    - destination:
        host: api-service.us-east.svc.cluster.local
        port:
          number: 8080
      weight: 80
    - destination:
        host: api-service.eu-west.svc.cluster.local
        port:
          number: 8080
      weight: 20
---
# security/peerauthentication.yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: default
  namespace: istio-system
spec:
  mtls:
    mode: STRICT
---
# security/authorization-policies.yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: api-frontend
  namespace: production
spec:
  selector:
    matchLabels:
      app: api-service
  rules:
  - from:
    - source:
        principals:
        - cluster.local/ns/gateway/sa/gateway-service
    to:
    - operations:
      - ports: ["8080"]
        methods: ["GET", "POST"]
---
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: backend-allow
  namespace: production
spec:
  selector:
    matchLabels:
      app: backend-service
  rules:
  - from:
    - source:
        namespaces: ["production"]
    to:
    - operations:
      - ports: ["8080"]
```

#### Service Implementation with Mesh Features
```java
// api-service/src/main/java/com/learning/api/ApiServiceApplication.java
package com.learning.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ApiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiServiceApplication.class, args);
    }
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

// MeshAwareController.java
package com.learning.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class MeshAwareController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @GetMapping("/health")
    public Map<String, Object> health(@RequestHeader HttpHeaders headers) {
        return Map.of(
            "status", "healthy",
            "pod", System.getenv().getOrDefault("HOSTNAME", "unknown"),
            "region", getRegion(),
            "availability-zone", getAvailabilityZone()
        );
    }
    
    private String getRegion() {
        return System.getenv().getOrDefault("ISTIO_META_LOCATION", 
            System.getenv().getOrDefault("REGION", "us-east-1"));
    }
    
    private String getAvailabilityZone() {
        return System.getenv().getOrDefault("AZ", "us-east-1a");
    }
    
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestHeader HttpHeaders headers) {
        
        // Use mesh DNS for service discovery
        try {
            Map<String, Object> products = restTemplate.getForObject(
                "http://catalog-service:8080/api/catalog/products",
                Map.class
            );
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(
                Map.of("error", "Catalog service unavailable")
            );
        }
    }
    
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Map<String, Object> order,
            @RequestHeader HttpHeaders headers) {
        
        // Add distributed tracing headers
        String traceId = headers.getFirst("x-b3-traceid");
        String spanId = headers.getFirst("x-b3-spanid");
        
        order.put("traceId", traceId);
        order.put("createdAt", System.currentTimeMillis());
        
        try {
            Map<String, Object> result = restTemplate.postForObject(
                "http://order-service:8081/api/orders",
                order,
                Map.class
            );
            return ResponseEntity.accepted().body(result);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(
                Map.of("error", "Order service unavailable")
            );
        }
    }
}

// ServiceMetadataBean.java
package com.learning.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ServiceMetadataBean {
    
    @Value("${SERVICE_NAME:api-service}")
    private String serviceName;
    
    @Value("${SERVICE_VERSION:v1.0.0}")
    private String serviceVersion;
    
    @Value("${ISTIO_META_LOCATION:unknown}")
    private String location;
    
    public Map<String, String> getMetadata() {
        return Map.of(
            "service", serviceName,
            "version", serviceVersion,
            "location", location,
            "pod", System.getenv().getOrDefault("HOSTNAME", "unknown")
        );
    }
}
```

#### Cross-Cluster Service Configuration
```yaml
# clusters/us-east/services/catalog-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: catalog-service
  namespace: production
  labels:
    app: catalog-service
    version: v1
spec:
  replicas: 3
  selector:
    matchLabels:
      app: catalog-service
  template:
    metadata:
      labels:
        app: catalog-service
        version: v1
      annotations:
        sidecar.istio.io/inject: "true"
    spec:
      containers:
      - name: catalog-service
        image: catalog-service:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: ISTIO_META_LOCATION
          value: "region/us-east/zone/us-east-1"
        - name: SERVICE_VERSION
          value: "v1.0.0"
        resources:
          requests:
            cpu: 100m
            memory: 256Mi
          limits:
            cpu: 500m
            memory: 512Mi
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 5
          periodSeconds: 10
---
# Service for cross-cluster discovery
apiVersion: v1
kind: Service
metadata:
  name: catalog-service
  namespace: production
spec:
  ports:
  - port: 8080
    targetPort: 8080
  selector:
    app: catalog-service
---
# clusters/eu-west/services/catalog-deployment.yaml (similar with different region)
apiVersion: apps/v1
kind: Deployment
metadata:
  name: catalog-service
  namespace: production
spec:
  replicas: 2
  template:
    metadata:
      labels:
        app: catalog-service
        version: v1
      annotations:
        sidecar.istio.io/inject: "true"
    spec:
      containers:
      - name: catalog-service
        image: catalog-service:1.0.0
        env:
        - name: ISTIO_META_LOCATION
          value: "region/eu-west/zone/eu-west-1"
```

### Build and Deployment
```bash
# Setup Istio multi-cluster
# Install Istio on primary cluster
istioctl install --set values.global.multiCluster.clusterName=us-east \
    --set values.global.network=primary-network -y

# Install Istio on secondary cluster
istioctl install --set values.global.multiCluster.clusterName=eu-west \
    --set values.global.network=primary-network -y

# Enable endpoint discovery
istioctl x create-remote-secret us-east | kubectl apply -f - 
istioctl x create-remote-secret eu-west | kubectl apply -f -

# Build and push images
docker build -t registry.example.com/catalog-service:1.0.0 ./catalog-service
docker push registry.example.com/catalog-service:1.0.0

# Deploy to US-East
kubectl apply -f clusters/us-east/services/
kubectl apply -f clusters/us-east/istio-config/

# Deploy to EU-West
kubectl apply -f clusters/us-west/services/
kubectl apply -f clusters/eu-west/istio-config/

# Apply common configurations
kubectl apply -f common/
kubectl apply -f security/
kubectl apply -f observability/

# Verify multi-cluster setup
istioctl pc endpoints -n production catalog-service

# Test global load balancing
for i in {1..10}; do 
  curl -s https://api.global.example.com/api/v1/health | jq .region
done

# Monitor with Kiali
istioctl dashboard kiali &

# Check metrics
istioctl proxy stats service -n production
```

### Learning Outcomes
- Configure multi-cluster service mesh with Istio
- Implement global load balancing and failover
- Set up mutual TLS between clusters
- Configure distributed tracing across clusters
- Implement authorization policies
- Monitor and visualize service mesh traffic