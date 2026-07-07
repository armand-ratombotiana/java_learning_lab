# Flashcards — Exact Exponential Algorithms

Q: Meet-in-the-middle time complexity?
A: O(2^{n/2} * n)

Q: Inclusion-exclusion formula?
A: |union A_i| = sum (-1)^{|S|+1} |intersection_{i in S} A_i|

Q: Zeta transform?
A: F[S] = sum_{T subseteq S} f[T]; computed in O(n * 2^n)

Q: Moebius transform?
A: Inverse zeta: f[S] = sum_{T subseteq S} (-1)^{|S|-|T|} F[T]

Q: Naive subset convolution?
A: O(3^n)

Q: Fast subset convolution?
A: O(n^2 * 2^n)

Q: Branching number?
A: Root of recurrence T(n) = sum T(n - a_i)

Q: ETH?
A: 3-SAT requires 2^{Omega(n)} time