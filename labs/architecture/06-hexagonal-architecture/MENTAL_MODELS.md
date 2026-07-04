# Mental Models for Hexagonal Architecture

## The Hexagon Shape
The hexagon represents the application core. Each side is a port. Adapters plug into these ports, like peripheral devices plugging into a computer.

## The Computer Analogy
The domain core is like a CPU, ports are like USB/HDMI ports, and adapters are like keyboards, monitors, and USB drives. The CPU doesn't care what's plugged into the ports.

## The Embassy Model
The domain is like an embassy in a foreign country. The embassy has its own rules (domain logic) and communicates with the outside world through defined channels (ports).

## The Power Outlet
The power outlet (port) provides a standard interface. Any device (adapter) that conforms to the standard can plug in and work, regardless of what the device does internally.

## The Translator
Ports are like translators between the core domain language and external languages (HTTP, SQL, JSON). The core speaks only domain language.

```java
// Domain core speaks only domain language
public class OrderService {
    public Order createOrder(CreateOrderCommand cmd) {
        // Pure domain logic
        return new Order(cmd.customerId());
    }
}

// Adapter translates between HTTP and domain language
@RestController
public class OrderController {
    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest req) {
        CreateOrderCommand cmd = req.toCommand(); // Translation
        Order order = orderService.createOrder(cmd);
        return ResponseEntity.ok(OrderResponse.from(order)); // Translation back
    }
}
```
