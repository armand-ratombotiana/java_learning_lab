# Common Mistakes in Pipeline Architecture

## 1. Shared Mutable State
```java
// WRONG: Stage modifies shared state
public class CountingStage implements Stage<String> {
    private int count = 0; // Shared mutable state!
    public String process(String input) {
        count++;
        return input;
    }
}

// CORRECT: Stateless stage
public class CountingStage implements Stage<String> {
    public String process(String input) {
        return input; // Pure function
    }
}
```

## 2. Side Effects in Stages
```java
// WRONG: Database call in stage
public class DatabaseStage implements Stage<Data> {
    public Data process(Data input) {
        dataRepository.save(input.toEntity()); // Side effect!
        return input;
    }
}

// CORRECT: Collect and persist at end
```

## 3. Tight Coupling Between Stages
```java
// WRONG: Stage depends on previous stage internals
public class ProcessingStage implements Stage<Data> {
    public Data process(Data input) {
        return new Data(input.getInternalField()); // Depends on implementation!
    }
}
```

## 4. Error Handling Missing
```java
// WRONG: No error handling in pipeline
pipeline.addStage(new FailingStage()); // If this fails, pipeline stops with no recovery

// CORRECT: Add error handling
pipeline.addStage(new SafeStage(new FailingStage()));
```

## 5. Overly Complex Stages
```java
// WRONG: Stage does too much
public class GodStage implements Stage<Data> {
    public Data process(Data input) {
        // Validation, enrichment, transformation, persistence all here!
    }
}
```
