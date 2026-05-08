# 📌 Java 21 Features - Quick Reference

Quick lookup for Java 21 LTS features.

---

## Virtual Threads (JEP 444)

```java
// Create virtual thread
Thread vt = Thread.ofVirtual().start(() -> task());

// Executor
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();

// Virtual thread per task
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    exec.submit(() -> work());
}
```

---

## Record Patterns (JEP 440)

```java
// In instanceof
if (obj instanceof Point(int x, int y)) {
    System.out.println(x + y);
}

// Nested patterns
if (obj instanceof Outer(Inner(int v))) {
    System.out.println(v);
}
```

---

## Pattern Matching for Switch (JEP 441)

```java
String result = switch (obj) {
    case Integer i -> "Number: " + i;
    case String s when s.length() > 5 -> "Long";
    case String s -> "Short";
    case null -> "Null";
    default -> "Other";
};
```

---

## Sequenced Collections (JEP 431)

```java
List<String> list = new ArrayList<>();
list.addFirst("A");     // Add to front
list.addLast("Z");      // Add to end
list.getFirst();        // O(1)
list.getLast();         // O(1)
list.reversed();        // View (not copy)
```

---

## Unnamed Variables (JEP 456)

```java
// In loops
for (String _ : items) { count++; }

// In patterns
if (obj instanceof Point(int x, _)) { }

// In lambdas
list.forEach(_ -> System.out.println("done"));
```

---

## Key Methods Quick Ref

| Feature | Method |
|---------|--------|
| Collection | `getFirst()`, `getLast()`, `reversed()` |
| Pattern | `case Type(var)` |
| Switch | `case Type when condition ->` |
| Thread | `Thread.ofVirtual()` |
| Executor | `newVirtualThreadPerTaskExecutor()` |

---

## JEP Reference

| JEP | Feature |
|-----|---------|
| 444 | Virtual Threads |
| 440 | Record Patterns |
| 441 | Pattern Matching for Switch |
| 431 | Sequenced Collections |
| 456 | Unnamed Variables |

---

**Remember**: Java 21 is LTS - use in production!