# Combinatorics

The mathematics of counting, arrangement, and combination of objects.

## Scope

- Permutations and combinations
- Binomial theorem and Pascal's triangle
- Inclusion-exclusion principle
- Recurrence relations
- Generating functions

## Java Implementation

```java
public class Combinatorics {
    public static long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }
    public static long binomial(int n, int k) {
        if (k < 0 || k > n) return 0;
        k = Math.min(k, n - k);
        long result = 1;
        for (int i = 1; i <= k; i++)
            result = result * (n - k + i) / i;
        return result;
    }
}
```
