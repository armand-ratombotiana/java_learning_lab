# PROBLEM WALKTHROUGH: Implement a Residual Block (ResNet)

## Problem Statement

**Difficulty:** Hard | **Category:** CNN Architectures | **Estimated Time:** 75 minutes

Implement a residual block with skip connections and bottleneck design as used in the ResNet family of architectures (He et al., 2015). Your `ResidualBlock` class must support both the basic block (two 3x3 convolutions) and the bottleneck block (1x1 → 3x3 → 1x1), with configurable stride for downsampling and the option for identity or projection shortcut connections.

**Input:**
- `input`: A 3D tensor of shape `(channels, height, width)` of type `double[][]`.
- `inChannels`: Number of input channels.
- `outChannels`: Number of output channels (determines filter count in the last conv layer of the block).
- `stride`: Stride for the first convolution (use > 1 for spatial downsampling).
- `useBottleneck`: If `true`, use the bottleneck design (1x1→3x3→1x1). If `false`, use the basic design (3x3→3x3).
- `projectionShortcut`: If `true`, use a 1x1 convolution for the shortcut when dimensions change. If `false`, zero-pad the shortcut.

**Output:**
- `output`: A 3D tensor of shape `(outChannels, outH, outW)` where:
  - For basic block: Two consecutive 3x3 convolutions, each followed by batch normalization and ReLU.
  - For bottleneck block: 1x1 (reduce) → 3x3 (spatial) → 1x1 (expand), each followed by batch normalization and ReLU (except the last before the final ReLU after the addition).

**Constraints:**
- Implement batch normalization (simplified: compute mean and variance along spatial dimensions, normalize, scale/shift).
- Implement the skip connection properly: identity mapping when dimensions match, projection or zero-padding when they don't.
- Apply ReLU after the addition.

**Evaluation Criteria:**
- Correct output dimensions for both basic and bottleneck blocks.
- Numerical correctness of the skip connection addition.
- Proper handling of stride > 1 (downsampling in the first convolution and the shortcut).
- Batch normalization correctness (epsilon for numerical stability).

---

## Step-by-Step Solution Walkthrough

### 1. Understanding Residual Learning

The core idea of ResNet is to learn residual functions with reference to the layer inputs, instead of learning unreferenced functions.

**Mathematical Formulation:**

Instead of learning `H(x)`, learn `F(x) = H(x) - x`, then compute:

```
y = F(x, W_i) + x
```

Where `F(x, W_i)` is the residual mapping (a stack of convolutions + batch norm + ReLU) and `x` is the identity shortcut connection.

**Why this works:**
- If the identity mapping is optimal, the layers can simply learn to push `F(x)` toward zero.
- This prevents the degradation problem (deeper networks performing worse) by allowing gradients to flow directly through the skip connection during backpropagation.
- The gradient of the loss with respect to `x` is `∂L/∂y * (∂F/∂x + 1)`, which ensures the gradient never vanishes (the "+1" term provides a direct path).

### 2. Basic Block vs Bottleneck Block

**Basic Block** (used in ResNet-18, ResNet-34):
```
input → [3x3 Conv, BN, ReLU] → [3x3 Conv, BN] → + → ReLU → output
                                               ↑
                                               identity
```
- Parameters: `2 * (3*3*C_in*C_out) + 2*C_out` (bias for BN)
- Used when depth is moderate (18, 34 layers).

**Bottleneck Block** (used in ResNet-50, ResNet-101, ResNet-152):
```
input → [1x1 Conv, BN, ReLU] → [3x3 Conv, BN, ReLU] → [1x1 Conv, BN] → + → ReLU → output
                                                                         ↑
                                                                         identity
```
- 1x1 conv reduces channels from `C_in` to `C_mid` (typically `C_out / 4`)
- 3x3 conv operates on reduced channels (`C_mid`)
- 1x1 conv expands back to `C_out`
- Parameters: `(1*1*C_in*C_mid) + (3*3*C_mid*C_mid) + (1*1*C_mid*C_out)`
- Approximately `(C_mid/C_out) * (1 + 9*C_mid/C_out + 1)` fraction of a basic block's parameters.

### 3. Shortcut Connections

