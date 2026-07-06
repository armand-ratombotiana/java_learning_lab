# Randomized Algorithms — Internal Mechanics

## Randomized Quickselect

`java
int quickselect(int[] arr, int left, int right, int k) {
    if (left == right) return arr[left];
    int pivotIdx = left + ThreadLocalRandom.current().nextInt(right - left + 1);
    pivotIdx = partition(arr, left, right, pivotIdx);
    if (k == pivotIdx) return arr[k];
    else if (k < pivotIdx) return quickselect(arr, left, pivotIdx - 1, k);
    else return quickselect(arr, pivotIdx + 1, right, k);
}
`

## Fisher-Yates Shuffle

`java
void shuffle(int[] arr) {
    Random rng = ThreadLocalRandom.current();
    for (int i = arr.length - 1; i > 0; i--) {
        int j = rng.nextInt(i + 1);
        int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
    }
}
`

## Reservoir Sampling

`java
int[] reservoirSample(int[] stream, int k) {
    int[] reservoir = new int[k];
    for (int i = 0; i < k; i++) reservoir[i] = stream[i];
    Random rng = ThreadLocalRandom.current();
    for (int i = k; i < stream.length; i++) {
        int j = rng.nextInt(i + 1);
        if (j < k) reservoir[j] = stream[i];
    }
    return reservoir;
}
`

## Freivalds' Checker

`java
boolean freivaldsCheck(int[][] A, int[][] B, int[][] C) {
    int n = A.length;
    int[] x = new int[n];
    Random rng = ThreadLocalRandom.current();
    for (int i = 0; i < n; i++) x[i] = rng.nextBoolean() ? 1 : 0;
    int[] Bx = multiply(B, x);
    int[] ABx = multiply(A, Bx);
    int[] Cx = multiply(C, x);
    return Arrays.equals(ABx, Cx);
}
`
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Randomized Algorithms

## Expected Time of Quickselect

The expected number of comparisons in randomized quickselect is E[T(n)] = n + E[T(max(k, n-k))] with probability 1/n for each pivot position. Solving: E[T(n)] <= 2n + o(n).

## Reservoir Sampling Probability

The i-th item appears in the final reservoir with probability k/n. Proof: For i <= k: P(i in final) = product_{j=k+1}^n (1-1/j) * (k/k) = k/n. For i > k: P(i added) * P(not removed) = (k/i) * product_{j=i+1}^n (1-1/j) = k/n.

## Freivalds' Error Bound

If A*B != C, then P(r * (A*B - C) != 0) >= 1/2, where r is the random vector. The test detects inequality with probability at least 1/2 per run. After k runs, the probability of error is at most 2^{-k}.

## Karger's Success Probability

Each random edge contraction in Karger's algorithm preserves the global min cut with probability at least 2/n^2 per independent run. After O(n^2 log n) runs, the probability of finding the global min cut is at least 1 - 1/n.

## Union Bound

The union bound (Boole's inequality) is used extensively in randomized algorithm analysis: P(union of events) <= sum of individual probabilities. This allows bounding the overall error probability of multiple randomized tests.
