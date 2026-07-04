# Code Deep Dive: DDD Implementation

## Complete Aggregate Example

### Value Objects
```java
@Value
public class OrderId implements Serializable {
    String value;

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }
}

@Embeddable
@Value
public class Address {
    String street;
    String city;
    String zipCode;
    String country;

    public static Address forShipping(String street, String city, String zip, String country) {
        return new Address(street, city, zip, country);
    }
}

@Value
public class Money {
    BigDecimal amount;
    Currency currency;

    public static Money usd(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("USD"));
    }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
```

### Aggregate Root
```java
@Entity
@Table(name = "orders")
@AggregateRoot
public class Order {

    @EmbeddedId
    private OrderId id;

    @Embedded
    private Money totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "shipping_street")),
        @AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "shipping_zip")),
        @AttributeOverride(name = "country", column = @Column(name = "shipping_country"))
    })
    private Address shippingAddress;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<LineItem> items = new ArrayList<>();

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected Order() {}

    public Order(OrderId id, CustomerId customerId, Address shippingAddress) {
        this.id = id;
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PENDING;
        this.totalAmount = Money.usd(BigDecimal.ZERO);
    }

    public void addItem(ProductId productId, String productName, Money price, int quantity) {
        if (status != OrderStatus.PENDING) {
            throw new OrderNotModifiableException(id);
        }
        items.add(new LineItem(productId, productName, price, quantity));
        recalculateTotal();
    }

    public void submit() {
        if (items.isEmpty()) {
            throw new EmptyOrderException(id);
        }
        this.status = OrderStatus.SUBMITTED;
        registerEvent(new OrderSubmitted(id, customerId, totalAmount));
    }

    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new OrderCannotBeCancelledException(id, status);
        }
        this.status = OrderStatus.CANCELLED;
        registerEvent(new OrderCancelled(id));
    }

    public List<DomainEvent> releaseEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }
}
```

### Domain Repository
```java
public interface OrderRepository extends JpaRepository<Order, OrderId> {
    Optional<Order> findById(OrderId id);
    List<Order> findByCustomerId(CustomerId customerId);
    List<Order> findByStatus(OrderStatus status);
}
```

### Application Service
```java
@Service
@RequiredArgsConstructor
@Transactional
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final DomainEventPublisher eventPublisher;

    public OrderResponse placeOrder(PlaceOrderCommand command) {
        Order order = orderFactory.createOrder(
            new CustomerId(command.customerId()),
            command.shippingAddress()
        );
        command.items().forEach(item ->
            order.addItem(item.productId(), item.productName(),
                item.price(), item.quantity()));
        order.submit();
        orderRepository.save(order);
        eventPublisher.publishAll(order.releaseEvents());
        return OrderResponse.from(order);
    }
}
```
