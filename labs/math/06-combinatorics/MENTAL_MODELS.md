# Mental Models for Combinatorics

## The Multiplication Principle

If task A has $m$ ways and task B has $n$ ways, then doing both has $m \times n$ ways.

```
Choose shirt (3) × Choose pants (4) = 12 outfits
```

## The Stars and Bars

Distribute $n$ identical items into $k$ bins: $\binom{n+k-1}{k-1}$

```
★★ | ★ | ★★★  — 6 stars into 3 bins
```

## The Pigeonhole Principle

If $n$ items go into $m$ boxes and $n > m$, some box has $\ge 2$ items. For example: among 367 people, at least 2 share a birthday.

## Pascal's Triangle

```
        1
      1   1
    1   2   1
  1   3   3   1
1   4   6   4   1
```

Each number is sum of two above. Row $n$ gives $\binom{n}{k}$ for $k = 0 \dots n$.

## The Inclusion-Exclusion Lens

Count everything, subtract overcounts, add back over-subtractions:

$$
|A \cup B| = |A| + |B| - |A \cap B|
$$
