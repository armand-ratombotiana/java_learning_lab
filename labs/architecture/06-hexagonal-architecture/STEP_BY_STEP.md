# Step-by-Step Hexagonal Architecture

## Step 1: Define Domain Model
Create domain entities and value objects with no framework dependencies.

## Step 2: Define Ports (Interfaces)
```java
// Inbound - driving
public interface CreateOrderUseCase {
    Order execute(CreateOrderCommand cmd);
}
// Outbound - driven
public interface OrderRepository {
    Order save(Order order);
}
```

## Step 3: Implement Domain Service
```java
@Service
public class CreateOrderService implements CreateOrderUseCase {
    private final OrderRepository repo;
    // Pure business logic using only domain types
}
```

## Step 4: Create Driving Adapter
```java
@RestController
public class OrderController {
    // Translates HTTP to domain calls
}
```

## Step 5: Create Driven Adapter
```java
@Component
public class JpaOrderAdapter implements OrderRepository {
    @Autowired
    private SpringDataJpaRepository repo;
    // Translates domain calls to JPA
}
```

## Step 6: Wire Together
```java
@Configuration
public class BeanConfig {
    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepository repo) {
        return new CreateOrderService(repo);
    }
}
```

## Step 7: Test
```java
@Test
void domainTest() {
    InMemoryOrderRepository repo = new InMemoryOrderRepository();
    var service = new CreateOrderService(repo);
    service.execute(new CreateOrderCommand("C1"));
    assertThat(service).isNotNull();
}
```
