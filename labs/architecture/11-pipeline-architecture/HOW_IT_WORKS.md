# How Pipeline Architecture Works

## Basic Pipeline Flow
```
Input -> Stage1 -> Stage2 -> Stage3 -> ... -> StageN -> Output
```

## Complete Example

### Stage Interface
```java
@FunctionalInterface
public interface Stage<T> {
    T process(T input);

    default Stage<T> andThen(Stage<T> next) {
        return input -> next.process(this.process(input));
    }
}
```

### Individual Stages
```java
public class TrimStage implements Stage<String> {
    @Override
    public String process(String input) {
        return input.trim();
    }
}

public class UppercaseStage implements Stage<String> {
    @Override
    public String process(String input) {
        return input.toUpperCase();
    }
}

public class ReplaceStage implements Stage<String> {
    private final String target;
    private final String replacement;

    public ReplaceStage(String target, String replacement) {
        this.target = target;
        this.replacement = replacement;
    }

    @Override
    public String process(String input) {
        return input.replace(target, replacement);
    }
}
```

### Composition
```java
// Compose stages
Stage<String> pipeline = new TrimStage()
    .andThen(new UppercaseStage())
    .andThen(new ReplaceStage(" ", "_"));

String result = pipeline.process("  hello world  ");
// Result: "HELLO_WORLD"
```

### Data Processing Pipeline
```java
public class OrderProcessingPipeline {

    private final Stage<OrderRequest> pipeline;

    public OrderProcessingPipeline() {
        this.pipeline = new ValidationStage()
            .andThen(new CustomerEnrichmentStage())
            .andThen(new InventoryCheckStage())
            .andThen(new PricingStage())
            .andThen(new OrderCreationStage());
    }

    public OrderResponse process(OrderRequest request) {
        try {
            OrderRequest processed = pipeline.process(request);
            return OrderResponse.success(processed);
        } catch (PipelineException e) {
            return OrderResponse.failure(e.getMessage());
        }
    }
}
```

### Java Streams as Pipeline
```java
public List<OrderSummary> generateReport(List<Order> orders) {
    return orders.stream()
        .filter(Order::isPaid)
        .map(this::toSummary)
        .sorted(Comparator.comparing(OrderSummary::getDate).reversed())
        .limit(100)
        .toList();
}
```
