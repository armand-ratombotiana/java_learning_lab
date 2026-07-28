# Lab 13: Distributed Training — Guide

## Step 1: Understand Parallelism Strategies

```
Data Parallelism:              Model Parallelism:
┌──────┐ ┌──────┐ ┌──────┐    ┌──────┐──────┐──────┐
│Data 1│ │Data 2│ │Data 3│    │Layer1│Layer2│Layer3│
│Model │ │Model │ │Model │    │ GPU1 │ GPU2 │ GPU3 │
│Grad 1│ │Grad 2│ │Grad 3│    └──────┘──────┘──────┘
└──┬───┘ └──┬───┘ └──┬───┘         │       │       │
   └────────┼────────┘              └───►───┘───►───┘
            ▼
      All-Reduce (avg)
            ▼
       Updated Model
```

## Step 2: Implement DistributedWorker

Each worker has a copy of the model (data parallelism) and computes gradients on its shard of data.

## Step 3: Implement RingAllReduce

The ring all-reduce algorithm:
1. **Scatter-reduce**: Each node sends a chunk to neighbor, accumulates
2. **All-gather**: Each node sends the reduced chunk around ring

## Step 4: Compile and Run

```bash
cd lab13/src
javac com/mlops/lab13/*.java
java com.mlops.lab13.DistributedTrainingLab
```

## Scaling Concepts

| Concept | Description |
|---------|-------------|
| Synchronous | All workers compute, then all-reduce; deterministic |
| Asynchronous | Workers update model independently; faster but stale gradients |
| All-Reduce | Aggregate gradients across workers (sum/avg) |
| Ring All-Reduce | O(N) communication instead of O(2N) for parameter server |
| Scaling Efficiency | speedup / N (ideal = 1.0, real < 1.0 due to communication) |

## Best Practices
- Match parallelism strategy to model size and data volume
- Use gradient accumulation for large batch sizes
- Profile communication vs computation to identify bottlenecks
- Use mixed precision training to reduce communication volume
- Implement fault tolerance with checkpointing and worker recovery
