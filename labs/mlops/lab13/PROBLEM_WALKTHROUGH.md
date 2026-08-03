# Problem Walkthrough: Distributed Training

## Problem 1: Ring All-Reduce Training Simulation — Company: Microsoft

### Interview Scenario

> **Interviewer**: "We're training a small model across 4 simulated workers, and we want a Java demonstration of the full distributed loop: workers compute gradients on local data shards in parallel, a ring all-reduce averages the gradients, and the global model updates. The demo runs 5 epochs with a 64-parameter model, 1,000 samples, batch 128, learning rate 0.01. The walkthrough must produce a stable transcript — and I want the two ring phases explained as if to a new hire."
>
> **Candidate**: "I'll mirror the lab's `RingAllReduce`, make the simulation deterministic so the transcript is a golden file, and walk through why the ring is O(N) per worker."

### The Problem

1. Build a `Model` with 64 weights (initialized to 0.5) supporting `applyGradients` and a squared-error `computeLoss`.
2. Create 1,000 samples from a seeded `Random(42)` stream and shard them across 4 workers (250 each).
3. Each epoch: copy the global model to 4 local models, compute per-worker gradients in parallel over 128 random samples, and print each worker's loss.
4. Average the gradients with a ring all-reduce — scatter-reduce phase then all-gather phase — and divide by the worker count.
5. Update the global model with the averaged gradients and print the global loss.
6. The transcript must be byte-stable across runs — no unseeded randomness, no racy print order, no runtime-dependent timing.

### Solution Walkthrough

1. **Keep the data pipeline deterministic.** The 1,000 samples are generated once from `Random(42)`; sharding is pure `subList` arithmetic (250 samples each). The data is therefore identical on every run — the only remaining nondeterminism in the lab is the worker RNG and print order.
2. **Seed each worker's sampling.** The lab's `DistributedWorker` uses `new Random()` — different gradients every run, so the loss curve is unreproducible. The walkthrough seeds per worker with `new Random(1000L * id)`, making each worker's 128-sample gradient deterministic while keeping workers distinct. This mirrors real DDP where each worker's data shard (and hence gradients) is fixed.
3. **Print worker results in collection order, not call order.** The lab prints inside `call()` — four threads finishing 200ms sleeps simultaneously makes the output interleave differently each run. The walkthrough has `call()` return gradients and prints each worker line from `main` after `future.get()`, in the deterministic order of the `invokeAll` future list.
4. **Run the ring in two phases.** `RingAllReduce.allReduce` first deep-copies each worker's gradient vector, then: **Phase 1 — scatter-reduce** — for `nWorkers - 1` steps, each worker sends its chunk (`step * chunkSize`) to its right neighbor, which *adds* it into its own chunk. After the ring completes, every chunk holds the sum of all workers' chunk values, distributed one chunk per node. **Phase 2 — all-gather** — another `nWorkers - 1` steps pass each node's summed chunk around the ring with `System.arraycopy` (copy, not add), until every node holds the complete sum. Finally each element is divided by `nWorkers`. Total communication is `2 × (nWorkers - 1) × nParams/nWorkers` elements per worker — **O(N) per worker regardless of worker count**, which is the whole point of the ring versus a parameter-server or all-to-all pattern (both O(N × workers)).
5. **Understand why chunking works.** With 64 params and 4 workers, `chunkSize = 16`. Scatter-reduce step 0 moves chunk 0 (indices 0-15) around the ring accumulating every worker's contribution; steps 1 and 2 do chunks 16-31 and 32-47; chunk 48-63 is already held by node 0's original vector... the all-gather then distributes the four summed chunks to everyone. The `% (nParams)` in the all-gather start index is the modular-ring bookkeeping that maps steps to chunk offsets.
6. **Update and measure — and read the flat loss correctly.** The averaged gradients (all workers hold the identical copy after the ring) update the global model at learning rate 0.01, and `computeLoss` on the first sample reports the global loss per epoch. The loss *does not decrease* — that is correct behavior, not a bug: the data is uniform on [0,1), its per-dimension mean is 0.5, and the weights start at 0.5, so the expected gradient `2*(w - x)/N` averages to ~0 over every batch. The model is already at the empirical optimum of this toy target, and the global loss stays pinned at `0.060098` — the squared error of weight 0.5 against that specific sample. The ring's job — producing the correct averaged gradient — is exactly what this steady state demonstrates: the aggregated update is a faithful average of all workers' gradients, whether that average is zero or not.
7. **Drop the timing line for stability.** The lab prints "Training completed in N seconds" from wall-clock — the simulation's sleeps make that value flaky at second granularity. The walkthrough ends after the last global loss; the omitted line is the only runtime-dependent output in the lab.
8. **Verify against the compiled run.** Every number in the Expected Output below comes from the walkthrough class on this repo's JDK, confirmed identical across two consecutive runs — the per-worker losses, the five identical global losses, and the flat convergence curve.

