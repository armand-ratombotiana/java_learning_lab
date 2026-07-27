# Problem Walkthrough: SGD, Momentum, Adam Optimizers

## Problem Statement

**Interview Problem: Implement SGD, Momentum, and Adam Optimizers**

You are building a deep learning training framework. Implement three stochastic optimizers:

1. **SGD (Stochastic Gradient Descent)** - Basic minibatch gradient descent
2. **SGD with Momentum** - Accumulates velocity to smooth gradient updates
3. **RMSprop** - Adaptive learning rate per parameter
4. **Adam** - Combines momentum and RMSprop with bias correction

**Constraints:**
- All optimizers implement a common Optimizer interface
- Support configurable learning rate, weight decay, and minibatch updates
- Adam must include bias correction terms
- Numerically stable (add epsilon to denominator)
- Track parameter state across steps

**Example:**
```java
Optimizer adam = new Adam(0.001).setWeightDecay(1e-5);
for (int epoch = 0; epoch < 100; epoch++) {
    for (var batch : dataLoader) {
        adam.zeroGrad();
        double loss = model.computeLoss(batch);
        model.backward();
        adam.step(model);
    }
}
```

---

## Step-by-Step Solution Walkthrough

### 1. Mathematical Foundation

#### 1.1 Stochastic Gradient Descent (SGD)

theta_{t+1} = theta_t - eta * g_t

where g_t = (1/B) * sum_{i in batch} grad f_i(theta_t)

**Properties:**
- g_t is unbiased: E[g_t] = grad f(theta_t)
- Variance: Var(g_t) = Var(full) / B

#### 1.2 SGD with Momentum

v_t = beta * v_{t-1} + g_t
theta_{t+1} = theta_t - eta * v_t

Accumulates gradients in consistent directions, cancels oscillations.
Typical beta = 0.9.

#### 1.3 RMSprop

v_t = beta * v_{t-1} + (1-beta) * g_t^2
theta_{t+1} = theta_t - eta * g_t / (sqrt(v_t) + epsilon)

Adaptive learning rate: large gradients reduced, small gradients increased.

#### 1.4 Adam

m_t = beta1 * m_{t-1} + (1-beta1) * g_t
v_t = beta2 * v_{t-1} + (1-beta2) * g_t^2
m_hat_t = m_t / (1 - beta1^t)
v_hat_t = v_t / (1 - beta2^t)
theta_{t+1} = theta_t - eta * m_hat_t / (sqrt(v_hat_t) + epsilon)

**Why bias correction?** m_t, v_t initialized at 0, biased toward 0 early on.

---

### 2. Algorithm Design

#### 2.1 SGD

```
for each parameter theta with gradient g:
  if weight_decay > 0: g += wd * theta
  theta -= lr * g
```

#### 2.2 SGD with Momentum

```
for each parameter theta with gradient g:
  if weight_decay > 0: g += wd * theta
  v = mu * v + lr * g
  theta -= v
```

#### 2.3 RMSprop

```
for each parameter theta with gradient g:
  if weight_decay > 0: g += wd * theta
  s = beta * s + (1-beta) * g^2
  theta -= lr * g / (sqrt(s) + eps)
```

#### 2.4 Adam

```
t += 1
for each parameter theta with gradient g:
  if weight_decay > 0: g += wd * theta
  m = beta1 * m + (1-beta1) * g
  v = beta2 * v + (1-beta2) * g^2
  m_hat = m / (1 - beta1^t)
  v_hat = v / (1 - beta2^t)
  theta -= lr * m_hat / (sqrt(v_hat) + eps)
```

---

### 3. Java Implementation

