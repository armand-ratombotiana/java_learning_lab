# Debugging Combinatorics in Java

## Common Issues

### Overflow Without Warning

```java
// n=21: factorial(21) overflows long silently
long f = 1;
for (int i = 2; i <= 21; i++) f *= i;
// f is now 51,090,942,171,709,440,000 — but wrong due to overflow!
```

Fix: use `BigInteger` or check bounds.

### Division Before Multiplication

```java
// WRONG: integer division truncates
long result = (n * (n-1) * (n-2)) / 6; // may overflow before division

// RIGHT: divide as you go
long result = 1;
result = result * n / 1;
result = result * (n-1) / 2;
result = result * (n-2) / 3;
```

## Debugging Checklist

- [ ] Permutation vs combination correct?
- [ ] Overflow in factorial or binomial?
- [ ] Division performed at the right time?
- [ ] Inclusion-exclusion complete (alternating signs)?
- [ ] Distinguishable vs indistinguishable objects?
- [ ] With or without replacement?
