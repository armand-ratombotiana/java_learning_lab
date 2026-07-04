# Architecture Patterns - SECURITY

## Layered Architecture Security

### Authentication & Authorization
- **Presentation layer**: Spring Security filters for JWT/OAuth2
- **Service layer**: Method-level security with `@PreAuthorize`
- **Repository layer**: Row-level security via `@PostFilter`

```java
@RestController
public class OrderController {
    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('USER') and @orderSecurity.canRead(#id)")
    public OrderDTO getOrder(@PathVariable String id) { /* ... */ }
}
```

### Input Validation
Validate in controller, sanitize in service. Never trust user input.

## Microservices Security

### Perimeter Security
- API Gateway validates all incoming tokens
- Internal services trust internal network (zero-trust is better)
- Service mesh (Istio) with mTLS for inter-service communication

### Token Propagation
```java
// Propagate JWT between services
restTemplate.setInterceptors(List.of((request, body, execution) -> {
    request.getHeaders().setBearerAuth(currentToken.get());
    return execution.execute(request, body);
}));
```

### Secrets Management
Use Spring Cloud Config with Vault or Kubernetes Secrets. Never hardcode secrets.

## Event-Driven Security

### Event Authorization
Events should carry authentication context. Consumers verify permissions.

### Data Privacy
- Mask sensitive data in events (PII, credit cards)
- Encrypt events at rest and in transit
- Use schema registry access controls

## CQRS Security

### Different Permissions
Commands and queries can have different access controls:
```java
// Write permission for commands
@PreAuthorize("hasAuthority('ORDER_WRITE')")
public void handle(CreateOrderCommand cmd) { /* ... */ }

// Read permission for queries
@PreAuthorize("hasAuthority('ORDER_READ')")
public OrderView handle(GetOrderQuery query) { /* ... */ }
```

### Audit Trail
Event sourcing provides a natural audit log. Never delete events.
