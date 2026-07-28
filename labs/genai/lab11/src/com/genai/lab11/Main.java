package com.genai.lab11;

import java.util.*;

/**
 * Model Quantization & Deployment
 * 
 * Demonstrates FP16 conversion, INT8 symmetric/asymmetric quantization,
 * calibration, and graph optimization concepts in Java.
 */
public class Main {

    /** FP16: simulate half-precision float storage. */
    static short toFP16(float value) {
        int fbits = Float.floatToIntBits(value);
        int sign = (fbits >> 31) & 1;
        int exp = (fbits >> 23) & 0xff;
        int mant = fbits & 0x7fffff;

        if (exp == 0) return 0;
        if (exp == 0xff) return (short) ((sign << 15) | 0x7c00 | (mant >> 13));

        int newExp = exp - 127 + 15;
        if (newExp >= 31) return (short) ((sign << 15) | 0x7c00);
        if (newExp <= 0) return 0;

        return (short) ((sign << 15) | (newExp << 10) | (mant >> 13));
    }

    static float fromFP16(short hbits) {
        int sign = (hbits >> 15) & 1;
        int exp = (hbits >> 10) & 0x1f;
        int mant = hbits & 0x3ff;

        if (exp == 0) {
            return (sign == 0) ? 0.0f : -0.0f;
        }
        if (exp == 31) {
            return (mant == 0) ? (sign == 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY)
                               : Float.NaN;
        }

        int fbits = (sign << 31) | ((exp - 15 + 127) << 23) | (mant << 13);
        return Float.intBitsToFloat(fbits);
    }

    /** INT8 symmetric quantization. */
    static class SymmetricQuantization {
        final double scale;
        final int[] quantized;

        SymmetricQuantization(double[] weights) {
            double absMax = 0.0;
            for (double w : weights) if (Math.abs(w) > absMax) absMax = Math.abs(w);
            scale = absMax / 127.0;
            quantized = new int[weights.length];
            for (int i = 0; i < weights.length; i++) {
                quantized[i] = (int) Math.round(weights[i] / scale);
                quantized[i] = Math.clamp(quantized[i], -128, 127);
            }
        }

        double[] dequantize() {
            double[] result = new double[quantized.length];
            for (int i = 0; i < quantized.length; i++) result[i] = quantized[i] * scale;
            return result;
        }
    }

    /** INT8 asymmetric quantization with zero-point. */
    static class AsymmetricQuantization {
        final double scale;
        final int zeroPoint;
        final int[] quantized;

        AsymmetricQuantization(double[] weights) {
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (double w : weights) { if (w < min) min = w; if (w > max) max = w; }
            scale = (max - min) / 255.0;
            zeroPoint = (int) Math.round(-min / scale);
            quantized = new int[weights.length];
            for (int i = 0; i < weights.length; i++) {
                quantized[i] = (int) Math.round(weights[i] / scale) + zeroPoint;
                quantized[i] = Math.clamp(quantized[i], 0, 255);
            }
        }

        double[] dequantize() {
            double[] result = new double[quantized.length];
            for (int i = 0; i < quantized.length; i++) result[i] = (quantized[i] - zeroPoint) * scale;
            return result;
        }
    }

    /** Graph optimization (TensorRT concept): fuse operations. */
    static class GraphOptimizer {
        record Node(String op, double[] weights) {}
        record FusedNode(String op, double[] weights) {}

        static FusedNode fuseConvBiasRelu(double[] convWeights, double[] bias) {
            double[] fused = Arrays.copyOf(convWeights, convWeights.length);
            for (int i = 0; i < fused.length; i++) {
                fused[i] = Math.max(0, fused[i] + bias[i % bias.length]);
            }
            return new FusedNode("Conv+Bias+ReLU", fused);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== FP16 Conversion ===");
        float orig = 3.14159f;
        short fp16 = toFP16(orig);
        float back = fromFP16(fp16);
        System.out.printf("%.5f -> FP16(0x%04x) -> %.5f%n", orig, fp16, back);

        double[] weights = {0.5, -1.2, 0.8, -0.3, 2.1, -1.5, 0.0, -0.7};
        System.out.println("\n=== INT8 Symmetric ===");
        SymmetricQuantization sq = new SymmetricQuantization(weights);
        System.out.println("Scale: " + sq.scale);
        System.out.println("Quantized: " + Arrays.toString(sq.quantized));

        System.out.println("\n=== INT8 Asymmetric ===");
        AsymmetricQuantization aq = new AsymmetricQuantization(weights);
        System.out.println("Scale: " + aq.scale + ", zeroPoint: " + aq.zeroPoint);
        System.out.println("Quantized: " + Arrays.toString(aq.quantized));

        System.out.println("\n=== Dequantization Error ===");
        double[] deqS = sq.dequantize();
        double[] deqA = aq.dequantize();
        double errS = 0, errA = 0;
        for (int i = 0; i < weights.length; i++) {
            errS += Math.abs(weights[i] - deqS[i]);
            errA += Math.abs(weights[i] - deqA[i]);
        }
        System.out.printf("Symmetric error: %.4f%n", errS);
        System.out.printf("Asymmetric error: %.4f%n", errA);

        System.out.println("\n=== Graph Optimization (Fusion) ===");
        double[] convW = {0.5, -0.2, 0.3, 0.1};
        double[] bias = {0.1, -0.1};
        var fused = GraphOptimizer.fuseConvBiasRelu(convW, bias);
        System.out.println("Fused op: " + fused.op());
        System.out.println("Fused weights: " + Arrays.toString(fused.weights()));

        System.out.println("\nQuantization & deployment concepts validated.");
    }
}
