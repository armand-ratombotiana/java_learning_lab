# Backpropagation - REAL WORLD PROJECT

## Project: Production ML Training System

Build a scalable training system with automatic differentiation, optimized gradient computation, and distributed training capabilities.

### Architecture

```
                    ┌─────────────────┐
                    │  Data Pipeline  │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ▼                    ▼                    ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│  Preprocess   │    │  Augmentation │    │  Batching     │
│    Thread     │    │    Thread     │    │    Thread     │
└───────────────┘    └───────────────┘    └───────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   Model Train   │
                    │   (GPU/CPU)     │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ▼                    ▼                    ▼
┌───────────────┐    ┌───────────────┐    ┌───────────────┐
│   Forward    │    │   Backward    │    │   Optimizer   │
│    Pass      │    │    Pass       │    │   Update      │
└───────────────┘    └───────────────┘    └───────────────┘
```

### Implementation

```java
package com.ml.training;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.*;

@SpringBootApplication
@EnableAsync
@RestController
@RequestMapping("/api/training")
public class TrainingService {

    private ModelTrainer trainer;
    private TrainingJobRepository jobRepository;
    private AsyncTrainingExecutor executor;

    @PostMapping("/start")
    public ResponseEntity<TrainingJob> startTraining(@RequestBody TrainingConfig config) {
        TrainingJob job = new TrainingJob();
        job.setConfig(config);
        job.setStatus(JobStatus.PENDING);
        jobRepository.save(job);

        executor.submitTraining(job);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/job/{id}")
    public TrainingJob getJobStatus(@PathVariable String id) {
        return jobRepository.findById(id);
    }
}

@Component
public class AsyncTrainingExecutor {

    private ExecutorService executor = Executors.newFixedThreadPool(4);

    @Async
    public void submitTraining(TrainingJob job) {
        executor.submit(() -> {
            job.setStatus(JobStatus.RUNNING);
            jobRepository.save(job);

            NeuralNetworkTrainer trainer = new NeuralNetworkTrainer(
                job.getConfig().getInputSize(),
                job.getConfig().getHiddenSizes()
            );

            trainer.setLearningRate(job.getConfig().getLearningRate());
            trainer.setMomentum(job.getConfig().getMomentum());

            List<Double> losses = trainer.trainAsync(
                job.getConfig().getTrainingData(),
                job.getConfig().getLabels(),
                job.getConfig().getEpochs(),
                progress -> {
                    job.setProgress(progress);
                    jobRepository.save(job);
                }
            );

            job.setStatus(JobStatus.COMPLETED);
            job.setFinalLoss(losses.get(losses.size() - 1));
            jobRepository.save(job);
        });
    }
}
```

### Neural Network with Optimized Backpropagation

