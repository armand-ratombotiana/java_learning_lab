# Common Mistakes — Java Streams

## 1. Reusing a Stream After Terminal Operation
```java
Stream<String> stream = list.stream();
stream.forEach(System.out::println);
long count = stream.count(); // IllegalStateException
```
**Fix:** Create a new stream for each terminal operation.

## 2. Mutating State in Lambdas
```java
List<Integer> result = new ArrayList<>();
stream.filter(x -> x > 5).forEach(result::add); // Not thread-safe
```
**Fix:** Use `collect(Collectors.toList())`.

## 3. Forgetting Intermediate Operations Are Lazy
```java
stream.filter(x -> { System.out.println("filtering"); return true; });
// Nothing prints — no terminal op
```

## 4. Using `parallelStream()` Without Understanding Thread Safety
Non-concurrent collectors or shared mutable state cause data corruption.

## 5. Assuming Encounter Order Is Preserved
`findFirst()` versus `findAny()` — parallel streams break order unless the source is ordered.

## 6. Using `peek()` for Production Logic
`peek()` is meant for debugging only; it is not a substitute for `map()`.

## 7. Converting boxed streams inefficiently
```java
int[] array = list.stream().mapToInt(Integer::intValue).toArray();
// Preferred over boxing/unboxing loops.
```
