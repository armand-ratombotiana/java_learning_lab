# Deep Dive: Advanced Stream Collectors

## 1. Beyond `toList()` and `toSet()`
The `java.util.stream.Collectors` utility class provides many built-in collectors (`toList()`, `groupingBy()`, `joining()`). However, complex business logic often requires custom aggregations that the standard collectors cannot handle efficiently in a single pass. 

To master the Streams API, you must understand how to build your own `Collector`.

## 2. The `Collector<T, A, R>` Interface
A `Collector` is specified by three generic types:
*   `T`: The type of input elements from the stream.
*   `A`: The mutable accumulation type of the reduction operation (often hidden as an implementation detail).
*   `R`: The result type of the reduction operation.

A Collector is defined by four functions that work together to process elements:

1.  **`supplier()`**: Creates a new, empty mutable result container (e.g., `ArrayList::new`).
2.  **`accumulator()`**: Incorporates a new data element into a result container (e.g., `List::add`).
3.  **`combiner()`**: Combines two result containers into one. This is **only used during parallel execution** when multiple threads have processed different chunks of the stream and their partial results need to be merged (e.g., `(list1, list2) -> { list1.addAll(list2); return list1; }`).
4.  **`finisher()`**: Performs an optional final transform on the container to produce the final result type `R`. If the container `A` is the same as the return type `R`, this is just `Function.identity()`.

## 3. Creating a Custom Collector
You can create a custom collector using the `Collector.of()` factory method.

```java
// A custom collector that concatenates strings with a prefix and suffix, 
// using a StringBuilder as the internal accumulator.
Collector<String, StringBuilder, String> myJoiner = Collector.of(
    StringBuilder::new,               // supplier
    (sb, str) -> sb.append(str),      // accumulator
    (sb1, sb2) -> sb1.append(sb2),    // combiner (for parallel streams)
    sb -> "[" + sb.toString() + "]"   // finisher
);

String result = Stream.of("A", "B", "C").collect(myJoiner); // Result: "[ABC]"
```

## 4. Collector Characteristics
The `Collector.of()` method accepts an optional varargs of `Collector.Characteristics`. These tell the stream engine how it can optimize the execution.

*   **`CONCURRENT`**: Indicates that the *accumulator* function can be called concurrently by multiple threads on the *same* result container. (If not set, parallel streams will create separate containers and merge them using the *combiner*).
*   **`UNORDERED`**: Indicates that the collection operation does not commit to preserving the encounter order of input elements.
*   **`IDENTITY_FINISH`**: Indicates that the finisher function is the identity function (`x -> x`) and can be elided. The accumulator object `A` is directly cast to the result type `R`.

## 5. Teeing (Java 12+)
Introduced in Java 12, `Collectors.teeing()` is a composite collector. It takes two downstream collectors and a merger function. Every element from the stream is pushed into *both* downstream collectors. When the stream is exhausted, the merger function combines the results of the two collectors into a single object.

This is incredibly useful for calculating multiple metrics (like min and max, or sum and count) in a single pass of the stream.

```java
// Calculate the average by teeing a sum collector and a count collector
Double average = Stream.of(1, 2, 3, 4, 5).collect(
    Collectors.teeing(
        Collectors.summingDouble(i -> i),     // Downstream 1
        Collectors.counting(),                // Downstream 2
        (sum, count) -> sum / count           // Merger
    )
);
```