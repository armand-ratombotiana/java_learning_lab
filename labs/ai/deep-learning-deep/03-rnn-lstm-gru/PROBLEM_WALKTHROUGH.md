# PROBLEM WALKTHROUGH: Implement an LSTM Cell from Scratch

## Problem Statement

**Difficulty:** Hard | **Category:** Recurrent Neural Networks | **Estimated Time:** 90 minutes

Implement the forward pass of an LSTM (Long Short-Term Memory) cell from scratch in Java 21+. Your `LSTMCell` must implement all four gating mechanisms (forget, input, output, and candidate gates), maintain both hidden state and cell state, and handle variable-length sequences through the hidden and cell dimensions.

**Input:**
- `input`: A 2D array of shape `(batchSize, inputSize)` representing a single timestep's input.
- `hiddenState`: A 2D array of shape `(batchSize, hiddenSize)` representing the previous hidden state `h_{t-1}`.
- `cellState`: A 2D array of shape `(batchSize, hiddenSize)` representing the previous cell state `C_{t-1}`.

**Weights and Biases:**
- `w_i, w_f, w_o, w_c`: Input-to-hidden weight matrices for each gate, each of shape `(inputSize, hiddenSize)`.
- `u_i, u_f, u_o, u_c`: Hidden-to-hidden weight matrices for each gate, each of shape `(hiddenSize, hiddenSize)`.
- `b_i, b_f, b_o, b_c`: Bias vectors for each gate, each of length `hiddenSize`.

**Output:**
- `newHiddenState`: `h_t` of shape `(batchSize, hiddenSize)`.
- `newCellState`: `C_t` of shape `(batchSize, hiddenSize)`.
- Optionally return the gate activations for debugging.

**Constraints:**
- Implement all gates using the sigmoid (forget, input, output) and tanh (candidate, cell, hidden) activation functions.
- Do NOT use external linear algebra libraries. Implement matrix operations using nested loops.
- Properly initialize the forget gate bias to 1.0 (the "forget gate bias initialization" trick).
- Support mini-batch processing (batchSize >= 1).

**Evaluation Criteria:**
- Correct gate computation and state update following the standard LSTM equations.
- Proper handling of the cell state as a conveyor belt of information.
- Numerical stability (use appropriate activation function implementations).
- Hidden and cell state dimensions preserved across timesteps.

---

## Step-by-Step Solution Walkthrough

### 1. The LSTM Equations

The LSTM cell introduces a gating mechanism to control information flow. At each timestep `t`:

```
Forget gate:    f_t = σ(W_f * x_t + U_f * h_{t-1} + b_f)
Input gate:     i_t = σ(W_i * x_t + U_i * h_{t-1} + b_i)
Candidate:      C̃_t = tanh(W_c * x_t + U_c * h_{t-1} + b_c)
Cell state:     C_t = f_t ⊙ C_{t-1} + i_t ⊙ C̃_t
Output gate:    o_t = σ(W_o * x_t + U_o * h_{t-1} + b_o)
Hidden state:   h_t = o_t ⊙ tanh(C_t)
```

Where:
- `σ` is the sigmoid function: `σ(z) = 1 / (1 + e^{-z})`
- `tanh` is the hyperbolic tangent: `tanh(z) = (e^z - e^{-z}) / (e^z + e^{-z})`
- `⊙` denotes element-wise (Hadamard) multiplication

### 2. Intuition Behind Each Gate

**Forget Gate `f_t`:**
- Decides what information to discard from the cell state.
- Outputs values in [0, 1] — 0 means "completely forget", 1 means "completely keep."
- Uses sigmoid activation for gating.

**Input Gate `i_t`:**
- Decides which values to update in the cell state.
- Also bounded in [0, 1] via sigmoid.

**Candidate Cell State `C̃_t`:**
- Creates new candidate values to add to the cell state.
- Uses tanh activation, outputting values in [-1, 1].

**Output Gate `o_t`:**
- Decides what parts of the cell state to output as the hidden state.
- Values in [0, 1] via sigmoid.

