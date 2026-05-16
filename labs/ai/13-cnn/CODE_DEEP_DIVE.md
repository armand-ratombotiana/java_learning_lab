# CNN (Convolutional Neural Networks) - CODE DEEP DIVE

## Java Implementations

### 1. Convolution Operation

```java
public class Convolution2D {
    private double[][] kernel;
    private int stride;
    private int padding;
    
    public Convolution2D(double[][] kernel, int stride, int padding) {
        this.kernel = kernel;
        this.stride = stride;
        this.padding = padding;
    }
    
    public double[][] forward(double[][] input) {
        int inputHeight = input.length;
        int inputWidth = input[0].length;
        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;
        
        int outputHeight = (inputHeight - kernelHeight + 2 * padding) / stride + 1;
        int outputWidth = (inputWidth - kernelWidth + 2 * padding) / stride + 1;
        
        double[][] padded = padInput(input, padding);
        double[][] output = new double[outputHeight][outputWidth];
        
        for (int i = 0; i < outputHeight; i++) {
            for (int j = 0; j < outputWidth; j++) {
                output[i][j] = convolveRegion(padded, i * stride, j * stride, kernel);
            }
        }
        
        return output;
    }
    
    private double[][] padInput(double[][] input, int padding) {
        int height = input.length;
        int width = input[0].length;
        double[][] padded = new double[height + 2 * padding][width + 2 * padding];
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                padded[i + padding][j + padding] = input[i][j];
            }
        }
        
        return padded;
    }
    
    private double convolveRegion(double[][] input, int row, int col, double[][] kernel) {
        double sum = 0.0;
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel[0].length; j++) {
                sum += input[row + i][col + j] * kernel[i][j];
            }
        }
        return sum;
    }
    
    public double[][][] backward(double[][] input, double[][] gradientOutput) {
        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;
        int outputHeight = gradientOutput.length;
        int outputWidth = gradientOutput[0].length;
        
        double[][] kernelGradient = new double[kernelHeight][kernelWidth];
        double[][] inputGradient = new double[input.length][input[0].length];
        
        for (int i = 0; i < outputHeight; i++) {
            for (int j = 0; j < outputWidth; j++) {
                double grad = gradientOutput[i][j];
                
                for (int ki = 0; ki < kernelHeight; ki++) {
                    for (int kj = 0; kj < kernelWidth; kj++) {
                        int inputRow = i * stride + ki - padding;
                        int inputCol = j * stride + kj - padding;
                        
                        if (inputRow >= 0 && inputRow < input.length &&
                            inputCol >= 0 && inputCol < input[0].length) {
                            kernelGradient[ki][kj] += grad * input[inputRow][inputCol];
                            inputGradient[inputRow][inputCol] += grad * kernel[ki][kj];
                        }
                    }
                }
            }
        }
        
        return new double[][][]{kernelGradient, inputGradient};
    }
}
```

### 2. Max Pooling Layer

```java
public class MaxPooling2D {
    private int poolSize;
    private int stride;
    
    public MaxPooling2D(int poolSize, int stride) {
        this.poolSize = poolSize;
        this.stride = stride;
    }
    
    public double[][] forward(double[][] input) {
        int outputHeight = (input.length - poolSize) / stride + 1;
        int outputWidth = (input[0].length - poolSize) / stride + 1;
        
        double[][] output = new double[outputHeight][outputWidth];
        int[][] maxIndices = new int[outputHeight][outputWidth];
        
        for (int i = 0; i < outputHeight; i++) {
            for (int j = 0; j < outputWidth; j++) {
                double maxVal = Double.NEGATIVE_INFINITY;
                int maxRow = 0, maxCol = 0;
                
                for (int pi = 0; pi < poolSize; pi++) {
                    for (int pj = 0; pj < poolSize; pj++) {
                        int inputRow = i * stride + pi;
                        int inputCol = j * stride + pj;
                        
                        if (input[inputRow][inputCol] > maxVal) {
                            maxVal = input[inputRow][inputCol];
                            maxRow = inputRow;
                            maxCol = inputCol;
                        }
                    }
                }
                
                output[i][j] = maxVal;
                maxIndices[i][j] = maxRow * input[0].length + maxCol;
            }
        }
        
        return output;
    }
    
    public double[][] backward(double[][] gradientOutput, int[][] maxIndices, 
                                int inputHeight, int inputWidth) {
        double[][] inputGradient = new double[inputHeight][inputWidth];
        
        for (int i = 0; i < gradientOutput.length; i++) {
            for (int j = 0; j < gradientOutput[0].length; j++) {
                int maxIdx = maxIndices[i][j];
                int maxRow = maxIdx / inputWidth;
                int maxCol = maxIdx % inputWidth;
                
                inputGradient[maxRow][maxCol] += gradientOutput[i][j];
            }
        }
        
        return inputGradient;
    }
}
```

