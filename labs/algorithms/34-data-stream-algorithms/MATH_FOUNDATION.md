# Data Stream Algorithms — Mathematical Foundation

## Frequency Moments

The k-th frequency moment F_k = sum_{i=1}^m f_i^k where f_i is the frequency of element i and m is the number of distinct elements. F_0 = m (distinct count), F_1 = n (stream length), F_2 = sum of squared frequencies (repeat rate, Gini index). F_infinity = max frequency (most frequent element). Streaming algorithms estimate these moments in O(log n) space.

## AMS Estimator for F2

Let m_j be the number of times the j-th distinct element appears in the stream. Choose a random position p uniformly. Let X be the value at position p. Let r be the number of times X appears from position p to end. The estimator Y = n * (2r - 1) has E[Y] = sum m_j^2 = F2 and Var[Y] <= 2 * F2^2. By averaging m independent copies, variance reduces to O(F2^2 / m).

## Count-Min Sketch Error

For a sketch with depth d and width w: the estimate f'_i >= f_i always. The expected error E[f'_i - f_i] <= n/w. By Markov's inequality, P(f'_i - f_i > epsilon n) <= 1/(epsilon w). Setting w = ceil(e/epsilon) and taking the minimum of d rows: P(error > epsilon n) <= e^{-d}. For delta = e^{-d}, d = ceil(ln 1/delta).

## Reservoir Sampling Probability

Each element i has probability k/n of being in the reservoir. Proof: at the time element i arrives, it is added with probability k/i. For it to remain in the reservoir, no later element j > i should replace it. The j-th element replaces i with probability k/j * 1/k = 1/j. P(i stays) = k/i * product_{j=i+1}^n (1 - 1/j) = k/i * product_{j=i+1}^n (j-1)/j = k/n.

## Misra-Gries Guarantee

After processing n elements with k-1 counters, the estimated count f'_i for any element i satisfies f_i - n/k <= f'_i <= f_i. An element with frequency > n/k is guaranteed to be in the counters (it is a heavy hitter). The algorithm uses O(k) space regardless of n or the universe size.