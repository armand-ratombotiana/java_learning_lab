# Model Quantization (INT8)

## Problem Statement

**Problem:** Implement post-training quantization (FP32 to INT8) with calibration, supporting both symmetric and asymmetric quantization, per-tensor and per-channel schemes, and dequantization.

Design and implement a `Quantizer` system that:
1. Calibrates quantization parameters (scale, zero point) from a calibration dataset.
2. Performs symmetric quantization ($\text{zero\_point} = 0$) for weights.
3. Performs asymmetric quantization ($\text{zero\_point} \neq 0$) for activations.
4. Supports per-tensor (same scale for all channels) and per-channel (different scale per channel) quantization.
5. Provides dequantization for converting back to FP32.
6. Computes quantization error metrics (MSE, SNR, max error).

**Example:**
```
FP32 weights: [0.5, -1.2, 3.7, -2.1, 0.0, 4.5, -3.3, 1.8]

Symmetric INT8 quantization:
  scale = max(|min|, |max|) / 127 = 4.5 / 127 = 0.03543
  zero_point = 0
  INT8: [14, -34, 104, -59, 0, 127, -93, 51]
  Dequantized: [0.496, -1.205, 3.685, -2.090, 0.0, 4.5, -3.295, 1.807]
```

**Constraints:**
- Input values are any double (FP32).
- INT8 range: $[-128, 127]$ for signed quantization.
- Calibration dataset size: $1 \leq N \leq 10^6$ samples.
- Must handle edge cases: all-zero weights, single outlier, constant tensors.

---

## Step-by-Step Solution Walkthrough

### 1. Why Quantization?

Model quantization reduces the precision of weights and activations from 32-bit floating point to lower bit-widths (typically INT8). Benefits:

- **4× memory reduction:** FP32 (4 bytes) → INT8 (1 byte).
- **2-4× speedup:** Integer operations are faster and more power-efficient than FP32.
- **Hardware support:** Modern CPUs (VNNI), GPUs (Tensor Cores), and mobile NPUs have INT8 acceleration.
- **Reduced bandwidth:** Smaller model size for deployment.

### 2. Quantization Fundamentals

#### Affine Quantization (Asymmetric)

$$q = \text{round}\left(\frac{r}{\Delta}\right) + Z$$

$$\Delta = \frac{r_{\max} - r_{\min}}{Q_{\max} - Q_{\min}}$$

$$Z = \text{round}\left(Q_{\max} - \frac{r_{\max}}{\Delta}\right) = \text{round}\left(-\frac{r_{\min}}{\Delta}\right)$$

Where:
- $r$ = real (FP32) value
- $q$ = quantized (INT8) value
- $\Delta$ = scale (step size)
- $Z$ = zero point (maps to $r=0$)
- $Q_{\max}, Q_{\min}$ = quantization range (127, -128 for INT8)

Dequantization:
$$r \approx \Delta \cdot (q - Z)$$

#### Symmetric Quantization

$$Z = 0, \quad \Delta = \frac{\max(|r|)}{Q_{\max}}$$

$$q = \text{round}\left(\frac{r}{\Delta}\right)$$

**Symmetric** is typically used for weights (symmetric distribution around 0). **Asymmetric** is used for activations (ReLU outputs are non-negative, so asymmetric captures the range better).

### 3. Calibration

Calibration determines the optimal $\Delta$ and $Z$ by observing the range of values on a representative dataset.

**Methods:**
1. **Min-max:** Use the observed min and max values directly. Simple but sensitive to outliers.
2. **Percentile:** Use the $p$-th percentile (e.g., 99.9%) instead of min/max. More robust.
3. **KL divergence (entropy):** Minimize KL divergence between original and quantized distributions. Used by TensorRT.
4. **MSE minimization:** Find $\Delta$ that minimizes mean squared quantization error.

### 4. Quantization Error

$$\text{Quantization Error} = |r - \Delta \cdot (q - Z)|$$

