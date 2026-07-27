# PROBLEM WALKTHROUGH: Implement 2D Convolution from Scratch

## Problem Statement

**Difficulty:** Hard | **Category:** Convolutional Neural Networks | **Estimated Time:** 90 minutes

Write a Java 21+ implementation of a 2D convolution operation that supports multiple input channels, multiple output filters, configurable kernel size, stride, and padding. Your `Conv2D` class must implement the forward pass of a convolutional layer exactly as used in modern CNNs, including the distinction between cross-correlation and true convolution.

**Input:**
- `input`: A 3D tensor of shape `(inChannels, height, width)` of type `double[][]` where each channel is a 2D matrix.
- `kernels`: A 4D tensor of shape `(outChannels, inChannels, kH, kW)` of type `double[][][][]`.
- `bias`: A 1D array of length `outChannels` of type `double[]`.
- `stride`: An `int` representing the stride for both height and width dimensions.
- `padding`: An `int` representing zero-padding applied to all four sides of each input channel.

**Output:**
- `output`: A 3D tensor of shape `(outChannels, outH, outW)` where:
  - `outH = floor((height + 2 * padding - kH) / stride) + 1`
  - `outW = floor((width + 2 * padding - kW) / stride) + 1`

**Constraints:**
- Do not use external linear algebra libraries. Implement convolution using nested loops.
- Handle non-square kernels and asymmetric padding implicitly via the formula.
- If `stride > 1`, skip input positions accordingly.
- Implement both cross-correlation mode and true convolution (kernel flipped) via a boolean flag `useTrueConvolution`.

**Evaluation Criteria:**
- Correct output dimensions for all valid parameter combinations.
- Numerical correctness verified against a brute-force reference implementation.
- Proper handling of edge cases: `padding = 0`, `stride = 1`, single-channel input, single-filter output.

---

## Step-by-Step Solution Walkthrough

### 1. Understanding the Convolution Operation

The 2D convolution (strictly, cross-correlation) slides a kernel over the input, computing element-wise dot products at each position.

**Mathematical Definition:**

For a single input channel `X` and a single kernel `K`, the output at position `(i, j)` is:

```
Y[i][j] = Σ_{m=0}^{kH-1} Σ_{n=0}^{kW-1} X[i + m][j + n] * K[m][n] + bias
```

When padding is applied, we first pad the input with zeros:

```
X_padded[p][q] = 0 if p < pad or p >= H + pad or q < pad or q >= W + pad
X_padded[p][q] = X[p - pad][q - pad] otherwise
```

With stride `s`, we compute output at positions `(i * s, j * s)` of the padded input:

```
Y[i][j] = Σ_{m} Σ_{n} X_padded[i * s + m][j * s + n] * K[m][n] + bias
```

### 2. Cross-Correlation vs True Convolution

In deep learning frameworks, what is called "convolution" is actually **cross-correlation**:

- **Cross-correlation**: Slide the kernel as-is over the input.
- **True convolution**: Flip the kernel both vertically and horizontally before sliding.

The relationship:
```
TrueConv(X, K) = CrossCorr(X, flip180(K))
```

Where `flip180` rotates the kernel by 180 degrees.

**Why does this matter?**
- Convolution is commutative (if you flip the kernel), which is mathematically convenient for proofs.
- Cross-correlation is simpler to implement and equivalent in learning capacity (the network can learn the flipped kernel).
- Most frameworks (PyTorch, TensorFlow) implement cross-correlation but call it convolution.

### 3. Computing Output Dimensions

Given:
- Input `H x W`
- Padding `P`
- Kernel `kH x kW`
- Stride `s`

```
outH = floor((H + 2P - kH) / s) + 1
outW = floor((W + 2P - kW) / s) + 1
```

**Derivation:**
The kernel must fit entirely within the padded input. Starting from position 0, the kernel occupies `kH` positions. After each stride, we move `s` positions forward. The last valid starting position is `(H + 2P - kH)`. The number of positions is `floor((H + 2P - kH) / s) + 1`.

### 4. Multi-Channel Input and Multi-Filter Output

