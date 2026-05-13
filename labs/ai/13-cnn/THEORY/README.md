# Convolutional Neural Networks - Theory

## 1. Convolution

### Operation
```
(f * g)[i,j] = Σₘ Σₙ f[m,n] * g[i-m, j-n]
```

### 2D Convolution
- Input: H×W×C
- Kernel: K×K
- Output: (H-K+1)×(W-K+1)

### Parameters
- Filters (output channels)
- Stride (step size)
- Padding (add border)

## 2. Pooling

### Max Pooling
- Take maximum in window

### Average Pooling
- Take mean in window

### Global Pooling
- Pool entire feature map
- Reduces to single value

## 3. Modern Architectures

### LeNet-5 (1998)
- First CNN
- Handwritten digit recognition

### AlexNet (2012)
- ImageNet breakthrough
- ReLU, dropout

### VGG (2014)
- 3×3 convolutions
- Deeper networks

### ResNet (2015)
- Skip connections
- Residual learning
- Enables very deep networks

### EfficientNet (2019)
- Compound scaling
- Balanced width/depth/resolution

### Vision Transformers (ViT)
- Patch embeddings
- Self-attention
- Now competitive with CNNs