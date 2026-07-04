# Vector Operations Code Deep Dive

This lab implements the mathematical vector operations from scratch in Java to bridge the gap between theory and software engineering.

## 💻 Pure Java Implementation

```java file="labs/ai/01-linear-algebra-for-ml/vectors/SOLUTION/VectorMath.java"
package ai.linearalgebra;

import java.util.Arrays;

/**
 * A fundamental implementation of Vector mathematics for Machine Learning.
 * Note: In production ML systems, these operations are highly optimized 
 * using SIMD instructions or offloaded to GPUs via frameworks like ND4J or Panama API.
 */
public class VectorMath {

    /**
     * Adds two vectors element-wise.
     * @throws IllegalArgumentException if vectors have different dimensions.
     */
    public static double[] add(double[] u, double[] v) {
        if (u.length != v.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension.");
        }
        double[] result = new double[u.length];
        for (int i = 0; i < u.length; i++) {
            result[i] = u[i] + v[i];
        }
        return result;
    }

    /**
     * Multiplies a vector by a scalar value.
     */
    public static double[] scalarMultiply(double scalar, double[] v) {
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = scalar * v[i];
        }
        return result;
    }

    /**
     * Computes the dot product of two vectors.
     * The foundation of neural network forward passes.
     */
    public static double dotProduct(double[] u, double[] v) {
        if (u.length != v.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension.");
        }
        double sum = 0;
        for (int i = 0; i < u.length; i++) {
            sum += u[i] * v[i];
        }
        return sum;
    }

    /**
     * Computes the L2 Norm (Euclidean length) of the vector.
     */
    public static double l2Norm(double[] v) {
        double sumOfSquares = 0;
        for (double val : v) {
            sumOfSquares += val * val;
        }
        return Math.sqrt(sumOfSquares);
    }

    /**
     * Computes the Cosine Similarity between two vectors.
     * Used extensively in NLP and Embeddings to measure semantic similarity.
     * Range: [-1, 1] (1 = identical direction, 0 = orthogonal, -1 = opposite)
     */
    public static double cosineSimilarity(double[] u, double[] v) {
        double dot = dotProduct(u, v);
        double normU = l2Norm(u);
        double normV = l2Norm(v);
        
        if (normU == 0 || normV == 0) {
            return 0.0; // Prevent division by zero
        }
        return dot / (normU * normV);
    }

    public static void main(String[] args) {
        double[] v1 = {3.0, 4.0};
        double[] v2 = {1.0, 2.0};

        System.out.println("v1: " + Arrays.toString(v1));
        System.out.println("v2: " + Arrays.toString(v2));
        
        System.out.println("Addition: " + Arrays.toString(add(v1, v2)));
        System.out.println("Scalar Mult (v1 * 2): " + Arrays.toString(scalarMultiply(2, v1)));
        System.out.println("Dot Product: " + dotProduct(v1, v2));
        System.out.println("L2 Norm of v1: " + l2Norm(v1));
        System.out.println("Cosine Similarity: " + cosineSimilarity(v1, v2));
    }
}
```

## 🚀 Performance Implications
While this pure Java implementation is pedagogically correct, a simple `for` loop is too slow for training large neural networks. Modern Java ML libraries utilize the **Vector API (JEP 460)** to leverage SIMD (Single Instruction, Multiple Data) CPU instructions, allowing the CPU to process multiple vector elements in a single clock cycle, or they use JNI to offload the math to highly optimized C++/CUDA BLAS libraries.