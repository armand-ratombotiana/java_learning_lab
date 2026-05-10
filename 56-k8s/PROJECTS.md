# Kubernetes Projects - Module 56

This module covers Kubernetes deployment, Pods, Services, and ConfigMaps with practical Java applications.

## Mini-Project: Spring Boot Application Deployment (2-4 hours)

### Overview
Deploy a Spring Boot microservice to Kubernetes with proper configuration management using ConfigMaps and Services.

### Project Structure
```
mini-k8s-project/
├── src/
│   └── main/
│       ├── java/com/learning/k8s/
│       │   ├── MiniK8sApplication.java
│       │   ├── controller/HelloController.java
│       │   ├── service/MessageService.java
│       │   └── config/AppConfig.java
│       └── resources/
│           └── application.yml
├── Dockerfile
├── deployment.yaml
├── service.yaml
├── configmap.yaml
└── pom.xml
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
    <artifactId>mini-k8s-project</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
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
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Java Application
```java
// src/main/java/com/learning/k8s/MiniK8sApplication.java
package com.learning.k8s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiniK8sApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiniK8sApplication.class, args);
    }
}

// src/main/java/com/learning/k8s/controller/HelloController.java
package com.learning.k8s.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    
    @Value("${app.message:Hello from Kubernetes!}")
    private String message;
    
    @Value("${app.environment:dev}")
    private String environment;
    
    @GetMapping("/")
    public String hello() {
        return message;
    }
    
    @GetMapping("/health")
    public HealthStatus health() {
        return new HealthStatus("UP", environment);
    }
    
    record HealthStatus(String status, String environment) {}
}

// src/main/java/com/learning/k8s/service/MessageService.java
package com.learning.k8s.service;

import org.springframework.stereotype.Service;

@Service
public class MessageService {
    
    public String getWelcomeMessage(String name) {
        return "Welcome, " + name + " to Kubernetes learning!";
    }
    
    public String getPodInfo() {
        return System.getenv("HOSTNAME") != null 
            ? "Running on pod: " + System.getenv("HOSTNAME")
            : "Running locally";
    }
}
```

#### Kubernetes Resources
```yaml
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  APP_MESSAGE: "Hello from Kubernetes Cluster!"
  APP_ENVIRONMENT: "production"
---
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: spring-app
  template:
    metadata:
      labels:
        app: spring-app
    spec:
      containers:
      - name: spring-app
        image: mini-k8s-project:1.0.0
        ports:
        - containerPort: 8080
        envFrom:
        - configMapRef:
            name: app-config
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
---
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: spring-app-service
spec:
  selector:
    app: spring-app
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP
```

### Build and Deploy Instructions
```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t mini-k8s-project:1.0.0 .

# Apply Kubernetes resources
kubectl apply -f configmap.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# Check status
kubectl get pods
kubectl get services
kubectl get configmaps

# View logs
kubectl logs -l app=spring-app

# Scale application
kubectl scale deployment spring-app --replicas=5
```

---

## Real-World Project: Multi-Tier E-Commerce Platform (8+ hours)

### Overview
Deploy a complete e-commerce platform with frontend, backend API, database, and message queue to Kubernetes with proper networking, storage, and monitoring.

### Architecture
- **Frontend**: React-based UI (Node.js)
- **Backend API**: Spring Boot REST API
- **Database**: PostgreSQL with persistent storage
- **Message Queue**: RabbitMQ for async processing
- **Ingress**: NGINX Ingress for external access
- **Monitoring**: Prometheus and Grafana

### Project Structure
```
ecommerce-k8s/
├── backend/
│   ├── src/main/java/com/learning/ecommerce/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   └── Dockerfile
├── k8s/
│   ├── namespace.yaml
│   ├── backend-deployment.yaml
│   ├── frontend-deployment.yaml
│   ├── postgres-deployment.yaml
│   ├── rabbitmq-deployment.yaml
│   ├── services.yaml
│   ├── ingress.yaml
│   ├── secrets.yaml
│   ├── configmaps.yaml
│   └── persistent-volume.yaml
└── scripts/
    └── deploy.sh
```

### Implementation

#### Backend Spring Boot Application
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>ecommerce-backend</artifactId>
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
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

#### Core Application Classes
```java
// EcommerceApplication.java
package com.learning.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}

// ProductController.java
package com.learning.ecommerce.controller;

