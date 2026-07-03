# Architecture — Parallel Algorithms

## Java Parallelism Options
| Approach | When to Use |
|----------|-------------|
| Parallel streams | Simple data-parallel operations |
| Fork/Join | Divide-and-conquer parallelism |
| CompletableFuture | Async composition with parallelism |
| ExecutorService | Custom thread pool management |
| Phaser/CyclicBarrier | Multi-phase parallel computation |

## Real-World Systems
- Big Data: Apache Spark (distributed DAG parallelism)
- Scientific Computing: MPI for HPC
- Image Processing: Parallel pixel operations
- Machine Learning: Parallel gradient computation
- Databases: Parallel query execution
