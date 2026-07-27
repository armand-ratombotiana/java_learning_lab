# Loss Functions for Deep Learning

## Problem Statement

**Problem:** Implement common loss functions used in deep learning with their gradients, supporting both forward loss computation and backward gradient calculation.

Implement a `LossFunction` interface with:
1. **Mean Squared Error (MSE)** — for regression tasks
2. **Cross-Entropy Loss** — for classification (both binary and multi-class)
3. **Hinge Loss** — for SVM-style margin-based classification
4. **Focal Loss** — for handling class imbalance

Each loss must provide:
- `compute(predictions, targets)` — compute the scalar loss value
- `gradient(predictions, targets)` — compute the gradient w.r.t. predictions
- Numerical stability handling (log-sum-exp trick, epsilon clamping)

**Example:**
```
Input:  predictions = [0.7, 0.2, 0.1]  (softmax probabilities)
        targets     = [0.0, 1.0, 0.0]  (one-hot, class 1)
Output: crossEntropy.compute(...)  → 1.6094
        crossEntropy.gradient(...) → [0.7, -0.8, 0.1]
```

**Constraints:**
- $1 \leq N \leq 10^6$ (batch size)
- $1 \leq C \leq 10^4$ (number of classes)
- Predictions for cross-entropy are raw logits (not softmax outputs)
- Must avoid floating-point overflow/underflow

---

## Step-by-Step Solution Walkthrough

### 1. The Role of Loss Functions

A loss function $\mathcal{L}(\hat{y}, y)$ quantifies the difference between predicted output $\hat{y}$ and true target $y$. During training, we minimize:

$$\theta^* = \arg\min_\theta \frac{1}{N} \sum_{i=1}^{N} \mathcal{L}(f_\theta(x^{(i)}), y^{(i)})$$

Key properties:
- **Differentiability:** Required for gradient-based optimization.
- **Convexity:** Simplifies optimization (not strictly required for deep learning).
- **Numerical stability:** Must handle edge cases gracefully.

### 2. Loss Functions in Detail

#### Mean Squared Error (MSE)

$$\mathcal{L}_{\text{MSE}}(\hat{y}, y) = \frac{1}{N} \sum_{i=1}^{N} (\hat{y}_i - y_i)^2$$

**Gradient:** $\frac{\partial \mathcal{L}}{\partial \hat{y}_i} = \frac{2}{N}(\hat{y}_i - y_i)$

**Use cases:** Regression, autoencoders.

**Properties:**
- Convex in $\hat{y}$.
- Penalizes large errors quadratically (sensitive to outliers).
- Unit-sensitive: scales with output magnitude.

#### Cross-Entropy Loss

**Binary classification (BCE):**
$$\mathcal{L}_{\text{BCE}}(\hat{y}, y) = -\frac{1}{N} \sum_{i} [y_i \log(\hat{y}_i) + (1 - y_i) \log(1 - \hat{y}_i)]$$

**Multi-class (categorical cross-entropy with softmax):**
$$\mathcal{L}_{\text{CE}}(\hat{z}, y) = -\sum_{j} y_j \log\left(\frac{e^{z_j}}{\sum_k e^{z_k}}\right) = -\sum_j y_j \left(z_j - \log\sum_k e^{z_k}\right)$$

**Gradient (with logits $z$):** $\frac{\partial \mathcal{L}}{\partial z_j} = \hat{y}_j - y_j$ where $\hat{y}_j = \text{softmax}(z_j)$

**Use cases:** Classification.

**Properties:**
- Convex in logits.
- Well-calibrated probabilities.
- Log-sum-exp trick needed for numerical stability.

#### Hinge Loss

$$\mathcal{L}_{\text{hinge}}(\hat{y}, y) = \frac{1}{N} \sum_{i} \max(0, 1 - y_i \cdot \hat{y}_i)$$

where $y_i \in \{-1, +1\}$ and $\hat{y}_i$ is the raw score.

**Gradient (subgradient):** $\frac{\partial \mathcal{L}}{\partial \hat{y}_i} = \begin{cases} -y_i & \text{if } y_i \cdot \hat{y}_i < 1 \\ 0 & \text{otherwise} \end{cases}$

**Use cases:** SVM, maximum-margin classification.

