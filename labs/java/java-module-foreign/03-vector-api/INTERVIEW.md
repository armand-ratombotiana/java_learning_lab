# Interview Questions: Vector API

## Basic
1. What is the Vector API and what problem does it solve?
2. What is a `VectorSpecies`?
3. How does the Vector API achieve SIMD without platform-specific code?

## Intermediate
4. What is loop tail handling and how does `loopBound()` work?
5. How do masked vector operations work?
6. What is the difference between `reduceLanes` and element-wise operations?

## Advanced
7. How does the Vector API interact with the JIT compiler's auto-vectorization?
8. When would you use the Vector API over a simple scalar loop?
9. What are the limitations of `SPECIES_PREFERRED` across different hardware?
10. How does the Vector API handle data alignment?

## Expert
11. Explain how `VectorShuffle` works for permuting lanes.
12. How does the Vector API handle double-precision vs single-precision performance differences?
13. What is the cost of extracting a single lane from a vector?
14. How can you detect available SIMD features at runtime for fallback logic?