**Multiple input channels:** Each filter has `inChannels` separate kernels. The output for one filter is the sum of convolutions across all input channels:

```
Y_filter_f[i][j] = Σ_{c=0}^{inChannels-1} (X_c * K_f_c)[i][j] + bias_f
```

**Multiple output filters:** Each filter produces one output channel. Stack them to form the output tensor.

### 5. Algorithm Pseudocode

```
function conv2d(input, kernels, bias, stride, padding, useTrueConvolution):
    inChannels, H, W = input.shape
    outChannels, _, kH, kW = kernels.shape

    outH = floor((H + 2*padding - kH) / stride) + 1
    outW = floor((W + 2*padding - kW) / stride) + 1

    output = new double[outChannels][outH][outW]

    for f = 0 to outChannels - 1:
        for i = 0 to outH - 1:
            for j = 0 to outW - 1:
                sum = bias[f]
                for c = 0 to inChannels - 1:
                    for m = 0 to kH - 1:
                        for n = 0 to kW - 1:
                            hPos = i * stride + m - padding
                            wPos = j * stride + n - padding
                            if 0 <= hPos < H and 0 <= wPos < W:
                                if useTrueConvolution:
                                    kernelVal = kernels[f][c][kH-1-m][kW-1-n]
                                else:
                                    kernelVal = kernels[f][c][m][n]
                                sum += input[c][hPos][wPos] * kernelVal
                output[f][i][j] = sum

    return output
```

### 6. Numerical Considerations

- **Accumulation order**: Summing across channels first (innermost loops over kernel positions) improves cache locality if input channels are stored contiguously.
- **Boundary checks**: Checking bounds inside the loop is simpler but slower. An alternative is to pad the input explicitly and avoid bounds checks.
- **Precision**: Use `double` for accumulation to minimize floating-point error.

### 7. Explicit Padding vs Bounds Checking

| Approach | Pros | Cons |
|----------|------|------|
| Explicit padding | Cleaner inner loops, potential speed | Extra memory, padding overhead |
| Bounds checking | No extra memory | Branch overhead in inner loop |

For educational clarity, the explicit padding approach is often preferred. The bounds-checking approach avoids allocating the padded array.

---

## Java Implementation