**Properties:**
- Non-differentiable at $y_i \cdot \hat{y}_i = 1$ (subgradient).
- Only penalizes misclassified or margin-violating samples.
- Encourages margin maximization.

#### Focal Loss

$$\mathcal{L}_{\text{focal}}(\hat{y}_t) = -\alpha_t (1 - \hat{y}_t)^\gamma \log(\hat{y}_t)$$

where:
- $\hat{y}_t$ = predicted probability for the true class
- $\alpha_t$ = class balancing weight (handles class imbalance)
- $\gamma \geq 0$ = focusing parameter (reduces loss for well-classified examples)

**Gradient:**
$$\frac{\partial \mathcal{L}_{\text{focal}}}{\partial \hat{y}_t} = \alpha_t \left[ \gamma (1 - \hat{y}_t)^{\gamma-1} \log(\hat{y}_t) - \frac{(1 - \hat{y}_t)^\gamma}{\hat{y}_t} \right]$$

**Use cases:** Object detection (RetinaNet), imbalanced classification.

**Properties:**
- When $\gamma = 0$, equivalent to cross-entropy.
- Higher $\gamma$ focuses more on hard, misclassified examples.
- $\alpha_t$ balances positive/negative class frequency.

### 3. Numerical Stability

#### Log-Sum-Exp Trick

For softmax cross-entropy with logits $z$:
$$\log\sum_k e^{z_k} = m + \log\sum_k e^{z_k - m}$$
where $m = \max_k z_k$. This prevents overflow when $z_k$ is large.

#### Epsilon Clamping

To avoid $\log(0)$ errors, clamp probabilities:
$$\hat{p} = \text{clamp}(\hat{y}, \epsilon, 1 - \epsilon)$$
where $\epsilon = 10^{-7}$ or $10^{-15}$.

---

## Java Implementation

