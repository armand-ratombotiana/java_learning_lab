# Step-by-Step: Learning Functional Programming

## Step 1: Understand Pure Functions (2 hours)

**Goal**: Write functions with no side effects

**Tasks**:
1. Identify side effects in existing code
2. Refactor to remove state changes
3. Write functions that only compute

**Practice**:
```java
// Pure - just computation
int add(int a, int b) { return a + b; }

// Impure - has side effect
int addAndPrint(int a, int b) { 
    System.out.println(a + b);  // side effect
    return a + b; 
}
```

---

## Step 2: Immutable Data (2 hours)

**Goal**: Create immutable objects

**Tasks**:
1. Use Java records (Java 14+)
2. Use final fields with builders
3. Return new objects instead of modifying

**Practice**:
```java
record Person(String name, int age) {
    Person withName(String name) {
        return new Person(name, this.age);
    }
}
```

---

## Step 3: Stream Operations (4 hours)

**Goal**: Master filter, map, reduce

**Tasks**:
1. Convert loops to streams
2. Chain multiple operations
3. Use collect for results

**Practice**:
```java
list.stream()
    .filter(x -> x > 5)
    .map(x -> x * 2)
    .reduce(0, Integer::sum);
```

---

## Step 4: Function Composition (3 hours)

**Goal**: Compose simple functions

**Tasks**:
1. Use andThen and compose
2. Create transformation pipelines
3. Extract complex lambdas to methods

**Practice**:
```java
Function<String, String> pipeline = 
    String::trim
    .andThen(String::toLowerCase)
    .andThen(s -> s.replace(" ", "-"));
```

---

## Step 5: Optional (2 hours)

**Goal**: Handle nulls functionally

**Tasks**:
1. Replace null checks with Optional
2. Use map, flatMap, orElse
3. Chain optional operations

**Practice**:
```java
String result = user
    .flatMap(User::getAddress)
    .flatMap(Address::getCity)
    .orElse("Unknown");
```

---

## Step 6: Parallel Streams (2 hours)

**Goal**: Use parallel safely

**Tasks**:
1. Identify when parallel helps
2. Ensure functions are stateless
3. Avoid shared mutable state

**Practice**:
```java
list.parallelStream()
    .filter(this::isValid)
    .map(this::process)
    .collect(Collectors.toList());
```

---

## Step 7: Real Application (4 hours)

**Goal**: Apply FP to real problems

**Tasks**:
1. Refactor service methods to use streams
2. Create immutable domain objects
3. Use composition for business logic

---

## Summary Checklist

- [ ] Write pure functions
- [ ] Use immutable data types
- [ ] Use stream operations (filter, map, reduce)
- [ ] Compose functions
- [ ] Handle nulls with Optional
- [ ] Use parallel streams safely