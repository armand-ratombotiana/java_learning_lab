# Data Stream Algorithms — Common Mistakes

1. Reservoir sampling: using random.nextInt(i) instead of random.nextInt(i+1) for the replacement position. The range should be [0, i] inclusive, giving probability 1/(i+1).

2. Count-Min Sketch: assuming the estimate is exact. The sketch always overestimates due to hash collisions. The minimum across rows mitigates but does not eliminate this.

3. AMS estimator variance: using too few estimators (m too small) gives high variance. The variance of each estimator is O(F2^2), so m = O(1/epsilon^2) is typically needed.

4. Misra-Gries: assuming the output counts are true frequencies. The counts are underestimates by up to n/k. Only elements with frequency > n/k are guaranteed to appear in the output.

5. Space-Saving min-heap out of sync: when incrementing a tracked element's count, the min-heap must be updated (decrease-key or re-insert). Forgetting this causes incorrect eviction decisions.

6. Sliding window expiry: not properly handling bucket expiration when elements leave the window. If buckets are not removed promptly, the distinct count includes stale elements.

7. Streaming variance using the naive formula: sumSq/count - mean^2 is numerically unstable because sumSq and mean are both large, and subtracting nearly-equal numbers loses precision. Use Welford's algorithm instead.

8. Not handling count = 1 in streaming variance: sample variance (dividing by count-1) is undefined for a single element. Return 0 or double.NaN in this case.