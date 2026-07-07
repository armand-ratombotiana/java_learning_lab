# Online Algorithms — Common Mistakes

1. LRU implementation using Queue instead of LinkedHashMap: a plain Queue does not support the move-to-front operation. LinkedHashMap with accessOrder=true is the correct Java implementation.

2. FIFO not tracking existing pages: when a page is accessed and is already in cache, FIFO should NOT update its position in the queue. Only page faults cause queue updates.

3. Marker algorithm: forgetting to reset marks when all pages are marked. The algorithm starts a new epoch by clearing all marks, then evicts an unmarked page.

4. Ski rental randomized strategy: choosing the threshold uniformly at random from [0, B-1] gives a different competitive ratio (2) than the optimal distribution. The optimal randomized strategy uses a carefully chosen distribution.

5. Secretary problem: rejecting exactly the first n/e candidates and then selecting the first better candidate. The threshold is n/e, not n/2 or any other fraction. Using n/2 reduces success probability.

6. Epsilon-greedy: using a fixed epsilon that is too large leads to too much exploration and high regret. Using epsilon that is too small leads to too little exploration and may miss the optimal arm.

7. Competitive analysis: comparing the algorithm against an offline optimal that knows the entire input in advance. Using an online optimal (which doesn't exist) is meaningless.

8. Bandit with non-stationary distributions: epsilon-greedy with constant epsilon may fail when reward distributions change over time. Use epsilon-decay or sliding window approaches.