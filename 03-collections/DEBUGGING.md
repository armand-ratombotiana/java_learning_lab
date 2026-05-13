# Debugging Collections in Java

## Common Failure Scenarios

### ArrayList Performance Issues

ArrayList is the most frequently used collection, but developers often encounter performance problems without understanding the underlying mechanics. The primary culprit is excessive resizing operations. When an ArrayList reaches its capacity, it creates a new array with 50% more space and copies all elements, which is an O(n) operation. If you know the approximate size in advance, always initialize with the appropriate constructor: `new ArrayList<>(expectedSize)`. This prevents repeated resizing during iteration or when adding elements in a loop.

Another common issue involves using ArrayList in multithreaded environments without synchronization. While the internal array operations are not atomic, the Java documentation explicitly warns against concurrent modifications. You may encounter `ConcurrentModificationException` when one thread iterates while another modifies, even accidentally through a for-each loop that compiles to an iterator. The stack trace shows the fail-fast behavior that detects modifications made after the iterator was created.

HashMap performance degradation occurs when the hash function produces many collisions. In the worst case, HashMap degenerates to a linked list with O(n) lookup time. This typically happens when using mutable objects as keys or when the hashCode() implementation is poorly designed. Always use immutable keys when possible, and ensure hashCode() distributes values evenly across the int range.

### Stack Trace Examples

**ConcurrentModificationException:**
```
Exception in thread "main" java.util.ConcurrentModificationException
    at java.util.ArrayList$Itr.checkForCommodification(ArrayList.java:1012)
    at java.util.ArrayList$Itr.next(ArrayList.java:1006)
    at com.example.CollectionsDemo.printAll(CollectionsDemo.java:25)
    at com.example.CollectionsDemo.main(CollectionsDemo.java:15)
```

**IndexOutOfBoundsException during remove:**
```
Exception in thread "main" java.lang.IndexOutOfBoundsException: Index: 5, Size: 3
    at java.util.ArrayList.rangeCheck(ArrayList.java:657)
    at java.util.ArrayList.get(ArrayList.java:433)
    at com.example.CollectionsDemo.processData(CollectionsDemo.java:42)
```

**ClassCastException with heterogeneous list:**
```
Exception in thread "main" java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
    at com.example.CollectionsDemo.sumList(CollectionsDemo.java:58)
```

## Debugging Techniques

### Identifying the Root Cause

When debugging collection issues, always start by examining the collection's state at the point of failure. Use logging or breakpoints to inspect the collection's internal array, size, and modCount. For ArrayList, look at the `elementData` array and `size` field. For HashMap, examine the `table` array, `size`, and `loadFactor`. Understanding these internal states often reveals the problem immediately.

The `modCount` field is particularly useful for diagnosing ConcurrentModificationException. This counter increments on every structural modification. When you create an iterator, it captures the current modCount. If it changes during iteration, the iterator throws the exception. Check whether modifications happen inside loops that also iterate, or if you accidentally modify a shared collection from multiple threads.

For HashMap debugging, use a breakpoint in your custom hashCode() method to verify it gets called and returns expected values. Poor hash distribution often stems from implementing hashCode() that only uses a subset of the object's fields, or using fields that don't vary much.

### Performance Profiling

When performance issues occur, use JVisualVM or YourKit to profile the application. Look for excessive time spent in methods like `ArrayList.ensureCapacity()`, `HashMap.resize()`, or `HashMap.put()`. If resize operations dominate, pre-size your collections. Use `-XX:+PrintGCDetails` and `-XX:+PrintGCTimeStamps` to see if collections are频繁, indicating memory pressure from collection operations.

## Best Practices

Always specify initial capacity for collections that will grow significantly. The formula `new ArrayList<>(expectedSize * 1.5f)` provides a comfortable margin without excessive memory waste. For HashMap, use `new HashMap<>(expectedSize, 0.8f)` to set both capacity and load factor explicitly.

Prefer immutable keys with HashMap to ensure consistent hash behavior. If you must use mutable objects as keys, never modify them after insertion. Document this constraint clearly in your code.

Use `Iterator.remove()` instead of `list.remove(index)` when removing elements during iteration. Using the index-based remove after modifying the collection can cause IndexOutOfBoundsException or skip elements.

For thread-safe collections, use the concurrent implementations from `java.util.concurrent`: ConcurrentHashMap, CopyOnWriteArrayList, BlockingQueue. These are designed for concurrent access and avoid the pitfalls of synchronized wrappers.

Remember that collections store references, not objects. Modifying an object retrieved from a collection affects the original. If you need isolation, create defensive copies before returning or storing references.