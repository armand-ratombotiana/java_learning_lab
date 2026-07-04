# Pipeline Architecture Internals

## Pipeline Implementation
```java
public class StagePipeline<T> {

    private final List<Stage<T>> stages = new ArrayList<>();
    private final List<Stage<T>> errorHandlers = new ArrayList<>();

    public StagePipeline<T> addStage(Stage<T> stage) {
        stages.add(stage);
        errorHandlers.add(null); // No error handler by default
        return this;
    }

    public StagePipeline<T> addStage(Stage<T> stage, Stage<StageException> errorHandler) {
        stages.add(stage);
        errorHandlers.add(errorHandler);
        return this;
    }

    public T execute(T input) {
        T current = input;
        for (int i = 0; i < stages.size(); i++) {
            try {
                current = stages.get(i).process(current);
            } catch (Exception e) {
                Stage<StageException> handler = errorHandlers.get(i);
                if (handler != null) {
                    handler.process(new StageException(e, current, i));
                } else {
                    throw new PipelineExecutionException("Failed at stage " + i, e);
                }
            }
        }
        return current;
    }

    public StagePipeline<T> addBranchingStage(Predicate<T> condition,
                                               StagePipeline<T> ifTrue,
                                               StagePipeline<T> ifFalse) {
        return addStage(input -> {
            if (condition.test(input)) {
                return ifTrue.execute(input);
            } else {
                return ifFalse.execute(input);
            }
        });
    }
}
```

## Parallel Pipeline
```java
public class ParallelPipeline<T> {

    private final List<Pipeline<T>> branches = new ArrayList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public ParallelPipeline<T> addBranch(Pipeline<T> branch) {
        branches.add(branch);
        return this;
    }

    public List<T> execute(T input) {
        List<CompletableFuture<T>> futures = branches.stream()
            .map(branch -> CompletableFuture.supplyAsync(
                () -> branch.execute(input), executor))
            .toList();

        return futures.stream()
            .map(CompletableFuture::join)
            .toList();
    }
}
```

## Metrics Stage
```java
public class MetricsStage<T> implements Stage<T> {

    private final Stage<T> delegate;
    private final MeterRegistry meterRegistry;
    private final String stageName;

    @Override
    public T process(T input) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            T result = delegate.process(input);
            sample.stop(Timer.builder("pipeline.stage")
                .tag("stage", stageName)
                .tag("status", "success")
                .register(meterRegistry));
            return result;
        } catch (Exception e) {
            sample.stop(Timer.builder("pipeline.stage")
                .tag("stage", stageName)
                .tag("status", "error")
                .register(meterRegistry));
            throw e;
        }
    }
}
```
