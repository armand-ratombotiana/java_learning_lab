# Debugging — Randomized Algorithms

## Shuffle Verification

`java
// Statistical test: count occurrences of each element at each position
int[][] count = new int[n][n];
for (int trial = 0; trial < 100000; trial++) {
    int[] arr = {1, 2, 3, 4};
    shuffle(arr);
    for (int i = 0; i < arr.length; i++)
        count[arr[i]-1][i]++;
}
// Each cell should be ~25000 for uniform shuffle
`

## Reservoir Correctness

`java
// Verify each element appears with approx k/n probability
int[] reservoir = new int[k];
int[] freq = new int[n];
for (int trial = 0; trial < 100000; trial++) {
    sample(stream, reservoir);
    for (int val : reservoir) freq[val]++;
}
// Each freq entry should be ~100000 * k / n
`

## Deterministic Seed for Debugging

Use a fixed seed during development: Random rng = new Random(42). This reproduces the same pseudo-random sequence for reproducible debugging.
