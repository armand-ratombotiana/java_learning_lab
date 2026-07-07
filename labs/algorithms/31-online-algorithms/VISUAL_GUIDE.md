# Online Algorithms — Visual Guide

## Paging Timeline

Time: 1 2 3 4 5 6 7 8
Page: a b c d a b c d
Cache size k=3. LRU: miss on a,b,c,d (evict a), a (evict b), b (evict c), c (evict d), d. Total faults = 8. Optimal (knows future): miss on a,b,c,d, hit a,b,c,d. Total = 4.

## Cache State Transitions

LRU maintains list order. After accessing a: [a]. After b: [b, a]. After c: [c, b, a]. After d (miss, evict a): [d, c, b]. After a (miss, evict b): [a, d, c]. Each access moves the page to front.

## Ski Rental Timeline

Days: 1 2 3 4 5 ... B=5. Rent cost per day = 1. Buy cost = 5. Deterministic strategy: rent for 4 days (cost 4), buy on day 5 (cost 5). If skiing ends day 4: cost=4 vs OPT=4 (all rent). If skiing ends day 5: cost=9 vs OPT=5 (buy on day 1). Ratio Worst: max(1, 9/5) = 1.8.

## Secretary Problem Visual

n=10 candidates. Reject first 10/e ≈ 3. Candidate scores: [40, 30, 50, 80, 20, 90, 70, 60, 10, 85]. Max of first 3 = 50. Starting from candidate 4: pick first exceeding 50 -> candidate 4 (80) selected, success (best is 90 at position 6). The 1/e rule maximizes probability of choosing the best.

## Epsilon-Greedy Visual

3 arms with reward probabilities [0.8, 0.5, 0.2]. Epsilon=0.1. Over 1000 rounds: explore 100 times (random), exploit 900 times (best-known). Early on, arms are tried equally. After ~30 rounds, arm 0 emerges as best. From then, arm 0 is played ~95% of the time (90% exploit + 10/3% explore).