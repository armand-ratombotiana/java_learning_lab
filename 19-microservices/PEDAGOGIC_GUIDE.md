# Microservices - Pedagogic Guide

---

## Learning Objectives

By the end of this module, learners will be able to:

1. **Design Microservices Architecture**
   - Decompose monolithic applications
   - Define service boundaries
   - Identify data ownership per service

2. **Implement Service Communication**
   - Use synchronous REST communication
   - Implement asynchronous messaging
   - Handle partial failures

3. **Apply Resilience Patterns**
   - Implement circuit breakers
   - Configure retry policies
   - Use bulkhead isolation

4. **Deploy Microservices**
   - Containerize services with Docker
   - Orchestrate with Kubernetes
   - Configure service discovery

---

## Teaching Sequence

### Phase 1: Fundamentals (2-3 hours)

**Topic 1: Service Discovery**
- Lecture: Why service discovery matters
- Demo: Eureka Server setup
- Exercise: Register services and discover them

**Topic 2: API Gateway**
- Lecture: Gateway responsibilities
- Demo: Spring Cloud Gateway configuration
- Exercise: Route requests to multiple services

### Phase 2: Communication (2-3 hours)

**Topic 3: REST Communication**
- Lecture: REST vs Messaging
- Demo: OpenFeign client implementation
- Exercise: Implement inter-service calls

**Topic 4: Asynchronous Messaging**
- Lecture: Event-driven architecture
- Demo: Kafka/RabbitMQ integration
- Exercise: Implement event publishing

### Phase 3: Resilience (2-3 hours)

**Topic 5: Circuit Breaker**
- Lecture: Failure modes and handling
- Demo: Resilience4j configuration
- Exercise: Add circuit breaker to existing service calls

**Topic 6: Retry and Timeout**
- Lecture: Retry strategies
- Demo: Configure retry policies
- Exercise: Implement exponential backoff

### Phase 4: Deployment (2-3 hours)

**Topic 7: Container Orchestration**
- Lecture: Kubernetes basics
- Demo: Deploy services to K8s
- Exercise: Configure scaling and health checks

**Topic 8: Service Mesh**
- Lecture: Service mesh vs gateway
- Demo: Istio traffic management
- Exercise: Implement canary deployment

---

## Hands-On Projects

### Mini-Project: Service Discovery System
**Duration**: 5-6 hours
**Focus**: Core microservices patterns

Learning outcomes:
- Service registration with Eureka
- OpenFeign client implementation
- API Gateway routing
- Basic resilience patterns

### Real-World Project: E-Commerce Platform
**Duration**: 20+ hours
**Focus**: Production microservices

Learning outcomes:
- Multiple services with shared contracts
- Circuit breaker implementation
- Kafka event streaming
- Kubernetes deployment
- Distributed tracing

---

## Assessment Criteria

### Must Have (Core)
- [ ] Set up service registry
- [ ] Register services with discovery
- [ ] Implement API gateway routing
- [ ] Create REST client with Feign

### Should Have (Intermediate)
- [ ] Implement circuit breaker
- [ ] Add retry policies
- [ ] Configure distributed tracing
- [ ] Set up health endpoints

### Nice to Have (Advanced)
- [ ] Deploy to Kubernetes
- [ ] Implement service mesh
- [ ] Configure canary deployment
- [ ] Set up auto-scaling

---

## Common Pitfalls

1. **Distributed Monolith**
   - Avoid synchronous calls between every service
   - Use events for cross-service communication

2. **Shared Databases**
   - Each service should own its data
   - Use API calls, not direct database access

3. **Lack of Observability**
   - Always add tracing headers
   - Log with correlation IDs

4. **Ignoring Failures**
   - Design for partial failure
   - Implement circuit breakers everywhere

---

## Discussion Questions

1. How do you decide the right granularity for microservices?
2. When should you use synchronous vs asynchronous communication?
3. What are the trade-offs of a service mesh?
4. How do you handle transactions that span multiple services?

---

## Extension Activities

1. **Observability Challenge**: Add distributed tracing to all services
2. **Performance Challenge**: Optimize service response times
3. **Reliability Challenge**: Achieve 99.9% uptime

---

## Additional Resources

- Building Microservices by Sam Newman
- Spring Cloud Documentation
- Kubernetes Up and Running
- Istio Documentation

