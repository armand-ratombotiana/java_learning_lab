# Recursion — Internal Mechanics

## Stack Frame
Each recursive call creates a frame with:
- Return address
- Local variables
- Parameters
- Return value slot

## Java Stack Limits
- Default: ~1MB per thread
- ~10,000-20,000 calls before StackOverflowError
- Can increase with -Xss flag

## Tail Recursion (NOT optimized in Java)
`java
public static int factorialTail(int n, int acc) {
    if (n <= 1) return acc;
    return factorialTail(n - 1, n * acc);
}
`
Java does NOT perform tail call optimization. Use iteration for deep recursion.

## Recursion vs Iteration
`java
// Iterative factorial — preferred in Java for deep recursion
public static int factorialIter(int n) {
    int result = 1;
    for (int i = 2; i <= n; i++) result *= i;
    return result;
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Recursion

## Recurrence Relations
- Factorial: T(n) = T(n-1) + O(1) → O(n)
- Binary Search: T(n) = T(n/2) + O(1) → O(log n)
- Merge Sort: T(n) = 2T(n/2) + O(n) → O(n log n)
- Fibonacci (naive): T(n) = T(n-1) + T(n-2) + O(1) → O(φⁿ)

## Master Theorem
T(n) = aT(n/b) + f(n)
- If f(n) = O(n^c) where c < log_b(a): Θ(n^{log_b(a)})
- If f(n) = Θ(n^c log^k n) where c = log_b(a): Θ(n^c log^{k+1} n)
- If f(n) = Ω(n^c) where c > log_b(a): Θ(f(n))
