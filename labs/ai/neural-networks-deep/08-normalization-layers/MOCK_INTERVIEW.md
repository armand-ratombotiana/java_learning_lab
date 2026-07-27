# Mock Interview: Implement BatchNorm and LayerNorm from Scratch

## Scenario
You are interviewing for a deep learning engineer role. They want to test your understanding of normalization techniques.

## Interviewer Opening Question
"Implement Batch Normalization and Layer Normalization from scratch. When would you use each?"

## Candidate Response
"BatchNorm normalizes across the batch dimension: for each feature, subtract batch mean, divide by batch std, then scale and shift with learned parameters gamma and beta. LayerNorm normalizes across the feature dimension for each sample independently. BatchNorm is great for CNNs with large batches; LayerNorm is essential for transformers and RNNs."

## Interviewer Probing Questions

**Q: What's the difference in behavior at train vs test time for BatchNorm?**
"During training, BatchNorm uses batch statistics (mean, var per batch). During inference, it uses running averages computed during training. This is critical because at test time, a single sample's statistics would be meaningless."

**Q: How does LayerNorm handle sequence data?**
"In transformers, LayerNorm normalizes across the feature dimension, independently for each token position and each sample. This makes it independent of batch size and sequence length, which is why it works for autoregressive generation."

**Q: What's the gradient flow through BatchNorm?**
"BatchNorm introduces dependencies between samples in the same batch. The gradient for one sample depends on all samples in the batch. This means the gradient computation requires computing dmu/dx_i and dsigma^2/dx_i for the entire batch."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import numpy as np

class BatchNorm1d(nn.Module):
    """Batch Normalization from scratch."""
    def __init__(self, num_features, eps=1e-5, momentum=0.1, affine=True):
        super().__init__()
        self.num_features = num_features
        self.eps = eps
        self.momentum = momentum

        if affine:
            self.gamma = nn.Parameter(torch.ones(num_features))
            self.beta = nn.Parameter(torch.zeros(num_features))
        else:
            self.register_parameter("gamma", None)
            self.register_parameter("beta", None)

        # Running statistics
        self.register_buffer("running_mean", torch.zeros(num_features))
        self.register_buffer("running_var", torch.ones(num_features))
        self.num_batches_tracked = 0

    def forward(self, x):
        if self.training:
            # Batch statistics
            mean = x.mean(dim=0)  # (num_features,)
            var = x.var(dim=0, unbiased=False)  # (num_features,)

            # Update running statistics
            self.running_mean = (1 - self.momentum) * self.running_mean + self.momentum * mean
            self.running_var = (1 - self.momentum) * self.running_var + self.momentum * var
            self.num_batches_tracked += 1

            # Normalize
            x_norm = (x - mean) / torch.sqrt(var + self.eps)
        else:
            x_norm = (x - self.running_mean) / torch.sqrt(self.running_var + self.eps)

        if self.gamma is not None:
            x_norm = x_norm * self.gamma + self.beta
        return x_norm

class BatchNorm2d(nn.Module):
    """BatchNorm for convolutional layers."""
    def __init__(self, num_channels, eps=1e-5, momentum=0.1, affine=True):
        super().__init__()
        self.num_channels = num_channels
        self.eps = eps
        self.momentum = momentum

        if affine:
            self.gamma = nn.Parameter(torch.ones(num_channels))
            self.beta = nn.Parameter(torch.zeros(num_channels))
        else:
            self.register_parameter("gamma", None)
            self.register_parameter("beta", None)

        self.register_buffer("running_mean", torch.zeros(num_channels))
        self.register_buffer("running_var", torch.ones(num_channels))

    def forward(self, x):
        # x: (batch, channels, height, width)
        if self.training:
            # Mean/var over (batch, height, width) for each channel
            mean = x.mean(dim=(0, 2, 3))  # (channels,)
            var = x.var(dim=(0, 2, 3), unbiased=False)
            self.running_mean = (1 - self.momentum) * self.running_mean + self.momentum * mean
            self.running_var = (1 - self.momentum) * self.running_var + self.momentum * var
            x_norm = (x - mean[None, :, None, None]) / torch.sqrt(var[None, :, None, None] + self.eps)
        else:
            x_norm = (x - self.running_mean[None, :, None, None]) / torch.sqrt(
                self.running_var[None, :, None, None] + self.eps)

        if self.gamma is not None:
            x_norm = x_norm * self.gamma[None, :, None, None] + self.beta[None, :, None, None]
        return x_norm