```java
package com.deeplearning.loss;

/**
 * Interface for loss functions used in neural network training.
 * Each implementation provides forward loss computation and
 * backward gradient computation.
 */
public interface LossFunction {

    /**
     * Computes the scalar loss value for a batch of predictions.
     *
     * @param predictions model output values (can be logits or probabilities)
     * @param targets     ground truth values
     * @return scalar loss averaged over the batch
     */
    double compute(double[] predictions, double[] targets);

    /**
     * Computes the same loss for a batch of predictions (2D).
     *
     * @param predictions model outputs shape [N, C]
     * @param targets     ground truth shape [N, C] (one-hot) or [N] (class indices)
     * @return scalar loss averaged over all samples
     */
    double compute(double[][] predictions, double[][] targets);

    /**
     * Computes the gradient of the loss w.r.t. predictions for a single sample.
     *
     * @param predictions model output values
     * @param targets     ground truth values
     * @param gradient    output array to fill with gradient values
     */
    void gradient(double[] predictions, double[] targets, double[] gradient);

    /**
     * Computes the gradient for a batch.
     *
     * @param predictions model outputs shape [N, C]
     * @param targets     ground truth shape [N, C]
     * @param gradient    output shape [N, C]
     */
    void gradient(double[][] predictions, double[][] targets, double[][] gradient);

    /**
     * Returns the name of this loss function.
     */
    String getName();

    // ---------------------------------------------------------------
    // Implementation: Mean Squared Error
    // ---------------------------------------------------------------

    /**
     * Mean Squared Error loss for regression.
     * L = (1/N) * Σ(ŷ - y)²
     * Gradient: ∂L/∂ŷ = (2/N) * (ŷ - y)
     */
    final class MeanSquaredError implements LossFunction {

        @Override
        public double compute(double[] predictions, double[] targets) {
            double sum = 0.0;
            for (int i = 0; i < predictions.length; i++) {
                double diff = predictions[i] - targets[i];
                sum += diff * diff;
            }
            return sum / predictions.length;
        }

        @Override
        public double compute(double[][] predictions, double[][] targets) {
            double total = 0.0;
            int count = 0;
            for (int i = 0; i < predictions.length; i++) {
                for (int j = 0; j < predictions[i].length; j++) {
                    double diff = predictions[i][j] - targets[i][j];
                    total += diff * diff;
                    count++;
                }
            }
            return total / count;
        }

        @Override
        public void gradient(double[] predictions, double[] targets, double[] gradient) {
            double scale = 2.0 / predictions.length;
            for (int i = 0; i < predictions.length; i++) {
                gradient[i] = scale * (predictions[i] - targets[i]);
            }
        }

        @Override
        public void gradient(double[][] predictions, double[][] targets, double[][] gradient) {
            int count = 0;
            for (double[] predRow : predictions) {
                count += predRow.length;
            }
            double scale = 2.0 / count;
            for (int i = 0; i < predictions.length; i++) {
                for (int j = 0; j < predictions[i].length; j++) {
                    gradient[i][j] = scale * (predictions[i][j] - targets[i][j]);
                }
            }
        }

        @Override
        public String getName() {
            return "MeanSquaredError";
        }
    }

    // ---------------------------------------------------------------
    // Implementation: Categorical Cross-Entropy (with logits)
    // ---------------------------------------------------------------

    /**
     * Categorical Cross-Entropy loss (combines softmax + cross-entropy).
     * Accepts raw logits and applies softmax internally.
     * Uses log-sum-exp trick for numerical stability.
     * 
     * Gradient: ∂L/∂z = softmax(z) - y
     */
    final class CrossEntropyLoss implements LossFunction {

        private static final double EPS = 1e-15;

        @Override
        public double compute(double[] logits, double[] targets) {
            double max = logits[0];
            for (double v : logits) if (v > max) max = v;

            double sumExp = 0.0;
            for (double v : logits) {
                sumExp += Math.exp(v - max);
            }
            double logSumExp = max + Math.log(sumExp);

            double loss = 0.0;
            for (int i = 0; i < logits.length; i++) {
                if (targets[i] > 0.5) {
                    loss += -targets[i] * (logits[i] - logSumExp);
                }
            }
            return loss;
        }

        @Override
        public double compute(double[][] logits, double[][] targets) {
            double total = 0.0;
            for (int i = 0; i < logits.length; i++) {
                total += compute(logits[i], targets[i]);
            }
            return total / logits.length;
        }

        @Override
        public void gradient(double[] logits, double[] targets, double[] gradient) {
            double max = logits[0];
            for (double v : logits) if (v > max) max = v;

            double sumExp = 0.0;
            for (double v : logits) {
                sumExp += Math.exp(v - max);
            }

            for (int i = 0; i < logits.length; i++) {
                double softmax = Math.exp(logits[i] - max) / sumExp;
                gradient[i] = softmax - targets[i];
            }
        }

        @Override
        public void gradient(double[][] logits, double[][] targets, double[][] gradient) {
            for (int i = 0; i < logits.length; i++) {
                gradient(logits[i], targets[i], gradient[i]);
            }
        }

        @Override
        public String getName() {
            return "CrossEntropy";
        }
    }

    // ---------------------------------------------------------------
    // Implementation: Binary Cross-Entropy
    // ---------------------------------------------------------------

    /**
     * Binary Cross-Entropy loss for binary classification.
     * Accepts logits (pre-sigmoid) and applies sigmoid internally.
     * L = -[y·log(σ(z)) + (1-y)·log(1-σ(z))]
     */
    final class BinaryCrossEntropy implements LossFunction {

        private static final double EPS = 1e-15;

        private double sigmoid(double x) {
            return x >= 0 ? 1.0 / (1.0 + Math.exp(-x))
                          : Math.exp(x) / (1.0 + Math.exp(x));
        }

        @Override
        public double compute(double[] logits, double[] targets) {
            double sum = 0.0;
            for (int i = 0; i < logits.length; i++) {
                double p = sigmoid(logits[i]);
                p = Math.max(EPS, Math.min(1.0 - EPS, p));
                sum += -targets[i] * Math.log(p) - (1.0 - targets[i]) * Math.log(1.0 - p);
            }
            return sum / logits.length;
        }

        @Override
        public double compute(double[][] logits, double[][] targets) {
            double total = 0.0;
            int count = 0;
            for (int i = 0; i < logits.length; i++) {
                for (int j = 0; j < logits[i].length; j++) {
                    double p = sigmoid(logits[i][j]);
                    p = Math.max(EPS, Math.min(1.0 - EPS, p));
                    total += -targets[i][j] * Math.log(p)
                             - (1.0 - targets[i][j]) * Math.log(1.0 - p);
                    count++;
                }
            }
            return total / count;
        }

        @Override
        public void gradient(double[] logits, double[] targets, double[] gradient) {
            for (int i = 0; i < logits.length; i++) {
                double p = sigmoid(logits[i]);
                gradient[i] = p - targets[i];
            }
        }

        @Override
        public void gradient(double[][] logits, double[][] targets, double[][] gradient) {
            for (int i = 0; i < logits.length; i++) {
                gradient(logits[i], targets[i], gradient[i]);
            }
        }

        @Override
        public String getName() {
            return "BinaryCrossEntropy";
        }
    }

    // ---------------------------------------------------------------
    // Implementation: Hinge Loss
    // ---------------------------------------------------------------

    /**
     * Hinge loss for maximum-margin classification (SVM).
     * L = max(0, 1 - y·ŷ) where y ∈ {-1, +1}
     * Subgradient: -y if y·ŷ < 1, else 0
     */
    final class HingeLoss implements LossFunction {

        @Override
        public double compute(double[] predictions, double[] targets) {
            double sum = 0.0;
            for (int i = 0; i < predictions.length; i++) {
                double margin = 1.0 - targets[i] * predictions[i];
                sum += Math.max(0.0, margin);
            }
            return sum / predictions.length;
        }

        @Override
        public double compute(double[][] predictions, double[][] targets) {
            double total = 0.0;
            int count = 0;
            for (int i = 0; i < predictions.length; i++) {
                for (int j = 0; j < predictions[i].length; j++) {
                    double margin = 1.0 - targets[i][j] * predictions[i][j];
                    total += Math.max(0.0, margin);
                    count++;
                }
            }
            return total / count;
        }

        @Override
        public void gradient(double[] predictions, double[] targets, double[] gradient) {
            for (int i = 0; i < predictions.length; i++) {
                if (targets[i] * predictions[i] < 1.0) {
                    gradient[i] = -targets[i];
                } else {
                    gradient[i] = 0.0;
                }
            }
        }

        @Override
        public void gradient(double[][] predictions, double[][] targets, double[][] gradient) {
            for (int i = 0; i < predictions.length; i++) {
                gradient(predictions[i], targets[i], gradient[i]);
            }
        }

        @Override
        public String getName() {
            return "HingeLoss";
        }
    }

    // ---------------------------------------------------------------
    // Implementation: Focal Loss
    // ---------------------------------------------------------------

    /**
     * Focal Loss for imbalanced classification (Lin et al., 2017).
     * L = -α_t · (1 - p_t)^γ · log(p_t)
     * where p_t = predicted probability of true class.
     * 
     * γ (focusing parameter) reduces loss for well-classified examples.
     * α (balancing weight) handles class frequency imbalance.
     */
    final class FocalLoss implements LossFunction {

        private static final double EPS = 1e-15;
        private final double gamma;
        private final double alpha;

        /**
         * @param gamma focusing parameter (≥ 0). Higher values focus more on hard examples.
         * @param alpha class balancing weight (0 < α ≤ 1). Applied to positive class.
         */
        public FocalLoss(double gamma, double alpha) {
            this.gamma = gamma;
            this.alpha = alpha;
        }

        public FocalLoss() {
            this(2.0, 0.25);
        }

        /**
         * Computes focal loss for a single sample with softmax probabilities.
         * Targets should be one-hot encoded.
         */
        @Override
        public double compute(double[] probabilities, double[] targets) {
            double loss = 0.0;
            for (int i = 0; i < probabilities.length; i++) {
                if (targets[i] > 0.5) {
                    double pt = Math.max(EPS, Math.min(1.0 - EPS, probabilities[i]));
                    double at = alpha; // α for positive class
                    loss += -at * Math.pow(1.0 - pt, gamma) * Math.log(pt);
                }
            }
            return loss;
        }

        @Override
        public double compute(double[][] probabilities, double[][] targets) {
            double total = 0.0;
            for (int i = 0; i < probabilities.length; i++) {
                total += compute(probabilities[i], targets[i]);
            }
            return total / probabilities.length;
        }

        /**
         * Gradient for focal loss w.r.t. softmax probabilities.
         */
        @Override
        public void gradient(double[] probabilities, double[] targets, double[] gradient) {
            for (int i = 0; i < probabilities.length; i++) {
                if (targets[i] > 0.5) {
                    double pt = Math.max(EPS, Math.min(1.0 - EPS, probabilities[i]));
                    double term1 = gamma * Math.pow(1.0 - pt, gamma - 1) * Math.log(pt);
                    double term2 = -Math.pow(1.0 - pt, gamma) / pt;
                    gradient[i] = alpha * (term1 + term2);
                } else {
                    gradient[i] = 0.0;
                }
            }
        }

        @Override
        public void gradient(double[][] probabilities, double[][] targets, double[][] gradient) {
            for (int i = 0; i < probabilities.length; i++) {
                gradient(probabilities[i], targets[i], gradient[i]);
            }
        }

        @Override
        public String getName() {
            return "FocalLoss(γ=" + gamma + ", α=" + alpha + ")";
        }
    }
}
```