**Cell State `C_t`:**
- The "conveyor belt" of information running through the cell.
- Information flows through with only minor linear interactions (forget + input modulation).

**Hidden State `h_t`:**
- A filtered version of the cell state, gated by the output gate.

### 3. The Vanishing Gradient Problem and How LSTM Solves It

**The problem in vanilla RNNs:**
```
h_t = tanh(W_h * x_t + U_h * h_{t-1} + b_h)
```
During backpropagation through time (BPTT):
```
∂L/∂h_{t-k} = ∂L/∂h_t * Π_{j=1}^{k} diag(tanh'(z_{t-j+1})) * U_h
```
Since `tanh'` ≤ 1, repeated multiplication leads to either vanishing (if `U_h` < 1) or exploding (if `U_h` > 1) gradients.

**How LSTM solves it:**

The cell state recurrence is:
```
C_t = f_t ⊙ C_{t-1} + i_t ⊙ C̃_t
```
The gradient of the loss w.r.t. `C_{t-1}`:
```
∂L/∂C_{t-1} = ∂L/∂C_t * f_t + ...
```
If `f_t ≈ 1` (the forget gate is "open"), the gradient flows back through the cell state almost unchanged. This additive recurrence (not multiplicative) allows gradients to flow over long sequences without vanishing.

### 4. Forget Gate Bias Initialization

A common trick is to initialize the forget gate bias to 1.0 (or a large positive value) rather than 0.0:

```
b_f = 1.0 (initialized)
```

**Why this works:**
- With `b_f = 0`, the forget gate output `σ(0) = 0.5`, meaning the cell forgets half its information at initialization.
- With `b_f = 1`, `σ(1) ≈ 0.73`, the cell retains more information at the start of training.
- This helps the network learn long-range dependencies from the beginning of training.

### 5. Algorithm Pseudocode

```
function lstmCellForward(x_t, h_{t-1}, C_{t-1}, weights, biases):
    B = batchSize, H = hiddenSize

    // For each gate, compute the linear transformation
    // Combine input and hidden contributions:

    // Gate computations (vectorized across batch)
    f_gate = sigmoid(x_t @ W_f + h_{t-1} @ U_f + b_f)  // shape: (B, H)
    i_gate = sigmoid(x_t @ W_i + h_{t-1} @ U_i + b_i)  // shape: (B, H)
    o_gate = sigmoid(x_t @ W_o + h_{t-1} @ U_o + b_o)  // shape: (B, H)
    c_tilde = tanh(x_t @ W_c + h_{t-1} @ U_c + b_c)    // shape: (B, H)

    // Cell state update
    C_t = f_gate ⊙ C_{t-1} + i_gate ⊙ c_tilde          // shape: (B, H)

    // Hidden state
    h_t = o_gate ⊙ tanh(C_t)                            // shape: (B, H)

    return (h_t, C_t)
```

### 6. Peephole Connections (Optional Variant)

Some LSTM variants add "peephole" connections where the cell state feeds into the gates:

```
f_t = σ(W_f * x_t + U_f * h_{t-1} + V_f * C_{t-1} + b_f)
i_t = σ(W_i * x_t + U_i * h_{t-1} + V_i * C_{t-1} + b_i)
o_t = σ(W_o * x_t + U_o * h_{t-1} + V_o * C_t + b_o)
```

This gives the gates direct access to the cell state, which can help with certain timing tasks. However, the standard LSTM (without peepholes) remains the most widely used variant.

---

## Java Implementation

