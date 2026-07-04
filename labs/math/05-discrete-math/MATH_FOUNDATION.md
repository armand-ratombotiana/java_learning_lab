# Math Foundation of Discrete Mathematics

## Prerequisites

- Basic arithmetic and algebra
- Understanding of functions and relations
- Familiarity with basic proof techniques

## Core Concepts

### Sets

A set is an unordered collection of distinct elements.

$$
|A| \text{ — cardinality (size) of set A}
$$

$$
\mathcal{P}(A) \text{ — power set, size } 2^{|A|}
$$

### Logic Laws

$$
\lnot(\forall x P(x)) \equiv \exists x \lnot P(x)
$$
$$
\lnot(\exists x P(x)) \equiv \forall x \lnot P(x)
$$

### Divisibility

$$
a \mid b \iff \exists k \in \mathbb{Z} : b = a \cdot k
$$

### Euclidean Algorithm (GCD)

$$
\gcd(a, b) = \gcd(b, a \bmod b)
$$

### Pigeonhole Principle

If $n$ items are placed into $m$ containers and $n > m$, then at least one container contains more than one item.

### Inclusion-Exclusion

$$
|A \cup B| = |A| + |B| - |A \cap B|
$$
