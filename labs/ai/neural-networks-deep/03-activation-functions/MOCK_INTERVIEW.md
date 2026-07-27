# Mock Interview: Compare GELU vs SwiGLU — Implement Both and Analyze

## Scenario
You are interviewing for a NLP research role. Modern LLMs use GELU and SwiGLU activations — they want to test your understanding.

## Interviewer Opening Question
"Implement GELU and SwiGLU from scratch. Compare their mathematical properties and explain why SwiGLU is preferred in modern LLMs."

## Candidate Response
"GELU is x * Phi(x) where Phi is the standard Gaussian CDF. SwiGLU is a gated variant: SwiGLU(x, W, V, W_o) = (Swish(xW) * xV) * W_o, where Swish = x * sigmoid(x). The gating mechanism in SwiGLU allows the network to learn which information to pass through, improving expressivity."

## Interviewer Probing Questions

**Q: How does GELU compare to ReLU?**
"GELU is a smooth approximation of ReLU with non-zero gradients for negative values. Unlike ReLU which hard-zeroes negatives, GELU down-weights them smoothly. This improves gradient flow and often yields better results in transformers."

**Q: Why does SwiGLU use a gating mechanism?**
"The element-wise multiplication of Swish(xW) and (xV) creates a learned gating. The gate controls information flow — it can selectively amplify or suppress different features. This is analogous to attention but in the feed-forward network."

**Q: What's the parameter overhead of SwiGLU?**
"Standard FFN: d_model -> 4*d_model -> d_model (2 matrices). SwiGLU: d_model -> 8/3*d_model -> d_model (3 matrices — W, V, W_o). Typically SwiGLU uses 8/3*d_model intermediate instead of 4*d_model to keep similar parameter count."

## Candidate Solution (Python)

```python
import torch
import torch.nn as nn
import torch.nn.functional as F
import math
import numpy as np

class GELU(nn.Module):
    """Gaussian Error Linear Unit."""
    def __init__(self, approximate=True):
        super().__init__()
        self.approximate = approximate

    def forward(self, x):
        if self.approximate:
            # GELU approximation used in BERT/GPT: 0.5 * x * (1 + tanh(sqrt(2/pi) * (x + 0.044715 * x^3)))
            return 0.5 * x * (1.0 + torch.tanh(
                math.sqrt(2.0 / math.pi) * (x + 0.044715 * torch.pow(x, 3))
            ))
        else:
            # Exact GELU: x * Phi(x) where Phi is Gaussian CDF
            return x * 0.5 * (1.0 + torch.erf(x / math.sqrt(2.0)))

class Swish(nn.Module):
    """Swish activation: x * sigmoid(x)."""
    def forward(self, x):
        return x * torch.sigmoid(x)

class SwiGLU(nn.Module):
    """
    SwiGLU: Swish-gated Linear Unit.
    SwiGLU(x, W, V) = Swish(x * W) * (x * V)
    """
    def __init__(self, d_model, d_ff):
        super().__init__()
        # Standard FFN has 4*d_model. SwiGLU with 8/3*d_model matches parameter count.
        self.W = nn.Linear(d_model, d_ff, bias=False)
        self.V = nn.Linear(d_model, d_ff, bias=False)

    def forward(self, x):
        return self.W(x) * F.silu(self.V(x))

class SwiGLUFFN(nn.Module):
    """Complete SwiGLU-based feed-forward network (Llama-style)."""
    def __init__(self, d_model, d_ff=None):
        super().__init__()
        d_ff = d_ff or int(8 * d_model / 3)  # 8/3*d_model to match 4*d_model param count
        self.gate_proj = nn.Linear(d_model, d_ff, bias=False)
        self.up_proj = nn.Linear(d_model, d_ff, bias=False)
        self.down_proj = nn.Linear(d_ff, d_model, bias=False)

    def forward(self, x):
        # SwiGLU gating: silu(gate) * up, then project down
        return self.down_proj(F.silu(self.gate_proj(x)) * self.up_proj(x))

class GELUFFN(nn.Module):
    """Standard GELU FFN (BERT-style)."""
    def __init__(self, d_model, d_ff=None):
        super().__init__()
        d_ff = d_ff or 4 * d_model
        self.fc1 = nn.Linear(d_model, d_ff)
        self.fc2 = nn.Linear(d_ff, d_model)
        self.gelu = GELU()

    def forward(self, x):
        return self.fc2(self.gelu(self.fc1(x)))

def compare_activations():
    """Plot activations for comparison."""
    x = torch.linspace(-6, 6, 1000)
    relu_out = F.relu(x)
    gelu_out = GELU(approximate=True)(x)
    swish_out = Swish()(x)

    print("Activation comparison at various x values:")
    print(f"{'x':>8} {'ReLU':>8} {'GELU':>8} {'Swish':>8}")
    for val in [-5, -2, -1, 0, 1, 2, 5]:
        idx = (x - val).abs().argmin()
        print(f"{val:>8.1f} {relu_out[idx]:>8.4f} {gelu_out[idx]:>8.4f} {swish_out[idx]:>8.4f}")

class ActivationAnalyzer:
    def __init__(self):
        self.activations = {
            "ReLU": F.relu,
            "GELU": GELU(approximate=True),
            "Swish": Swish(),
        }

    def analyze_gradient_flow(self, x):
        results = {}
        for name, fn in self.activations.items():
            x.requires_grad_(True)
            y = fn(x).sum()
            y.backward()
            grad = x.grad.detach().clone()
            x.grad.zero_()
            results[name] = {
                "nonzero_grad": (grad.abs() > 1e-6).float().mean().item(),
                "mean_grad": grad.mean().item(),
                "max_grad": grad.max().item(),
                "dead_neurons": (grad.abs() < 1e-6).float().mean().item(),
            }
        return results

    def parameter_count(self, d_model=4096):
        gelu_params = 2 * d_model * 4 * d_model  # W1 + W2
        swiglu_params = 3 * d_model * int(8 * d_model / 3)  # W_gate + W_up + W_down
        return {"GELU_FFN": gelu_params, "SwiGLU_FFN": swiglu_params}
```

## Interviewer Feedback
"Excellent implementation and analysis. Your explanation of the gating mechanism in SwiGLU and the parameter count trade-off is precise. The numerical comparison across activations shows practical understanding."

## Key Takeaways
- GELU: smooth approximation of ReLU with non-zero negative gradients
- SwiGLU: gated activation using Swish(xW) * xV
- SwiGLU uses 8/3*d_model intermediate dimension to match standard FFN params
- Gating mechanism allows learned information flow control
- Modern LLMs (Llama, PaLM, Gemini) use SwiGLU over GELU for better quality
