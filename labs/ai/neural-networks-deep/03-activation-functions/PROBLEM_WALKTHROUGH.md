# Activation Functions & Derivatives

## Problem Statement

**Problem:** Implement common activation functions used in deep learning and their exact derivatives, demonstrating numerical stability and proper gradient computation.

Design and implement an `ActivationFunction` interface with the following activation functions:
1. **Sigmoid** — classic S-shaped, outputs in (0, 1)
2. **Tanh** — zero-centered sigmoid variant, outputs in (-1, 1)
3. **ReLU** — Rectified Linear Unit, max(0, x)
4. **Leaky ReLU** — ReLU with small slope for negative values
5. **GELU** — Gaussian Error Linear Unit (modern Transformer activation)
6. **Swish** — Self-gated activation: x * sigmoid(x)

Each activation must provide:
- `forward(x)` — compute activation value(s)
- `backward(x)` — compute derivative at given input(s)
- Support for both scalar and vector operations.

**Example:**
```
Input:  x = [-2.0, -1.0, 0.0, 1.0, 2.0]
        sigmoid.forward(x)  → [0.1192, 0.2689, 0.5000, 0.7311, 0.8808]
        sigmoid.backward(x) → [0.1050, 0.1966, 0.2500, 0.1966, 0.1050]
        relu.forward(x)     → [0.0, 0.0, 0.0, 1.0, 2.0]
        relu.backward(x)    → [0.0, 0.0, 0.0, 1.0, 1.0]
```

**Constraints:**
- Input values can be any double (no bound guarantees).
- Must handle edge cases: very large positive/negative values (avoid overflow).
- Must be thread-safe (no mutable shared state).

---

## Step-by-Step Solution Walkthrough

### 1. The Role of Activation Functions

Activation functions introduce **non-linearity** into neural networks. Without them, a multi-layer network collapses to a single linear transformation (composing linear functions yields a linear function). Key properties:

- **Non-linearity:** Enables learning complex functions.
- **Differentiability:** Required for gradient-based learning (backpropagation).
- **Bounded range:** Can help control activation magnitudes.
- **Computational efficiency:** Must be fast for forward/backward passes.

### 2. Activation Functions in Detail

#### Sigmoid

$$\sigma(x) = \frac{1}{1 + e^{-x}}$$

**Range:** (0, 1)
**Derivative:** $\sigma'(x) = \sigma(x)(1 - \sigma(x))$

**Properties:**
- Smooth, differentiable everywhere.
- Outputs can be interpreted as probabilities.
- **Saturation:** For $|x| > 5$, gradient approaches 0 → vanishing gradient.
- Not zero-centered: outputs always positive → can cause zigzagging gradients.

#### Tanh

$$\tanh(x) = \frac{e^x - e^{-x}}{e^x + e^{-x}} = 2\sigma(2x) - 1$$

**Range:** (-1, 1)
**Derivative:** $\tanh'(x) = 1 - \tanh^2(x)$

**Properties:**
- Zero-centered: outputs in (-1, 1) → easier optimization than sigmoid.
- Still saturates for large $|x|$, causing vanishing gradients.
- Preferred over sigmoid in hidden layers of classic MLPs.

#### ReLU (Rectified Linear Unit)

$$f(x) = \max(0, x)$$

**Range:** [0, $\infty$)
**Derivative:** $f'(x) = \begin{cases} 1 & x > 0 \\ 0 & x \leq 0 \end{cases}$

**Properties:**
- Non-saturating: gradient is 1 for $x > 0$ → mitigates vanishing gradient.
- Computationally trivial: single max operation.
- **Dead ReLU problem:** Neurons can get stuck in the negative region (gradient = 0) and never recover.
- Not differentiable at $x = 0$ (subgradient: 0 is conventionally used).

#### Leaky ReLU

$$f(x) = \begin{cases} x & x > 0 \\ \alpha x & x \leq 0 \end{cases}$$

