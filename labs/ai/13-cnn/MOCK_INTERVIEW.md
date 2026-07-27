# Mock Interview: CNN

## Question 1: Convolution Operation
**Q**: Implement 2D convolution from scratch (forward pass).

**A**:
```python
def convolution2d(img, kernel, stride=1, padding=0):
    C, H, W = img.shape
    K, _, k_h, k_w = kernel.shape

    # Pad input
    H_pad = H + 2 * padding
    W_pad = W + 2 * padding
    img_pad = np.pad(img, ((0,0),(padding,padding),(padding,padding)), mode='constant')

    # Output dimensions
    H_out = (H_pad - k_h) // stride + 1
    W_out = (W_pad - k_w) // stride + 1

    out = np.zeros((K, H_out, W_out))
    for k in range(K):
        for i in range(H_out):
            for j in range(W_out):
                h_start = i * stride
                w_start = j * stride
                patch = img_pad[:, h_start:h_start+k_h, w_start:w_start+k_w]
                out[k, i, j] = np.sum(patch * kernel[k])
    return out
```

**Follow-up**: How would you optimize this? (Answer: im2col + matrix multiply, or use FFT)

## Question 2: CNN Architecture Design
**Q**: Design a CNN for CIFAR-10 classification. Explain each design choice.

**A**:
```python
class SimpleCNN(nn.Module):
    def __init__(self):
        super().__init__()
        self.features = nn.Sequential(
            nn.Conv2d(3, 32, 3, padding=1),  # 32x32 -> 32x32
            nn.ReLU(), nn.BatchNorm2d(32),
            nn.Conv2d(32, 64, 3, padding=1), # 32x32 -> 32x32
            nn.ReLU(), nn.MaxPool2d(2),       # 32x32 -> 16x16
            nn.Conv2d(64, 128, 3, padding=1), # 16x16 -> 16x16
            nn.ReLU(), nn.MaxPool2d(2)        # 16x16 -> 8x8
        )
        self.classifier = nn.Sequential(
            nn.Flatten(),
            nn.Linear(128 * 8 * 8, 256), nn.ReLU(), nn.Dropout(0.5),
            nn.Linear(256, 10)
        )
```

Design choices: 3x3 kernels (stack multiple for larger receptive field), doubling channels after pooling, batch norm for stability, dropout for regularization.

## Question 3: Receptive Field
**Q**: Calculate the receptive field of a 3-layer CNN with 3x3 kernels and stride=1.

**A**: Each 3x3 conv adds 2 to the receptive field.
Layer 1: 3x3
Layer 2: 5x5 (3 + 2)
Layer 3: 7x7 (5 + 2)

General formula: RF = 1 + sum((k_i - 1) * stride_product) for all layers.

For a 100-layer CNN with 3x3 kernels: RF = 1 + 100 * 2 = 201.

## Question 4: Pooling Alternatives
**Q**: Compare max pooling, average pooling, and stride convolution for downsampling.

**A**:
- **Max pooling**: Preserves strongest features. Translation invariance. Loses spatial information.
- **Average pooling**: Preserves overall feature intensity. Smoother. Less invariant to translation.
- **Stride convolution**: Learnable downsampling. Network decides which information to keep.

Modern CNNs often use stride convolution instead of pooling (e.g., ResNet uses stride-2 convolutions).

## Question 5: Depthwise Separable Convolution
**Q**: Explain depthwise separable convolution and its advantages.

**A**: Factorizes standard convolution into two steps:
1. **Depthwise convolution**: Each input channel has its own filter (no cross-channel mixing)
2. **Pointwise convolution**: 1x1 convolution to mix channels

**Advantages**:
- Standard conv: params = K * C * k_h * k_w
- Depthwise sep: params = C * k_h * k_w + K * C * 1 * 1
- Compression: ~1/K + 1/(k_h*k_w) reduction factor

Used in: MobileNet, Xception, EfficientNet. Critical for on-device ML.
