# Optimization - Real World Project

## Production-Ready Neural Network Optimizer Framework

### Project Overview
Build a comprehensive optimization framework for training neural networks from scratch in Java. This production-ready system will support multiple optimization algorithms, learning rate scheduling, gradient clipping, and regularization.

### Target Users
- ML engineers training custom neural networks
- Researchers experimenting with optimization algorithms
- Students learning deep learning implementation

---

## Project Architecture

```
optimization-framework/
├── src/main/java/com/ml/optimize/
│   ├── Optimizer.java
│   ├── OptimizerConfig.java
│   ├── LossFunction.java
│   ├── param/
│   │   ├── Parameters.java
│   │   └── ParameterHistory.java
│   ├── schedulers/
│   │   ├── LearningRateScheduler.java
│   │   ├── ExponentialScheduler.java
│   │   ├── CosineAnnealingScheduler.java
│   │   ├── WarmupScheduler.java
│   │   └── StepDecayScheduler.java
│   ├── optimizers/
│   │   ├── BaseOptimizer.java
│   │   ├── SGDOptimizer.java
│   │   ├── MomentumOptimizer.java
│   │   ├── NesterovOptimizer.java
│   │   ├── AdagradOptimizer.java
│   │   ├── RMSpropOptimizer.java
│   │   ├── AdamOptimizer.java
│   │   └── AdamWOptimizer.java
│   ├── regularizers/
│   │   ├── Regularizer.java
│   │   ├── L2Regularizer.java
│   │   ├── L1Regularizer.java
│   │   └── ElasticNetRegularizer.java
│   ├── clipper/
│   │   └── GradientClipper.java
│   ├── trainer/
│   │   ├── NeuralNetworkTrainer.java
│   │   ├── TrainingListener.java
│   │   └── TrainingMetrics.java
│   └── util/
│       ├── MatrixUtil.java
│       └── MathUtil.java
├── src/main/resources/
│   └── config.properties
├── src/test/java/com/ml/optimize/
│   ├── OptimizerTests.java
│   ├── SchedulerTests.java
│   └── IntegrationTests.java
├── build.gradle
└── README.md
```

---

## Core Components

### 1. Optimizer Interface and Configuration

```java
package com.ml.optimize;

import java.util.List;

public interface Optimizer {
    void initialize(int paramCount);
    double[] update(double[] params, double[] gradients, int iteration);
    void setLearningRate(double lr);
    double getLearningRate();
    OptimizerConfig getConfig();
    String getName();
}
```

```java
package com.ml.optimize;

public record OptimizerConfig(
    double learningRate,
    double momentum,
    double beta1,
    double beta2,
    double epsilon,
    double weightDecay,
    boolean useNesterov,
    LearningRateScheduler scheduler,
    GradientClipper clipper
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private double learningRate = 0.001;
        private double momentum = 0.0;
        private double beta1 = 0.9;
        private double beta2 = 0.999;
        private double epsilon = 1e-8;
        private double weightDecay = 0.0;
        private boolean useNesterov = false;
        private LearningRateScheduler scheduler = null;
        private GradientClipper clipper = null;

        public Builder learningRate(double lr) {
            this.learningRate = lr;
            return this;
        }

        public Builder momentum(double m) {
            this.momentum = m;
            return this;
        }

        public Builder beta1(double b1) {
            this.beta1 = b1;
            return this;
        }

        public Builder beta2(double b2) {
            this.beta2 = b2;
            return this;
        }

        public Builder epsilon(double eps) {
            this.epsilon = eps;
            return this;
        }

        public Builder weightDecay(double wd) {
            this.weightDecay = wd;
            return this;
        }

        public Builder nesterov(boolean n) {
            this.useNesterov = n;
            return this;
        }

        public Builder scheduler(LearningRateScheduler s) {
            this.scheduler = s;
            return this;
        }

        public Builder clipper(GradientClipper c) {
            this.clipper = c;
            return this;
        }

        public OptimizerConfig build() {
            return new OptimizerConfig(
                learningRate, momentum, beta1, beta2,
                epsilon, weightDecay, useNesterov,
                scheduler, clipper
            );
        }
    }
}
```

### 2. Learning Rate Schedulers

