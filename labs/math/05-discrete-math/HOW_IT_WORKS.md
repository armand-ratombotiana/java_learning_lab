# How Discrete Mathematics Works

## Propositional Logic

Operators: $\land$ (AND), $\lor$ (OR), $\lnot$ (NOT), $\implies$ (implies), $\iff$ (iff)

**Modus ponens**: if $P \implies Q$ and $P$ is true, then $Q$ is true.

## Set Operations

$$
A \cup B = \{x \mid x \in A \lor x \in B\}
$$
$$
A \cap B = \{x \mid x \in A \land x \in B\}
$$

## Mathematical Induction

To prove $P(n)$ for all $n \ge 1$:
1. **Base**: Prove $P(1)$
2. **Induction**: Prove $P(k) \implies P(k+1)$

```java
// Proof by induction: sum of 1 to n = n(n+1)/2
public static int sumToN(int n) {
    // P(1): 1 = 1*2/2 = 1 ✓
    // Assume P(k): sum = k(k+1)/2
    // P(k+1): sum + (k+1) = k(k+1)/2 + (k+1) = (k+1)(k+2)/2 ✓
    return n * (n + 1) / 2;
}
```

## Modular Arithmetic

$$
a \equiv b \pmod{m} \iff m \mid (a - b)
$$

```java
// (a + b) mod m = ((a mod m) + (b mod m)) mod m
int modSum = (a % m + b % m) % m;
```