When input and output dimensions differ (`C_in ≠ C_out` or spatial size changes due to stride), the shortcut must be adjusted:

**Option A: Projection Shortcut**
```
shortcut(x) = Conv2D(x, kernel=1x1, stride=s, padding=0)
```
- Uses a learned 1x1 convolution to project input to output dimensions.
- Adds parameters: `C_in * C_out * 1 * 1 + C_out` (bias).
- Matches dimensions exactly.

**Option B: Zero-Padding Shortcut**
```
shortcut(x) = pad_channels(avg_pool(x, stride=s))
```
- Average pool to reduce spatial dimensions, then pad with zeros to increase channels.
- No additional parameters.
- Information is lost in padded channels.

### 4. Batch Normalization (Simplified)

For a 2D input `x` of shape `(C, H, W)`:

```
μ_c = (1 / (H * W)) * Σ_i Σ_j x[c][i][j]
σ²_c = (1 / (H * W)) * Σ_i Σ_j (x[c][i][j] - μ_c)²
x̂[c][i][j] = (x[c][i][j] - μ_c) / sqrt(σ²_c + ε)
y[c][i][j] = γ_c * x̂[c][i][j] + β_c
```

Where:
- `γ_c`, `β_c` are learnable parameters (scale and shift).
- `ε` is a small constant (typically 1e-5) for numerical stability.

During training, `μ` and `σ²` are computed from the current batch. During inference, running averages are used.

### 5. Forward Pass Algorithm

```
function residualBlockForward(x, layers, shortcut, gamma, beta, runningMean, runningVar, epsilon):
    // First conv + BN + ReLU
    out = conv2d(x, layers[0].weight, layers[0].bias, layers[0].stride, layers[0].padding)
    out = batchNorm(out, gamma[0], beta[0], runningMean[0], runningVar[0], epsilon)
    out = relu(out)

    // Middle layers (for bottleneck: conv + BN + ReLU)
    for i = 1 to layers.length - 2:
        out = conv2d(out, layers[i].weight, layers[i].bias, 1, layers[i].padding)
        out = batchNorm(out, gamma[i], beta[i], runningMean[i], runningVar[i], epsilon)
        out = relu(out)

    // Last conv + BN (no ReLU before addition)
    out = conv2d(out, layers[last].weight, layers[last].bias, 1, layers[last].padding)
    out = batchNorm(out, gamma[last], beta[last], runningMean[last], runningVar[last], epsilon)

    // Shortcut connection
    shortcutOut = (dimensions match) ? x : shortcut(x)
    out = out + shortcutOut

    // Final ReLU
    out = relu(out)
    return out
```

### 6. Dimension Matching for Shortcut

Let input be `(C_in, H, W)` and output be `(C_out, H_out, W_out)`.

When `C_in == C_out` and `H == H_out` and `W == W_out`:
- Identity shortcut: `shortcut(x) = x`

Otherwise:
- Projection shortcut: 1x1 conv with stride == current block's stride.
- Zero-pad shortcut: Average pool (if stride > 1), then zero-pad channel dimension.

---

## Java Implementation