### 3. LeNet-5 Architecture Implementation

```java
public class LeNet5 {
    private Convolution2D conv1;
    private MaxPooling2D pool1;
    private Convolution2D conv2;
    private MaxPooling2D pool2;
    private FullyConnectedLayer fc1;
    private FullyConnectedLayer fc2;
    private SoftmaxLayer softmax;
    
    public LeNet5() {
        conv1 = new Convolution2D(createKernel(6, 5), 1, 0);
        pool1 = new MaxPooling2D(2, 2);
        conv2 = new Convolution2D(createKernel(16, 5), 1, 0);
        pool2 = new MaxPooling2D(2, 2);
        fc1 = new FullyConnectedLayer(120, 400);
        fc2 = new FullyConnectedLayer(84, 120);
        softmax = new SoftmaxLayer(10, 84);
    }
    
    private double[][] createKernel(int numKernels, int size) {
        double[][] kernels = new double[numKernels][size * size];
        Random rand = new Random(42);
        
        for (int k = 0; k < numKernels; k++) {
            for (int i = 0; i < size * size; i++) {
                kernels[k][i] = (rand.nextDouble() - 0.5) * 0.1;
            }
        }
        
        return kernels[0].length == size * size ? kernels : 
               new double[numKernels][size][size];
    }
    
    public int[] forward(double[][][] images) {
        double[][] predictions = new double[images.length][10];
        
        for (int img = 0; img < images.length; img++) {
            double[][] current = images[img];
            
            current = conv1.forward(current);
            current = pool1.forward(current);
            current = conv2.forward(current);
            current = pool2.forward(current);
            
            double[] flat = flatten(current);
            flat = fc1.forward(flat);
            flat = fc2.forward(flat);
            double[] probs = softmax.forward(flat);
            
            predictions[img] = probs;
        }
        
        return getPredictions(predictions);
    }
    
    private double[] flatten(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] flat = new double[rows * cols];
        
        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flat[idx++] = matrix[i][j];
            }
        }
        
        return flat;
    }
    
    private int[] getPredictions(double[][] probs) {
        int[] preds = new int[probs.length];
        
        for (int i = 0; i < probs.length; i++) {
            preds[i] = argmax(probs[i]);
        }
        
        return preds;
    }
    
    private int argmax(double[] arr) {
        int idx = 0;
        double max = arr[0];
        
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                idx = i;
            }
        }
        
        return idx;
    }
}
```

### 4. ResNet Residual Block

```java
public class ResNetBlock {
    private Convolution2D conv1;
    private BatchNormLayer bn1;
    private Convolution2D conv2;
    private BatchNormLayer bn2;
    private Convolution2D shortcutConv;
    private Activation relu;
    
    public ResNetBlock(int inChannels, int outChannels, int stride) {
        conv1 = new Convolution2D(createKernel(outChannels, inChannels, 3), stride, 1);
        bn1 = new BatchNormLayer(outChannels);
        conv2 = new Convolution2D(createKernel(outChannels, outChannels, 3), 1, 1);
        bn2 = new BatchNormLayer(outChannels);
        
        if (stride != 1 || inChannels != outChannels) {
            shortcutConv = new Convolution2D(
                createKernel(outChannels, inChannels, 1), stride, 0);
        }
        
        relu = new Activation("relu");
    }
    
    private double[][] createKernel(int outCh, int inCh, int size) {
        double[][] kernel = new double[outCh][size * size];
        
        for (int c = 0; c < outCh; c++) {
            for (int i = 0; i < size * size; i++) {
                kernel[c][i] = Math.sqrt(2.0 / (inCh * size * size)) * 
                               (new Random().nextDouble() - 0.5) * 2;
            }
        }
        
        return kernel;
    }
    
    public double[][][] forward(double[][][] input) {
        double[][][] identity = input;
        
        double[][][] out = conv1.forward(input[0]);
        out = bn1.forward(out);
        out = relu.forward(out);
        
        out = conv2.forward(out);
        out = bn2.forward(out);
        
        if (shortcutConv != null) {
            identity = shortcutConv.forward(input);
        }
        
        out = addResidual(out, identity);
        out = relu.forward(out);
        
        return out;
    }
    
    private double[][][] addResidual(double[][][] a, double[][][] b) {
        double[][][] result = new double[a.length][][];
        
        for (int i = 0; i < a.length; i++) {
            result[i] = new double[a[i].length][a[i][0].length];
            
            for (int j = 0; j < a[i].length; j++) {
                for (int k = 0; k < a[i][0].length; k++) {
                    result[i][j][k] = a[i][j][k] + b[i][j][k];
                }
            }
        }
        
        return result;
    }
}
```