```java
package lab01.cnn;

import java.util.Arrays;

/**
 * A 2D convolution implementation from scratch supporting multiple input channels,
 * multiple output filters, configurable kernel size, stride, and padding.
 * <p>
 * This class implements both cross-correlation (standard in deep learning frameworks)
 * and true convolution (with kernel flips).
 */
public class Conv2D {

    private final int stride;
    private final int padding;
    private final boolean useTrueConvolution;

    /**
     * Constructs a Conv2D operation with the given parameters.
     *
     * @param stride              the stride for both height and width dimensions (must be &gt; 0)
     * @param padding             zero-padding applied to all four sides of each input channel (must be &gt;= 0)
     * @param useTrueConvolution  if true, flips the kernel (true convolution); otherwise cross-correlation
     * @throws IllegalArgumentException if stride &lt;= 0 or padding &lt; 0
     */
    public Conv2D(int stride, int padding, boolean useTrueConvolution) {
        if (stride <= 0) {
            throw new IllegalArgumentException("Stride must be positive, got: " + stride);
        }
        if (padding < 0) {
            throw new IllegalArgumentException("Padding must be non-negative, got: " + padding);
        }
        this.stride = stride;
        this.padding = padding;
        this.useTrueConvolution = useTrueConvolution;
    }

    /**
     * Performs the forward pass of the 2D convolution.
     *
     * @param input  3D tensor of shape (inChannels, height, width) — each channel is a 2D matrix
     * @param kernels 4D tensor of shape (outChannels, inChannels, kH, kW)
     * @param bias   1D array of length outChannels
     * @return 3D tensor of shape (outChannels, outH, outW)
     * @throws IllegalArgumentException if dimensions are inconsistent or null
     */
    public double[][][] forward(double[][] input, double[][][][] kernels, double[] bias) {
        // Validate inputs
        if (input == null || kernels == null || bias == null) {
            throw new IllegalArgumentException("Input, kernels, and bias must not be null");
        }
        int inChannels = input.length;
        if (inChannels == 0) {
            throw new IllegalArgumentException("Input must have at least one channel");
        }
        int height = input[0].length;
        int width = input[0][2].length;

        int outChannels = kernels.length;
        if (outChannels == 0) {
            throw new IllegalArgumentException("Kernels must have at least one output channel");
        }
        int kInChannels = kernels[0].length;
        if (kInChannels != inChannels) {
            throw new IllegalArgumentException(
                "Kernel input channels (" + kInChannels + ") must match input channels (" + inChannels + ")"
            );
        }
        int kH = kernels[0][0].length;
        int kW = kernels[0][0][0].length;

        if (bias.length != outChannels) {
            throw new IllegalArgumentException(
                "Bias length (" + bias.length + ") must match outChannels (" + outChannels + ")"
            );
        }

        // Compute output dimensions
        int outH = (height + 2 * padding - kH) / stride + 1;
        int outW = (width + 2 * padding - kW) / stride + 1;

        if (outH <= 0 || outW <= 0) {
            throw new IllegalArgumentException(
                "Invalid output dimensions: kernel too large for given input and padding"
            );
        }

        // Allocate output tensor
        double[][][] output = new double[outChannels][outH][outW];

        // Perform convolution
        for (int f = 0; f < outChannels; f++) {
            for (int i = 0; i < outH; i++) {
                for (int j = 0; j < outW; j++) {
                    double sum = bias[f];
                    for (int c = 0; c < inChannels; c++) {
                        for (int m = 0; m < kH; m++) {
                            // Input row in the padded coordinate system
                            int hPos = i * stride + m - padding;
                            if (hPos < 0 || hPos >= height) {
                                continue;
                            }
                            for (int n = 0; n < kW; n++) {
                                int wPos = j * stride + n - padding;
                                if (wPos < 0 || wPos >= width) {
                                    continue;
                                }
                                double kernelVal;
                                if (useTrueConvolution) {
                                    // Flip kernel: rotate 180 degrees
                                    kernelVal = kernels[f][c][kH - 1 - m][kW - 1 - n];
                                } else {
                                    kernelVal = kernels[f][c][m][n];
                                }
                                sum += input[c][hPos][wPos] * kernelVal;
                            }
                        }
                    }
                    output[f][i][j] = sum;
                }
            }
        }
        return output;
    }

    /**
     * Returns the expected output height for given input height and kernel height.
     */
    public int getOutputHeight(int inputHeight, int kernelHeight) {
        return (inputHeight + 2 * padding - kernelHeight) / stride + 1;
    }

    /**
     * Returns the expected output width for given input width and kernel width.
     */
    public int getOutputWidth(int inputWidth, int kernelWidth) {
        return (inputWidth + 2 * padding - kernelWidth) / stride + 1;
    }

    /**
     * Returns the stride.
     */
    public int getStride() {
        return stride;
    }

    /**
     * Returns the padding.
     */
    public int getPadding() {
        return padding;
    }

    /**
     * Returns whether true convolution (kernel flipped) is used.
     */
    public boolean isUseTrueConvolution() {
        return useTrueConvolution;
    }
}
```

**Example Usage:**

```java
package lab01.cnn;

public class Conv2DExample {
    public static void main(String[] args) {
        // Input: 1 channel, 5x5
        double[][] input = {
            {1, 2, 3, 4, 5},
            {6, 7, 8, 9, 10},
            {11, 12, 13, 14, 15},
            {16, 17, 18, 19, 20},
            {21, 22, 23, 24, 25}
        };
        double[][][] batchedInput = {input};

        // Kernel: 1 output filter, 1 input channel, 3x3
        double[][][][] kernels = {{
            {
                {1, 0, -1},
                {1, 0, -1},
                {1, 0, -1}
            }
        }};
        double[] bias = {0.0};

        Conv2D conv = new Conv2D(1, 0, false);
        double[][][] output = conv.forward(batchedInput, kernels, bias);

        System.out.println("Output shape: " + output[0].length + "x" + output[0][0].length);
        for (double[] row : output[0]) {
            System.out.println(Arrays.toString(row));
        }
    }
}
```

