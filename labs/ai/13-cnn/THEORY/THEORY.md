# Convolutional Neural Networks (CNN) - Complete Theory and Implementation

## 1. Motivation

### 1.1 Why not Fully Connected?
- Images have spatial structure
- Too many parameters
- Lose spatial information

### 1.2 CNN Advantages
- Sparse connections
- Parameter sharing
- Translation equivariance

## 2. Convolution Operation

### 2.1 Discrete Convolution
y[i,j] = sum_m sum_n x[i+m, j+n] * k[m,n]

### 2.2 Cross-Correlation
Neural networks use cross-correlation (flip not needed):
y[i,j] = sum_m sum_n x[i+m, j+n] * k[m,n]

### 2.3 Convolution with Multiple Channels
- Input: C_in channels
- Output: C_out channels
- Kernel: C_out x C_in x H x W

## 3. Components

### 3.1 Conv Layer
- Filters detect patterns
- Each filter produces one channel
- Stride and padding control output size

### 3.2 Output Size Calculation
Output = floor((H - K + 2P) / S) + 1
- H: input size
- K: kernel size
- P: padding
- S: stride

### 3.3 Padding
- Valid: no padding
- Same: pad to keep size
- Full: maximum padding

### 3.4 Pooling
- Max pooling: extract maximum
- Average pooling: extract average
- Reduces spatial size
- Provides translation invariance

### 3.5 Batch Normalization
Normalize each channel across batch and spatial dimensions.

## 4. CNN Architecture Patterns

### 4.1 LeNet
- 7 layers
- Conv-Pool-Conv-Pool-FC-FC

### 4.2 AlexNet
- 8 layers
- ReLU, dropout, data augmentation

### 4.3 VGG
- 16/19 layers
- 3x3 conv everywhere

### 4.4 ResNet
- Skip connections
- Enables deeper networks
- Resolves vanishing gradient

## 5. Java Implementation

### 5.1 Convolution Layer

```java
package com.ml.cnn;

import com.ml.la.matrix.Matrix;

public class ConvLayer {
    private int inChannels, outChannels;
    private int kernelH, kernelW;
    private int stride, padding;
    private Matrix[] kernels;  // weights
    private Vector[] biases;
    
    public ConvLayer(int inCh, int outCh, int kH, int kW, int stride, int pad) {
        this.inChannels = inCh;
        this.outChannels = outCh;
        this.kernelH = kH;
        this.kernelW = kW;
        this.stride = stride;
        this.padding = pad;
        
        java.util.Random gen = new java.util.Random();
        kernels = new Matrix[outCh * inCh];
        biases = new Vector[outCh];
        
        double scale = Math.sqrt(2.0 / (kH * kW * inCh));
        for (int oc = 0; oc < outCh; oc++) {
            biases[oc] = Vector.random(kH * kW, gen.nextLong());
            for (int ic = 0; ic < inCh; ic++) {
                int idx = oc * inCh + ic;
                double[][] data = new double[kH][kW];
                for (int i = 0; i < kH; i++) {
                    for (int j = 0; j < kW; j++) {
                        data[i][j] = gen.nextGaussian() * scale;
                    }
                }
                kernels[idx] = new Matrix(data);
            }
        }
    }
    
    public Matrix[] forward(Matrix[] input) {
        int inH = input[0].rows();
        int inW = input[0].cols();
        
        int outH = (inH + 2 * padding - kernelH) / stride + 1;
        int outW = (inW + 2 * padding - kernelW) / stride + 1;
        
        Matrix[] output = new Matrix[outChannels];
        for (int oc = 0; oc < outChannels; oc++) {
            output[oc] = new Matrix(new double[outH][outW]);
        }
        
        for (int oc = 0; oc < outChannels; oc++) {
            for (int oh = 0; oh < outH; oh++) {
                for (int ow = 0; ow < outW; ow++) {
                    double sum = 0;
                    
                    for (int ic = 0; ic < inChannels; ic++) {
                        Matrix kernel = kernels[oc * inChannels + ic];
                        
                        for (int kh = 0; kh < kernelH; kh++) {
                            for (int kw = 0; kw < kernelW; kw++) {
                                int ih = oh * stride - padding + kh;
                                int iw = ow * stride - padding + kw;
                                
                                if (ih >= 0 && ih < inH && iw >= 0 && iw < inW) {
                                    sum += input[ic].get(ih, iw) * kernel.get(kh, kw);
                                }
                            }
                        }
                    }
                    
                    output[oc].data[oh][ow] = sum + biases[oc].get(0);
                }
            }
        }
        
        return output;
    }
}
```