**Range:** (-$\infty$, $\infty$)
**Derivative:** $f'(x) = \begin{cases} 1 & x > 0 \\ \alpha & x \leq 0 \end{cases}$

**Properties:**
- Addresses dead ReLU: small negative gradient allows recovery.
- Typical $\alpha$ values: 0.01, 0.1, or learned (Parametric ReLU).

#### GELU (Gaussian Error Linear Unit)

$$f(x) = x \cdot \Phi(x) = x \cdot \frac{1}{2}\left[1 + \text{erf}\left(\frac{x}{\sqrt{2}}\right)\right]$$

**Range:** (-$\infty$, $\infty$)
**Derivative:** $f'(x) = \Phi(x) + x \cdot \phi(x)$ where $\phi(x) = \frac{1}{\sqrt{2\pi}} e^{-x^2/2}$

**Properties:**
- Smoother than ReLU: continuous differentiability.
- Used in BERT, GPT, and modern Transformer architectures.
- Non-monotonic: has a small negative region near 0.
- Can be approximated: $f(x) \approx 0.5x(1 + \tanh(\sqrt{2/\pi}(x + 0.044715x^3)))$.

#### Swish (SiLU)

$$f(x) = x \cdot \sigma(x) = \frac{x}{1 + e^{-x}}$$

**Range:** (-$\infty$, $\infty$)
**Derivative:** $f'(x) = \sigma(x) + x \cdot \sigma(x)(1 - \sigma(x)) = \sigma(x) + f(x)(1 - \sigma(x))$

**Properties:**
- Self-gated: the sigmoid of $x$ acts as a gate on $x$.
- Non-monotonic: has a small negative region.
- Outperforms ReLU in deeper networks (Google Brain, 2017).
- Smooth everywhere.

### 3. Numerical Stability Considerations

#### Sigmoid Overflow Prevention

For $x \ll 0$, $e^{-x}$ overflows to $\infty$. Solution: compute $\sigma(x)$ differently for $x < 0$:

$$\sigma(x) = \begin{cases} \frac{1}{1 + e^{-x}} & x \geq 0 \\ \frac{e^x}{1 + e^x} & x < 0 \end{cases}$$

#### Softmax Stability

For softmax (related to sigmoid for multi-class), subtract the maximum value:

$$\text{softmax}(x_i) = \frac{e^{x_i - \max(x)}}{\sum_j e^{x_j - \max(x)}}$$

#### Tanh Overflow Prevention

$$\tanh(x) = \begin{cases} 1 - \frac{2}{e^{2x} + 1} & x \geq 0 \\ \frac{e^{2x} - 1}{e^{2x} + 1} & x < 0 \end{cases}$$

---

## Java Implementation

