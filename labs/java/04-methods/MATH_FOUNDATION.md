# Math Foundation for Methods

## Recursion

Understanding recursive methods requires basic induction:

- **Base case**: The simplest input that returns directly
- **Recursive case**: The problem reduced toward the base case

```java
// factorial: n! = n * (n-1)!, where 0! = 1
int factorial(int n) {
    if (n <= 1) return 1;          // Base case
    return n * factorial(n - 1);    // Recursive case
}
```

## Fibonacci

```java
int fib(int n) {
    if (n <= 1) return n;
    return fib(n - 1) + fib(n - 2);
}
```

No specific math foundation required beyond basic arithmetic. Recursion is a conceptual tool, not a mathematical prerequisite.
