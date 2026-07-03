# Interview Preparation: Collection Performance Tuning

This document covers advanced questions related to JVM memory layout, Autoboxing overhead, capacity math, and Big O complexity in standard collections.

## Q1: Explain the mathematical formula for pre-sizing a `HashMap` to prevent resizing, and why it is necessary.
**Answer:**
A `HashMap` resizes when the number of elements exceeds its `threshold`. The threshold is calculated as `Capacity * LoadFactor` (default 0.75).
If you initialize a map with `new HashMap<>(100)` and add 100 elements, it will resize when you add the 76th element, allocating a new internal array of size 200 and rehashing all existing elements. This is very expensive.
To prevent resizing, you must set the initial capacity such that the `threshold` is greater than or equal to your expected number of elements. The formula is: `Math.ceil(ExpectedElements / LoadFactor)`. For 100 elements, this is `Math.ceil(100 / 0.75) = 134`. (Java 19+ provides `HashMap.newHashMap(100)` to do this automatically).

## Q2: Why is `List<Integer>` considered a memory anti-pattern for large datasets compared to `int[]`?
**Answer:**
Because of **Autoboxing** and **Object Overhead**.
An `int[]` stores primitive 4-byte integers contiguously in memory. It is extremely cache-friendly.
A `List<Integer>` stores references (pointers) to `Integer` objects. 
1.  An `Integer` object has a 16-byte object header (on a 64-bit JVM without compressed oops), a 4-byte `int` payload, and 4 bytes of padding to align to 8-byte boundaries. That's 24 bytes per integer.
2.  The array backing the `ArrayList` stores an 8-byte reference (pointer) to that object.
Total cost: ~32 bytes per integer, an 800% increase over the primitive `int`. Furthermore, chasing those pointers across the heap causes CPU cache misses, severely degrading traversal performance.

## Q3: You have a `LinkedList` and you iterate through it using `for (int i=0; i < list.size(); i++) { list.get(i); }`. What is the time complexity and why?
**Answer:**
The time complexity is **$O(N^2)$**.
The `for` loop runs $N$ times. Inside the loop, `list.get(i)` is called. Unlike an array, a `LinkedList` does not have random access. To find the element at index `i`, it must start at the head (or tail) and traverse node-by-node until it reaches `i`. This traversal takes $O(N)$ time.
Executing an $O(N)$ operation inside an $O(N)$ loop results in quadratic $O(N^2)$ time. You should **always** use an `Iterator` (or enhanced `for-each` loop) for `LinkedList` traversal, which maintains a pointer to the current node, resulting in $O(N)$ total time.

## Q4: How does `ArrayList.trimToSize()` help with memory leaks?
**Answer:**
When you add elements to an `ArrayList`, its internal backing array grows. If you add 1,000,000 elements, it allocates a massive array.
If you later call `list.clear()`, it sets all the references inside the array to `null` (so the objects can be garbage collected), and sets `size = 0`. **However, it does not shrink the physical array**. The massive `Object[]` remains in memory.
If this list is long-lived (e.g., a static cache or a field in a long-lived service), it acts as a memory leak. Calling `trimToSize()` forces the `ArrayList` to allocate a new, smaller array that exactly fits its current `size` (which would be 0 after `clear()`), allowing the massive array to be garbage collected.

## Q5: What is JMH (Java Microbenchmark Harness) and why must you use it instead of `System.currentTimeMillis()` for profiling?
**Answer:**
`System.currentTimeMillis()` (or `nanoTime()`) in a simple `main` method loop is highly inaccurate for profiling Java code due to the JVM's dynamic nature:
1.  **JIT Compilation**: The JVM starts in interpreted mode (slow). After a method is called many times, the Just-In-Time compiler compiles it to native machine code (fast). A simple timer will measure the slow interpreted time, not the actual production performance.
2.  **Dead Code Elimination**: If you write a loop that calculates a value but never uses or returns that value, the JIT compiler is smart enough to realize the code has no side effects and will literally delete the loop from the machine code, making your benchmark report 0 nanoseconds.
JMH handles JVM warmups, prevents dead code elimination (via `Blackhole`), and manages OS-level noise to provide scientifically accurate microbenchmarks.