### 5. Image Data Augmentation

```java
public class ImageAugmentation {
    private Random random = new Random();
    
    public double[][][] augment(double[][][] image) {
        double[][][] augmented = deepCopy(image);
        
        if (random.nextBoolean()) {
            augmented = flipHorizontal(augmented);
        }
        
        if (random.nextBoolean()) {
            augmented = randomRotation(augmented);
        }
        
        if (random.nextBoolean()) {
            augmented = randomCrop(augmented);
        }
        
        if (random.nextBoolean()) {
            augmented = addNoise(augmented, 0.01);
        }
        
        augmented = adjustBrightness(augmented, random.nextDouble() * 0.2 - 0.1);
        augmented = adjustContrast(augmented, random.nextDouble() * 0.4 + 0.8);
        
        return augmented;
    }
    
    private double[][][] flipHorizontal(double[][][] image) {
        int channels = image.length;
        int height = image[0].length;
        int width = image[0][0].length;
        
        double[][][] flipped = new double[channels][height][width];
        
        for (int c = 0; c < channels; c++) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    flipped[c][i][j] = image[c][i][width - 1 - j];
                }
            }
        }
        
        return flipped;
    }
    
    private double[][][] randomRotation(double[][][] image) {
        double angle = (random.nextDouble() - 0.5) * 30 * Math.PI / 180;
        return rotateImage(image, angle);
    }
    
    private double[][][] rotateImage(double[][][] image, double angle) {
        int height = image[0].length;
        int width = image[0][0].length;
        double[][][] rotated = new double[image.length][height][width];
        
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        
        int centerY = height / 2;
        int centerX = width / 2;
        
        for (int c = 0; c < image.length; c++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int srcX = (int) Math.round((x - centerX) * cos - 
                                                 (y - centerY) * sin + centerX);
                    int srcY = (int) Math.round((x - centerX) * sin + 
                                                 (y - centerY) * cos + centerY);
                    
                    if (srcX >= 0 && srcX < width && srcY >= 0 && srcY < height) {
                        rotated[c][y][x] = image[c][srcY][srcX];
                    }
                }
            }
        }
        
        return rotated;
    }
    
    private double[][][] addNoise(double[][][] image, double noiseLevel) {
        double[][][] noisy = deepCopy(image);
        
        for (int c = 0; c < image.length; c++) {
            for (int i = 0; i < image[c].length; i++) {
                for (int j = 0; j < image[c][0].length; j++) {
                    double noise = random.nextGaussian() * noiseLevel;
                    noisy[c][i][j] = Math.max(0, Math.min(1, image[c][i][j] + noise));
                }
            }
        }
        
        return noisy;
    }
    
    private double[][][] adjustBrightness(double[][][] image, double delta) {
        double[][][] adjusted = deepCopy(image);
        
        for (int c = 0; c < image.length; c++) {
            for (int i = 0; i < image[c].length; i++) {
                for (int j = 0; j < image[c][0].length; j++) {
                    adjusted[c][i][j] = Math.max(0, Math.min(1, image[c][i][j] + delta));
                }
            }
        }
        
        return adjusted;
    }
    
    private double[][][] adjustContrast(double[][][] image, double factor) {
        double[][][] adjusted = deepCopy(image);
        double mean = calculateMean(image);
        
        for (int c = 0; c < image.length; c++) {
            for (int i = 0; i < image[c].length; i++) {
                for (int j = 0; j < image[c][0].length; j++) {
                    adjusted[c][i][j] = Math.max(0, Math.min(1, 
                        factor * (image[c][i][j] - mean) + mean));
                }
            }
        }
        
        return adjusted;
    }
    
    private double calculateMean(double[][][] image) {
        double sum = 0;
        int count = 0;
        
        for (int c = 0; c < image.length; c++) {
            for (int i = 0; i < image[c].length; i++) {
                for (int j = 0; j < image[c][0].length; j++) {
                    sum += image[c][i][j];
                    count++;
                }
            }
        }
        
        return sum / count;
    }
    
    private double[][][] randomCrop(double[][][] image) {
        int height = image[0].length;
        int width = image[0][0].length;
        int cropSize = (int) (Math.min(height, width) * 0.9);
        
        int startY = random.nextInt(height - cropSize);
        int startX = random.nextInt(width - cropSize);
        
        double[][][] cropped = new double[image.length][height][width];
        
        for (int c = 0; c < image.length; c++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (y >= startY && y < startY + cropSize &&
                        x >= startX && x < startX + cropSize) {
                        cropped[c][y][x] = image[c][y][x];
                    }
                }
            }
        }
        
        return cropped;
    }
    
    private double[][][] deepCopy(double[][][] arr) {
        double[][][] copy = new double[arr.length][][];
        
        for (int i = 0; i < arr.length; i++) {
            copy[i] = new double[arr[i].length][];
            
            for (int j = 0; j < arr[i].length; j++) {
                copy[i][j] = arr[i][j].clone();
            }
        }
        
        return copy;
    }
}
```

