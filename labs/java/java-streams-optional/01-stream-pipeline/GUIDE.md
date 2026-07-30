# GUIDE — Stream Pipeline

## Step 1: Stream Sources
```java
Stream.of("a","b"); List.stream(); Arrays.stream(arr);
Pattern.compile(",").splitAsStream("a,b");
Files.lines(Path.of("file.txt"));
Stream.iterate(0, n -> n + 1).limit(10);
Stream.generate(Math::random).limit(5);
```

## Step 2: Intermediate Operations
- `filter(Predicate)`, `map(Function)`, `flatMap(Function)`
- `distinct()`, `sorted()`, `peek(Consumer)`, `limit(long)`, `skip(long)`

## Step 3: Terminal Operations
- `collect(toList())`, `forEach(System.out::println)`, `count()`
- `reduce(identity, accumulator)`, `anyMatch(pred)`, `findFirst()`

## Step 4: Lazy Evaluation
- Intermediate ops don't execute until a terminal op is called
- `peek()` shows pipeline execution order — elements flow one‑by‑one

## Step 5: Exercises
1. Stream pipeline to get top‑5 longest words from a file
2. Filter + map + reduce chain for invoice totals
3. Compare sequential vs lazy behaviour with `peek()`
