# Code Deep Dive: Pipeline Architecture

## Complete ETL Pipeline Example

### Stage Definitions
```java
@FunctionalInterface
public interface Stage<T> {
    T process(T input);

    default Stage<T> andThen(Stage<T> next) {
        return input -> next.process(this.process(input));
    }
}
```

### Data Model
```java
public record RawData(String id, String content, LocalDateTime timestamp) {}
public record ValidatedData(String id, String content, boolean valid, List<String> errors) {}
public record TransformedData(String id, String uppercase, int wordCount, String hash) {}
public record OutputRecord(String id, String content, int wordCount, String hash, LocalDateTime processedAt) {}
```

### Stages
```java
public class ValidationStage implements Stage<RawData, ValidatedData> {
    @Override
    public ValidatedData process(RawData input) {
        List<String> errors = new ArrayList<>();
        if (input.id() == null || input.id().isBlank()) {
            errors.add("ID is required");
        }
        if (input.content() == null || input.content().isBlank()) {
            errors.add("Content is required");
        }
        return new ValidatedData(input.id(), input.content(),
            errors.isEmpty(), errors);
    }
}

public class TransformationStage implements Stage<ValidatedData, TransformedData> {
    @Override
    public TransformedData process(ValidatedData input) {
        if (!input.valid()) {
            throw new ValidationException(input.errors());
        }
        return new TransformedData(
            input.id(),
            input.content().toUpperCase(),
            input.content().split("\\s+").length,
            DigestUtils.sha256Hex(input.content())
        );
    }
}

public class OutputStage implements Stage<TransformedData, OutputRecord> {
    @Override
    public OutputRecord process(TransformedData input) {
        return new OutputRecord(
            input.id(),
            input.uppercase(),
            input.wordCount(),
            input.hash(),
            LocalDateTime.now()
        );
    }
}

public class LoggingStage<T> implements Stage<T> {
    private final String name;

    public LoggingStage(String name) { this.name = name; }

    @Override
    public T process(T input) {
        log.info("Pipeline [{}] processing: {}", name, input);
        return input;
    }
}
```

### Pipeline Builder
```java
public class PipelineBuilder<T> {

    private Stage<T> pipeline;

    public PipelineBuilder(Stage<T> initial) {
        this.pipeline = initial;
    }

    public PipelineBuilder<T> then(Stage<T> stage) {
        this.pipeline = this.pipeline.andThen(stage);
        return this;
    }

    public Stage<T> build() {
        return pipeline;
    }
}
```

### Usage
```java
public class ETLPipelineExample {

    private final Stage<RawData, OutputRecord> pipeline;

    public ETLPipelineExample() {
        this.pipeline = new PipelineBuilder<RawData>(new LoggingStage<>("ETL"))
            .then(new ValidationStage())
            .then(new LoggingStage<>("Validated"))
            .then(new TransformationStage())
            .then(new LoggingStage<>("Transformed"))
            .then(new OutputStage())
            .then(new LoggingStage<>("Complete"))
            .build();
    }

    public OutputRecord process(RawData data) {
        return pipeline.process(data);
    }
}
```

### Reactive Pipeline
```java
public class ReactiveOrderPipeline {

    public Mono<OrderResponse> processOrder(OrderRequest request) {
        return Mono.just(request)
            .flatMap(this::validate)
            .flatMap(this::enrichWithCustomer)
            .flatMap(this::checkInventory)
            .flatMap(this::calculatePricing)
            .flatMap(this::createOrder)
            .map(OrderResponse::from);
    }
}
```
