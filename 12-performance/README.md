# 12 - Performance Optimization

Java performance measurement and optimization. Covers JMH (Java Microbenchmark Harness) for writing benchmarks, loop performance analysis, string concatenation optimization (StringBuilder vs StringBuffer), and JVM performance considerations.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- JMH (Java Microbenchmark Harness): `@Benchmark`, `@BenchmarkMode`, `@OutputTimeUnit`
- Loop performance and optimization techniques
- String operations: StringBuilder vs StringBuffer vs string concatenation
- Memory allocation and garbage collection impact
- Profiling basics and hotspot detection

## Module Structure

- `01-jmh-benchmarks/` - JMH benchmark examples

## Learning Objectives

- Write and execute JMH microbenchmarks
- Identify performance bottlenecks in Java code
- Apply string and loop optimization techniques

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 12-performance
mvn clean package
```

Run JMH benchmarks:

```bash
cd 01-jmh-benchmarks
mvn clean package
java -jar target/jmh-benchmarks-1.0.0.jar
```