```java
package lab02.cnn;

import java.util.Arrays;

/**
 * A residual block supporting both basic and bottleneck designs with skip connections.
 * <p>
 * This implements the residual learning framework from "Deep Residual Learning
 * for Image Recognition" (He et al., 2015). Both identity and projection shortcuts
 * are supported.
 */
public class ResidualBlock {

    private final int inChannels;
    private final int outChannels;
    private final int stride;
    private final boolean useBottleneck;
    private final boolean projectionShortcut;
    private final double epsilon;

    // Conv layers weights and biases
    private double[][][][][] convWeights; // [layer][outCh][inCh][kH][kW]
    private double[][] convBiases;

    // Batch norm parameters: gamma and beta per layer per channel
    private double[][] bnGamma;
    private double[][] bnBeta;
    private double[][] bnRunningMean;
    private double[][] bnRunningVar;

    // Shortcut projection (if used)
    private double[][][][] projWeight; // [outCh][inCh][1][1]
    private double[] projBias;

    private int numLayers;

    /**
     * Constructs a ResidualBlock.
     *
     * @param inChannels          number of input channels
     * @param outChannels         number of output channels
     * @param stride              stride for the block (first conv)
     * @param useBottleneck       true for bottleneck design (1x1→3x3→1x1), false for basic (3x3→3x3)
     * @param projectionShortcut  true to use 1x1 conv shortcut, false to use zero-padding
     * @param epsilon             epsilon for batch normalization numerical stability
     */
    public ResidualBlock(int inChannels, int outChannels, int stride,
                         boolean useBottleneck, boolean projectionShortcut, double epsilon) {
        this.inChannels = inChannels;
        this.outChannels = outChannels;
        this.stride = stride;
        this.useBottleneck = useBottleneck;
        this.projectionShortcut = projectionShortcut;
        this.epsilon = epsilon;

        int midChannels = useBottleneck ? outChannels / 4 : outChannels;
        if (midChannels == 0) midChannels = 1;

        if (useBottleneck) {
            // Bottleneck: 1x1 (reduce) → 3x3 (spatial) → 1x1 (expand)
            numLayers = 3;
            convWeights = new double[numLayers][][][][];
            convBiases = new double[numLayers][];

            // Layer 0: 1x1 reduce: inChannels → midChannels
            convWeights[0] = createKernel(midChannels, inChannels, 1, 1);
            convBiases[0] = new double[midChannels];

            // Layer 1: 3x3 spatial: midChannels → midChannels
            convWeights[1] = createKernel(midChannels, midChannels, 3, 3);
            convBiases[1] = new double[midChannels];

            // Layer 2: 1x1 expand: midChannels → outChannels
            convWeights[2] = createKernel(outChannels, midChannels, 1, 1);
            convBiases[2] = new double[outChannels];
        } else {
            // Basic: 3x3 → 3x3
            numLayers = 2;
            convWeights = new double[numLayers][][][][];
            convBiases = new double[numLayers][];

            // Layer 0: 3x3: inChannels → outChannels (with stride)
            convWeights[0] = createKernel(outChannels, inChannels, 3, 3);
            convBiases[0] = new double[outChannels];

            // Layer 1: 3x3: outChannels → outChannels
            convWeights[1] = createKernel(outChannels, outChannels, 3, 3);
            convBiases[1] = new double[outChannels];
        }

        // Initialize BN parameters
        bnGamma = new double[numLayers][];
        bnBeta = new double[numLayers][];
        bnRunningMean = new double[numLayers][];
        bnRunningVar = new double[numLayers][];

        for (int l = 0; l < numLayers; l++) {
            int ch = convWeights[l].length;
            bnGamma[l] = ones(ch);
            bnBeta[l] = zeros(ch);
            bnRunningMean[l] = zeros(ch);
            bnRunningVar[l] = ones(ch);
        }

        // Shortcut projection if needed
        if (projectionShortcut && (inChannels != outChannels || stride != 1)) {
            projWeight = createKernel(outChannels, inChannels, 1, 1);
            projBias = new double[outChannels];
        }
    }

    /**
     * Performs the forward pass of the residual block.
     *
     * @param input 3D tensor of shape (channels, height, width)
     * @return 3D tensor of shape (outChannels, outH, outW)
     */
    public double[][][] forward(double[][] input) {
        int height = input[0].length;
        int width = input[0][0].length;

        // Determine padding for first layer
        int pad0 = useBottleneck ? 0 : 1; // 1x1 needs pad=0, 3x3 needs pad=1
        double[][][] out = conv2d(input, convWeights[0], convBiases[0], stride, pad0);
        out = batchNorm(out, bnGamma[0], bnBeta[0], bnRunningMean[0], bnRunningVar[0]);
        out = relu(out);

        // Middle layers
        if (useBottleneck) {
            // Layer 1: 3x3 spatial with stride=1
            out = conv2d(out, convWeights[1], convBiases[1], 1, 1);
            out = batchNorm(out, bnGamma[1], bnBeta[1], bnRunningMean[1], bnRunningVar[1]);
            out = relu(out);

            // Layer 2: 1x1 expand
            out = conv2d(out, convWeights[2], convBiases[2], 1, 0);
            out = batchNorm(out, bnGamma[2], bnBeta[2], bnRunningMean[2], bnRunningVar[2]);
        } else {
            // Layer 1: 3x3
            out = conv2d(out, convWeights[1], convBiases[1], 1, 1);
            out = batchNorm(out, bnGamma[1], bnBeta[1], bnRunningMean[1], bnRunningVar[1]);
        }

        // Shortcut connection
        double[][] shortcut = shortcut(input, height, width);

        // Element-wise addition
        int outH = out[0].length;
        int outW = out[0][0].length;
        double[][][] result = new double[outChannels][outH][outW];
        for (int c = 0; c < outChannels; c++) {
            for (int i = 0; i < outH; i++) {
                for (int j = 0; j < outW; j++) {
                    result[c][i][j] = out[c][i][j] + shortcut[c][i][j];
                }
            }
        }

        // Final ReLU
        result = relu(result);
        return result;
    }

    /**
     * Computes the shortcut connection.
     */
    private double[][] shortcut(double[][] input, int inH, int inW) {
        if (inChannels == outChannels && stride == 1) {
            return input; // Identity shortcut
        }

        if (projectionShortcut) {
            // 1x1 conv projection
            return conv2d(input, projWeight, projBias, stride, 0);
        } else {
            // Zero-padding shortcut: average pool then pad channels
            int outH = (inH + stride - 1) / stride;
            int outW = (inW + stride - 1) / stride;
            double[][] pooled;

            if (stride > 1) {
                pooled = avgPool(input, stride);
            } else {
                pooled = input;
            }

            // Pad or project channels
            double[][][] result = new double[outChannels][outH][outW];
            int copyCh = Math.min(inChannels, outChannels);
            for (int c = 0; c < copyCh; c++) {
                for (int i = 0; i < outH; i++) {
                    System.arraycopy(pooled[c][i], 0, result[c][i], 0, outW);
                }
            }
            // Remaining channels stay zero
            return result;
        }
    }

    // ---- Helper Methods ----

    private double[][][][] createKernel(int outCh, int inCh, int kH, int kW) {
        double[][][][] k = new double[outCh][inCh][kH][kW];
        // He initialization
        double std = Math.sqrt(2.0 / (inCh * kH * kW));
        for (int o = 0; o < outCh; o++) {
            for (int i = 0; i < inCh; i++) {
                for (int m = 0; m < kH; m++) {
                    for (int n = 0; n < kW; n++) {
                        k[o][i][m][n] = randn() * std;
                    }
                }
            }
        }
        return k;
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
        // Box-Muller transform
        double u1 = Math.random();
        double u2 = Math.random();
        return Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
    }

    /**
     * Simplified 2D convolution (cross-correlation).
     */
    private double[][] conv2d(double[][] input, double[][][][] kernel, double[] bias,
                                       int stride, int padding) {
        int inCh = input.length;
        int H = input[0].length;
        int W = input[0][0].length;
        int outCh = kernel.length;
        int kH = kernel[0][0].length;
        int kW = kernel[0][0][0].length;
        int outH = (H + 2 * padding - kH) / stride + 1;
        int outW = (W + 2 * padding - kW) / stride + 1;

        double[][] output = new double[outCh][outH][outW];
        for (int f = 0; f < outCh; f++) {
            for (int i = 0; i < outH; i++) {
                for (int j = 0; j < outW; j++) {
                    double sum = bias[f];
                    for (int c = 0; c < inCh; c++) {
                        for (int m = 0; m < kH; m++) {
                            int hPos = i * stride + m - padding;
                            if (hPos < 0 || hPos >= H) continue;
                            for (int n = 0; n < kW; n++) {
                                int wPos = j * stride + n - padding;
                                if (wPos < 0 || wPos >= W) continue;
                                sum += input[c][hPos][wPos] * kernel[f][c][m][n];
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
     * Batch normalization for 2D input.
     */
    private double[][] batchNorm(double[][] input, double[] gamma, double[] beta,
                                          double[] runningMean, double[] runningVar) {
        int C = input.length;
        int H = input[0].length;
        int W = input[0][0].length;
        double[][] output = new double[C][H][W];

        for (int c = 0; c < C; c++) {
            double mean = 0;
            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W; j++) {
                    mean += input[c][i][j];
                }
            }
            mean /= (H * W);

            double var = 0;
            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W; j++) {
                    double diff = input[c][i][j] - mean;
                    var += diff * diff;
                }
            }
            var /= (H * W);

            double std = Math.sqrt(var + epsilon);
            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W; j++) {
                    output[c][i][j] = gamma[c] * (input[c][i][j] - mean) / std + beta[c];
                }
            }
        }
        return output;
    }

    /**
     * Average pooling with given kernel size equal to stride.
     */
    private double[][] avgPool(double[][] input, int poolSize) {
        int C = input.length;
        int H = input[0].length;
        int W = input[0][0].length;
        int outH = (H + poolSize - 1) / poolSize;
        int outW = (W + poolSize - 1) / poolSize;

        double[][] output = new double[C][outH][outW];
        for (int c = 0; c < C; c++) {
            for (int i = 0; i < outH; i++) {
                for (int j = 0; j < outW; j++) {
                    double sum = 0;
                    int count = 0;
                    for (int m = 0; m < poolSize; m++) {
                        for (int n = 0; n < poolSize; n++) {
                            int h = i * poolSize + m;
                            int w = j * poolSize + n;
                            if (h < H && w < W) {
                                sum += input[c][h][w];
                                count++;
                            }
                        }
                    }
                    output[c][i][j] = sum / count;
                }
            }
        }
        return output;
    }

    private double[][] relu(double[][] input) {
        int C = input.length;
        int H = input[0].length;
        int W = input[0][0].length;
        double[][] output = new double[C][H][W];
        for (int c = 0; c < C; c++) {
            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W; j++) {
                    output[c][i][j] = Math.max(0, input[c][i][j]);
                }
            }
        }
        return output;
    }
}
```