**Sources:**
1. **Rounding error:** From rounding to nearest integer. Bounded by $\pm \Delta/2$.
2. **Clipping error:** Values outside $[r_{\min}, r_{\max}]$ are clipped, causing larger errors.

**Signal-to-Quantization-Noise Ratio (SQNR):**
$$\text{SQNR (dB)} = 10 \cdot \log_{10}\left(\frac{\sigma_r^2}{\sigma_e^2}\right)$$

where $\sigma_r^2$ is signal variance and $\sigma_e^2$ is error variance.

### 5. Per-Tensor vs Per-Channel

**Per-tensor:** A single scale and zero point for all channels in a tensor.

**Per-channel:** Distinct scale and zero point for each output channel. Common for convolutional weights where each filter has different range.

Per-channel quantization gives lower error but requires more storage for metadata ($C$ scales vs 1).

---

## Java Implementation

```java
package com.deeplearning.quantization;

import java.util.Arrays;

/**
 * INT8 quantizer supporting symmetric and asymmetric quantization,
 * per-tensor and per-channel modes, with calibration.
 * 
 * <p>Provides FP32 → INT8 quantization and INT8 → FP32 dequantization
 * with configurable calibration strategies.</p>
 */
public class Quantizer {

    /** Quantization mode. */
    public enum Mode { SYMMETRIC, ASYMMETRIC }

    /** Calibration strategy. */
    public enum Calibration { MIN_MAX, PERCENTILE, MSE }

    // INT8 range
    private static final int QMIN = -128;
    private static final int QMAX = 127;
    private static final double Q_RANGE = QMAX - QMIN; // 255

    // Quantization parameters
    private final Mode mode;
    private final Calibration calibration;
    private final double percentile;
    private final boolean perChannel;

    // Per-tensor params
    private double scale;
    private int zeroPoint;

    // Per-channel params
    private double[] channelScales;
    private int[] channelZeroPoints;

    // Error tracking
    private double mse;
    private double maxError;
    private double snr;

    /**
     * Creates a Quantizer with the specified configuration.
     *
     * @param mode         symmetric or asymmetric
     * @param calibration  calibration strategy
     * @param percentile   percentile for PERCENTILE calibration (0-100)
     * @param perChannel   true for per-channel, false for per-tensor
     */
    public Quantizer(Mode mode, Calibration calibration,
                      double percentile, boolean perChannel) {
        this.mode = mode;
        this.calibration = calibration;
        this.percentile = percentile;
        this.perChannel = perChannel;
    }

    public Quantizer() {
        this(Mode.SYMMETRIC, Calibration.MIN_MAX, 100.0, false);
    }

    // ---------------------------------------------------------------
    // Calibration
    // ---------------------------------------------------------------

    /**
     * Calibrates quantization parameters from a calibration dataset.
     * For per-tensor: computes single scale/zeroPoint.
     * For per-channel: computes per-channel scales/zeroPoints.
     *
     * @param data calibration data, shape [samples][channels]
     */
    public void calibrate(double[][] data) {
        int samples = data.length;
        int channels = data[0].length;

        if (perChannel) {
            channelScales = new double[channels];
            channelZeroPoints = new int[channels];

            for (int c = 0; c < channels; c++) {
                double[] channelData = new double[samples];
                for (int s = 0; s < samples; s++) {
                    channelData[s] = data[s][c];
                }
                calibrateChannel(channelData, c);
            }
        } else {
            // Flatten all data
            double[] flat = new double[samples * channels];
            int idx = 0;
            for (double[] row : data) {
                System.arraycopy(row, 0, flat, idx, row.length);
                idx += row.length;
            }
            calibrateTensor(flat);
        }
    }

    /**
     * Calibrates from a 1D tensor (flattened weights or activations).
     */
    public void calibrateTensor(double[] data) {
        double rmin, rmax;

        switch (calibration) {
            case MIN_MAX:
                rmin = min(data);
                rmax = max(data);
                break;
            case PERCENTILE:
                double[] sorted = data.clone();
                Arrays.sort(sorted);
                int lowerIdx = (int) ((100.0 - percentile) / 200.0 * sorted.length);
                int upperIdx = (int) ((100.0 + percentile) / 200.0 * sorted.length);
                lowerIdx = Math.max(0, lowerIdx);
                upperIdx = Math.min(sorted.length - 1, upperIdx);
                rmin = sorted[lowerIdx];
                rmax = sorted[upperIdx];
                break;
            case MSE:
                // Find optimal scale by grid search
                double bestScale = 0;
                double bestMse = Double.MAX_VALUE;
                double minVal = min(data);
                double maxVal = max(data);
                for (int step = 1; step <= 1000; step++) {
                    double candidateScale = (maxVal - minVal) / Q_RANGE * step / 1000;
                    if (candidateScale < 1e-20) continue;
                    double mse = computeMse(data, candidateScale,
                        mode == Mode.SYMMETRIC ? 0 : (int) Math.round(-minVal / candidateScale));
                    if (mse < bestMse) {
                        bestMse = mse;
                        bestScale = candidateScale;
                    }
                }
                rmin = mode == Mode.SYMMETRIC ? -bestScale * QMAX : -bestScale * QMIN;
                rmax = mode == Mode.SYMMETRIC ? bestScale * QMAX : bestScale * QMAX;
                break;
            default:
                throw new IllegalStateException("Unknown calibration: " + calibration);
        }

        computeParams(rmin, rmax);
        computeError(data);
    }

    private void calibrateChannel(double[] channelData, int channelIdx) {
        double rmin = min(channelData);
        double rmax = max(channelData);

        if (mode == Mode.SYMMETRIC) {
            double absMax = Math.max(Math.abs(rmin), Math.abs(rmax));
            double scaleCh = absMax / QMAX;
            channelScales[channelIdx] = scaleCh > 0 ? scaleCh : 1e-10;
            channelZeroPoints[channelIdx] = 0;
        } else {
            double scaleCh = (rmax - rmin) / Q_RANGE;
            int zp = (int) Math.round(-rmin / scaleCh);
            zp = clamp(zp, QMIN, QMAX);
            channelScales[channelIdx] = scaleCh > 0 ? scaleCh : 1e-10;
            channelZeroPoints[channelIdx] = zp;
        }
    }

    private void computeParams(double rmin, double rmax) {
        if (mode == Mode.SYMMETRIC) {
            double absMax = Math.max(Math.abs(rmin), Math.abs(rmax));
            scale = absMax / QMAX;
            zeroPoint = 0;
        } else {
            scale = (rmax - rmin) / Q_RANGE;
            zeroPoint = (int) Math.round(-rmin / scale);
            zeroPoint = clamp(zeroPoint, QMIN, QMAX);
        }
        if (scale < 1e-10) scale = 1e-10;
    }

    // ---------------------------------------------------------------
    // Quantization & Dequantization
    // ---------------------------------------------------------------

    /**
     * Quantizes a single value using per-tensor parameters.
     */
    public byte quantize(double value) {
        double scaled = value / scale + zeroPoint;
        return (byte) clamp((int) Math.round(scaled), QMIN, QMAX);
    }

    /**
     * Dequantizes a single quantized value.
     */
    public double dequantize(byte quantized) {
        return scale * (quantized - zeroPoint);
    }

    /**
     * Quantizes a 1D array (per-tensor or per-channel depending on configuration).
     */
    public byte[] quantize(double[] values) {
        byte[] q = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            q[i] = quantize(values[i]);
        }
        return q;
    }

    /**
     * Quantizes a 2D array with per-channel quantization.
     * First dimension is channels, second dimension is elements per channel.
     */
    public byte[] quantizePerChannel(double[][] values) {
        if (!perChannel) {
            throw new IllegalStateException("Quantizer not configured for per-channel");
        }
        int channels = values.length;
        int elemsPerChannel = values[0].length;
        byte[] result = new byte[channels * elemsPerChannel];

        for (int c = 0; c < channels; c++) {
            double s = channelScales[c];
            int zp = channelZeroPoints[c];
            for (int e = 0; e < elemsPerChannel; e++) {
                double scaled = values[c][e] / s + zp;
                result[c * elemsPerChannel + e] =
                    (byte) clamp((int) Math.round(scaled), QMIN, QMAX);
            }
        }
        return result;
    }

    /**
     * Dequantizes a byte array back to doubles.
     */
    public double[] dequantize(byte[] quantized) {
        double[] result = new double[quantized.length];
        for (int i = 0; i < quantized.length; i++) {
            result[i] = scale * (quantized[i] - zeroPoint);
        }
        return result;
    }

    // ---------------------------------------------------------------
    // Error Computation
    // ---------------------------------------------------------------

    private void computeError(double[] original) {
        double sumSqError = 0;
        double sumSqSignal = 0;
        maxError = 0;

        for (double v : original) {
            byte q = quantize(v);
            double reconstructed = dequantize(q);
            double error = v - reconstructed;
            sumSqError += error * error;
            sumSqSignal += v * v;
            maxError = Math.max(maxError, Math.abs(error));
        }

        mse = sumSqError / original.length;
        snr = sumSqSignal > 0 ? 10 * Math.log10(sumSqSignal / sumSqError) : 0;
    }

    private double computeMse(double[] data, double testScale, int testZp) {
        double sumSqError = 0;
        for (double v : data) {
            double scaled = v / testScale + testZp;
            byte q = (byte) clamp((int) Math.round(scaled), QMIN, QMAX);
            double reconstructed = testScale * (q - testZp);
            double error = v - reconstructed;
            sumSqError += error * error;
        }
        return sumSqError / data.length;
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double min(double[] arr) {
        double m = arr[0];
        for (double v : arr) if (v < m) m = v;
        return m;
    }

    private static double max(double[] arr) {
        double m = arr[0];
        for (double v : arr) if (v > m) m = v;
        return m;
    }

    // --- Getters ---

    public double getScale() { return scale; }
    public int getZeroPoint() { return zeroPoint; }
    public double getMse() { return mse; }
    public double getMaxError() { return maxError; }
    public double getSnr() { return snr; }
    public double[] getChannelScales() { return channelScales; }
    public int[] getChannelZeroPoints() { return channelZeroPoints; }
}
```