### 5.2 Max Pooling Layer

```java
package com.ml.cnn;

import com.ml.la.matrix.Matrix;

public class MaxPoolLayer {
    private int poolH, poolW;
    private int stride;
    
    public MaxPoolLayer(int poolH, int poolW, int stride) {
        this.poolH = poolH;
        this.poolW = poolW;
        this.stride = stride;
    }
    
    public Matrix[] forward(Matrix[] input) {
        int channels = input.length;
        int inH = input[0].rows();
        int inW = input[0].cols();
        
        int outH = (inH - poolH) / stride + 1;
        int outW = (inW - poolW) / stride + 1;
        
        Matrix[] output = new Matrix[channels];
        
        for (int c = 0; c < channels; c++) {
            output[c] = new Matrix(new double[outH][outW]);
            
            for (int oh = 0; oh < outH; oh++) {
                for (int ow = 0; ow < outW; ow++) {
                    double maxVal = Double.NEGATIVE_INFINITY;
                    
                    for (int ph = 0; ph < poolH; ph++) {
                        for (int pw = 0; pw < poolW; pw++) {
                            int ih = oh * stride + ph;
                            int iw = ow * stride + pw;
                            maxVal = Math.max(maxVal, input[c].get(ih, iw));
                        }
                    }
                    
                    output[c].data[oh][ow] = maxVal;
                }
            }
        }
        
        return output;
    }
    
    public Matrix[] backward(Matrix[] input, Matrix[] outputGrad, Matrix[] output) {
        int channels = input.length;
        int outH = outputGrad[0].rows();
        int outW = outputGrad[0].cols();
        
        Matrix[] inputGrad = new Matrix[channels];
        
        for (int c = 0; c < channels; c++) {
            inputGrad[c] = MatrixOperations.zeros(input[0].rows(), input[0].cols());
            
            for (int oh = 0; oh < outH; oh++) {
                for (int ow = 0; ow < outW; ow++) {
                    double maxVal = Double.NEGATIVE_INFINITY;
                    int maxH = 0, maxW = 0;
                    
                    for (int ph = 0; ph < poolH; ph++) {
                        for (int pw = 0; pw < poolW; pw++) {
                            int ih = oh * stride + ph;
                            int iw = ow * stride + pw;
                            if (input[c].get(ih, iw) > maxVal) {
                                maxVal = input[c].get(ih, iw);
                                maxH = ih;
                                maxW = iw;
                            }
                        }
                    }
                    
                    inputGrad[c].data[maxH][maxW] = outputGrad[c].get(oh, ow);
                }
            }
        }
        
        return inputGrad;
    }
}
```

### 5.3 Fully Connected Layer

```java
package com.ml.cnn;

public class FCLayer {
    private Matrix weights;
    private Vector biases;
    
    public FCLayer(int inSize, int outSize) {
        java.util.Random gen = new java.util.Random();
        double scale = Math.sqrt(2.0 / (inSize + outSize));
        
        double[][] wData = new double[outSize][inSize];
        for (int i = 0; i < outSize; i++) {
            for (int j = 0; j < inSize; j++) {
                wData[i][j] = gen.nextGaussian() * scale;
            }
        }
        weights = new Matrix(wData);
        biases = Vector.random(outSize, gen.nextLong());
    }
    
    public Vector forward(Vector input) {
        Vector z = MatrixOperations.multiply(weights, input);
        z = VectorOperations.add(z, biases);
        return relu(z);
    }
    
    public Vector backward(Vector input, Vector outputGrad) {
        // Apply derivative of activation
        Vector delta = VectorOperations.applyFunction(input, 
            x -> x > 0 ? outputGrad.get(0) : 0);
        
        // Weight gradients
        Matrix gradW = MatrixOperations.outerProduct(outputGrad, input);
        
        // Update weights
        weights = MatrixOperations.subtract(weights, 
            MatrixOperations.scale(gradW, 0.01));
        
        return MatrixOperations.multiply(MatrixOperations.transpose(weights), outputGrad);
    }
}
```

