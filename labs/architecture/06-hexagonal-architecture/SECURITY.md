# Hexagonal Architecture Security

## Security Patterns

### Authentication in Adapter
```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        // Authentication handled by adapter
        CreateOrderCommand cmd = request.toCommand(user.getId());
        Order order = createOrderUseCase.execute(cmd);
        return ResponseEntity.ok(OrderResponse.from(order));
    }
}
```

### Authorization in Port
```java
// Authorization logic can be a decorator around the port
@Component
public class AuthorizedCreateOrderUseCase implements CreateOrderUseCase {

    private final CreateOrderUseCase delegate;
    private final AuthorizationService authz;

    @Override
    public Order execute(CreateOrderCommand command) {
        if (!authz.canCreateOrder(command.userId())) {
            throw new AuthorizationException("Cannot create orders");
        }
        return delegate.execute(command);
    }
}
```

### Input Validation in Adapter
```java
@RestController
public class OrderController {
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request) {
        // Bean Validation handles syntactic validation
        // Adapter translates to domain command
    }
}
```

## Security by Layer
- **Adapters**: Authentication, input validation, rate limiting
- **Ports**: Authorization decorators
- **Domain**: Business rule validation
- **Adapters**: Output encoding, encryption
