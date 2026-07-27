# Deep Learning Optimizers (Adam, RMSprop)

## Problem Statement

**Problem:** Implement the Adam optimizer with bias correction and optional decoupled weight decay (AdamW), along with learning rate scheduling.

Design and implement:
1. **Adam optimizer** — Adaptive Moment Estimation with bias correction
2. **RMSprop** — Root Mean Square Propagation (maintains per-parameter learning rates)
3. **AdamW** — Adam with decoupled weight decay

Each optimizer must:
- Maintain first and second moment estimates ($m_t$, $v_t$).
- Perform bias correction for initialization bias.
- Support configurable $\beta_1$, $\beta_2$, $\epsilon$, and learning rate.
- Support optional L2 regularization / weight decay.
- Be efficiently vectorizable (per-parameter operations).

**Example:**
```
Parameter θ = [1.0, -0.5, 0.3]
Gradient  g = [0.1, -0.2, 0.05]
Adam update at step t=1, β₁=0.9, β₂=0.999, η=0.001, ε=1e-8:
  m₁ = 0.9·0 + 0.1·g = [0.01, -0.02, 0.005]
  v₁ = 0.999·0 + 0.001·g² = [1e-5, 4e-5, 2.5e-6]
  m̂₁ = m₁ / (1 - 0.9¹) = [0.1, -0.2, 0.05]
  v̂₁ = v₁ / (1 - 0.999¹) = [0.01, 0.04, 0.0025]
  θ₁ = θ₀ - 0.001 · m̂₁ / (√v̂₁ + 1e-8)
```

**Constraints:**
- $0 < \beta_1 < 1$, $0 < \beta_2 < 1$
- $\eta > 0$, $\epsilon > 0$ (typically $\epsilon = 10^{-8}$)
- Must handle very large (10⁶+) parameter counts efficiently.

---

## Step-by-Step Solution Walkthrough

### 1. The Optimization Problem

Given a loss function $\mathcal{L}(\theta)$ with parameters $\theta$, we seek:

$$\theta^* = \arg\min_\theta \mathcal{L}(\theta)$$

Stochastic gradient descent (SGD) with learning rate $\eta$:
$$\theta_{t+1} = \theta_t - \eta \cdot g_t \quad \text{where} \quad g_t = \nabla_\theta \mathcal{L}(\theta_t)$$

**Limitations of SGD:**
- Fixed learning rate for all parameters.
- Sensitive to learning rate tuning.
- Slow convergence in ravines and saddle points.
- No adaptation to gradient history.

### 2. Momentum

Momentum accumulates past gradients to smooth updates:
$$m_t = \beta_1 \cdot m_{t-1} + (1 - \beta_1) \cdot g_t$$
$$\theta_{t+1} = \theta_t - \eta \cdot m_t$$

This accelerates convergence in consistent directions and dampens oscillations.

### 3. RMSprop

RMSprop (Hinton, 2012) adapts the learning rate per-parameter using the root mean square of past gradients:

$$v_t = \beta_2 \cdot v_{t-1} + (1 - \beta_2) \cdot g_t^2$$
$$\theta_{t+1} = \theta_t - \frac{\eta}{\sqrt{v_t + \epsilon}} \cdot g_t$$

**Intuition:** Parameters with large gradients get smaller learning rates; parameters with small gradients get larger learning rates.

### 4. Adam (Kingma & Ba, 2015)

Adam combines momentum with RMSprop's adaptive learning rates:

$$m_t = \beta_1 \cdot m_{t-1} + (1 - \beta_1) \cdot g_t \quad \text{(biased first moment estimate)}$$
$$v_t = \beta_2 \cdot v_{t-1} + (1 - \beta_2) \cdot g_t^2 \quad \text{(biased second moment estimate)}$$

**Bias correction** (crucial for early timesteps):
$$\hat{m}_t = \frac{m_t}{1 - \beta_1^t} \quad \text{and} \quad \hat{v}_t = \frac{v_t}{1 - \beta_2^t}$$

**Update:**
$$\theta_{t+1} = \theta_t - \frac{\eta}{\sqrt{\hat{v}_t} + \epsilon} \cdot \hat{m}_t$$

