# Mock Interview: Compare ResNet, EfficientNet, and ConvNeXt

## Scenario
You are interviewing for a computer vision engineer role. The team needs to choose a backbone architecture for a production image classification system.

## Interviewer Opening Question
"Compare ResNet, EfficientNet, and ConvNeXt. Which would you choose and why?"

## Candidate Response
"ResNet is the reliable baseline with skip connections for training deep networks. EfficientNet uses compound scaling (depth, width, resolution) for Pareto-optimal efficiency. ConvNeXt modernizes ConvNets with design elements from Transformers — large kernels, LayerNorm, GELU. I'd choose ConvNeXt if accuracy matters most, EfficientNet if efficiency is critical."

## Interviewer Probing Questions

**Q: What specific design choices make ConvNeXt effective?**
"Seven key changes: 7x7 kernels instead of 3x3, GELU activation, LayerNorm instead of BatchNorm, fewer activation/norm layers, patchify stem, inverted bottleneck (like MBConv), and larger kernel depthwise convolutions."

**Q: How do you decide between ConvNeXt and a ViT?**
"ConvNeXt is better for limited data (no need for massive pretraining), easier to optimize, and has better latency on edge devices. ViT wins with very large datasets and compute budgets."

**Q: What about deploying on mobile?**
"EfficientNet-Lite or MobileNetV3 are better. ConvNeXt is too heavy for mobile. I'd use quantization and pruning for further optimization."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torchvision.models as models

# ResNet Implementation (simplified bottleneck block)
class Bottleneck(nn.Module):
    expansion = 4
    def __init__(self, in_channels, out_channels, stride=1):
        super().__init__()
        self.conv1 = nn.Conv2d(in_channels, out_channels, 1, bias=False)
        self.bn1 = nn.BatchNorm2d(out_channels)
        self.conv2 = nn.Conv2d(out_channels, out_channels, 3, stride, 1, bias=False)
        self.bn2 = nn.BatchNorm2d(out_channels)
        self.conv3 = nn.Conv2d(out_channels, out_channels * self.expansion, 1, bias=False)
        self.bn3 = nn.BatchNorm2d(out_channels * self.expansion)
        self.relu = nn.ReLU(inplace=True)
        self.downsample = None
        if stride != 1 or in_channels != out_channels * self.expansion:
            self.downsample = nn.Sequential(
                nn.Conv2d(in_channels, out_channels * self.expansion, 1, stride, bias=False),
                nn.BatchNorm2d(out_channels * self.expansion)
            )

    def forward(self, x):
        identity = x
        out = self.relu(self.bn1(self.conv1(x)))
        out = self.relu(self.bn2(self.conv2(out)))
        out = self.bn3(self.conv3(out))
        if self.downsample is not None:
            identity = self.downsample(x)
        out += identity
        return self.relu(out)

# ConvNeXt Block (simplified)
class ConvNeXtBlock(nn.Module):
    def __init__(self, dim, kernel_size=7):
        super().__init__()
        self.dwconv = nn.Conv2d(dim, dim, kernel_size, padding=kernel_size//2,
                                groups=dim, bias=False)
        self.norm = nn.LayerNorm(dim, eps=1e-6)
        self.pwconv1 = nn.Linear(dim, 4 * dim)
        self.act = nn.GELU()
        self.pwconv2 = nn.Linear(4 * dim, dim)

    def forward(self, x):
        identity = x
        x = self.dwconv(x)
        x = x.permute(0, 2, 3, 1)  # NCHW -> NHWC
        x = self.norm(x)
        x = self.pwconv1(x)
        x = self.act(x)
        x = self.pwconv2(x)
        x = x.permute(0, 3, 1, 2)  # NHWC -> NCHW
        return identity + x

# Architecture comparison
class ArchitectureComparator:
    def __init__(self):
        self.models = {
            "resnet50": models.resnet50(pretrained=True),
            "resnet101": models.resnet101(pretrained=True),
            "convnext_tiny": models.convnext_tiny(pretrained=True),
            "convnext_base": models.convnext_base(pretrained=True),
            "efficientnet_b0": models.efficientnet_b0(pretrained=True),
            "efficientnet_b3": models.efficientnet_b3(pretrained=True),
        }

    def compare(self):
        results = {}
        dummy = torch.randn(1, 3, 224, 224)
        for name, model in self.models.items():
            model.eval()
            flops = self._count_flops(model, dummy)
            params = sum(p.numel() for p in model.parameters())
            latency = self._measure_latency(model, dummy)
            accuracy = self._get_imagenet_acc(name)
            results[name] = {
                "params_m": params / 1e6,
                "flops_g": flops / 1e9,
                "latency_ms": latency,
                "top1_acc": accuracy
            }
        return results

    def _count_flops(self, model, inputs):
        from fvcore.nn import FlopCountAnalysis
        return FlopCountAnalysis(model, inputs).total()

    def _measure_latency(self, model, inputs, iterations=100):
        import time
        with torch.no_grad():
            for _ in range(10):  # warmup
                model(inputs)
            start = time.time()
            for _ in range(iterations):
                model(inputs)
            return (time.time() - start) / iterations * 1000

    def _get_imagenet_acc(self, name):
        imagenet_results = {
            "resnet50": 76.1, "resnet101": 77.4,
            "convnext_tiny": 82.1, "convnext_base": 83.8,
            "efficientnet_b0": 77.7, "efficientnet_b3": 81.1,
        }
        return imagenet_results.get(name, 0)
```

## Interviewer Feedback
"Strong comparison with concrete architectural understanding. You highlighted the right design choices for ConvNeXt and gave practical deployment guidance. The code correctly implements the key blocks."

## Key Takeaways
- ResNet: reliable baseline with skip connections enabling very deep networks
- EfficientNet: compound scaling for optimal efficiency-accuracy Pareto frontier
- ConvNeXt: modernizes ConvNets with Transformer-inspired design elements
- Choice depends on constraints: data size, compute budget, latency requirements
- ConvNeXt > ViT when data is limited or edge deployment is needed