### Test Harness

```java
package com.deeplearning.quantization;

import java.util.Arrays;

/**
 * Test harness for INT8 quantization.
 * Validates calibration, quantization, dequantization, error metrics,
 * symmetric/asymmetric modes, and per-channel quantization.
 */
public class QuantizerTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        testSymmetric();
        testAsymmetric();
        testSymmetricRoundTrip();
        testAsymmetricRoundTrip();
        testPerTensorCalibration();
        testPerChannelCalibration();
        testPercentileCalibration();
        testMseCalibration();
        testAllZeroWeights();
        testOutlierHandling();
        testErrorMetrics();
        testEdgeCases();
        System.out.printf("%n=== Results: %d passed, %d failed ===%n", passed, failed);
    }

    static void assertTest(boolean condition, String name) {
        if (condition) { passed++; System.out.printf("[PASS] %s%n", name); }
        else { failed++; System.err.printf("[FAIL] %s%n", name); }
    }

    static boolean approx(double a, double b, double tol) {
        return Math.abs(a - b) < tol;
    }

    static void testSymmetric() {
        Quantizer q = new Quantizer(Quantizer.Mode.SYMMETRIC,
            Quantizer.Calibration.MIN_MAX, 100.0, false);
        double[] data = {0.5, -1.2, 3.7, -2.1, 0.0, 4.5, -3.3, 1.8};
        q.calibrateTensor(data);

        // Symmetric: zero_point should be 0
        assertTest(q.getZeroPoint() == 0, "Symmetric zero_point = 0");

        // Scale = max_abs / 127 = 4.5 / 127 ≈ 0.03543
        assertTest(approx(q.getScale(), 4.5 / 127, 1e-6), "Symmetric scale = max/127");

        // Verify round-trip error is small
        byte[] qData = q.quantize(data);
        double[] deq = q.dequantize(qData);

        for (int i = 0; i < data.length; i++) {
            assertTest(approx(data[i], deq[i], q.getScale()),
                "Symmetric round-trip for index " + i);
        }
    }

    static void testAsymmetric() {
        Quantizer q = new Quantizer(Quantizer.Mode.ASYMMETRIC,
            Quantizer.Calibration.MIN_MAX, 100.0, false);
        double[] data = {0.5, 1.0, 0.0, 2.5, 1.5, 3.0, 0.5, 1.0};
        q.calibrateTensor(data);

        // Asymmetric: zero_point should map 0 to quantized value
        // For all non-negative data, zero_point should be 0
        assertTest(q.getZeroPoint() >= 0, "Asymmetric zero_point >= 0");

        // Scale should be range / 255
        double expectedScale = (3.0 - 0.0) / 255.0;
        assertTest(approx(q.getScale(), expectedScale, 1e-6), "Asymmetric scale = range/255");
    }

    static void testSymmetricRoundTrip() {
        Quantizer q = new Quantizer();
        double[] data = {-5.0, -4.0, -3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0};
        q.calibrateTensor(data);

        byte[] qd = q.quantize(data);
        double[] deq = q.dequantize(qd);

        // Max error should be ≤ scale/2 (rounding error)
        double maxErr = 0;
        for (int i = 0; i < data.length; i++) {
            maxErr = Math.max(maxErr, Math.abs(data[i] - deq[i]));
        }
        assertTest(maxErr <= q.getScale() / 2 + 1e-10,
            "Symmetric round-trip max error ≤ scale/2");
    }

    static void testAsymmetricRoundTrip() {
        Quantizer q = new Quantizer(Quantizer.Mode.ASYMMETRIC,
            Quantizer.Calibration.MIN_MAX, 100.0, false);
        double[] data = {0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0};
        q.calibrateTensor(data);

        byte[] qd = q.quantize(data);
        double[] deq = q.dequantize(qd);

        double maxErr = 0;
        for (int i = 0; i < data.length; i++) {
            maxErr = Math.max(maxErr, Math.abs(data[i] - deq[i]));
        }
        assertTest(maxErr <= q.getScale() / 2 + 1e-10,
            "Asymmetric round-trip max error ≤ scale/2");
    }

    static void testPerTensorCalibration() {
        Quantizer q = new Quantizer(Quantizer.Mode.SYMMETRIC,
            Quantizer.Calibration.MIN_MAX, 100.0, false);
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        };
        q.calibrate(data);

        // Per-tensor: should have a single scale
        assertTest(q.getScale() > 0, "Per-tensor: single scale > 0");
    }

    static void testPerChannelCalibration() {
        Quantizer q = new Quantizer(Quantizer.Mode.SYMMETRIC,
            Quantizer.Calibration.MIN_MAX, 100.0, true);
        double[][] data = {
            {0.1, 0.2, 0.3, 0.4, 0.5},  // channel 0: small range
            {100.0, 200.0, 300.0, 400.0, 500.0} // channel 1: large range
        };
        q.calibrate(data);

        double[] scales = q.getChannelScales();
        // Channel 1 should have larger scale than channel 0
        assertTest(scales[1] > scales[0],
            "Per-channel: different scales for different channels");
        assertTest(scales.length == 2, "Per-channel: correct number of scales");
    }

    static void testPercentileCalibration() {
        // With min-max, a single outlier would set the range
        // With percentile, the outlier is ignored
        double[] data = new double[1000];
        for (int i = 0; i < 999; i++) data[i] = Math.sin(i * 0.1);
        data[999] = 1000.0; // outlier

        Quantizer minMax = new Quantizer(Quantizer.Mode.SYMMETRIC,
            Quantizer.Calibration.MIN_MAX, 100.0, false);
        minMax.calibrateTensor(data);

        Quantizer percentile = new Quantizer(Quantizer.Mode.SYMMETRIC,
            Quantizer.Calibration.PERCENTILE, 99.9, false);
        percentile.calibrateTensor(data);

        // Percentile calibration should have smaller scale (ignores outlier)
        assertTest(percentile.getScale() < minMax.getScale(),
            "Percentile calibration smaller scale than min-max");
    }

    static void testMseCalibration() {
        Quantizer q = new Quantizer(Quantizer.Mode.SYMMETRIC,
            Quantizer.Calibration.MSE, 100.0, false);
        double[] data = {-3.0, -1.5, 0.0, 1.5, 3.0};
        q.calibrateTensor(data);

        // MSE calibration should produce reasonable results
        assertTest(q.getScale() > 0, "MSE calibration: positive scale");
        assertTest(q.getSnr() > 10, "MSE calibration: SNR > 10 dB");
    }

    static void testAllZeroWeights() {
        Quantizer q = new Quantizer();
        double[] data = {0.0, 0.0, 0.0, 0.0};
        q.calibrateTensor(data);

        // All zeros: scale should be epsilon
        assertTest(q.getScale() > 0, "All-zero: scale > 0 (epsilon)");
        assertTest(q.getZeroPoint() == 0, "All-zero: zero_point = 0");
    }

    static void testOutlierHandling() {
        Quantizer q = new Quantizer();
        double[] data = {-1.0, -0.5, 0.0, 0.5, 1.0, 100.0}; // outlier at 100
        q.calibrateTensor(data);

        // Scale will be large due to outlier
        // Many values will be quantized to very few levels
        byte[] qd = q.quantize(data);
        // Verify outlier maps to 127
        assertTest(qd[5] == 127, "Outlier quantized to 127");
        // Normal values should be close to their correct quantized values
        assertTest(qd[0] <= -1, "Normal value quantized reasonably");
    }

    static void testErrorMetrics() {
        Quantizer q = new Quantizer();
        double[] data = {-1.0, -0.5, 0.0, 0.5, 1.0};
        q.calibrateTensor(data);

        assertTest(q.getMse() >= 0, "Quantization MSE non-negative");
        assertTest(q.getMaxError() >= 0, "Quantization max error non-negative");
        assertTest(q.getSnr() > 0, "Quantization SNR positive (dB)");
    }

    static void testEdgeCases() {
        // Single element
        Quantizer q = new Quantizer();
        q.calibrateTensor(new double[]{3.14});
        byte qv = q.quantize(3.14);
        assertTest(qv > 0 && qv <= 127, "Single value quantized");

        // Negative values only
        q.calibrateTensor(new double[]{-10.0, -5.0, -1.0});
        assertTest(q.quantize(-10.0) == -128, "All negative: smallest maps to -128 (symmetric)");
    }
}
```

