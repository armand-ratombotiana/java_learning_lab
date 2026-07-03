# Common Mistakes

- Confusing Big O with Big Θ — O is upper bound, Θ is tight
- Dropping constants when constants dominate (e.g., n=10)
- Analyzing wrong operation — focus on dominant operation
- Hidden costs — String concatenation, autoboxing, array copying
- Best-case != average-case — always mention which
- Ignoring space complexity — memory is often the bottleneck
- Assuming all O(n log n) sorts are equal — constant factors matter
