# MOCK_INTERVIEW — Bytecode Manipulation

## Scenario
Design a `@Benchmark` annotation that a ByteBuddy agent uses to time method execution.

## Interviewer Notes
- Candidate should describe class transformation pipeline
- Use `ClassFileTransformer` with `retransformClasses`
- Discuss `Advice` in ByteBuddy for method entry/exit

## Expected Solution Sketch
```java
public class BenchmarkAgent {
    public static void premain(String args, Instrumentation inst) {
        new AgentBuilder.Default()
            .type(ElementMatchers.nameContains("Benchmark"))
            .transform((builder, type, cl, jm) -> builder
                .method(ElementMatchers.isAnnotatedWith(Benchmark.class))
                .intercept(Advice.to(TimingAdvice.class)))
            .installOn(inst);
    }
}
```

## Follow‑Up
- How to exclude specific methods?
- What are the risks of production instrumentation?
