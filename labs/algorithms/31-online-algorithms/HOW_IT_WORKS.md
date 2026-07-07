# How Online Algorithms Work

## LRU Paging

Implementation: maintain a doubly-linked list of pages. When a page is accessed, move it to the head (most recently used). When a page fault occurs, evict the tail (least recently used) and add the new page to the head. LRU is k-competitive (optimal deterministic), matching the lower bound.

## FIFO Paging

Implementation: maintain a queue. When a page is accessed, do nothing if it is already in cache. When a page fault occurs, enqueue the new page and dequeue the oldest. FIFO is also k-competitive but suffers from Belady's anomaly: increasing cache size can increase page faults.

## Marker Algorithm (Randomized)

Divide the cache into marked and unmarked pages. Start an epoch with all pages unmarked. On each request: if the page is in cache, mark it. If not (page fault) and there is an unmarked page, evict a random unmarked page, bring in the new page, mark it. If all pages are marked, mark all as unmarked (start new epoch). Expected competitive ratio O(log k).

## Ski Rental

Deterministic strategy: rent for B-1 days, then buy on day B. If total skiing days < B, you paid rent only (optimal). If >= B, you paid 2B-1 vs optimal B, ratio 2 - 1/B.

Randomized strategy: choose a random day d from 1 to B-1 according to a specific distribution. Rent until day d, then buy. The expected competitive ratio approaches e/(e-1) ≈ 1.58 as B grows large.

## Secretary Problem

Algorithm: reject the first n/e candidates (observe only). Then select the first candidate who is better than all observed so far. The probability of selecting the best candidate converges to 1/e. Proof uses integration: P(success) = integral_{x=0}^{1} x * (-ln x) dx = 1/e.

## Epsilon-Greedy Bandit

Maintain for each arm i: count[i] (pulls) and totalReward[i]. On each round:
- With probability epsilon: select a random arm (explore).
- With probability 1-epsilon: select arm with highest average reward argmax(totalReward[i]/count[i]) (exploit).
For non-stationary distributions, use a sliding window or exponentially weighted averages. The regret is O(k * T * epsilon + k * log T / epsilon), minimized by epsilon ~ sqrt(k log T / T).