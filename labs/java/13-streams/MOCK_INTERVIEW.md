# Mock Interview Transcript: Streams

## Interviewer: Senior SWE, Meta
## Candidate: Mid-level Java developer
## Time: 40 minutes
## Focus: Stream pipeline, parallel streams, collectors

---

**Q1: How does Stream pipeline execution work? Explain lazy evaluation.**

**Candidate**: Streams use lazy evaluation. Intermediate operations (filter, map) don't execute until a terminal operation (collect, forEach, reduce) is called. When the terminal operation starts, it pulls elements through the pipeline one at a time. This allows optimizations like short-circuiting (limit, findFirst) and loop fusion (multiple operations applied to each element in a single pass).

**Interviewer**: Write a method that groups transactions by currency, summing amounts, only for transactions > 1000.

**Candidate**: 
```java
Map<Currency, Long> highValueByCurrency(List<Transaction> txns) {
    return txns.stream()
        .filter(t -> t.amount() > 1000)
        .collect(Collectors.groupingBy(
            Transaction::currency,
            Collectors.summingLong(Transaction::amount)
        ));
}
```

**Interviewer**: What's the time and space complexity?

**Candidate**: O(n) time — single pass through the stream. Space is O(m) where m is distinct currencies. The filter and grouping happen in one pass because of lazy evaluation.

**Interviewer**: How would you get the top 3 currencies by total amount?

**Candidate**: 
```java
Map<Currency, Long> byCurrency = ...;  // from above
List<Currency> top3 = byCurrency.entrySet().stream()
    .sorted(Map.Entry.<Currency, Long>comparingByValue().reversed())
    .limit(3)
    .map(Map.Entry::getKey)
    .toList();
```

**Interviewer**: What happens when you use `parallelStream()` on this?

**Candidate**: The stream is split using a Spliterator, processed in parallel via ForkJoinPool.commonPool(), and merged using the Collector's combiner. For this case: (1) the input list is split into chunks, (2) each chunk is filtered and accumulated into a local map, (3) maps are merged using `putAll`. Parallelism helps with large datasets but adds overhead for splitting and merging.

**Interviewer**: When would parallelStream be harmful?

**Candidate**: When: (1) dataset is small (overhead > benefit), (2) operations are I/O-bound (blocking common pool), (3) operations have ordering constraints, (4) shared mutable state exists in the pipeline, (5) the Spliterator has poor splitting characteristics (e.g., LinkedList), (6) the combining step is expensive.

**Interviewer**: Explain the difference between `findFirst()` and `findAny()` for parallel streams.

**Candidate**: `findFirst()` respects encounter order — it must find the first element in the stream's defined order. `findAny()` returns any element, which is more flexible for parallel processing because it can return the first result from any thread without coordinating order. For sequential streams, they often behave the same.

**Interviewer**: Implement a custom Collector that computes both average and count.

**Candidate**: 
```java
static <T> Collector<T, ?, Map<String, Double>> averagingWithCount(
        ToDoubleFunction<? super T> mapper) {
    return Collector.of(
        () -> new double[2],  // [sum, count]
        (acc, t) -> { acc[0] += mapper.applyAsDouble(t); acc[1]++; },
        (a, b) -> { a[0] += b[0]; a[1] += b[1]; return a; },
        acc -> Map.of("avg", acc[0] / acc[1], "count", acc[1])
    );
}
```

**Interviewer**: Good. One more: How does `Collectors.toList()` differ from `stream.toList()` (Java 16+)?

**Candidate**: `Collectors.toList()` returns a mutable `List` (ArrayList). `stream.toList()` (Java 16+) returns an unmodifiable list — you can't add or remove elements. Also, `toList()` is more direct and doesn't need the Collector infrastructure, so it's slightly faster.

---

## Feedback

**Strengths**:
- Clear explanation of lazy evaluation and pipeline execution
- Correct and idiomatic stream usage
- Understands parallel stream trade-offs
- Implements custom Collector correctly

**Areas for Improvement**:
- Could mention `teeing()` collector for multi-branch streams
- Should discuss `takeWhile()` and `dropWhile()` (Java 9+)

**Score**: 4/5 — Strong streams knowledge