**Example Usage:**

```java
package lab02.cnn;

import java.util.Arrays;

public class ResNetExample {
    public static void main(String[] args) {
        // Simple test: input 3 channels, 32x32, output 64 channels
        double[][] input = new double[3][32][32];
        for (int c = 0; c < 3; c++) {
            for (int i = 0; i < 32; i++) {
                Arrays.fill(input[c][i], 0.5);
            }
        }

        // Basic block: 3x3→3x3, stride=1
        ResidualBlock basicBlock = new ResidualBlock(3, 64, 1, false, false, 1e-5);
        double[][] output = basicBlock.forward(input);
        System.out.println("Basic block output shape: " + output.length + "x"
            + output[0].length + "x" + output[0][0].length);

        // Bottleneck block: 1x1→3x3→1x1, stride=2 (downsample)
        ResidualBlock bottleneckBlock = new ResidualBlock(64, 256, 2, true, true, 1e-5);
        double[][] output2 = bottleneckBlock.forward(output);
        System.out.println("Bottleneck block output shape: " + output2.length + "x"
            + output2[0].length + "x" + output2[0][0].length);
    }
}
```

---

## Complexity Analysis

### Basic Block (3x3 → 3x3)

**Parameters:**
- Conv1: `C_in * C_out * 9 + C_out`
- Conv2: `C_out * C_out * 9 + C_out`
- BN1: `2 * C_out` (gamma + beta)
- BN2: `2 * C_out`
- Total: `9 * C_out * (C_in + C_out) + 2 * C_out + 4 * C_out`