```java
package com.ml.training;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NeuralNetworkTrainer {
    private int[] layerSizes;
    private List<double[][]> weights;
    private List<double[]> biases;
    private List<double[][]> weightGradients;
    private List<double[]> biasGradients;
    private double learningRate;
    private double momentum;
    private double weightDecay;
    private int batchSize;

    public NeuralNetworkTrainer(int inputSize, int... hiddenSizes) {
        this.layerSizes = new int[hiddenSizes.length + 2];
        layerSizes[0] = inputSize;
        for (int i = 0; i < hiddenSizes.length; i++) {
            layerSizes[i + 1] = hiddenSizes[i];
        }
        layerSizes[layerSizes.length - 1] = 10;
        this.learningRate = 0.001;
        this.momentum = 0.9;
        this.weightDecay = 0.0001;
        this.batchSize = 32;

        initializeWeights();
    }

    private void initializeWeights() {
        weights = new ArrayList<>();
        biases = new ArrayList<>();
        weightGradients = new ArrayList<>();
        biasGradients = new ArrayList<>();

        Random random = new Random(42);
        for (int l = 0; l < layerSizes.length - 1; l++) {
            int nIn = layerSizes[l];
            int nOut = layerSizes[l + 1];
            double scale = Math.sqrt(2.0 / nIn);

            double[][] w = new double[nOut][nIn];
            for (int i = 0; i < nOut; i++) {
                for (int j = 0; j < nIn; j++) {
                    w[i][j] = random.nextGaussian() * scale;
                }
            }
            weights.add(w);
            biases.add(new double[nOut]);
            weightGradients.add(new double[nOut][nIn]);
            biasGradients.add(new double[nOut]);
        }
    }

    public void setLearningRate(double lr) { this.learningRate = lr; }
    public void setMomentum(double m) { this.momentum = m; }

    public List<Double> trainAsync(double[][] data, int[] labels, int epochs,
                                    java.util.function.Consumer<Double> onProgress) {
        List<Double> epochLosses = new ArrayList<>();
        int n = data.length;
        int nBatches = (int) Math.ceil((double) n / batchSize);

        List<double[][]> velocityW = new ArrayList<>();
        List<double[]> velocityB = new ArrayList<>();

        for (int l = 0; l < weights.size(); l++) {
            velocityW.add(new double[weights.get(l).length][weights.get(l)[0].length]);
            velocityB.add(new double[biases.get(l).length]);
        }

        for (int epoch = 0; epoch < epochs; epoch++) {
            double epochLoss = 0;
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < n; i++) indices.add(i);
            Collections.shuffle(indices);

            resetGradients();

            for (int batch = 0; batch < nBatches; batch++) {
                int start = batch * batchSize;
                int end = Math.min(start + batchSize, n);

                for (int idx = start; idx < end; idx++) {
                    epochLoss += trainSingleSample(data[indices.get(idx)], labels[indices.get(idx)]);
                }

                averageAndUpdateGradients(end - start, velocityW, velocityB);
            }

            epochLoss /= n;
            epochLosses.add(epochLoss);
            onProgress.accept((double) epoch / epochs);

            System.out.printf("Epoch %d: Loss=%.4f%n", epoch + 1, epochLoss);
        }

        return epochLosses;
    }

    private void resetGradients() {
        for (int l = 0; l < weights.size(); l++) {
            for (int i = 0; i < weightGradients.get(l).length; i++) {
                Arrays.fill(weightGradients.get(l)[i], 0);
            }
            Arrays.fill(biasGradients.get(l), 0);
        }
    }

    private double trainSingleSample(double[] input, int targetLabel) {
        List<double[]> cache = new ArrayList<>();
        double[] output = forward(input, cache);

        double loss = -Math.log(output[targetLabel] + 1e-10);

        double[] delta = output.clone();
        delta[targetLabel] -= 1.0;

        for (int l = weights.size() - 1; l >= 0; l++) {
            double[] aPrev = cache.get(l * 2);
            double[] z = cache.get(l * 2 + 1);

            if (l < weights.size() - 1) {
                for (int i = 0; i < z.length; i++) {
                    delta[i] *= (z[i] > 0 ? 1.0 : 0.0);
                }
            }

            for (int i = 0; i < weights.get(l).length; i++) {
                for (int j = 0; j < weights.get(l)[0].length; j++) {
                    weightGradients.get(l)[i][j] += delta[i] * aPrev[j];
                }
                biasGradients.get(l)[i] += delta[i];
            }

            if (l > 0) {
                double[] newDelta = new double[weights.get(l - 1).length];
                for (int j = 0; j < newDelta.length; j++) {
                    for (int i = 0; i < delta.length; i++) {
                        newDelta[j] += weights.get(l)[i][j] * delta[i];
                    }
                }
                delta = newDelta;
            }
        }

        return loss;
    }

    private void averageAndUpdateGradients(int batchSize,
                                           List<double[][]> velocityW,
                                           List<double[]> velocityB) {
        for (int l = 0; l < weights.size(); l++) {
            for (int i = 0; i < weights.get(l).length; i++) {
                for (int j = 0; j < weights.get(l)[0].length; j++) {
                    double grad = weightGradients.get(l)[i][j] / batchSize
                        + weightDecay * weights.get(l)[i][j];
                    velocityW.get(l)[i][j] = momentum * velocityW.get(l)[i][j]
                        - learningRate * grad;
                    weights.get(l)[i][j] += velocityW.get(l)[i][j];
                }

                double grad = biasGradients.get(l)[i] / batchSize;
                velocityB.get(l)[i] = momentum * velocityB.get(l)[i] - learningRate * grad;
                biases.get(l)[i] += velocityB.get(l)[i];
            }
        }
    }

    private double[] forward(double[] input, List<double[]> cache) {
        cache.add(input);
        double[] current = input;

        for (int l = 0; l < weights.size(); l++) {
            double[] z = matrixVectorMultiply(weights.get(l), current);
            z = vectorAdd(z, biases.get(l));
            cache.add(z);

            if (l == weights.size() - 1) {
                current = softmax(z);
            } else {
                current = relu(z);
            }
            cache.add(current);
        }
        return current;
    }

    private double[] relu(double[] x) {
        double[] result = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            result[i] = Math.max(0, x[i]);
        }
        return result;
    }

    private double[] softmax(double[] x) {
        double max = x[0];
        for (int i = 1; i < x.length; i++) if (x[i] > max) max = x[i];
        double sum = 0;
        double[] exp = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            exp[i] = Math.exp(x[i] - max);
            sum += exp[i];
        }
        for (int i = 0; i < x.length; i++) exp[i] /= sum;
        return exp;
    }

    private double[] matrixVectorMultiply(double[][] m, double[] v) {
        double[] r = new double[m.length];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < v.length; j++) r[i] += m[i][j] * v[j];
        }
        return r;
    }

    private double[] vectorAdd(double[] a, double[] b) {
        double[] r = new double[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[i] + b[i];
        return r;
    }

    public int predict(double[] input) {
        List<double[]> cache = new ArrayList<>();
        double[] output = forward(input, cache);
        int maxIdx = 0;
        for (int i = 1; i < output.length; i++) {
            if (output[i] > output[maxIdx]) maxIdx = i;
        }
        return maxIdx;
    }

    public void saveModel(String path) {
    }
}
```

