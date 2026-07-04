# DDD Internals

## Aggregate Persistence Internals
```java
@Entity
@Access(AccessType.FIELD)
public class Customer extends AbstractAggregateRoot<CustomerId> {

    @EmbeddedId
    private CustomerId id;

    @Embedded
    private Email email;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "shipping_street")),
    })
    private Address shippingAddress;

    @OneToMany(cascade = ALL, mappedBy = "customer")
    private List<Order> orders = new ArrayList<>();

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    // JPA uses identity field directly
    // Domain uses behavior methods
}
```

## Domain Event Publishing Internals
```java
@Component
public class DomainEventPublisher {
    private final ApplicationEventPublisher springPublisher;

    public void publishEvents(List<DomainEvent> events) {
        events.forEach(event -> {
            // Store in event store
            eventStore.append(event);
            // Publish for integration
            springPublisher.publishEvent(event);
            // Publish to message broker
            kafkaTemplate.send("domain-events", event);
        });
    }
}
```

## Specification Pattern Internals
```java
public class OrderSpecifications {
    public static Specification<Order> byCustomer(CustomerId customerId) {
        return (root, query, cb) ->
            cb.equal(root.get("customerId"), customerId);
    }

    public static Specification<Order> byStatus(OrderStatus status) {
        return (root, query, cb) ->
            cb.equal(root.get("status"), status);
    }

    public static Specification<Order> byAmountGreaterThan(Money amount) {
        return (root, query, cb) ->
            cb.greaterThanOrEqualTo(
                root.get("totalAmount").get("amount"),
                amount.getAmount());
    }
}
```