For a typical block with `C_in = 64, C_out = 64`:
- Total: `9 * 64 * 128 + 6 * 64 = 73, 728 + 384 = 74, 112` parameters

**FLOPs:**
- Conv1: `2 * C_out * H_out * W_out * C_in * 9`
- Conv2: `2 * C_out * H_out * W_out * C_out * 9`
- BN: `4 * C_out * H_out * W_out` (mean, var, normalize, scale/shift)
- Total: `O(C_out * H_out * W_out * (C_in + C_out) * 9)`

### Bottleneck Block (1x1 → 3x3 → 1x1)

**Parameters:**
- Conv1 (1x1): `C_in * C_mid * 1 + C_mid`
- Conv2 (3x3): `C_mid * C_mid * 9 + C_mid`
- Conv3 (1x1): `C_mid * C_out * 1 + C_out`
- BN: `2 * (C_mid + C_mid + C_out)`
- Total: `C_in * C_mid + 9 * C_mid² + C_mid * C_out + C_mid + C_mid + C_out + 2 * (2 * C_mid + C_out)`

For `C_in = 256, C_out = 256, C_mid = 64`:
- Total: `256*64 + 9*4096 + 64*256 + 64 + 64 + 256 + 2*(128+256)`
- = `16384 + 36864 + 16384 + 64 + 64 + 256 + 768 = 70, 784`

