# How Recursion Works

## Factorial Call Stack
`
factorial(4) → 4 * factorial(3)
  factorial(3) → 3 * factorial(2)
    factorial(2) → 2 * factorial(1)
      factorial(1) → 1
    ← returns 2
  ← returns 6
← returns 24
`

## Fibonacci (inefficient)
`java
public static int fib(int n) {
    if (n <= 1) return n;
    return fib(n - 1) + fib(n - 2);
}
`
fib(5) calls fib(4) and fib(3); fib(4) calls fib(3) and fib(2) — many redundant calls (O(2ⁿ)).
