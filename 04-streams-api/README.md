# 04 - Java Streams API

Functional-style operations on collections using Java Stream API. Covers stream creation, intermediate operations (filter, map, flatMap, distinct, sorted, skip, limit), terminal operations (count, matching, find, forEach, reduce), collectors (toMap, averagingInt, summarizingInt), and parallel streams.

## Prerequisites

- Java 17+
- Maven 3.x

## Key Concepts

- Stream creation: `Stream.of()`, `Collection.stream()`, `Arrays.stream()`, `Stream.iterate()`, `Stream.generate()`
- Intermediate operations: `filter`, `map`, `flatMap`, `distinct`, `sorted`, `peek`, `skip`, `limit`
- Terminal operations: `collect`, `forEach`, `reduce`, `count`, `anyMatch`, `allMatch`, `noneMatch`, `findFirst`, `findAny`
- Collectors: `toList`, `toSet`, `toMap`, `joining`, `groupingBy`, `partitioningBy`, `averagingInt`, `summarizingInt`
- Reduction operations with `reduce()`
- Parallel streams and performance considerations
- Lazy evaluation and stream pipeline optimization

## Module Structure

- `src/main/java/com/learning/lab/module04/Lab.java` - Main lab source
- `RESOURCES/` - Supplementary materials and flowcharts
- `SOLUTION/` - Solution implementations

## Learning Objectives

- Understand how to create and manipulate streams
- Apply intermediate and terminal operations effectively
- Use collectors for data aggregation and transformation
- Implement parallel stream processing

## Estimated Time

- Core lab: 2-3 hours
- With projects/extensions: 4-6 hours

## How to Run

```bash
cd 04-streams-api
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module04.Lab"
```