---

## Complexity Analysis

### Time Complexity

For a single forward pass:

```
O(outChannels * outH * outW * inChannels * kH * kW)
```

Let's denote:
- `C_in = inChannels`
- `C_out = outChannels`
- `H_out = outH`, `W_out = outW`
- `K_h = kH`, `K_w = kW`

**Total operations:** `C_out * H_out * W_out * C_in * K_h * K_w`

Each operation is: 1 multiplication + 1 addition (fused multiply-add).

**With stride > 1:** `H_out` and `W_out` shrink, reducing total operations proportionally.

**With padding > 0:** `H_out = (H + 2P - K_h) / S + 1`. Padding increases `H_out` and `W_out`, increasing computation.

### Space Complexity

**Input storage:** `C_in * H * W` (doubles)
**Kernel storage:** `C_out * C_in * K_h * K_w` (doubles)
**Bias storage:** `C_out` (doubles)
**Output storage:** `C_out * H_out * W_out` (doubles)
**Working memory (explicit padding approach):** `C_in * (H + 2P) * (W + 2P)` (doubles)

**Total (without padding copy):** `O(C_in * H * W + C_out * C_in * K_h * K_w + C_out * H_out * W_out)`

### Comparison with Modern Implementations

Modern frameworks (cuDNN, MKL-DNN) use:
- **im2col + GEMM**: Transforms convolution into matrix multiplication. Adds memory overhead (the im2col buffer is `C_in * K_h * K_w` times larger than the output) but leverages highly optimized BLAS routines.
- **Winograd**: Reduces multiplication count for 3x3 kernels (common in modern CNNs).
- **FFT-based**: For large kernels, convolution in frequency domain is faster.

| Method | Time Complexity (relative) | Memory Overhead |
|--------|---------------------------|-----------------|
| Naive loops (ours) | 1x | None |
| im2col + GEMM | ~0.5x–1x | ~K_h * K_w x output |
| Winograd (F(2x2, 3x3)) | ~0.44x | Moderate |
| FFT | ~0.1x–0.3x (large kernels) | High |

---

## Follow-Up Questions with Answers

### Q1: What is the difference between cross-correlation and true convolution? Why do frameworks use cross-correlation?

**Answer:** Cross-correlation slides the kernel as-is, while true convolution flips the kernel by 180 degrees before sliding. Mathematically:

```
(X ★ K)[i][j] = Σ_m Σ_n X[i+m][j+n] * K[m][n]  (cross-correlation)
(X ∗ K)[i][j] = Σ_m Σ_n X[i+m][j+n] * K[-m][-n]  (true convolution, flipping the kernel)
```

Frameworks use cross-correlation because:
1. It is simpler to implement (no flipping).
2. The kernel weights are learned anyway; flipping would just learn the flipped version.
3. It does not affect the representational capacity of the network.
4. The property of convolution (commutativity) is not needed in feed-forward networks.

### Q2: How does stride affect the output size and the receptive field?

**Answer:** Stride controls how much the kernel moves between applications.

- **Stride = 1**: Dense application. Every possible aligned position is computed. Output size ≈ input size (with appropriate padding).
- **Stride = 2**: Sparse application. Every other position is computed. Output size ≈ input size / 2.
- **Stride > 1** reduces output spatial dimensions (downsampling).

**Receptive field impact:** With stride > 1, each output neuron "sees" a larger effective area of the input (because the skipped positions mean information is pooled over larger regions). In a multi-layer network, strides > 1 exponentially increase the receptive field.

### Q3: Define "parameter sharing" and "sparsity of connections" in the context of CNNs.

**Answer:**

- **Parameter sharing**: The same kernel weights are used across all spatial positions of the input. A 3x3 kernel has 9 weights regardless of input size (32x32 or 224x224). This is in contrast to a fully connected layer where each input-output pair has its own weight.

- **Sparsity of connections**: Each output neuron connects to only a local region of the input (the kernel size), not all input pixels. A 3x3 kernel connects to 9 input pixels, not 1024 (for a 32x32 input). This dramatically reduces the number of parameters.

