# Interview Preparation: Advanced Stream Collectors

This document covers advanced questions related to the `Collector` interface, parallel streams, and memory management during stream operations.

## Q1: Explain the four main functions required to build a custom `Collector` using `Collector.of()`.
**Answer:**
1.  **Supplier**: A function that creates and returns a new, empty mutable result container (accumulator). E.g., `ArrayList::new`.
2.  **Accumulator**: A function that folds an element from the stream into the result container. E.g., `(list, element) -> list.add(element)`.
3.  **Combiner**: A function that accepts two partial result containers and merges them into one. This is *only* used during parallel stream execution. E.g., `(list1, list2) -> { list1.addAll(list2); return list1; }`.
4.  **Finisher**: An optional function that performs a final transformation on the accumulator container to produce the final result type. E.g., converting a `StringBuilder` to a `String`.

## Q2: When writing a custom collector, what is the danger of not implementing the `combiner` function properly (e.g., returning `null`)?
**Answer:**
If you only test the collector on sequential streams, it will work perfectly because the `combiner` is never called. However, if another developer later changes the stream to `parallel()`, the stream engine will split the data, process chunks in separate threads, and then attempt to call your `combiner` to merge the partial results. If it returns `null` or throws an exception, the entire parallel stream execution will crash. A `combiner` must always be correctly implemented for robust code.

## Q3: What does the `Characteristics.CONCURRENT` flag do in a custom Collector?
**Answer:**
By default, a parallel stream gives every thread its own separate accumulator container (created via the `supplier`), and then merges them using the `combiner`.
If you apply the `CONCURRENT` characteristic, you tell the stream engine to use a *single* accumulator container for the entire stream, and allow all threads to call the `accumulator` function on that single container concurrently.
**Warning**: You must *only* use this flag if your accumulator container is inherently thread-safe (like a `ConcurrentHashMap`). If you use it with an `ArrayList`, you will get a `ConcurrentModificationException` or data corruption.

## Q4: Why might `Collectors.groupingBy` cause an `OutOfMemoryError` on a very large stream?
**Answer:**
Streams are designed to process data lazily, meaning they can theoretically process an infinite stream of data without exhausting memory, provided you use stateless operations (like `filter` or `map`).
However, `groupingBy` is a stateful terminal operation. It must pull every single element from the stream, evaluate its grouping key, and store the element in memory (usually in a `List` inside a `Map`). If the stream contains 100 million records, all 100 million records will be loaded into the heap simultaneously. If the heap is too small, the application will crash.

## Q5: What problem does `Collectors.teeing()` solve?
**Answer:**
Before Java 12, if you needed to calculate two different metrics from a stream (e.g., finding the maximum value AND the minimum value), you had to either iterate over the data twice (creating two separate streams) or write a complex, custom collector that tracked both values manually.
`teeing()` solves this by acting as a composite collector. It takes two downstream collectors and a merger function. It pushes every element into *both* downstream collectors in a single pass, and then uses the merger function to combine their final results into a single object.