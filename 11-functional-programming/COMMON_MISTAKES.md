# Common Mistakes in Functional Programming

## 1. Confusing Side Effects with FP

**Mistake**: Modifying state inside map
```java
int count = 0;
list.stream()
    .map(x -> {
        count++;  // Side effect!
        return x * 2;
    });
```

**Fix**: Use reduce or external collection in collect
```java
AtomicInteger count = new AtomicInteger(0);
list.stream()
    .map(x -> {
        count.incrementAndGet();
        return x * 2;
    });
// Better: just use count()
```

## 2. Forgetting Terminal Operations

**Mistake**: Stream never executes
```java
list.stream()
    .filter(x -> x > 5)
    .map(x -> x * 2);
// Nothing happens! Need terminal operation
```

**Fix**: Add terminal operation
```java
List<Integer> result = list.stream()
    .filter(x -> x > 5)
    .map(x -> x * 2)
    .collect(Collectors.toList());
```

## 3. Using forEach Instead of Proper Operations

**Mistake**: Using forEach for everything
```java
List<String> upper = new ArrayList<>();
list.forEach(s -> upper.add(s.toUpperCase()));
```

**Fix**: Use collect
```java
List<String> upper = list.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

## 4. Mutating in reduce

**Mistake**: Using reduce for side effects
```java
List<String> result = list.stream()
    .reduce(new ArrayList<>(), (acc, s) -> {
        acc.add(s.toUpperCase());  // Mutating accumulator!
        return acc;
    });
```

**Fix**: Use collect for mutable reduction
```java
List<String> result = list.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

## 5. Not Handling Empty Streams

**Mistake**: NPE with reduce on empty stream
```java
Integer min = list.stream()
    .min(Integer::compare)
    .get();  // Throws if empty!
```

**Fix**: Handle optional properly
```java
Integer min = list.stream()
    .min(Integer::compare)
    .orElse(0);  // Default value
```

## 6. Overusing flatMap

**Mistake**: Unnecessary flattening
```java
list.stream()
    .flatMap(x -> Stream.of(x * 2))  // Unnecessary
```

**Fix**: Use map when not really flattening
```java
list.stream()
    .map(x -> x * 2)
```