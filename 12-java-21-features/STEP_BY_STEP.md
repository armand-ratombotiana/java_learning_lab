# Step-by-Step: Learning Java 21 Features

## Step 1: Virtual Threads (3 hours)

**Goal**: Understand and use virtual threads

**Tasks**:
1. Create virtual threads manually
2. Use virtual thread executor
3. Compare to platform threads

**Practice**:
```java
Thread.ofVirtual().name("worker").start(() -> process());
ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor();
```

---

## Step 2: Sequenced Collections (2 hours)

**Goal**: Use ordered collection methods

**Tasks**:
1. Use getFirst/getLast
2. Use addFirst/addLast
3. Use reversed view

**Practice**:
```java
SequencedCollection<String> seq = new ArrayList<>();
seq.addFirst("first");
seq.getLast();
seq.reversed();
```

---

## Step 3: Record Patterns (3 hours)

**Goal**: Pattern matching with records

**Tasks**:
1. Use in instanceof
2. Use in switch
3. Handle nested patterns

**Practice**:
```java
if (obj instanceof Point(int x, int y)) {
    System.out.println(x + y);
}
```

---

## Step 4: Pattern Matching for Switch (2 hours)

**Goal**: Type-safe switch statements

**Tasks**:
1. Use type patterns in cases
2. Handle null explicitly
3. Add guard conditions

**Practice**:
```java
return switch(obj) {
    case null -> "null";
    case String s -> STR."String: \{s}";
    default -> "other";
};
```

---

## Step 5: String Templates (1 hour)

**Goal**: Use string interpolation

**Tasks**:
1. Enable preview feature
2. Use STR templates
3. Use expressions in templates

**Practice**:
```java
String msg = STR."Hello \{name}, age: \{age + 1}";
```

---

## Summary Checklist

- [ ] Create and use virtual threads
- [ ] Use sequenced collection methods
- [ ] Apply record patterns in instanceof
- [ ] Use pattern matching in switch
- [ ] Enable and use string templates