# Online Algorithms — Theoretical Foundation

## Online Computational Model

In the online model, an algorithm receives input piece by piece and must make irrevocable decisions without knowledge of future inputs. This contrasts with the offline model, where the entire input is available in advance. Online algorithms are evaluated using competitive analysis: compare the algorithm's performance to that of an optimal offline algorithm.

## Competitive Analysis

An online algorithm A is c-competitive if for all input sequences I, cost(A, I) &lt;= c * cost(OPT, I) + alpha, where OPT is the optimal offline algorithm and alpha is a constant. The competitive ratio c is the worst-case ratio. A deterministic algorithm has competitive ratio at least equal to the value of the underlying game.

## Paging Problem

In paging, a cache of size k holds pages. When a requested page is not in cache (a page fault), it must be brought in, evicting some existing page if the cache is full. The goal is to minimize page faults. LRU (Least Recently Used) evicts the page that was used furthest in the past. FIFO evicts the oldest page. The Marker algorithm marks pages during an epoch and evicts unmarked pages.

## Ski Rental Problem

A skier must decide whether to rent skis (cost 1 per day) or buy them (cost B). The number of skiing days is unknown. The optimal deterministic strategy is to rent for B-1 days and buy on day B, achieving competitive ratio 2 - 1/B. The optimal randomized strategy achieves ratio e/(e-1) ≈ 1.58.

## Secretary Problem

The secretary problem (also known as the optimal stopping problem) asks: given n candidates interviewed sequentially in random order, when should we stop and select the current candidate to maximize the probability of choosing the best overall? The optimal strategy is to reject the first n/e candidates and then select the next candidate better than all seen so far, achieving success probability 1/e ≈ 37%.

## Multi-Armed Bandit

In the multi-armed bandit problem, a player chooses among k slot machines (arms), each with unknown reward distribution. The player must balance exploration (trying arms to learn their distributions) and exploitation (playing the best-known arm). Epsilon-greedy selects the best-known arm with probability 1-epsilon and a random arm with probability epsilon. Regret is measured against the best arm in hindsight.