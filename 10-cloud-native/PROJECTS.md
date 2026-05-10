# Cloud-Native Java Projects - Module 10

This module covers cloud-native Java development, containerization with Docker, Kubernetes deployment, and cloud-native patterns for Java applications.

## Mini-Project: Docker Containerization (2-4 hours)

### Overview
Create Docker containers for a Spring Boot microservice with multi-stage builds, proper configuration management, and health checks.

### Project Structure
```
cloud-native-demo/
├── pom.xml
├── src/main/java/com/learning/cloudnative/
│   ├── CloudNativeApplication.java
│   ├── controller/ProductController.java
│   ├── model/Product.java
│   ├── repository/ProductRepository.java
│   └── service/ProductService.java
├── src/main/resources/
│   └── application.yml
├── Dockerfile
├── Dockerfile.multistage
└── docker-compose.yml
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>cloud-native-demo</artifactId>
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
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    
    <build>
        <finalName>cloud-native-demo</finalName>
    </build>
</project>
```

#### Product Model
```java
package com.learning.cloudnative.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String sku;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    private Integer stockQuantity;
    
    private boolean active;
    
    public Product() {}
    
    public Product(String sku, String name, BigDecimal price, Integer stockQuantity) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.active = true;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
```

#### ProductController
```java
package com.learning.cloudnative.controller;

import com.learning.cloudnative.model.Product;
import com.learning.cloudnative.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, 
                                                   @RequestBody Product product) {
        return productService.updateProduct(id, product)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
```

#### ProductService
```java
package com.learning.cloudnative.service;

import com.learning.cloudnative.model.Product;
import com.learning.cloudnative.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Optional<Product> updateProduct(Long id, Product product) {
        return productRepository.findById(id)
            .map(existing -> {
                existing.setName(product.getName());
                existing.setDescription(product.getDescription());
                existing.setPrice(product.getPrice());
                existing.setStockQuantity(product.getStockQuantity());
                return productRepository.save(existing);
            });
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
```

#### ProductRepository
```java
package com.learning.cloudnative.repository;

import com.learning.cloudnative.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
}
```

#### application.yml
```yaml
server:
  port: 8080

spring:
  application:
    name: cloud-native-demo
  datasource:
    url: jdbc:h2:mem:productdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.learning: INFO
```

