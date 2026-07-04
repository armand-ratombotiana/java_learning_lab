# Mini Project: JMH String Concatenation Benchmark

## Objective
Write a legitimate Java Microbenchmark using JMH to prove the performance differences between `String` concatenation (using `+`), `StringBuilder`, and `String.format()`. You will learn how to use `@State`, `@Setup`, and `Blackhole` to defeat JIT optimizations.

## Prerequisites
*   Java 17+
*   Maven (JMH is best run via a Maven plugin to generate the correct benchmark bytecode).

## Step 1: Maven Setup
Create a new Maven project and add the JMH dependencies.

```xml
<dependencies>
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-core</artifactId>
        <version>1.37</version>
    </dependency>
    <dependency>
        <groupId>org.openjdk.jmh</groupId>
        <artifactId>jmh-generator-annprocess</artifactId>
        <version>1.37</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.openjdk.jmh</groupId>
                        <artifactId>jmh-generator-annprocess</artifactId>
                        <version>1.37</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Step 2: The Benchmark Class
Create the benchmark class. We use `@State(Scope.Benchmark)` to define variables that are shared across all benchmark threads.

```java
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)       // Measure average time per operation
@OutputTimeUnit(TimeUnit.NANOSECONDS)  // Output in nanoseconds
@Warmup(iterations = 3, time = 1)      // 3 warmup iterations of 1 second each
@Measurement(iterations = 5, time = 1) // 5 measurement iterations of 1 second each
@Fork(1)                               // Run in a single separate JVM process
@State(Scope.Benchmark)                // State shared across threads
public class StringConcatBenchmark {

    // These fields act as our input data. 
    // They are NOT final, preventing the JIT from constant-folding them.
    private String prefix;
    private String suffix;
    private int number;

    @Setup
    public void setup() {
        // Setup logic is NOT measured in the benchmark time
        prefix = "User_";
        suffix = "_Data";
        number = 42;
    }

    @Benchmark
    public void testPlusOperator(Blackhole bh) {
        // The '+' operator in a loop is bad, but for a single concatenation, 
        // modern Java compilers optimize it using invokedynamic and StringConcatFactory.
        String result = prefix + number + suffix;
        
        // CRITICAL: Consume the result so the JIT doesn't delete the code
        bh.consume(result);
    }

    @Benchmark
    public void testStringBuilder(Blackhole bh) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append(number).append(suffix);
        bh.consume(sb.toString());
    }

    @Benchmark
    public void testStringFormat(Blackhole bh) {
        // String.format is notoriously slow due to regex parsing and varargs
        String result = String.format("%s%d%s", prefix, number, suffix);
        bh.consume(result);
    }
}
```

## Step 3: Execute the Benchmark
To run JMH properly, you should build an executable JAR (often called `benchmarks.jar` via the Maven Shade plugin). However, for a quick test in an IDE, you can use the JMH Runner API.

```java
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Main {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringConcatBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
```

## Expected Output
JMH will print a lot of warmup data. Look at the final summary table.
You will see that `String.format` is orders of magnitude slower than the `+` operator or `StringBuilder`. You will also see that in modern Java (9+), the `+` operator is heavily optimized and often matches or beats `StringBuilder` for simple, single-line concatenations.

```text
Benchmark                               Mode  Cnt     Score    Error  Units
StringConcatBenchmark.testPlusOperator  avgt    5     7.123 ±  0.512  ns/op
StringConcatBenchmark.testStringBuilder avgt    5     8.456 ±  0.621  ns/op
StringConcatBenchmark.testStringFormat  avgt    5   125.789 ± 12.456  ns/op
```