**Why bias correction?** At $t=1$, $m_1 = (1 - \beta_1)g_1$ ≈ $0.1g_1$ (for $\beta_1 = 0.9$). Without correction, the first update would be too small. Dividing by $1 - \beta_1^1 = 0.1$ corrects this.

### 5. AdamW (Loshchilov & Hutter, 2019)

AdamW decouples weight decay from the adaptive gradient updates. Standard Adam with L2 regularization:

$$\theta_{t+1} = \theta_t - \eta\left(\frac{\hat{m}_t}{\sqrt{\hat{v}_t} + \epsilon} + \lambda\theta_t\right)$$

The issue: The weight decay $\lambda\theta_t$ is scaled by $\eta/\sqrt{\hat{v}_t}$, making it parameter-dependent.

**AdamW fixes this:**
$$\theta_{t+1} = \theta_t - \eta\left(\frac{\hat{m}_t}{\sqrt{\hat{v}_t} + \epsilon}\right) - \eta\lambda\theta_t$$

Now weight decay is uniformly applied, decoupled from the adaptive rates.

### 6. Learning Rate Schedules

Common schedules:
- **Step decay:** $\eta_t = \eta_0 \cdot \gamma^{\lfloor t / s \rfloor}$
- **Exponential decay:** $\eta_t = \eta_0 \cdot e^{-kt}$
- **Cosine annealing:** $\eta_t = \eta_{\min} + \frac{1}{2}(\eta_{\max} - \eta_{\min})(1 + \cos(\frac{t}{T}\pi))$
- **Linear warmup:** $\eta_t = \eta_0 \cdot \min(1, t/T_{\text{warmup}})$
- **Inverse sqrt:** $\eta_t = \eta_0 / \sqrt{\max(1, t)}$

---

## Java Implementation

