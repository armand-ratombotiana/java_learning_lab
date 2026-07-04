# Six-Port Architecture Reference

## Architecture Diagram
```
[Client] -> 1. Inbound Driving Port -> [OrderService] -> 2. Outbound Driven Port -> [Database]
                      |                           |
                      |                    3. Outbound Driving Port -> [Payment Gateway]
                      |                           |
                      |                    4. Event Publisher Port -> [Kafka Topic]
                      |                           |
                      |                    5. Notification Port -> [Email Service]
                      |                           |
                      |                    6. Event Subscriber Port <- [Kafka Topic]
```

## Port Type Reference
| # | Port Type | Direction | Interface Suffix | Example |
|---|-----------|-----------|-----------------|---------|
| 1 | Inbound Driving | In | Port | OrderInputPort |
| 2 | Outbound Driven | Out | Port | OrderRepositoryPort |
| 3 | Outbound Driving | Out | Port | PaymentGatewayPort |
| 4 | Outbound Event | Out | EventPort | OrderEventPublisherPort |
| 5 | Inbound Event | In | EventPort | OrderEventSubscriberPort |
| 6 | Notification | Out | NotificationPort | EmailNotificationPort |

## Package Structure
```
com.company.service/
  port/
    inbound/
    outbound/
      persistence/
      external/
      event/
      notification/
  adapter/
    inbound/
      rest/
      messaging/
    outbound/
      persistence/
        jpa/
        mongodb/
      external/
        payment/
        shipping/
      event/
        kafka/
        rabbitmq/
      notification/
        email/
        sms/
  domain/
    model/
    service/
```