These two properties are the key innovations of CNNs over fully connected networks:
- Sparsity reduces parameters from `O(H * W * C_in * H_out * W_out * C_out)` to `O(C_in * C_out * K_h * K_w)`.
- Parameter sharing makes the model translation-equivariant: a feature learned at one position is recognized at any other position.

### Q4: How would you optimize this implementation for performance?

**Answer:**

1. **Loop reordering**: Move the `kH` and `kW` loops to be the innermost to maximize cache reuse of input values.
2. **Explicit padding**: Pre-pad the input array to avoid branch predictions in the inner loop.
3. **im2col + GEMM**: Transform the problem into matrix multiplication:
   - Unroll each kernel-sized patch into a column → `(C_in * K_h * K_w) x (H_out * W_out)` matrix.
   - Reshape kernels into `C_out x (C_in * K_h * K_w)` matrix.
   - Perform matrix multiplication.
4. **SIMD vectorization**: Use Java Vector API (JEP 426) to process multiple kernel positions in parallel.
5. **Parallelization**: Use parallel streams or fork-join to process output channels independently.
6. **Memory layout**: Use NCHW (channels-first) for cuDNN compatibility or NHWC (channels-last) for better cache behavior on CPUs.

### Q5: What is the receptive field of a stack of convolutional layers?

**Answer:** The receptive field is the region of the input that influences a particular output neuron.

For a stack of convolutional layers:

```
RF_layer_k = RF_layer_{k-1} + (K_k - 1) * S_{1..k-1}
```

Where `S_{1..k-1}` is the product of strides up to layer `k-1`.

For example, two consecutive 3x3 convolutions with stride 1:
- Layer 1: RF = 3
- Layer 2: RF = 3 + (3 - 1) * 1 = 5

Two 3x3 convs have the same receptive field as one 5x5 conv but with fewer parameters (2 * 9 = 18 vs 25).

### Q6: How does padding affect the output size? What types of padding exist?

**Answer:**

- **Valid padding (P = 0):** Output shrinks by `K - 1` pixels per dimension per layer. `out = floor((H - K) / S) + 1`.
- **Same padding:** Output size equals input size (rounded up). Requires `P = floor((K - 1) / 2)` and `S = 1`.
- **Full padding:** Output expands such that every input pixel influences every output pixel. `P = K - 1`.

Types:
- **Zero padding**: Fill with 0 (most common).
- **Reflection padding**: Reflect the input at boundaries.
- **Replication padding**: Repeat the edge values.
- **Circular padding**: Wrap around (like FFT assumes).

### Q7: Why is the convolution operation linear? How does the bias term work?

**Answer:** Convolution is linear because it satisfies:
- **Additivity**: `Conv(X1 + X2) = Conv(X1) + Conv(X2)`
- **Homogeneity**: `Conv(αX) = α * Conv(X)`

This follows directly from the definition: convolution is sums of products, which is a linear operation.

The bias term adds a learnable constant to each output position of each filter:
```
Y_f = (Σ_c X_c * K_f_c) + b_f
```

The bias is independent of the spatial position and input values, making the output channel have a learnable threshold. In modern practice, bias is often omitted when batch normalization follows (since BN has its own shift parameter).

---

## Test Cases

### Test Case 1: Basic 3x3 Kernel, No Padding, Stride 1

```java
void testBasicConv() {
    double[][] input = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9}
    };
    double[][][] batchedInput = {input};

    // Identity-like kernel (only center is 1)
    double[][][][] kernels = {{{ {0, 0, 0}, {0, 1, 0}, {0, 0, 0} }}};
    double[] bias = {0};

    Conv2D conv = new Conv2D(1, 0, false);
    double[][][] output = conv.forward(batchedInput, kernels, bias);

    // Should output same as input (1x1 output since 3x3 input, 3x3 kernel, no padding)
    assert output[0].length == 1 && output[0][0].length == 1 :
        "Expected 1x1 output, got " + output[0].length + "x" + output[0][0].length;
    assert output[0][0][0] == 5.0 :
        "Expected center value 5.0, got " + output[0][0][0];
}
```

### Test Case 2: With Padding (Same Output Size)

