# 09 - Service Discovery

## Overview

Service Discovery enables microservices to find and communicate with each other dynamically. This lab covers service registries, Eureka, Consul, Kubernetes DNS, and client-side discovery with Java implementations.

## Learning Objectives
- Understand service registration and discovery
- Implement Eureka server and clients
- Use Consul for service discovery
- Understand Kubernetes DNS-based discovery

## Quick Start
```java
// Eureka Server
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

// Eureka Client (service)
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}

// Client-side discovery
@RestController
public class OrderController {
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/users-from-service")
    public List<User> getUsers() {
        List<ServiceInstance> instances = discoveryClient.getInstances("user-service");
        String uri = instances.get(0).getUri().toString();
        return new RestTemplate().getForObject(uri + "/api/users", List.class);
    }
}
```
