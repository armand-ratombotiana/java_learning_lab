# Code Deep Dive: Microservices

## Complete Service Implementation
```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

## Order Controller
```java
@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        log.info("Creating order for customer: {}", request.customerId());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
```

## Order Service with Resilience
```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryFallback")
    @Retry(name = "inventory", maxAttempts = 3)
    @TimeLimiter(name = "inventory")
    public OrderResponse createOrder(OrderRequest request) {
        InventoryStatus status = inventoryClient.checkAvailability(
            request.productId(), request.quantity());
        if (!status.available()) {
            throw new InsufficientInventoryException("Product not available");
        }

        Order order = Order.builder()
            .customerId(request.customerId())
            .productId(request.productId())
            .quantity(request.quantity())
            .status(OrderStatus.PENDING)
            .build();

        order = orderRepository.save(order);

        if (processPayment(order)) {
            order.setStatus(OrderStatus.CONFIRMED);
            eventPublisher.publishOrderCreated(order);
        }

        return OrderResponse.from(order);
    }

    private OrderResponse inventoryFallback(OrderRequest request, Throwable t) {
        log.warn("Inventory service unavailable, queuing order for retry");
        Order order = Order.builder()
            .customerId(request.customerId())
            .status(OrderStatus.PENDING_VERIFICATION)
            .build();
        orderRepository.save(order);
        return OrderResponse.pending(order);
    }

    private boolean processPayment(Order order) {
        try {
            PaymentResponse payment = paymentClient.processPayment(
                new PaymentRequest(order.getId(), order.getTotalAmount()));
            return payment.success();
        } catch (Exception e) {
            log.error("Payment failed for order {}", order.getId(), e);
            return false;
        }
    }
}
```

## Event Publisher
```java
@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderCreated(Order order) {
        OrderEvent event = OrderEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .orderId(order.getId())
            .eventType("ORDER_CREATED")
            .timestamp(Instant.now())
            .data(order)
            .build();

        kafkaTemplate.send("order-events", event);
    }
}
```

## API Gateway Configuration
```java
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order-service", r -> r.path("/api/orders/**")
                .filters(f -> f.circuitBreaker(config -> config
                    .setName("orderServiceCB")
                    .setFallbackUri("forward:/fallback/orders")))
                .uri("lb://ORDER-SERVICE"))
            .route("discovery-server", r -> r.path("/eureka/web")
                .filters(f -> f.setPath("/"))
                .uri("http://localhost:8761"))
            .build();
    }
}
```