class LayerNorm(nn.Module):
    """Layer Normalization: normalize across feature dimension."""
    def __init__(self, normalized_shape, eps=1e-5, elementwise_affine=True):
        super().__init__()
        if isinstance(normalized_shape, int):
            normalized_shape = (normalized_shape,)
        self.normalized_shape = normalized_shape
        self.eps = eps

        if elementwise_affine:
            self.weight = nn.Parameter(torch.ones(normalized_shape))
            self.bias = nn.Parameter(torch.zeros(normalized_shape))
        else:
            self.register_parameter("weight", None)
            self.register_parameter("bias", None)

    def forward(self, x):
        # x: (*, normalized_shape)
        mean = x.mean(dim=-1, keepdim=True)
        var = x.var(dim=-1, keepdim=True, unbiased=False)
        x_norm = (x - mean) / torch.sqrt(var + self.eps)
        if self.weight is not None:
            x_norm = x_norm * self.weight
        if self.bias is not None:
            x_norm = x_norm + self.bias
        return x_norm

class NormalizationComparator:
    """Compare BatchNorm and LayerNorm behavior."""
    def __init__(self):
        self.bn = BatchNorm1d(64)
        self.ln = LayerNorm(64)

    def compare_statistics(self, x):
        # x: (batch, features)
        bn_out = self.bn(x)
        ln_out = self.ln(x)

        print(f"Input shape: {x.shape}")
        print(f"Input mean: {x.mean().item():.4f}, std: {x.std().item():.4f}")
        print(f"BatchNorm output mean: {bn_out.mean().item():.4f}, std: {bn_out.std().item():.4f}")
        print(f"LayerNorm output mean: {ln_out.mean().item():.4f}, std: {ln_out.std().item():.4f}")
        print(f"BatchNorm per-feature mean range: "
              f"[{bn_out.mean(dim=0).min().item():.4f}, {bn_out.mean(dim=0).max().item():.4f}]")
        print(f"LayerNorm per-sample mean range: "
              f"[{ln_out.mean(dim=1).min().item():.4f}, {ln_out.mean(dim=1).max().item():.4f}]")

    def gradient_behavior(self):
        """Compare gradient flow through each normalization."""
        x = torch.randn(32, 64, requires_grad=True)
        bn_out = self.bn(x)
        ln_out = self.ln(x)
        bn_out.sum().backward(retain_graph=True)
        bn_grad = x.grad.clone()
        x.grad.zero_()
        ln_out.sum().backward()
        ln_grad = x.grad.clone()
        print(f"BatchNorm grad stats: mean={bn_grad.mean().item():.6f}, "
              f"std={bn_grad.std().item():.6f}")
        print(f"LayerNorm grad stats: mean={ln_grad.mean().item():.6f}, "
              f"std={ln_grad.std().item():.6f}")

def demonstrate_bn_limitation():
    """Show that BatchNorm fails with batch_size=1."""
    bn = BatchNorm1d(8)
    x = torch.randn(1, 8)  # batch_size=1
    try:
        out = bn(x)  # This will divide by zero variance
        print(f"BatchNorm with batch=1: {out}")
    except Exception as e:
        print(f"BatchNorm with batch=1 fails: var is zero")
    ln = LayerNorm(8)
    out = ln(x)  # Works fine
    print(f"LayerNorm with batch=1: {out}")
```

## Interviewer Feedback
"Excellent implementation of both normalization methods. Your explanation of when each is appropriate — BatchNorm for CNNs, LayerNorm for transformers/RNNs — is correct. The gradient analysis demonstrates deeper understanding."

## Key Takeaways
- BatchNorm: normalizes across batch, uses running averages at inference
- LayerNorm: normalizes across features, independent of batch size
- BatchNorm requires minimum batch size > 1 for meaningful statistics
- LayerNorm is preferred for transformers due to sequence independence
- Both use learned affine transforms (gamma, beta) after normalization
