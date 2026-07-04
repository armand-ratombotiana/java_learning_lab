# Debugging Hexagonal Architecture

## Common Issues

### 1. Wrong Adapter Wire-up
```bash
# Check Spring context for adapter beans
# Verify correct implementation injected
2024-01-01 INFO: Bean 'orderRepository' of type [JpaOrderRepository] created
```

### 2. Testing Wrong Layer
```java
// WRONG: Testing adapter with domain logic
@SpringBootTest
class OrderControllerTest {
    // This tests both adapter AND domain - not isolated
}

// CORRECT: Test domain in isolation
class CreateOrderServiceTest {
    private InMemoryOrderRepository repo;
    private CreateOrderService service;
    
    @Test
    void domainLogicTest() {
        service.execute(new CreateOrderCommand("C1"));
        // Fast, no infrastructure needed
    }
}
```

### 3. Mapping Errors
```java
@Component
@Slf4j
public class OrderMapper {
    public Order toDomain(OrderEntity entity) {
        if (entity == null) return null;
        // Add logging for debugging
        log.debug("Mapping entity {} to domain", entity.getId());
        return new Order(
            new OrderId(entity.getId()),
            entity.getCustomerId()
        );
    }
}
```

### 4. Port Not Implemented
```bash
# Check for missing adapter implementations
Description:
Field orderRepository in CreateOrderService required a bean of type
'OrderRepository' that could not be found.
```
