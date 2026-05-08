# Deep Dive: Java 21 Features

Java 21 (LTS) introduces significant features that modernize Java development. This document covers each feature in depth.

---

## 1. Record Patterns (JEP 440)

### Overview
Record Patterns allow you to decompose records in instanceof and switch patterns.

### Basic Record Pattern
```java
record Point(int x, int y) {}

// Before Java 21
if (obj instanceof Point p) {
    int x = p.x();
    int y = p.y();
}

// With Record Pattern (Java 21)
if (obj instanceof Point(int x, int y)) {
    // Directly use x and y
    System.out.println(x + y);
}
```

### Nested Record Patterns
```java
record Inner(int value) {}
record Outer(Inner inner) {}

Object obj = new Outer(new Inner(42));

// Java 21 - Nested pattern
if (obj instanceof Outer(Inner(int value))) {
    System.out.println(value);  // 42
}
```

---

## 2. Pattern Matching for Switch (JEP 441)

### Overview
Pattern matching extends to switch statements and expressions.

### Type Patterns in Switch
```java
String process(Object obj) {
    return switch (obj) {
        case Integer i -> "Integer: " + i;
        case String s -> "String: " + s.length();
        case null -> "Null value";
        default -> "Unknown: " + obj.getClass().getSimpleName();
    };
}
```

### Guarded Patterns
```java
return switch (obj) {
    case String s when s.length() > 5 -> "Long string";
    case String s -> "Short string";
    case Integer i when i > 0 -> "Positive number";
    case Integer i -> "Non-positive";
    default -> "Other";
};
```

### Dominance Rules
```java
// More specific patterns must come first
case String s -> "Any string";       // Never reached - dominated by next
case String s when s.length() > 5 -> "Long string";
```

---

## 3. Sequenced Collections (JEP 431)

### New Interface Hierarchy
```
Iterable
    ↑
Collection
    ↑
SequencedCollection
    ↑
    ├── List
    ├── Set
    └── Deque
```

### New Methods
```java
// Get first/last elements
List<String> list = List.of("A", "B", "C");
list.getFirst();  // "A"
list.getLast();   // "C"

// Add at ends
list.addFirst("0");
list.addLast("D");

// Reversed view
SequencedCollection<String> reversed = list.reversed();
```

### Implementation Support
```java
// All these now implement SequencedCollection
ArrayList<String> arrayList = new ArrayList<>();
LinkedList<String> linkedList = new LinkedList<>();
LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
```

---

## 4. Virtual Threads (JEP 444)

### Overview
Virtual threads are lightweight threads that reduce thread creation overhead.

### Creating Virtual Threads
```java
// Create a virtual thread
Thread vt = Thread.ofVirtual().start(() -> {
    System.out.println("Running in virtual thread");
});

// Using Executors
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

executor.submit(() -> {
    // Run in virtual thread
    Thread.sleep(Duration.ofSeconds(1));
    return "done";
});
```

### Key Characteristics
```java
// Virtual threads are daemon threads by default
Thread virtual = Thread.ofVirtual().start(() -> {
    // Unlike platform threads, virtual threads don't block the scheduler
    // when blocking (Thread.sleep, I/O operations)
});

// Virtual threads are created quickly (millions can exist)
// vs platform threads (limited by OS)
```

### When to Use
```java
// Good for: High-throughput concurrent tasks
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
for (int i = 0; i < 10000; i++) {
    final int id = i;
    exec.submit(() -> process(id));
}

// Avoid for: CPU-intensive tasks (use platform threads)
```

---

## 5. Unnamed Variables (JEP 456)

### Overview
Unnamed variables allow you to ignore values when you don't need them.

### In Loops
```java
// Before - had to invent a variable name
for (String unused : items) {
    count++;
}

// Java 21 - use underscore
for (String _ : items) {
    count++;
}
```

### In Lambdas
```java
// Need parameter but don't use it
list.forEach(s -> System.out.println("Processing"));
list.forEach(_ -> System.out.println("Done"));
```

### In Patterns
```java
record Point(int x, int y) {}

Object obj = new Point(5, 10);

// Only care about x, ignore y
if (obj instanceof Point(int x, _)) {
    System.out.println(x);  // 5
}
```

### In try-with-resources
```java
try (var _ = new AutoCloseable() {
    @Override public void close() { cleanup(); }
}) {
    // Resource closed automatically
}
```

---

## 6. String Templates (Preview)

**Note**: String templates are a preview feature in Java 21.

```java
// Requires: --enable-preview
String name = "World";
int age = 30;

// String interpolation
String message = STR."Hello \{name}, you are \{age} years old";

// Multi-line
String html = STR."""
    <div>
        <h1>\{name}</h1>
        <p>Age: \{age}</p>
    </div>
    """;
```

---

## 7. Class-File API (Preview)

New API for reading and writing Java class files:

```java
ClassFile cf = ClassFile.of();
ClassModel cm = cf.parse(bytes);

// Process class structure
for (MethodModel method : cm.methods()) {
    System.out.println(method.methodName().stringValue());
}
```

---

## Performance Considerations

### Virtual Threads Performance
- **Startup time**: ~1ms vs ~100ms for platform threads
- **Memory**: ~230 bytes vs ~1MB per thread
- **Scalability**: Can handle millions of virtual threads

### Sequenced Collections
- `getFirst()`/`getLast()`: O(1) for ArrayList, LinkedList
- `reversed()`: O(1) view, no copy

---

## Migration Guide

| Feature | Migration |
|---------|-----------|
| Record Patterns | Update instanceof checks |
| Pattern Switch | Migrate from if-else chains |
| Virtual Threads | Replace thread-per-request model |
| Unnamed Variables | Use `_` for unused variables |
| Sequenced Collections | Use new methods, existing code works |

---

## Code Examples

### Complete Example: Pattern Matching
```java
sealed interface Shape permits Circle, Rectangle, Triangle {}
record Circle(int radius) implements Shape {}
record Rectangle(int width, int height) implements Shape {}
record Triangle(int base, int height) implements Shape {}

double area(Shape s) {
    return switch (s) {
        case Circle(int r) -> Math.PI * r * r;
        case Rectangle(int w, int h) -> w * h;
        case Triangle(int b, h) -> 0.5 * b * h;
    };
}
```

### Complete Example: Virtual Threads
```java
public class VirtualThreadDemo {
    public static void main(String[] args) throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new ArrayList<>();

            for (int i = 0; i < 1000; i++) {
                final int id = i;
                futures.add(executor.submit(() -> "Task " + id));
            }

            for (Future<String> f : futures) {
                System.out.println(f.get());
            }
        }
    }
}
```