### Test Harness

```java
package com.deeplearning.loss;

import java.util.Arrays;

/**
 * Test harness for all loss function implementations.
 */
public class LossFunctionTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testMSE();
        testCrossEntropy();
        testBinaryCrossEntropy();
        testHingeLoss();
        testFocalLoss();
        testNumericalStability();
        testGradientCheck();
        testEdgeCases();
        System.out.printf("%n=== Results: %d passed, %d failed ===%n", passed, failed);
    }

    static void assertTest(boolean condition, String name) {
        if (condition) { passed++; System.out.printf("[PASS] %s%n", name); }
        else { failed++; System.err.printf("[FAIL] %s%n", name); }
    }

    static void testMSE() {
        LossFunction mse = new LossFunction.MeanSquaredError();
        double[] pred = {1.0, 2.0, 3.0};
        double[] target = {0.0, 2.0, 5.0};
        double loss = mse.compute(pred, target);
        // (1² + 0² + 2²) / 3 = 5/3 ≈ 1.6667
        assertTest(Math.abs(loss - 1.6666666667) < 1e-9, "MSE exact value");

        double[] grad = new double[3];
        mse.gradient(pred, target, grad);
        assertTest(Math.abs(grad[0] - (2.0/3)) < 1e-9, "MSE gradient[0] = 2/3");
        assertTest(Math.abs(grad[1]) < 1e-9, "MSE gradient[1] = 0");
        assertTest(Math.abs(grad[2] - (-4.0/3)) < 1e-9, "MSE gradient[2] = -4/3");
    }

    static void testCrossEntropy() {
        LossFunction ce = new LossFunction.CrossEntropyLoss();
        // logits = [2.0, 1.0, 0.1], target = class 0 ([1, 0, 0])
        double[] logits = {2.0, 1.0, 0.1};
        double[] target = {1.0, 0.0, 0.0};
        double loss = ce.compute(logits, target);

        // softmax: e²/(e²+e¹+e^0.1) = 7.389/(7.389+2.718+1.105) = 7.389/11.212 = 0.659
        // loss = -log(0.659) = 0.417
        assertTest(loss > 0.0 && loss < 1.0, "Cross-entropy range check");

        double[] grad = new double[3];
        ce.gradient(logits, target, grad);
        // gradient = softmax - target
        double sumExp = Math.exp(2.0) + Math.exp(1.0) + Math.exp(0.1);
        double s0 = Math.exp(2.0) / sumExp;
        assertTest(Math.abs(grad[0] - (s0 - 1.0)) < 1e-9, "CE gradient class 0");
        assertTest(grad[1] > 0, "CE gradient class 1 positive");

        // Batch test
        double[][] batchLogits = {{2.0, 1.0, 0.1}, {0.1, 2.0, 1.0}};
        double[][] batchTarget = {{1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}};
        double batchLoss = ce.compute(batchLogits, batchTarget);
        assertTest(batchLoss > 0, "Cross-entropy batch loss positive");
    }

    static void testBinaryCrossEntropy() {
        LossFunction bce = new LossFunction.BinaryCrossEntropy();
        // logits = [2.0], target = [1.0]
        double[] logits = {2.0};
        double[] target = {1.0};
        double loss = bce.compute(logits, target);
        // sigmoid(2) = 0.8808, loss = -log(0.8808) = 0.1269
        assertTest(loss > 0.0, "BCE loss positive");

        double[] grad = new double[1];
        bce.gradient(logits, target, grad);
        // gradient = sigmoid(2) - 1 = -0.1192
        assertTest(Math.abs(grad[0] + 0.119202922) < 1e-8, "BCE gradient");
    }

    static void testHingeLoss() {
        LossFunction hinge = new LossFunction.HingeLoss();
        double[] pred = {0.5, -0.2, 1.5};
        double[] target = {1.0, -1.0, 1.0};
        double loss = hinge.compute(pred, target);
        // max(0, 1-0.5) + max(0, 1-0.2) + max(0, 1-1.5) = 0.5 + 0.8 + 0 = 1.3
        // average = 1.3/3 = 0.4333
        assertTest(Math.abs(loss - 0.4333333333) < 1e-9, "Hinge loss exact value");

        double[] grad = new double[3];
        hinge.gradient(pred, target, grad);
        assertTest(grad[0] == -1.0, "Hinge gradient[0] = -y (margin violated)");
        assertTest(grad[1] == 1.0, "Hinge gradient[1] = -(-1) = 1 (margin violated)");
        assertTest(grad[2] == 0.0, "Hinge gradient[2] = 0 (margin satisfied)");
    }

    static void testFocalLoss() {
        LossFunction focal = new LossFunction.FocalLoss(2.0, 0.25);
        double[] prob = {0.9, 0.05, 0.05};
        double[] target = {1.0, 0.0, 0.0};
        double loss = focal.compute(prob, target);
        // pt = 0.9, α = 0.25, focal = -0.25 * (1-0.9)² * log(0.9)
        // = -0.25 * 0.01 * (-0.10536) = 0.000263
        assertTest(loss > 0.0 && loss < 0.01, "Focal loss small for confident prediction");

        double[] probHard = {0.4, 0.3, 0.3};
        double lossHard = focal.compute(probHard, target);
        assertTest(lossHard > loss, "Focal loss larger for uncertain prediction");
    }

    static void testNumericalStability() {
        LossFunction ce = new LossFunction.CrossEntropyLoss();
        // Very large logits
        double[] largeLogits = {1000.0, 0.0, 0.0};
        double[] target = {1.0, 0.0, 0.0};
        double loss = ce.compute(largeLogits, target);
        assertTest(!Double.isNaN(loss) && !Double.isInfinite(loss), "CE stable with large logits");
        assertTest(loss < 1e-9, "CE near zero for confident correct prediction");

        // Very negative logits
        double[] negLogits = {-1000.0, -1000.0, -1000.0};
        double[] uniformTarget = {0.33, 0.33, 0.34};
        double lossNeg = ce.compute(negLogits, uniformTarget);
        assertTest(!Double.isNaN(lossNeg) && !Double.isInfinite(lossNeg), "CE stable with negative logits");
    }

    static void testGradientCheck() {
        LossFunction[] losses = {
            new LossFunction.MeanSquaredError(),
            new LossFunction.CrossEntropyLoss(),
            new LossFunction.BinaryCrossEntropy(),
            new LossFunction.HingeLoss()
        };
        double eps = 1e-8;
        double[][] testCases = {
            {0.5, 0.2, 0.3},
            {1.0, -1.0, 0.0},
            {0.1, 0.9, 0.0}
        };
        double[][] targets = {
            {1.0, 0.0, 0.0},
            {-1.0, 1.0, -1.0},
            {0.0, 1.0, 0.0}
        };
        for (int l = 0; l < losses.length; l++) {
            boolean allOk = true;
            for (int t = 0; t < testCases.length; t++) {
                double[] grad = new double[3];
                losses[l].gradient(testCases[t], targets[t], grad);
                for (int i = 0; i < 3; i++) {
                    double[] predPlus = testCases[t].clone();
                    double[] predMinus = testCases[t].clone();
                    predPlus[i] += eps;
                    predMinus[i] -= eps;
                    double lossPlus = losses[l].compute(predPlus, targets[t]);
                    double lossMinus = losses[l].compute(predMinus, targets[t]);
                    double numericalGrad = (lossPlus - lossMinus) / (2 * eps);
                    if (Math.abs(grad[i] - numericalGrad) > 1e-6) {
                        allOk = false;
                    }
                }
            }
            assertTest(allOk, losses[l].getName() + " gradient check");
        }
    }

    static void testEdgeCases() {
        LossFunction mse = new LossFunction.MeanSquaredError();
        // Single element
        double[] single = {1.0};
        double[] singleT = {2.0};
        assertTest(Math.abs(mse.compute(single, singleT) - 1.0) < 1e-9, "MSE single element");
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Forward (single sample, C classes):** $O(C)$ for all loss functions.

**Backward (gradient, single sample):** $O(C)$.

**Batch (N samples, C classes):** $O(N \cdot C)$.

| Loss Function | Forward Operations | Key Bottleneck |
|--------------|-------------------|----------------|
| MSE | 1 subtract, 1 multiply per element | None |
| Cross-Entropy | softmax (C exp, log-sum-exp) | Exp over C |
| Binary Cross-Entropy | sigmoid (1 exp) | Exp |
| Hinge | 1 multiply, 1 max | None |
| Focal | log, pow, exp | Pow operation |

### Space Complexity

$O(1)$ for single-sample computation. $O(N \cdot C)$ if storing intermediate gradients.

---

## Follow-Up Questions

### Q1: How would you modify cross-entropy loss for multi-label classification?

**Answer:** For multi-label classification (each sample can belong to multiple classes), use **binary cross-entropy** independently for each class:

$$\mathcal{L} = -\frac{1}{C} \sum_{j=1}^{C} [y_j \log(\sigma(z_j)) + (1 - y_j) \log(1 - \sigma(z_j))]$$

This treats each output as an independent binary classification problem. The output layer uses sigmoid (not softmax) to produce class probabilities.

### Q2: Explain the log-sum-exp trick in detail.

**Answer:** The naive computation of $\log\sum_{k=1}^{K} e^{z_k}$ overflows when any $z_k$ is large (e.g., > 709 for double precision). The trick factors out the maximum:

$$\log\sum_k e^{z_k} = \log\left(e^m \sum_k e^{z_k - m}\right) = m + \log\sum_k e^{z_k - m}$$

where $m = \max_k z_k$. Since $z_k - m \leq 0$, all $e^{z_k - m} \in (0, 1]$, and the sum is at most $K$ — well within representable range.

**Example:** $\log(e^{1000} + e^{999}) = 1000 + \log(1 + e^{-1}) \approx 1000.313$, avoiding the overflow of $e^{1000}$.

### Q3: Why is hinge loss considered a "maximum margin" loss?

**Answer:** Hinge loss penalizes any prediction where $y \cdot \hat{y} < 1$, even if the prediction is correct but not confident enough. This encourages the model to produce scores with a **margin** of at least 1. The SVM objective:

$$\min_{\mathbf{w}} \frac{1}{2}\|\mathbf{w}\|^2 + C\sum_i \max(0, 1 - y_i(\mathbf{w} \cdot \mathbf{x}_i + b))$$

optimizes both margin maximization ($\|\mathbf{w}\|^2$) and classification error. The margin is $2/\|\mathbf{w}\|$ in the original SVM formulation.

### Q4: Derive the focal loss gradient.

**Answer:** Let $p_t$ be the predicted probability for the true class. Focal loss:

$$\mathcal{L}_{\text{FL}} = -\alpha (1 - p_t)^\gamma \log(p_t)$$

Using the product rule and chain rule:

$$\frac{\partial \mathcal{L}_{\text{FL}}}{\partial p_t} = -\alpha \left[ \gamma(1-p_t)^{\gamma-1}(-1) \log(p_t) + (1-p_t)^\gamma \cdot \frac{1}{p_t} \right]$$

$$= -\alpha \left[ -\gamma(1-p_t)^{\gamma-1} \log(p_t) + \frac{(1-p_t)^\gamma}{p_t} \right]$$

$$= \alpha \left[ \gamma(1-p_t)^{\gamma-1} \log(p_t) - \frac{(1-p_t)^\gamma}{p_t} \right]$$

For $\gamma = 0$, this reduces to $-\alpha / p_t$, which (combined with softmax gradient) gives back the standard cross-entropy gradient.

### Q5: How do you handle loss functions for sequences (e.g., CTC loss)?

**Answer:** Connectionist Temporal Classification (CTC) loss handles sequence-to-sequence problems where input and output lengths differ (e.g., speech recognition). Key ideas:
1. The model outputs a per-timestep probability distribution over the alphabet + blank token.
2. CTC computes the probability of all possible alignments that map to the target sequence.
3. Uses dynamic programming (forward-backward algorithm) for efficient computation.

The loss is $\mathcal{L} = -\log P(Y | X)$, where $P(Y | X)$ sums over all valid alignments.

### Q6: What loss function would you use for ordinal regression?

**Answer:** For ordinal regression (labels have ordered categories, e.g., movie ratings 1-5):
1. **Ordinal logistic regression:** Models cumulative probabilities: $P(y \leq k | x) = \sigma(\theta_k - f(x))$.
2. **Corners loss:** Treats adjacent labels as partially correct.
3. **Mean Absolute Error (MAE):** For integer ordinal outputs, MAE can work with proper rounding.

The key insight is that ordinal losses should penalize predictions proportionally to their distance from the true label.

### Q7: Compare the sensitivity of MSE vs. MAE to outliers.

**Answer:**

| Property | MSE | MAE (Mean Absolute Error) |
|---------|-----|--------------------------|
| Formula | $(y - \hat{y})^2$ | $\|y - \hat{y}\|$ |
| Gradient magnitude | $2\|y - \hat{y}\|$ (grows with error) | $\text{sign}(y - \hat{y})$ (bounded) |
| Outlier sensitivity | High (quadratic) | Low (linear) |
| Optimal for | Gaussian noise | Laplacian noise |
| Unique minimum | Yes (strictly convex) | No (non-strict) |

For regression with outliers, MAE (or Huber loss, which combines both) is preferred.

### Q8: How would you implement Huber loss?

**Answer:** Huber loss combines MSE and MAE:

```java
final class HuberLoss implements LossFunction {
    private final double delta;