---

## Complexity Analysis

### Time Complexity

**Calibration (min-max):** $O(N)$ where $N$ is the number of values — one pass to find min/max.

**Calibration (percentile):** $O(N \log N)$ — requires sorting.

**Calibration (MSE):** $O(N \cdot K)$ where $K$ is the number of candidate scales (grid search). Inefficient for large $N$.

**Quantization (per value):** $O(1)$ — one division, one addition, one round, one clamp.

**Dequantization (per value):** $O(1)$ — one subtraction, one multiply.

### Space Complexity

- **Per-tensor:** $O(1)$ — store 2 values (scale, zero point).
- **Per-channel:** $O(C)$ where $C$ is the number of channels.
- **Quantized data:** $O(N)$ — 1 byte per value (vs 4 bytes for FP32).

---

## Follow-Up Questions

### Q1: Compare symmetric vs. asymmetric quantization. When would you use each?

**Answer:**

| Property | Symmetric | Asymmetric |
|---------|-----------|------------|
| Zero point | 0 (fixed) | Non-zero (learned) |
| Range | $[-\alpha, \alpha]$ | $[\alpha_{\min}, \alpha_{\max}]$ |
| INT8 utilization | Full range for symmetric data | Better for skewed data |
| Computation | Simpler (no ZP subtraction) | Requires ZP correction |
| Weights | Usually symmetric (mean ≈ 0) | Rarely needed |
| Activations | Less efficient (if ReLU, only positive) | More efficient (captures distribution) |

