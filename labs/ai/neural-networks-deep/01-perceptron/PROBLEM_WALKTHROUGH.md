# Perceptron Learning Algorithm

## Problem Statement

**Problem:** Implement the classic perceptron algorithm for binary classification of linearly separable data.

You are given a dataset $D = \{(\mathbf{x}^{(i)}, y^{(i)})\}_{i=1}^{N}$ where each $\mathbf{x}^{(i)} \in \mathbb{R}^n$ is an input feature vector and $y^{(i)} \in \{-1, +1\}$ is the binary label. Implement the perceptron learning algorithm that finds a weight vector $\mathbf{w} \in \mathbb{R}^n$ and bias $b \in \mathbb{R}$ defining a decision boundary:

$$f(\mathbf{x}) = \text{sign}(\mathbf{w} \cdot \mathbf{x} + b)$$

The algorithm should:
1. Initialize weights and bias to small random values or zeros.
2. For each misclassified sample, update the weights using the perceptron rule.
3. Iterate until convergence (no misclassifications) or a maximum number of epochs.
4. Return the learned weight vector and bias.

**Example:**
```
Input:  X = [[1, 2], [2, 3], [3, 1], [4, 2]]  (4 samples, 2 features)
        Y = [1, 1, -1, -1]
Output: w = [0.5, -0.8], b = 0.2
        (correctly classifies all points)
```

**Constraints:**
- $1 \leq N \leq 10^5$ (number of samples)
- $1 \leq n \leq 10^3$ (feature dimension)
- Data is guaranteed to be linearly separable for convergence test.

---

## Step-by-Step Solution Walkthrough

### 1. Understanding the Perceptron Model

The perceptron is the simplest form of a neural network — a single-layer linear classifier. The decision function is:

$$\hat{y} = \text{sign}(\mathbf{w} \cdot \mathbf{x} + b) = \begin{cases} +1 & \text{if } \mathbf{w} \cdot \mathbf{x} + b > 0 \\ -1 & \text{otherwise} \end{cases}$$

The bias $b$ can be absorbed into $\mathbf{w}$ by augmenting $\mathbf{x}$ with a constant 1 dimension: $\mathbf{x}' = [1, x_1, x_2, ..., x_n]$, $\mathbf{w}' = [b, w_1, ..., w_n]$, giving $\hat{y} = \text{sign}(\mathbf{w}' \cdot \mathbf{x}')$.

**Geometric interpretation:** The decision boundary $\mathbf{w} \cdot \mathbf{x} + b = 0$ is a hyperplane in $\mathbb{R}^n$. The weight vector $\mathbf{w}$ is normal to this hyperplane.

### 2. The Perceptron Learning Rule

The perceptron updates weights only when a sample is misclassified. For a misclassified sample $(\mathbf{x}, y)$ where $\hat{y} \neq y$:

$$\mathbf{w} \leftarrow \mathbf{w} + \eta \cdot y \cdot \mathbf{x}$$
$$b \leftarrow b + \eta \cdot y$$

where $\eta$ is the learning rate (typically 1).

**Intuition behind the update:**
- If the true label is $y = +1$ but the prediction is $-1$, then $\mathbf{w} \cdot \mathbf{x} + b \leq 0$. Adding $\eta \cdot \mathbf{x}$ to $\mathbf{w}$ increases the dot product, moving the decision boundary toward correctly classifying this point.
- If the true label is $y = -1$ but the prediction is $+1$, then $\mathbf{w} \cdot \mathbf{x} + b > 0$. Subtracting $\mathbf{x}$ from $\mathbf{w}$ decreases the dot product.

### 3. Convergence Theorem

**Perceptron Convergence Theorem** (Rosenblatt, 1958): If the training data is linearly separable, the perceptron algorithm will converge to a solution that correctly classifies all training samples in a finite number of iterations.

**Proof sketch:**
1. Let $\mathbf{w}^*$ be a unit vector that perfectly separates the data, so $y^{(i)}(\mathbf{w}^* \cdot \mathbf{x}^{(i)}) \geq \gamma > 0$ for all $i$.
2. Define $\gamma = \min_i y^{(i)}(\mathbf{w}^* \cdot \mathbf{x}^{(i)})$.
3. Let $R = \max_i \|\mathbf{x}^{(i)}\|$.
4. After $k$ updates, the cosine similarity between $\mathbf{w}^{(k)}$ and $\mathbf{w}^*$ satisfies:
   $$\cos(\theta_k) \geq \frac{k\gamma}{\sqrt{k^2 R^2}} = \frac{k\gamma}{kR} = \frac{\gamma}{R}$$