    public HuberLoss(double delta) {
        this.delta = delta;
    }

    @Override
    public double compute(double[] predictions, double[] targets) {
        double sum = 0.0;
        for (int i = 0; i < predictions.length; i++) {
            double diff = Math.abs(predictions[i] - targets[i]);
            if (diff <= delta) {
                sum += 0.5 * diff * diff;
            } else {
                sum += delta * diff - 0.5 * delta * delta;
            }
        }
        return sum / predictions.length;
    }

    @Override
    public void gradient(double[] predictions, double[] targets, double[] gradient) {
        for (int i = 0; i < predictions.length; i++) {
            double diff = predictions[i] - targets[i];
            double absDiff = Math.abs(diff);
            if (absDiff <= delta) {
                gradient[i] = diff / predictions.length;
            } else {
                gradient[i] = delta * Math.signum(diff) / predictions.length;
            }
        }
    }
}
```

---

## Test Cases

| Test Case | Loss | Input | Expected |
|-----------|------|-------|----------|
| TC-01 | MSE | pred=[1,2,3], target=[0,2,5] | loss=1.6667 |
| TC-02 | MSE gradient | pred=[1,2,3], target=[0,2,5] | grad=[0.666, 0, -1.333] |
| TC-03 | Cross-Entropy | logits=[2,1,0.1], target=class 0 | loss > 0 |
| TC-04 | CE gradient | logits=[2,1,0.1], target=class 0 | grad[0] = softmax[0] - 1 |
| TC-05 | CE batch | 2 samples, 3 classes | Positive loss |
| TC-06 | BCE | logit=2, target=1 | loss ≈ 0.127 |
| TC-07 | BCE gradient | logit=2, target=1 | grad ≈ -0.119 |
| TC-08 | Hinge | pred=[0.5,-0.2,1.5], target=[1,-1,1] | loss=0.4333 |
| TC-09 | Hinge gradient | margin violated | grad = -target |
| TC-10 | Focal | p=0.9, γ=2, α=0.25 | loss ≈ 0.00026 |
| TC-11 | Focal | p=0.4, γ=2, α=0.25 | loss > TC-10 |
| TC-12 | Numerical stability | Large logits (1000) | No overflow |
| TC-13 | Numerical stability | All negative logits | No underflow |
| TC-14 | Gradient check (MSE) | Random values | Analytical ≈ Numerical |
| TC-15 | Gradient check (CE) | Random logits | Analytical ≈ Numerical |

---

## Key Takeaways

- **MSE** for regression, **cross-entropy** for classification, **hinge** for margin-based methods, **focal** for imbalance.
- Cross-entropy with softmax gives the clean gradient $\hat{y} - y$ — a fundamental result.
- Numerical stability (log-sum-exp, epsilon clamping) is essential for robust implementations.
- Focal loss down-weights easy examples to focus on hard, misclassified examples.
- The choice of loss function significantly impacts training dynamics and model behavior.
