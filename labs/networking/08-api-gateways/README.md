# 08 - API Gateways

## Overview

API Gateways provide a single entry point for microservices. This lab covers gateway patterns, Spring Cloud Gateway, routing, rate limiting, circuit breakers, and security with Java implementations.

## Learning Objectives
- Understand API Gateway patterns and use cases
- Configure Spring Cloud Gateway routing
- Implement rate limiting and circuit breakers
- Handle authentication and security at the gateway

## Quick Start
```java
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

// application.yml
// spring:
//   cloud:
//     gateway:
//       routes:
//         - id: user-service
//           uri: lb://user-service
//           predicates:
//             - Path=/api/users/**
//           filters:
//             - StripPrefix=1
//             - name: RequestRateLimiter
//               args:
//                 redis-rate-limiter.replenishRate: 100
```
