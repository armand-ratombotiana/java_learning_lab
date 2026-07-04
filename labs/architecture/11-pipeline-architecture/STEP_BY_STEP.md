# Step-by-Step Pipeline Architecture

## Step 1: Define Stage Interface
```java
@FunctionalInterface
public interface Stage<T> {
    T process(T input);
}
```

## Step 2: Create Individual Stages
```java
public class TrimStage implements Stage<String> {
    public String process(String input) { return input.trim(); }
}
public class CapitalizeStage implements Stage<String> {
    public String process(String input) { 
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
```

## Step 3: Compose Pipeline
```java
Stage<String> pipeline = new TrimStage()
    .andThen(new CapitalizeStage());
```

## Step 4: Execute Pipeline
```java
String result = pipeline.process("  hello  ");
// Result: "Hello"
```

## Step 5: Add Error Handling
```java
public class SafeStage<T> implements Stage<T> {
    private final Stage<T> delegate;
    public T process(T input) {
        try { return delegate.process(input); }
        catch (Exception e) { log.error("Stage failed", e); throw e; }
    }
}
```

## Step 6: Build Complex Pipeline
```java
Pipeline<OrderRequest, OrderResponse> orderPipeline = new Pipeline<>();
orderPipeline
    .addStage(new ValidationStage())
    .addStage(new EnrichmentStage())
    .addStage(new ProcessingStage())
    .addStage(new PersistenceStage());
```

## Step 7: Test Pipeline
```java
@Test
void testFullPipeline() {
    OrderResponse response = orderPipeline.execute(validRequest());
    assertThat(response.isSuccess()).isTrue();
}

@Test
void testIndividualStage() {
    ValidationStage stage = new ValidationStage();
    assertThrows(Exception.class, () -> stage.process(invalidRequest()));
}
```
