# GUIDE — More Functional

## Step 1: Function Composition
```java
Function<Integer, Integer> add1 = x -> x + 1;
Function<Integer, Integer> doubleIt = x -> x * 2;

Function<Integer, Integer> add1ThenDouble = add1.andThen(doubleIt);
Function<Integer, Integer> doubleThenAdd1 = add1.compose(doubleIt);
```

## Step 2: Currying
```java
Function<Integer, Function<Integer, Integer>> curryAdd =
    a -> b -> a + b;
int sum = curryAdd.apply(3).apply(4); // 7
```

## Step 3: Memoization
```java
public static <T, R> Function<T, R> memoize(Function<T, R> fn) {
    Map<T, R> cache = new ConcurrentHashMap<>();
    return t -> cache.computeIfAbsent(t, fn);
}
```

## Step 4: Functional Interfaces
- `UnaryOperator<T>` — `Function<T, T>`
- `IntFunction<R>` — primitive input
- `BiPredicate<T, U>`, `BinaryOperator<T>`

## Step 5: Exercises
1. Compose `trim` -> `toUpperCase` -> `getFirstWord`
2. Implement a curried `discount` function
3. Memoize a recursive Fibonacci function