```java
package com.ml.optimize.schedulers;

public interface LearningRateScheduler {
    double getLearningRate(int iteration);
    void reset();
    String getType();
}
```

```java
package com.ml.optimize.schedulers;

public class ExponentialScheduler implements LearningRateScheduler {

    private final double initialLr;
    private final double decayRate;
    private final int decayEvery;

    public ExponentialScheduler(double initialLr, double decayRate, int decayEvery) {
        this.initialLr = initialLr;
        this.decayRate = decayRate;
        this.decayEvery = decayEvery;
    }

    @Override
    public double getLearningRate(int iteration) {
        int decaySteps = iteration / decayEvery;
        return initialLr * Math.pow(decayRate, decaySteps);
    }

    @Override
    public void reset() {}

    @Override
    public String getType() {
        return "exponential";
    }
}
```

```java
package com.ml.optimize.schedulers;

public class CosineAnnealingScheduler implements LearningRateScheduler {

    private final double initialLr;
    private final double minLr;
    private final int totalSteps;
    private final int warmupSteps;

    public CosineAnnealingScheduler(double initialLr, double minLr,
                                    int totalSteps, int warmupSteps) {
        this.initialLr = initialLr;
        this.minLr = minLr;
        this.totalSteps = totalSteps;
        this.warmupSteps = warmupSteps;
    }

    @Override
    public double getLearningRate(int iteration) {
        if (iteration < warmupSteps) {
            return initialLr * (iteration + 1) / warmupSteps;
        }

        double progress = (iteration - warmupSteps) / (double)(totalSteps - warmupSteps);
        progress = Math.min(progress, 1.0);
        return minLr + (initialLr - minLr) * (1 + Math.cos(Math.PI * progress)) / 2;
    }

    @Override
    public void reset() {}

    @Override
    public String getType() {
        return "cosine_annealing";
    }
}
```

```java
package com.ml.optimize.schedulers;

public class WarmupScheduler implements LearningRateScheduler {

    private final double targetLr;
    private final int warmupSteps;
    private final LearningRateScheduler afterWarmup;

    public WarmupScheduler(double targetLr, int warmupSteps,
                           LearningRateScheduler afterWarmup) {
        this.targetLr = targetLr;
        this.warmupSteps = warmupSteps;
        this.afterWarmup = afterWarmup;
    }

    @Override
    public double getLearningRate(int iteration) {
        if (iteration < warmupSteps) {
            return targetLr * (iteration + 1) / warmupSteps;
        }
        return afterWarmup.getLearningRate(iteration - warmupSteps);
    }

    @Override
    public void reset() {
        afterWarmup.reset();
    }

    @Override
    public String getType() {
        return "warmup";
    }
}
```

### 3. Adam Optimizer Implementation

```java
package com.ml.optimize.optimizers;

import com.ml.optimize.Optimizer;
import com.ml.optimize.OptimizerConfig;
import com.ml.optimize.schedulers.LearningRateScheduler;
import com.ml.optimize.clipper.GradientClipper;

public class AdamOptimizer implements Optimizer {

    private OptimizerConfig config;
    private double learningRate;
    private double[] m; // First moment
    private double[] v; // Second moment
    private double beta1;
    private double beta2;
    private double epsilon;
    private int t; // Time step
    private LearningRateScheduler scheduler;
    private GradientClipper clipper;

    public AdamOptimizer(OptimizerConfig config) {
        this.config = config;
        this.learningRate = config.learningRate();
        this.beta1 = config.beta1();
        this.beta2 = config.beta2();
        this.epsilon = config.epsilon();
        this.scheduler = config.scheduler();
        this.clipper = config.clipper();
    }

    @Override
    public void initialize(int paramCount) {
        m = new double[paramCount];
        v = new double[paramCount];
        t = 0;
    }

    @Override
    public double[] update(double[] params, double[] gradients, int iteration) {
        if (m == null) {
            initialize(params.length);
        }

        if (gradients.length != params.length) {
            throw new IllegalArgumentException(
                "Gradient dimension mismatch: " + gradients.length + " vs " + params.length);
        }

        // Apply gradient clipping if configured
        double[] grad = gradients;
        if (clipper != null) {
            grad = clipper.clip(gradients);
        }

        t++;

        // Update biased first moment estimate
        for (int i = 0; i < params.length; i++) {
            m[i] = beta1 * m[i] + (1 - beta1) * grad[i];
            v[i] = beta2 * v[i] + (1 - beta2) * grad[i] * grad[i];
        }

        // Bias correction
        double mHat[] = new double[params.length];
        double vHat[] = new double[params.length];
        double biasCorrection1 = 1 - Math.pow(beta1, t);
        double biasCorrection2 = 1 - Math.pow(beta2, t);

        for (int i = 0; i < params.length; i++) {
            mHat[i] = m[i] / biasCorrection1;
            vHat[i] = v[i] / biasCorrection2;
        }

        // Compute effective learning rate
        double effectiveLr = learningRate;
        if (scheduler != null) {
            effectiveLr = scheduler.getLearningRate(iteration);
        }

        // Update parameters
        double[] updatedParams = new double[params.length];
        for (int i = 0; i < params.length; i++) {
            updatedParams[i] = params[i] - effectiveLr * mHat[i] /
                               (Math.sqrt(vHat[i]) + epsilon);
        }

        return updatedParams;
    }

    @Override
    public void setLearningRate(double lr) {
        this.learningRate = lr;
    }

    @Override
    public double getLearningRate() {
        return learningRate;
    }

    @Override
    public OptimizerConfig getConfig() {
        return config;
    }

    @Override
    public String getName() {
        return "Adam";
    }
}
```

