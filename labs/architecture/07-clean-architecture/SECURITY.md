# Clean Architecture Security

## Security by Layer

### Controller Layer (Authentication)
```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<OrderResponseModel> createOrder(
            @RequestBody OrderRequestModel request,
            @AuthenticationPrincipal UserDetails user) {
        // Authentication verified by Spring Security
        // Pass user identity to use case
        CreateOrderInputData input = new CreateOrderInputData(
            user.getUsername(), request.getItems());
        createOrderUseCase.execute(input, presenter);
        return ResponseEntity.ok(presenter.getResponse());
    }
}
```

### Use Case Layer (Authorization)
```java
@Component
public class CreateOrderInteractor implements CreateOrderUseCase {

    private final AuthorizationService authzService;

    public void execute(CreateOrderInputData input, CreateOrderOutputBoundary output) {
        if (!authzService.canCreateOrder(input.getUserId())) {
            output.presentError("Unauthorized to create orders");
            return;
        }
        // Continue with business logic...
    }
}
```

### Entity Layer (Business Rules)
```java
public class Order {
    public void cancel(CustomerId requestedBy) {
        if (!this.customerId.equals(requestedBy)) {
            throw new UnauthorizedOperationException("Only owner can cancel");
        }
        // Business rule validation
    }
}
```

## Security Validation
- Input validation in controller (syntactic)
- Authorization in use case
- Business rule validation in entity
- Output encoding in presenter
- Audit logging at gateway layer