```java
package com.deeplearning.optimizer;

import java.util.Arrays;

/**
 * Adam optimizer with bias correction, RMSprop, and AdamW variants.
 * Maintains per-parameter first and second moment estimates.
 */
public class AdamOptimizer {

    private final double learningRate;
    private final double beta1;
    private final double beta2;
    private final double epsilon;
    private final double weightDecay;

    // Per-parameter states
    private double[] m; // first moment
    private double[] v; // second moment
    private int t;      // timestep
    private int paramCount;

    // Learning rate schedule state
    private int warmupSteps;
    private int currentStep;
    private double lrScale;

    /**
     * Creates an Adam optimizer with the given hyperparameters.
     *
     * @param paramCount  total number of parameters to optimize
     * @param learningRate step size (typical: 0.001)
     * @param beta1       exponential decay rate for first moment (typical: 0.9)
     * @param beta2       exponential decay rate for second moment (typical: 0.999)
     * @param epsilon     numerical stability constant (typical: 1e-8)
     * @param weightDecay decoupled weight decay (AdamW) coefficient (0 = no decay)
     */
    public AdamOptimizer(int paramCount, double learningRate,
                          double beta1, double beta2,
                          double epsilon, double weightDecay) {
        if (paramCount <= 0) throw new IllegalArgumentException("paramCount must be > 0");
        if (learningRate <= 0) throw new IllegalArgumentException("learningRate must be > 0");
        if (beta1 <= 0 || beta1 >= 1) throw new IllegalArgumentException("beta1 must be in (0,1)");
        if (beta2 <= 0 || beta2 >= 1) throw new IllegalArgumentException("beta2 must be in (0,1)");
        if (epsilon <= 0) throw new IllegalArgumentException("epsilon must be > 0");
        if (weightDecay < 0) throw new IllegalArgumentException("weightDecay must be >= 0");

        this.paramCount = paramCount;
        this.learningRate = learningRate;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
        this.weightDecay = weightDecay;
        this.m = new double[paramCount];
        this.v = new double[paramCount];
        this.t = 0;
        this.currentStep = 0;
        this.lrScale = 1.0;
        this.warmupSteps = 0;
    }

    /**
     * Convenience constructor with default hyperparameters.
     */
    public AdamOptimizer(int paramCount, double learningRate) {
        this(paramCount, learningRate, 0.9, 0.999, 1e-8, 0.0);
    }

    /**
     * Configures linear warmup steps.
     */
    public void setWarmupSteps(int warmupSteps) {
        this.warmupSteps = warmupSteps;
    }

    /**
     * Sets a custom learning rate scale (for scheduling).
     */
    public void setLearningRateScale(double scale) {
        this.lrScale = scale;
    }

    /**
     * Performs a single optimization step for all parameters.
     *
     * @param params    current parameter values (will be updated in-place)
     * @param gradients gradient values for each parameter
     */
    public void step(double[] params, double[] gradients) {
        if (params.length != paramCount || gradients.length != paramCount) {
            throw new IllegalArgumentException("Parameter/gradient array length mismatch");
        }

        t++;
        currentStep++;
        double effectiveLr = getEffectiveLearningRate();

        for (int i = 0; i < paramCount; i++) {
            double g = gradients[i];

            // Apply decoupled weight decay (AdamW)
            if (weightDecay > 0) {
                params[i] -= effectiveLr * weightDecay * params[i];
            }

            // Update biased first moment estimate
            m[i] = beta1 * m[i] + (1.0 - beta1) * g;

            // Update biased second moment estimate
            v[i] = beta2 * v[i] + (1.0 - beta2) * g * g;

            // Bias correction
            double mHat = m[i] / (1.0 - Math.pow(beta1, t));
            double vHat = v[i] / (1.0 - Math.pow(beta2, t));

            // Update parameters
            params[i] -= effectiveLr * mHat / (Math.sqrt(vHat) + epsilon);
        }
    }

    /**
     * Computes the effective learning rate considering warmup and schedule.
     */
    private double getEffectiveLearningRate() {
        double scale = lrScale;
        if (warmupSteps > 0 && currentStep < warmupSteps) {
            scale *= (double) currentStep / warmupSteps;
        }
        return learningRate * scale;
    }

    /**
     * Resets optimizer state (for starting fresh).
     */
    public void reset() {
        Arrays.fill(m, 0.0);
        Arrays.fill(v, 0.0);
        t = 0;
        currentStep = 0;
        lrScale = 1.0;
    }

    // --- Getters ---

    public int getStep() { return t; }
    public double getLearningRate() { return learningRate; }
    public double getBeta1() { return beta1; }
    public double getBeta2() { return beta2; }
    public double getEpsilon() { return epsilon; }
    public double getWeightDecay() { return weightDecay; }
}
```

### Learning Rate Scheduler

```java
package com.deeplearning.optimizer;

/**
 * Learning rate schedulers for training deep neural networks.
 */
public class LRScheduler {

    /**
     * Computes the learning rate scale factor for a given step.
     */
    @FunctionalInterface
    public interface Schedule {
        double getScale(int step, int totalSteps);
    }

    /** Constant schedule (no decay). */
    public static final Schedule CONSTANT = (step, total) -> 1.0;

    /**
     * Step decay: multiply by gamma every stepSize steps.
     */
    public static Schedule stepDecay(double gamma, int stepSize) {
        return (step, total) -> Math.pow(gamma, step / stepSize);
    }

    /**
     * Exponential decay: η = η₀ · e^(-decayRate · step)
     */
    public static Schedule exponentialDecay(double decayRate) {
        return (step, total) -> Math.exp(-decayRate * step);
    }

    /**
     * Cosine annealing: η = η_min + 0.5(η_max - η_min)(1 + cos(π·step/total))
     */
    public static Schedule cosineAnnealing(double etaMin, double etaMax) {
        return (step, total) -> {
            double cos = Math.cos(Math.PI * step / total);
            return (etaMin / etaMax) + 0.5 * (1 - etaMin / etaMax) * (1 + cos);
        };
    }

    /**
     * Linear warmup followed by cosine decay.
     */
    public static Schedule warmupCosine(double warmupFraction, double etaMin, double etaMax) {
        return (step, total) -> {
            int warmupSteps = (int) (total * warmupFraction);
            if (step < warmupSteps) {
                return (double) step / warmupSteps;
            }
            double progress = (double) (step - warmupSteps) / (total - warmupSteps);
            double cos = Math.cos(Math.PI * progress);
            return (etaMin / etaMax) + 0.5 * (1 - etaMin / etaMax) * (1 + cos);
        };
    }
}
```

