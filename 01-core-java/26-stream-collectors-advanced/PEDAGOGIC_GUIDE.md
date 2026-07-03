# Pedagogic Guide: Advanced Stream Collectors

## 1. Module Overview
This module takes learners beyond the "magic" of `stream.toList()`. It deconstructs the reduction phase of the Streams API, teaching learners how to build their own aggregation engines. This is crucial for optimizing complex data processing tasks that would otherwise require multiple stream passes or unreadable nested loops.

## 2. Learning Paths

### Path A: The Data Engineer (Focus: Custom Aggregation & Teeing)
**Target Audience**: Developers writing ETL pipelines, report generators, or complex data transformation logic.
*   **Focus**: `DEEP_DIVE.md` (Custom Collectors, Teeing) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Mastering `Collector.of()` to perform complex, multi-variable aggregations in a single, clean pass of the data stream, and utilizing `teeing()` for split-logic processing.

### Path B: The Performance Optimizer (Focus: Concurrency & Parallelism)
**Target Audience**: Senior developers optimizing high-throughput data processing systems.
*   **Focus**: `EDGE_CASES.md` (Combiner Trap, CONCURRENT flag) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Deeply understanding the `combiner` function's role in parallel streams, and knowing exactly when and how to safely apply `Collector.Characteristics.CONCURRENT`.

## 3. Teaching Strategies

### The "Assembly Line" Metaphor
To teach the four functions of a Collector, use a factory assembly line metaphor:
1.  **Supplier**: The worker who grabs an empty cardboard box.
2.  **Accumulator**: The worker who takes items off the conveyor belt (the stream) and packs them into the box.
3.  **Combiner**: Used only when there are multiple assembly lines running in parallel. This is the worker who takes two partially filled boxes from different lines and dumps them into a single, larger box.
4.  **Finisher**: The worker at the end who seals the box, slaps a shipping label on it, and sends it out the door (transforming the internal accumulator into the final return type).

### The "Teeing" Visual
Draw a Y-shaped pipe. Explain that before Java 12, if you poured water (data) down a pipe, it could only go to one bucket. If you needed to fill two different buckets (e.g., one to calculate Min, one to calculate Max), you had to pour water down the pipe twice (iterating the stream twice). 
`teeing()` acts as a splitter valve, sending every drop of water to both buckets simultaneously, and then providing a mechanism to compare the buckets at the end.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why does my custom collector crash when I use `.parallel()`?"
*   **Clarification**: This is the classic "Combiner Trap." Beginners often ignore the third argument of `Collector.of()` because it isn't used in sequential streams. Emphasize that the stream engine *must* have a way to merge the results of Thread A and Thread B. If the combiner returns null or throws an exception, parallel execution fails.

### Block 2: "If I use `CONCURRENT`, is my stream faster?"
*   **Clarification**: This requires careful explanation. `CONCURRENT` means all threads dump data into the *same* box (accumulator). If that box is a standard `ArrayList`, it will crash. If it is a `ConcurrentHashMap`, it works, but multiple threads contending for the same lock might actually be *slower* than the default behavior (where each thread gets its own box and they are combined at the end). `CONCURRENT` is an optimization that requires profiling to prove its worth.

### Block 3: "Why did `groupingBy` cause an OutOfMemoryError?"
*   **Clarification**: Remind learners that streams are lazy, but `groupingBy` is a stateful terminal operation. It acts like a dam blocking a river. The river (stream) can be infinite, but the dam (the Map) must hold all the water in memory before releasing it. If the river is too big, the dam breaks (OOM).

## 5. Assessment Strategy
*   **Formative**: Provide a custom collector implementation that uses `ArrayList::new` as the supplier and has the `CONCURRENT` characteristic applied. Ask the learner what will happen if this is run in parallel.
*   **Summative**: The `MINI_PROJECT.md` requires the learner to build a Statistical Analyzer. They must successfully implement all four functions of `Collector.of()` and correctly merge values in the `combiner`, proving they understand the full lifecycle of stream reduction.