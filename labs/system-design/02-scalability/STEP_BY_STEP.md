# Scalability - STEP BY STEP

## Making a Service Horizontally Scalable

### Step 1: Remove Server-Side Session State
```java
// Before: session state in memory
httpSession.setAttribute("cart", cart);

// After: state in Redis or JWT
@RedisHash("cart")
public class Cart { /* ... */ }
```

### Step 2: Make All Config External
```java
// Before: hardcoded
db.url = "localhost:3306/mydb";

// After: environment variable
db.url = ${DATABASE_URL};
```

### Step 3: Run Multiple Instances
```bash
# Start 3 instances on different ports
java -jar app.jar --server.port=8081
java -jar app.jar --server.port=8082
java -jar app.jar --server.port=8083
```

### Step 4: Add Load Balancer
```nginx
# nginx.conf
upstream backend {
    server localhost:8081;
    server localhost:8082;
    server localhost:8083;
}
```

### Step 5: Implement Health Checks
```java
@GetMapping("/health")
public ResponseEntity<String> health() {
    if (db.isHealthy() && cache.isHealthy()) {
        return ResponseEntity.ok("OK");
    }
    return ResponseEntity.status(503).body("Unhealthy");
}
```

## Setting Up Database Read Replicas

### Step 1: Configure Master Database
```yaml
# application-master.yml
spring.datasource.url: jdbc:postgresql://master:5432/db
spring.datasource.hikari.maximum-pool-size: 10
```

### Step 2: Configure Replica
```yaml
# application-replica.yml
spring.datasource.url: jdbc:postgresql://replica-1:5432/db
spring.datasource.hikari.maximum-pool-size: 50
```

### Step 3: Implement Read/Write Routing
```java
@Transactional(readOnly = true)  // → replica
public List<Product> findAll() { /* ... */ }

@Transactional  // → master
public Product save(Product p) { /* ... */ }
```

## Implementing Caching

### Step 1: Add Cache Dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

### Step 2: Enable Caching
```java
@EnableCaching
@SpringBootApplication
public class Application { /* ... */ }
```

### Step 3: Add Cache Annotations
```java
@Cacheable("products")  // cache result
public Product getProduct(String id) { /* ... */ }

@CacheEvict("products")  // invalidate on update
public Product updateProduct(Product p) { /* ... */ }
```

## Configuring Auto-Scaling

### Step 1: Package as Container
```dockerfile
FROM eclipse-temurin:17
COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Step 2: Deploy to Kubernetes
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
spec:
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```
