# Interview Questions: Java 21 Features

## Basic Questions

### Q1: What are virtual threads in Java 21?
**A**: Lightweight threads implemented by JDK. Unlike platform threads, many virtual threads share few OS threads. Created with Thread.ofVirtual() or Executors.newVirtualThreadPerTaskExecutor().

### Q2: How are virtual threads different from platform threads?
**A**: Platform threads map 1:1 to OS threads. Virtual threads are scheduled by JVM and run on carrier threads. Virtual threads use minimal memory (~1KB vs ~1MB).

### Q3: What is a sequenced collection?
**A**: An ordered collection that provides getFirst(), getLast(), addFirst(), addLast(), and reversed() methods. Interface: SequencedCollection.

### Q4: How do record patterns work in Java 21?
**A**: Deconstruct records in instanceof and switch. obj instanceof Point(int x, int y) directly extracts x and y from a Point record.

## Intermediate Questions

### Q5: When should you use virtual threads vs platform threads?
**A**: Use virtual threads for I/O-bound tasks (web servers, API calls). Use platform threads for CPU-intensive work where you need a dedicated OS thread.

### Q6: How does pattern matching for switch work?
**A**: Type patterns, null guards, and conditional logic in case labels. Must handle null explicitly with case null.

### Q7: What are the sequenced methods?
**A**: getFirst(), getLast(), addFirst(), addLast(), removeFirst(), removeLast(), reversed()

### Q8: Explain the String Templates feature
**A**: Enable with --add-exports. Use STR."Hello \{name}" for interpolation. Can include expressions like \{a + b}.

## Advanced Questions

### Q9: Can synchronized blocks cause problems with virtual threads?
**A**: Yes, virtual threads can be pinned in synchronized blocks, preventing them from being unmounted. Use java.util.concurrent locks instead.

### Q10: How does virtual thread scheduling work?
**A**: JVM scheduler runs virtual threads on carrier threads. When virtual thread blocks (I/O), JVM unmounts it and mounts another.

### Q11: What is the performance impact of ThreadLocal with virtual threads?
**A**: Each virtual thread has its own ThreadLocal values. Overuse can cause significant memory overhead with many threads.

### Q12: How do record patterns improve performance?
**A**: Compile-time pattern matching is faster than runtime reflection. JIT can optimize deconstruction better than manual extraction.