```java
void testPaddingSameOutput() {
    double[][] input = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9}
    };
    double[][][] batchedInput = {input};

    double[][][][] kernels = {{{ {1, 0, -1}, {1, 0, -1}, {1, 0, -1} }}};
    double[] bias = {0};

    // With padding=1 and stride=1, 3x3 input + 3x3 kernel => 3x3 output (same padding)
    Conv2D conv = new Conv2D(1, 1, false);
    double[][][] output = conv.forward(batchedInput, kernels, bias);

    System.out.println("Output shape: " + output[0].length + "x" + output[0][0].length);
    // Should be 3x3 (same as input)
    assert output[0].length == 3 && output[0][0].length == 3 :
        "Expected 3x3 output with padding=1, got " + output[0].length + "x" + output[0][0].length;
}
```

### Test Case 3: Stride = 2 (Downsampling)

```java
void testStride2() {
    double[][] input = {
        {1, 2, 3, 4},
        {5, 6, 7, 8},
        {9, 10, 11, 12},
        {13, 14, 15, 16}
    };
    double[][][] batchedInput = {input};

    double[][][][] kernels = {{{ {1, 0, -1}, {1, 0, -1}, {1, 0, -1} }}};
    double[] bias = {0};

    // Stride 2, no padding: 4x4 input + 3x3 kernel => floor((4-3)/2)+1 = 1x1
    Conv2D conv = new Conv2D(2, 0, false);
    double[][][] output = conv.forward(batchedInput, kernels, bias);

    assert output[0].length == 1 && output[0][0].length == 1 :
        "Expected 1x1 output with stride=2, got " + output[0].length + "x" + output[0][0].length;
}
```

### Test Case 4: Multiple Input Channels

```java
void testMultipleInputChannels() {
    double[][] channel0 = {
        {1, 2},
        {3, 4}
    };
    double[][] channel1 = {
        {5, 6},
        {7, 8}
    };
    double[][][] input = {channel0, channel1}; // 2 channels, 2x2

    // 1 output filter, 2 input channels, 2x2 kernel per channel
    double[][][][] kernels = {{
        { {1, 0}, {0, 1} },  // kernel for channel 0
        { {0, 1}, {1, 0} }   // kernel for channel 1
    }};
    double[] bias = {0};

    Conv2D conv = new Conv2D(1, 0, false);
    double[][][] output = conv.forward(input, kernels, bias);

    // Expected: 1x1 output (2x2 input + 2x2 kernel, no padding)
    // channel0: 1*1 + 2*0 + 3*0 + 4*1 = 1 + 4 = 5
    // channel1: 5*0 + 6*1 + 7*1 + 8*0 = 6 + 7 = 13
    // Total: 5 + 13 = 18
    assert Math.abs(output[0][0][0] - 18.0) < 1e-9 :
        "Expected 18.0, got " + output[0][0][0];
}
```

### Test Case 5: Multiple Output Filters

```java
void testMultipleOutputFilters() {
    double[][] input = {
        {1, 2},
        {3, 4}
    };
    double[][][] batchedInput = {input};

    // 2 output filters, 1 input channel, 2x2 kernel each
    double[][][][] kernels = {
        {{ {1, 0}, {0, 0} }},  // filter 0: extracts top-left
        {{ {0, 0}, {0, 1} }}   // filter 1: extracts bottom-right
    };
    double[] bias = {0, 0};

    Conv2D conv = new Conv2D(1, 0, false);
    double[][][] output = conv.forward(batchedInput, kernels, bias);

    assert output.length == 2 : "Expected 2 output channels, got " + output.length;
    // Filter 0: 1*1 = 1
    assert Math.abs(output[0][0][0] - 1.0) < 1e-9 :
        "Expected filter[0]=1.0, got " + output[0][0][0];
    // Filter 1: 4*1 = 4
    assert Math.abs(output[1][0][0] - 4.0) < 1e-9 :
        "Expected filter[1]=4.0, got " + output[1][0][0];
}
```

### Test Case 6: True Convolution (Kernel Flipped)

