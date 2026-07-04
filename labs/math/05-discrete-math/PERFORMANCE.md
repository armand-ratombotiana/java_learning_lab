# Discrete Math Performance

## Bit Manipulation for Sets

```java
// Represent a subset of {0, 1, ..., n-1} as a bitmask (int/long)
// Operations in O(1) instead of O(n)
long setA = 0b1101; // {0, 2, 3}
long setB = 0b1011; // {0, 1, 3}

long union   = setA | setB;       // 0b1111
long intersect = setA & setB;     // 0b1001
long diff    = setA & ~setB;      // 0b0100
int size     = Long.bitCount(setA); // popcount
```

## Efficient Prime Check

```java
// Check only up to sqrt(n), skip even numbers
public static boolean isPrime(int n) {
    if (n < 2) return false;
    if (n == 2) return true;
    if (n % 2 == 0) return false;
    for (int i = 3; i * i <= n; i += 2)
        if (n % i == 0) return false;
    return true;
}
```

## Memoization for Recursive Definitions

```java
// Fibonacci with memoization: O(n) vs O(2^n)
Map<Integer, Long> memo = new HashMap<>();
public long fib(int n) {
    if (n <= 1) return n;
    return memo.computeIfAbsent(n, k -> fib(k-1) + fib(k-2));
}
```
