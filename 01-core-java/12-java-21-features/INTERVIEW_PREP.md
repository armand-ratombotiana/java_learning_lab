# Module 12: Java 21 Features - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What are Virtual Threads (Project Loom) and how do they differ from traditional Platform Threads?
**Answer**:
- **Platform Threads**: Are 1:1 mapped to OS threads. They are expensive to create, consume a lot of memory (~1MB stack size), and rely on the OS for context switching.
- **Virtual Threads**: Are lightweight threads managed by the JVM, not the OS. Millions of virtual threads can be created on a single machine. When a virtual thread hits a blocking operation (like I/O or sleep), the JVM "unmounts" it from the underlying carrier platform thread, allowing another virtual thread to use the CPU. This enables a massive scale of concurrent I/O operations without callback-hell or reactive programming paradigms.

### Q2: What are Sequenced Collections?
**Answer**:
Before Java 21, Java lacked a unified interface for collections that have a defined encounter order (like `List`, `Deque`, `LinkedHashSet`). Finding the first or last element required different methods depending on the collection type.
Java 21 introduced `SequencedCollection`, `SequencedSet`, and `SequencedMap` interfaces. These provide uniform methods like `.getFirst()`, `.getLast()`, `.addFirst()`, `.addLast()`, and `.reversed()` to get a reversed view of the collection.

### Q3: Explain Record Patterns and how they enhance switch expressions.
**Answer**:
Record Patterns allow you to destructure a Record into its component parts directly within an `instanceof` check or a `switch` case.
Instead of doing:
```java
if (obj instanceof Point p) {
    int x = p.x();
    int y = p.y();
}
```
You can destructure it cleanly:
```java
if (obj instanceof Point(int x, int y)) {
    // x and y are immediately available
}
```
This applies to `switch` expressions too, allowing deep, nested pattern matching and the use of guard clauses (`when x > 10`) to create highly readable declarative logic.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Refactoring a Legacy Switch
**Problem**: Refactor the following Java 8 code using Java 21 pattern matching, switch expressions, and unnamed variables.

```java
public String format(Object obj) {
    if (obj == null) {
        return "Null";
    }
    if (obj instanceof Integer) {
        Integer i = (Integer) obj;
        return "Int: " + i;
    }
    if (obj instanceof String) {
        String s = (String) obj;
        if (s.isEmpty()) {
            return "Empty";
        }
        return "String: " + s;
    }
    return "Unknown";
}
```

**Solution**:
```java
public String format(Object obj) {
    return switch (obj) {
        case null -> "Null";
        case Integer i -> "Int: " + i;
        // Guarded pattern
        case String s when s.isEmpty() -> "Empty";
        case String s -> "String: " + s;
        // Unnamed variable for default type
        case Object _ -> "Unknown"; 
    };
}
```

### Scenario 2: Virtual Thread Execution
**Problem**: Write a code snippet to fire off 1,000 asynchronous tasks that each sleep for 1 second, and wait for them all to finish using Virtual Threads.

**Solution**:
Use the structured concurrency pattern via a try-with-resources block and the new executor.

```java
public void runTasks() {
    // The executor automatically waits for all submitted tasks to finish before closing
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                Thread.sleep(Duration.ofSeconds(1));
                return "Done";
            });
        }
    }
}
```