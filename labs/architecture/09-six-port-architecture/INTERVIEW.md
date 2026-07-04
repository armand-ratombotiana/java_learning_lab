# Six-Port Architecture Interview Questions

## Junior Level

### Q: What is Six-Port Architecture?
**A:** An architectural pattern that categorizes all external interactions into 6 explicit port types: Inbound Driving, Outbound Driven, Outbound Driving, Event Publisher, Event Subscriber, and Notification.

### Q: How is it different from Hexagonal Architecture?
**A:** Hexagonal defines general ports and adapters. Six-Port provides more specific categorization with 6 named port types and consistent naming conventions.

## Mid Level

### Q: How do you test a Six-Port architecture service?
**A:** Create in-memory test adapters for each port type. The domain service can then be tested with all external dependencies replaced by lightweight, fast in-memory implementations.

### Q: What is the advantage of explicit port types?
**A:** Clear documentation of system integration points, consistent naming across teams, easier onboarding, and standardized testing patterns.

## Senior Level

### Q: Design a Six-Port architecture for a payment processing system.
**A:**
Inbound Driving: PaymentInputPort (REST API)
Outbound Driven: PaymentRepositoryPort (JPA)
Outbound Driving: BankGatewayPort (external bank API)
Event Publisher: PaymentEventPublisherPort (Kafka)
Event Subscriber: RefundEventSubscriberPort (Kafka consumer)
Notification: PaymentNotificationPort (email/SMS)
Domain service orchestrates all 6 port types.

### Q: How do you handle cross-cutting concerns across all ports?
**A:** Use decorator pattern around each port. Create abstract PortDecorator that wraps a port implementation with logging, metrics, security, and transaction management.
