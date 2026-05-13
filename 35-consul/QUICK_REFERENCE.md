# Quick Reference: Consul

<div align="center">

![Module](https://img.shields.io/badge/Module-35-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Consul-green?style=for-the-badge)

**Quick lookup guide for Consul service mesh**

</div>

---

## 📋 Core Concepts

| Concept | Description |
|---------|-------------|
| **Agent** | Consul daemon running on each node |
| **Catalog** | Registry of services and nodes |
| **Health Checks** | Service health monitoring |
| **KV Store** | Distributed key-value |
| **Connect** | Service mesh with mTLS |

---

## 🔑 Key Commands

### CLI
```bash
# Start agent in dev mode
consul agent -dev

# Register service
consul services register -name=my-service -port=8080

# Health check
consul members

# KV operations
consul kv put config/app/db_url "postgres://localhost:5432"
consul kv get config/app/db_url

# Query service
curl http://localhost:8500/v1/catalog/service/my-service
```

---

## 📊 Java Client (Spring Cloud Consul)

### Configuration
```java
@Configuration
@EnableDiscoveryClient
public class ConsulConfig {
    @Bean
    public ConsulProperties consulProperties() {
        ConsulProperties props = new ConsulProperties();
        props.setHost("localhost");
        props.setPort(8500);
        return props;
    }
}
```

### Service Registration
```yaml
spring:
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        service-name: my-service
        health-check-path: /actuator/health
        health-check-interval: 10s
        prefer-ip-address: true
```

### Service Discovery
```java
@Service
public class MyService {
    @Autowired
    private DiscoveryClient discoveryClient;
    
    public void callService() {
        List<ServiceInstance> instances = discoveryClient.getInstances("other-service");
        ServiceInstance instance = instances.get(0);
        String url = instance.getUri() + "/api/endpoint";
    }
}
```

---

## 💻 Load Balancing

```java
@Configuration
public class LoadBalancerConfig {
    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier(
            DiscoveryClientOptionalConfiguration config) {
        return ServiceInstanceListSupplier.builder()
            .withDiscoveryClient(config.getDiscoveryClient())
            .withZoneAffinity()
            .build();
    }
}

// Usage with RestTemplate
@LoadBalanced
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}

// Call: http://other-service/api/endpoint
```

---

## 📊 Health Checks

```yaml
spring:
  cloud:
    consul:
      discovery:
        health-check-path: /health
        health-check-interval: 10s
        register-health-check: true
```

### Types
- HTTP: `/health`
- TCP: `localhost:8080`
- TTL: 30s (service must heartbeat)
- Script: `/bin/check.sh`

---

## ✅ Best Practices

- Use ACL tokens for production
- Configure appropriate health check intervals
- Use prepared queries for load balancing
- Enable mTLS with Consul Connect

### ❌ DON'T
- Don't use dev mode in production
- Don't skip health checks

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>