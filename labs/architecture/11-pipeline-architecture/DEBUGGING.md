# Debugging Pipeline Architecture

## Common Issues

### 1. Finding Which Stage Failed
```java
public class DebugStage<T> implements Stage<T> {
    private final Stage<T> delegate;
    private final String name;

    @Override
    public T process(T input) {
        log.debug("Stage [{}] input: {}", name, input);
        try {
            T result = delegate.process(input);
            log.debug("Stage [{}] output: {}", name, result);
            return result;
        } catch (Exception e) {
            log.error("Stage [{}] failed with input: {}", name, input, e);
            throw e;
        }
    }
}
```

### 2. Pipeline Monitoring
```java
public class PipelineMonitor<T> implements Stage<T> {
    private final Stage<T> delegate;
    private final MeterRegistry registry;

    @Override
    public T process(T input) {
        return registry.timer("pipeline.stage")
            .record(() -> delegate.process(input));
    }
}
```

### 3. Testing Specific Stage
```java
@Test
void testStageInIsolation() {
    Stage<String> stage = new CapitalizeStage();
    assertThat(stage.process("hello")).isEqualTo("Hello");
}
```

### 4. Data Flow Inspection
```java
// Add inspection points
Pipeline<Data, Data> pipeline = new Pipeline<>();
pipeline.addStage(new DebugStage<>(new ValidationStage(), "validation"));
pipeline.addStage(new DebugStage<>(new TransformStage(), "transform"));
pipeline.addStage(new DebugStage<>(new OutputStage(), "output"));
```
