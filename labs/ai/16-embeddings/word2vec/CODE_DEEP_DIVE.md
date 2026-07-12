# Word2Vec Code Deep Dive

This lab provides a pure Java conceptual implementation of the Skip-Gram training loop with Negative Sampling, demonstrating how semantic vectors are learned.

## 💻 Pure Java Implementation

```java file="labs/ai/16-embeddings/word2vec/SOLUTION/SkipGramNegativeSampling.java"
package ai.embeddings.word2vec;

import java.util.Random;

/**
 * A conceptual implementation of Skip-Gram with Negative Sampling.
 */
public class SkipGramNegativeSampling {

    private final int vocabSize;
    private final int embeddingDim;
    private final double learningRate;
    
    // The Input Embeddings (This is the final Word2Vec matrix we care about)
    private final double[][] inputWeights;
    
    // The Output Embeddings (Used only during training)
    private final double[][] outputWeights;
    
    private final Random random = new Random(42);

    public SkipGramNegativeSampling(int vocabSize, int embeddingDim, double learningRate) {
        this.vocabSize = vocabSize;
        this.embeddingDim = embeddingDim;
        this.learningRate = learningRate;
        
        this.inputWeights = new double[vocabSize][embeddingDim];
        this.outputWeights = new double[vocabSize][embeddingDim];
        
        // Initialize with small random values
        for (int i = 0; i < vocabSize; i++) {
            for (int j = 0; j < embeddingDim; j++) {
                inputWeights[i][j] = (random.nextDouble() - 0.5) / embeddingDim;
                outputWeights[i][j] = (random.nextDouble() - 0.5) / embeddingDim;
            }
        }
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    /**
     * Trains the model on a single center word and context word pair.
     * 
     * @param centerWordId The ID of the center word.
     * @param contextWordId The ID of the positive context word.
     * @param numNegativeSamples The number of negative samples to draw.
     */
    public void trainPair(int centerWordId, int contextWordId, int numNegativeSamples) {
        
        double[] centerVector = inputWeights[centerWordId];
        
        // Array to accumulate gradients for the center vector
        double[] centerGradient = new double[embeddingDim];
        
        // 1. Positive Sample Update (Label = 1)
        updateWeights(centerVector, outputWeights[contextWordId], centerGradient, 1.0);
        
        // 2. Negative Samples Update (Label = 0)
        for (int i = 0; i < numNegativeSamples; i++) {
            int negativeWordId = drawNegativeSample(centerWordId, contextWordId);
            updateWeights(centerVector, outputWeights[negativeWordId], centerGradient, 0.0);
        }
        
        // 3. Update the center vector using the accumulated gradients
        for (int i = 0; i < embeddingDim; i++) {
            centerVector[i] += centerGradient[i];
        }
    }

    /**
     * Calculates the gradient and updates the output weights.
     */
    private void updateWeights(double[] centerVector, double[] outputVector, double[] centerGradient, double label) {
        // Calculate dot product
        double dotProduct = 0;
        for (int i = 0; i < embeddingDim; i++) {
            dotProduct += centerVector[i] * outputVector[i];
        }
        
        // Calculate prediction and error
        double prediction = sigmoid(dotProduct);
        double error = label - prediction; // If label=1 and pred=0.1, error=0.9 (needs big update)
        
        // Update Output Vector and accumulate Center Gradient
        for (int i = 0; i < embeddingDim; i++) {
            // Gradient for the center vector
            centerGradient[i] += learningRate * error * outputVector[i];
            
            // Gradient for the output vector (updated in place)
            outputVector[i] += learningRate * error * centerVector[i];
        }
    }

    /**
     * Simulates drawing a random word from the vocabulary based on a unigram distribution.
     */
    private int drawNegativeSample(int centerWordId, int contextWordId) {
        int negativeId;
        do {
            negativeId = random.nextInt(vocabSize);
        } while (negativeId == centerWordId || negativeId == contextWordId);
        return negativeId;
    }

    public static void main(String[] args) {
        // Pretend vocabulary size is 10,000 and we want 300-dimensional embeddings
        SkipGramNegativeSampling model = new SkipGramNegativeSampling(10000, 300, 0.025);
        
        System.out.println("Training started...");
        // Pretend word 5 is the center word, and word 42 is found in its context window.
        // We use 5 negative samples.
        model.trainPair(5, 42, 5);
        System.out.println("Training step completed. Weights updated.");
    }
}
```

## 🔍 Key Takeaways
1. **The Efficiency of Negative Sampling**: Look at the `trainPair` method. We only call `updateWeights` once for the positive sample, and $K$ times for the negative samples. If $K=5$, we only updated 6 output vectors out of 10,000. This is the magic that makes Word2Vec incredibly fast to train.
2. **Input vs Output Weights**: The model maintains two separate matrices (`inputWeights` and `outputWeights`). After training is complete, the `outputWeights` matrix is usually discarded. The `inputWeights` matrix becomes the final Word Embeddings used in downstream ML tasks.