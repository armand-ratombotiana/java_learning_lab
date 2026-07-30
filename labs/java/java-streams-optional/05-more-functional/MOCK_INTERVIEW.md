# MOCK_INTERVIEW — More Functional

## Scenario
Implement a memoized version of a recursive Fibonacci function and compare performance.

## Interviewer Notes
- Candidate should implement `Function<Long, BigInteger>` memoizer
- ConcurrentHashMap for thread safety
- Discuss stack overflow vs memoization limits

## Expected Solution Sketch
```java
Function<Long, BigInteger> fib = memoize(new Function<>() {
    public BigInteger apply(Long n) {
        if (n <= 1) return BigInteger.valueOf(n);
        return this.apply(n - 1).add(this.apply(n - 2));
    }
});
```

## Follow‑Up
- How to handle stack overflow for large n?
- What is the time complexity after memoization?
