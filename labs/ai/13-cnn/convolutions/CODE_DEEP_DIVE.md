# Convolutions Code Deep Dive

This lab provides a pure Java implementation of a 2D Convolution operation and a Max Pooling operation, demonstrating exactly how filters extract features from images.

## 💻 Pure Java Implementation

```java file="labs/ai/13-cnn/convolutions/SOLUTION/Conv2D.java"
package ai.neuralnetworks.cnn;

import java.util.Arrays;

/**
 * A fundamental implementation of 2D Convolution and Max Pooling.
 */
public class Conv2D {

    /**
     * Applies a 2D convolution (cross-correlation) over a 2D input image.
     * Assumes stride = 1 and padding = 0 (Valid padding).
     * 
     * @param input The 2D image matrix (e.g., grayscale pixel values)
     * @param kernel The 2D filter matrix (e.g., 3x3 edge detector)
     * @return The resulting feature map
     */
    public static double[][] convolve2D(double[][] input, double[][] kernel) {
        int inputHeight = input.length;
        int inputWidth = input[0].length;
        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;

        // Calculate output dimensions: O = W - F + 1 (since P=0, S=1)
        int outputHeight = inputHeight - kernelHeight + 1;
        int outputWidth = inputWidth - kernelWidth + 1;

        double[][] output = new double[outputHeight][outputWidth];

        // Slide the kernel over the image
        for (int y = 0; y < outputHeight; y++) {
            for (int x = 0; x < outputWidth; x++) {
                
                double sum = 0.0;
                
                // Element-wise multiplication of the kernel and the image patch
                for (int ky = 0; ky < kernelHeight; ky++) {
                    for (int kx = 0; kx < kernelWidth; kx++) {
                        sum += input[y + ky][x + kx] * kernel[ky][kx];
                    }
                }
                output[y][x] = sum;
            }
        }
        return output;
    }

    /**
     * Applies Max Pooling to downsample the feature map.
     * 
     * @param input The feature map
     * @param poolSize The size of the pooling window (e.g., 2 for 2x2)
     * @param stride The step size (usually equals poolSize)
     * @return The pooled (shrunk) feature map
     */
    public static double[][] maxPool2D(double[][] input, int poolSize, int stride) {
        int inputHeight = input.length;
        int inputWidth = input[0].length;
        
        int outputHeight = ((inputHeight - poolSize) / stride) + 1;
        int outputWidth = ((inputWidth - poolSize) / stride) + 1;
        
        double[][] output = new double[outputHeight][outputWidth];
        
        for (int y = 0; y < outputHeight; y++) {
            for (int x = 0; x < outputWidth; x++) {
                
                double max = Double.NEGATIVE_INFINITY;
                
                // Find the maximum value in the pooling window
                for (int py = 0; py < poolSize; py++) {
                    for (int px = 0; px < poolSize; px++) {
                        int inputY = (y * stride) + py;
                        int inputX = (x * stride) + px;
                        if (input[inputY][inputX] > max) {
                            max = input[inputY][inputX];
                        }
                    }
                }
                output[y][x] = max;
            }
        }
        return output;
    }

    public static void printMatrix(double[][] matrix, String name) {
        System.out.println("--- " + name + " ---");
        for (double[] row : matrix) {
            for (double val : row) {
                System.out.printf("%6.1f ", val);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // A simple 5x5 image (e.g., a vertical line of 10s down the middle)
        double[][] image = {
            {0, 0, 10, 0, 0},
            {0, 0, 10, 0, 0},
            {0, 0, 10, 0, 0},
            {0, 0, 10, 0, 0},
            {0, 0, 10, 0, 0}
        };

        // A 3x3 Vertical Edge Detection Kernel (Sobel-like)
        double[][] verticalEdgeKernel = {
            {-1, 0, 1},
            {-1, 0, 1},
            {-1, 0, 1}
        };

        printMatrix(image, "Original Image (5x5)");
        printMatrix(verticalEdgeKernel, "Vertical Edge Kernel (3x3)");

        // Apply Convolution
        double[][] featureMap = convolve2D(image, verticalEdgeKernel);
        printMatrix(featureMap, "Feature Map after Convolution (3x3)");
        
        // Apply Max Pooling (2x2 with stride 1 for demonstration)
        double[][] pooledMap = maxPool2D(featureMap, 2, 1);
        printMatrix(pooledMap, "Pooled Map (2x2)");
    }
}
```

## 🔍 Key Takeaways
1. **The 4 Nested Loops**: Look at the `convolve2D` method. The two outer loops (`y`, `x`) slide the kernel across the image. The two inner loops (`ky`, `kx`) perform the dot product between the kernel and the specific patch of the image. This $O(N^2 \cdot K^2)$ complexity is why GPUs (which can parallelize these loops massively) are required for training CNNs.
2. **Feature Extraction**: If you run this code, you will see the `featureMap` strongly highlights the transition from 0 to 10 (the vertical edge) with high positive and negative values, while completely ignoring the flat 0 areas. The kernel successfully extracted the "vertical edge" feature!