### 5.4 CNN Architecture

```java
package com.ml.cnn;

import com.ml.la.matrix.Matrix;
import com.ml.la.vector.Vector;

public class CNN {
    private ConvLayer conv1, conv2;
    private MaxPoolLayer pool1, pool2;
    private FCLayer fc1, fc2;
    
    public CNN(int imageSize) {
        // Conv layer 1: 1 -> 32 channels, 3x3
        conv1 = new ConvLayer(1, 32, 3, 3, 1, 1);
        pool1 = new MaxPoolLayer(2, 2, 2);
        
        // Conv layer 2: 32 -> 64 channels, 3x3
        conv2 = new ConvLayer(32, 64, 3, 3, 1, 1);
        pool2 = new MaxPoolLayer(2, 2, 2);
        
        // FC layers
        int fcInputSize = 64 * (imageSize / 4) * (imageSize / 4);
        fc1 = new FCLayer(fcInputSize, 128);
        fc2 = new FCLayer(128, 10);  // 10 classes
    }
    
    public Vector forward(double[][] image) {
        // Convert to tensor representation
        Matrix[] input = new Matrix[]{new Matrix(image)};
        
        // Conv 1 -> ReLU -> Pool
        Matrix[] x = conv1.forward(input);
        x = applyReLU(x);
        x = pool1.forward(x);
        
        // Conv 2 -> ReLU -> Pool
        x = conv2.forward(x);
        x = applyReLU(x);
        x = pool2.forward(x);
        
        // Flatten
        Vector flat = flatten(x);
        
        // FC layers
        Vector h = fc1.forward(flat);
        Vector output = fc2.forward(h);
        
        return softmax(output);
    }
    
    public void backward(Vector yTrue, Vector prediction) {
        // Compute output gradient
        Vector outputGrad = VectorOperations.subtract(prediction, yTrue);
        
        // Backprop through FC layers
        Vector grad = fc2.backward(fc1.forward(new Vector(new double[128])), outputGrad);
        grad = fc1.backward(flatten(new Matrix[0]), grad);
    }
    
    private Matrix[] applyReLU(Matrix[] x) {
        Matrix[] result = new Matrix[x.length];
        for (int i = 0; i < x.length; i++) {
            double[][] data = x[i].toArray();
            for (int r = 0; r < data.length; r++) {
                for (int c = 0; c < data[0].length; c++) {
                    data[r][c] = Math.max(0, data[r][c]);
                }
            }
            result[i] = new Matrix(data);
        }
        return result;
    }
    
    private Vector flatten(Matrix[] x) {
        int totalSize = x.length * x[0].rows() * x[0].cols();
        double[] flat = new double[totalSize];
        int idx = 0;
        
        for (Matrix m : x) {
            for (int r = 0; r < m.rows(); r++) {
                for (int c = 0; c < m.cols(); c++) {
                    flat[idx++] = m.get(r, c);
                }
            }
        }
        return new Vector(flat);
    }
    
    private Vector softmax(Vector z) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < z.size(); i++) {
            max = Math.max(max, z.get(i));
        }
        
        double sum = 0;
        double[] exp = new double[z.size()];
        for (int i = 0; i < z.size(); i++) {
            exp[i] = Math.exp(z.get(i) - max);
            sum += exp[i];
        }
        
        for (int i = 0; i < z.size(); i++) {
            exp[i] /= sum;
        }
        return new Vector(exp);
    }
}
```

## 6. Practical Considerations

### 6.1 Data Augmentation
- Random crops
- Horizontal flips
- Color jitter
- Rotation

### 6.2 Transfer Learning
- Pre-trained models (ImageNet)
- Fine-tune on new task
- Freeze early layers

### 6.3 Efficient Convolution
- Depthwise separable convolutions
- Pointwise convolutions
- Group convolutions