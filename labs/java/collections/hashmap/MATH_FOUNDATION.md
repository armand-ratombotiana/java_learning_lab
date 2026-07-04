# Mathematics of Hashing & Probability

To truly understand `HashMap` performance, we must look at the mathematics governing collisions and bucket distribution.

## 🎲 The Birthday Paradox & Hash Collisions
The probability of a hash collision is closely related to the Birthday Problem in probability theory. 

Given a hash table of size $N$ (number of buckets) and $k$ inserted elements, what is the probability $P$ of at least one collision?

The probability of *no* collisions when inserting $k$ elements into $N$ buckets is:
$$ P(\text{no collision}) = \frac{N}{N} \times \frac{N-1}{N} \times \frac{N-2}{N} \times \dots \times \frac{N-k+1}{N} $$

Therefore, the probability of at least one collision is:
$$ P(\text{collision}) = 1 - \prod_{i=0}^{k-1} \left(1 - \frac{i}{N}\right) $$

Because of this exponential curve, collisions are **inevitable and frequent**, even when the table is mostly empty. A robust collision resolution strategy (like Java's chaining and treeification) is mathematically required.

## 📈 Load Factor & Expected Chain Length
The **Load Factor** ($\alpha$) is defined as:
$$ \alpha = \frac{n}{m} $$
Where:
- $n$ = number of entries in the map
- $m$ = number of buckets (table capacity)

If the hash function distributes keys uniformly (Simple Uniform Hashing Assumption), the expected length of a chain in any given bucket is exactly $\alpha$.

- **Successful Search**: Expected time is $O(1 + \frac{\alpha}{2})$
- **Unsuccessful Search**: Expected time is $O(1 + \alpha)$

This mathematical relationship is why Java chooses a default load factor of **0.75**. It provides a calculated trade-off between time cost (chain length) and space cost (memory overhead of empty buckets).

## 🌳 The Poisson Distribution in Treeification
Why does Java 8 treeify bins when the count reaches 8?

Under ideal random hash codes, the frequency of nodes in bins follows a **Poisson distribution**:
$$ P(X = k) = \frac{e^{-\lambda} \lambda^k}{k!} $$
Where $\lambda$ (the expected value) is the load factor (default 0.5 when resizing occurs, up to 0.75).

For $\lambda = 0.5$, the probabilities of a bin having $k$ elements are:
- k = 0: ~0.6065
- k = 1: ~0.3032
- k = 2: ~0.0758
- ...
- k = 8: ~0.00000006

The probability of a bin reaching 8 elements under normal circumstances is infinitesimally small (less than 1 in 10 million). Therefore, if a bin reaches 8 elements, it is statistically highly probable that the hash function is poorly distributed or under a deliberate Denial of Service (DoS) attack via hash collisions. Treeification (converting to a Red-Black tree) acts as a mathematical fail-safe against this worst-case scenario.