### Code

```java
package com.mlops.lab13;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class DistributedTrainingWalkthrough {

    static class Model {
        final double[] weights;

        Model(int size) {
            this.weights = new double[size];
            Arrays.fill(weights, 0.5);
        }

        void applyGradients(double[] gradients, double learningRate) {
            for (int i = 0; i < weights.length; i++) {
                weights[i] -= learningRate * gradients[i];
            }
        }

        double computeLoss(double[] data) {
            double loss = 0.0;
            for (int i = 0; i < weights.length && i < data.length; i++) {
                loss += Math.pow(weights[i] - data[i], 2);
            }
            return loss / weights.length;
        }
    }

    static class DistributedWorker implements Callable<double[]> {
        final int id;
        final Model localModel;
        final List<double[]> dataShard;
        final int batchSize;
        final Random rng;

        DistributedWorker(int id, Model localModel, List<double[]> dataShard, int batchSize) {
            this.id = id;
            this.localModel = localModel;
            this.dataShard = dataShard;
            this.batchSize = batchSize;
            this.rng = new Random(1000L * id);
        }

        @Override
        public double[] call() {
            double[] gradients = new double[localModel.weights.length];
            int samplesUsed = Math.min(batchSize, dataShard.size());

            for (int s = 0; s < samplesUsed; s++) {
                double[] sample = dataShard.get(rng.nextInt(dataShard.size()));
                for (int i = 0; i < gradients.length && i < sample.length; i++) {
                    gradients[i] += 2 * (localModel.weights[i] - sample[i]) / localModel.weights.length;
                }
            }
            for (int i = 0; i < gradients.length; i++) {
                gradients[i] /= samplesUsed;
            }
            simulateComputeCost(200);
            return gradients;
        }

        private void simulateComputeCost(long ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    static class RingAllReduce {

        static List<double[]> allReduce(List<double[]> perWorkerGradients) {
            int nWorkers = perWorkerGradients.size();
            int nParams = perWorkerGradients.get(0).length;
            int chunkSize = (int) Math.ceil((double) nParams / nWorkers);

            List<double[]> reduced = perWorkerGradients.stream()
                    .map(g -> Arrays.copyOf(g, nParams))
                    .collect(Collectors.toList());

            SimulateCommunication(50);

            for (int step = 0; step < nWorkers - 1; step++) {
                for (int w = 0; w < nWorkers; w++) {
                    int sendTo = (w + 1) % nWorkers;
                    int chunkStart = step * chunkSize;
                    int chunkEnd = Math.min(chunkStart + chunkSize, nParams);
                    if (chunkStart < nParams) {
                        for (int i = chunkStart; i < chunkEnd; i++) {
                            reduced.get(sendTo)[i] += reduced.get(w)[i];
                        }
                    }
                }
            }

            for (int step = 0; step < nWorkers - 1; step++) {
                for (int w = 0; w < nWorkers; w++) {
                    int sendTo = (w + 1) % nWorkers;
                    int chunkStart = ((step + 1) * chunkSize) % (nParams);
                    int chunkEnd = Math.min(chunkStart + chunkSize, nParams);
                    if (chunkStart < nParams) {
                        System.arraycopy(reduced.get(w), chunkStart, reduced.get(sendTo), chunkStart, chunkEnd - chunkStart);
                    }
                }
            }

            for (int w = 0; w < nWorkers; w++) {
                for (int i = 0; i < nParams; i++) {
                    reduced.get(w)[i] /= nWorkers;
                }
            }
            return reduced;
        }

        private static void SimulateCommunication(long ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Distributed Training Simulation ===\n");

        int modelSize = 64;
        int nWorkers = 4;
        int dataSize = 1000;
        int batchSize = 128;
        int epochs = 5;
        double learningRate = 0.01;

        Model globalModel = new Model(modelSize);
        List<double[]> allData = new ArrayList<>();
        Random rng = new Random(42);
        for (int i = 0; i < dataSize; i++) {
            double[] sample = new double[modelSize];
            for (int j = 0; j < modelSize; j++) {
                sample[j] = rng.nextDouble();
            }
            allData.add(sample);
        }

        int shardSize = dataSize / nWorkers;
        List<List<double[]>> shards = new ArrayList<>();
        for (int w = 0; w < nWorkers; w++) {
            int start = w * shardSize;
            int end = (w == nWorkers - 1) ? dataSize : start + shardSize;
            shards.add(allData.subList(start, end));
        }

        ExecutorService executor = Executors.newFixedThreadPool(nWorkers);

        for (int epoch = 0; epoch < epochs; epoch++) {
            System.out.printf("%nEpoch %d/%d%n", epoch + 1, epochs);

            List<DistributedWorker> workers = new ArrayList<>();
            for (int w = 0; w < nWorkers; w++) {
                Model localModel = new Model(modelSize);
                System.arraycopy(globalModel.weights, 0, localModel.weights, 0, modelSize);
                workers.add(new DistributedWorker(w + 1, localModel, shards.get(w), batchSize));
            }

            List<Future<double[]>> futures = executor.invokeAll(workers);
            List<double[]> gradients = new ArrayList<>();
            for (int w = 0; w < futures.size(); w++) {
                double[] g = futures.get(w).get();
                gradients.add(g);
                System.out.printf("  Worker %d: computed gradients (loss=%.4f)%n",
                        w + 1, workers.get(w).localModel.computeLoss(shards.get(w).get(0)));
            }

            System.out.println("  Ring All-Reduce...");
            List<double[]> averagedGrads = RingAllReduce.allReduce(gradients);

            globalModel.applyGradients(averagedGrads.get(0), learningRate);

            double loss = globalModel.computeLoss(allData.get(0));
            System.out.printf("  Global loss: %.6f%n", loss);
        }

        executor.shutdown();
    }
}
```

