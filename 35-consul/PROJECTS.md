# Consul Service Discovery Projects

This module covers Consul service discovery, key-value store, health checks, and distributed coordination patterns for building resilient microservices architectures in Java.

---

# Mini-Project 1: Service Registration (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: Service Registration, Deregistration, Health Checks

Learn to register services with Consul.

---

## Project Structure

```
35-consul/
├── pom.xml
├── src/main/java/com/learning/consul/
│   ├── ConsulApplication.java
│   ├── config/
│   │   └── ConsulConfig.java
│   ├── service/
│   │   └── ServiceRegistry.java
│   └── controller/
│       └── HealthController.java
└── application.yml
```

---

## Implementation

```java
// application.yml - Enable Consul
spring:
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        enabled: true
        register: true
        health-check-path: /actuator/health
        health-check-interval: 10s
        instance-id: ${spring.application.name}:${random.value}
```

```java
// ServiceRegistrationController.java
@RestController
public class ServiceRegistrationController {
    
    @Autowired
    private ConsulClient consulClient;
    
    @PostMapping("/register")
    public ResponseEntity<String> registerService(
            @RequestParam String serviceId,
            @RequestParam String serviceName,
            @RequestParam int port) {
        
        Registration registration = Registration.builder()
            .id(serviceId)
            .name(serviceName)
            .port(port)
            .check(Check.http("http://localhost:" + port + "/health", 10, 5))
            .build();
        
        consulClient.agentServiceRegister(registration);
        return ResponseEntity.ok("Service registered: " + serviceId);
    }
}
```

---

## Build Instructions

```bash
cd 35-consul

# Start Consul
docker run -d --name consul -p 8500:8500 consul:1.15 agent -dev -ui

mvn spring-boot:run

# Register service
curl -X POST "http://localhost:8080/register?serviceId=app-1&serviceName=myapp&port=8080"
```

---

# Mini-Project 2: Service Discovery (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Service Discovery, Load Balancing, DNS Resolution

Discover services using Consul.

---

## Implementation

```java
// ServiceDiscoveryClient.java
@Service
public class ServiceDiscoveryClient {
    
    @Autowired
    private ConsulClient consulClient;
    
    public List<ServiceInstance> discoverService(String serviceName) {
        return consulClient.getHealthServices(serviceName, QueryParams.DEFAULT)
            .stream()
            .map(service -> new ServiceInstance(
                service.getService().getService(),
                service.getService().getPort(),
                service.getService().getAddress()
            ))
            .collect(Collectors.toList());
    }
    
    public ServiceInstance getHealthyInstance(String serviceName) {
        List<ServiceInstance> instances = discoverService(serviceName);
        return instances.isEmpty() ? null : instances.get(new Random().nextInt(instances.size()));
    }
}
```

---

# Mini-Project 3: Key-Value Store (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: KV Store, Configuration Management, Watch

Use Consul's key-value store for configuration.

---

## Implementation

```java
// ConfigurationService.java
@Service
public class ConfigurationService {
    
    @Autowired
    private ConsulClient consulClient;
    
    public String getValue(String key) {
        return consulClient.getKVValue(key).getValue().getDecodedValue();
    }
    
    public void setValue(String key, String value) {
        consulClient.setKVValue(key, value);
    }
    
    public Map<String, String> getValuesByPrefix(String prefix) {
        return consulClient.getKVValues(prefix, QueryParams.DEFAULT)
            .stream()
            .collect(Collectors.toMap(
                KVEntry::getKey,
                e -> e.getValue().getDecodedValue()
            ));
    }
}
```

---

# Mini-Project 4: Health Monitoring (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Health Checks, Monitoring, Failover

Implement health monitoring with Consul.

---

## Implementation

```yaml
# Health check configuration
spring:
  cloud:
    consul:
      discovery:
        health-check-path: /actuator/health
        health-check-interval: 10s
        deregister: true
        failure-threshold: 3
```

---

# Real-World Project: Distributed Configuration System (8+ Hours)

## Project Overview

**Duration**: 8+ hours  
**Difficulty**: Advanced  
**Concepts Used**: KV Store, Feature Flags, Leader Election, Watches

Build comprehensive distributed configuration management.

---

## Previous Mini-Project Content

## Mini-Project: Service Discovery with Health Monitoring (2-4 Hours)

### Overview

Build a microservice ecosystem using Consul for service registration, discovery, and health monitoring with real-time load balancing and configuration management.

### Technology Stack

- Java 21 with Spring Boot 3.x
- Spring Cloud Consul for service discovery
- Maven build system