#### Dockerfile (Basic)
```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven && \
    mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/cloud-native-demo.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### Dockerfile.multistage (Production)
```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/target/*.jar app.jar

RUN chown -R spring:spring /app

USER spring:spring

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### docker-compose.yml
```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile.multistage
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=default
      - JAVA_OPTS=-Xms256m -Xmx512m
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana-data:/var/lib/grafana

volumes:
  grafana-data:
```

#### prometheus.yml
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8080']
```

### Build and Run Instructions
```bash
# Build Docker image
docker build -t cloud-native-demo:1.0.0 .

# Run with docker-compose
docker-compose up -d

# Check running containers
docker ps

# View logs
docker-compose logs -f app

# Check health
curl http://localhost:8080/actuator/health

# Check metrics
curl http://localhost:8080/actuator/prometheus

# Stop services
docker-compose down

# Run with resource limits
docker run -m 512m --cpus 0.5 -p 8080:8080 cloud-native-demo:1.0.0
```

---

## Real-World Project: Kubernetes Microservices Deployment (8+ hours)

### Overview
Deploy a complete microservices architecture to Kubernetes with service discovery, config management, auto-scaling, and observability.

### Architecture
- **Application**: E-commerce platform with product, order, and customer services
- **Container Registry**: Docker Hub or private registry
- **Orchestration**: Kubernetes with Helm charts
- **Service Mesh**: Istio for traffic management
- **Monitoring**: Prometheus and Grafana

### Project Structure
```
k8s-microservices/
├── helm/
│   ├── charts/
│   │   ├── product-service/
│   │   ├── order-service/
│   │   └── customer-service/
│   └── values.yaml
├── k8s/
│   ├── namespaces.yaml
│   ├── deployments/
│   ├── services/
│   ├── configmaps/
│   ├── secrets/
│   ├── horizontal-pod-autoscaler.yaml
│   └── ingress.yaml
├── istio/
│   ├── virtual-service.yaml
│   ├── destination-rule.yaml
│   └── gateway.yaml
└── docker-compose.yml
```

### Implementation

#### Product Service Helm Chart
```yaml
# helm/charts/product-service/Chart.yaml
apiVersion: v2
name: product-service
description: A Helm chart for Product Service
version: 1.0.0
appVersion: "1.0.0"
```

```yaml
# helm/charts/product-service/values.yaml
replicaCount: 2

image:
  repository: product-service
  tag: 1.0.0
  pullPolicy: IfNotPresent

service:
  type: ClusterIP
  port: 8080
  targetPort: 8080

resources:
  limits:
    cpu: 500m
    memory: 512Mi
  requests:
    cpu: 200m
    memory: 256Mi

livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 10

readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8080
  initialDelaySeconds: 30
  periodSeconds: 5

autoscaling:
  enabled: true
  minReplicas: 2
  maxReplicas: 10
  targetCPUUtilizationPercentage: 70

env:
  - name: SPRING_PROFILES_ACTIVE
    value: "k8s"
  - name: SPRING_DATASOURCE_URL
    valueFrom:
      secretKeyRef:
        name: db-credentials
        key: url
  - name: SPRING_DATASOURCE_USERNAME
    valueFrom:
      secretKeyRef:
        name: db-credentials
        key: username
  - name: SPRING_DATASOURCE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: db-credentials
        key: password

configmap:
  SPRING_KAFKA_BOOTSTRAP_SERVERS: "kafka-service:9092"
  SPRING_REDIS_HOST: "redis-service"
  SPRING_REDIS_PORT: "6379"
```

```yaml
# helm/charts/product-service/templates/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ .Chart.Name }}
  labels:
    app: {{ .Chart.Name }}
    version: v1
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels:
      app: {{ .Chart.Name }}
  template:
    metadata:
      labels:
        app: {{ .Chart.Name }}
        version: v1
      annotations:
        sidecar.istio.io/inject: "true"
    spec:
      containers:
      - name: {{ .Chart.Name }}
        image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
        imagePullPolicy: {{ .Values.image.pullPolicy }}
        ports:
        - name: http
          containerPort: {{ .Values.service.targetPort }}
          protocol: TCP
        env:
        {{- range .Values.env }}
        - name: {{ .name }}
          value: {{ .value | quote }}
        {{- end }}
        {{- range .Values.envFromSecret }}
        - name: {{ .name }}
          valueFrom:
            secretKeyRef:
              name: {{ .secretName }}
              key: {{ .key }}
        {{- end }}
        resources:
          requests:
            memory: {{ .Values.resources.requests.memory }}
            cpu: {{ .Values.resources.requests.cpu }}
          limits:
            memory: {{ .Values.resources.limits.memory }}
            cpu: {{ .Values.resources.limits.cpu }}
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: {{ .Values.service.targetPort }}
          initialDelaySeconds: {{ .Values.livenessProbe.initialDelaySeconds }}
          periodSeconds: {{ .Values.livenessProbe.periodSeconds }}
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: {{ .Values.service.targetPort }}
          initialDelaySeconds: {{ .Values.readinessProbe.initialDelaySeconds }}
          periodSeconds: {{ .Values.readinessProbe.periodSeconds }}
```

```yaml
# helm/charts/product-service/templates/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: {{ .Chart.Name }}
  labels:
    app: {{ .Chart.Name }}
spec:
  type: {{ .Values.service.type }}
  ports:
  - port: {{ .Values.service.port }}
    targetPort: {{ .Values.service.targetPort }}
    protocol: TCP
    name: http
  selector:
    app: {{ .Chart.Name }}
```

#### Kubernetes Configurations
```yaml
# k8s/namespaces.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: production
  labels:
    istio-injection: enabled
---
apiVersion: v1
kind: Namespace
metadata:
  name: monitoring
```

```yaml
# k8s/secrets/db-credentials.yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
  namespace: production
type: Opaque
data:
  url: cG9zdGdyZXNxbDovL3Bvc3RncmVzbGlzdGVuZXI6NTQzMi9wcm9kdWN0aW9u
  username: cG9zdGdyZXM=
  password: cG9zdGdyZXMtcGFzc3dvcmQ=
```

```yaml
# k8s/configmaps/app-config.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: production
data:
  SPRING_KAFKA_BOOTSTRAP_SERVERS: "kafka-service:9092"
  SPRING_REDIS_HOST: "redis-service"
  SPRING_REDIS_PORT: "6379"
  LOG_LEVEL: "INFO"
```

```yaml
# k8s/deployments/product-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
        version: v1
      annotations:
        sidecar.istio.io/inject: "true"
    spec:
      containers:
      - name: product-service
        image: product-service:1.0.0
        ports:
        - containerPort: 8080
        envFrom:
        - configMapRef:
            name: app-config
        - secretRef:
            name: db-credentials
        resources:
          requests:
            cpu: 200m
            memory: 256Mi
          limits:
            cpu: 500m
            memory: 512Mi
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
```

```yaml
# k8s/services/product-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: product-service
  namespace: production
spec:
  type: ClusterIP
  ports:
  - port: 8080
    targetPort: 8080
    protocol: TCP
  selector:
    app: product-service
```

```yaml
# k8s/services/order-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service
  namespace: production
spec:
  type: ClusterIP
  ports:
  - port: 8081
    targetPort: 8081
    protocol: TCP
  selector:
    app: order-service
```

```yaml
# k8s/services/customer-service.yaml
apiVersion: v1
kind: Service
metadata:
  name: customer-service
  namespace: production
spec:
  type: ClusterIP
  ports:
  - port: 8082
    targetPort: 8082
    protocol: TCP
  selector:
    app: customer-service
```

```yaml
# k8s/horizontal-pod-autoscaler/product-hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service-hpa
  namespace: production
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: product-service
  minReplicas: 2
  maxReplicas: 10
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
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
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

```yaml
# k8s/ingress/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: api-gateway
  namespace: production
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  tls:
  - hosts:
    - api.example.com
    secretName: api-tls-secret
  rules:
  - host: api.example.com
    http:
      paths:
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
              number: 8081
      - path: /api/customers
        pathType: Prefix
        backend:
          service:
            name: customer-service
            port:
              number: 8082
```

#### Istio Service Mesh Configuration
```yaml
# istio/gateway.yaml
apiVersion: networking.istio.io/v1beta1
kind: Gateway
metadata:
  name: api-gateway
  namespace: production
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
      credentialName: api-tls-secret
    hosts:
    - "api.example.com"
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - "api.example.com"
    tls:
      httpsRedirect: true
```

```yaml
# istio/virtual-service.yaml
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: product-service-vs
  namespace: production
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
  retries:
    attempts: 3
    perTryTimeout: 2s
    retryOn: 5xx,reset,connect-failure,retriable-4xx
  timeout: 10s
```

```yaml
# istio/destination-rule.yaml
apiVersion: networking.istio.io/v1beta1
kind: DestinationRule
metadata:
  name: product-service-dr
  namespace: production
spec:
  host: product-service
  trafficPolicy:
    connectionPool:
      http:
        h2UpgradePolicy: UPGRADE
        http1MaxPendingRequests: 100
        http2MaxRequests: 1000
        maxRequestsPerConnection: 100
    loadBalancer:
      simple: LEAST_REQUEST
    circuitBreaker:
      consecutive5xxErrors: 5
      interval: 30s
      baseEjectionTime: 30s
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2
```

```yaml
# istio/telemetry.yaml
apiVersion: telemetry.istio.io/v1alpha1
kind: Telemetry
metadata:
  name: default-telemetry
  namespace: production
spec:
  tracing:
  - providers:
    - name: jaeger
    randomSamplingPercentage: 10.0
  metrics:
  - providers:
    - name: prometheus
```

#### Service Implementation with Kubernetes Awareness
```java
// src/main/java/com/learning/product/ProductApplication.java
package com.learning.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class ProductApplication {
    
    @Value("${POD_NAME:unknown}")
    private String podName;
    
    @Value("${NODE_NAME:unknown}")
    private String nodeName;
    
    @Value("${POD_IP:unknown}")
    private String podIp;
    
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}

// src/main/java/com/learning/product/config/KubernetesConfig.java
package com.learning.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
public class KubernetesConfig implements WebMvcConfigurer {
    
    @Value("${POD_NAME:unknown}")
    private String podName;
    
    @Value("${POD_IP:unknown}")
    private String podIp;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PodInfoInterceptor(podName, podIp));
    }
    
    static class PodInfoInterceptor implements HandlerInterceptor {
        private final String podName;
        private final String podIp;
        
        PodInfoInterceptor(String podName, String podIp) {
            this.podName = podName;
            this.podIp = podIp;
        }
        
        @Override
        public boolean preHandle(HttpServletRequest request, 
                                HttpServletResponse response, 
                                Object handler) {
            response.setHeader("X-Pod-Name", podName);
            response.setHeader("X-Pod-Ip", podIp);
            return true;
        }
    }
}
```

#### Helm Values (Global)
```yaml
# helm/values.yaml
global:
  domain: example.com
  registry: docker.io
  
  imagePullSecrets: 
    - regcred
  
  monitoring:
    enabled: true
    prometheus:
      enabled: true
    grafana:
      enabled: true

  tracing:
    enabled: true
    jaeger:
      endpoint: jaeger-collector.monitoring:14268

  serviceMesh:
    enabled: true

ingress:
  enabled: true
  className: nginx
  tls:
    enabled: true
    secretName: api-tls-secret

resources:
  limits:
    cpu: 1000m
    memory: 1Gi
  requests:
    cpu: 500m
    memory: 512Mi
```

### Build and Deployment Instructions
```bash
# Install Helm if not present
curl -fsSL https://get.helm.sh/helm-v3.12.0-linux-amd64.tar.gz | tar -xz

# Build Docker images
docker build -t product-service:1.0.0 ./product-service
docker build -t order-service:1.0.0 ./order-service
docker build -t customer-service:1.0.0 ./customer-service

# Push to registry
docker push your-registry/product-service:1.0.0
docker push your-registry/order-service:1.0.0
docker push your-registry/customer-service:1.0.0

# Create namespace
kubectl create namespace production
kubectl label namespace production istio-injection=enabled

# Install cert-manager for TLS
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Install Istio
istioctl install --set profile=default -y

# Deploy with Helm
helm install product-service ./helm/charts/product-service \
  --namespace production \
  --set image.repository=your-registry/product-service \
  --set image.tag=1.0.0

helm install order-service ./helm/charts/order-service \
  --namespace production

helm install customer-service ./helm/charts/customer-service \
  --namespace production

# Apply Istio configuration
kubectl apply -f istio/

# Apply Ingress
kubectl apply -f k8s/ingress/

# Check deployment status
kubectl get pods -n production
kubectl get svc -n production

# View logs
kubectl logs -n production -l app=product-service

# Scale deployment
kubectl scale deployment product-service --replicas=5 -n production

# Check HPA
kubectl get hpa -n production

# Port forward for testing
kubectl port-forward -n production svc/product-service 8080:8080

# View Istio config
istioctl proxy config -n production

# Access Prometheus
kubectl port-forward -n monitoring svc/prometheus 9090:9090

# Access Grafana
kubectl port-forward -n monitoring svc/grafana 3000:3000
```

### Learning Outcomes
- Create Docker images with multi-stage builds
- Configure Kubernetes deployments and services
- Implement horizontal pod autoscaling
- Configure Istio service mesh
- Set up ingress with TLS
- Implement observability with Prometheus and Grafana
- Use Helm for deployment management