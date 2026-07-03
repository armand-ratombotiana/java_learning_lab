# Module 46: Performance Optimization - Mini Project

**Project Name**: JMH Microbenchmarking Suite  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Use Java Microbenchmark Harness (JMH) to scientifically measure the performance differences between various Java operations, proving the impact of algorithmic choices and object allocations.

## 📝 Requirements

### Core Features

1. **JMH Setup**:
   - Create a Maven project and add the dependencies for `jmh-core` and `jmh-generator-annprocess`.

2. **Benchmark 1: String Concatenation**:
   - Create a class `StringBenchmark`.
   - Write a method using the `+=` operator in a loop of 10,000 iterations.
   - Write a second method using `StringBuilder` for the same loop.
   - Annotate both with `@Benchmark`, `@BenchmarkMode(Mode.AverageTime)`, and `@OutputTimeUnit(TimeUnit.MILLISECONDS)`.

3. **Benchmark 2: Loop vs Stream**:
   - Create a class `IterationBenchmark`.
   - Setup a `List<Integer>` with 1,000,000 random integers using `@State` and `@Setup`.
   - Write a method that sums the list using a traditional `for` loop.
   - Write a method that sums the list using `.stream().reduce(0, Integer::sum)`.
   - Write a method using `.parallelStream().reduce(0, Integer::sum)`.

4. **Benchmark 3: Exception Throwing**:
   - Create a class `ExceptionBenchmark`.
   - Write a method that returns an error string on failure.
   - Write a method that throws a `new RuntimeException()` on failure.
   - Compare the massive performance difference of creating the stack trace.

5. **Execution**:
   - Write a main method using `OptionsBuilder` to run the benchmarks.
   - Analyze the output table to see the statistical differences in operations per second or average time.

---

## 💡 Solution Blueprint

```java
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class StringBenchmark {

    @Benchmark
    public String slowConcatenation() {
        String s = "";
        for (int i = 0; i < 10000; i++) {
            s += i;
        }
        return s;
    }

    @Benchmark
    public String fastConcatenation() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append(i);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(StringBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(3)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }
}
```