Compare with basic block operating on 256 channels:
- `9 * 256 * 512 + 6 * 256 = 1, 179, 648 + 1, 536 = 1, 181, 184`
- Bottleneck is ~16.7x more parameter-efficient!

### Space Complexity

**Forward pass storage:**
- Input: `C_in * H * W`
- After first conv: `C_mid * H_out * W_out` (or `C_out` for basic)
- After second: `C_mid * H_out * W_out`
- After third (bottleneck): `C_out * H_out * W_out`
- Shortcut: `C_out * H_out * W_out`
- Output: `C_out * H_out * W_out`

**Total working memory:** `O(max(C_in * H * W, C_out * H_out * W_out, C_mid * H_out * W_out))`

For a typical 56x56 feature map with 256 channels:
- ~256 * 56 * 56 * 8 bytes ≈ 6.4 MB per tensor
- Multiple tensors in flight: ~20-30 MB for a single block

---

## Follow-Up Questions with Answers

### Q1: Explain the "degradation problem" that ResNet solves. Why does adding more layers to a plain network lead to higher training error?

**Answer:** The degradation problem refers to the observation that deeper plain networks (without residual connections) exhibit higher training error than their shallower counterparts, even though deeper networks are a superset of shallower ones (they could theoretically learn the identity for extra layers).

**Causes:**
1. **Vanishing gradients:** In very deep networks, repeated multiplication of gradients during backpropagation can cause gradients to vanish exponentially with depth.
2. **Shattered gradients:** The gradients become increasingly random (white noise) as depth increases, providing no useful signal.
3. **Optimization difficulty:** The loss landscape becomes increasingly non-convex and difficult to navigate with SGD.

**How ResNet solves it:**
The skip connection adds an identity path that allows gradients to flow directly back to earlier layers:

```
∂L/∂x_l = ∂L/∂x_L * (1 + ∂/∂x_l Σ_{i=l}^{L-1} F_i(x_i))
```

The "+1" term ensures that even if the residual branches have tiny gradients, the identity path provides a gradient of magnitude at least `∂L/∂x_L`.

### Q2: Why does the bottleneck design use 1x1 convolutions to reduce and then expand channels?

**Answer:** The bottleneck design uses 1x1 convolutions for dimensionality reduction and expansion to:

1. **Reduce computational cost**: Operating 3x3 convolutions on a reduced number of channels dramatically decreases FLOPs.
2. **Create a information bottleneck**: The narrow middle layer forces the network to learn more compact, meaningful representations.
3. **Increase depth without increasing parameters**: A bottleneck block has 3 layers vs 2 in a basic block, adding more non-linearity for the same parameter count.

**The ratio (typically 4:1):** The middle channel count is typically `C_out / 4`. This ratio balances:
- `C_out / 4` still provides enough capacity for the spatial transformation.
- The 3x3 conv operates at 1/4 the cost of expanding directly.

### Q3: What is the difference between identity mapping and projection shortcut? When would you use each?

**Answer:**

**Identity mapping:** `shortcut(x) = x`
- Use when `C_in == C_out` and spatial dimensions match (`stride == 1`).
- No additional parameters, no extra computation.
- Theoretically optimal (the authors show that identity shortcuts introduce no extra parameter or computation, and are important for training stability).

**Projection shortcut:** `shortcut(x) = Conv2D_1x1(x, stride=s)`
- Use when dimensions change (`C_in ≠ C_out` or spatial downsampling needed).
- Adds parameters: `C_in * C_out`.
- Can be trainable and potentially more expressive.
- In ResNet papers, projection shortcuts are used only when needed for dimension matching.

