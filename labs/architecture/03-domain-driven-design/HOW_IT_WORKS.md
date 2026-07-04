# How DDD Works

## Strategic Design Process

### Step 1: Domain Discovery
- Event Storming workshops with domain experts
- Identify domain events and commands
- Find aggregate boundaries

### Step 2: Bounded Context Definition
```java
// Define context boundaries
@Configuration
@EnableAutoConfiguration
@BoundedContext("billing")
@EnableJpaRepositories(basePackages = "com.company.billing.domain")
@ComponentScan("com.company.billing")
public class BillingContextConfiguration {
    // Configure billing-specific infrastructure
}
```

### Step 3: Ubiquitous Language
Create a glossary of terms shared between domain experts and developers:
```
Customer (Entity) - A person who can place orders
Order (Aggregate) - A request to purchase products
Line Item (Value) - A product with quantity in an order
Money (Value) - An amount with currency
```

## Tactical Implementation Flow

### Application Layer
```java
@Service
@RequiredArgsConstructor
public class OrderApplicationService {
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final EventPublisher eventPublisher;

    @Transactional
    public OrderResponse placeOrder(PlaceOrderCommand command) {
        Customer customer = customerRepository.findById(command.customerId());
        Cart cart = cartRepository.findById(command.cartId());
        Order order = orderFactory.createOrder(customer, cart, command.address());
        orderRepository.save(order);
        eventPublisher.publishEvents(order.releaseEvents());
        return OrderResponse.from(order);
    }
}
```

### Domain Layer
```java
@Entity
@AggregateRoot
public class Order {
    protected Order() {} // JPA

    public void addItem(Product product, Quantity quantity) {
        if (isClosed()) throw new ClosedOrderException();
        LineItem item = new LineItem(product, quantity);
        items.add(item);
        total = total.add(item.calculateTotal());
    }

    public void submit() {
        if (items.isEmpty()) throw new EmptyOrderException();
        status = OrderStatus.SUBMITTED;
        registerEvent(new OrderSubmitted(id));
    }
}
```
