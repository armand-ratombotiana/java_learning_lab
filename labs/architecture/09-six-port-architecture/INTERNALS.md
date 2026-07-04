# Six-Port Architecture Internals

## Package Structure
```
com.company.orders/
  domain/
    model/
      Order.java
      OrderId.java
    service/
      OrderService.java
  port/
    inbound/
      CreateOrderPort.java
      GetOrderPort.java
    outbound/
      persistence/
        OrderPersistencePort.java
      external/
        PaymentGatewayPort.java
        InventoryServicePort.java
      event/
        OrderEventPublisherPort.java
        OrderEventSubscriberPort.java
      notification/
        NotificationPort.java
  adapter/
    inbound/
      rest/
        OrderRestController.java
      messaging/
        OrderEventConsumer.java
    outbound/
      persistence/
        jpa/
          JpaOrderPersistenceAdapter.java
          OrderJpaEntity.java
          OrderMapper.java
      external/
        stripe/
          StripePaymentGatewayAdapter.java
      event/
        kafka/
          KafkaOrderEventPublisherAdapter.java
      notification/
        email/
          EmailNotificationAdapter.java
```

## Domain Service with Ports
```java
@Service
@RequiredArgsConstructor
public class OrderService {

    // All six port types used
    private final CreateOrderPort inputPort;
    private final OrderPersistencePort persistencePort;
    private final PaymentGatewayPort paymentPort;
    private final OrderEventPublisherPort eventPort;
    private final OrderEventSubscriberPort subscriberPort;
    private final NotificationPort notificationPort;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = inputPort.createOrder(request);
        persistencePort.save(order);
        PaymentResponse payment = paymentPort.charge(request.toPayment());
        eventPort.publish(new OrderCreatedEvent(order.getId()));
        notificationPort.send(new OrderConfirmation(order.getCustomerEmail()));
        return order;
    }
}
```

## Adapter Registration
```java
@Configuration
public class PortAdapterConfig {

    @Bean
    public OrderPersistencePort persistencePort() {
        return new JpaOrderPersistenceAdapter(jpaRepo, mapper);
    }

    @Bean
    public PaymentGatewayPort paymentPort() {
        return new StripePaymentGatewayAdapter(stripeClient);
    }

    @Bean
    public OrderEventPublisherPort eventPort() {
        return new KafkaOrderEventPublisherAdapter(kafkaTemplate);
    }
}
```