```java
void testTrueConvolution() {
    double[][] input = {
        {1, 2},
        {3, 4}
    };
    double[][][] batchedInput = {input};

    double[][][][] kernels = {{{ {1, 2}, {3, 4} }}};
    double[] bias = {0};

    // Cross-correlation: K as-is
    Conv2D convCorr = new Conv2D(1, 0, false);
    double[][][] outputCorr = convCorr.forward(batchedInput, kernels, bias);

    // True convolution: K flipped = {{4, 3}, {2, 1}}
    Conv2D convTrue = new Conv2D(1, 0, true);
    double[][][] outputTrue = convTrue.forward(batchedInput, kernels, bias);

    // Cross-correlation: 1*1 + 2*2 + 3*3 + 4*4 = 1+4+9+16 = 30
    assert Math.abs(outputCorr[0][0][0] - 30.0) < 1e-9;

    // True convolution: 1*4 + 2*3 + 3*2 + 4*1 = 4+6+6+4 = 20
    assert Math.abs(outputTrue[0][0][0] - 20.0) < 1e-9;

    // Should differ
    assert Math.abs(outputCorr[0][0][0] - outputTrue[0][0][0]) > 1e-9 :
        "Cross-correlation and true convolution should differ";
}
```

### Test Case 7: Bias Term

```java
void testBias() {
    double[][] input = {
        {1, 2},
        {3, 4}
    };
    double[][][] batchedInput = {input};

    double[][][][] kernels = {{{ {1, 0}, {0, 0} }}};
    double[] bias = {5.0};

    Conv2D conv = new Conv2D(1, 0, false);
    double[][][] output = conv.forward(batchedInput, kernels, bias);

    // 1*1 + 5 = 6
    assert Math.abs(output[0][0][0] - 6.0) < 1e-9 :
        "Expected 6.0 with bias=5, got " + output[0][0][0];
}
```

### Test Case 8: Receptive Field Verification with Multiple Channels

```java
void testEdgeCaseKernelLargerThanInput() {
    double[][] input = {
        {1, 2},
        {3, 4}
    };
    double[][][] batchedInput = {input};

    // 5x5 kernel on 2x2 input with padding=0 => invalid (negative dimension)
    double[][][][] kernels = {{{ {
        {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1},
        {1, 1, 1, 1, 1}
    } }}};
    double[] bias = {0};

    try {
        Conv2D conv = new Conv2D(1, 0, false);
        conv.forward(batchedInput, kernels, bias);
        assert false : "Should have thrown IllegalArgumentException";
    } catch (IllegalArgumentException e) {
        // Expected: output dimensions <= 0
    }
}
```

### Test Case 9: Large Input Verification

```java
void testLargeInputSanity() {
    int H = 32, W = 32, C_in = 3, C_out = 16, kH = 3, kW = 3;
    double[][][] input = new double[C_in][H][W];
    // Fill with random-ish values
    for (int c = 0; c < C_in; c++) {
        for (int i = 0; i < H; i++) {
            Arrays.fill(input[c][i], 1.0);
        }
    }
    double[][][][] kernels = new double[C_out][C_in][kH][kW];
    for (int f = 0; f < C_out; f++) {
        for (int c = 0; c < C_in; c++) {
            for (int m = 0; m < kH; m++) {
                Arrays.fill(kernels[f][c][m], 1.0);
            }
        }
    }
    double[] bias = new double[C_out];

    Conv2D conv = new Conv2D(1, 1, false);
    double[][][] output = conv.forward(input, kernels, bias);

    int outH = H; // with padding=1, stride=1, 3x3 kernel => same size
    int outW = W;
    assert output.length == C_out;
    assert output[0].length == outH;
    assert output[0][0].length == outW;

    // Each output = sum over C_in * kH * kW = 3*9 = 27 values of 1*1 = 27
    for (int f = 0; f < C_out; f++) {
        for (int i = 0; i < outH; i++) {
            for (int j = 0; j < outW; j++) {
                assert Math.abs(output[f][i][j] - 27.0) < 1e-9 :
                    "Expected 27.0 at [" + f + "][" + i + "][" + j + "], got " + output[f][i][j];
            }
        }
    }
}
```
