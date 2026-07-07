# Exact Exponential Algorithms — Mental Models

## Split the Battle in Half

Meet-in-the-middle is like a military strategy: instead of attacking with all forces from one direction, split your army in half and attack from two sides. The enemy (problem) must divide its attention. The combined pressure from both sides (enumerating both halves and combining) achieves the goal with less total effort.

## Overcounting and Correcting

Inclusion-exclusion is like taking a photo of a crowd. If you count everyone, you might count people in overlapping groups twice. First count all, then subtract those counted twice, then add back those subtracted three times, and so on. The alternating sum gives the correct count of distinct individuals.

## Zeta Transform as Prefix Sum Over Subsets

The zeta transform on subsets is like a prefix sum but over subset inclusion instead of linear order. For each subset S, it sums f(T) over all T subset of S. This is like computing prefix sums in a high-dimensional Boolean cube. The Moebius transform (inverse) reverses this, extracting f(S) from the prefix sums.

## Branching as a Decision Tree

Branching algorithms are like a decision tree where each node is a partial solution and each branch is a choice. The branching number determines the growth rate: if each decision splits into 2 subproblems with size reduced by 1, the tree has 2^n leaves. But if we can reduce the size by 2 in one branch, the tree becomes smaller (Fibonacci-like, about 1.618^n).

## Subset Convolution as Polynomial Multiplication Over Subsets

Subset convolution is like polynomial multiplication where each monomial corresponds to a subset rather than a monomial power. The convolution (f * g)(S) sums f(T) * g(S \ T) over disjoint subsets. Fast subset convolution uses the ranked zeta transform, which is like sorting elements by subset size before doing prefix sums, ensuring only disjoint subsets are combined.