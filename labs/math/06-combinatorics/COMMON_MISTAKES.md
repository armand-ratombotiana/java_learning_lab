# Common Mistakes in Combinatorics

## Permutation vs Combination Confusion

```java
// Permutations: order matters
// Combinations: order doesn't matter
// Choosing 3 from 10 for president/VP/treasurer: P(10,3) = 720
// Choosing 3 from 10 for a committee: C(10,3) = 120
```

## Integer Overflow in Factorial

```java
// WRONG: 20! overflows long
long fact = factorial(20); // 2.43e18 > Long.MAX_VALUE

// RIGHT: use BigInteger
BigInteger fact = BigInteger.ONE;
for (int i = 2; i <= n; i++)
    fact = fact.multiply(BigInteger.valueOf(i));
```

## Forcing Order When Not Needed

```java
// WRONG: 52*51*50*49*48 when counting poker hands
// (order doesn't matter in a hand)
// RIGHT: C(52, 5)
```

## Double Counting in Inclusion-Exclusion

```java
// WRONG: |A∪B∪C| = |A|+|B|+|C| - |A∩B| - |A∩C| - |B∩C|
// Forgetting + |A∩B∩C|
```
