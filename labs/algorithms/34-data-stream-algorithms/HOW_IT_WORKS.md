# How Data Stream Algorithms Work

## Reservoir Sampling

Implementation: maintain an array reservoir of size k.
- For i = 0 to k-1: reservoir[i] = stream[i].
- For i = k to n-1:
  - j = random(0, i) (inclusive).
  - If j < k: reservoir[j] = stream[i].
Each element has probability k/n of being in the final sample. Proof by induction: after processing i elements, each has probability k/i of being in the reservoir.

## AMS F2 Estimation

- Choose a random position p in the stream (0-indexed).
- Let X = value at position p.
- Let c = number of times X appears from position p onward (including position p).
- The estimator Y = n * (2*c - 1) has expected value equal to the second frequency moment F2.
- Maintain m independent such estimators, compute their average to reduce variance.
- The median-of-means technique further improves robustness.

## Count-Min Sketch

Initialize a 2D array count[d][w] = 0. Choose d hash functions h_j: universe -> [0, w-1].
- Update(x, delta): for j = 0..d-1: count[j][h_j(x)] += delta.
- Estimate(x): return min_j count[j][h_j(x)].
The estimate is always >= true count. With probability 1 - delta, the error is <= epsilon * ||f||_1 where w = ceil(e/epsilon) and d = ceil(ln(1/delta)).

## Misra-Gries Frequent Items

Maintain k-1 (key, count) pairs.
- On seeing element x: if x has a counter, increment it. Else if there are fewer than k-1 counters, add (x, 1). Else decrement all counters (evict any that reach 0).
- At the end, any element with frequency > n/k must be in the counters. The counts are underestimates; the true frequency is count + (total decrements).

## Sliding Window Distinct Count

Divide the window of size W into buckets of exponentially increasing size. For each bucket, maintain a set of distinct element hashes. When inserting a new element, it goes into the first (smallest) bucket. When a bucket's size limit is reached, merge it into the next bucket (union of distinct elements). Expire buckets entirely when they leave the window.