5. Since cosine is at most 1, $k \leq \left(\frac{R}{\gamma}\right)^2$.

Thus the number of mistakes is bounded by $(R/\gamma)^2$.

### 4. Algorithm Pseudocode

```
Algorithm: Perceptron Learning
Input: X ∈ ℝ^{N×n}, y ∈ {-1,+1}^N, η ∈ ℝ^+, max_epochs ∈ ℕ
Output: w ∈ ℝ^n, b ∈ ℝ

1. Initialize w ← [0, 0, ..., 0], b ← 0
2. For epoch = 1 to max_epochs:
3.   misclassified ← 0
4.   For each (x_i, y_i) in (X, y):
5.     activation ← w · x_i + b
6.     prediction ← sign(activation)
7.     If y_i * activation ≤ 0:           // misclassified
8.       w ← w + η * y_i * x_i
9.       b ← b + η * y_i
10.      misclassified ← misclassified + 1
11.  If misclassified == 0:
12.    break                              // converged
13. Return w, b
```

### 5. Limitations of the Perceptron

1. **Linear separability requirement:** The perceptron only converges if the data is linearly separable. For non-separable data, it will oscillate forever.

2. **No probabilistic output:** The perceptron outputs a hard class label, not class probabilities.

3. **Single layer limitation:** A single perceptron can only learn linear decision boundaries. XOR is the classic counter-example.

4. **Convergence speed:** Convergence can be slow for data where classes are close together (small margin $\gamma$).

---

## Java Implementation

```java
package com.deeplearning.perceptron;

import java.util.Arrays;
import java.util.Random;

/**
 * Implementation of the classic Perceptron Learning Algorithm for binary
 * classification of linearly separable data.
 * 
 * <p>The perceptron is a linear classifier that finds a decision boundary
 * defined by weights {@code w} and bias {@code b} such that:
 * {@code f(x) = sign(w·x + b)}.</p>
 * 
 * <p>This implementation uses the perceptron update rule and detects
 * convergence when all training samples are correctly classified.</p>
 */
public class Perceptron {

    private final double[] weights;
    private double bias;
    private final double learningRate;
    private final int maxEpochs;
    private int epochsTrained;
    private int totalUpdates;
    private final Random random;

    /**
     * Constructs a perceptron with the specified parameters.
     *
     * @param featureCount number of input features (n)
     * @param learningRate step size for weight updates (eta)
     * @param maxEpochs    maximum number of training epochs
     * @throws IllegalArgumentException if featureCount <= 0, learningRate <= 0,
     *                                  or maxEpochs <= 0
     */
    public Perceptron(int featureCount, double learningRate, int maxEpochs) {
        if (featureCount <= 0) {
            throw new IllegalArgumentException("Feature count must be positive, got: " + featureCount);
        }
        if (learningRate <= 0) {
            throw new IllegalArgumentException("Learning rate must be positive, got: " + learningRate);
        }
        if (maxEpochs <= 0) {
            throw new IllegalArgumentException("Max epochs must be positive, got: " + maxEpochs);
        }
        this.weights = new double[featureCount];
        this.bias = 0.0;
        this.learningRate = learningRate;
        this.maxEpochs = maxEpochs;
        this.epochsTrained = 0;
        this.totalUpdates = 0;
        this.random = new Random(42);
        initializeWeights();
    }

    /**
     * Initializes weights to small random values in [-0.01, 0.01].
     */
    private void initializeWeights() {
        for (int i = 0; i < weights.length; i++) {
            weights[i] = (random.nextDouble() - 0.5) * 0.02;
        }
        this.bias = (random.nextDouble() - 0.5) * 0.02;
    }

    /**
     * Trains the perceptron on the provided data until convergence or
     * maxEpochs is reached.
     *
     * @param features training data matrix of shape [N][n]
     * @param labels   training labels of length N, each element in {-1, +1}
     * @return number of epochs trained
     * @throws IllegalArgumentException if data dimensions are inconsistent
     */
    public int train(double[][] features, int[] labels) {
        validateData(features, labels);
        int n = features.length;

        this.epochsTrained = 0;

        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            int misclassified = 0;

            for (int i = 0; i < n; i++) {
                double activation = dotProduct(weights, features[i]) + bias;
                int prediction = sign(activation);

                if (labels[i] * activation <= 0) {
                    // Update rule for misclassified sample
                    for (int j = 0; j < weights.length; j++) {
                        weights[j] += learningRate * labels[i] * features[i][j];
                    }
                    bias += learningRate * labels[i];
                    misclassified++;
                    totalUpdates++;
                }
            }

            epochsTrained++;

            if (misclassified == 0) {
                break; // Converged
            }
        }

        return epochsTrained;
    }

    /**
     * Predicts the class label for a single input vector.
     *
     * @param input input feature vector of length n
     * @return predicted label in {-1, +1}
     */
    public int predict(double[] input) {
        if (input.length != weights.length) {
            throw new IllegalArgumentException(
                "Input length " + input.length + " does not match weights length " + weights.length);
        }
        double activation = dotProduct(weights, input) + bias;
        return sign(activation);
    }

    /**
     * Predicts class labels for multiple input vectors.
     *
     * @param inputs feature matrix of shape [M][n]
     * @return array of predicted labels, each in {-1, +1}
     */
    public int[] predict(double[][] inputs) {
        int[] predictions = new int[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            predictions[i] = predict(inputs[i]);
        }
        return predictions;
    }

    /**
     * Returns the linear activation (pre-sign) value.
     * Used internally and for debugging / margin analysis.
     *
     * @param input input feature vector
     * @return activation value w·x + b
     */
    public double getActivation(double[] input) {
        return dotProduct(weights, input) + bias;
    }

    // --- Getters ---

    public double[] getWeights() {
        return Arrays.copyOf(weights, weights.length);
    }

    public double getBias() {
        return bias;
    }

    public int getEpochsTrained() {
        return epochsTrained;
    }

    public int getTotalUpdates() {
        return totalUpdates;
    }

    // --- Private Helpers ---

    private void validateData(double[][] features, int[] labels) {
        if (features == null || labels == null) {
            throw new IllegalArgumentException("Features and labels must not be null");
        }
        if (features.length != labels.length) {
            throw new IllegalArgumentException(
                "Feature count " + features.length + " does not match label count " + labels.length);
        }
        if (features.length == 0) {
            throw new IllegalArgumentException("Training data must not be empty");
        }
        for (int i = 0; i < features.length; i++) {
            if (features[i] == null) {
                throw new IllegalArgumentException("Feature row " + i + " is null");
            }
            if (features[i].length != weights.length) {
                throw new IllegalArgumentException(
                    "Feature row " + i + " length " + features[i].length
                    + " does not match expected " + weights.length);
            }
        }
    }

    /**
     * Computes the dot product of two vectors.
     */
    private static double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    /**
     * Sign function: returns +1 for positive input, -1 otherwise.
     */
    private static int sign(double value) {
        return value > 0.0 ? 1 : -1;
    }

    /**
     * Returns a string representation of the learned decision boundary.
     */
    @Override
    public String toString() {
        return String.format("Perceptron{w=[%s], b=%.4f, epochs=%d, updates=%d}",
            Arrays.toString(weights), bias, epochsTrained, totalUpdates);
    }
}
```