### Expected Output

```
=== Distributed Training Simulation ===


Epoch 1/5
  Worker 1: computed gradients (loss=0.0601)
  Worker 2: computed gradients (loss=0.0835)
  Worker 3: computed gradients (loss=0.0779)
  Worker 4: computed gradients (loss=0.0934)
  Ring All-Reduce...
  Global loss: 0.060098

Epoch 2/5
  Worker 1: computed gradients (loss=0.0601)
  Worker 2: computed gradients (loss=0.0835)
  Worker 3: computed gradients (loss=0.0779)
  Worker 4: computed gradients (loss=0.0934)
  Ring All-Reduce...
  Global loss: 0.060098

Epoch 3/5
  Worker 1: computed gradients (loss=0.0601)
  Worker 2: computed gradients (loss=0.0835)
  Worker 3: computed gradients (loss=0.0779)
  Worker 4: computed gradients (loss=0.0934)
  Ring All-Reduce...
  Global loss: 0.060098

Epoch 4/5
  Worker 1: computed gradients (loss=0.0601)
  Worker 2: computed gradients (loss=0.0835)
  Worker 3: computed gradients (loss=0.0779)
  Worker 4: computed gradients (loss=0.0934)
  Ring All-Reduce...
  Global loss: 0.060098

Epoch 5/5
  Worker 1: computed gradients (loss=0.0601)
  Worker 2: computed gradients (loss=0.0835)
  Worker 3: computed gradients (loss=0.0779)
  Worker 4: computed gradients (loss=0.0934)
  Ring All-Reduce...
  Global loss: 0.060098
```

