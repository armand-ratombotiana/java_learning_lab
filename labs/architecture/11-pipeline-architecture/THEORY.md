# Pipeline Architecture Theory

## Core Concepts

### Pipeline Stage
Each stage is a processing unit that receives input, transforms it, and passes output to the next stage.
```java
@FunctionalInterface
public interface PipelineStage<I, O> {
    O process(I input);
}
```

### Pipeline
A pipeline chains multiple stages together.
```java
public class Pipeline<I, O> {

    private final List<PipelineStage> stages = new ArrayList<>();

    public Pipeline<I, O> addStage(PipelineStage stage) {
        stages.add(stage);
        return this;
    }

    @SuppressWarnings("unchecked")
    public O execute(I input) {
        Object result = input;
        for (PipelineStage stage : stages) {
            result = stage.process(result);
        }
        return (O) result;
    }
}
```

### Stage Types
1. **Filter** - Removes items based on criteria
2. **Transformer** - Converts input to different format
3. **Validator** - Checks data validity
4. **Enricher** - Adds data from external sources
5. **Aggregator** - Combines multiple items
6. **Splitter** - Divides item into multiple items

### Pipeline Example
```java
Pipeline<OrderRequest, OrderResponse> orderPipeline = new Pipeline<>();
orderPipeline
    .addStage(new OrderValidationStage())     // Validates input
    .addStage(new CustomerLookupStage())      // Enriches with customer data
    .addStage(new InventoryCheckStage())       // Checks availability
    .addStage(new PricingStage())              // Calculates pricing
    .addStage(new OrderCreationStage());       // Creates order

OrderResponse response = orderPipeline.execute(request);
```
