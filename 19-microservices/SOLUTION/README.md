# Microservices Solution

Reference implementation for microservice patterns: service discovery, Feign, circuit breaker.

## Service Discovery
- `ServiceRegistry` for service registration
- `EurekaClient` for service discovery
- `register`, `deregister`, `getServiceUrl`

## Feign Client
- Declarative HTTP client
- `@FeignClient` annotation
- Synchronous and async calls

## Load Balancing
- `RibbonLoadBalancer` with round-robin
- Server list management

## Circuit Breaker
- `CircuitBreaker` interface
- `CircuitBreakerImpl` with threshold
- `ResilienceCircuitBreaker` with state machine
- States: CLOSED, OPEN, HALF_OPEN

## API Gateway
- Route management
- Circuit breaker integration

## Additional
- `ServiceMesh` for service instances
- `ConfigServer` for configuration
- `TraceContext` for distributed tracing

## Test Coverage: 25+ tests