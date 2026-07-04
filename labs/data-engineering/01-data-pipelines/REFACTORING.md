# Refactoring

## Before: Monolithic
```java
public class Pipeline { public void run() { /* 500 lines mixed */ } }
```

## After: Modular
```java
@Component
public class PipelineOrchestrator {
    private final Extractor e; private final Transformer t; private final Loader l;
    public PipelineResult run(PipelineContext ctx) {
        return l.load(t.transform(e.extract(ctx)));
    }
}
```