### Test Harness

```java
package com.deeplearning.perceptron;

/**
 * Test harness for the Perceptron class.
 */
public class PerceptronTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testAND();
        testOR();
        testSimpleLinear();
        testConvergence();
        testNonSeparableHandling();
        testEdgeCases();
        System.out.printf("\n=== Results: %d passed, %d failed ===%n", passed, failed);
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

    static void testAND() {
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        int[] y = {-1, -1, -1, 1};
        Perceptron p = new Perceptron(2, 0.1, 100);
        p.train(X, y);
        int[] preds = p.predict(X);
        boolean ok = true;
        for (int i = 0; i < y.length; i++) {
            if (preds[i] != y[i]) ok = false;
        }
        assertTest(ok, "AND gate linearly separable");
    }

    static void testOR() {
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        int[] y = {-1, 1, 1, 1};
        Perceptron p = new Perceptron(2, 0.1, 100);
        p.train(X, y);
        int[] preds = p.predict(X);
        boolean ok = true;
        for (int i = 0; i < y.length; i++) {
            if (preds[i] != y[i]) ok = false;
        }
        assertTest(ok, "OR gate linearly separable");
    }

    static void testSimpleLinear() {
        // 2D points separated by a line: x1 > x2 => +1
        double[][] X = {{2, 1}, {3, 2}, {5, 4}, {1, 2}, {2, 3}, {4, 5}};
        int[] y = {1, 1, 1, -1, -1, -1};
        Perceptron p = new Perceptron(2, 0.5, 200);
        int epochs = p.train(X, y);
        int[] preds = p.predict(X);
        boolean ok = true;
        for (int i = 0; i < y.length; i++) {
            if (preds[i] != y[i]) ok = false;
        }
        assertTest(ok, "Simple linear separation converged in " + epochs + " epochs");
    }

    static void testConvergence() {
        // Larger random linearly separable dataset
        int n = 100;
        int dim = 5;
        double[][] X = new double[n][dim];
        int[] y = new int[n];
        double[] trueW = {1.0, -0.5, 0.8, -0.2, 0.3};
        double trueB = 0.1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < dim; j++) {
                X[i][j] = Math.random() * 10 - 5;
            }
            double act = dotProduct(trueW, X[i]) + trueB;
            y[i] = act > 0 ? 1 : -1;
        }
        Perceptron p = new Perceptron(dim, 0.1, 1000);
        int epochs = p.train(X, y);
        int[] preds = p.predict(X);
        int correct = 0;
        for (int i = 0; i < n; i++) {
            if (preds[i] == y[i]) correct++;
        }
        assertTest(correct == n,
            "Convergence on separable " + n + "-point dataset in " + epochs + " epochs (100% accuracy)");
    }

    static void testNonSeparableHandling() {
        // XOR is not linearly separable
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        int[] y = {-1, 1, 1, -1};
        Perceptron p = new Perceptron(2, 0.1, 500);
        int epochs = p.train(X, y);
        int[] preds = p.predict(X);
        int correct = 0;
        for (int i = 0; i < y.length; i++) {
            if (preds[i] == y[i]) correct++;
        }
        // XOR is non-separable; perceptron will not get 100%
        assertTest(correct < 4, "XOR non-separable: perceptron cannot achieve 100% (got " + correct + "/4)");
    }

    static void testEdgeCases() {
        // Single sample
        double[][] X = {{1.0, 2.0}};
        int[] y = {1};
        Perceptron p = new Perceptron(2, 0.1, 10);
        p.train(X, y);
        assertTest(p.predict(X[0]) == 1, "Single sample training");

        // Zero initialization edge
        boolean threw = false;
        try {
            new Perceptron(0, 0.1, 10);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        assertTest(threw, "Rejects zero feature count");
    }

    private static double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Training (one epoch):** $O(N \cdot n)$ where $N$ is the number of samples and $n$ is the number of features. Each sample requires computing a dot product ($O(n)$) and, if misclassified, updating $n$ weights ($O(n)$).

**Total training:** $O(E \cdot N \cdot n)$ where $E$ is the number of epochs. In the worst case, $E = (R/\gamma)^2$ (per convergence theorem bound).

**Prediction (single sample):** $O(n)$ — a dot product and comparison.

### Space Complexity

$O(n)$ for storing the weight vector and bias. The training data is not stored by the model after training (online learning compatible).

---

## Follow-Up Questions

### Q1: How would you modify the perceptron for non-linearly separable data?

**Answer:** For non-separable data, we can use the **pocket algorithm**:
1. Keep a "pocket" copy of the best weights seen so far (lowest misclassification count).
2. Run the standard perceptron algorithm.
3. After each epoch, if the current weights achieve fewer misclassifications than the pocket, replace the pocket.
4. Return the pocket weights at the end.

This doesn't solve non-linearity but provides the best linear approximation.

### Q2: What is the kernel perceptron and how does it handle non-linear decision boundaries?

**Answer:** The kernel perceptron uses the **kernel trick** to implicitly map inputs to a higher-dimensional feature space without computing the mapping explicitly. The dual formulation expresses weights as $\mathbf{w} = \sum_i \alpha_i y^{(i)} \mathbf{x}^{(i)}$, where $\alpha_i$ is the number of times $\mathbf{x}^{(i)}$ was misclassified. Predictions become:

$$f(\mathbf{x}) = \text{sign}\left(\sum_i \alpha_i y^{(i)} K(\mathbf{x}^{(i)}, \mathbf{x}) + b\right)$$

Common kernels: polynomial kernel $K(\mathbf{x}, \mathbf{z}) = (\mathbf{x} \cdot \mathbf{z} + c)^d$, RBF kernel $K(\mathbf{x}, \mathbf{z}) = \exp(-\gamma\|\mathbf{x} - \mathbf{z}\|^2)$.

### Q3: Why does the perceptron use sign() for activation rather than sigmoid?

**Answer:** The perceptron was originally designed as a biological neuron model (McCulloch-Pitts) with a hard threshold. The sign function provides a clean decision boundary and guarantees convergence for separable data. However, it is **not differentiable**, which prevents gradient-based learning. The sigmoid activation was introduced later to enable gradient descent in multi-layer networks (backpropagation).

### Q4: How does the learning rate affect convergence?

**Answer:** For the classic perceptron with the sign activation, the learning rate $\eta$ does **not affect convergence or final weights** — it merely scales the updates. Any $\eta > 0$ converges to the same solution (assuming the same sequence of updates) because the decision boundary depends only on the **direction** of $\mathbf{w}$, not its magnitude. However, in practice (e.g., with the pocket algorithm or margin-based variants), the learning rate matters.

### Q5: What is the relationship between the perceptron and logistic regression?

**Answer:** Both are linear classifiers, but:
- **Perceptron:** Minimizes the number of misclassifications (0-1 loss). Outputs hard labels. Non-probabilistic.
- **Logistic regression:** Minimizes negative log-likelihood (cross-entropy loss). Outputs probabilities via sigmoid. Supports stochastic gradient descent with smooth gradients.

Logistic regression can be viewed as a "soft" version of the perceptron that also outputs well-calibrated probabilities.

### Q6: Implement the voted perceptron variant.

**Answer:** The voted perceptron (Freund & Schapire, 1999) keeps all intermediate weight vectors and counts how many training examples each one survived for:

```java
class VotedPerceptron {
    List<double[]> weightVectors = new ArrayList<>();
    List<Double> biases = new ArrayList<>();
    List<Integer> counts = new ArrayList<>();
    int weightIndex = 0;
    double[] w;
    double b;
    int survivalCount = 0;

