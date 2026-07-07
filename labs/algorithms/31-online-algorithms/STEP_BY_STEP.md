# Online Algorithms — Step by Step Guide

## Step 1: Implement LRU Paging

Create class LRUCache with capacity k. Use LinkedHashMap with accessOrder=true. Override removeEldestEntry. Track page faults. Test with known access sequence and compare with optimal.

## Step 2: Implement FIFO Paging

Create class FIFOCache with capacity k. Use Queue<Integer> and HashSet<Integer>. On access, if not in set, enqueue, add to set, increment faults. If queue is full, poll and remove from set first.

## Step 3: Implement Marker Paging

Create class MarkerCache with capacity k. Maintain boolean[] marked and Map<Integer, Boolean>. On access: if present, mark. If not (fault): if all marked, clear marks. Evict random unmarked page. Add new page, mark it.

## Step 4: Implement Ski Rental

Class SkiRental with method rentOrBuy(int B, int totalDays). Deterministic: rent for B-1 days, buy on day B. Return total cost. Compare with optimal (min(rent*B, buy)) for various totalDays.

## Step 5: Implement Secretary Problem

Class SecretaryProblem with method simulate(int n). Generate n random values. Find max of first n/e. For remaining values, select first exceeding max. Return whether it was the global max. Run 10000 trials, compute success probability.

## Step 6: Implement Epsilon-Greedy

Class MultiArmedBandit with arms, epsilon. Maintain counts[] and sumRewards[]. Each round: explore with prob epsilon, else exploit. Return chosen arm. Run simulation with known reward distributions (e.g., Bernoulli arms with p=[0.9, 0.7, 0.5]).

## Step 7: Analyze and Compare

Compare actual competitive ratios with theoretical bounds. Graph regret over time for bandits.