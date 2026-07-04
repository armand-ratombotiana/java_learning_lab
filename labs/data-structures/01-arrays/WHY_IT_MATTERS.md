# Why Arrays Matter

Arrays matter because they are the **foundation of nearly every data structure** and the **fastest possible way to access sequential data**.

## Practical Impact

- **Performance-critical systems**: games, real-time analytics, HFT — all rely on array cache locality
- **Database indexing**: B-tree leaf pages use contiguous arrays for scan speed
- **Image processing**: pixel buffers are 2D/3D arrays; convolution kernels depend on sequential access
- **Machine learning**: tensors (NumPy, etc.) are multi-dimensional arrays under the hood
- **Language runtimes**: JVM string pools, class metadata, method tables all use arrays

## Why Learn Arrays

1. **Everything builds on them**: dynamic arrays, ArrayLists, hash table buckets, heap arrays, string buffers
2. **Interview essential**: two-pointer, sliding window, prefix sum — all array patterns
3. **Understanding memory layout** gives insight into performance differences between structures
4. **Amortized analysis** of dynamic arrays is a model for understanding all resizable structures

Arrays are the **lingua franca** of data structures — understanding them deeply makes every other structure easier.