```java
package com.ml.optimize;

import java.util.HashMap;
import java.util.Map;

public interface ParameterProvider {
    double[] getParameters();
    double[] getGradients();
    default String getName() { return "parameter"; }
}

public abstract class Optimizer {

    protected double learningRate;
    protected double weightDecay;
    protected double epsilon;
    protected int t;

    protected Optimizer(double learningRate) {
        if (learningRate <= 0) throw new IllegalArgumentException(
            "Learning rate must be positive: " + learningRate);
        this.learningRate = learningRate;
        this.weightDecay = 0.0;
        this.epsilon = 1e-8;
        this.t = 0;
    }

    public Optimizer setWeightDecay(double wd) {
        if (wd < 0) throw new IllegalArgumentException(
            "Weight decay must be non-negative: " + wd);
        this.weightDecay = wd;
        return this;
    }

    public Optimizer setEpsilon(double eps) {
        if (eps <= 0) throw new IllegalArgumentException(
            "Epsilon must be positive: " + eps);
        this.epsilon = eps;
        return this;
    }

    public abstract void step(ParameterProvider model);
    public abstract void zeroGrad();
    public int getTimestep() { return t; }

    protected void applyWeightDecay(double[] params, double[] grads) {
        if (weightDecay > 0) {
            for (int i = 0; i < params.length; i++) {
                grads[i] += weightDecay * params[i];
            }
        }
    }
}

class SGD extends Optimizer {

    public SGD(double learningRate) { super(learningRate); }

    @Override
    public void step(ParameterProvider model) {
        double[] params = model.getParameters();
        double[] grads = model.getGradients();
        applyWeightDecay(params, grads);
        for (int i = 0; i < params.length; i++) {
            params[i] -= learningRate * grads[i];
        }
        t++;
    }

    @Override
    public void zeroGrad() {}
}

class SGDMomentum extends Optimizer {

    private final double momentum;
    private final Map<String, double[]> velocities;

    public SGDMomentum(double learningRate, double momentum) {
        super(learningRate);
        if (momentum < 0 || momentum >= 1) throw new IllegalArgumentException(
            "Momentum must be in [0, 1): " + momentum);
        this.momentum = momentum;
        this.velocities = new HashMap<>();
    }

    @Override
    public void step(ParameterProvider model) {
        double[] params = model.getParameters();
        double[] grads = model.getGradients();
        applyWeightDecay(params, grads);
        double[] v = velocities.computeIfAbsent(
            model.getName(), k -> new double[params.length]);
        for (int i = 0; i < params.length; i++) {
            v[i] = momentum * v[i] + learningRate * grads[i];
            params[i] -= v[i];
        }
        t++;
    }

    @Override
    public void zeroGrad() {}
}

class RMSprop extends Optimizer {

    private final double beta;
    private final Map<String, double[]> sqGrads;

    public RMSprop(double learningRate, double beta) {
        super(learningRate);
        if (beta < 0 || beta >= 1) throw new IllegalArgumentException(
            "Beta must be in [0, 1): " + beta);
        this.beta = beta;
        this.sqGrads = new HashMap<>();
    }

    @Override
    public void step(ParameterProvider model) {
        double[] params = model.getParameters();
        double[] grads = model.getGradients();
        applyWeightDecay(params, grads);
        double[] s = sqGrads.computeIfAbsent(
            model.getName(), k -> new double[params.length]);
        for (int i = 0; i < params.length; i++) {
            s[i] = beta * s[i] + (1 - beta) * grads[i] * grads[i];
            params[i] -= learningRate * grads[i] / (Math.sqrt(s[i]) + epsilon);
        }
        t++;
    }

    @Override
    public void zeroGrad() {}
}

class Adam extends Optimizer {

    private final double beta1;
    private final double beta2;
    private final Map<String, double[]> firstMoments;
    private final Map<String, double[]> secondMoments;

    public Adam(double learningRate) {
        this(learningRate, 0.9, 0.999);
    }

    public Adam(double learningRate, double beta1, double beta2) {
        super(learningRate);
        if (beta1 < 0 || beta1 >= 1) throw new IllegalArgumentException(
            "beta1 must be in [0, 1): " + beta1);
        if (beta2 < 0 || beta2 >= 1) throw new IllegalArgumentException(
            "beta2 must be in [0, 1): " + beta2);
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.firstMoments = new HashMap<>();
        this.secondMoments = new HashMap<>();
    }

    @Override
    public void step(ParameterProvider model) {
        t++;
        double[] params = model.getParameters();
        double[] grads = model.getGradients();
        applyWeightDecay(params, grads);
        double[] m = firstMoments.computeIfAbsent(
            model.getName(), k -> new double[params.length]);
        double[] v = secondMoments.computeIfAbsent(
            model.getName(), k -> new double[params.length]);
        double biasCorrection1 = 1.0 - Math.pow(beta1, t);
        double biasCorrection2 = 1.0 - Math.pow(beta2, t);
        for (int i = 0; i < params.length; i++) {
            m[i] = beta1 * m[i] + (1 - beta1) * grads[i];
            v[i] = beta2 * v[i] + (1 - beta2) * grads[i] * grads[i];
            double mHat = m[i] / biasCorrection1;
            double vHat = v[i] / biasCorrection2;
            params[i] -= learningRate * mHat / (Math.sqrt(vHat) + epsilon);
        }
    }

    @Override
    public void zeroGrad() {}
}
```

---

### 4. Test Cases