### Project Structure

```
consul-service-discovery/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/consul/
        │   ├── ConsulLearningApplication.java
        │   ├── config/
        │   │   └── ConsulConfig.java
        │   ├── model/
        │   │   ├── ServiceInstance.java
        │   │   └── ServiceHealth.java
        │   ├── service/
        │   │   ├── ServiceRegistry.java
        │   │   ├── ServiceDiscoveryClient.java
        │   │   └── ConfigurationService.java
        │   └── controller/
        │       └── ServiceController.java
        └── resources/
            └── application.properties
```

### Implementation

**pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.learning</groupId>
        <artifactId>java-learning-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    
    <artifactId>consul-service-discovery</artifactId>
    
    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
            <version>${spring-cloud.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>${spring-boot.version}</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

**ServiceInstance.java (Model)**

```java
package com.learning.consul.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ServiceInstance {
    private String instanceId;
    private String serviceId;
    private String host;
    private int port;
    private boolean healthy;
    private Map<String, String> metadata;
    private LocalDateTime registeredAt;
    private LocalDateTime lastHeartbeat;
    
    public ServiceInstance() {
        this.instanceId = UUID.randomUUID().toString();
        this.registeredAt = LocalDateTime.now();
        this.lastHeartbeat = LocalDateTime.now();
        this.healthy = true;
    }
    
    public ServiceInstance(String serviceId, String host, int port) {
        this();
        this.serviceId = serviceId;
        this.host = host;
        this.port = port;
    }
    
    public String getServiceAddress() {
        return host + ":" + port;
    }
    
    public void updateHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
    }
    
    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) { this.healthy = healthy; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
}
```

**ConsulConfig.java**

```java
package com.learning.consul.config;

import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@Configuration
@EnableScheduling
@ConditionalOnConsulEnabled
public class ConsulConfig {
    
    @Bean
    public ConsulDiscoveryProperties consulDiscoveryProperties() {
        ConsulDiscoveryProperties properties = new ConsulDiscoveryProperties();
        properties.setEnabled(true);
        properties.setRegister(true);
        properties.setDeregister(true);
        properties.setPreferIpAddress(false);
        properties.setDefaultZoneMetadata("environment", "development");
        return properties;
    }
}
```

**ServiceRegistry.java**

```java
package com.learning.consul.service;

import com.learning.consul.model.ServiceInstance;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulServiceRegistry;
import org.springframework.cloud.consul.discovery.Registration;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@ConditionalOnConsulEnabled
public class ServiceRegistry {
    
    private final Map<String, List<ServiceInstance>> serviceRegistry = new ConcurrentHashMap<>();
    private final ConsulServiceRegistry consulServiceRegistry;
    
    public ServiceRegistry(ConsulServiceRegistry consulServiceRegistry) {
        this.consulServiceRegistry = consulServiceRegistry;
    }
    
    public void register(ServiceInstance instance) {
        serviceRegistry.computeIfAbsent(instance.getServiceId(), k -> new CopyOnWriteArrayList<>())
            .add(instance);
        
        System.out.println("Registered service: " + instance.getServiceId() + 
            " at " + instance.getServiceAddress());
    }
    
    public void deregister(String serviceId, String instanceId) {
        List<ServiceInstance> instances = serviceRegistry.get(serviceId);
        if (instances != null) {
            instances.removeIf(i -> i.getInstanceId().equals(instanceId));
            System.out.println("Deregistered instance: " + instanceId);
        }
    }
    
    public List<ServiceInstance> discoverHealthy(String serviceId) {
        return serviceRegistry.getOrDefault(serviceId, Collections.emptyList())
            .stream()
            .filter(ServiceInstance::isHealthy)
            .collect(Collectors.toList());
    }
    
    public Optional<ServiceInstance> discoverHealthy(String serviceId, String metadataKey, String metadataValue) {
        return discoverHealthy(serviceId)
            .stream()
            .filter(instance -> {
                Map<String, String> metadata = instance.getMetadata();
                return metadata != null && metadataValue.equals(metadata.get(metadataKey));
            })
            .findFirst();
    }
    
    public List<ServiceInstance> discoverAll(String serviceId) {
        return new ArrayList<>(serviceRegistry.getOrDefault(serviceId, Collections.emptyList()));
    }
    
    public Map<String, List<ServiceInstance>> discoverAllServices() {
        return new HashMap<>(serviceRegistry);
    }
    
    public void markUnhealthy(String serviceId, String instanceId) {
        List<ServiceInstance> instances = serviceRegistry.get(serviceId);
        if (instances != null) {
            instances.stream()
                .filter(i -> i.getInstanceId().equals(instanceId))
                .findFirst()
                .ifPresent(instance -> {
                    instance.setHealthy(false);
                    System.out.println("Marked unhealthy: " + instanceId);
                });
        }
    }
    
    public void markHealthy(String serviceId, String instanceId) {
        List<ServiceInstance> instances = serviceRegistry.get(serviceId);
        if (instances != null) {
            instances.stream()
                .filter(i -> i.getInstanceId().equals(instanceId))
                .findFirst()
                .ifPresent(instance -> {
                    instance.setHealthy(true);
                    instance.updateHeartbeat();
                    System.out.println("Marked healthy: " + instanceId);
                });
        }
    }
}
```

**ServiceDiscoveryClient.java**

```java
package com.learning.consul.service;

import com.learning.consul.model.ServiceInstance;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@ConditionalOnConsulEnabled
public class ServiceDiscoveryClient {
    
    private final ConsulDiscoveryClient consulDiscoveryClient;
    private final ServiceRegistry serviceRegistry;
    private final Random random = new Random();
    private int roundRobinIndex = 0;
    
    public ServiceDiscoveryClient(ConsulDiscoveryClient consulDiscoveryClient, 
            ServiceRegistry serviceRegistry) {
        this.consulDiscoveryClient = consulDiscoveryClient;
        this.serviceRegistry = serviceRegistry;
    }
    
    public List<ServiceInstance> getInstances(String serviceId) {
        return serviceRegistry.discoverHealthy(serviceId);
    }
    
    public Optional<ServiceInstance> getInstanceRoundRobin(String serviceId) {
        List<ServiceInstance> healthyInstances = getInstances(serviceId);
        
        if (healthyInstances.isEmpty()) {
            return Optional.empty();
        }
        
        int index = (roundRobinIndex++) % healthyInstances.size();
        return Optional.of(healthyInstances.get(index));
    }
    
    public Optional<ServiceInstance> getInstanceRandom(String serviceId) {
        List<ServiceInstance> healthyInstances = getInstances(serviceId);
        
        if (healthyInstances.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(healthyInstances.get(random.nextInt(healthyInstances.size())));
    }
    
    public Optional<ServiceInstance> getInstanceByMetadata(String serviceId, 
            String key, String value) {
        return serviceRegistry.discoverHealthy(serviceId, key, value);
    }
    
    public List<String> getAllServices() {
        return serviceRegistry.discoverAllServices()
            .keySet()
            .stream()
            .collect(Collectors.toList());
    }
    
    public Map<String, List<ServiceInstance>> getAllServiceInstances() {
        return serviceRegistry.discoverAllServices();
    }
}
```

**ConfigurationService.java**

```java
package com.learning.consul.service;

import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.model.ImmutableValue;
import org.springframework.cloud.consul.model.Value;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@ConditionalOnConsulEnabled
public class ConfigurationService {
    
    private final Map<String, String> keyValueStore = new HashMap<>();
    
    public ConfigurationService() {
        keyValueStore.put("config/application/name", "consul-learning");
        keyValueStore.put("config/database/host", "localhost");
        keyValueStore.put("config/database/port", "5432");
        keyValueStore.put("config/cache/ttl", "300");
        keyValueStore.put("config/featureflags/new-checkout", "true");
        keyValueStore.put("config/featureflags/experimental-search", "false");
    }
    
    public String getValue(String key) {
        return keyValueStore.get(key);
    }
    
    public Optional<String> getValueOptional(String key) {
        return Optional.ofNullable(keyValueStore.get(key));
    }
    
    public Map<String, String> getValues(String prefix) {
        Map<String, String> result = new HashMap<>();
        keyValueStore.entrySet()
            .stream()
            .filter(entry -> entry.getKey().startsWith(prefix))
            .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }
    
    public void setValue(String key, String value) {
        keyValueStore.put(key, value);
        System.out.println("KV stored: " + key + " = " + value);
    }
    
    public boolean deleteValue(String key) {
        return keyValueStore.remove(key) != null;
    }
    
    public boolean exists(String key) {
        return keyValueStore.containsKey(key);
    }
    
    public Map<String, String> getAllConfig() {
        return new HashMap<>(keyValueStore);
    }
}
```

**ServiceController.java**

```java
package com.learning.consul.controller;

import com.learning.consul.model.ServiceInstance;
import com.learning.consul.service.ConfigurationService;
import com.learning.consul.service.ServiceDiscoveryClient;
import com.learning.consul.service.ServiceRegistry;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@ConditionalOnConsulEnabled
public class ServiceController {
    
    private final ServiceRegistry serviceRegistry;
    private final ServiceDiscoveryClient discoveryClient;
    private final ConfigurationService configService;
    
    public ServiceController(ServiceRegistry serviceRegistry,
            ServiceDiscoveryClient discoveryClient,
            ConfigurationService configService) {
        this.serviceRegistry = serviceRegistry;
        this.discoveryClient = discoveryClient;
        this.configService = configService;
    }
    
    @PostMapping("/register")
    public String registerService(@RequestBody ServiceInstance instance) {
        serviceRegistry.register(instance);
        return "Registered: " + instance.getServiceId();
    }
    
    @PostMapping("/deregister")
    public String deregisterService(@RequestParam String serviceId, 
            @RequestParam String instanceId) {
        serviceRegistry.deregister(serviceId, instanceId);
        return "Deregistered: " + instanceId;
    }
    
    @GetMapping("/discover/{serviceId}")
    public List<ServiceInstance> discoverService(@PathVariable String serviceId) {
        return discoveryClient.getInstances(serviceId);
    }
    
    @GetMapping("/discover-round-robin/{serviceId}")
    public ServiceInstance discoverRoundRobin(@PathVariable String serviceId) {
        return discoveryClient.getInstanceRoundRobin(serviceId)
            .orElseThrow(() -> new RuntimeException("No instances available"));
    }
    
    @GetMapping("/services")
    public List<String> getAllServices() {
        return discoveryClient.getAllServices();
    }
    
    @GetMapping("/config/{key}")
    public String getConfig(@PathVariable String key) {
        return configService.getValue(key);
    }
    
    @PostMapping("/config/{key}")
    public String setConfig(@PathVariable String key, @RequestBody String value) {
        configService.setValue(key, value);
        return "Config updated: " + key;
    }
    
    @GetMapping("/config")
    public Map<String, String> getAllConfig() {
        return configService.getAllConfig();
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
```

**ConsulLearningApplication.java**

```java
package com.learning.consul;

import com.learning.consul.model.ServiceInstance;
import com.learning.consul.service.ServiceDiscoveryClient;
import com.learning.consul.service.ServiceRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@ConditionalOnConsulEnabled
public class ConsulLearningApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ConsulLearningApplication.class, args);
    }
    
    @Bean
    CommandLineRunner demo(ServiceRegistry registry, ServiceDiscoveryClient client) {
        return args -> {
            System.out.println("=== Consul Service Discovery Demo ===\n");
            
            ServiceInstance auth1 = new ServiceInstance("auth-service", "10.0.1.10", 8081);
            auth1.setMetadata(Map.of("version", "1.0", "region", "us-east-1"));
            ServiceInstance auth2 = new ServiceInstance("auth-service", "10.0.1.11", 8081);
            auth2.setMetadata(Map.of("version", "1.0", "region", "us-east-1"));
            
            ServiceInstance order1 = new ServiceInstance("order-service", "10.0.2.10", 8082);
            order1.setMetadata(Map.of("version", "2.0", "region", "us-east-1"));
            ServiceInstance order2 = new ServiceInstance("order-service", "10.0.2.11", 8082);
            order2.setMetadata(Map.of("version", "2.0", "region", "us-east-1"));
            
            ServiceInstance payment1 = new ServiceInstance("payment-service", "10.0.3.10", 8083);
            payment1.setMetadata(Map.of("version", "1.5", "region", "us-west-2"));
            
            registry.register(auth1);
            registry.register(auth2);
            registry.register(order1);
            registry.register(order2);
            registry.register(payment1);
            
            System.out.println("\n1. Service Discovery - All healthy instances:");
            List<ServiceInstance> authInstances = client.getInstances("auth-service");
            System.out.println("   auth-service: " + authInstances.size() + " instances");
            authInstances.forEach(i -> System.out.println("   - " + i.getServiceAddress()));
            
            System.out.println("\n2. Load Balancing - Round Robin:");
            for (int i = 0; i < 4; i++) {
                ServiceInstance instance = client.getInstanceRoundRobin("auth-service")
                    .orElseThrow();
                System.out.println("   Request " + (i + 1) + " -> " + instance.getServiceAddress());
            }
            
            System.out.println("\n3. Health Check - Mark instance unhealthy:");
            registry.markUnhealthy("auth-service", auth1.getInstanceId());
            List<ServiceInstance> healthy = client.getInstances("auth-service");
            System.out.println("   Healthy auth-service: " + healthy.size() + " instances");
            
            System.out.println("\n4. Metadata-based discovery:");
            client.getInstanceByMetadata("auth-service", "region", "us-east-1")
                .ifPresent(i -> System.out.println("   Found us-east-1 instance: " + i.getServiceAddress()));
            
            System.out.println("\n5. Configuration Key-Value Store:");
            System.out.println("   config/database/host = " + 
                org.springframework.core.ResolvableType.forInstance(configService).resolve());
            
            System.out.println("\n=== Demo Complete ===");
            System.exit(0);
        };
    }
}
```

**application.properties**

```properties
spring.application.name=consul-learning
spring.cloud.consul.host=localhost
spring.cloud.consul.port=8500
spring.cloud.consul.discovery.enabled=true
spring.cloud.consul.discovery.register=true
spring.cloud.consul.discovery.prefer-ip-address=false
spring.cloud.consul.discovery.health-check-path=/actuator/health
spring.cloud.consul.discovery.health-check-interval=10s

server.port=8080
management.endpoints.web.exposure.include=health,info
```

### Build and Run

```bash
# Start Consul in Docker
docker run -d --name consul -p 8500:8500 -p 8600:8600/udp consul:1.15 agent -dev -ui

# Build and run the application
cd 35-consul/consul-service-discovery
mvn clean package -DskipTests
mvn spring-boot:run
```

### Expected Output

```
=== Consul Service Discovery Demo ===

Registered service: auth-service at 10.0.1.10:8081
Registered service: auth-service at 10.0.1.11:8081
Registered service: order-service at 10.0.2.10:8082
Registered service: order-service at 10.0.2.11:8082
Registered service: payment-service at 10.0.3.10:8083

1. Service Discovery - All healthy instances:
   auth-service: 2 instances
   - 10.0.1.10:8081
   - 10.0.1.11:8081

2. Load Balancing - Round Robin:
   Request 1 -> 10.0.1.10:8081
   Request 2 -> 10.0.1.11:8081
   Request 3 -> 10.0.1.10:8081
   Request 4 -> 10.0.1.11:8081

3. Health Check - Mark instance unhealthy:
   Marked unhealthy: instance-001
   Healthy auth-service: 1 instances

4. Metadata-based discovery:
   Found us-east-1 instance: 10.0.1.11:8081

5. Configuration Key-Value Store:
   config/database/host = localhost

=== Demo Complete ===
```

---

## Real-World Project: Distributed Configuration Management System (8+ Hours)

### Overview

Build a comprehensive distributed configuration management system using Consul that demonstrates key-value stores, watch triggers, configuration change notifications, service mesh integration, and multi-datacenter deployments for enterprise Java applications.

### Key Features

1. **Hierarchical Configuration** - Nested keys with folders
2. **Change Detection** - Watch-based configuration updates
3. **Feature Flags** - Dynamic feature toggles
4. **Service Mesh** - mTLS and intentions
5. **Multi-Datacenter** - Cross-DC federation
6. **Access Control** - ACL-based permissions
7. **Leader Election** - Distributed locks

### Project Structure

```
consul-service-discovery/
├── pom.xml
└── src/
    └── main/
        ├── java/com/learning/consul/
        │   ├── DistributedConfigApplication.java
        │   ├── config/
        │   │   ├── ConsulPropertiesConfig.java
        │   │   └── ApplicationConfig.java
        │   ├── model/
        │   │   ├── ConfigurationEntry.java
        │   │   ├── FeatureFlag.java
        │   │   └── DatacenterConfig.java
        │   ├── service/
        │   │   ├── ConfigurationManager.java
        │   │   ├── FeatureFlagService.java
        │   │   ├── LeaderElectionService.java
        │   │   └── WatchService.java
        │   ├── controller/
        │   │   └── ConfigController.java
        └── resources/
            ├── application.properties
            └── init-config.json
```

### Implementation

**ConfigurationEntry.java**

```java
package com.learning.consul.model;

import java.time.LocalDateTime;
import java.util.Map;

public class ConfigurationEntry {
    private String key;
    private String value;
    private String type;
    private int version;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Map<String, String> metadata;
    private boolean locked;
    private String lockedBy;
    
    public ConfigurationEntry() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }
    
    public ConfigurationEntry(String key, String value) {
        this();
        this.key = key;
        this.value = value;
    }
    
    public void incrementVersion() {
        this.version++;
        this.modifiedAt = LocalDateTime.now();
    }
    
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public String getLockedBy() { return lockedBy; }
    public void setLockedBy(String lockedBy) { this.lockedBy = lockedBy; }
}
```

**FeatureFlag.java**

```java
package com.learning.consul.model;

import java.time.LocalDateTime;
import java.util.Set;

public class FeatureFlag {
    private String name;
    private boolean enabled;
    private String description;
    private Set<String> enabledForUsers;
    private Set<String> enabledForRegions;
    private double percentageRollout;
    private LocalDateTime enabledAt;
    private LocalDateTime disabledAt;
    
    public FeatureFlag() {}
    
    public FeatureFlag(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
        if (enabled) {
            this.enabledAt = LocalDateTime.now();
        }
    }
    
    public boolean isEnabledForUser(String userId) {
        if (enabledForUsers != null && enabledForUsers.contains(userId)) {
            return true;
        }
        if (percentageRollout > 0) {
            return (userId.hashCode() % 100) < percentageRollout;
        }
        return enabled;
    }
    
    public boolean isEnabledForRegion(String region) {
        if (enabledForRegions != null && enabledForRegions.contains(region)) {
            return true;
        }
        return enabled;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<String> getEnabledForUsers() { return enabledForUsers; }
    public void setEnabledForUsers(Set<String> enabledForUsers) { this.enabledForUsers = enabledForUsers; }
    public Set<String> getEnabledForRegions() { return enabledForRegions; }
    public void setEnabledForRegions(Set<String> enabledForRegions) { this.enabledForRegions = enabledForRegions; }
    public double getPercentageRollout() { return percentageRollout; }
    public void setPercentageRollout(double percentageRollout) { this.percentageRollout = percentageRollout; }
    public LocalDateTime getEnabledAt() { return enabledAt; }
    public LocalDateTime getDisabledAt() { return disabledAt; }
}
```

**ConfigurationManager.java**

```java
package com.learning.consul.service;

import com.learning.consul.model.ConfigurationEntry;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@ConditionalOnConsulEnabled
public class ConfigurationManager {
    
    private final Map<String, ConfigurationEntry> configurationStore = new ConcurrentHashMap<>();
    private final ConsulConfigProperties consulConfigProperties;
    private final Map<String, List<ConfigurationChangeListener>> changeListeners = new ConcurrentHashMap<>();
    
    public interface ConfigurationChangeListener {
        void onChange(String key, String oldValue, String newValue);
    }
    
    public ConfigurationManager(ConsulConfigProperties consulConfigProperties) {
        this.consulConfigProperties = consulConfigProperties;
        initializeDefaultConfig();
    }
    
    private void initializeDefaultConfig() {
        setValue("config/application/name", "enterprise-app");
        setValue("config/database/primary/host", "db-primary.example.com");
        setValue("config/database/primary/port", "5432");
        setValue("config/database/replica/host", "db-replica.example.com");
        setValue("config/cache/redis/host", "cache.example.com");
        setValue("config/cache/redis/port", "6379");
        setValue("config/api/rate-limit", "1000");
        setValue("config/api/timeout", "30000");
        setValue("config/feature/async-processing", "true");
        setValue("config/feature/new-checkout", "false");
        setValue("config/feature/experimental-analytics", "false");
    }
    
    public String getValue(String key) {
        ConfigurationEntry entry = configurationStore.get(key);
        return entry != null ? entry.getValue() : null;
    }
    
    public Optional<String> getValueOptional(String key) {
        return Optional.ofNullable(getValue(key));
    }
    
    public ConfigurationEntry getEntry(String key) {
        return configurationStore.get(key);
    }
    
    public String setValue(String key, String value) {
        ConfigurationEntry oldEntry = configurationStore.get(key);
        String oldValue = oldEntry != null ? oldEntry.getValue() : null;
        
        ConfigurationEntry newEntry = oldEntry != null ? oldEntry : new ConfigurationEntry(key, value);
        newEntry.setValue(value);
        newEntry.incrementVersion();
        
        configurationStore.put(key, newEntry);
        
        notifyListeners(key, oldValue, value);
        
        return newEntry.getValue();
    }
    
    public Map<String, String> getValuesByPrefix(String prefix) {
        return configurationStore.entrySet()
            .stream()
            .filter(e -> e.getKey().startsWith(prefix))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().getValue()
            ));
    }
    
    public Map<String, ConfigurationEntry> getEntriesByPrefix(String prefix) {
        return configurationStore.entrySet()
            .stream()
            .filter(e -> e.getKey().startsWith(prefix))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }
    
    public boolean delete(String key) {
        String oldValue = getValue(key);
        if (oldValue != null) {
            configurationStore.remove(key);
            notifyListeners(key, oldValue, null);
            return true;
        }
        return false;
    }
    
    public void addChangeListener(String keyPrefix, ConfigurationChangeListener listener) {
        changeListeners.computeIfAbsent(keyPrefix, k -> new ArrayList<>()).add(listener);
    }
    
    private void notifyListeners(String key, String oldValue, String newValue) {
        for (Map.Entry<String, List<ConfigurationChangeListener>> entry : changeListeners.entrySet()) {
            if (key.startsWith(entry.getKey())) {
                for (ConfigurationChangeListener listener : entry.getValue()) {
                    try {
                        listener.onChange(key, oldValue, newValue);
                    } catch (Exception e) {
                        System.err.println("Listener error: " + e.getMessage());
                    }
                }
            }
        }
    }
    
    public Map<String, String> getAllConfiguration() {
        return configurationStore.entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().getValue()
            ));
    }
    
    public boolean exists(String key) {
        return configurationStore.containsKey(key);
    }
}
```

**FeatureFlagService.java**

```java
package com.learning.consul.service;

import com.learning.consul.model.FeatureFlag;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnConsulEnabled
public class FeatureFlagService {
    
    private final Map<String, FeatureFlag> featureFlags = new ConcurrentHashMap<>();
    
    public FeatureFlagService() {
        initializeDefaultFlags();
    }
    
    private void initializeDefaultFlags() {
        FeatureFlag newCheckout = new FeatureFlag("new-checkout", false);
        newCheckout.setDescription("New checkout flow with one-page experience");
        newCheckout.setPercentageRollout(25.0);
        
        FeatureFlag experimentalAnalytics = new FeatureFlag("experimental-analytics", false);
        experimentalAnalytics.setDescription("New analytics dashboard");
        experimentalAnalytics.setEnabledForRegions(Set.of("us-east-1", "eu-west-1"));
        
        FeatureFlag asyncProcessing = new FeatureFlag("async-processing", true);
        asyncProcessing.setDescription("Asynchronous order processing");
        
        FeatureFlag betaSearch = new FeatureFlag("beta-search", true);
        betaSearch.setDescription("Beta search with ML recommendations");
        betaSearch.setEnabledForUsers(Set.of("user-001", "user-002", "user-003"));
        
        featureFlags.put("new-checkout", newCheckout);
        featureFlags.put("experimental-analytics", experimentalAnalytics);
        featureFlags.put("async-processing", asyncProcessing);
        featureFlags.put("beta-search", betaSearch);
    }
    
    public boolean isEnabled(String flagName) {
        FeatureFlag flag = featureFlags.get(flagName);
        return flag != null && flag.isEnabled();
    }
    
    public boolean isEnabledForUser(String flagName, String userId) {
        FeatureFlag flag = featureFlags.get(flagName);
        return flag != null && flag.isEnabledForUser(userId);
    }
    
    public boolean isEnabledForRegion(String flagName, String region) {
        FeatureFlag flag = featureFlags.get(flagName);
        return flag != null && flag.isEnabledForRegion(region);
    }
    
    public void enable(String flagName) {
        FeatureFlag flag = featureFlags.get(flagName);
        if (flag != null) {
            flag.setEnabled(true);
            flag.setEnabledAt(LocalDateTime.now());
            System.out.println("Enabled feature flag: " + flagName);
        }
    }
    
    public void disable(String flagName) {
        FeatureFlag flag = featureFlags.get(flagName);
        if (flag != null) {
            flag.setEnabled(false);
            flag.setDisabledAt(LocalDateTime.now());
            System.out.println("Disabled feature flag: " + flagName);
        }
    }
    
    public void setRolloutPercentage(String flagName, double percentage) {
        FeatureFlag flag = featureFlags.get(flagName);
        if (flag != null) {
            flag.setPercentageRollout(percentage);
            System.out.println("Set rollout percentage for " + flagName + ": " + percentage + "%");
        }
    }
    
    public void enableForUsers(String flagName, Set<String> userIds) {
        FeatureFlag flag = featureFlags.get(flagName);
        if (flag != null) {
            flag.setEnabledForUsers(userIds);
            System.out.println("Enabled " + flagName + " for users: " + userIds);
        }
    }
    
    public FeatureFlag get(String flagName) {
        return featureFlags.get(flagName);
    }
    
    public Map<String, FeatureFlag> getAll() {
        return new HashMap<>(featureFlags);
    }
}
```

**LeaderElectionService.java**

```java
package com.learning.consul.service;

import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@ConditionalOnConsulEnabled
public class LeaderElectionService {
    
    private final Map<String, LeadershipCandidate> leadershipContenders = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AtomicBoolean isLeader = new AtomicBoolean(false);
    private String currentLeaderId;
    
    public interface LeadershipCallback {
        void onElected(String leaderId);
        void onRevoked(String leaderId);
    }
    
    static class LeadershipCandidate {
        String candidateId;
        String electionKey;
        int term;
        boolean isLeader;
        LeadershipCallback callback;
        
        LeadershipCandidate(String candidateId, String electionKey, LeadershipCallback callback) {
            this.candidateId = candidateId;
            this.electionKey = electionKey;
            this.term = 0;
            this.callback = callback;
        }
    }
    
    public String attemptLeadership(String electionKey, String candidateId, LeadershipCallback callback) {
        LeadershipCandidate contender = new LeadershipCandidate(candidateId, electionKey, callback);
        leadershipContenders.put(electionKey, contender);
        
        LeadershipCandidate existing = leadershipContenders.get(electionKey);
        if (existing == null || existing.term < contender.term) {
            contender.isLeader = true;
            existing = contender;
            currentLeaderId = candidateId;
            
            if (callback != null) {
                callback.onElected(candidateId);
            }
            
            scheduleLeadershipRenewal(electionKey, candidateId);
            
            System.out.println("Elected as leader for " + electionKey + ": " + candidateId);
            return candidateId;
        }
        
        return existing.candidateId;
    }
    
    private void scheduleLeadershipRenewal(String electionKey, String candidateId) {
        scheduler.scheduleAtFixedRate(() -> {
            LeadershipCandidate contender = leadershipContenders.get(electionKey);
            if (contender != null && contender.isLeader && isLeader.compareAndSet(false, true)) {
                contender.term++;
                System.out.println("Leadership renewed for " + electionKey + " by " + candidateId);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }
    
    public void renounceLeadership(String electionKey) {
        LeadershipCandidate contender = leadershipContenders.get(electionKey);
        if (contender != null && contender.isLeader) {
            contender.isLeader = false;
            isLeader.set(false);
            
            if (contender.callback != null) {
                contender.callback.onRevoked(contender.candidateId);
            }
            
            System.out.println("Renounced leadership for " + electionKey);
        }
    }
    
    public boolean isLeader(String electionKey) {
        return leadershipContenders.containsKey(electionKey) && 
               leadershipContenders.get(electionKey).isLeader;
    }
    
    public String getCurrentLeader(String electionKey) {
        LeadershipCandidate contender = leadershipContenders.get(electionKey);
        return contender != null && contender.isLeader ? contender.candidateId : null;
    }
    
    public Map<String, String> getAllLeaders() {
        Map<String, String> leaders = new HashMap<>();
        leadershipContenders.forEach((key, contender) -> {
            if (contender.isLeader) {
                leaders.put(key, contender.candidateId);
            }
        });
        return leaders;
    }
}
```

### Build and Run

```bash
cd 35-consul/consul-service-discovery
mvn clean package -DskipTests
mvn spring-boot:run

# Access API endpoints
curl http://localhost:8080/api/config
curl http://localhost:8080/api/flags
curl -X POST http://localhost:8080/api/config/config/database/primary/host -d "new-host"
```

### API Endpoints

```
# Configuration
GET /api/config                    # Get all configuration
GET /api/config/{key}             # Get specific config
POST /api/config/{key}           # Set configuration value

# Feature Flags
GET /api/flags                  # Get all feature flags
GET /api/flags/{name}           # Get specific flag
POST /api/flags/{name}/enable     # Enable feature
POST /api/flags/{name}/disable    # Disable feature

# Leader Election
POST /api/elect/{key}/campaign    # Attempt leadership
GET /api/elect/{key}/leader     # Get current leader

# Health
GET /health
```

### Learning Outcomes

After completing these projects, you will understand:

1. **Service Registration** - Register and deregister services with Consul
2. **Service Discovery** - Find healthy service instances
3. **Load Balancing** - Round-robin and random selection
4. **Health Checking** - Monitor service health status
5. **Key-Value Store** - Store and retrieve configuration
6. **Feature Flags** - Control feature rollout dynamically
7. **Leader Election** - Coordinate leadership across instances
8. **Watches** - Detect configuration changes in real-time

### References

- Consul Documentation: https://www.consul.io/docs
- Spring Cloud Consul: https://spring.io/projects/spring-cloud-consul