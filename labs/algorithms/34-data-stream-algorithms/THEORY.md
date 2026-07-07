# Data Stream Algorithms — Theoretical Foundation

## Streaming Model

In the data stream model, a sequence of elements passes by, and an algorithm can examine each element at most once (or a few times) using limited memory. The algorithm cannot store all elements. The space complexity is typically poly-logarithmic in the stream size. Results are often approximate with probabilistic guarantees.

## Reservoir Sampling

Reservoir sampling maintains a uniform random sample of size k from a stream without knowing the total length. For the first k elements, all are added to the reservoir. For the i-th element (i &gt; k), it is added with probability k/i, replacing a randomly selected element from the reservoir. This ensures each element has an equal probability k/n of being in the final sample.

## AMS Algorithm for F2 Estimation

The Alon-Matias-Szegedy algorithm estimates the second frequency moment F2 = sum_i f_i^2 (where f_i is the frequency of element i). It maintains a random variable X = n * (2 * r - 1), where r is the number of times a randomly chosen position's value appears from that position onward. Multiple such estimators are averaged to reduce variance. The algorithm uses O(log n) space.

## Frequent Items Algorithms

Misra-Gries (MG) uses k-1 counters. For each element, if it has a counter, increment it; else if a counter is available, assign it; else decrement all counters (the element is treated as evicting one count from each). Space-Saving tracks elements with approximate counts and estimates frequencies with guarantees. Lossy Counting segments the stream and prunes elements whose count falls below a threshold.

## Count-Min Sketch

Count-Min Sketch is a probabilistic data structure using a 2D array of d x w counters with d hash functions. For each element, it computes d hash values (each in [0, w-1]) and increments the corresponding counters. The estimated frequency is the minimum of the d counter values. The estimate is an overestimate with high probability: error within epsilon * n with probability 1 - delta.

## Sliding Window Counting

In sliding window models, only the most recent W elements matter. Counting distinct elements in a sliding window is challenging because elements leaving the window affect counts. Algorithms use timestamped counters and hash-based bucketing. The basic approach divides the window into buckets and maintains for each bucket a set of distinct element hashes, merging and expiring buckets as the window slides.

## Streaming Statistics

Mean and variance can be computed incrementally: maintain count, sum, and sum of squares. For each new value x: count++, sum += x, sumSq += x*x. The mean is sum/count, and variance is (sumSq/count) - mean^2. This is Welford's algorithm, which is numerically stable compared to the textbook formula.