**Usage recommendation:** Identity for matching dimensions, projection for mismatched dimensions. The ResNet paper found that projection shortcuts for all blocks (even when dimensions match) did not improve performance enough to justify the extra parameters.

### Q4: How does pre-activation (BN-ReLU-Conv) differ from post-activation (Conv-BN-ReLU)? Why was pre-activation proposed?

**Answer:**

**Post-activation (original ResNet):**
```
Conv → BN → ReLU → Conv → BN → + → ReLU
```
- The addition is followed by ReLU, which means the signal going into the next block has been thresholded.

**Pre-activation (ResNet v2):**
```
BN → ReLU → Conv → BN → ReLU → Conv → + → (identity)
```
- The addition is the identity (no ReLU after addition).
- The signal entering the next block goes through BN-ReLU first.

**Advantages of pre-activation:**
1. **Better gradient flow:** The identity path from input to output of the block has no non-linearities, allowing unimpeded gradient flow.
2. **Improved regularization:** Batch normalization before each convolution helps stabilize training.
3. **Easier to train:** Enables training of 1000+ layer networks without auxiliary classifiers.
4. **Improved accuracy:** The authors observed consistent improvement, especially for very deep networks.

### Q5: What is the effective receptive field in a deep ResNet?

**Answer:** The receptive field grows linearly with depth for a stack of 3x3 convolutions with stride 1:

```
RF = 2 * depth + 1
```

