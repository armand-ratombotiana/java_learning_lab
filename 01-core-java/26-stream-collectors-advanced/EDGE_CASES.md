# Edge Cases & Pitfalls: Advanced Stream Collectors

Custom collectors and parallel streams introduce subtle edge cases that can lead to data corruption, performance degradation, or incorrect results if the Collector contracts are violated.

## 1. The Combiner Trap in Parallel Streams
*   **The Scenario**: You write a custom collector, but you only test it on sequential streams. You implement the `combiner` function by returning `null` or throwing an exception because "it's never called."
*   **The Pitfall**: The `combiner` is *only* called when the stream is executed in parallel (`stream.parallel().collect(...)`). If someone later changes your stream to run in parallel to boost performance, your code will crash or return `null`.
*   **Mitigation**: Always implement a robust `combiner` function that correctly merges two accumulator containers, even if you only plan to use the collector sequentially.

## 2. Mutating the Wrong Container in the Combiner
*   **The Scenario**: You implement the `combiner` function for a custom collector.
    ```java
    (list1, list2) -> {
        List<String> merged = new ArrayList<>();
        merged.addAll(list1);
        merged.addAll(list2);
        return merged;
    }
    ```
*   **The Pitfall**: While correct, this is highly inefficient. The contract of the `combiner` allows (and encourages) mutating and returning the first argument to avoid unnecessary object allocation.
*   **Mitigation**: Mutate `list1` directly: `(list1, list2) -> { list1.addAll(list2); return list1; }`.

## 3. Misusing the `CONCURRENT` Characteristic
*   **The Scenario**: You add the `Characteristics.CONCURRENT` flag to your custom collector to "make it faster," but your accumulator is a standard `ArrayList`.
*   **The Pitfall**: The `CONCURRENT` flag tells the stream engine: "Do not create multiple accumulators and combine them. Instead, let all threads in the parallel stream call the `accumulator` function on the *exact same* container simultaneously." Because `ArrayList` is not thread-safe, this will result in `ConcurrentModificationException` or silent data loss.
*   **Mitigation**: Only use the `CONCURRENT` characteristic if your accumulator container is inherently thread-safe (e.g., `ConcurrentHashMap`, `AtomicInteger`, or a synchronized collection).

## 4. `Collectors.toMap` Duplicate Key Collision
*   **The Scenario**: You use `Collectors.toMap(User::getDepartment, User::getName)`.
*   **The Pitfall**: If two users belong to the same department, the collector encounters a duplicate key. By default, `toMap` throws an `IllegalStateException: Duplicate key...`.
*   **Mitigation**: Always provide a merge function (the third argument to `toMap`) if there is any chance of duplicate keys.
    ```java
    Collectors.toMap(
        User::getDepartment, 
        User::getName, 
        (existingName, newName) -> existingName + ", " + newName // Merge logic
    )
    ```

## 5. `Collectors.groupingBy` Memory Exhaustion
*   **The Scenario**: You have a massive stream of millions of records, and you use `Collectors.groupingBy` to group them by a specific ID.
*   **The Pitfall**: `groupingBy` pulls the entire stream into memory, creating lists for every group. If the stream is larger than available heap space, you will get an `OutOfMemoryError`. Streams are designed to process infinite data, but stateful terminal operations like `groupingBy` break this capability.
*   **Mitigation**: If grouping massive datasets, do not use `groupingBy` in memory. Instead, process the stream in batches, or use a database `GROUP BY` query before pulling the data into Java.