### 6. Batch Normalization Layer

```java
public class BatchNormLayer {
    private double[] gamma;
    private double[] beta;
    private double[] runningMean;
    private double[] runningVar;
    private double momentum = 0.9;
    private double epsilon = 1e-5;
    private boolean training = true;
    
    public BatchNormLayer(int numChannels) {
        this.gamma = new double[numChannels];
        this.beta = new double[numChannels];
        this.runningMean = new double[numChannels];
        this.runningVar = new double[numChannels];
        
        for (int i = 0; i < numChannels; i++) {
            gamma[i] = 1.0;
            beta[i] = 0.0;
        }
    }
    
    public double[][][] forward(double[][][] input) {
        int batchSize = input.length;
        int channels = input.length > 0 ? input.length : 1;
        
        double[][][] output = new double[batchSize][][];
        
        for (int b = 0; b < batchSize; b++) {
            output[b] = new double[input[b].length][input[b][0].length];
        }
        
        if (training) {
            double[] mean = computeMean(input);
            double[] variance = computeVariance(input, mean);
            
            System.arraycopy(mean, 0, runningMean, 0, mean.length);
            System.arraycopy(variance, 0, runningVar, 0, variance.length);
            
            for (int c = 0; c < channels; c++) {
                double std = Math.sqrt(variance[c] + epsilon);
                
                for (int i = 0; i < input[c].length; i++) {
                    for (int j = 0; j < input[c][0].length; j++) {
                        double normalized = (input[c][i][j] - mean[c]) / std;
                        output[c][i][j] = gamma[c] * normalized + beta[c];
                    }
                }
            }
        } else {
            for (int c = 0; c < channels; c++) {
                double std = Math.sqrt(runningVar[c] + epsilon);
                
                for (int i = 0; i < input[c].length; i++) {
                    for (int j = 0; j < input[c][0].length; j++) {
                        double normalized = (input[c][i][j] - runningMean[c]) / std;
                        output[c][i][j] = gamma[c] * normalized + beta[c];
                    }
                }
            }
        }
        
        return output;
    }
    
    private double[] computeMean(double[][][] input) {
        int channels = input.length;
        int height = input[0].length;
        int width = input[0][0].length;
        
        double[] mean = new double[channels];
        
        for (int c = 0; c < channels; c++) {
            double sum = 0;
            
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    sum += input[c][i][j];
                }
            }
            
            mean[c] = sum / (height * width);
        }
        
        return mean;
    }
    
    private double[] computeVariance(double[][][] input, double[] mean) {
        int channels = input.length;
        int height = input[0].length;
        int width = input[0][0].length;
        
        double[] variance = new double[channels];
        
        for (int c = 0; c < channels; c++) {
            double sumSq = 0;
            
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    double diff = input[c][i][j] - mean[c];
                    sumSq += diff * diff;
                }
            }
            
            variance[c] = sumSq / (height * width);
        }
        
        return variance;
    }
    
    public void setTraining(boolean training) {
        this.training = training;
    }
}
```