```java
package com.ml.optimize;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OptimizerTest {

    private static final double DELTA = 1e-8;

    static class QuadraticModel implements ParameterProvider {
        private double[] params;
        private final double[] target;
        private double[] grads;

        QuadraticModel(double[] initial, double[] target) {
            this.params = initial.clone();
            this.target = target.clone();
            this.grads = new double[initial.length];
        }

        public double[] getParameters() { return params; }
        public double[] getGradients() {
            for (int i = 0; i < params.length; i++) {
                grads[i] = params[i] - target[i];
            }
            return grads;
        }
    }

    @Test
    void testSGDConvergence() {
        SGD sgd = new SGD(0.1);
        QuadraticModel model = new QuadraticModel(
            new double[]{5.0, -3.0}, new double[]{2.0, 2.0});
        for (int iter = 0; iter < 500; iter++) sgd.step(model);
        assertArrayEquals(new double[]{2.0, 2.0}, model.getParameters(), 1e-4);
    }

    @Test
    void testMomentumConvergence() {
        SGDMomentum sgd = new SGDMomentum(0.1, 0.9);
        QuadraticModel model = new QuadraticModel(
            new double[]{10.0, -5.0}, new double[]{0.0, 0.0});
        for (int iter = 0; iter < 200; iter++) sgd.step(model);
        assertArrayEquals(new double[]{0.0, 0.0}, model.getParameters(), 1e-4);
    }

    @Test
    void testRMSpropConvergence() {
        RMSprop rms = new RMSprop(0.1, 0.9);
        QuadraticModel model = new QuadraticModel(
            new double[]{5.0, 5.0}, new double[]{0.0, 0.0});
        for (int iter = 0; iter < 500; iter++) rms.step(model);
        assertArrayEquals(new double[]{0.0, 0.0}, model.getParameters(), 1e-4);
    }

    @Test
    void testAdamConvergence() {
        Adam adam = new Adam(0.1);
        QuadraticModel model = new QuadraticModel(
            new double[]{5.0, 5.0}, new double[]{0.0, 0.0});
        for (int iter = 0; iter < 200; iter++) adam.step(model);
        assertArrayEquals(new double[]{0.0, 0.0}, model.getParameters(), 1e-4);
    }

    @Test
    void testWeightDecay() {
        SGD sgd = new SGD(0.1);
        sgd.setWeightDecay(0.1);
        QuadraticModel model = new QuadraticModel(
            new double[]{10.0, 10.0}, new double[]{0.0, 0.0});
        for (int iter = 0; iter < 500; iter++) sgd.step(model);
        // Weight decay pulls toward 0
        assertTrue(model.getParameters()[0] < 0.1);
        assertTrue(model.getParameters()[1] < 0.1);
    }

    @Test
    void testAdamBiasCorrection() {
        Adam adam = new Adam(0.1);
        QuadraticModel model = new QuadraticModel(
            new double[]{1.0}, new double[]{0.0});
        adam.step(model); // t=1
        // First step should be reasonable (not NaN)
        assertFalse(Double.isNaN(model.getParameters()[0]));
    }

    @Test
    void testDifferentParameters() {
        Adam adam = new Adam(0.01);
        QuadraticModel m1 = new QuadraticModel(
            new double[]{5.0}, new double[]{0.0});
        QuadraticModel m2 = new QuadraticModel(
            new double[]{-3.0}, new double[]{0.0});
        for (int iter = 0; iter < 500; iter++) {
            adam.step(m1);
            adam.step(m2);
        }
        assertArrayEquals(new double[]{0.0}, m1.getParameters(), 1e-4);
        assertArrayEquals(new double[]{0.0}, m2.getParameters(), 1e-4);
    }
}
```

---

### 5. Complexity Analysis

**Per-step complexity:** O(n) for all optimizers (n = number of parameters).

**Memory:** O(n) for each optimizer (velocity/momentum buffers).

| Optimizer | Extra Memory | Hyperparameters |
|-----------|-------------|----------------|
| SGD | 0 | lr |
| SGD + Momentum | n | lr, mu |
| RMSprop | n | lr, beta, eps |
| Adam | 2n | lr, beta1, beta2, eps |

---

### 6. Follow-Up Questions

**Q1: Compare the convergence behavior of these optimizers on ill-conditioned problems.**

SGD: slow, zig-zags along narrow valleys. Momentum: dampens oscillations. RMSprop/Adam: adaptive per-parameter lr handles different curvatures well.

**Q2: Why does Adam require bias correction?**

m_t and v_t are initialized at 0, so early estimates are biased toward 0. Without correction, the first few steps are too small. Correction scales early estimates to match true expectations.

**Q3: When would you choose SGD over Adam?**

SGD with proper learning rate schedule often generalizes better (test accuracy) than Adam, though Adam converges faster on training loss. For large datasets and computer vision, SGD+momentum remains popular.

**Q4: How do you tune the learning rate differently for each optimizer?**

SGD: lr ~ 0.01-0.1; need schedule. Adam: lr ~ 0.001; less sensitive. RMSprop: lr ~ 0.001. Momentum: 0.9-0.99 (higher for smoother objectives).

**Q5: What is the difference between weight decay and L2 regularization?**

In SGD, weight decay = L2 regularization (theta -= lr * g + lr*wd*theta = (1-lr*wd)*theta - lr*g). In Adam, they differ because L2 would add wd*theta to g before computing adaptive rates, while weight decay applies decay after adaptive scaling.

**Q6: Explain the relationship between momentum and the condition number.**

Momentum accelerates convergence by reducing the effective condition number. The optimal momentum is related to the condition number: mu_opt = (sqrt(kappa)-1)/(sqrt(kappa)+1). For kappa = 100 (poor conditioning), mu_opt = 0.818.

---

### 7. Applications in ML

| Optimizer | Best For |
|-----------|----------|
| SGD | CV, linear models, when generalization matters |
| Momentum | Deep networks, stable training |
| RMSprop | RNNs, non-stationary objectives |
| Adam | Transformers, GANs, general-purpose |