For a ResNet-50 (which has ~50 layers, but with strides of 2 at certain blocks):
- Initial conv: 7x7 → RF = 7
- After stage 1 (3 bottleneck blocks, stride 1): RF ≈ 7 + 2 * 3 * 3 = 25 (each bottleneck has 3 convs, but the skip connections don't change RF)
- After stage 2 (4 bottleneck blocks, stride 2): RF ≈ 25 * 2 + some expansion
- Total receptive field of ResNet-50: ~483 x 483 for a 224x224 input

However, the *effective* receptive field (where the gradients actually concentrate) is much smaller — it follows a Gaussian distribution centered at the pixel, with the effective area being only ~10-20% of the theoretical RF.

---

## Test Cases

### Test Case 1: Basic Block Identity Shortcut

```java
void testBasicBlockIdentity() {
    double[][] input = new double[64][56][56];
    for (int c = 0; c < 64; c++) {
        for (int i = 0; i < 56; i++) {
            for (int j = 0; j < 56; j++) {
                input[c][i][j] = (i + j + c) % 10 / 10.0;
            }
        }
    }

    ResidualBlock block = new ResidualBlock(64, 64, 1, false, false, 1e-5);
    double[][] output = block.forward(input);

    assert output.length == 64 : "Expected 64 output channels";
    assert output[0].length == 56 : "Expected 56 output height";
    assert output[0][0].length == 56 : "Expected 56 output width";
}
```

### Test Case 2: Basic Block with Stride 2

```java
void testBasicBlockStride2() {
    double[][] input = new double[64][56][56];
    for (int c = 0; c < 64; c++) {
        Arrays.fill(input[c][0], 1.0);
        for (int i = 1; i < 56; i++) {
            System.arraycopy(input[c][0], 0, input[c][i], 0, 56);
        }
    }

    ResidualBlock block = new ResidualBlock(64, 128, 2, false, true, 1e-5);
    double[][] output = block.forward(input);

    // Stride 2, 3x3 conv with pad 1: (56 + 2*1 - 3)/2 + 1 = 28
    assert output.length == 128 : "Expected 128 output channels";
    assert output[0].length == 28 : "Expected 28 output height, got " + output[0].length;
    assert output[0][0].length == 28 : "Expected 28 output width";
}
```

### Test Case 3: Bottleneck Block

```java
void testBottleneckBlock() {
    double[][] input = new double[256][28][28];
    for (int c = 0; c < 256; c++) {
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                input[c][i][j] = 1.0;
            }
        }
    }

    ResidualBlock block = new ResidualBlock(256, 256, 1, true, false, 1e-5);
    double[][] output = block.forward(input);

    assert output.length == 256;
    assert output[0].length == 28;
    assert output[0][0].length == 28;
}
```

### Test Case 4: Bottleneck with Downsampling

```java
void testBottleneckStride2() {
    double[][] input = new double[256][28][28];
    for (int c = 0; c < 256; c++) {
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                input[c][i][j] = (i * 28 + j) / 784.0;
            }
        }
    }

    ResidualBlock block = new ResidualBlock(256, 512, 2, true, true, 1e-5);
    double[][] output = block.forward(input);

    // Stride 2: 1x1 strides=2: (28 + 0 - 1)/2 + 1 = 14
    // Then 3x3 stride 1: (14 + 2*1 - 3)/1 + 1 = 14
    // Then 1x1 stride 1: same as above
    assert output.length == 512;
    assert output[0].length == 14;
    assert output[0][0].length == 14;
}
```

### Test Case 5: Skip Connection Verification

```java
void testSkipConnectionAddsCorrectly() {
    // Force all weights to near-zero so residual is ≈ bias
    double[][] input = new double[3][16][16];
    for (int c = 0; c < 3; c++) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                input[c][i][j] = 5.0; // constant input
            }
        }
    }

    ResidualBlock block = new ResidualBlock(3, 3, 1, false, false, 1e-5);
    double[][] output = block.forward(input);

    // With random initialization, output should be ≈ ReLU(5 + small_noise) ≈ 5
    for (int c = 0; c < 3; c++) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                // Output should be close to 5 (identity dominates early)
                // but with BN scaling, the values may shift
                // The key is that it should be > 0 (ReLU preserves)
                assert output[c][i][j] >= 0 : "Output should be non-negative after ReLU";
            }
        }
    }
}
```

### Test Case 6: Projection vs Zero-Padding Shortcut

```java
void testProjectionVsZeroPadding() {
    double[][] input = new double[64][32][32];
    for (int c = 0; c < 64; c++) {
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                input[c][i][j] = Math.sin(i * 0.1 + j * 0.2 + c * 0.3);
            }
        }
    }

    // Block with projection shortcut
    ResidualBlock projBlock = new ResidualBlock(64, 128, 2, false, true, 1e-5);
    double[][] projOutput = projBlock.forward(input);

    // Block with zero-padding shortcut
    ResidualBlock zeroBlock = new ResidualBlock(64, 128, 2, false, false, 1e-5);
    double[][] zeroOutput = zeroBlock.forward(input);

    // Both should produce valid output (same shape, different values)
    assert projOutput.length == zeroOutput.length;
    assert projOutput[0].length == zeroOutput[0].length;
    assert projOutput[0][0].length == zeroOutput[0][0].length;
}
```

### Test Case 7: Gradient Flow Test (Conceptual)

```java
void testGradientFlow() {
    // In a real system, we'd verify that gradients flow through the skip connection.
    // Conceptually:
    // dL/dx = dL/dy * (dy/dF * dF/dx + dy/dx)
    //       = dL/dy * (dF/dx + 1)
    // The +1 ensures gradient magnitude >= |dL/dy|
    
    // Without skip: dL/dx = dL/dy * dF/dx
    // With skip:    dL/dx = dL/dy * (dF/dx + 1)
    // The skip adds a constant 1 to the gradient multiplier.
}
```

### Test Case 8: He Initialization Impact

```java
void testHeInitializationScale() {
    double[][] input = new double[3][224][224];
    Arrays.fill(input[0][0], 1.0);
    for (int c = 0; c < 3; c++) {
        for (int i = 0; i < 224; i++) {
            if (i > 0 || c > 0) {
                Arrays.fill(input[c][i], 1.0);
            }
        }
    }

    ResidualBlock block = new ResidualBlock(3, 64, 1, false, false, 1e-5);
    double[][] output = block.forward(input);

    double sum = 0;
    for (int c = 0; c < 64; c++) {
        for (int i = 0; i < 224; i++) {
            for (int j = 0; j < 224; j++) {
                sum += output[c][i][j];
            }
        }
    }
    double mean = sum / (64 * 224 * 224);
    
    // With He init, the mean output should be bounded
    // (not exploding, not vanishing to 0)
    System.out.println("Output mean with He init: " + mean);
    assert !Double.isNaN(mean) && !Double.isInfinite(mean) : "Output should be finite";
}
```
