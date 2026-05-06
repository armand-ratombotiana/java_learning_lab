# Java Master Lab - Integration and Deployment Guide

## 🚀 Complete Integration and Deployment Framework

**Purpose**: Guide for integrating and deploying Java applications  
**Target Audience**: DevOps engineers, developers  
**Focus**: Seamless integration and reliable deployment  

---

## 🔗 SYSTEM INTEGRATION

### Microservices Integration

#### Service Discovery
```java
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

@Configuration
public class ServiceDiscoveryConfig {
    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

@Service
public class OrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    public User getUserFromUserService(Long userId) {
        List<ServiceInstance> instances = 
            discoveryClient.getInstances("user-service");
        
        if (instances.isEmpty()) {
            throw new ServiceUnavailableException("user-service");
        }
        
        ServiceInstance instance = instances.get(0);
        String url = instance.getUri() + "/api/users/" + userId;
        
        return restTemplate.getForObject(url, User.class);
    }
}
```

#### API Gateway
```java
@SpringBootApplication
@EnableZuulProxy
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

# application.yml
zuul:
  routes:
    user-service:
      path: /api/users/**
      serviceId: user-service
      stripPrefix: false
    order-service:
      path: /api/orders/**
      serviceId: order-service
      stripPrefix: false
  host:
    connect-timeout-millis: 5000
    socket-timeout-millis: 10000
```

#### Circuit Breaker Pattern
```java
@Service
public class ResilientOrderService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    public User getUser(Long userId) {
        return restTemplate.getForObject(
            "http://user-service/api/users/" + userId, 
            User.class
        );
    }
    
    public User getUserFallback(Long userId, Exception e) {
        // Return cached or default user
        return new User(userId, "Unknown User", "unknown@example.com");
    }
}

@Configuration
public class CircuitBreakerConfig {
    
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build();
        
        return CircuitBreakerRegistry.of(config);
    }
}
```

---

## 📦 CONTAINERIZATION

### Docker Configuration

```dockerfile
# Multi-stage build
FROM maven:3.8.1-openjdk-11 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/app.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD java -cp app.jar org.springframework.boot.loader.JarLauncher \
    -Dspring.boot.actuate.health.enabled=true

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/mydb
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=password
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
    depends_on:
      - db
      - redis
    networks:
      - app-network
    restart: unless-stopped

  db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=mydb
    volumes:
      - db-data:/var/lib/mysql
    networks:
      - app-network
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    networks:
      - app-network
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - app
    networks:
      - app-network
    restart: unless-stopped

volumes:
  db-data:

networks:
  app-network:
    driver: bridge
```

---

## ☸️ KUBERNETES DEPLOYMENT

### Kubernetes Manifests

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: java-app
  labels:
    app: java-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: java-app
  template:
    metadata:
      labels:
        app: java-app
    spec:
      containers:
      - name: java-app
        image: myregistry/java-app:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: database-url
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: app-secrets
              key: db-password
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5

---
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: java-app-service
spec:
  selector:
    app: java-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer

---
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
data:
  database-url: "jdbc:mysql://mysql-service:3306/mydb"
  log-level: "INFO"

---
# secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: app-secrets
type: Opaque
stringData:
  db-password: "secure-password"
  jwt-secret: "jwt-secret-key"
```

---

## 🔄 CI/CD PIPELINES

### GitHub Actions

```yaml
name: Java CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 11
      uses: actions/setup-java@v2
      with:
        java-version: '11'
        distribution: 'adopt'
    
    - name: Build with Maven
      run: mvn clean package
    
    - name: Run tests
      run: mvn test
    
    - name: Generate coverage report
      run: mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v2
      with:
        files: ./target/site/jacoco/jacoco.xml
    
    - name: SonarQube analysis
      run: |
        mvn sonar:sonar \
          -Dsonar.projectKey=java-master-lab \
          -Dsonar.host.url=${{ secrets.SONAR_HOST_URL }} \
          -Dsonar.login=${{ secrets.SONAR_TOKEN }}
    
    - name: Build Docker image
      run: docker build -t myregistry/java-app:${{ github.sha }} .
    
    - name: Push Docker image
      run: |
        echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
        docker push myregistry/java-app:${{ github.sha }}
    
    - name: Deploy to Kubernetes
      if: github.ref == 'refs/heads/main'
      run: |
        kubectl set image deployment/java-app \
          java-app=myregistry/java-app:${{ github.sha }} \
          --record
```

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'myregistry'
        DOCKER_IMAGE = 'java-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        
        stage('Code Quality') {
            steps {
                sh '''
                    mvn sonar:sonar \
                      -Dsonar.projectKey=java-master-lab \
                      -Dsonar.host.url=${SONAR_HOST_URL} \
                      -Dsonar.login=${SONAR_TOKEN}
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG} .'
            }
        }
        
        stage('Push Docker Image') {
            steps {
                sh '''
                    echo ${DOCKER_PASSWORD} | docker login -u ${DOCKER_USERNAME} --password-stdin
                    docker push ${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG}
                '''
            }
        }
        
        stage('Deploy to Kubernetes') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    kubectl set image deployment/java-app \
                      java-app=${DOCKER_REGISTRY}/${DOCKER_IMAGE}:${DOCKER_TAG} \
                      --record
                '''
            }
        }
    }
    
    post {
        always {
            junit 'target/surefire-reports/**/*.xml'
            publishHTML([
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'Code Coverage Report'
            ])
        }
        failure {
            emailext(
                subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build failed. Check console output at ${env.BUILD_URL}",
                to: "${env.CHANGE_AUTHOR_EMAIL}"
            )
        }
    }
}
```

---

## 📊 MONITORING & OBSERVABILITY

### Prometheus Metrics

```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistry meterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
}

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Order savedOrder = orderRepository.save(order);
            meterRegistry.counter("orders.created").increment();
            return ResponseEntity.ok(savedOrder);
        } finally {
            sample.stop(Timer.builder("order.creation.time")
                .description("Time to create order")
                .register(meterRegistry));
        }
    }
}
```

### ELK Stack Integration

```yaml
# logback-spring.xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProperty name="LOG_FILE" source="logging.file.name" defaultValue="logs/app.log"/>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>
    
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>logstash:5000</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE"/>
        <appender-ref ref="LOGSTASH"/>
    </root>
</configuration>
```

---

## 🔄 BLUE-GREEN DEPLOYMENT

### Blue-Green Strategy

```bash
#!/bin/bash

# Blue-Green Deployment Script

BLUE_VERSION="1.0.0"
GREEN_VERSION="1.1.0"
BLUE_PORT=8080
GREEN_PORT=8081

# Deploy green version
echo "Deploying green version..."
docker run -d -p $GREEN_PORT:8080 \
  --name java-app-green \
  myregistry/java-app:$GREEN_VERSION

# Wait for green to be ready
sleep 30
if ! curl -f http://localhost:$GREEN_PORT/actuator/health; then
    echo "Green deployment failed"
    docker stop java-app-green
    exit 1
fi

# Switch traffic to green
echo "Switching traffic to green..."
docker stop java-app-blue
docker rename java-app-green java-app-blue

echo "Deployment complete"
```

---

## 📄 Document Information

| Property | Value |
|----------|-------|
| **Document Type** | Integration and Deployment Guide |
| **Version** | 1.0 |
| **Created** | 2024 |
| **Status** | Active |
| **Focus** | Deployment |

---

**Java Master Lab - Integration and Deployment Guide**

*Complete Framework for Integration and Deployment*

**Status: Active | Focus: Deployment | Impact: Reliability**

---

*Deploy with confidence!* 🚀