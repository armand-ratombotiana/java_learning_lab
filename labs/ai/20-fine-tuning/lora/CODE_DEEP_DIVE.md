# LoRA Code Deep Dive

This lab provides a pure Java conceptual implementation of a LoRA (Low-Rank Adaptation) layer. It demonstrates how the base weights remain frozen while the low-rank matrices are updated, and how they can be merged for zero-latency inference.

## 💻 Pure Java Implementation

```java file="labs/ai/20-fine-tuning/lora/SOLUTION/LoRALayer.java"
package ai.finetuning.lora;

import java.util.Random;

/**
 * A conceptual implementation of a Linear Layer augmented with LoRA.
 */
public class LoRALayer {

    private final int inputDim;
    private final int outputDim;
    private final int rank;
    private final double alpha; // Scaling factor

    // The Frozen Base Model Weights (W0)
    private final double[][] baseWeights;

    // The Trainable LoRA Matrices (A and B)
    private final double[][] loraA; // Shape: rank x inputDim
    private final double[][] loraB; // Shape: outputDim x rank

    private final Random random = new Random(42);

    /**
     * @param inputDim  Dimension of input features (e.g., 4096)
     * @param outputDim Dimension of output features (e.g., 4096)
     * @param rank      The low rank 'r' (e.g., 8)
     * @param alpha     Scaling factor (often set to rank, or a constant like 16 or 32)
     */
    public LoRALayer(int inputDim, int outputDim, int rank, double alpha) {
        this.inputDim = inputDim;
        this.outputDim = outputDim;
        this.rank = rank;
        this.alpha = alpha;

        this.baseWeights = new double[outputDim][inputDim];
        this.loraA = new double[rank][inputDim];
        this.loraB = new double[outputDim][rank];

        initializeWeights();
    }

    private void initializeWeights() {
        // 1. Simulate a pre-trained base model (frozen weights)
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                baseWeights[i][j] = random.nextGaussian() * 0.01;
            }
        }

        // 2. Initialize LoRA A with random Gaussian noise
        for (int i = 0; i < rank; i++) {
            for (int j = 0; j < inputDim; j++) {
                loraA[i][j] = random.nextGaussian() * 0.01;
            }
        }

        // 3. Initialize LoRA B with EXACTLY ZERO
        // This ensures the initial output of BAx is 0, so training starts exactly where the base model left off.
        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < rank; j++) {
                loraB[i][j] = 0.0;
            }
        }
    }

    /**
     * The Forward Pass during Training.
     * h = W0x + (alpha / r) * BAx
     */
    public double[] forward(double[] x) {
        if (x.length != inputDim) throw new IllegalArgumentException("Input dimension mismatch");

        double[] output = new double[outputDim];
        double scale = alpha / rank;

        for (int i = 0; i < outputDim; i++) {
            // 1. Base Model Path (W0 * x)
            double baseSum = 0;
            for (int j = 0; j < inputDim; j++) {
                baseSum += baseWeights[i][j] * x[j];
            }

            // 2. LoRA Path (B * (A * x))
            double loraSum = 0;
            for (int k = 0; k < rank; k++) {
                double ax = 0;
                for (int j = 0; j < inputDim; j++) {
                    ax += loraA[k][j] * x[j];
                }
                loraSum += loraB[i][k] * ax;
            }

            // Combine and scale
            output[i] = baseSum + (loraSum * scale);
        }
        return output;
    }

    /**
     * Simulates the merging of LoRA weights into the base weights for zero-latency inference.
     * W_merged = W0 + (alpha / r) * (B * A)
     */
    public double[][] mergeWeightsForInference() {
        double[][] mergedWeights = new double[outputDim][inputDim];
        double scale = alpha / rank;

        for (int i = 0; i < outputDim; i++) {
            for (int j = 0; j < inputDim; j++) {
                
                // Calculate (B * A) for this specific weight
                double baProduct = 0;
                for (int k = 0; k < rank; k++) {
                    baProduct += loraB[i][k] * loraA[k][j];
                }
                
                // Merge: W0 + scaled(BA)
                mergedWeights[i][j] = baseWeights[i][j] + (baProduct * scale);
            }
        }
        return mergedWeights;
    }

    public static void main(String[] args) {
        int dim = 4096;
        int r = 8;
        
        System.out.println("Initializing LoRA Layer...");
        System.out.printf("Base Parameters (Frozen): %,d\n", (dim * dim));
        System.out.printf("LoRA Parameters (Trainable): %,d\n", (dim * r) + (r * dim));
        
        LoRALayer layer = new LoRALayer(dim, dim, r, 16.0);
        
        // Simulate input
        double[] input = new double[dim];
        for(int i=0; i<dim; i++) input[i] = 1.0;
        
        // Forward pass
        double[] output = layer.forward(input);
        System.out.println("\nForward pass completed successfully.");
        
        // Merge weights for production serving
        System.out.println("Merging weights for zero-latency inference...");
        double[][] productionWeights = layer.mergeWeightsForInference();
        System.out.println("Weights merged. Ready for deployment.");
    }
}
```

## 🔍 Key Takeaways
1. **The Zero Initialization**: Notice in `initializeWeights()` that `loraB` is initialized entirely with `0.0`. If you trace the math in the `forward()` method, before any training happens, `loraSum` will be exactly 0. The layer acts *exactly* like the original frozen base model until the backpropagation algorithm starts updating `loraB`.
2. **Parameter Reduction Calculation**: If you run the `main` method, it prints the parameter counts. The base matrix has 16,777,216 parameters. The LoRA matrices combined have only 65,536 parameters. This is why you can fine-tune a 7B parameter model on a single consumer GPU (like an RTX 4090 or even a 3090) instead of needing a massive datacenter cluster.