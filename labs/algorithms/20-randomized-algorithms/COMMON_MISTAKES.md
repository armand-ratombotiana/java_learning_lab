# Common Mistakes — Randomized Algorithms

- **Wrong random range in shuffle** — Using nextInt(n) instead of nextInt(i+1) biases results
- **Creating new Random per call** — ThreadLocalRandom.current() is the correct approach
- **Fisher-Yates going forward** — Must iterate backward to avoid bias
- **Reservoir: wrong replacement probability** — The j < k comparison is critical
- **Quickselect: not randomizing** — Deterministic pivot gives O(n^2) worst case
- **Freivalds: using same vector** — Each iteration needs a fresh random vector
- **Not boosting confidence** — Single Freivalds run gives only 50% detection rate
- **Karger: forgetting to copy graph** — Each repetition needs a fresh graph copy
- **Predictable randomness for security** — Using Random instead of SecureRandom for crypto
- **Seed reuse** — Same seed produces same sequence; use random seeds