    public VotedPerceptron(int dim) {
        w = new double[dim];
        b = 0;
    }

    public void train(double[][] X, int[] y, int epochs) {
        for (int ep = 0; ep < epochs; ep++) {
            for (int i = 0; i < X.length; i++) {
                if (y[i] * (dot(w, X[i]) + b) <= 0) {
                    weightVectors.add(w.clone());
                    biases.add(b);
                    counts.add(survivalCount);
                    for (int j = 0; j < w.length; j++)
                        w[j] += y[i] * X[i][j];
                    b += y[i];
                    survivalCount = 1;
                } else {
                    survivalCount++;
                }
            }
        }
        weightVectors.add(w.clone());
        biases.add(b);
        counts.add(survivalCount);
    }

    public int predict(double[] x) {
        double sum = 0;
        for (int k = 0; k < weightVectors.size(); k++)
            sum += counts.get(k) * sign(dot(weightVectors.get(k), x) + biases.get(k));
        return sum > 0 ? 1 : -1;
    }
}
```

### Q7: How would you extend the perceptron to multi-class classification?

**Answer:** Use **one-vs-all** (OvA) or **one-vs-one** (OvO) strategies:

- **OvA:** Train $K$ perceptrons, one per class. For class $c$, relabel $y^{(i)} = +1$ if class $c$ else $-1$. Predict the class whose perceptron gives the highest activation.
- **OvO:** Train $K(K-1)/2$ binary perceptrons, one per pair of classes. Use majority voting.

### Q8: Why can't a single perceptron learn XOR?

**Answer:** XOR is not linearly separable — there is no single line (in 2D) or hyperplane (in general) that separates the four points into two classes correctly. The XOR truth table:
```
(0,0) -> -1
(0,1) -> +1
(1,0) -> +1
(1,1) -> -1
```
In 2D, the points are at the corners of a square, with opposite corners having the same label. No linear decision boundary can separate them. This limitation motivated the development of multi-layer neural networks.

---

## Test Cases

| Test Case | Description | Input | Expected |
|-----------|-------------|-------|----------|
| TC-01 | AND gate | X=[[0,0],[0,1],[1,0],[1,1]], y=[-1,-1,-1,1] | 100% accuracy |
| TC-02 | OR gate | X=[[0,0],[0,1],[1,0],[1,1]], y=[-1,1,1,1] | 100% accuracy |
| TC-03 | Linear diagonal | Points separated by x1 > x2 | 100% accuracy |
| TC-04 | High-dim separable | 5D random separable data, 100 pts | Convergence within bounds |
| TC-05 | XOR (non-separable) | XOR pattern | < 100% accuracy |
| TC-06 | Single sample | 1 sample, 2 features | Correct prediction |
| TC-07 | Zero features | featureCount=0 | IllegalArgumentException |
| TC-08 | Empty data | features.length=0 | IllegalArgumentException |
| TC-09 | Null input | features=null | IllegalArgumentException |
| TC-10 | Mismatched dims | features[0].length != featureCount | IllegalArgumentException |

## Key Takeaways

- The perceptron is a foundational algorithm in machine learning and neural networks.
- It provably converges for linearly separable data (Perceptron Convergence Theorem).
- Its limitations (linear separability, hard threshold) directly motivated the development of multi-layer networks with differentiable activations.
- Understanding the perceptron is essential before studying MLPs and deep learning.
