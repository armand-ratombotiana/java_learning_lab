# Lab 13: Interview Questions

## FAANG-Level Questions

### Q1: Design a distributed training infrastructure for a 100B parameter model.
**Answer**: Use 3D parallelism: (1) Data parallelism — replicate model across nodes, shard data, (2) Tensor parallelism — split each layer across devices within a node (Megatron-LM style), (3) Pipeline parallelism — split layers across nodes (GPipe/PTD-P). Use hierarchical all-reduce: NVLink within node, InfiniBand/RoCE across nodes. ZeRO-3 for optimizer state sharding.

### Q2: Explain the ring all-reduce algorithm. Why is it better than parameter server?
**Answer**: Ring all-reduce has O(N) communication per worker (each sends/receives N-1 messages). Parameter server has O(2N) (all workers send to PS, PS sends back). For large N (many workers), ring all-reduce is more efficient and eliminates the parameter server bottleneck. The algorithm: (1) scatter-reduce phase — each node sends 1/N of data around ring, accumulating, (2) all-gather phase — each node sends reduced chunk around ring, collecting full result.

### Q3: How do you handle stragglers in synchronous distributed training?
**Answer**: (1) Use backup workers — wait for first K out of N workers (partial sync), (2) Hierarchical synchronization — sync within node first, then across nodes, (3) Adaptive timeout — dynamically adjust wait time based on historical worker speeds, (4) Speculative execution — compute extra gradients to mask straggler delays.

### Q4: What is the communication overhead in distributed training and how do you reduce it?
**Answer**: Communication overhead = time spent on gradient sync / total iteration time. Reduce by: (1) Gradient compression (quantization, sparsification, Top-K), (2) Overlap communication with computation (async all-reduce), (3) Larger batch sizes (fewer sync steps), (4) Gradient accumulation (compute multiple micro-batches before syncing).

## LeetCode / NeetCode References
- **Design Web Crawler (LeetCode 1242)** — Multi-threaded distributed computation
- **The Skyline Problem (LeetCode 218)** — Divide and conquer (parallel patterns)
- **Merge K Sorted Lists (LeetCode 23)** — Hierarchical merging (all-reduce analogy)