**Guidelines:**
- **Weights:** Always symmetric (they're approximately zero-centered).
- **Activations:** Asymmetric for ReLU outputs (all non-negative).
- **Hardware:** Some accelerators only support symmetric (e.g., NVIDIA TensorRT).

### Q2: What is the difference between quantization-aware training (QAT) and post-training quantization (PTQ)?

**Answer:**

| Aspect | PTQ | QAT |
|--------|-----|-----|
| When applied | After training | During training |
| Data needed | Small calibration set | Training data |
| Accuracy loss | 1-3% (INT8) | < 1% (INT8) |
| Time | Minutes | Full training time |
| Complexity | Low | High (simulated quantization) |
| Use case | Quick deployment | Maximum accuracy |

**PTQ:** Calibrate on a small dataset, quantize all weights/activations. Fast but may lose accuracy.

**QAT:** Insert "fake quantization" nodes in the computation graph during training, model learns to be robust to quantization:

```java
// QAT: simulate quantization during forward pass
double fakeQuantize(double x, double scale, int zp) {
    byte q = quantize(x, scale, zp);
    double deq = dequantize(q, scale, zp);
    return deq; // returns FP32 but with quantization noise
}
```

### Q3: Derive the optimal scale for symmetric quantization under MSE.

**Answer:** For symmetric quantization, we minimize:
$$\text{MSE}(\Delta) = \frac{1}{N} \sum_i \left(r_i - \Delta \cdot q_i\right)^2$$

where $q_i = \text{round}\left(\frac{r_i}{\Delta}\right)$ and $q_i \in [-128, 127]$.

For a given $\Delta$, the clipping threshold is $T = 127 \cdot \Delta$. The total MSE has two components:
1. **Rounding error** inside $[-T, T]$: $\frac{\Delta^2}{12}$ (uniform quantization theory).
2. **Clipping error** outside $[-T, T]$: $\mathbb{E}[(r - T)^2 | r > T] \cdot P(r > T)$.

The optimal $\Delta$ balances these two. For Laplacian or Gaussian distributed weights, this can be solved analytically or via grid search (as implemented).

### Q4: How do you handle bias quantization?

**Answer:** Bias vectors are typically kept in FP32 (higher precision needed) because:
1. Biases are added after the quantized dot product, so they can be accumulated in INT32.
2. Biases are few (one per output channel), so FP32 storage overhead is negligible.
3. Bias quantization error compounds across layers.

**Practical approach:** Keep biases in FP32 or INT32, not INT8.

### Q5: Explain per-channel quantization for convolutional layers.

**Answer:** In a convolutional layer, each output channel has its own filter. Different filters can have very different weight ranges. With per-tensor quantization, the scale is set by the worst outlier across all channels, causing all other channels to lose precision.

**Per-channel quantization:** Each output channel $c$ has its own scale $\Delta_c$ and zero point $Z_c$:

$$q_{c,i} = \text{round}\left(\frac{W_{c,i}}{\Delta_c}\right) + Z_c$$

Benefits:
- Each channel uses its optimal quantization range.
- No single outlier degrades all channels.
- Standard in TensorRT, ONNX Runtime, PyTorch.

### Q6: What is quantization granularity (per-tensor, per-channel, per-group)?

**Answer:**

| Granularity | Description | Overhead | Accuracy |
|------------|-------------|----------|----------|
| Per-tensor | 1 scale for all values | Minimal | Lower |
| Per-channel | 1 scale per output channel | Moderate | Good |
| Per-group | 1 scale per group of K values | Higher | Best |
| Per-element | 1 scale per value (FP32) | None | Reference |

**Per-group quantization** (e.g., group size 32 or 64) is used in GPTQ and AWQ for LLM quantization. It provides a balance between granularity and overhead.

### Q7: How does INT8 quantization affect inference speed on modern hardware?

**Answer:** INT8 inference speedup depends on hardware:

| Hardware | INT8 Support | Speedup vs FP32 |
|----------|-------------|-----------------|
| CPU (x86 VNNI) | AVX-512 VNNI, AVX-VNNI | 2-4× |
| NVIDIA GPU (Turing+) | Tensor Cores (INT8) | 2-4× |
| NVIDIA GPU (Ampere+) | Tensor Cores (INT8/INT4) | 4-8× |
| Qualcomm Hexagon | HVX, HTA | 2-8× |
| Apple Neural Engine | ANE (INT8) | 10-20× |

**Key operations accelerated:**
- Matrix multiply: INT8 with INT32 accumulation.
- Convolution: INT8 Tensor Core operations.
- Memory bandwidth: 4× less data movement.

### Q8: What is "smooth quantization" and how does it address activation outliers?

**Answer:** SmoothQuant (Xiao et al., 2023) addresses the problem that activations in LLMs are much harder to quantize than weights due to outlier channels. The key insight:

**Shift difficulty from activations to weights:**
$$\text{diag}(s)^{-1} \cdot (\mathbf{W} \cdot \text{diag}(s)) \cdot \mathbf{x} = \mathbf{W}' \cdot \mathbf{x}'$$

By choosing a per-channel smoothing factor $s$, we can:
- Divide activation channels by $s$ (making activations smoother).
- Multiply weight columns by $s$ (absorbing the scaling).

The smoothing factor is chosen to balance the quantization difficulty:
$$s_j = \max_j(|X_j|)^{\alpha} / \max_j(|W_j|)^{1-\alpha}$$

where $\alpha$ controls the trade-off (typically $\alpha = 0.5$).

This enables INT8 quantization of LLMs (like OPT-175B) with negligible accuracy loss.

---

## Test Cases

| Test Case | Mode | Data | Expected |
|-----------|------|------|----------|
| TC-01 | Symmetric | [-3.3, 4.5] | scale = 4.5/127, zp = 0 |
| TC-02 | Asymmetric | [0, 3.0] | scale = 3/255, zp >= 0 |
| TC-03 | Symmetric round-trip | [-5..5] | max error ≤ scale/2 |
| TC-04 | Asymmetric round-trip | [0..3] | max error ≤ scale/2 |
| TC-05 | Per-channel | 2 channels, different ranges | Different scales per channel |
| TC-06 | Percentile | 1000 points + 1 outlier | Smaller scale than min-max |
| TC-07 | All zeros | [0,0,0,0] | scale = epsilon |
| TC-08 | Single value | [3.14] | quantized value in (0, 127] |
| TC-09 | Negative only | [-10, -5, -1] | -10 → -128 (symmetric) |
| TC-10 | Error metrics | [-1, -0.5, 0, 0.5, 1] | MSE ≥ 0, SNR > 0 |

---

## Key Takeaways

- **Quantization** reduces model size (4× for INT8) and accelerates inference.
- **Symmetric** (ZP = 0) for weights; **asymmetric** (ZP ≠ 0) for activations.
- **Calibration** determines optimal scale/zero point via min-max, percentile, or MSE minimization.
- **Per-channel** quantization preserves accuracy for convolutional filters with different ranges.
- **Quantization error** has two sources: rounding (±Δ/2) and clipping (outliers).
- **PTQ** is fast but less accurate; **QAT** is slower but better for low-bit quantization.
- Modern quantization techniques (SmoothQuant, GPTQ) enable INT4/INT8 for LLMs with minimal accuracy loss.