### 4. AdamW (Adam with Weight Decay)

```java
package com.ml.optimize.optimizers;

import com.ml.optimize.Optimizer;
import com.ml.optimize.OptimizerConfig;
import com.ml.optimize.schedulers.LearningRateScheduler;
import com.ml.optimize.clipper.GradientClipper;

public class AdamWOptimizer implements Optimizer {

    private OptimizerConfig config;
    private double learningRate;
    private double[] m;
    private double[] v;
    private double beta1;
    private double beta2;
    private double epsilon;
    private double weightDecay;
    private int t;
    private LearningRateScheduler scheduler;
    private GradientClipper clipper;

    public AdamWOptimizer(OptimizerConfig config) {
        this.config = config;
        this.learningRate = config.learningRate();
        this.beta1 = config.beta1();
        this.beta2 = config.beta2();
        this.epsilon = config.epsilon();
        this.weightDecay = config.weightDecay();
        this.scheduler = config.scheduler();
        this.clipper = config.clipper();
    }

    @Override
    public void initialize(int paramCount) {
        m = new double[paramCount];
        v = new double[paramCount];
        t = 0;
    }

    @Override
    public double[] update(double[] params, double[] gradients, int iteration) {
        if (m == null) {
            initialize(params.length);
        }

        double[] grad = gradients;
        if (clipper != null) {
            grad = clipper.clip(gradients);
        }

        t++;

        // Update moments
        for (int i = 0; i < params.length; i++) {
            m[i] = beta1 * m[i] + (1 - beta1) * grad[i];
            v[i] = beta2 * v[i] + (1 - beta2) * grad[i] * grad[i];
        }

        // Bias correction
        double biasCorrection1 = 1 - Math.pow(beta1, t);
        double biasCorrection2 = 1 - Math.pow(beta2, t);

        double effectiveLr = learningRate;
        if (scheduler != null) {
            effectiveLr = scheduler.getLearningRate(iteration);
        }

        // Update parameters with weight decay (decoupled from gradient update)
        double[] updatedParams = new double[params.length];
        for (int i = 0; i < params.length; i++) {
            double mHat = m[i] / biasCorrection1;
            double vHat = v[i] / biasCorrection2;
            updatedParams[i] = params[i] * (1 - effectiveLr * weightDecay) -
                               effectiveLr * mHat / (Math.sqrt(vHat[i]) + epsilon);
        }

        return updatedParams;
    }

    @Override
    public void setLearningRate(double lr) {
        this.learningRate = lr;
    }

    @Override
    public double getLearningRate() {
        return learningRate;
    }

    @Override
    public OptimizerConfig getConfig() {
        return config;
    }

    @Override
    public String getName() {
        return "AdamW";
    }
}
```

### 5. Gradient Clipper

