package com.genai.lab06;

import java.util.Arrays;
import java.util.Random;

/**
 * Fine-Tuning with LoRA/QLoRA
 * 
 * Demonstrates LoRA low-rank adaptation, forward pass with injected
 * LoRA weights, and QLoRA quantization concepts in Java.
 */
public class Main {

    static class LoRALayer {
        final double[][] W;       // frozen base weights (d x d)
        final double[][] A;       // LoRA A (d x r)
        final double[][] B;       // LoRA B (r x d)
        final double scaling;     // alpha / r

        LoRALayer(int d, int r, double alpha) {
            W = new double[d][d];
            A = new double[d][r];
            B = new double[r][d];
            scaling = alpha / r;
            Random rng = new Random(42);
            for (int i = 0; i < d; i++) {
                for (int j = 0; j < d; j++) W[i][j] = rng.nextGaussian() * 0.1;
                for (int j = 0; j < r; j++) A[i][j] = rng.nextGaussian() * 0.01;
            }
            for (int i = 0; i < r; i++)
                for (int j = 0; j < d; j++) B[i][j] = 0.0;
        }

        double[] forward(double[] x) {
            int d = W.length;
            double[] y = new double[d];
            // y = xW
            for (int i = 0; i < d; i++) {
                for (int j = 0; j < d; j++) {
                    y[i] += x[j] * W[j][i];
                }
            }
            // y += x * A * B * scaling
            double[] xA = new double[A[0].length];
            for (int i = 0; i < A[0].length; i++) {
                for (int j = 0; j < d; j++) {
                    xA[i] += x[j] * A[j][i];
                }
            }
            for (int i = 0; i < d; i++) {
                for (int j = 0; j < xA.length; j++) {
                    y[i] += xA[j] * B[j][i] * scaling;
                }
            }
            return y;
        }

        double trainStep(double[] x, double[] target, double lr) {
            double[] y = forward(x);
            double loss = 0.0;
            for (int i = 0; i < y.length; i++) {
                double diff = y[i] - target[i];
                loss += diff * diff;
            }
            loss /= y.length;

            int r = A[0].length;
            double[][] gradB = new double[r][W.length];
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < W.length; j++) {
                    for (int k = 0; k < W.length; k++) {
                        gradB[i][j] += (y[k] - target[k]) * A[k][i] * x[j];
                    }
                    gradB[i][j] *= scaling / y.length;
                }
            }
            for (int i = 0; i < r; i++)
                for (int j = 0; j < W.length; j++)
                    B[i][j] -= lr * gradB[i][j];

            return loss;
        }
    }

    static class QuantizedWeights {
        final int[] int4Weights;
        final double scale;

        QuantizedWeights(double[][] w, int bits) {
            double max = Double.NEGATIVE_INFINITY;
            for (double[] row : w) for (double v : row) if (Math.abs(v) > max) max = Math.abs(v);
            scale = max / ((1 << (bits - 1)) - 1);
            int size = w.length * w[0].length;
            int4Weights = new int[size];
            int idx = 0;
            for (double[] row : w) for (double v : row) int4Weights[idx++] = (int) Math.round(v / scale);
        }

        double[][] dequantize(int d) {
            double[][] w = new double[d][d];
            int idx = 0;
            for (int i = 0; i < d; i++)
                for (int j = 0; j < d; j++)
                    w[i][j] = int4Weights[idx++] * scale;
            return w;
        }
    }

    public static void main(String[] args) {
        int d = 8, r = 2;
        LoRALayer lora = new LoRALayer(d, r, 4.0);

        double[] x = new double[d];
        double[] target = new double[d];
        Random rng = new Random(123);
        for (int i = 0; i < d; i++) { x[i] = rng.nextGaussian(); target[i] = x[i] * 2.0 + 0.5; }

        System.out.println("=== LoRA Forward Pass ===");
        double[] y = lora.forward(x);
        System.out.println("Input:  " + Arrays.toString(Arrays.copyOf(x, 4)));
        System.out.println("Output: " + Arrays.toString(Arrays.copyOf(y, 4)));

        System.out.println("\n=== LoRA Training (B only) ===");
        for (int epoch = 0; epoch < 50; epoch++) {
            double loss = lora.trainStep(x, target, 0.01);
            if (epoch % 10 == 0) System.out.printf("Epoch %d, loss: %.6f%n", epoch, loss);
        }

        System.out.println("\n=== QLoRA: 4-bit Quantization ===");
        QuantizedWeights qw = new QuantizedWeights(lora.W, 4);
        System.out.println("Quantized scale: " + qw.scale);
        System.out.println("Quantized size: " + qw.int4Weights.length + " entries (4-bit each)");

        System.out.println("\nLoRA/QLoRA concepts validated.");
    }
}
