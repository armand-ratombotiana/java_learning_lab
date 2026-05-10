package com.learning.lab.module19;

import java.util.*;
import java.time.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 19: Microservices ===");
        microservicesBasics();
        serviceDiscovery();
        apiGateway();
        interServiceCommunication();
        circuitBreakerPattern();
        distributedTracing();
        serviceMesh();
        eventDrivenArchitecture();
    }

    static void microservicesBasics() {
        System.out.println("\n--- Microservices Basics ---");
        System.out.println("Principles:");
        System.out.println("  - Single Responsibility");
        System.out.println("  - Decentralized governance");
        System.out.println("  - Infrastructure automation");
        System.out.println("  - Design for failure");
        System.out.println("  - Evolutionary design");
        
        System.out.println("\nBenefits:");
        System.out.println("  - Independent deployment");
        System.out.println("  - Technology diversity");
        System.out.println("  - Scalability");
        System.out.println("  - Fault isolation");
        System.out.println("  - Team autonomy");
        
        System.out.println("\nChallenges:");
        System.out.println("  - Distributed data management");
        System.out.println("  - Service discovery");
        System.out.println("  - Network latency");
        System.out.println("  - Debugging complexity");
    }

    static void serviceDiscovery() {
        System.out.println("\n--- Service Discovery ---");
        System.out.println("Client-side discovery:");
        System.out.println("  - Service registers with registry");
        System.out.println("  - Client queries registry");
        System.out.println("  - Examples: Eureka, Consul");
        
        System.out.println("\nServer-side discovery:");
        System.out.println("  - Load balancer queries registry");
        System.out.println("  - Examples: AWS ALB, NGINX");
        
        System.out.println("\nRegistration:");
        System.out.println("  - Self-registration: Service registers itself");
        System.out.println("  - Third-party: External agent handles registration");
        
        System.out.println("\nConsul features:");
        System.out.println("  - Service catalog");
        System.out.println("  - Health checking");
        System.out.println("  - Key-value store");
        System.out.println("  - Multi-datacenter support");
    }

    static void apiGateway() {
        System.out.println("\n--- API Gateway ---");
        System.out.println("Responsibilities:");
        System.out.println("  - Request routing");
        System.out.println("  - Load balancing");
        System.out.println("  - Authentication/Authorization");
        System.out.println("  - Rate limiting");
        System.out.println("  - Request/Response transformation");
        System.out.println("  - Logging and monitoring");
        
        System.out.println("\nPopular gateways:");
        System.out.println("  - Spring Cloud Gateway");
        System.out.println("  - Kong");
        System.out.println("  - NGINX");
        System.out.println("  - AWS API Gateway");
        
        System.out.println("\nSpring Cloud Gateway example:");
        System.out.println("spring:");
        System.out.println("  cloud:");
        System.out.println("    gateway:");
        System.out.println("      routes:");
        System.out.println("        - id: user-service");
        System.out.println("          uri: lb://user-service");
        System.out.println("          predicates:");
        System.out.println("            - Path=/users/**");
    }

    static void interServiceCommunication() {
        System.out.println("\n--- Service Communication ---");
        System.out.println("Synchronous:");
        System.out.println("  - REST over HTTP");
        System.out.println("  - gRPC (Protocol Buffers)");
        System.out.println("  - GraphQL");
        
        System.out.println("\nAsynchronous:");
        System.out.println("  - Message queues (Kafka, RabbitMQ)");
        System.out.println("  - Event-driven");
        System.out.println("  - Async REST (WebFlux)");
        
        System.out.println("\nREST example:");
        System.out.println("  RestTemplate restTemplate = new RestTemplate();");
        System.out.println("  User user = restTemplate.getForObject(");
        System.out.println("    \"http://user-service/users/1\", User.class);");
        
        System.out.println("\nWebClient example:");
        System.out.println("  WebClient.builder()");
        System.out.println("    .baseUrl(\"http://user-service\")");
        System.out.println("    .build()");
        System.out.println("    .get().uri(\"/users/{id}\", id)");
        System.out.println("    .retrieve().bodyToMono(User.class);");
    }

    static void circuitBreakerPattern() {
        System.out.println("\n--- Circuit Breaker Pattern ---");
        System.out.println("States:");
        System.out.println("  CLOSED - Normal operation");
        System.out.println("  OPEN - Failing, reject requests");
        System.out.println("  HALF_OPEN - Test recovery");
        
        System.out.println("\nResilience4j configuration:");
        System.out.println("  resilience4j.circuitbreaker:");
        System.out.println("    configs:");
        System.out.println("      default:");
        System.out.println("        slidingWindowSize: 10");
        System.out.println("        failureRateThreshold: 50");
        System.out.println("        waitDurationInOpenState: 60s");
        
        System.out.println("\nUsage:");
        System.out.println("  @CircuitBreaker(name = \"userService\")");
        System.out.println("  public User getUser(Long id) { ... }");
        
        System.out.println("\nFallback:");
        System.out.println("  @CircuitBreaker(name = \"userService\",");
        System.out.println("    fallbackMethod = \"getUserFallback\")");
    }

    static void distributedTracing() {
        System.out.println("\n--- Distributed Tracing ---");
        System.out.println("Key concepts:");
        System.out.println("  - Trace - Request from start to end");
        System.out.println("  - Span - Unit of work");
        System.out.println("  - Context propagation");
        
        System.out.println("\nTools:");
        System.out.println("  - Jaeger");
        System.out.println("  - Zipkin");
        System.out.println("  - AWS X-Ray");
        System.out.println("  - OpenTelemetry");
        
        System.out.println("\nB3 Headers:");
        System.out.println("  X-B3-TraceId");
        System.out.println("  X-B3-SpanId");
        System.out.println("  X-B3-Sampled");
    }

    static void serviceMesh() {
        System.out.println("\n--- Service Mesh ---");
        System.out.println("Features:");
        System.out.println("  - Traffic management");
        System.out.println("  - Security (mTLS)");
        System.out.println("  - Observability");
        System.out.println("  - Policy enforcement");
        
        System.out.println("\nSidecar proxy:");
        System.out.println("  - Deployed alongside each service");
        System.out.println("  - Handles network communication");
        System.out.println("  - Service-to-service encrypted");
        
        System.out.println("\nPopular meshes:");
        System.out.println("  - Istio");
        System.out.println("  - Linkerd");
        System.out.println("  - Consul Connect");
    }

    static void eventDrivenArchitecture() {
        System.out.println("\n--- Event-Driven Architecture ---");
        System.out.println("Patterns:");
        System.out.println("  - Event Sourcing");
        System.out.println("  - CQRS (Command Query Responsibility Segregation)");
        System.out.println("  - Saga Pattern");
        
        System.out.println("\nEvent-driven communication:");
        System.out.println("  Producer -> Message Broker -> Consumer");
        System.out.println("\nApache Kafka:");
        System.out.println("  - Topics with partitions");
        System.out.println("  - Consumer groups");
        System.out.println("  - Exactly-once semantics");
        System.out.println("  - Event sourcing storage");
        
        System.out.println("\nSaga patterns:");
        System.out.println("  - Choreography: Services publish events");
        System.out.println("  - Orchestration: Central coordinator");
    }
}
