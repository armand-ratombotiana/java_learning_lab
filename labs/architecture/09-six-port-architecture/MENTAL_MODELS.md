# Mental Models for Six-Port Architecture

## The Shipping Company
Six types of interactions like a shipping company:
- Inbound orders (REST ports)
- Outbound shipping (Persistence ports)
- Partner communications (External service ports)
- Notification of status (Event ports)
- Receiving updates (Listener ports)
- Internal communications (Notification ports)

## The Airport
An airport has:
- Arrivals (Inbound ports)
- Departures (Outbound ports)
- Ground control (Internal coordination)
- Weather feeds (External information)
- Flight notifications (Event publishing)
- Passenger updates (Event listening)

## The Computer Ports
Different types of computer ports serve different purposes:
- USB (Inbound/Outbound data)
- HDMI (Display output)
- Ethernet (Network communication)
- Audio (Sound input/output)
- Power (Energy)

```java
// Each port has a specific connector type
public interface RestOrderPort {
    OrderResponse handleRequest(OrderRequest request);
}

public interface JpaOrderPersistencePort {
    Order save(Order order);
}

public interface KafkaOrderEventPort {
    void emit(OrderEvent event);
}

public interface EmailNotificationPort {
    void send(Notification notification);
}
```
