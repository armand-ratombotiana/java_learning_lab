# Layered Architecture Internals

## Package Structure
```
com.company.app/
  controller/          # Presentation layer
    UserController.java
    ProductController.java
  service/             # Business layer
    UserService.java
    ProductService.java
  repository/          # Persistence layer
    UserRepository.java
    ProductRepository.java
  entity/              # Data models
    User.java
    Product.java
  dto/                 # Data transfer objects
    UserDto.java
    ProductDto.java
  config/              # Cross-cutting
    SecurityConfig.java
    WebConfig.java
  exception/           # Error handling
    GlobalExceptionHandler.java
```

## Service Layer Patterns

### Transactional Service
```java
@Service
@Transactional
public class OrderService {

    public Order createOrder(CreateOrderRequest request) {
        // All operations within single transaction
        Order order = orderRepository.save(request.toEntity());
        inventoryRepository.deductStock(request.getProductId(), request.getQuantity());
        return order;
    }
}
```

### Service Facade
```java
@Service
public class OrderFacade {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public OrderResponse placeOrder(PlaceOrderRequest request) {
        Order order = orderService.create(request);
        Payment payment = paymentService.process(order);
        notificationService.sendConfirmation(order.getCustomerEmail());
        return OrderResponse.from(order, payment);
    }
}
```

### Cross-Cutting via AOP
```java
@Aspect
@Component
public class ServiceMonitor {

    @Before("execution(* com.company.service.*.*(..))")
    public void logServiceAccess(JoinPoint jp) {
        log.info("Service called: {}", jp.getSignature());
    }
}
```

## Layer Communication
```
Controller --> Service --> Repository
    |             |            |
    |          @Transactional  |
    |    (Business logic)   (Data access)
    v             v            v
   DTO          Entity       Entity
```
