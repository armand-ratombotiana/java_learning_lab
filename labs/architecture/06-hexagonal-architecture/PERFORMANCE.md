# Hexagonal Architecture Performance

## Performance Considerations

### Adapter Overhead
```java
// Mapping between domain and external types has overhead
@RestController
public class OrderController {
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        long start = System.nanoTime();
        CreateOrderCommand cmd = request.toCommand();      // 0.1ms
        Order order = createOrderUseCase.execute(cmd);     // 50ms (logic)
        OrderResponse resp = OrderResponse.from(order);    // 0.05ms
        long duration = System.nanoTime() - start;
        if (duration > 100_000_000) { // >100ms
            log.warn("Slow order creation: {}ms", duration / 1_000_000);
        }
        return ResponseEntity.ok(resp);
    }
}
```

### Optimization Tips
- Use mapper caching for repeated conversions
- Batch database operations in adapters
- Profile adapter vs domain time
- Keep adapter translation thin
- Use direct mapping libraries (MapStruct)

## Performance Monitoring
```java
@Component
public class AdapterMetrics {
    private final MeterRegistry registry;

    public void recordAdapterCall(String adapterName, String method, long ms) {
        registry.timer("adapter.duration", "name", adapterName, "method", method)
            .record(Duration.ofMillis(ms));
    }
}
```

## Benchmarking
- Domain should be < 10% of total request time
- Adapter translation should be minimal (< 1ms)
- Focus optimization on domain logic, not adapter overhead
- Monitor mapping efficiency with profiling
