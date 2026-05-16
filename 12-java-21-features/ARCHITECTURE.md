# Architecture with Java 21 Features

## Virtual Thread Architecture

### Design Patterns
```java
// Per-Task Executor Pattern
public class VirtualThreadExecutor {
    private final ExecutorService executor = 
        Executors.newVirtualThreadPerTaskExecutor();
    
    public void submit(Runnable task) {
        executor.submit(task);
    }
    
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }
}
```

## Sequenced Collections Architecture

### Consistent API
```java
public interface ProcessingPipeline<T> extends SequencedCollection<T> {
    default ProcessingPipeline<T> addStep(Function<T, T> step) {
        addLast(step);
        return this;
    }
    
    default List<T> execute(List<T> input) {
        return input.stream()
            .map(this::processPipeline)
            .toList();
    }
    
    private T processPipeline(T item) {
        T result = item;
        for (Function<T, T> step : this) {
            result = step.apply(result);
        }
        return result;
    }
}
```

## Pattern Matching Architecture

### Polymorphic Handlers
```java
sealed interface Response permits Success, Error, Redirect {}
record Success(int code, String body) implements Response {}
record Error(int code, String message) implements Response {}
record Redirect(String url) implements Response {}

String handle(Response r) {
    return switch(r) {
        case Success(int code, String body) -> STR."OK: \{body}";
        case Error(int code, String message) -> STR."Error: \{message}";
        case Redirect(String url) -> STR."Redirect: \{url}";
    };
}
```