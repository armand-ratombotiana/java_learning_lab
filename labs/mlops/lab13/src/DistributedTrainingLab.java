package com.mlops.lab13;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Distributed Training — Lab 13.
 * <p>
 * Simulates distributed training concepts: data parallelism, model parallelism,
 * ring all-reduce gradient aggregation, and synchronous vs asynchronous training.
 * Demonstrates communication patterns used in Horovod, DeepSpeed, and PyTorch DDP.
 */
public class DistributedTrainingLab {

    /** Simulates a small neural network model with weights. */
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

    /** Simulates a distributed worker with local data shard. */
    static class DistributedWorker implements Callable<double[]> {
        final int id;
        final Model localModel;
        final List<double[]> dataShard;
        final int batchSize;
        final Random rng = new Random();

        DistributedWorker(int id, Model localModel, List<double[]> dataShard, int batchSize) {
            this.id = id;
            this.localModel = localModel;
            this.dataShard = dataShard;
            this.batchSize = batchSize;
        }

        @Override
        public double[] call() {
            // Compute gradients on local data shard
            double[] gradients = new double[localModel.weights.length];
            int samplesUsed = Math.min(batchSize, dataShard.size());

            for (int s = 0; s < samplesUsed; s++) {
                double[] sample = dataShard.get(rng.nextInt(dataShard.size()));
                for (int i = 0; i < gradients.length && i < sample.length; i++) {
                    gradients[i] += 2 * (localModel.weights[i] - sample[i]) / localModel.weights.length;
                }
            }
            // Average gradients
            for (int i = 0; i < gradients.length; i++) {
                gradients[i] /= samplesUsed;
            }
            simulateComputeCost(200); // Simulated computation time
            System.out.printf("  Worker %d: computed gradients (loss=%.4f)%n",
                    id, localModel.computeLoss(dataShard.get(0)));
            return gradients;
        }

        private void simulateComputeCost(long ms) {
            try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    /** Ring all-reduce implementation. */
    static class RingAllReduce {

        /**
         * Performs ring all-reduce to average gradients across workers.
         * O(N) communication per worker.
         */
        static List<double[]> allReduce(List<double[]> perWorkerGradients) {
            int nWorkers = perWorkerGradients.size();
            int nParams = perWorkerGradients.get(0).length;
            int chunkSize = (int) Math.ceil((double) nParams / nWorkers);

            // Deep copy for scatter-reduce phase
            List<double[]> reduced = perWorkerGradients.stream()
                    .map(g -> Arrays.copyOf(g, nParams))
                    .collect(Collectors.toList());

            SimulateCommunication(50); // Simulated network latency

            // Phase 1: Scatter-reduce (each node sums a chunk)
            for (int step = 0; step < nWorkers - 1; step++) {
                for (int w = 0; w < nWorkers; w++) {
                    int sendTo = (w + 1) % nWorkers;
                    int chunkStart = step * chunkSize;
                    int chunkEnd = Math.min(chunkStart + chunkSize, nParams);
                    // Send chunk from w to (w+1), add to receiver's chunk
                    if (chunkStart < nParams) {
                        for (int i = chunkStart; i < chunkEnd; i++) {
                            reduced.get(sendTo)[i] += reduced.get(w)[i];
                        }
                    }
                }
            }

            // Phase 2: All-gather (each node gets full averaged result)
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

            // Average gradients
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

        // Initialize model and data
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

        // Shard data across workers
        int shardSize = dataSize / nWorkers;
        List<List<double[]>> shards = new ArrayList<>();
        for (int w = 0; w < nWorkers; w++) {
            int start = w * shardSize;
            int end = (w == nWorkers - 1) ? dataSize : start + shardSize;
            shards.add(allData.subList(start, end));
        }

        ExecutorService executor = Executors.newFixedThreadPool(nWorkers);
        Instant start = Instant.now();

        // Training loop
        for (int epoch = 0; epoch < epochs; epoch++) {
            System.out.printf("%nEpoch %d/%d%n", epoch + 1, epochs);

            // Step 1: Create workers with local model copies
            List<DistributedWorker> workers = new ArrayList<>();
            for (int w = 0; w < nWorkers; w++) {
                Model localModel = new Model(modelSize);
                System.arraycopy(globalModel.weights, 0, localModel.weights, 0, modelSize);
                workers.add(new DistributedWorker(w + 1, localModel, shards.get(w), batchSize));
            }

            // Step 2: Compute gradients in parallel
            List<Future<double[]>> futures = executor.invokeAll(workers);
            List<double[]> gradients = new ArrayList<>();
            for (Future<double[]> f : futures) {
                gradients.add(f.get());
            }

            // Step 3: Ring all-reduce to average gradients
            System.out.println("  Ring All-Reduce...");
            List<double[]> averagedGrads = RingAllReduce.allReduce(gradients);

            // Step 4: Update global model
            globalModel.applyGradients(averagedGrads.get(0), learningRate);

            double loss = globalModel.computeLoss(allData.get(0));
            System.out.printf("  Global loss: %.6f%n", loss);
        }

        Instant end = Instant.now();
        executor.shutdown();
        System.out.printf("%nTraining completed in %d seconds%n",
                Duration.between(start, end).toSeconds());
    }

}
