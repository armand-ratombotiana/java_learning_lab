# Complexity Analysis — Internal Mechanics

## Master Theorem Details
T(n) = aT(n/b) + f(n) where a ≥ 1, b > 1

### Case 1: f(n) = O(n^c), c < log_b(a)
T(n) = Θ(n^{log_b(a)})

### Case 2: f(n) = Θ(n^c log^k n), c = log_b(a)
T(n) = Θ(n^c log^{k+1} n)

### Case 3: f(n) = Ω(n^c), c > log_b(a)
T(n) = Θ(f(n)) if af(n/b) ≤ kf(n) for some k < 1

## Amortized Analysis Methods

### Aggregate Method
Sum cost of all operations, divide by number of operations.

### Accounting Method
Assign different costs to operations. Some pay extra (credit), credit used for expensive operations.

### Potential Method
Define potential function Φ. Amortized cost = actual cost + ΔΦ.
