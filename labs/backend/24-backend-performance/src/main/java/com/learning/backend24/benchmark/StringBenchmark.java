package com.learning.backend24.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(value = 2, jvmArgs = {"-Xms1G", "-Xmx1G"})
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 3)
public class StringBenchmark {

    private static final String STRING = "Hello, World!";
    private static final int ITERATIONS = 1000;

    @Benchmark
    public String stringConcat() {
        String result = "";
        for (int i = 0; i < ITERATIONS; i++) {
            result += STRING + i;
        }
        return result;
    }

    @Benchmark
    public String stringBuilder() {
        StringBuilder sb = new StringBuilder(ITERATIONS * 20);
        for (int i = 0; i < ITERATIONS; i++) {
            sb.append(STRING).append(i);
        }
        return sb.toString();
    }

    @Benchmark
    public double piComputation() {
        long inside = 0;
        var rng = new java.util.Random();
        for (int i = 0; i < 100_000; i++) {
            double x = rng.nextDouble();
            double y = rng.nextDouble();
            if (x * x + y * y <= 1) inside++;
        }
        return 4.0 * inside / 100_000;
    }

    public static void main(String[] args) throws RunnerException {
        var opt = new OptionsBuilder()
            .include(StringBenchmark.class.getSimpleName())
            .build();
        new Runner(opt).run();
    }
}
