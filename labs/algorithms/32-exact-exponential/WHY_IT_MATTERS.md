# Why Exact Exponential Algorithms Matter

Exact exponential algorithms matter because they solve real problems. In computational biology, exact algorithms for maximum parsimony (tree inference) run in O(2^n) on 20-30 species, which is sufficient for many biological studies. In cryptography, subset sum solvers break knapsack cryptosystems. In operations research, TSP solvers (Concorde) use branch-and-cut with exponential worst-case but solve instances with millions of cities.

Meet-in-the-middle matters because it is the most practical technique for many NP-hard problems. The 2^{n/2} bound means that problems with n up to 60 are feasible, covering many real-world instances. Applications include knapsack (resource allocation), subset sum (financial portfolio), and collision finding in cryptography.

Fast subset convolution matters because many DP-on-subsets problems arise in machine learning (feature selection), operations research (facility location), and network design. The improvement from O(3^n) to O(n^2 * 2^n) is the difference between n=15 and n=20-25 being feasible.

Inclusion-exclusion matters for counting problems in statistics (contingency tables), combinatorics (graph coloring), and probability (union bound improvements). It is also the basis of the principle of inclusion-exclusion in sieve methods for prime counting.

Exact exponential algorithms matter because they deepen our understanding of algorithmic complexity. Studying the exponential-time complexity hierarchy (ETH, SETH) reveals connections between problems and guides algorithm design even for polynomial-time solvable problems.