# Six-Port Architecture Flashcards

## Q: What are the 6 port types?
**A:** Inbound Driving, Outbound Driven, Outbound Driving, Event Publisher, Event Subscriber, Notification.

## Q: What is an Inbound Driving Port?
**A:** A port that receives external requests (e.g., REST controller interface).

## Q: What is an Outbound Driven Port?
**A:** A port for persistence operations (e.g., repository interface).

## Q: What is an Outbound Driving Port?
**A:** A port for calling external services (e.g., payment gateway interface).

## Q: What is an Event Publisher Port?
**A:** A port for publishing events to message brokers.

## Q: What is an Event Subscriber Port?
**A:** A port for consuming events from external systems.

## Q: What is a Notification Port?
**A:** A port for sending notifications (email, SMS, push).

## Q: What naming convention does Six-Port use?
**A:** Interface suffix "Port" (e.g., OrderRepositoryPort).

## Q: What is an adapter?
**A:** An implementation of a port that translates between domain and external technology.

## Q: How does Six-Port differ from Hexagonal?
**A:** Six-Port explicitly defines 6 port categories with specific naming conventions.