### RMSprop Optimizer

```java
package com.deeplearning.optimizer;

import java.util.Arrays;

/**
 * RMSprop optimizer (Hinton, 2012).
 * Maintains per-parameter squared gradient moving average.
 */
public class RMSpropOptimizer {

    private final double learningRate;
    private final double decay;          // β₂ in Adam notation
    private final double epsilon;
    private final double momentum;

    private double[] squaredGradients;
    private double[] momentumBuffer;
    private int paramCount;

    public RMSpropOptimizer(int paramCount, double learningRate,
                             double decay, double epsilon, double momentum) {
        this.paramCount = paramCount;
        this.learningRate = learningRate;
        this.decay = decay;
        this.epsilon = epsilon;
        this.momentum = momentum;
        this.squaredGradients = new double[paramCount];
        this.momentumBuffer = new double[paramCount];
    }

    public RMSpropOptimizer(int paramCount, double learningRate) {
        this(paramCount, learningRate, 0.99, 1e-8, 0.0);
    }

    /**
     * Performs an RMSprop update step.
     */
    public void step(double[] params, double[] gradients) {
        for (int i = 0; i < paramCount; i++) {
            double g = gradients[i];

            // Running average of squared gradients
            squaredGradients[i] = decay * squaredGradients[i] + (1.0 - decay) * g * g;

            // Compute scaled gradient
            double scaledGrad = g / (Math.sqrt(squaredGradients[i]) + epsilon);

            // Optional momentum
            if (momentum > 0) {
                momentumBuffer[i] = momentum * momentumBuffer[i] + scaledGrad;
                params[i] -= learningRate * momentumBuffer[i];
            } else {
                params[i] -= learningRate * scaledGrad;
            }
        }
    }

    public void reset() {
        Arrays.fill(squaredGradients, 0.0);
        Arrays.fill(momentumBuffer, 0.0);
    }
}
```

### Test Harness

