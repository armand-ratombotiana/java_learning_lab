# Online Algorithms — Mathematical Foundation

## Competitive Ratio Definition

An online algorithm ALG is c-competitive if there exists a constant alpha such that for all input sequences I: cost(ALG, I) <= c * cost(OPT, I) + alpha, where OPT is the optimal offline algorithm. For minimization problems, c >= 1 (c closer to 1 is better). For randomized algorithms, the expected cost is compared.

## Lower Bounds

For deterministic paging with cache size k, no algorithm can be better than k-competitive. Proof: adversary can request k+1 pages cyclically, causing a fault on every request while OPT faults every k steps. For randomized paging against an oblivious adversary, the lower bound is H_k ≈ ln k (the k-th harmonic number).

## Ski Rental Analysis

Deterministic competitive ratio: if ALG buys on day d, and OPT stops after d-1 days, ALG's cost = d-1 (rent) vs OPT's cost = d-1, ratio = 1. If OPT stops on day d or later, ALG's cost = (d-1) + B vs OPT's cost = min(B, total). Worst-case ratio = min_d max(1, (d-1+B)/B) = 2 - 1/B at d = B.

## Secretary Problem Optimality

Optimal strategy: skip the first n/e candidates, then pick the next candidate better than all seen. The success probability approaches 1/e for large n. This is optimal: no strategy can achieve success probability greater than 1/e.

## Regret in Bandits

Regret = sum_{t=1}^T (mu* - mu_{a_t}) where mu* is the best arm's expected reward and mu_{a_t} is the chosen arm's expected reward. Lai and Robbins proved that any consistent algorithm has regret at least Omega(log T). Epsilon-greedy with optimal epsilon achieves O(T^{2/3}) regret. UCB achieves O(log T) regret, matching the lower bound.