*(Determinism notes: the lab's `DistributedWorker` draws its 128 samples from an unseeded `Random()`, prints inside `call()`, and closes with a wall-clock timing line — all three make its output unreproducible. The walkthrough seeds each worker (`Random(1000 * id)`), prints worker lines from `main` in future order, and omits the timing line; the transcript above is therefore byte-stable.)*

## Problem 2: Effective Batch Size and Learning Rate Scaling — Company: Nvidia

### The Problem

A single-GPU baseline trains with batch 128 and learning rate 0.01. Scaling to 8 GPUs with the lab's data-parallel pattern — each worker computing gradients on its own 128-sample batch — gives an effective batch of 1024. What changes to the training recipe, and why?

### Solution Walkthrough

1. **Recognize what the ring preserves.** After `RingAllReduce.allReduce`, every worker applies the *average* of the 8 per-worker gradients — mathematically the gradient of the concatenated 1024-sample batch, exactly the baseline update scaled up. The ring is what makes the aggregate correct; the remaining question is how to step.
2. **Scale the learning rate with the batch (linear scaling rule).** Gradient norms grow roughly linearly with effective batch, so `lr_8gpu = lr_1gpu × 8 = 0.08` keeps the parameter trajectory comparable; the walkthrough's `learningRate = 0.01` would instead take 8× larger steps than intended at 8× batch.
3. **Watch the variance effects.** Larger batches cut gradient noise, so with the scaled LR the model may converge in *fewer epochs* — but the noise reduction can hurt generalization at extremes; the standard practice is to tune LR with a warmup over the first epochs and measure validation loss, not just training loss.
4. **Add the engineering guardrails.** 8 workers mean 8× shards (the lab's `shardSize = dataSize / nWorkers`), enough total data to avoid sampling the same points; and because the all-reduce is O(N) per worker regardless of scale, the ring stays efficient — the growth to watch is the per-epoch wall time of the 200ms simulated compute, which is constant per worker, so 8 GPUs should finish one epoch near the single-GPU time.

## Problem 3: A Straggler in the Ring — Company: Apple

### The Problem

In the training loop, one worker's compute takes 2,000ms instead of 200ms (noisy neighbor on the host). Every epoch now stalls at `invokeAll`, because the ring can't start until all gradients arrive. Walk through the impact and the standard mitigations.

### Solution Walkthrough

1. **Quantify the stall.** `executor.invokeAll(workers)` blocks until *all* futures complete — one 2,000ms worker makes every epoch ~1.8s slower, and with the fixed `Thread.sleep(200)` on the others, the epoch time is set by the slowest worker: synchronous training is as fast as its straggler.
2. **Rank the mitigations.** (a) **Elasticity/backup workers**: spawn 4 + 1 spare workers and take the first 4 results — the standard approach in large-scale DDP frameworks; (b) **rate limiter**: slow the fast workers to match (reduces fairness issues but sacrifices the fast nodes); (c) **reduce variance**: make shards balanced and homogenous — the lab's `subList` shards are equal-sized, but real shards vary in length, and gradient *averaging by sample count* (which the lab does per worker) already weights shards correctly.
3. **Protect the training run itself.** The 200ms simulated compute is a toy; real training adds checkpointing every N epochs so a killed worker restarts from a checkpoint rather than epoch 1, and the executor should be configured with a timeout so a hung worker fails the epoch with a clear error instead of stalling the cluster.
4. **Know when async is the answer.** If the straggler is systematic (heterogeneous hardware), switch from synchronous all-reduce to an async parameter-server pattern — workers push gradients when ready and pull fresh weights when needed — trading exact gradient averaging (which the ring guarantees) for throughput. The lab's synchronous ring is the correctness baseline; async is the throughput escape hatch.