```java
package com.deeplearning.optimizer;

import java.util.Arrays;

/**
 * Test harness for Adam, AdamW, and RMSprop optimizers.
 * Validates core update mechanics and convergence properties.
 */
public class OptimizerTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testAdamBasicUpdate();
        testAdamBiasCorrection();
        testAdamConvergence();
        testAdamWWeightDecay();
        testRMSprop();
        testWarmup();
        testLRScheduler();
        testEdgeCases();
        testCompareSGD();
        System.out.printf("%n=== Results: %d passed, %d failed ===%n", passed, failed);
    }

    static void assertTest(boolean condition, String name) {
        if (condition) { passed++; System.out.printf("[PASS] %s%n", name); }
        else { failed++; System.err.printf("[FAIL] %s%n", name); }
    }

    static void testAdamBasicUpdate() {
        int n = 3;
        AdamOptimizer adam = new AdamOptimizer(n, 0.1, 0.9, 0.999, 1e-8, 0.0);
        double[] params = {1.0, -0.5, 0.3};
        double[] grads = {0.1, -0.2, 0.05};

        double[] before = params.clone();
        adam.step(params, grads);

        // After one step, params should have changed
        boolean changed = false;
        for (int i = 0; i < n; i++) {
            if (Math.abs(params[i] - before[i]) > 0) changed = true;
        }
        assertTest(changed, "Adam update changes parameters");

        // First moment should be (1-0.9) * grad = 0.1 * grad
        // Check that parameters moved in correct direction
        for (int i = 0; i < n; i++) {
            double expectedDir = -Math.signum(grads[i]);
            double actualDir = Math.signum(params[i] - before[i]);
            if (Math.abs(grads[i]) > 1e-10) {
                assertTest(actualDir == expectedDir || actualDir == 0,
                    "Adam parameter " + i + " moves opposite to gradient");
            }
        }
    }

    static void testAdamBiasCorrection() {
        int n = 1;
        AdamOptimizer adam = new AdamOptimizer(n, 1.0, 0.9, 0.999, 1e-8, 0.0);
        double[] params = {0.0};
        double[] grads = {1.0};

        // At step 1: m = 0.1 * 1.0 = 0.1, m_hat = 0.1 / 0.1 = 1.0
        // v = 0.001 * 1.0 = 0.001, v_hat = 0.001 / 0.001 = 1.0
        // update = 1.0 * 1.0 / (sqrt(1.0) + 1e-8) ≈ 1.0
        adam.step(params, grads);
        assertTest(Math.abs(params[0] - (-1.0)) < 0.01,
            "Adam bias correction produces correct update magnitude");

        // Without bias correction, m_hat would be 0.1 and v_hat would be 0.001
        // giving an update of 1.0 * 0.1 / sqrt(0.001) ≈ 3.16 (much too large)
        // So bias correction is essential for correctness
    }

    static void testAdamConvergence() {
        // Minimize f(x) = x² — converge to 0
        int n = 1;
        AdamOptimizer adam = new AdamOptimizer(n, 0.1, 0.9, 0.999, 1e-8, 0.0);
        double[] params = {5.0};

        for (int step = 0; step < 1000; step++) {
            double grad = 2.0 * params[0]; // gradient of x²
            adam.step(params, new double[]{grad});
        }

        assertTest(Math.abs(params[0]) < 0.01,
            "Adam converges f(x)=x² to near zero (got " + params[0] + ")");
    }

    static void testAdamWWeightDecay() {
        int n = 2;
        AdamOptimizer adam = new AdamOptimizer(n, 0.1, 0.9, 0.999, 1e-8, 0.01); // wd=0.01
        double[] params = {1.0, 1.0};
        double[] grads = {0.0, 0.0}; // no data gradient

        // With decoupled weight decay, params should shrink: -= 0.1 * 0.01 * param
        double expected0 = 1.0 - 0.1 * 0.01 * 1.0;
        adam.step(params, grads);
        assertTest(Math.abs(params[0] - expected0) < 1e-10,
            "AdamW decoupled weight decay shrinks params");

        // Verify decay is uniform regardless of gradient magnitude
        double[] params2 = {1.0, 100.0};
        double[] grads2 = {0.0, 0.0};
        adam = new AdamOptimizer(n, 0.1, 0.9, 0.999, 1e-8, 0.01);
        adam.step(params2, grads2);
        assertTest(Math.abs(params2[1] - (100.0 - 0.1 * 0.01 * 100.0)) < 1e-8,
            "AdamW uniform decay regardless of parameter magnitude");
    }

    static void testRMSprop() {
        int n = 1;
        RMSpropOptimizer rms = new RMSpropOptimizer(n, 0.1, 0.9, 1e-8, 0.0);
        double[] params = {10.0};
        double[] grads = {1.0};

        // v = 0.9 * 0 + 0.1 * 1 = 0.1
        // update = 0.1 * 1.0 / sqrt(0.1 + 1e-8) ≈ 0.316
        rms.step(params, grads);
        double expectedUpdate = 0.1 * 1.0 / Math.sqrt(0.1 + 1e-8);
        assertTest(Math.abs(params[0] - (10.0 - expectedUpdate)) < 1e-6,
            "RMSprop update magnitude");

        // After many steps with zero gradient, v should decay
        // and no more updates should occur
        for (int i = 0; i < 100; i++) {
            rms.step(params, new double[]{0.0});
        }
        // v should approach 0, and updates should stop
    }

    static void testWarmup() {
        AdamOptimizer adam = new AdamOptimizer(1, 0.001, 0.9, 0.999, 1e-8, 0);
        adam.setWarmupSteps(10);
        double[] params = {1.0};
        double[] grads = {0.1};

        // Step 1: lr scale = 1/10 = 0.1
        // Step 5: lr scale = 5/10 = 0.5
        // Step 10: lr scale = 10/10 = 1.0
        // Step 11: lr scale = 1.0 (warmup complete)
        for (int i = 1; i <= 15; i++) {
            double before = params[0];
            adam.step(params, grads);
            double change = Math.abs(params[0] - before);

            if (i == 1) {
                assertTest(change < 1e-4, "Warmup step 1: small update");
            }
        }
    }

    static void testLRScheduler() {
        // Test cosine schedule
        var cosine = LRScheduler.cosineAnnealing(0.0, 1.0);
        double s0 = cosine.getScale(0, 100);
        double s50 = cosine.getScale(50, 100);
        double s100 = cosine.getScale(100, 100);

        assertTest(Math.abs(s0 - 1.0) < 1e-9, "Cosine schedule at step 0: max LR");
        assertTest(Math.abs(s50 - 0.5) < 1e-9, "Cosine schedule at midpoint: 0.5 * (1 + cos(π/2)) = 0.5");
        assertTest(Math.abs(s100 - 0.0) < 1e-9, "Cosine schedule at step 100: min LR");

        // Test warmup cosine
        var warmCosine = LRScheduler.warmupCosine(0.1, 0.0, 1.0);
        double w0 = warmCosine.getScale(0, 100);
        double w5 = warmCosine.getScale(5, 100);
        double w10 = warmCosine.getScale(10, 100);
        double w50 = warmCosine.getScale(50, 100);
        assertTest(Math.abs(w0) < 1e-9, "Warmup-cosine at step 0: 0");
        assertTest(Math.abs(w10 - 1.0) < 0.001, "Warmup-cosine at step 10 (end of warmup): 1.0");
        assertTest(w50 < 1.0 && w50 > 0.0, "Warmup-cosine at midpoint");
    }

    static void testEdgeCases() {
        // Single parameter
        AdamOptimizer adam = new AdamOptimizer(1, 0.001);
        double[] p = {0.0};
        adam.step(p, new double[]{0.0});
        assertTest(Math.abs(p[0]) < 1e-15, "Adam with zero gradient: no change");

        // Negative learning rate (should be rejected by constructor)
        boolean threw = false;
        try {
            new AdamOptimizer(1, -0.001);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        assertTest(threw, "Rejects negative learning rate");
    }

    static void testCompareSGD() {
        // Compare simple quadratic minimization with Adam vs theoretical SGD
        int n = 1;
        AdamOptimizer adam = new AdamOptimizer(n, 0.01, 0.9, 0.999, 1e-8, 0);
        double[] adamParams = {10.0};
        double[] sgdParams = {10.0};

        // 100 steps of f(x) = x²
        for (int step = 0; step < 100; step++) {
            double grad = 2.0 * adamParams[0];
            adam.step(adamParams, new double[]{grad});

            grad = 2.0 * sgdParams[0];
            sgdParams[0] -= 0.01 * grad;
        }

        // Adam should converge faster than SGD for this simple problem
        assertTest(Math.abs(adamParams[0]) < Math.abs(sgdParams[0]),
            "Adam converges faster than SGD on quadratic");
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Per parameter update (all optimizers):** $O(1)$ — a few multiplications, additions, and a square root.

**Total per step:** $O(P)$ where $P$ is the total number of parameters.

| Operation | Adam | RMSprop | SGD |
|-----------|------|---------|-----|
| Gradient scale | 1 mul | 1 div | 1 mul |
| Moment updates | 4 mul, 2 add | 2 mul, 1 add | 0 |
| Bias correction | 2 div | 0 | 0 |
| Square root | 1 sqrt | 1 sqrt | 0 |
| Weight decay | 1 mul, 1 add | optional | optional |
| **Total ops/param** | ~12 | ~6 | ~2 |

### Space Complexity

- **Adam:** $O(2P)$ for $m$ and $v$ states.
- **RMSprop:** $O(P)$ for squared gradient cache.
- **AdamW:** Same as Adam ($O(2P)$).
- **SGD:** $O(1)$ (no momentum) or $O(P)$ (with momentum).

---

## Follow-Up Questions

### Q1: Why does Adam need bias correction?

**Answer:** At early timesteps, $m_t$ and $v_t$ are initialized to 0, so the moving averages are biased toward zero. For $t=1$:

$$m_1 = \beta_1 \cdot 0 + (1 - \beta_1) \cdot g_1 = (1 - \beta_1) \cdot g_1$$

With $\beta_1 = 0.9$: $m_1 = 0.1g_1$ — only 10% of the true direction. The update would be too small.

Similarly, $v_1 = 0.001g_1^2$ — the second moment is severely underestimated, making $\sqrt{v_1}$ too small and the update too large.

Bias correction fixes both:
$$\hat{m}_1 = \frac{0.1g_1}{0.1} = g_1, \quad \hat{v}_1 = \frac{0.001g_1^2}{0.001} = g_1^2$$

As $t \to \infty$, $\beta_1^t \to 0$ and $\beta_2^t \to 0$, so the correction becomes negligible.

### Q2: Derive the Adam update rule from first principles.

**Answer:** Adam can be viewed as combining two ideas:

1. **Momentum** (SGD with momentum):
   $$m_t = \beta_1 m_{t-1} + (1 - \beta_1) g_t = \text{exponential moving average of gradients}$$

2. **Adaptive learning rates** (from RMSprop/AdaDelta):
   $$v_t = \beta_2 v_{t-1} + (1 - \beta_2) g_t^2 = \text{exponential moving average of squared gradients}$$

The effective step size for parameter $i$ at step $t$ is:
$$\Delta\theta_{t,i} = -\eta \cdot \frac{\text{signal}_{t,i}}{\sqrt{\text{noise}_{t,i}} + \epsilon}$$

where $\text{signal} = \hat{m}_t$ (directional momentum) and $\sqrt{\text{noise}} = \sqrt{\hat{v}_t}$ (RMS of observed gradients).

**Key insight:** The ratio $\hat{m}_t / \sqrt{\hat{v}_t}$ is approximately the **signal-to-noise ratio** (SNR) of the gradient. When SNR is high (reliable gradient direction), the step is large. When SNR is low (noisy gradients), the step is small.

### Q3: What is the difference between "weight decay" and "L2 regularization" in Adam?

**Answer:** In standard SGD, they are equivalent. In Adam, they differ:

**L2 regularization with Adam:**
$$\theta_{t+1} = \theta_t - \eta \cdot \text{AdamUpdate}(g_t + \lambda \theta_t)$$

The weight decay term $\lambda \theta_t$ is included in the gradient before the adaptive scaling, so it gets divided by $\sqrt{\hat{v}_t}$. This means the effective regularization is $\eta\lambda \theta_t / \sqrt{\hat{v}_t}$, which varies per parameter.

**Decoupled weight decay (AdamW):**
$$\theta_{t+1} = \theta_t - \eta \cdot \text{AdamUpdate}(g_t) - \eta\lambda \theta_t$$

The weight decay is applied after the adaptive step, uniformly to all parameters. This has been shown to improve generalization, especially for Transformers (Loshchilov & Hutter, 2019).

### Q4: Explain the advantages of Adam over SGD with momentum.

**Answer:**

| Aspect | SGD + Momentum | Adam |
|--------|---------------|------|
| Adaptive LR | No (single global LR) | Yes (per-parameter) |
| Learning rate tuning | Critical, requires schedule | More robust (default 0.001 usually works) |
| Sparse gradients | Struggles | Excellent (adaptive per-param) |
| Convergence proof | Convex + some non-convex | Convex + some non-convex |
| Generalization | Often better (flat minima) | Can overfit (need weight decay) |
| Memory | $O(P)$ for momentum | $O(2P)$ for moments |
| Hyperparameters | 1-2 (LR, momentum) | 3-4 (LR, β₁, β₂, ε) |

**Practical takeaway:** Use Adam for rapid prototyping and NLP/Transformers. Use SGD + momentum with careful scheduling for computer vision.

### Q5: How would you implement gradient clipping with Adam?

**Answer:** Gradient clipping prevents exploding gradients by scaling down the entire gradient vector when its norm exceeds a threshold:

```java
public void stepWithClipping(double[] params, double[] gradients, double maxNorm) {
    // Compute gradient norm
    double norm = 0.0;
    for (double g : gradients) norm += g * g;
    norm = Math.sqrt(norm);

    // Scale if norm exceeds threshold
    double scale = norm > maxNorm ? maxNorm / norm : 1.0;

    // Apply scaled gradients
    double[] scaledGrads = new double[gradients.length];
    for (int i = 0; i < gradients.length; i++) {
        scaledGrads[i] = gradients[i] * scale;
    }

    step(params, scaledGrads);
}
```

Typical max norms: 0.25 (RNNs), 1.0 (Transformers), 5.0 (CNNs).

### Q6: Compare Adam, AdaGrad, and RMSprop.

**Answer:**

| Property | AdaGrad | RMSprop | Adam |
|---------|---------|---------|------|
| Gradient accumulation | Sum of squared grads | EMA of squared grads | EMA of both moments |
| Learning rate | Monotonically decreasing | Adaptive, can increase | Adaptive, can increase |
| Suitable for | Sparse features | Non-stationary | General purpose |
| Memory | $O(P)$ | $O(P)$ | $O(2P)$ |
| Issue | LR → 0 too quickly | Needs global LR tuning | May not generalize as well |

**AdaGrad** accumulates all past squared gradients, so the effective learning rate monotonically decreases and eventually becomes 0. **RMSprop** fixes this by using a moving average instead of sum. **Adam** adds momentum on top.

### Q7: What is the "AMSGrad" variant of Adam and why was it proposed?

**Answer:** AMSGrad (Reddi et al., 2018) fixes a convergence issue in Adam where the effective learning rate $\eta / \sqrt{v_t}$ can **increase** if $\hat{v}_t$ decreases. The fix:

$$v_t = \beta_2 v_{t-1} + (1 - \beta_2) g_t^2$$
$$\hat{v}_t = \max(\hat{v}_{t-1}, v_t)$$

By taking the element-wise maximum, $\hat{v}_t$ is guaranteed to be monotonic, ensuring the learning rate monotonically decreases. However, empirical results show AMSGrad rarely outperforms standard Adam.

### Q8: How do you tune Adam hyperparameters in practice?

**Answer:**

| Hyperparameter | Default | When to change |
|---------------|---------|----------------|
| $\eta$ | 0.001 | If loss doesn't decrease, try 0.0003 or 0.003 |
| $\beta_1$ | 0.9 | For very noisy gradients, increase to 0.95 |
| $\beta_2$ | 0.999 | For sparse gradients, decrease to 0.99 |
| $\epsilon$ | 1e-8 | Rarely needs changing (try 1e-7 if instability) |
| Weight decay | 0.0 | Try 0.01-0.1 for Transformers |
| Warmup steps | 0 | Try 5-10% of total steps |
| Gradient clip | None | Try 1.0 if gradients explode |

**Rule of thumb:** For Transformer models, use AdamW with $\eta=3e-4$, $\beta_1=0.9$, $\beta_2=0.98$, $\epsilon=1e-8$, weight decay 0.01-0.1, and 5-10% warmup.

---

## Test Cases

| Test Case | Optimizer | Description | Expected |
|-----------|-----------|-------------|----------|
| TC-01 | Adam | Basic parameter update | Parameters change |
| TC-02 | Adam | Bias correction at step 1 | Correct magnitude |
| TC-03 | Adam | Convergence on x² | Near zero |
| TC-04 | AdamW | Decoupled weight decay | Uniform shrinkage |
| TC-05 | AdamW | Zero gradient + weight decay | Params decay by $\eta\lambda\theta$ |
| TC-06 | RMSprop | Update magnitude | $0.316$ for unit grad |
| TC-07 | Warmup | Linear scale | Scale = step/warmup |
| TC-08 | Cosine schedule | Scale at 0, 50, 100 | 1.0, 0.5, 0.0 |
| TC-09 | Zero gradient | Adam | No parameter change |
| TC-10 | Adam vs SGD | Quadratic convergence | Adam faster |

---

## Key Takeaways

- **Adam** combines momentum (first moment) with adaptive learning rates (second moment).
- **Bias correction** is essential for correct early-step behavior.
- **AdamW** decouples weight decay from adaptive updates, improving generalization.
- **Warmup** stabilizes early training, especially for Transformers.
- **Learning rate schedules** (cosine, step, exponential) help balance convergence speed and final loss.
- Adam is the default optimizer for most deep learning tasks, with SGD+momentum preferred for some vision tasks.
