# Exercises: Java 21 Features

Practice exercises for Java 21 features.

---

## Exercise 1: Record Patterns

Create a program using record patterns with nested records.

```java
record Inner(int value) {}
record Outer(Inner inner, String name) {}

// Write a method that extracts value from Outer
```

**Solution**: See DEEP_DIVE.md

---

## Exercise 2: Pattern Matching for Switch

Convert this if-else chain to a switch expression:

```java
if (obj instanceof String) { ... }
else if (obj instanceof Integer) { ... }
else if (obj instanceof Double) { ... }
else { ... }
```

**Goal**: Use guarded patterns and null handling

---

## Exercise 3: Sequenced Collections

Create a FIFO queue implementation using sequenced methods.

**Methods to use**: `addFirst()`, `addLast()`, `getFirst()`, `getLast()`

---

## Exercise 4: Virtual Threads

Create a web server simulation handling 1000 concurrent requests.

**Use**: `Executors.newVirtualThreadPerTaskExecutor()`

---

## Exercise 5: Unnamed Variables

Rewrite this loop using unnamed variables:

```java
for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
    String s = it.next();
    System.out.println("Item");
}
```

---

## Exercise 6: Combined Exercise

Create a program demonstrating all Java 21 features together:

1. Use record patterns to process shapes
2. Use pattern matching switch to categorize
3. Use sequenced collections to manage a queue
4. Use virtual threads to process tasks concurrently
5. Use unnamed variables where appropriate

---

## Solutions

### Exercise 1: Record Patterns
```java
void process(Outer outer) {
    if (outer instanceof Outer(Inner(int v), String n)) {
        System.out.println(v + " - " + n);
    }
}
```

### Exercise 3: FIFO Queue
```java
class FIFO<T> {
    private final LinkedList<T> list = new LinkedList<>();

    public void enqueue(T item) { list.addLast(item); }
    public T dequeue() { return list.getFirst(); }
    public T peek() { return list.getFirst(); }
}
```

---

## Next Steps

- Review [DEEP_DIVE.md](./DEEP_DIVE.md) for more details
- Practice [QUIZZES.md](./QUIZZES.md) for assessment
- Check [EDGE_CASES.md](./EDGE_CASES.md) for pitfalls to avoid