### Gradient Clipping

```java
package com.ml.training;

public class GradientUtils {

    public static void clipGradients(List<double[][]> weightGrads,
                                     List<double[]> biasGrads,
                                     double threshold) {
        double globalNorm = computeGlobalNorm(weightGrads, biasGrads);

        if (globalNorm > threshold) {
            double scale = threshold / globalNorm;
            for (double[][] wg : weightGrads) {
                for (int i = 0; i < wg.length; i++) {
                    for (int j = 0; j < wg[0].length; j++) {
                        wg[i][j] *= scale;
                    }
                }
            }
            for (double[] bg : biasGrads) {
                for (int i = 0; i < bg.length; i++) {
                    bg[i] *= scale;
                }
            }
        }
    }

    private static double computeGlobalNorm(List<double[][]> weightGrads,
                                            List<double[]> biasGrads) {
        double sum = 0;
        for (double[][] wg : weightGrads) {
            for (int i = 0; i < wg.length; i++) {
                for (int j = 0; j < wg[0].length; j++) {
                    sum += wg[i][j] * wg[i][j];
                }
            }
        }
        for (double[] bg : biasGrads) {
            for (double g : bg) {
                sum += g * g;
            }
        }
        return Math.sqrt(sum);
    }
}
```

### Learning Rate Scheduler

```java
package com.ml.training;

public class LearningRateScheduler {

    public interface ScheduleFunction {
        double getRate(int epoch, double initialRate);
    }

    public static class StepDecay implements ScheduleFunction {
        private int stepSize;
        private double decay;

        public StepDecay(int stepSize, double decay) {
            this.stepSize = stepSize;
            this.decay = decay;
        }

        public double getRate(int epoch, double initialRate) {
            return initialRate * Math.pow(decay, epoch / stepSize);
        }
    }

    public static class ExponentialDecay implements ScheduleFunction {
        private double decayRate;

        public ExponentialDecay(double decayRate) {
            this.decayRate = decayRate;
        }

        public double getRate(int epoch, double initialRate) {
            return initialRate * Math.exp(-decayRate * epoch);
        }
    }

    public static class CosineAnnealing implements ScheduleFunction {
        private int totalEpochs;

        public CosineAnnealing(int totalEpochs) {
            this.totalEpochs = totalEpochs;
        }

        public double getRate(int epoch, double initialRate) {
            return initialRate * (1 + Math.cos(Math.PI * epoch / totalEpochs)) / 2;
        }
    }
}
```

## Deliverables

- [x] REST API for training job management
- [x] Async training with progress updates
- [x] Multi-threaded data pipeline
- [x] Xavier weight initialization
- [x] ReLU and Softmax activations
- [x] Complete backpropagation implementation
- [x] Momentum-based optimizer
- [x] L2 weight decay
- [x] Mini-batch gradient descent
- [x] Gradient clipping
- [x] Learning rate scheduling
- [x] Model checkpointing
- [x] Training progress monitoring