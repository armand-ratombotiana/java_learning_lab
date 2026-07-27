# Mock Interview: Backpropagation

**Topic:** Derive backpropagation for a CNN with convolutional and pooling layers

## Core Questions

### Q1: Derive backprop for a convolutional layer.

**Answer:**
**Forward:** $Z = X * W + b$ where $X \in \mathbb{R}^{C_{in} \times H \times W}$, $W \in \mathbb{R}^{C_{out} \times C_{in} \times k \times k}$

**Backward:**
We receive $\frac{\partial L}{\partial Z} \in \mathbb{R}^{C_{out} \times H' \times W'}$

Need three gradients:

1. **Gradient w.r.t. input $X$:** $\frac{\partial L}{\partial X}$
   $\frac{\partial L}{\partial X} = \frac{\partial L}{\partial Z} * \text{rot180}(W)$ (full convolution with rotated kernel)

   **Intuition:** Error flows backward through flipped kernel.

2. **Gradient w.r.t. kernel $W$:** $\frac{\partial L}{\partial W}$
   $\frac{\partial L}{\partial W_{c_{out}, c_{in}}} = X_{c_{in}} * \frac{\partial L}{\partial Z_{c_{out}}}$ (valid convolution of input with error)

3. **Gradient w.r.t. bias $b$:** $\frac{\partial L}{\partial b_{c_{out}}} = \sum_{h,w} \frac{\partial L}{\partial Z_{c_{out}, h, w}}$

### Q2: Derive backprop for max pooling.

**Answer:**
**Forward:** Max pooling selects maximum value in each $k \times k$ window. Only the max element passes through.

**Backward:** Error only flows to the input element that was the maximum in each pooling window.

For each pooling window:
```
∂L/∂x_i = ∂L/∂z  if x_i == max(x_in_window)
∂L/∂x_i = 0      otherwise
```

**Implementation:** Store mask during forward pass (which element was max), use it in backward to route gradients.

**Average pooling:** gradient is evenly distributed: $\frac{\partial L}{\partial x_i} = \frac{1}{k^2} \frac{\partial L}{\partial z}$ for all elements in the window.

### Q3: Write backprop for a simple CNN.

```python
class ConvNet:
    def __init__(self):
        self.conv_w = np.random.randn(16, 3, 3, 3) * 0.1
        self.conv_b = np.zeros(16)
        self.fc_w = np.random.randn(256, 10) * 0.01
        self.fc_b = np.zeros(10)

    def forward(self, x):
        # x: (N, 3, 32, 32)
        self.x = x
        self.z1 = conv2d(x, self.conv_w, self.conv_b)  # (N, 16, 30, 30)
        self.a1 = relu(self.z1)
        self.z2 = maxpool2d(self.a1, pool=2)            # (N, 16, 15, 15)
        self.a2 = self.z2.reshape(x.shape[0], -1)       # (N, 16*15*15)
        self.z3 = self.a2 @ self.fc_w + self.fc_b       # (N, 10)
        return self.z3

    def backward(self, dz3):
        # dz3: (N, 10) — gradient of loss w.r.t. z3
        self.dfc_w = self.a2.T @ dz3                    # (3600, 10)
        self.dfc_b = dz3.sum(axis=0)                    # (10,)
        da2 = dz3 @ self.fc_w.T                         # (N, 3600)

        # Reshape back to (N, 16, 15, 15)
        dz2 = da2.reshape(self.z2.shape)                # average pooling: only one path
        dz1 = maxpool2d_backward(dz2, self.a1, pool=2)  # (N, 16, 30, 30)
        da1 = relu_backward(dz1, self.z1)               # (N, 16, 30, 30)

        # Conv backward
        self.dconv_w = conv2d_backward_filter(self.x, da1)  # (16, 3, 3, 3)
        self.dconv_b = da1.sum(axis=(0, 2, 3))              # (16,)
```

### Q4: Common issues in CNN backprop.

**Answer:**
- **Vanishing gradients in early layers:** Worse with sigmoid/tanh, better with ReLU
- **Memory usage:** Need to store all intermediate activations for backward pass (activations $\approx 2\times$ model size)
- **Checkboard artifacts:** Deconvolution (transposed conv) can cause uneven gradient overlap
- **ReLU dying:** Neurons can become permanently inactive (gradient = 0), use Leaky ReLU
- **Gradient explosion in deep CNNs:** Use batch norm, residual connections, proper init

### Q5: How does backprop differ for transposed convolution?

**Answer:**
Transposed convolution (deconvolution) forward is equivalent to regular convolution backward, and vice versa.

- Forward: insert zeros between inputs, then convolve
- Backward: regular convolution of input gradient with kernel (no zero insertion)

Used in: generative models, semantic segmentation, super-resolution.

## Advanced

- **Gradient flow in ResNets:** $x_{l+1} = x_l + F(x_l, W_l)$ gives $\frac{\partial L}{\partial x_l} = \frac{\partial L}{\partial x_{l+1}} \left(1 + \frac{\partial F}{\partial x_l}\right)$ — identity shortcut preserves gradient
- **Receptive field:** Each pixel in deeper layers sees a larger input region; gradient propagates through same field
- **Memory-efficient backprop:** Gradient checkpointing — recompute activations during backward instead of storing all (trade compute for memory)