```java
package com.deeplearning.activation;

import java.util.function.DoubleUnaryOperator;

/**
 * Interface for activation functions used in neural networks.
 * Each implementation provides forward evaluation and backward
 * derivative computation for both scalar and array inputs.
 */
public interface ActivationFunction {

    /**
     * Computes the activation output for a single input value.
     *
     * @param x input value
     * @return activated output
     */
    double forward(double x);

    /**
     * Computes the derivative of the activation at a given input value.
     *
     * @param x input value (NOT the forward output)
     * @return derivative value f'(x)
     */
    double backward(double x);

    /**
     * Applies activation to each element of an array.
     *
     * @param input  input array
     * @param output output array (can be same as input for in-place)
     */
    default void forward(double[] input, double[] output) {
        for (int i = 0; i < input.length; i++) {
            output[i] = forward(input[i]);
        }
    }

    /**
     * Applies derivative to each element of an array.
     *
     * @param input  input array (x values, NOT forward outputs)
     * @param output output array for derivatives
     */
    default void backward(double[] input, double[] output) {
        for (int i = 0; i < input.length; i++) {
            output[i] = backward(input[i]);
        }
    }

    /**
     * Returns the name of this activation function.
     */
    String getName();

    // --- Implementation: Sigmoid ---

    /**
     * Sigmoid activation: σ(x) = 1 / (1 + e^(-x))
     * Range: (0, 1). Compute with numerical stability for x < 0.
     */
    final class Sigmoid implements ActivationFunction {

        @Override
        public double forward(double x) {
            if (x >= 0) {
                return 1.0 / (1.0 + Math.exp(-x));
            } else {
                double expX = Math.exp(x);
                return expX / (1.0 + expX);
            }
        }

        @Override
        public double backward(double x) {
            double sig = forward(x);
            return sig * (1.0 - sig);
        }

        @Override
        public String getName() {
            return "Sigmoid";
        }
    }

    // --- Implementation: Tanh ---

    /**
     * Hyperbolic tangent: tanh(x) = (e^x - e^(-x)) / (e^x + e^(-x))
     * Range: (-1, 1). Zero-centered.
     */
    final class Tanh implements ActivationFunction {

        @Override
        public double forward(double x) {
            if (x >= 0) {
                double exp2x = Math.exp(2 * x);
                return (exp2x - 1) / (exp2x + 1);
            } else {
                double exp2x = Math.exp(2 * x);
                return (exp2x - 1) / (exp2x + 1);
            }
        }

        @Override
        public double backward(double x) {
            double t = forward(x);
            return 1.0 - t * t;
        }

        @Override
        public String getName() {
            return "Tanh";
        }
    }

    // --- Implementation: ReLU ---

    /**
     * Rectified Linear Unit: f(x) = max(0, x)
     * Derivative: f'(x) = 1 if x > 0 else 0
     */
    final class ReLU implements ActivationFunction {

        @Override
        public double forward(double x) {
            return Math.max(0.0, x);
        }

        @Override
        public double backward(double x) {
            return x > 0.0 ? 1.0 : 0.0;
        }

        @Override
        public String getName() {
            return "ReLU";
        }
    }

    // --- Implementation: LeakyReLU ---

    /**
     * Leaky ReLU: f(x) = x if x > 0 else α·x
     * Typically α = 0.01. Addresses dead ReLU problem.
     */
    final class LeakyReLU implements ActivationFunction {

        private final double alpha;

        /**
         * @param alpha slope for negative inputs (typically 0.01)
         */
        public LeakyReLU(double alpha) {
            this.alpha = alpha;
        }

        public LeakyReLU() {
            this(0.01);
        }

        @Override
        public double forward(double x) {
            return x > 0.0 ? x : alpha * x;
        }

        @Override
        public double backward(double x) {
            return x > 0.0 ? 1.0 : alpha;
        }

        @Override
        public String getName() {
            return "LeakyReLU(α=" + alpha + ")";
        }
    }

    // --- Implementation: GELU ---

    /**
     * Gaussian Error Linear Unit: f(x) = x · Φ(x)
     * where Φ(x) = 0.5 * (1 + erf(x / sqrt(2))).
     * Uses the tanh approximation for computational efficiency.
     */
    final class GELU implements ActivationFunction {

        private static final double SQRT_2_OVER_PI = Math.sqrt(2.0 / Math.PI);
        private static final double APPROX_CONST = 0.044715;

        /**
         * Approximate GELU using tanh approximation:
         * f(x) ≈ 0.5 * x * (1 + tanh(sqrt(2/π) * (x + 0.044715 * x³)))
         */
        @Override
        public double forward(double x) {
            double x3 = x * x * x;
            double inner = SQRT_2_OVER_PI * (x + APPROX_CONST * x3);
            return 0.5 * x * (1.0 + Math.tanh(inner));
        }

        /**
         * Exact GELU derivative:
         * f'(x) = Φ(x) + x · φ(x)
         * where φ(x) = e^(-x²/2) / sqrt(2π)
         */
        @Override
        public double backward(double x) {
            double phi = Math.exp(-0.5 * x * x) / Math.sqrt(2.0 * Math.PI);
            double Phi = 0.5 * (1.0 + erf(x / Math.sqrt(2.0)));
            return Phi + x * phi;
        }

        /**
         * Error function approximation (Abramowitz and Stegun).
         */
        private static double erf(double x) {
            if (Math.abs(x) > 6.0) {
                return x > 0 ? 1.0 : -1.0;
            }
            double t = 1.0 / (1.0 + 0.3275911 * Math.abs(x));
            double poly = t * (0.254829592
                + t * (-0.284496736
                + t * (1.421413741
                + t * (-1.453152027
                + t * 1.061405429))));
            double result = 1.0 - poly * Math.exp(-x * x);
            return x >= 0 ? result : -result;
        }

        @Override
        public String getName() {
            return "GELU";
        }
    }

    // --- Implementation: Swish ---

    /**
     * Swish (SiLU): f(x) = x · σ(x)
     * Self-gated activation discovered by Google Brain.
     * Derivative: f'(x) = σ(x) + x · σ(x) · (1 - σ(x))
     */
    final class Swish implements ActivationFunction {

        private final Sigmoid sigmoid = new Sigmoid();

        @Override
        public double forward(double x) {
            return x * sigmoid.forward(x);
        }

        @Override
        public double backward(double x) {
            double sig = sigmoid.forward(x);
            return sig + x * sig * (1.0 - sig);
        }

        @Override
        public String getName() {
            return "Swish";
        }
    }
}
```