```java
package lab03.rnn;

import java.util.Arrays;

/**
 * An LSTM (Long Short-Term Memory) cell implementation from scratch.
 * <p>
 * Implements the forward pass of a single LSTM cell with forget, input,
 * candidate, and output gates as described in Hochreiter &amp; Schmidhuber (1997)
 * with the forget gate bias initialization trick from Gers et al. (2000).
 * <p>
 * Dimensions:
 * <ul>
 *   <li>batchSize (B) — number of independent sequences processed in parallel</li>
 *   <li>inputSize (I) — dimensionality of the input at each timestep</li>
 *   <li>hiddenSize (H) — dimensionality of the hidden and cell states</li>
 * </ul>
 */
public class LSTMCell {

    private final int inputSize;
    private final int hiddenSize;

    // Weight matrices: each gate has input-to-hidden (W) and hidden-to-hidden (U)
    private double[][] wI; // (inputSize, hiddenSize)
    private double[][] uI; // (hiddenSize, hiddenSize)
    private double[] bI;

    private double[][] wF;
    private double[][] uF;
    private double[] bF;

    private double[][] wO;
    private double[][] uO;
    private double[] bO;

    private double[][] wC;
    private double[][] uC;
    private double[] bC;

    // For debugging: store the last gate activations
    private double[][] lastForgetGate;
    private double[][] lastInputGate;
    private double[][] lastOutputGate;
    private double[][] lastCandidate;

    /**
     * Constructs an LSTMCell with given dimensions and randomly initialized weights.
     *
     * @param inputSize  dimensionality of input features
     * @param hiddenSize dimensionality of hidden and cell states
     */
    public LSTMCell(int inputSize, int hiddenSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;

        // Initialize all weight matrices with Xavier/Glorot initialization
        double stdI = Math.sqrt(2.0 / (inputSize + hiddenSize));
        double stdH = Math.sqrt(2.0 / (2.0 * hiddenSize));

        wI = glorotInit(inputSize, hiddenSize, stdI);
        uI = glorotInit(hiddenSize, hiddenSize, stdH);
        bI = zeros(hiddenSize);

        wF = glorotInit(inputSize, hiddenSize, stdI);
        uF = glorotInit(hiddenSize, hiddenSize, stdH);
        bF = ones(hiddenSize); // Forget gate bias initialized to 1.0

        wO = glorotInit(inputSize, hiddenSize, stdI);
        uO = glorotInit(hiddenSize, hiddenSize, stdH);
        bO = zeros(hiddenSize);

        wC = glorotInit(inputSize, hiddenSize, stdI);
        uC = glorotInit(hiddenSize, hiddenSize, stdH);
        bC = zeros(hiddenSize);
    }

    /**
     * Performs a single timestep forward pass.
     *
     * @param input       input tensor of shape (batchSize, inputSize)
     * @param hiddenState previous hidden state h_{t-1} of shape (batchSize, hiddenSize)
     * @param cellState   previous cell state C_{t-1} of shape (batchSize, hiddenSize)
     * @return array of two tensors: [newHiddenState, newCellState] each (batchSize, hiddenSize)
     */
    public double[][][] forward(double[][] input, double[][] hiddenState, double[][] cellState) {
        int batchSize = input.length;
        validateDimensions(input, hiddenState, cellState, batchSize);

        // Compute all gates
        // forget gate: f_t = sigmoid(x_t @ W_f + h_{t-1} @ U_f + b_f)
        double[][] forgetGate = computeGate(input, hiddenState, wF, uF, bF, batchSize, true);

        // input gate: i_t = sigmoid(x_t @ W_i + h_{t-1} @ U_i + b_i)
        double[][] inputGate = computeGate(input, hiddenState, wI, uI, bI, batchSize, true);

        // output gate: o_t = sigmoid(x_t @ W_o + h_{t-1} @ U_o + b_o)
        double[][] outputGate = computeGate(input, hiddenState, wO, uO, bO, batchSize, true);

        // candidate: C̃_t = tanh(x_t @ W_c + h_{t-1} @ U_c + b_c)
        double[][] candidate = computeGate(input, hiddenState, wC, uC, bC, batchSize, false);

        // Store for debugging
        this.lastForgetGate = forgetGate;
        this.lastInputGate = inputGate;
        this.lastOutputGate = outputGate;
        this.lastCandidate = candidate;

        // Cell state update: C_t = f_t ⊙ C_{t-1} + i_t ⊙ C̃_t
        double[][] newCellState = new double[batchSize][hiddenSize];
        for (int b = 0; b < batchSize; b++) {
            for (int h = 0; h < hiddenSize; h++) {
                newCellState[b][h] = forgetGate[b][h] * cellState[b][h]
                                   + inputGate[b][h] * candidate[b][h];
            }
        }

        // Hidden state: h_t = o_t ⊙ tanh(C_t)
        double[][] newHiddenState = new double[batchSize][hiddenSize];
        for (int b = 0; b < batchSize; b++) {
            for (int h = 0; h < hiddenSize; h++) {
                newHiddenState[b][h] = outputGate[b][h] * tanh(newCellState[b][h]);
            }
        }

        return new double[][][]{newHiddenState, newCellState};
    }

    /**
     * Computes a single gate: activation(W * x + U * h + b).
     */
    private double[][] computeGate(double[][] input, double[][] hiddenState,
                                   double[][] w, double[][] u, double[] bias,
                                   int batchSize, boolean useSigmoid) {
        double[][] result = new double[batchSize][hiddenSize];

        for (int b = 0; b < batchSize; b++) {
            for (int h = 0; h < hiddenSize; h++) {
                double sum = bias[h];
                // Input contribution: x @ W
                for (int i = 0; i < inputSize; i++) {
                    sum += input[b][i] * w[i][h];
                }
                // Hidden contribution: h @ U
                for (int h2 = 0; h2 < hiddenSize; h2++) {
                    sum += hiddenState[b][h2] * u[h2][h];
                }
                result[b][h] = useSigmoid ? sigmoid(sum) : tanh(sum);
            }
        }
        return result;
    }

    /**
     * Validates input dimensions.
     */
    private void validateDimensions(double[][] input, double[][] hiddenState,
                                     double[][] cellState, int batchSize) {
        if (input[0].length != inputSize) {
            throw new IllegalArgumentException(
                "Input feature size " + input[0].length + " doesn't match expected " + inputSize);
        }
        if (hiddenState.length != batchSize || hiddenState[0].length != hiddenSize) {
            throw new IllegalArgumentException(
                "Hidden state shape mismatch: expected (" + batchSize + ", " + hiddenSize + ")");
        }
        if (cellState.length != batchSize || cellState[0].length != hiddenSize) {
            throw new IllegalArgumentException(
                "Cell state shape mismatch: expected (" + batchSize + ", " + hiddenSize + ")");
        }
    }

    /**
     * Sigmoid activation: σ(x) = 1 / (1 + exp(-x)).
     */
    private double sigmoid(double x) {
        if (x > 20) return 1.0;       // Prevent overflow
        if (x < -20) return 0.0;      // Prevent underflow
        return 1.0 / (1.0 + Math.exp(-x));
    }

    /**
     * Hyperbolic tangent activation.
     */
    private double tanh(double x) {
        if (x > 20) return 1.0;
        if (x < -20) return -1.0;
        double ePos = Math.exp(x);
        double eNeg = Math.exp(-x);
        return (ePos - eNeg) / (ePos + eNeg);
    }

    // ---- Initialization Helpers ----

    private double[][] glorotInit(int rows, int cols, double std) {
        double[][] m = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                m[i][j] = randn() * std;
            }
        }
        return m;
    }

    private double[] zeros(int n) {
        return new double[n];
    }

    private double[] ones(int n) {
        double[] arr = new double[n];
        Arrays.fill(arr, 1.0);
        return arr;
    }

    private double randn() {
        double u1 = Math.random();
        double u2 = Math.random();
        return Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
    }

    // ---- Getters for debugging ----

    public double[][] getLastForgetGate() { return lastForgetGate; }
    public double[][] getLastInputGate() { return lastInputGate; }
    public double[][] getLastOutputGate() { return lastOutputGate; }
    public double[][] getLastCandidate() { return lastCandidate; }

    public int getInputSize() { return inputSize; }
    public int getHiddenSize() { return hiddenSize; }
}
```

