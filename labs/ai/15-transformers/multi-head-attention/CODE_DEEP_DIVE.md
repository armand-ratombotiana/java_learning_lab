# Multi-Head Attention Code Deep Dive

This lab provides a pure Java implementation of Multi-Head Attention, demonstrating the split-heads, parallel attention, and concatenation steps.

## 💻 Pure Java Implementation

```java file="labs/ai/15-transformers/multi-head-attention/SOLUTION/MultiHeadAttention.java"
package ai.transformers.attention;

import java.util.Arrays;

/**
 * A fundamental implementation of Multi-Head Attention.
 */
public class MultiHeadAttention {

    private final int h; // Number of heads
    private final int dModel;
    private final int dK; // Dimension per head

    public MultiHeadAttention(int h, int dModel) {
        this.h = h;
        this.dModel = dModel;
        this.dK = dModel / h;
    }

    /**
     * Simulates the Multi-Head Attention forward pass.
     */
    public double[][] forward(double[][] x) {
        int seqLen = x.length;
        
        // 1. Parallel Attention for each head
        double[][][] heads = new double[h][seqLen][dK];
        for (int i = 0; i < h; i++) {
            // In a real network, we'd multiply x by learned weight matrices W_i^Q, W_i^K, W_i^V
            // Here we simulate the output of those projections
            heads[i] = computeScaledDotProductAttention(x, i);
        }

        // 2. Concatenation
        double[][] concatenated = new double[seqLen][dModel];
        for (int t = 0; i < seqLen; t++) {
            for (int headIdx = 0; headIdx < h; headIdx++) {
                System.arraycopy(heads[headIdx][t], 0, concatenated[t], headIdx * dK, dK);
            }
        }

        // 3. Final Linear Projection (Simulated as identity for this lab)
        return concatenated;
    }

    private double[][] computeScaledDotProductAttention(double[][] x, int headId) {
        // Simplified for demonstration: using x directly as Q, K, V
        // In reality, this would use the head-specific projections
        return x; 
    }

    public static void main(String[] args) {
        int heads = 8;
        int dModel = 512;
        MultiHeadAttention mha = new MultiHeadAttention(heads, dModel);
        
        double[][] input = new double[10][dModel]; // 10 tokens
        double[][] output = mha.forward(input);
        
        System.out.println("Input shape: 10x" + dModel);
        System.out.println("Output shape: " + output.length + "x" + output[0].length);
    }
}
```