import com.learning.ecommerce.model.Product;
import com.learning.ecommerce.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductRepository productRepository;
    
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
            .map(existing -> {
                existing.setName(product.getName());
                existing.setPrice(product.getPrice());
                existing.setDescription(product.getDescription());
                existing.setStock(product.getStock());
                return ResponseEntity.ok(productRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

// Product.java (Entity)
package com.learning.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private Integer stock;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    // Getters, Setters, Constructors
    public Product() {}
    
    public Product(String name, Double price, String description, Integer stock) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.createdAt = java.time.LocalDateTime.now();
    }
    
    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}

// OrderController.java
package com.learning.ecommerce.controller;

import com.learning.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        String orderId = orderService.processOrder(request);
        return ResponseEntity.accepted().body(orderId);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}

record OrderRequest(String customerId, java.util.List<OrderItem> items) {}
record OrderItem(Long productId, Integer quantity) {}
record OrderResponse(String orderId, String status, java.util.List<OrderItem> items, Double total) {}
```

#### Message Queue Integration
```java
// OrderService.java
package com.learning.ecommerce.service;

import com.learning.ecommerce.controller.OrderRequest;
import com.learning.ecommerce.controller.OrderResponse;
import com.learning.ecommerce.model.Order;
import com.learning.ecommerce.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public OrderService(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public String processOrder(OrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, request.customerId(), "PENDING", request.items());
        
        orderRepository.save(order);
        
        // Send to message queue for async processing
        rabbitTemplate.convertAndSend("orderQueue", order);
        
        return orderId;
    }
    
    public Optional<OrderResponse> getOrder(String orderId) {
        return orderRepository.findByOrderId(orderId)
            .map(order -> new OrderResponse(
                order.getOrderId(),
                order.getStatus(),
                order.getItems(),
                calculateTotal(order.getItems())
            ));
    }
    
    private Double calculateTotal(java.util.List<OrderItem> items) {
        // Calculate total from items
        return 0.0;
    }
}
```

#### Kubernetes Configuration
```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: ecommerce
---
# secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: ecommerce-secrets
  namespace: ecommerce
type: Opaque
stringData:
  postgres-username: admin
  postgres-password: secure-password-change-me
  rabbitmq-username: guest
  rabbitmq-password: guest-change-me
---
# configmaps.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: ecommerce-config
  namespace: ecommerce
data:
  DATABASE_HOST: postgres-service
  DATABASE_PORT: "5432"
  DATABASE_NAME: ecommerce
  RABBITMQ_HOST: rabbitmq-service
  RABBITMQ_PORT: "5672"
---
# postgres-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: ecommerce
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:15
        ports:
        - containerPort: 5432
        envFrom:
        - configMapRef:
            name: ecommerce-config
        env:
        - name: POSTGRES_USER
          valueFrom:
            secretKeyRef:
              name: ecommerce-secrets
              key: postgres-username
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: ecommerce-secrets
              key: postgres-password
        - name: POSTGRES_DB
          value: ecommerce
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
      volumes:
      - name: postgres-storage
        persistentVolumeClaim:
          claimName: postgres-pvc
---
# backend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
  namespace: ecommerce
spec:
  replicas: 3
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: ecommerce-backend:1.0.0
        ports:
        - containerPort: 8080
        envFrom:
        - configMapRef:
            name: ecommerce-config
        - secretRef:
            name: ecommerce-secrets
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
---
# services.yaml
apiVersion: v1
kind: Service
metadata:
  name: backend-service
  namespace: ecommerce
spec:
  selector:
    app: backend
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP
---
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
  namespace: ecommerce
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
---
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: ecommerce-ingress
  namespace: ecommerce
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: ecommerce.local
    http:
      paths:
      - path: /api
        pathType: Prefix
        backend:
          service:
            name: backend-service
            port:
              number: 80
      - path: /
        pathType: Prefix
        backend:
          service:
            name: frontend-service
            port:
              number: 80
```

### Build and Deployment
```bash
# Build all components
cd backend && mvn clean package -DskipTests
docker build -t ecommerce-backend:1.0.0 ./backend
docker build -t ecommerce-frontend:1.0.0 ./frontend

# Deploy to Kubernetes
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/configmaps.yaml
kubectl apply -f k8s/postgres-deployment.yaml
kubectl apply -f k8s/rabbitmq-deployment.yaml
kubectl apply -f k8s/backend-deployment.yaml
kubectl apply -f k8s/frontend-deployment.yaml
kubectl apply -f k8s/services.yaml
kubectl apply -f k8s/ingress.yaml

# Verify deployment
kubectl get all -n ecommerce
kubectl get ingress -n ecommerce

# Scale backend
kubectl scale deployment backend --replicas=5 -n ecommerce

# View logs
kubectl logs -l app=backend -n ecommerce -f

# Check health
curl http://localhost:8080/actuator/health
```

### Learning Outcomes
- Deploy multi-container applications to Kubernetes
- Configure persistent storage for databases
- Set up Kubernetes networking and Services
- Implement proper secrets and config management
- Configure Ingress for external access
- Set up monitoring and health checks