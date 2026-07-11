# Transformer Architecture Code Deep Dive

This lab provides a pure Java implementation of Sinusoidal Positional Encoding, demonstrating how position information is injected into the embeddings.

## 💻 Pure Java Implementation

```java file="labs/ai/15-transformers/architecture/SOLUTION/PositionalEncoding.java"
package ai.transformers.architecture;

import java.util.Arrays;

/**
 * A fundamental implementation of Sinusoidal Positional Encoding.
 */
public class PositionalEncoding {

    /**
     * Generates a matrix of positional encodings.
     * 
     * @param maxSeqLength The maximum sequence length the model can handle.
     * @param dModel The dimensionality of the embeddings.
     * @return A matrix [maxSeqLength x dModel] containing the encodings.
     */
    public static double[][] generatePositionalEncoding(int maxSeqLength, int dModel) {
        double[][] pe = new double[maxSeqLength][dModel];

        for (int pos = 0; pos < maxSeqLength; pos++) {
            for (int i = 0; i < dModel; i += 2) {
                // Calculate the denominator: 10000^(2i / dModel)
                double denominator = Math.pow(10000.0, (double) i / dModel);
                
                // Even dimensions get the Sine function
                pe[pos][i] = Math.sin(pos / denominator);
                
                // Odd dimensions get the Cosine function
                // Check if i+1 is within bounds (in case dModel is odd)
                if (i + 1 < dModel) {
                    pe[pos][i + 1] = Math.cos(pos / denominator);
                }
            }
        }
        return pe;
    }

    /**
     * Simulates adding the positional encoding to a batch of word embeddings.
     */
    public static double[][] addEncodingToEmbeddings(double[][] embeddings, double[][] pe) {
        int seqLength = embeddings.length;
        int dModel = embeddings[0].length;
        
        double[][] output = new double[seqLength][dModel];
        
        for (int pos = 0; pos < seqLength; pos++) {
            for (int i = 0; i < dModel; i++) {
                output[pos][i] = embeddings[pos][i] + pe[pos][i];
            }
        }
        return output;
    }

    public static void printMatrix(double[][] matrix, String name, int limit) {
        System.out.println("--- " + name + " ---");
        for (int i = 0; i < Math.min(matrix.length, limit); i++) {
            for (double val : matrix[i]) {
                System.out.printf("%6.3f ", val);
            }
            System.out.println();
        }
        if (matrix.length > limit) System.out.println("...");
        System.out.println();
    }

    public static void main(String[] args) {
        int maxSeqLength = 10;
        int dModel = 6; // Small dimension for visualization
        
        // 1. Generate the static Positional Encoding matrix
        double[][] pe = generatePositionalEncoding(maxSeqLength, dModel);
        printMatrix(pe, "Positional Encoding Matrix (First 5 positions)", 5);
        
        // 2. Simulate a sentence of 3 words, embedded into 6 dimensions
        double[][] sentenceEmbeddings = {
            {0.5, 0.5, 0.5, 0.5, 0.5, 0.5}, // Word 1
            {0.5, 0.5, 0.5, 0.5, 0.5, 0.5}, // Word 2 (Identical embedding)
            {0.5, 0.5, 0.5, 0.5, 0.5, 0.5}  // Word 3 (Identical embedding)
        };
        
        // 3. Add the positional encoding
        // Even though the raw embeddings are identical, the output will be unique based on position
        double[][] finalEmbeddings = addEncodingToEmbeddings(sentenceEmbeddings, pe);
        printMatrix(finalEmbeddings, "Final Embeddings (Word + Position)", 3);
    }
}
```

## 🔍 Key Takeaways
1. **Pre-computation**: Notice that the `generatePositionalEncoding` matrix is completely independent of the input data. In production frameworks (like PyTorch), this matrix is computed exactly once during model initialization and stored as a constant buffer, saving computation time during the forward pass.
2. **Differentiating Identical Words**: In the `main` method, we simulate a sentence where every word has the exact same base embedding `[0.5, 0.5, ...]`. After adding the positional encoding, look at the output. Every word now has a mathematically unique signature. This proves how the Transformer differentiates between "The dog" and "dog The".