```java
package com.ml.optimize.clipper;

public class GradientClipper {

    private final double maxNorm;
    private final ClipType type;

    public enum ClipType {
        BY_NORM,
        BY_VALUE,
        CLIP_ALPHA
    }

    public GradientClipper(double maxNorm, ClipType type) {
        this.maxNorm = maxNorm;
        this.type = type;
    }

    public static GradientClipper byNorm(double maxNorm) {
        return new GradientClipper(maxNorm, ClipType.BY_NORM);
    }

    public static GradientClipper byValue(double maxValue) {
        return new GradientClipper(maxValue, ClipType.BY_VALUE);
    }

    public double[] clip(double[] gradients) {
        return switch (type) {
            case BY_NORM -> clipByNorm(gradients);
            case BY_VALUE -> clipByValue(gradients);
            case CLIP_ALPHA -> clipAlpha(gradients);
        };
    }

    private double[] clipByNorm(double[] gradients) {
        double norm = 0.0;
        for (double g : gradients) {
            norm += g * g;
        }
        norm = Math.sqrt(norm);

        if (norm > maxNorm) {
            double scale = maxNorm / norm;
            double[] clipped = new double[gradients.length];
            for (int i = 0; i < gradients.length; i++) {
                clipped[i] = gradients[i] * scale;
            }
            return clipped;
        }
        return gradients;
    }

    private double[] clipByValue(double[] gradients) {
        double[] clipped = new double[gradients.length];
        for (int i = 0; i < gradients.length; i++) {
            clipped[i] = Math.max(-maxNorm, Math.min(maxNorm, gradients[i]));
        }
        return clipped;
    }

    private double[] clipAlpha(double[] gradients) {
        // Gradient clipping using α: clip at α percentile
        // Simplified implementation
        return clipByNorm(gradients);
    }
}
```

### 6. Neural Network Trainer

```java
package com.ml.optimize.trainer;

import com.ml.optimize.Optimizer;
import com.ml.optimize.LossFunction;

import java.util.ArrayList;
import java.util.List;

public class NeuralNetworkTrainer {

    private Optimizer optimizer;
    private LossFunction lossFunction;
    private List<TrainingListener> listeners;
    private int maxEpochs;
    private double earlyStopThreshold;
    private int patience;

    public NeuralNetworkTrainer(Optimizer optimizer, LossFunction lossFunction) {
        this.optimizer = optimizer;
        this.lossFunction = lossFunction;
        this.listeners = new ArrayList<>();
        this.maxEpochs = 100;
        this.earlyStopThreshold = 1e-6;
        this.patience = 10;
    }

    public void addListener(TrainingListener listener) {
        listeners.add(listener);
    }

    public TrainingResult train(double[][] X, double[][] y,
                               double[][] XVal, double[][] yVal,
                               Weights weights) {

        List<Double> trainLosses = new ArrayList<>();
        List<Double> valLosses = new ArrayList<>();
        int epoch = 0;
        double bestValLoss = Double.MAX_VALUE;
        int noImprovementCount = 0;

        optimizer.initialize(weights.getFlat().length);

        for (epoch = 0; epoch < maxEpochs; epoch++) {
            // Forward pass
            double[][] predictions = forward(X, weights);
            double trainLoss = lossFunction.compute(y, predictions);

            // Backward pass - compute gradients
            double[][] lossGradients = lossFunction.gradient(y, predictions);
            double[] flatGradients = computeGradients(lossGradients, X, weights);

            // Update weights
            double[] updatedWeights = optimizer.update(
                weights.getFlat(), flatGradients, epoch);
            weights.setFlat(updatedWeights);

            trainLosses.add(trainLoss);

            // Validation
            if (XVal != null && yVal != null) {
                double[][] valPredictions = forward(XVal, weights);
                double valLoss = lossFunction.compute(yVal, valPredictions);
                valLosses.add(valLoss);

                for (TrainingListener listener : listeners) {
                    listener.onEpoch(epoch, trainLoss, valLoss);
                }

                // Early stopping
                if (valLoss < bestValLoss - earlyStopThreshold) {
                    bestValLoss = valLoss;
                    noImprovementCount = 0;
                } else {
                    noImprovementCount++;
                    if (noImprovementCount >= patience) {
                        System.out.println("Early stopping at epoch " + epoch);
                        break;
                    }
                }
            } else {
                for (TrainingListener listener : listeners) {
                    listener.onEpoch(epoch, trainLoss, 0);
                }
            }
        }

        return new TrainingResult(weights, trainLosses, valLosses, epoch);
    }

    private double[][] forward(double[][] X, Weights weights) {
        // Simplified forward pass
        // In real implementation, this would be the actual network forward pass
        int n = X.length;
        int outputSize = weights.getOutputSize();
        double[][] output = new double[n][outputSize];

        // Placeholder: actual implementation would use weights
        return output;
    }

    private double[] computeGradients(double[][] lossGrad,
                                     double[][] X, Weights weights) {
        // Placeholder for actual gradient computation
        int paramCount = weights.getFlat().length;
        return new double[paramCount];
    }

    public static class TrainingResult {
        public final Weights finalWeights;
        public final List<Double> trainLosses;
        public final List<Double> valLosses;
        public final int totalEpochs;

        public TrainingResult(Weights finalWeights, List<Double> trainLosses,
                             List<Double> valLosses, int totalEpochs) {
            this.finalWeights = finalWeights;
            this.trainLosses = trainLosses;
            this.valLosses = valLosses;
            this.totalEpochs = totalEpochs;
        }
    }

    public interface Weights {
        double[] getFlat();
        void setFlat(double[] flat);
        int getOutputSize();
    }
}
```

