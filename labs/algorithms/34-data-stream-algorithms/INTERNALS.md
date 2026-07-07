# Data Stream Algorithms — Internal Implementation Details

The AMSMomentEstimation class maintains m independent estimators. For each estimator: a random position and value are chosen during initialization (reservoir sampling on the stream). After processing the full stream, each estimator computes Y = n * (2*c - 1) where c is the frequency of the chosen value from its chosen position onward. The final estimate is the average of the estimators. The median-of-means technique: group estimators into batches, take median of batch averages, reducing both bias and variance.

The FrequentItems class implements Misra-Gries with k-1 counters. Internal: HashMap<E, Integer> counters. On each element: if already in counters, increment. Else if counters.size() < k-1, add with count 1. Else: decrement all counts by 1; remove any that reach 0.

The Space-Saving variant maintains counters with a min-heap for efficient tracking of the smallest count. On each element: if already tracked, increment its count and update heap. Else, replace the element with smallest count, increment the count by 1.

The SlidingWindowCount class uses a bucketed approach. Internal: LinkedList of buckets. Each bucket stores a Set<E> of distinct elements and has a timestamp. New elements go into the current (most recent) bucket. When a bucket's size exceeds a threshold, it is merged into the next bucket. After each insertion, expired buckets (timestamp < currentTime - windowSize) are removed. Distinct count is the sum of distinct elements across all active buckets, tracked incrementally.

The StreamStatistics class uses Welford's algorithm: maintains count, mean, M2 (sum of squared differences from mean). On new value x: count++; delta = x - mean; mean += delta / count; M2 += delta * (x - mean). Variance = M2 / (count - 1) for sample variance, or M2 / count for population variance. This avoids catastrophic cancellation that plagues the naive two-pass formula.