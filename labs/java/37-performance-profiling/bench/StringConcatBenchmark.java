package com.javaacademy.lab37.profiling;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class StringConcatBenchmark {

    private String a = "Hello";
    private String b = "World";
    private String c = "Java";
    private String d = "Benchmark";

    @Benchmark
    public String plusConcat() {
        return a + ", " + b + " " + c + "!" + d;
    }

    @Benchmark
    public String stringBuilder() {
        return new StringBuilder()
            .append(a).append(", ")
            .append(b).append(" ")
            .append(c).append("!")
            .append(d).toString();
    }

    @Benchmark
    public String stringBuffer() {
        return new StringBuffer()
            .append(a).append(", ")
            .append(b).append(" ")
            .append(c).append("!")
            .append(d).toString();
    }

    @Benchmark
    public String stringFormat() {
        return String.format("%s, %s %s!%s", a, b, c, d);
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