### 7. Training Metrics

```java
package com.ml.optimize.trainer;

public class TrainingMetrics {

    private double currentEpoch;
    private double trainLoss;
    private double valLoss;
    private double trainAccuracy;
    private double valAccuracy;
    private double learningRate;
    private long epochTimeMs;

    public void update(double epoch, double trainLoss, double valLoss) {
        this.currentEpoch = epoch;
        this.trainLoss = trainLoss;
        this.valLoss = valLoss;
    }

    public void setLearningRate(double lr) {
        this.learningRate = lr;
    }

    public void setEpochTime(long ms) {
        this.epochTimeMs = ms;
    }

    public void setAccuracy(double trainAcc, double valAcc) {
        this.trainAccuracy = trainAcc;
        this.valAccuracy = valAcc;
    }

    @Override
    public String toString() {
        return String.format(
            "Epoch: %.1f | Train Loss: %.4f | Val Loss: %.4f | " +
            "Train Acc: %.2f%% | Val Acc: %.2f%% | LR: %.6f | Time: %dms",
            currentEpoch, trainLoss, valLoss,
            trainAccuracy * 100, valAccuracy * 100,
            learningRate, epochTimeMs
        );
    }
}
```

```java
package com.ml.optimize.trainer;

public interface TrainingListener {
    void onEpoch(int epoch, double trainLoss, double valLoss);
    void onTrainingComplete(TrainingMetrics finalMetrics);
    void onTrainingFailed(Exception e);
}
```

### 8. Usage Example

```java
package com.ml.optimize;

import com.ml.optimize.optimizers.AdamWOptimizer;
import com.ml.optimize.schedulers.CosineAnnealingScheduler;
import com.ml.optimize.clipper.GradientClipper;
import com.ml.optimize.trainer.NeuralNetworkTrainer;
import com.ml.optimize.trainer.TrainingListener;

public class ExampleUsage {

    public static void main(String[] args) {
        // Configure optimizer with builder pattern
        OptimizerConfig config = OptimizerConfig.builder()
            .learningRate(0.001)
            .beta1(0.9)
            .beta2(0.999)
            .epsilon(1e-8)
            .weightDecay(0.01)
            .scheduler(new CosineAnnealingScheduler(0.001, 1e-6, 1000, 100))
            .clipper(GradientClipper.byNorm(1.0))
            .build();

        // Create optimizer
        Optimizer optimizer = new AdamWOptimizer(config);

        // Create loss function
        LossFunction lossFunction = new LossFunction.MSELoss();

        // Create trainer
        NeuralNetworkTrainer trainer = new NeuralNetworkTrainer(optimizer, lossFunction);
        trainer.addListener(new TrainingListener() {
            @Override
            public void onEpoch(int epoch, double trainLoss, double valLoss) {
                if (epoch % 10 == 0) {
                    System.out.printf("Epoch %d: train=%.4f, val=%.4f%n",
                        epoch, trainLoss, valLoss);
                }
            }

            @Override
            public void onTrainingComplete(TrainingMetrics finalMetrics) {
                System.out.println("Training complete!");
            }

            @Override
            public void onTrainingFailed(Exception e) {
                System.err.println("Training failed: " + e.getMessage());
            }
        });

        // Train model
        // double[][] X = loadTrainingData();
        // double[][] y = loadTrainingLabels();
        // var result = trainer.train(X, y, null, null, weights);
    }
}
```