### Test Harness

```java
package com.deeplearning.activation;

import java.util.Arrays;

/**
 * Comprehensive test for all activation functions.
 * Tests forward outputs, backward derivatives, numerical stability,
 * and array operations.
 */
public class ActivationFunctionTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testSigmoid();
        testTanh();
        testReLU();
        testLeakyReLU();
        testGELU();
        testSwish();
        testNumericalStability();
        testArrayOperations();
        testDerivativesNumerically();
        System.out.printf("%n=== Results: %d passed, %d failed ===%n", passed, failed);
    }

    static void assertTest(boolean condition, String name) {
        if (condition) {
            passed++;
            System.out.printf("[PASS] %s%n", name);
        } else {
            failed++;
            System.err.printf("[FAIL] %s%n", name);
        }
    }

    static void testSigmoid() {
        ActivationFunction sig = new ActivationFunction.Sigmoid();
        assertTest(Math.abs(sig.forward(0.0) - 0.5) < 1e-9, "Sigmoid(0) = 0.5");
        assertTest(sig.forward(100) < 1e-15, "Sigmoid(100) ≈ 0");
        assertTest(Math.abs(sig.forward(-100) - 1.0) < 1e-9, "Sigmoid(-100) ≈ 1");
        assertTest(Math.abs(sig.forward(-1.0) - 0.26894142137) < 1e-8, "Sigmoid(-1) ≈ 0.2689");
        assertTest(Math.abs(sig.forward(1.0) - 0.73105857863) < 1e-8, "Sigmoid(1) ≈ 0.7311");
        // Derivative
        assertTest(Math.abs(sig.backward(0.0) - 0.25) < 1e-9, "Sigmoid'(0) = 0.25");
    }

    static void testTanh() {
        ActivationFunction tanh = new ActivationFunction.Tanh();
        assertTest(Math.abs(tanh.forward(0.0)) < 1e-15, "Tanh(0) = 0");
        assertTest(Math.abs(tanh.forward(1.0) - Math.tanh(1.0)) < 1e-9, "Tanh(1) = tanh(1)");
        assertTest(Math.abs(tanh.forward(-1.0) - Math.tanh(-1.0)) < 1e-9, "Tanh(-1) = tanh(-1)");
        assertTest(tanh.backward(0.0) == 1.0, "Tanh'(0) = 1");
    }

    static void testReLU() {
        ActivationFunction relu = new ActivationFunction.ReLU();
        assertTest(relu.forward(5.0) == 5.0, "ReLU(5) = 5");
        assertTest(relu.forward(-5.0) == 0.0, "ReLU(-5) = 0");
        assertTest(relu.forward(0.0) == 0.0, "ReLU(0) = 0");
        assertTest(relu.backward(5.0) == 1.0, "ReLU'(5) = 1");
        assertTest(relu.backward(-5.0) == 0.0, "ReLU'(-5) = 0");
        assertTest(relu.backward(0.0) == 0.0, "ReLU'(0) = 0 (subgradient)");
    }

    static void testLeakyReLU() {
        ActivationFunction leaky = new ActivationFunction.LeakyReLU(0.01);
        assertTest(leaky.forward(5.0) == 5.0, "LeakyReLU(5) = 5");
        assertTest(Math.abs(leaky.forward(-5.0) - (-0.05)) < 1e-9, "LeakyReLU(-5) = -0.05");
        assertTest(leaky.backward(5.0) == 1.0, "LeakyReLU'(5) = 1");
        assertTest(leaky.backward(-5.0) == 0.01, "LeakyReLU'(-5) = 0.01");
    }

    static void testGELU() {
        ActivationFunction gelu = new ActivationFunction.GELU();
        assertTest(Math.abs(gelu.forward(0.0)) < 1e-9, "GELU(0) ≈ 0");
        assertTest(gelu.forward(10.0) > 9.999, "GELU(10) ≈ 10");
        assertTest(Math.abs(gelu.forward(1.0) - 0.8413) < 1e-2, "GELU(1) ≈ 0.8413");
        assertTest(gelu.forward(-10.0) < 1e-15, "GELU(-10) ≈ 0");
        // Derivative range check
        double d0 = gelu.backward(0.0);
        assertTest(d0 >= 0.4 && d0 <= 0.6, "GELU'(0) ≈ 0.5");
    }

    static void testSwish() {
        ActivationFunction swish = new ActivationFunction.Swish();
        assertTest(Math.abs(swish.forward(0.0)) < 1e-15, "Swish(0) = 0");
        assertTest(Math.abs(swish.forward(1.0) - 0.73105857863) < 1e-8, "Swish(1) ≈ 0.7311");
        assertTest(Math.abs(swish.forward(-1.0) - (-0.26894142137)) < 1e-8, "Swish(-1) ≈ -0.2689");
        // Derivative
        double d = swish.backward(0.0);
        assertTest(Math.abs(d - 0.5) < 1e-9, "Swish'(0) = 0.5");
    }

    static void testNumericalStability() {
        ActivationFunction sig = new ActivationFunction.Sigmoid();
        ActivationFunction relu = new ActivationFunction.ReLU();
        // No overflow for extreme values
        double hugePos = sig.forward(800.0);
        double hugeNeg = sig.forward(-800.0);
        assertTest(!Double.isNaN(hugePos) && !Double.isInfinite(hugePos), "Sigmoid(+800) stable");
        assertTest(!Double.isNaN(hugeNeg) && !Double.isInfinite(hugeNeg), "Sigmoid(-800) stable");
        assertTest(relu.forward(Double.MAX_VALUE) == Double.MAX_VALUE, "ReLU(MAX_VALUE) stable");
        assertTest(relu.forward(Double.NEGATIVE_INFINITY) == 0.0, "ReLU(-Inf) = 0");
    }

    static void testArrayOperations() {
        ActivationFunction relu = new ActivationFunction.ReLU();
        double[] input = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] fwd = new double[5];
        double[] bwd = new double[5];
        relu.forward(input, fwd);
        relu.backward(input, bwd);
        assertTest(fwd[0] == 0.0 && fwd[4] == 2.0, "ReLU array forward");
        assertTest(bwd[0] == 0.0 && bwd[4] == 1.0, "ReLU array backward");
    }

    static void testDerivativesNumerically() {
        ActivationFunction[] fns = {
            new ActivationFunction.Sigmoid(),
            new ActivationFunction.Tanh(),
            new ActivationFunction.ReLU(),
            new ActivationFunction.LeakyReLU(0.01),
            new ActivationFunction.GELU(),
            new ActivationFunction.Swish()
        };
        double eps = 1e-8;
        double[] testPoints = {-3.0, -1.0, -0.1, 0.0, 0.1, 1.0, 3.0};
        for (ActivationFunction fn : fns) {
            boolean allOk = true;
            for (double x : testPoints) {
                double analytical = fn.backward(x);
                double numerical = (fn.forward(x + eps) - fn.forward(x - eps)) / (2 * eps);
                if (Math.abs(analytical - numerical) > 1e-6 && Math.abs(analytical - numerical) / Math.max(1, Math.abs(numerical)) > 1e-4) {
                    allOk = false;
                }
            }
            assertTest(allOk, fn.getName() + " numerical gradient check");
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Forward (single element):** $O(1)$ for all activation functions.

**Forward (array of size n):** $O(n)$ — element-wise, trivially parallelizable.

**Backward (single element):** $O(1)$.

| Activation | Forward Ops | Backward Ops |
|-----------|-------------|--------------|
| Sigmoid | 1 exp, 4 arithmetic | 1 sigmoid (cached) |
| Tanh | 1 exp, 5 arithmetic | 1 tanh (cached) |
| ReLU | 1 comparison | 1 comparison |
| Leaky ReLU | 1 comparison, 1 multiply | 1 comparison |
| GELU | 1 tanh, ~20 arithmetic | 1 erf, 1 exp, ~10 arithmetic |
| Swish | 1 sigmoid, 1 multiply | 1 sigmoid, ~5 arithmetic |

### Space Complexity

$O(1)$ for all — no dynamic allocation during forward/backward.

---

## Follow-Up Questions

### Q1: Why is ReLU preferred over sigmoid in deep networks?

**Answer:** Three main reasons:
1. **Non-saturating gradient:** For $x > 0$, gradient = 1, preventing vanishing gradients.
2. **Computational efficiency:** Simple `max(0, x)` vs. expensive exponentiation.
3. **Sparsity:** ReLU naturally produces sparse activations (neurons with $x \leq 0$ output exactly 0), which can improve regularization and computational efficiency.

### Q2: What is the "dying ReLU" problem and how do we fix it?

**Answer:** During training, a ReLU neuron can get stuck in the region where $x \leq 0$ for all inputs, making its gradient permanently 0. The neuron never recovers because no gradient flows through it.

**Fixes:**
1. **Leaky ReLU** ($\alpha = 0.01$): Small negative slope allows gradient flow.
2. **PReLU** (Parametric ReLU): Learn the negative slope via backprop.
3. **ELU** (Exponential Linear Unit): $\text{ELU}(x) = \max(0, x) + \min(0, \alpha(e^x - 1))$.
4. **Smaller learning rates:** Prevents large weight updates that push neurons into the dead region.
5. **Proper initialization:** He initialization keeps activations in a healthy range.

### Q3: Derive the GELU derivative.

**Answer:** Given $f(x) = x \cdot \Phi(x)$ where $\Phi(x) = \frac{1}{2}[1 + \text{erf}(x/\sqrt{2})]$:

$$f'(x) = 1 \cdot \Phi(x) + x \cdot \Phi'(x)$$

$\Phi'(x) = \phi(x) = \frac{1}{\sqrt{2\pi}} e^{-x^2/2}$ (the standard normal PDF).

Thus:
$$f'(x) = \Phi(x) + x \cdot \phi(x)$$

### Q4: Compare GELU and Swish. When would you use each?

**Answer:**

| Property | GELU | Swish |
|----------|------|-------|
| Formula | $x \cdot \Phi(x)$ | $x \cdot \sigma(x)$ |
| Differentiability | Smooth | Smooth |
| Negative region | Yes (small) | Yes (small) |
| Computational cost | Higher (erf) | Lower (sigmoid) |
| Used in | BERT, GPT, Transformers | EfficientNet, MobileNet |

**Guidance:** GELU is standard in NLP Transformers (BERT, GPT). Swish is common in vision models (EfficientNet). For new projects, start with GELU for NLP and ReLU or Swish for vision.

### Q5: Show that ReLU is not differentiable at x = 0. What subgradient do we use?

**Answer:** The left derivative at 0 is $\lim_{h \to 0^-} \frac{\max(0, 0+h) - 0}{h} = \lim_{h \to 0^-} \frac{0}{h} = 0$. The right derivative is $\lim_{h \to 0^+} \frac{\max(0, 0+h) - 0}{h} = \lim_{h \to 0^+} \frac{h}{h} = 1$. Since $0 \neq 1$, the function is not differentiable at 0.

The **subgradient** (set of subderivatives) at 0 is $[0, 1]$. In practice, frameworks like TensorFlow and PyTorch define the gradient to be 0 at 0 (matching the left derivative). Our implementation follows this convention.

### Q6: How would you implement the ELU activation?

**Answer:**

```java
final class ELU implements ActivationFunction {
    private final double alpha;

