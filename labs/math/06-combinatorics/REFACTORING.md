# Refactoring Combinatorics Code

## Centralize Counting Functions

```java
// BEFORE: scattered factorial calculations
long ways1 = factorial(10) / (factorial(3) * factorial(7));
long ways2 = factorial(52) / (factorial(5) * factorial(47));

// AFTER: dedicated combinatorics utility class
public final class Combinatorics {
    private Combinatorics() {}
    public static long permutations(int n, int k) { ... }
    public static long combinations(int n, int k) { ... }
    public static long factorial(int n) { ... }
}
```

## Use BigInteger for Large Numbers

```java
// BEFORE: long overflow
long result = combinations(100, 50); // overflow!

// AFTER
BigInteger result = Combinatorics.bigCombinations(100, 50);
```

## Convert Recursive to Iterative

```java
// BEFORE: recursive (stack overflow for large n)
public static long fib(int n) {
    if (n <= 1) return n;
    return fib(n-1) + fib(n-2);
}

// AFTER: iterative
public static long fib(int n) {
    if (n <= 1) return n;
    long a = 0, b = 1;
    for (int i = 2; i <= n; i++) { long t = a + b; a = b; b = t; }
    return b;
}
```