---

## Unit Tests

```java
package com.ml.optimize;

import com.ml.optimize.optimizers.*;
import com.ml.optimize.schedulers.*;
import com.ml.optimize.clipper.GradientClipper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OptimizerTests {

    @Test
    public void testAdamOnQuadratic() {
        // Simple quadratic function: f(x) = x^2
        // Gradient: f'(x) = 2x
        // Optimal at x = 0

        OptimizerConfig config = OptimizerConfig.builder()
            .learningRate(0.1)
            .beta1(0.9)
            .beta2(0.999)
            .build();

        AdamOptimizer adam = new AdamOptimizer(config);
        adam.initialize(1);

        double[] params = {10.0};
        double[] gradients = {20.0}; // 2 * 10

        for (int i = 0; i < 100; i++) {
            gradients[0] = 2 * params[0]; // f'(x) = 2x
            params = adam.update(params, gradients, i);
        }

        assertTrue(Math.abs(params[0]) < 0.1, "Should converge near zero");
    }

    @Test
    public void testGradientClipping() {
        GradientClipper clipper = GradientClipper.byNorm(1.0);
        double[] gradients = {1.0, 2.0, 3.0}; // norm = sqrt(14) ≈ 3.74

        double[] clipped = clipper.clip(gradients);
        double norm = Math.sqrt(clipped[0]*clipped[0] + clipped[1]*clipped[1] + clipped[2]*clipped[2]);

        assertTrue(norm <= 1.0 + 1e-6, "Norm should be clipped to 1.0");
    }

    @Test
    public void testCosineAnnealingSchedule() {
        CosineAnnealingScheduler scheduler =
            new CosineAnnealingScheduler(0.1, 0.01, 100, 10);

        // Warmup phase
        assertEquals(0.01, scheduler.getLearningRate(0), 1e-6);
        assertEquals(0.05, scheduler.getLearningRate(4), 1e-6);

        // After warmup - should be decreasing
        double lr50 = scheduler.getLearningRate(50);
        double lr90 = scheduler.getLearningRate(90);
        assertTrue(lr90 < lr50, "Learning rate should decrease over time");
    }
}
```

---

## Configuration File (config.properties)

```properties
# Optimization Settings
optimizer.name=adamw
optimizer.learningRate=0.001
optimizer.beta1=0.9
optimizer.beta2=0.999
optimizer.epsilon=1e-8
optimizer.weightDecay=0.01

# Scheduler Settings
scheduler.type=cosine_annealing
scheduler.warmupSteps=100
scheduler.totalSteps=10000
scheduler.minLr=1e-6

# Gradient Clipping
clipper.enabled=true
clipper.maxNorm=1.0
clipper.type=by_norm

# Training Settings
training.maxEpochs=100
training.batchSize=32
training.earlyStopping.patience=10
training.earlyStopping.threshold=1e-6
```

---

## Build Configuration (build.gradle)

```groovy
plugins {
    id 'java'
    id 'application'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.apache.commons:commons-math3:3.6.1'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.9.2'
}

application {
    mainClass = 'com.ml.optimize.ExampleUsage'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

---

## Key Features

1. **Multiple Optimizers**: SGD, Momentum, Nesterov, Adagrad, RMSprop, Adam, AdamW
2. **Flexible Scheduling**: Exponential, Step, Cosine Annealing, Warmup
3. **Gradient Clipping**: By norm, by value, α-clipping
4. **Regularization**: L1, L2, Elastic Net (via weight decay in AdamW)
5. **Training Features**: Early stopping, metrics tracking, listeners
6. **Production Ready**: Unit tests, configuration files, builder patterns

---

## Performance Considerations

- Use primitive arrays for performance
- Pre-allocate arrays to avoid garbage collection
- Consider usingEJML or Commons Math for matrix operations in production
- Profile with JMH for critical optimization paths