    public ELU(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public double forward(double x) {
        return x >= 0 ? x : alpha * (Math.exp(x) - 1.0);
    }

    @Override
    public double backward(double x) {
        return x >= 0 ? 1.0 : alpha * Math.exp(x);
    }

    @Override
    public String getName() {
        return "ELU(α=" + alpha + ")";
    }
}
```

ELU shares ReLU's positive region benefits but has a smooth negative region with non-zero gradient, helping with dead neuron recovery.

### Q7: What is the "exploding gradient" problem in the context of activation functions?

**Answer:** Exploding gradients occur when gradients grow exponentially as they're backpropagated through deep networks. This can happen with:
- **ReLU:** The unconstrained positive activation can produce large outputs, leading to large gradients.
- **High learning rates:** Large weight updates compound.

**Symptoms:** NaN loss values, extreme parameter updates, training instability.

**Mitigations:**
- Gradient clipping: $\mathbf{g} \leftarrow \mathbf{g} \cdot \min(1, \frac{\text{threshold}}{\|\mathbf{g}\|})$
- Batch normalization
- Proper weight initialization (Xavier/He)
- Lower learning rates with learning rate schedules

---

## Test Cases

| Test Case | Activation | Input | Expected Output |
|-----------|-----------|-------|-----------------|
| TC-01 | Sigmoid | 0 | 0.5 |
| TC-02 | Sigmoid | 100 | ≈ 1.0 |
| TC-03 | Sigmoid | -100 | ≈ 0.0 |
| TC-04 | Tanh | 0 | 0.0 |
| TC-05 | Tanh | 10 | ≈ 1.0 |
| TC-06 | Tanh | -10 | ≈ -1.0 |
| TC-07 | ReLU | 5 | 5.0 |
| TC-08 | ReLU | -3 | 0.0 |
| TC-09 | ReLU | 0 | 0.0 |
| TC-10 | LeakyReLU(0.01) | -5 | -0.05 |
| TC-11 | LeakyReLU(0.01) | 5 | 5.0 |
| TC-12 | GELU | 0 | ≈ 0.0 |
| TC-13 | GELU | 1 | ≈ 0.8413 |
| TC-14 | GELU | -10 | ≈ 0.0 |
| TC-15 | Swish | 0 | 0.0 |
| TC-16 | Swish | 1 | ≈ 0.7311 |
| TC-17 | Swish | -1 | ≈ -0.2689 |
| TC-18 | All | Large positive | No overflow/NaN |
| TC-19 | All | Large negative | No overflow/NaN |
| TC-20 | Numerical gradient | [-3, -1, 0, 1, 3] | Analytical ≈ Numerical |

---

## Key Takeaways

- Activation functions are the source of non-linearity in neural networks.
- Sigmoid and Tanh suffer from saturation → vanishing gradients in deep networks.
- ReLU is the default choice for most architectures due to computational efficiency and non-saturating behavior.
- GELU and Swish are modern alternatives with smooth differentiability, standard in Transformers.
- Numerical stability is critical: carefully handle large positive/negative inputs.
- Subgradients handle non-differentiable points (ReLU at 0) in practice.
