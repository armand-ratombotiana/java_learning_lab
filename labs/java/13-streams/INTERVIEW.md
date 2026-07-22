# Interview Questions: Streams

## Company-Specific Focus

### Google
- Short-circuiting operations: findFirst, findAny, limit and their lazy evaluation signatures
- Parallel stream performance vs sequential — determining the optimal processing strategy
- The pitfalls of boxing and object overhead in primitive data streams (IntStream, DoubleStream, etc.)

### Microsoft
- Stream API vs LINQ: the similarities and differences in declarative data processing
- Java Stream efficiency vs. the equivalent for loops with collections
- Collectors as terminal operations to group, partition, and count

### Amazon
- Using streams in data processing and aggregating time series in the cloud
- Lazy processing using a stream: how to avoid "might be very heavy" upfront evaluation
- Parallel stream performance with ForkJoinPool customization

### Meta
- Stream API vs. traditional for-each performance considerations at scale
- Observe limited cloning with the map and collect pattern
- Golden rule: reduce from stream means Optional - handle NoSuchElement

### Apple
- Using immutable collectors and streams over unmodifiable wrappers
- The pixel of List.of used versus Collectors.toUnmodifiableList
- Using streams with Optional; how flatMap collapse

### Oracle
- The streams contracts in the Java language specification
- The signature of the stream pipeline factory in the Stream interface architecture
- The Result of Stream.extend in the JIT elimination of the pipeline objects
- Stream flags in the internal expression Sink pipeline

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 217 Contains Duplicate | Easy | Google, Facebook, Amazon | Streams-based distinct count |
| 49 Group Anagrams | Medium | Amazon, Microsoft, Bloomberg | Stream collection to map using groupingBy |
| 347 Top K Frequent Words | Medium | Apple, Google | Stream ranking using sorted computation |
| 242 Valid Anagram | Easy | Google, Amazon | Character sequence map-based stream |
| 692 Top K Frequent Words | Medium | Amazon, Apple | Group by count with sorted |

## Real Production Scenarios
- **LinkedIn**: Switching a 10M document pipeline to use parallel stream turned an O(n) process into two huge pauses because of ForkJoinPool instances
- **Uber**: An element by a for loop goes an order faster than a pipeline because of the overheads of creating streams objects in a hot loop (10k impulses)
- **Slack**: A developer used parallelStream() inside a transaction log update, causing false sharing and blocking

## Interview Patterns & Tips
- **Lazy evaluation is your friend**: none of the scanning is done in the chain unless a terminal operator is added
- **stream().iterator() vs forEach side-effect**: Use official APIs for safety
- **Filter before map**: use a small subset before invoking mapping/deep clones
- **Parallel for I/O**: doesn't help when using a shared resource; yet it works at CPU-bound pure transformation
- **flatMap + filter + map = pipeline performance impact**: O(1) per n is always linear for each element in your sequence.

## Deep Dive Questions
- **Implementation**: How we understand the Pipeline and its abstract methods and Spliterator for parallelism?
- **JVM**: What objects does the JVM allocate when it processes a stream? Does the JIT optimize away intermediate objects if possible?
- **JIT**: Can the JIT compiler static final sink and string the loop's end like for-loops?
- **Performance**: Evaluate when push-based sink evaluation is better vs. pull-based iteration.
- **Avoiding hidden boxing**: How to properly use IntStream and the boxed pipeline.
