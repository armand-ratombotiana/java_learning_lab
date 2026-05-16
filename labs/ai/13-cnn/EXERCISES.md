# CNN (Convolutional Neural Networks) - EXERCISES

## Exercise 1: Convolution Operation
Implement a 3x3 Sobel edge detection kernel and apply it to the following 5x5 input matrix:

```
[[1, 2, 3, 4, 5],
 [2, 4, 6, 8, 10],
 [3, 6, 9, 12, 15],
 [4, 8, 12, 16, 20],
 [5, 10, 15, 20, 25]]
```

Using stride=1 and padding=0.

**Answer**: The Sobel kernel detects horizontal edges. Apply convolution with:
```java
public double[][] sobelHorizontal(double[][] input) {
    double[][] kernel = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };
    
    Convolution2D conv = new Convolution2D(kernel, 1, 0);
    return conv.forward(input);
}
// Result: 3x3 output matrix with edge values
```

## Exercise 2: Pooling Layer
Apply 2x2 max pooling with stride=2 to:

```
[[1, 3, 2, 1],
 [4, 6, 5, 2],
 [3, 2, 8, 7],
 [1, 1, 3, 4]]
```

**Answer**: Output should be:
```java
public double[][] maxPool(double[][] input, int poolSize, int stride) {
    MaxPooling2D pool = new MaxPooling2D(poolSize, stride);
    return pool.forward(input);
}
// Output: [[6, 5], [2, 8]]
```

## Exercise 3: Calculate Output Dimensions
For a 32x32 input image with:
- Conv layer: 64 filters of size 3x3, stride=1, padding=1
- Pool layer: 2x2 with stride=2

Calculate the output dimensions after each layer.

**Answer**:
```java
public int[] calcOutputDims() {
    // Input: 32x32
    // After conv (padding=1): (32 - 3 + 2*1)/1 + 1 = 32x32 with 64 channels
    // After pool: (32 - 2)/2 + 1 = 16x16 with 64 channels
    return new int[]{16, 16, 64};
}
```

## Exercise 4: ResNet Skip Connection
Given a ResNet block with input channels=64, output channels=128, stride=2:
- Main path: Conv 3x3 (stride 2) -> Conv 3x3 (stride 1)
- Skip connection needs to match dimensions

Implement the shortcut path calculation:

```java
public double[][][] shortcutPath(double[][][] input, int inCh, int outCh, int stride) {
    if (stride != 1 || inCh != outCh) {
        // Need projection: 1x1 conv with stride
        int height = input[0].length;
        int width = input[0][0].length;
        int newHeight = (height - 1) / stride + 1;
        int newWidth = (width - 1) / stride + 1;
        
        return new double[outCh][newHeight][newWidth];
    }
    // Identity mapping when dimensions match
    return input;
}
```

## Exercise 5: Image Augmentation Pipeline
Create a pipeline that applies these augmentations in sequence:
1. Random horizontal flip (50% chance)
2. Random brightness adjustment (-0.2 to +0.2)
3. Random contrast (0.8 to 1.2)

```java
public double[][][] augmentPipeline(double[][][] image) {
    ImageAugmentation aug = new ImageAugmentation();
    
    double[][][] result = image;
    Random rand = new Random();
    
    if (rand.nextBoolean()) {
        result = aug.flipHorizontal(result);
    }
    
    double brightness = (rand.nextDouble() - 0.5) * 0.4;
    result = aug.adjustBrightness(result, brightness);
    
    double contrast = rand.nextDouble() * 0.4 + 0.8;
    result = aug.adjustContrast(result, contrast);
    
    return result;
}
```

## Exercise 6: Backpropagation Through Conv Layer
Given gradient output from next layer and input, compute gradient w.r.t. kernel:

```java
public double[][] convBackward(double[][] input, double[][] kernel, 
                                double[][] gradOutput, int stride, int padding) {
    int kh = kernel.length;
    int kw = kernel[0].length;
    int gradH = gradOutput.length;
    int gradW = gradOutput[0].length;
    
    double[][] gradKernel = new double[kh][kw];
    double[][] gradInput = new double[input.length][input[0].length];
    
    for (int i = 0; i < gradH; i++) {
        for (int j = 0; j < gradW; j++) {
            for (int ki = 0; ki < kh; ki++) {
                for (int kj = 0; kj < kw; kj++) {
                    int inRow = i * stride + ki - padding;
                    int inCol = j * stride + kj - padding;
                    
                    if (inRow >= 0 && inRow < input.length &&
                        inCol >= 0 && inCol < input[0].length) {
                        gradKernel[ki][kj] += gradOutput[i][j] * input[inRow][inCol];
                        gradInput[inRow][inCol] += gradOutput[i][j] * kernel[ki][kj];
                    }
                }
            }
        }
    }
    
    return gradKernel;
}
```

---

## Solutions

### Exercise 1:
```java
public double[][] sobelConv() {
    double[][] input = {
        {1, 2, 3, 4, 5},
        {2, 4, 6, 8, 10},
        {3, 6, 9, 12, 15},
        {4, 8, 12, 16, 20},
        {5, 10, 15, 20, 25}
    };
    
    double[][] kernel = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };
    
    Convolution2D conv = new Convolution2D(kernel, 1, 0);
    return conv.forward(input);
}
```

### Exercise 2:
```java
public double[][] pool() {
    double[][] input = {
        {1, 3, 2, 1},
        {4, 6, 5, 2},
        {3, 2, 8, 7},
        {1, 1, 3, 4}
    };
    
    MaxPooling2D pool = new MaxPooling2D(2, 2);
    return pool.forward(input);
    // Returns [[6, 5], [2, 8]]
}
```

### Exercise 3:
```java
public int[] dimensions() {
    int input = 32;
    int convOut = input; // 32 with padding
    int poolOut = (convOut - 2) / 2 + 1; // 16
    return new int[]{poolOut, poolOut, 64};
}
```

### Exercise 4:
```java
// Skip connection projection when dimensions differ
// Output: 16x16x128 feature maps
```

### Exercise 5:
```java
// Augmentation applied in sequence with randomization
// Result varies based on random choices
```

### Exercise 6:
```java
// Gradient computed through nested loops
// Returns kernel gradients for weight updates
```