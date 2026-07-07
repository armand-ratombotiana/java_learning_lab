# Data Stream Algorithms — Code Deep Dive

The AMSMomentEstimation class initializes estimators during construction. Each estimator has a randomly chosen position pos[j] and stores the value at that position. After processing the stream, computeCounts processes the remaining stream to count occurrences of each chosen value. The estimate F2 = 0; for each estimator j: c = count[pos[j]]; Y = n * (2*c - 1); sum += Y; return (long)(sum / m). Median-of-means: group into batches, take median of batch averages.

The FrequentItems (Misra-Gries) class maintains a HashMap<E, Integer> of size k-1. The update method: if map contains key, map.put(key, count+1). Else if map.size() < k-1, map.put(key, 1). Else: for each entry: map.put(entry.key, entry.value - 1); remove if value == 0. The getEstimatedFrequency method returns map.getOrDefault(key, 0).

Space-Saving extends this with a min-heap (PriorityQueue) of counts. On update: if key already tracked, increment its count and update heap. Else: E victim = heap.poll(); map.remove(victim); map.put(key, victimCount + 1); heap.offer(entry).

The SlidingWindowCount class maintains buckets of timestamped HashSet<E>. On insert: add element to current bucket. If bucket size > threshold, merge into next bucket. Cleanup: remove buckets with timestamp < currentWindowStart. Estimated distinct count: sum of sizes of all active buckets. Optionally use HyperLogLog per bucket for memory efficiency.

The StreamStatistics class: count = 0; mean = 0.0; m2 = 0.0. On update(x): count++; delta = x - mean; mean += delta / count; m2 += delta * (x - mean). Variance = m2 / (count - 1). This is Welford's one-pass algorithm, numerically stable.