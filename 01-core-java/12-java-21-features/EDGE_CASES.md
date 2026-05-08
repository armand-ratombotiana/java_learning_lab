# Edge Cases & Pitfalls: Java 21 Features

Common mistakes and how to avoid them when using Java 21 features.

---

## 1. Record Patterns

### ❌ Wrong
```java
// Forgetting to handle null
Object obj = null;
if (obj instanceof Point(int x, int y)) {  // NPE!
    System.out.println(x);
}
```

### ✅ Correct
```java
Object obj = null;
if (obj instanceof Point(int x, int y)) {
    System.out.println(x);
}  // null safely handled
```

### ❌ Wrong
```java
// Generic record with wildcard
record Wrapper<T>(T value) {}

Object obj = new Wrapper("test");
if (obj instanceof Wrapper<String>(String s)) {  // Error!
    System.out.println(s);
}
```

### ✅ Correct
```java
Object obj = new Wrapper("test");
if (obj instanceof Wrapper(var s)) {  // Type inference
    System.out.println(s);
}
```

---

## 2. Pattern Matching for Switch

### ❌ Wrong
```java
// Forgetting default case with null
String result = switch (obj) {
    case Integer i -> "Number";
    case String s -> "String";
    // No default - may throw NPE!
};
```

### ✅ Correct
```java
String result = switch (obj) {
    case Integer i -> "Number";
    case String s -> "String";
    case null -> "Null value";
    default -> "Other";
};
```

### ❌ Wrong
```java
// Wrong dominance order
return switch (obj) {
    case String s -> "String";  // More specific case
    case CharSequence cs -> "CharSeq";  // Never reached!
    default -> "Other";
};
```

### ✅ Correct
```java
return switch (obj) {
    case CharSequence cs -> "CharSeq";  // More general first
    case String s -> "String";  // More specific after
    default -> "Other";
};
```

### ❌ Wrong
```java
// Mixing statement and expression in different branches
switch (obj) {
    case Integer i -> System.out.println("Number");
    case String s -> "String";  // Mixed!
}
```

### ✅ Correct
```java
// Use expression consistently
String result = switch (obj) {
    case Integer i -> "Number";
    case String s -> "String";
    default -> "Other";
};
```

---

## 3. Sequenced Collections

### ❌ Wrong
```java
// Using on immutable list
List<String> list = List.of("A", "B");
list.getFirst();  // Works fine
list.addFirst("X");  // UnsupportedOperationException!
```

### ✅ Correct
```java
List<String> list = new ArrayList<>(List.of("A", "B"));
list.addFirst("X");  // Works
```

### ❌ Wrong
```java
// Forgetting reversed() is a view, not a copy
List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
List<Integer> reversed = list.reversed();
list.clear();
System.out.println(reversed);  // Empty!
```

### ✅ Correct
```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
List<Integer> reversed = new ArrayList<>(list.reversed());  // Copy
list.clear();
System.out.println(reversed);  // [3, 2, 1]
```

---

## 4. Virtual Threads

### ❌ Wrong
```java
// Using virtual threads for CPU-intensive work
Thread.ofVirtual().start(() -> {
    // Heavy computation - blocks virtual thread
    calculateFactorial(10000);
});
```

### ✅ Correct
```java
// Use platform threads for CPU-bound work
Thread.ofPlatform().start(() -> {
    calculateFactorial(10000);
});
```

### ❌ Wrong
```java
// Using synchronized in virtual threads - can cause pinning!
synchronized (lock) {
    // This pins the virtual thread to platform thread
    // Avoid in hot paths
}
```

### ✅ Correct
```java
// Use ReentrantLock instead
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    // work
} finally {
    lock.unlock();
}
```

### ❌ Wrong
```java
// Creating too many virtual threads in tight loop
for (int i = 0; i < 1000000; i++) {
    Thread.ofVirtual().start(() -> {});  // Could OOM!
}
```

### ✅ Correct
```java
// Use executor to manage lifecycle
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
for (int i = 0; i < 1000000; i++) {
    exec.submit(() -> {});
}
```

---

## 5. Unnamed Variables

### ❌ Wrong
```java
// Using _ outside valid context
int _ = 5;  // Error! Can't use _ as variable name
```

### ✅ Correct
```java
// _ is only valid for pattern matching and lambdas
for (String _ : list) { }  // Valid
```

### ❌ Wrong
```java
// Reusing _ for multiple parameters
(int a, _, int b) -> a + b;  // Each _ is distinct
```

### ✅ Correct
```java
// Unnamed variables are independent
(int _, int _) -> _ + _;  // Actually valid - two different _
```

### ❌ Wrong
```java
// Using _ in value position
String s = _;  // Error! Can't assign to _
```

### ✅ Correct
```java
// Can only use _ to ignore
if (obj instanceof String _) { }  // Ignores value
System.out.println("done");  // No need for placeholder
```

---

## Performance Anti-Patterns

### ❌ Wrong
```java
// Blocking in virtual thread without using executor
Thread.ofVirtual().start(() -> {
    Thread.sleep(1000);  // OK but creates temp thread
});
```

### ✅ Correct
```java
// Use proper executor
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    exec.submit(() -> {
        Thread.sleep(1000);
    });
}
```

---

## Checklist: Avoiding Pitfalls

- [ ] Handle null explicitly in pattern matching
- [ ] Order switch patterns from specific to general
- [ ] Always include default case in switch expressions
- [ ] Use mutable list for addFirst/addLast operations
- [ ] Remember reversed() is a view, not a copy
- [ ] Don't use virtual threads for CPU-intensive tasks
- [ ] Avoid synchronized in virtual threads (use ReentrantLock)
- [ ] Use executor for virtual thread lifecycle management
- [ ] Use _ only in valid contexts (patterns, lambdas, try-with-resources)
- [ ] Don't use _ as a variable name (it's reserved)

---

## Debugging Tips

### Virtual Thread Issues
```java
// Enable virtual thread debugging
java -XX:+PrintVirtualThreadInfo ...

// Check thread state
Thread.currentThread().isVirtual();  // true for virtual
```

### Pattern Matching Issues
```java
// Use IDE inspection for pattern dominance
// Most IDEs flag unreachable patterns
```

---

**Remember**: Java 21 features are powerful but require understanding their nuances. Test thoroughly!