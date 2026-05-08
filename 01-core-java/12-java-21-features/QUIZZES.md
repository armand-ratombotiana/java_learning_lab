# Quizzes: Java 21 Features

Test your understanding of Java 21 features.

---

## Level 1: Beginner

### Q1: Record Patterns
What does this code output?

```java
record Point(int x, int y) {}

Object obj = new Point(3, 4);

if (obj instanceof Point(int x, int y)) {
    System.out.println(x + y);
}
```

A) 7
B) 34
C) Compilation error
D) null

---

### Q2: Virtual Threads
Which statement about virtual threads is TRUE?

A) Virtual threads require more memory than platform threads
B) Virtual threads block the OS scheduler when sleeping
C) Virtual threads can be created in millions
D) Virtual threads cannot use synchronized

---

### Q3: Sequenced Collections
What does this code output?

```java
List<String> list = new LinkedList<>();
list.add("A");
list.add("B");
System.out.println(list.getLast());
```

A) "A"
B) "B"
C) IndexOutOfBoundsException
D) null

---

## Level 2: Intermediate

### Q4: Pattern Matching for Switch
What's the output?

```java
String result = switch (obj) {
    case Integer i -> "Number: " + i;
    case String s when s.length() > 3 -> "Long string";
    case String s -> "Short: " + s;
    default -> "Other";
};

System.out.println(result);

Object obj = "Hello";
```

A) Number: Hello
B) Long string
C) Short: Hello
D) Other

---

### Q5: Unnamed Variables
Which is valid syntax?

A) `int _ = 5;`
B) `for (_ <- list) { }`
C) `if (obj instanceof Point(int _, int y)) { }`
D) `() -> _ + 1`

---

### Q6: Virtual Thread Executor
What happens?

```java
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
exec.submit(() -> Thread.sleep(100));
exec.shutdown();
```

A) Works fine
B) IllegalStateException - not started
C) RejectedExecutionException
D) NullPointerException

---

## Level 3: Advanced

### Q7: Nested Record Patterns
What does this output?

```java
record Inner(int value) {}
record Outer(Inner inner) {}

Object obj = new Outer(new Inner(42));

if (obj instanceof Outer(Inner(int v))) {
    System.out.println(v);
}
```

A) 42
B) Inner(42)
C) null
D) Compilation error

---

### Q8: Pattern Dominance
Which case is unreachable?

```java
return switch (obj) {
    case String s -> "String";
    case String s when s.isEmpty() -> "Empty string";
    case Integer i -> "Integer";
    default -> "Other";
};
```

A) case String s -> "String"
B) case String s when s.isEmpty() -> "Empty string"
C) case Integer i -> "Integer"
D) None are unreachable

---

### Q9: Virtual Thread Behavior
Which is NOT a characteristic of virtual threads?

A) Created as daemon threads
B) Cannot use ThreadLocal
C) Support thread priority
D) Use JVM-managed scheduling

---

## Level 4: Interview

### Q10: When to Use Virtual Threads

When is it appropriate to use virtual threads?

A) When running CPU-intensive computations
B) When you need to block on I/O operations
C) When you need exact control over thread scheduling
D) When thread count is less than CPU cores

---

### Q11: Record Patterns vs Traditional instanceof

What advantage do record patterns provide over traditional instanceof?

A) Better performance
B) More concise destructuring
C) Type safety at compile time
D) All of the above

---

### Q12: Migration from Legacy Code

How would you migrate this code to Java 21?

```java
for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
    String s = it.next();
    System.out.println(s);
}
```

A) `for (var _ : list) System.out.println(_);`
B) `list.forEach(_ -> System.out.println(_));`
C) `for (String _ : list) System.out.println(_);`
D) `list.stream().forEach(System.out::println);`

---

## Answer Key

| Q | Answer | Explanation |
|---|--------|-------------|
| 1 | A | Record pattern extracts x=3, y=4, so 3+4=7 |
| 2 | C | Virtual threads are lightweight, can create millions |
| 3 | B | getLast() returns last element "B" |
| 4 | B | "Hello" has length 5, matches guarded pattern |
| 5 | C | Unnamed variable in pattern is valid |
| 6 | B | Must start executor before shutdown |
| 7 | A | Nested pattern extracts inner value 42 |
| 8 | A | General String pattern dominates guarded |
| 9 | B | Virtual threads CAN use ThreadLocal |
| 10 | B | Virtual threads excel at I/O-bound tasks |
| 11 | D | Record patterns offer all these advantages |
| 12 | C | Valid syntax with unnamed pattern variable |

---

## Next Steps

- Practice with [DEEP_DIVE.md](./DEEP_DIVE.md)
- Review [EDGE_CASES.md](./EDGE_CASES.md)
- Try the exercises in [EXERCISES.md](./EXERCISES.md)