**Example Usage:**

```java
package lab03.rnn;

import java.util.Arrays;

public class LSTMExample {
    public static void main(String[] args) {
        int inputSize = 10;
        int hiddenSize = 20;
        int batchSize = 4;
        int sequenceLength = 5;

        LSTMCell cell = new LSTMCell(inputSize, hiddenSize);

        // Initialize states
        double[][] hiddenState = new double[batchSize][hiddenSize];
        double[][] cellState = new double[batchSize][hiddenSize];

        // Process a sequence
        for (int t = 0; t < sequenceLength; t++) {
            double[][] input = new double[batchSize][inputSize];
            // Fill input with random values
            for (int b = 0; b < batchSize; b++) {
                for (int i = 0; i < inputSize; i++) {
                    input[b][i] = Math.random() - 0.5;
                }
            }

            double[][][] states = cell.forward(input, hiddenState, cellState);
            hiddenState = states[0];
            cellState = states[1];

            System.out.println("Timestep " + t + ": h_norm="
                + Math.sqrt(Arrays.stream(hiddenState[0])
                    .map(x -> x * x).sum()));
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

For a single timestep forward pass with batch size `B`, input size `I`, and hidden size `H`:

| Operation | Complexity |
|-----------|------------|
| Input-to-hidden (4 gates) | `4 * B * I * H` multiply-adds |
| Hidden-to-hidden (4 gates) | `4 * B * H * H` multiply-adds |
| Bias addition (4 gates) | `4 * B * H` additions |
| Activations (4 sigmoid + 1 tanh) | `5 * B * H` element-wise ops |
| Cell state update | `2 * B * H` multiply-adds |
| Hidden state computation | `2 * B * H` multiply-adds |

**Total:** `O(B * (I * H + H²))` per timestep.

Since `I` and `H` are typically similar in practice, the dominant term is `O(B * H²)`.

### Space Complexity

**Weight storage:**
- 8 weight matrices: `4 * (I * H + H²)` parameters
- 4 bias vectors: `4 * H` parameters
- Total: `4 * H * (I + H + 1)` parameters

**Forward pass memory:**
- Input: `B * I`
- Hidden state (h, c): `2 * B * H`
- Gate pre-activations (if cached for backward): `4 * B * H`
- Gate activations: `4 * B * H`
- Output: `2 * B * H`

**Total working memory per timestep:** `O(B * (I + H))`

### Comparison with Vanilla RNN

| Aspect | Vanilla RNN | LSTM |
|--------|-------------|------|
| Parameters | `I*H + H² + H` | `4 * (I*H + H² + H)` |
| FLOPs per step | `O(B*(I*H + H²))` | `O(4*B*(I*H + H²))` |
| Backprop path | Multiplicative (vanishes) | Additive (preserves flow) |
| Performance on long sequences | Poor | Excellent |

---

## Follow-Up Questions with Answers

### Q1: Explain the vanishing gradient problem in RNNs and how LSTM addresses it.

**Answer:**

**Vanishing gradient:** In vanilla RNNs, the hidden state recurrence `h_t = tanh(Wx_t + Uh_{t-1})` involves repeated multiplication by `U`. During backpropagation through time, the gradient contains a product of `k` Jacobian matrices:

```
∂h_t / ∂h_{t-k} = Π_{j=1}^{k} diag(tanh'(z_{t-j+1})) * U
```

Since `tanh'(z) ∈ (0, 1]`, if the eigenvalues of `U` are less than 1, the product vanishes exponentially with `k`.

**How LSTM fixes it:** The cell state recurrence is additive, not multiplicative:

```
C_t = f_t ⊙ C_{t-1} + i_t ⊙ C̃_t
```

The gradient of the loss w.r.t. `C_{t-k}` contains a product of forget gates:

```
∂L / ∂C_{t-k} = ∂L / ∂C_t * Π_{j=1}^{k} f_{t-j+1}
```

If the forget gates are close to 1 (which the bias initialization encourages), this product remains close to 1 over long distances. The additive structure creates a "gradient superhighway" through the cell state.

### Q2: What is the purpose of the forget gate bias initialization to 1.0?

**Answer:** At initialization:
- With `b_f = 0`: `σ(0) = 0.5`, so the forget gate is half-open, discarding 50% of the cell state.
- With `b_f = 1`: `σ(1) ≈ 0.73`, so 73% of the cell state is retained.

The effect is that at the beginning of training, the network starts with a strong bias toward remembering information, which helps it learn long-range dependencies from the very first gradient updates. Without this, the network may initially lose long-range information before it learns to keep it.

This trick was proposed by Gers et al. (2000) and is now standard practice in all modern LSTM implementations.

### Q3: Contrast LSTM with GRU (Gated Recurrent Unit). What are the trade-offs?

**Answer:**

GRU simplifies LSTM by:
- Combining the forget and input gates into a single "update gate" `z_t`.
- Merging the cell state and hidden state into a single state `h_t`.
- Using a "reset gate" `r_t` to control how much past information to forget.

| Feature | LSTM | GRU |
|---------|------|-----|
| Gates | 3 (forget, input, output) + candidate | 2 (update, reset) |
| States | 2 (h, C) | 1 (h) |
| Parameters | `4*(I*H + H² + H)` | `3*(I*H + H² + H)` |
| Capacity | Higher (more flexible gating) | Lower (simpler dynamics) |
| Overfitting risk | Higher (more params) | Lower (fewer params) |
| Training speed | Slower | Faster |
| Long sequences | Slightly better (separate cell state) | Comparable |

**Practical guidance:** GRU often performs comparably to LSTM on many tasks while being faster to train and less prone to overfitting with small data. LSTM may have a slight edge on tasks requiring very long-term memory (hundreds of timesteps).

### Q4: How would you implement gradient clipping for an LSTM?

**Answer:** Gradient clipping prevents exploding gradients by normalizing the gradient when its norm exceeds a threshold:

```
// Before updating weights
double gradNorm = computeGradientNorm();
double clipThreshold = 5.0; // typical value

if (gradNorm > clipThreshold) {
    double scale = clipThreshold / gradNorm;
    for (each parameter p) {
        p.gradient *= scale;
    }
}
```

**Why it's needed:** Despite LSTM's improved gradient flow, the gradient can still explode (especially through the input-to-hidden connections). Gradient clipping ensures stable training.

**Variants:**
- **Value clipping:** Clip each gradient element-wise to `[-threshold, threshold]`.
- **Norm clipping:** Scale the entire gradient vector to have norm ≤ threshold.

Norm clipping is generally preferred as it preserves direction information.

### Q5: What is bidirectional LSTM? How does it differ from stacked LSTM?

**Answer:**

**Bidirectional LSTM (BiLSTM):** Processes the sequence both forwards and backwards using two independent LSTM layers. The outputs are concatenated:

```
h_t = [h_t_forward; h_t_backward]
```

- Input at timestep `t` has information from the entire sequence.
- Useful for sequence labeling, NER, POS tagging.
- Requires the entire sequence before producing output.

**Stacked LSTM (Deep LSTM):** Multiple LSTM layers on top of each other:

```
h_t^(1) = LSTM1(x_t, h_{t-1}^(1))
h_t^(2) = LSTM2(h_t^(1), h_{t-1}^(2))
```

- Higher layers learn more abstract temporal representations.
- Increases model capacity.
- Each layer has its own parameters.

**Combined:** You can have stacked bidirectional LSTMs (e.g., a 2-layer BiLSTM).

### Q6: How do you handle variable-length sequences in practice?

**Answer:**

1. **Padding:** Pad shorter sequences to the length of the longest sequence in the batch.
2. **Masking:** Use a mask to ignore the padded positions in the loss and state updates:

```
// During forward pass
if (isPadding[t][b]) {
    h_t[b] = h_{t-1}[b]  // carry forward the last valid state
    C_t[b] = C_{t-1}[b]
} else {
    h_t[b], C_t[b] = lstmCell(x_t[b], h_{t-1}[b], C_{t-1}[b])
}
```

3. **Packing:** PyTorch's `pack_padded_sequence` and `pad_packed_sequence` provide efficient handling.

4. **Bucketing:** Group sequences of similar lengths into the same batch to minimize padding overhead.

---

## Test Cases

### Test Case 1: Single Timestep, Batch Size 1

```java
void testSingleStep() {
    int inputSize = 5;
    int hiddenSize = 10;
    LSTMCell cell = new LSTMCell(inputSize, hiddenSize);

    double[][] input = new double[1][inputSize];
    Arrays.fill(input[0], 1.0);

    double[][] hiddenState = new double[1][hiddenSize];
    double[][] cellState = new double[1][hiddenSize];

    double[][][] result = cell.forward(input, hiddenState, cellState);
    double[][] newH = result[0];
    double[][] newC = result[1];

    assert newH.length == 1 : "Batch dimension preserved";
    assert newH[0].length == hiddenSize : "Hidden dimension preserved";
    assert newC.length == 1 : "Batch dimension preserved";
    assert newC[0].length == hiddenSize : "Hidden dimension preserved";
}
```

### Test Case 2: Multi-Batch Forward

```java
void testMultiBatch() {
    int inputSize = 8;
    int hiddenSize = 16;
    int batchSize = 8;
    LSTMCell cell = new LSTMCell(inputSize, hiddenSize);

    double[][] input = new double[batchSize][inputSize];
    double[][] hiddenState = new double[batchSize][hiddenSize];
    double[][] cellState = new double[batchSize][hiddenSize];

    for (int b = 0; b < batchSize; b++) {
        for (int i = 0; i < inputSize; i++) {
            input[b][i] = Math.random() - 0.5;
        }
    }

    double[][][] result = cell.forward(input, hiddenState, cellState);
    double[][] newH = result[0];
    double[][] newC = result[1];

    assert newH.length == batchSize : "Batch size preserved";
    assert newC.length == batchSize : "Batch size preserved";
    // Each batch element should have different states
    boolean allSame = true;
    for (int h = 0; h < hiddenSize; h++) {
        if (Math.abs(newH[0][h] - newH[1][h]) > 1e-10) {
            allSame = false;
            break;
        }
    }
    assert !allSame : "Batch elements should diverge with different inputs";
}
```

### Test Case 3: Sequence Consistency (h and C dimensions stable)

```java
void testSequenceProcess() {
    int inputSize = 10;
    int hiddenSize = 20;
    int seqLen = 10;
    int batchSize = 2;
    LSTMCell cell = new LSTMCell(inputSize, hiddenSize);

    double[][] hiddenState = new double[batchSize][hiddenSize];
    double[][] cellState = new double[batchSize][hiddenSize];

    for (int t = 0; t < seqLen; t++) {
        double[][] input = new double[batchSize][inputSize];
        for (int b = 0; b < batchSize; b++) {
            for (int i = 0; i < inputSize; i++) {
                input[b][i] = Math.sin(t * 0.5 + i);
            }
        }

        double[][][] result = cell.forward(input, hiddenState, cellState);
        hiddenState = result[0];
        cellState = result[1];

        // Check for NaN
        for (int b = 0; b < batchSize; b++) {
            for (int h = 0; h < hiddenSize; h++) {
                assert !Double.isNaN(hiddenState[b][h]) : "NaN in hidden state at t=" + t;
                assert !Double.isNaN(cellState[b][h]) : "NaN in cell state at t=" + t;
                assert !Double.isInfinite(hiddenState[b][h]) : "Inf in hidden state at t=" + t;
            }
        }
    }

    // States should have changed from initial zeros
    double hNorm = 0;
    for (int b = 0; b < batchSize; b++) {
        for (int h = 0; h < hiddenSize; h++) {
            hNorm += hiddenState[b][h] * hiddenState[b][h];
        }
    }
    assert hNorm > 1e-10 : "Hidden states should not remain zero after processing";
}
```

### Test Case 4: Forget Gate Bias Initialization

```java
void testForgetGateBiasInitializedToOne() {
    int inputSize = 3;
    int hiddenSize = 5;
    LSTMCell cell = new LSTMCell(inputSize, hiddenSize);

    // Processing with no input and zero states
    double[][] input = new double[1][inputSize];
    double[][] hiddenState = new double[1][hiddenSize];
    double[][] cellState = new double[1][hiddenSize];

    cell.forward(input, hiddenState, cellState);
    double[][] forgetGate = cell.getLastForgetGate();

    // With zero input and zero hidden state, forget gate = sigmoid(b_f)
    // b_f is initialized to 1.0, so sigmoid(1) ≈ 0.731
    for (int h = 0; h < hiddenSize; h++) {
        assert forgetGate[0][h] >= 0.7 && forgetGate[0][h] <= 0.75 :
            "Forget gate should be ~0.73 at init, got " + forgetGate[0][h];
    }
}
```

### Test Case 5: Gate Output Ranges

```java
void testGateOutputRanges() {
    LSTMCell cell = new LSTMCell(10, 20);

    double[][] input = new double[3][10];
    double[][] hiddenState = new double[3][20];
    double[][] cellState = new double[3][20];

    for (int b = 0; b < 3; b++) {
        Arrays.fill(input[b], 10.0); // Large input to push gates to extremes
    }

    cell.forward(input, hiddenState, cellState);

    double[][] forgetGate = cell.getLastForgetGate();
    double[][] inputGate = cell.getLastInputGate();
    double[][] outputGate = cell.getLastOutputGate();
    double[][] candidate = cell.getLastCandidate();

    // Sigmoid gates should be in (0, 1)
    for (int b = 0; b < 3; b++) {
        for (int h = 0; h < 20; h++) {
            assert forgetGate[b][h] > 0 && forgetGate[b][h] < 1 :
                "Forget gate out of range: " + forgetGate[b][h];
            assert inputGate[b][h] > 0 && inputGate[b][h] < 1;
            assert outputGate[b][h] > 0 && outputGate[b][h] < 1;
            // Tanh should be in (-1, 1)
            assert candidate[b][h] > -1 && candidate[b][h] < 1 :
                "Candidate out of range: " + candidate[b][h];
        }
    }
}
```

### Test Case 6: Numerical Stability with Extreme Inputs

```java
void testNumericalStability() {
    LSTMCell cell = new LSTMCell(5, 10);
    double[][] hiddenState = new double[2][10];
    double[][] cellState = new double[2][10];

    // Very large positive input
    double[][] largeInput = new double[2][5];
    Arrays.fill(largeInput[0], 1e6);
    Arrays.fill(largeInput[1], -1e6);

    double[][][] result = cell.forward(largeInput, hiddenState, cellState);

    for (int b = 0; b < 2; b++) {
        for (int h = 0; h < 10; h++) {
            assert !Double.isNaN(result[0][b][h]) : "NaN from extreme input";
            assert !Double.isNaN(result[1][b][h]) : "NaN in cell state from extreme input";
        }
    }
}
```

### Test Case 7: Cell State as Long-Term Memory

```java
void testCellStatePersistence() {
    LSTMCell cell = new LSTMCell(4, 8);
    double[][] h = new double[1][8];
    double[][] c = new double[1][8];

    // Inject a "remember" signal via large input gate, small forget gate decay
    // Multiple steps with small input to see if cell state persists
    for (int t = 0; t < 20; t++) {
        double[][] x = new double[1][4];
        Arrays.fill(x[0], 0.1);
        double[][][] res = cell.forward(x, h, c);
        h = res[0];
        c = res[1];
    }

    // Cell state should not be zero (information persists)
    double cNorm = 0;
    for (int hIdx = 0; hIdx < 8; hIdx++) {
        cNorm += c[0][hIdx] * c[0][hIdx];
    }
    assert cNorm > 1e-10 : "Cell state should retain information over time";
}
```
